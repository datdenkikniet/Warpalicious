package nl.datdenkikniet.warpalicious.handling.IO;

import nl.datdenkikniet.warpalicious.config.CustomConfig;
import nl.datdenkikniet.warpalicious.config.CustomConfigHandler;
import nl.datdenkikniet.warpalicious.handling.Flag;
import nl.datdenkikniet.warpalicious.handling.Warp;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class YAMLIOHandler implements IOHandler {

    private CustomConfigHandler cfgHandler;
    private CustomConfig warpsConfig;

    public YAMLIOHandler(CustomConfigHandler cfgHandler, CustomConfig config) {
        this.cfgHandler = cfgHandler;
        this.warpsConfig = config;
    }

    @Override
    public boolean saveWarps(Collection<Warp> warps) {
        FileConfiguration c = cfgHandler.getCustomConfig(warpsConfig);
        for (Warp warp : warps) {
            c.set(warp.getName() + ".owner", warp.getOwner().toString());
            if (warp.getLocation(false).getWorld() != null) {
                c.set(warp.getName() + ".location", LocationUtils.locationToString(warp.getLocation(false)));
            } else {
                Bukkit.getLogger().log(Level.WARNING, "Warning: warp \"" + warp.getName() + "\" has an invalid world!");
            }
            for (Flag flag : Flag.values()) {
                c.set(warp.getName() + ".flags." + flag.name(), warp.getFlags().get(flag));
            }
            ArrayList<String> invited = new ArrayList<>();
            warp.getInvitedPlayers().forEach(uuid -> invited.add(uuid.toString()));
            c.set(warp.getName() + ".invited", invited);
        }
        cfgHandler.saveCustomConfig(warpsConfig);
        return true;
    }

    @Override
    public Collection<Warp> loadWarps() {
        Collection<Warp> warps = new ArrayList<>();
        FileConfiguration c = cfgHandler.getCustomConfig(warpsConfig);
        for (String key : c.getKeys(false)) {
            try {
                if (!key.equalsIgnoreCase("total")) {
                    if (!c.isSet(key + ".timeswarpedto")) {
                        c.set(key + ".timeswarpedto", 0);
                    }
                    HashMap<Flag, Boolean> flags = new HashMap<>();
                    for (String k2 : c.getConfigurationSection(key + ".flags").getKeys(false)) {
                        flags.put(Flag.valueOf(k2.toUpperCase()), c.getBoolean(key + ".flags." + k2));
                    }
                    for (Flag flag : Flag.values()) {
                        if (!flags.containsKey(flag)) {
                            flags.put(flag, WarpHandler.getDefaultFlags().get(flag));
                        }
                    }
                    UUID owner = UUID.fromString(c.getString(key + ".owner"));
                    Location loc = LocationUtils.stringToLoc(c.getString(key + ".location"));
                    int times = c.getInt(key + ".timeswarpedto");
                    ArrayList<UUID> invitedPlayers = new ArrayList<>();
                    c.getStringList(key + ".invited").forEach(str -> invitedPlayers.add(UUID.fromString(str)));
                    warps.add(new Warp(owner, loc, key, flags, times, invitedPlayers));
                }
            } catch (Exception ex) {
                System.out.println("Error while loading flag " + key);
            }
        }
        return warps;
    }

    @Override
    public boolean saveWarp(Warp warp) {
        return false;
    }

    @Override
    public boolean deleteWarp(Warp warp) {
        FileConfiguration config = cfgHandler.getCustomConfig(warpsConfig);
        if (config.isSet("total")) {
            config.set("total", config.getInt("total") + warp.getTimesWarpedTo());
        } else {
            config.set("total", warp.getTimesWarpedTo());
        }
        config.set(warp.getName(), null);
        cfgHandler.saveCustomConfig(warpsConfig);
        return true;
    }
}
