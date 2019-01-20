package nl.datdenkikniet.warpalicious.handling;

import nl.datdenkikniet.warpalicious.handling.IO.IOHandler;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WarpHandler {

    private List<Warp> warps = new ArrayList<>();
    private IOHandler ioHandler;

    public WarpHandler(IOHandler ioHandler) {
        this.ioHandler = ioHandler;
        this.warps.clear();
    }

    public Warp createWarp(UUID owner, Location loc, String name, HashMap<Flag, Boolean> flags, int time, ArrayList<UUID> invited) {
        Warp warp = new Warp(owner, loc, name, flags, time, invited);
        warps.add(warp);
        ioHandler.saveWarp(warp);
        return warp;
    }

    public List<Warp> getWarps() {
        return warps;
    }

    public static HashMap<Flag, Boolean> getDefaultFlags() {
        HashMap<Flag, Boolean> toRet = new HashMap<>();
        toRet.put(Flag.PRIVATE, false);
        toRet.put(Flag.SIGNPRIVATE, true);
        return toRet;
    }

    public void loadWarps() {
        this.warps.clear();
        this.warps.addAll(ioHandler.loadWarps());
    }

    public void delWarp(Warp warp) {
        warps.remove(warp);
        ioHandler.deleteWarp(warp);
    }

    public Warp getWarp(String name) {
        for (Warp warp : warps) {
            if (warp.getName().equalsIgnoreCase(name)) {
                return warp;
            }
        }
        return null;
    }

    public List<Warp> getWarps(UUID player) {
        return filterWarps(w -> w.getOwner().equals(player)).collect(Collectors.toList());
    }

    public void saveWarps() {
        ioHandler.saveWarps(warps);
    }

    public boolean isFlag(String flag) {
        try {
            Flag.valueOf(flag.toUpperCase());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public String getFlags() {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < Flag.values().length; i++) {
            strBuilder.append(Flag.values()[i]);
            if (i != Flag.values().length - 1) {
                strBuilder.append(", ");
            }
        }
        return strBuilder.toString();
    }

    public int getDeletedWarpsAmount() {
        //TODO finish this method
        return -1;
    }

    public Stream<Warp> filterWarps(Predicate<Warp> predicate) {
        return warps.stream().filter(predicate);
    }
}
