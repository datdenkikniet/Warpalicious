package nl.datdenkikniet.warpalicious.handling;

import nl.datdenkikniet.warpalicious.WarpaliciousPlugin;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Warp
{

    private UUID owner;
    private Location loc;
    private String name;
    private HashMap<Flag, Boolean> flags;
    private int timesWarpedTo;
    private WarpaliciousPlugin plugin;
    private ArrayList<UUID> invitedPlayers;
    private String worldName;

    public Warp(WarpaliciousPlugin instance, UUID owner, Location loc, String name, HashMap<Flag, Boolean> flags, WarpHandler handler, int time, ArrayList<UUID> invited, String worldName)
    {
        this.owner = owner;
        this.loc = loc;
        this.name = name;
        this.flags = flags;
        this.timesWarpedTo = time;
        this.plugin = instance;
        this.invitedPlayers = invited;
        this.worldName = worldName;
        handler.addWarp(this);
    }

    public boolean isPrivate()
    {
        return flags.get(Flag.PRIVATE);
    }

    public void setFlag(Flag f, boolean bool)
    {
        flags.put(f, bool);
    }

    public boolean getFlag(Flag flag)
    {
        return flags.get(flag);
    }

    public UUID getOwner()
    {
        return owner;
    }

    public Location getLocation(boolean isWarp)
    {
        if (isWarp)
        {
            timesWarpedTo++;
        }
        return loc;
    }

    public void warp(Player player, TeleportMode mode, Strings str)
    {
        int delay = 0;
        GameMode gm = player.getGameMode();
        Location loc = player.getLocation();

        if (!str.checkPermission(player, str.nodelayperm))
        {
            if (!mode.getPerm(gm))
            {
                delay = mode.getDelay(gm);
            }
            else
            {
                delay = TeleportMode.getPermissionDelay(player, mode);
            }
        }

        if (delay == 0)
        {
            if (getLocation(false) != null && getLocation(false).getWorld() != null)
            {
                if (mode.getEffect(Direction.DEPART) != null && !str.checkPermission(player, str.noParticlePerm))
                {
                    loc.getWorld().spawnParticle(mode.getEffect(Direction.DEPART), loc, mode.getEffectCount(Direction.DEPART));
                }
                player.teleport(getLocation(true));
                player.sendMessage(str.warpToWarp.replace("%NAME%", getName()));
                if (mode.getEffect(Direction.ARRIVAL) != null && !str.checkPermission(player, str.noParticlePerm))
                {
                    loc.getWorld().spawnParticle(mode.getEffect(Direction.ARRIVAL), loc, mode.getEffectCount(Direction.ARRIVAL));
                }
            }
            else
            {
                player.sendMessage(str.invalidWorld.replace("%WARPNAME%", getName()).replace("%WORLDNAME%", worldName));
            }
        }
        else
        {
            if (!mode.allowMove(gm))
            {
                player.sendMessage(str.noMove);
            }
            final int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new TeleportRunnable(player, mode, this, str, delay), 0L, 20L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.getScheduler().cancelTask(taskId), delay * 20 + 10L);
        }
    }

    public String getName()
    {
        return name;
    }

    public int getTimesWarpedTo()
    {
        return timesWarpedTo;
    }

    public void setName(String n)
    {
        name = n;
    }

    HashMap<Flag, Boolean> getFlags()
    {
        return flags;
    }

    public boolean isInvited(UUID u)
    {
        return !u.equals(owner) && invitedPlayers.contains(u);
    }

    public void addInvitedPlayer(UUID u)
    {
        invitedPlayers.add(u);
    }

    public void addInvitedPlayer(Player p)
    {
        addInvitedPlayer(p.getUniqueId());
    }

    public void removeInvitedPlayer(UUID u)
    {
        invitedPlayers.remove(u);
    }

    public void removeInvitedPlayer(Player p)
    {
        removeInvitedPlayer(p.getUniqueId());
    }

    ArrayList<UUID> getInvitedPlayers()
    {
        return invitedPlayers;
    }
    protected String getWorldName(){
        return worldName;
    }
}
