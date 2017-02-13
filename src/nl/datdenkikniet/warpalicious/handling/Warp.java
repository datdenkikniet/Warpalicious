package nl.datdenkikniet.warpalicious.handling;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;

public class Warp {

    private UUID owner;
    private Location loc;
    private String name;
    private HashMap<String, Boolean> flags;
    private int timesWarpedTo;


    public Warp(UUID owner, Location loc, String name, HashMap<String, Boolean> flags, WarpHandler handler, int time) {
        this.owner = owner;
        this.loc = loc;
        this.name = name;
        this.flags = flags;
        handler.addWarp(this);
        timesWarpedTo = time;
        System.out.println(isPrivate());
    }

    public boolean isPrivate() {
        return flags.get("private");
    }

    public void setFlag(String name, boolean bool) {
        flags.put(name, bool);
    }

    public UUID getOwner() {
        return owner;
    }

    public Location getLocation() {
        return loc;
    }

    public String getName() {
        return name;
    }

    void addWarpedTo() {
        timesWarpedTo++;
    }

    public int getTimesWarpedTo() {
        return timesWarpedTo;
    }

    public void setName(String n) {
        name = n;
    }

    HashMap<String, Boolean> getFlags() {
        return flags;
    }
}
