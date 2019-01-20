package nl.datdenkikniet.warpalicious.handling.IO;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtils {

    static Location stringToLoc(String location) {
        String[] stringslist = location.split(",");
        return new Location(Bukkit.getWorld(stringslist[0]), Double.valueOf(stringslist[1]), Double.valueOf(stringslist[2]), Double.valueOf(stringslist[3]), Float.valueOf(stringslist[4]), Float.valueOf(stringslist[5]));
    }

    static String locationToString(Location loc) {
        return String.format("%s,%s,%s,%s,%s,%s", loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }
}
