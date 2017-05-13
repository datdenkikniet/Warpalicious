package nl.datdenkikniet.warpalicious.handling;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Warp {

    private UUID owner;
    private Location loc;
    private String name;
    private HashMap<Flag, Boolean> flags;
    private int timesWarpedTo;


    public Warp(UUID owner, Location loc, String name, HashMap<Flag, Boolean> flags, WarpHandler handler, int time) {
        this.owner = owner;
        this.loc = loc;
        this.name = name;
        this.flags = flags;
        handler.addWarp(this);
        timesWarpedTo = time;
    }

    public boolean isPrivate() {
        return flags.get(Flag.PRIVATE);
    }

    public void setFlag(Flag f, boolean bool) {
        flags.put(f, bool);
    }

    public boolean getFlag(Flag flag){
        return flags.get(flag);
    }

    public UUID getOwner() {
        return owner;
    }

    public Location getLocation() {
        return loc;
    }

    public void warp(Player player)
    {
        player.teleport(loc);
        timesWarpedTo++;
    }

    public String getName() {
        return name;
    }

    public int getTimesWarpedTo() {
        return timesWarpedTo;
    }

    public void setName(String n) {
        name = n;
    }

    HashMap<Flag, Boolean> getFlags() {
        return flags;
    }
}
