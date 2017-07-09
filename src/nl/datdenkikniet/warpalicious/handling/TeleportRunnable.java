package nl.datdenkikniet.warpalicious.handling;

import nl.datdenkikniet.warpalicious.config.messages.Strings;
import org.bukkit.Location;
import org.bukkit.entity.Player;

class TeleportRunnable implements Runnable
{

    private Player player;
    private Warp warp;
    private Strings str;
    private TeleportMode mode;
    private int px, py, pz;
    private int timeLeft;

    TeleportRunnable(Player p, TeleportMode m, Warp w, Strings s, int delay)
    {
        player = p;
        warp = w;
        mode = m;
        str = s;
        timeLeft = delay;
        Location l = p.getLocation().clone();
        px = l.getBlockX();
        py = l.getBlockY();
        pz = l.getBlockZ();
    }

    public void run()
    {
        if (timeLeft != -1)
        {
            if (player != null && player.isOnline())
            {
                if (!mode.allowMove(player.getGameMode()) && !locEquals(player.getLocation()))
                {
                    player.sendMessage(str.youMoved);
                    timeLeft = -1;
                    return;
                }
                if (timeLeft != 0 && (timeLeft % 5 == 0 || timeLeft < 6))
                {
                    player.sendMessage(str.tpInTime.replace("%TIME%", String.valueOf(timeLeft)));
                }
                else if (timeLeft < 1 && timeLeft != -1)
                {
                    if (warp.getLocation(false) != null && warp.getLocation(false).getWorld() != null)
                    {
                        if (mode.getEffect(Direction.DEPART) != null && !str.checkPermission(player, str.noParticlePerm))
                        {
                            player.getLocation().getWorld().spawnParticle(mode.getEffect(Direction.DEPART), player.getLocation(), mode.getEffectCount(Direction.DEPART));
                        }
                        player.teleport(warp.getLocation(true));
                        player.sendMessage(str.warpToWarp.replace("%NAME%", warp.getName()).replace("%WORLDNAME%", warp.getWorldName()));
                        if (mode.getEffect(Direction.ARRIVAL) != null && !str.checkPermission(player, str.noParticlePerm))
                        {
                            player.getLocation().getWorld().spawnParticle(mode.getEffect(Direction.ARRIVAL), player.getLocation(), mode.getEffectCount(Direction.ARRIVAL));
                        }

                    }
                    else
                    {
                        player.sendMessage(str.invalidWorld.replace("%WARPNAME%", warp.getName()).replace("%WORLDNAME%", ""));
                    }
                }
                timeLeft--;
            }
            else
            {
                timeLeft = -1;
            }
        }
    }

    private boolean locEquals(Location loc)
    {
        return (loc.getBlockX() == px && loc.getBlockY() == py && loc.getBlockZ() == pz);
    }
}
