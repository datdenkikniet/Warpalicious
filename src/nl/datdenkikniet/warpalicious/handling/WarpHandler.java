package nl.datdenkikniet.warpalicious.handling;

import nl.datdenkikniet.warpalicious.MyWarpsPlugin;
import nl.datdenkikniet.warpalicious.commands.*;
import nl.datdenkikniet.warpalicious.config.Config;
import nl.datdenkikniet.warpalicious.config.CustomConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class WarpHandler {

    private MyWarpsPlugin plugin;
    private Config config;
    private CustomConfig cfg;
    private ArrayList<Warp> warps = new ArrayList<>();
    private ArrayList<String> flags = new ArrayList<>();


    public WarpHandler(MyWarpsPlugin instance, Config config) {
        plugin = instance;
        this.config = config;
        cfg = plugin.cfgHandler;
        flags.add("private");
    }

    public ArrayList<Warp> getWarps() {
        return warps;
    }

    void addWarp(Warp warp) {
        warps.add(warp);
    }

    public HashMap<String, Boolean> getDefaultFlags() {
        HashMap<String, Boolean> toRet = new HashMap<>();
        for (String flag : flags) {
            toRet.put(flag, false);
        }
        return toRet;
    }

    private void loadWarps() {
        FileConfiguration c = cfg.getCustomConfig(config);
        for (String key : c.getKeys(false)) {
            try {
                if (!key.equalsIgnoreCase("total")) {
                    if (!c.isSet(key + ".timeswarpedto")) {
                        c.set(key + ".timeswarpedto", 0);
                    }
                    HashMap<String, Boolean> flags = new HashMap<>();
                    for (String k2 : c.getConfigurationSection(key + ".flags").getKeys(false)) {
                        flags.put(k2, c.getBoolean(key + ".flags." + k2));
                    }
                    UUID owner = UUID.fromString(c.getString(key + ".owner"));
                    Location loc = plugin.stringToLoc(c.getString(key + ".location"));
                    int times = c.getInt(key + ".timeswarpedto");
                    new Warp(owner, loc, key, flags, this, times);
                }
            } catch (Exception ex) {
                System.out.println("Error while loading flag " + key);
            }
        }
    }

    private void loadCommands() {
        plugin.getCommand("warp").setExecutor(new WarpCommand(plugin.getStrings(), this));
        plugin.getCommand("setwarp").setExecutor(new SetWarpCommand(plugin.getStrings(), this));
        plugin.getCommand("delwarp").setExecutor(new DelWarpCommand(plugin.getStrings(), this));
        plugin.getCommand("warplist").setExecutor(new WarplistCommand(plugin.getStrings(), this));
        plugin.getCommand("editwarp").setExecutor(new EditWarpCommand(plugin.getStrings(), this));
        plugin.getCommand("warpinfo").setExecutor(new WarpinfoCommand(plugin.getStrings(), this));
        plugin.getCommand("findwarp").setExecutor(new FindWarpCommand(plugin));
    }

    public void delWarp(Warp warp) {
        warps.remove(warp);
        if (cfg.getCustomConfig(config).isSet("total")) {
            cfg.getCustomConfig(config).set("total", cfg.getCustomConfig(config).getInt("total") + warp.getTimesWarpedTo());
        } else {
            cfg.getCustomConfig(config).set("total", warp.getTimesWarpedTo());
        }
        cfg.getCustomConfig(config).set(warp.getName(), null);
        cfg.saveCustomConfig(config);
    }

    public Warp getWarp(String name, boolean isTeleportation) {
        for (Warp warp : warps) {
            if (warp.getName().equalsIgnoreCase(name)) {
                if (isTeleportation) {
                    warp.addWarpedTo();
                }
                return warp;
            }
        }
        return null;
    }

    public void load() {
        loadWarps();
        loadCommands();
    }

    public ArrayList<Warp> getWarps(UUID player) {
        ArrayList<Warp> toRet = new ArrayList<>();
        warps.stream().filter(w -> w.getOwner().equals(player)).forEach(toRet::add);
        return toRet;
    }

    public void saveWarps() {
        FileConfiguration c = cfg.getCustomConfig(config);
        for (Warp warp : warps) {
            c.set(warp.getName() + ".owner", warp.getOwner().toString());
            c.set(warp.getName() + ".location", plugin.locationToString(warp.getLocation()));
            for (String flag : warp.getFlags().keySet()) {
                c.set(warp.getName() + ".flags." + flag, warp.getFlags().get(flag));
            }
            c.set(warp.getName() + ".timeswarpedto", warp.getTimesWarpedTo());
        }
        cfg.saveCustomConfig(config);
    }

    public boolean isFlag(String flag) {
        return (flags.contains(flag.toLowerCase()));
    }

    public String getFlags() {
        return (flags.toString().replace("[", "").replace("]", ""));
    }

    public String getWarpListPages(Player player) {
        if (player.hasPermission(plugin.getStrings().warpListPrivatePerm) || player.hasPermission(plugin.getStrings().universalPerm)) {
            return String.valueOf((int) Math.ceil(((double) warps.size()) / 9));
        } else {
            double amount = 0;
            for (Warp warp : warps) {
                if (!warp.isPrivate()) {
                    amount++;
                }
            }
            return String.valueOf((int) Math.ceil(amount / 9));
        }
    }

    private int getWarpListPagesAmt(Player player) {
        if (player.hasPermission(plugin.getStrings().warpListPrivatePerm) || player.hasPermission(plugin.getStrings().universalPerm)) {
            return (int) Math.ceil(((double) warps.size()) / 9);
        } else {
            double amount = 0;
            for (Warp warp : warps) {
                if (!warp.isPrivate()) {
                    amount++;
                }
            }
            return (int) Math.ceil(amount / 9);
        }
    }

    private int getWarplistPagesSelfAmt(Player player) {
        double amount = 0;
        for (Warp warp : warps) {
            if (warp.getOwner().equals(player.getUniqueId())) {
                amount++;
            }
        }
        return (int) Math.ceil(amount / 9);
    }

    public String getWarpListPage(Player player, int page) {
        if (page > getWarpListPagesAmt(player) || page < 1) {
            return plugin.getStrings().warpPageNotExists;
        } else {
            page = page - 1;
            String toRet = plugin.getStrings().warpList.replace("%PAGE%", String.valueOf(page + 1)).replace("%MAXPAGE%", String.valueOf(getWarpListPagesAmt(player)));
            int min = page * 9;
            int max = (page * 9) + 9;
            if (player.hasPermission(plugin.getStrings().warpListPrivatePerm) || player.hasPermission(plugin.getStrings().universalPerm)) {
                for (int i = min; i < (max > warps.size() ? warps.size() : max); i++) {
                    toRet += getWarpListString(warps.get(i), i);
                }
            } else {
                ArrayList<Warp> warps2 = new ArrayList<>();
                warps.stream().filter(warp -> !warp.isPrivate() || warp.getOwner().equals(player.getUniqueId())).forEach(warps2::add);
                for (int i = min; i < (max > warps2.size() ? warps2.size() : max); i++) {
                    toRet += getWarpListString(warps2.get(i), i);
                }
            }
            return toRet;
        }
    }

    public String getWarpListPageSelf(Player player, int page) {
        if (page > getWarplistPagesSelfAmt(player) || page < 1) {
            return plugin.getStrings().warpPageNotExists;
        } else {
            page = page - 1;
            int min = page * 9;
            int max = (page * 9) + 9;
            ArrayList<Warp> warps2 = new ArrayList<>();
            warps.stream().filter(warp -> warp.getOwner().equals(player.getUniqueId())).forEach(warps2::add);
            String toRet = plugin.getStrings().warpsOwnList.replace("%PAGE%", String.valueOf(page + 1)).replace("%MAXPAGE%", String.valueOf(getWarplistPagesSelfAmt(player)));
            for (int i = min; i < (max > warps2.size() ? warps2.size() : max); i++) {
                toRet += getWarpListString(warps2.get(i), i);
            }
            return toRet;
        }
    }

    public String getWarpListPageOther(Player player, OfflinePlayer pl, Integer page) {
        if (page > getWarpListPagesAmtOther(player, pl) || page < 1) {
            return plugin.getStrings().warpPageNotExists;
        } else {
            page = page - 1;
            int min = page * 9;
            int max = (page * 9) + 9;
            ArrayList<Warp> warps2 = new ArrayList<>();
            warps.stream().filter(warp -> (!warp.isPrivate() || player.hasPermission(plugin.getStrings().warpListPrivatePerm)) || warp.getOwner().equals(pl.getUniqueId())).forEach(warps2::add);
            String toRet = plugin.getStrings().warpOthersList.replace("%PAGE%", String.valueOf(page + 1)).replace("%MAXPAGE%", String.valueOf(getWarpListPagesAmtOther(player, pl))).replace("%PLAYERNAME%", pl.getName());
            for (int i = min; i < (max > warps2.size() ? warps2.size() : max); i++) {
                toRet += getWarpListString(warps2.get(i), i);
            }
            return toRet;
        }
    }

    private int getWarpListPagesAmtOther(Player player, OfflinePlayer pl) {
        double amount = 0;
        for (Warp warp : warps) {
            if ((!warp.isPrivate() || player.hasPermission(plugin.getStrings().warpListPrivatePerm)) && warp.getOwner().equals(pl.getUniqueId())) {
                amount++;
            }
        }
        return (int) Math.ceil(amount / 9);
    }

    public boolean parseBoolean(String bool) {
        if (bool.equalsIgnoreCase("yes") || bool.equalsIgnoreCase("y") || bool.equalsIgnoreCase("true") || bool.equalsIgnoreCase("allow")) {
            return true;
        } else if (bool.equalsIgnoreCase("no") || bool.equalsIgnoreCase("n") || bool.equalsIgnoreCase("false") || bool.equalsIgnoreCase("deny")) {
            return false;
        }
        return false;
    }

    public String sortPage(CommandSender player, int page, boolean noPrivate) {
        String toRet;
        int actualPage = page - 1;
        int availablePages = 0;
        if ((player.hasPermission(plugin.getStrings().warpListPrivatePerm) || player.hasPermission(plugin.getStrings().universalPerm)) && !noPrivate) {
            availablePages = getWarps().size() / 7;
        } else {
            for (Warp warp : warps) {
                if (!warp.isPrivate()) {
                    availablePages++;
                }
            }
            availablePages = availablePages / 7;
        }
        if (actualPage > availablePages || actualPage < 0) {
            return plugin.getStrings().noValidPage.replace("%PAGES%", String.valueOf(availablePages + 1));
        } else {
            toRet = plugin.getStrings().warpTopHeader.replace("%PAGE%", String.valueOf(page)).replace("%MAXPAGE%", String.valueOf(availablePages + 1));
            @SuppressWarnings("unchecked")
            ArrayList<Warp> tempWarps = (ArrayList<Warp>) getWarps().clone();
            if (noPrivate) {
                ArrayList<Warp> toRemove = new ArrayList<>();
                tempWarps.stream().filter(Warp::isPrivate).forEach(toRemove::add);
                tempWarps.removeAll(toRemove);
            }
            Warp currWarp = null;
            Warp[] warps = new Warp[tempWarps.size()];
            for (int i = 0; i < warps.length; i++) {
                for (Warp warp : tempWarps) {
                    if (currWarp == null) {
                        if (!warp.isPrivate() || player.hasPermission(plugin.getStrings().warpListPrivatePerm) || player.hasPermission(plugin.getStrings().universalPerm)) {
                            currWarp = warp;
                        }
                    } else {
                        if (currWarp.getTimesWarpedTo() < warp.getTimesWarpedTo()) {
                            if (!warp.isPrivate() || player.hasPermission(plugin.getStrings().warpListPrivatePerm) || player.hasPermission(plugin.getStrings().universalPerm)) {
                                currWarp = warp;
                            }
                        }
                    }
                }
                warps[i] = currWarp;
                tempWarps.remove(currWarp);
                currWarp = null;
            }
            for (int i = actualPage * 7; i < actualPage * 7 + 7; i++) {
                if (!(i >= warps.length)) {
                    if (!warps[i].isPrivate()) {
                        toRet += plugin.getStrings().warpTopSub.replace("%POSITION%", String.valueOf(i + 1)).replace("%WARPNAME%", warps[i].getName()).replace("%WARPAMOUNT%", String.valueOf(warps[i].getTimesWarpedTo())).replace("%OWNERNAME%", Bukkit.getOfflinePlayer(warps[i].getOwner()).getName());
                    } else {
                        toRet += plugin.getStrings().warpTopSubPrivate.replace("%POSITION%", String.valueOf(i + 1)).replace("%WARPNAME%", warps[i].getName()).replace("%WARPAMOUNT%", String.valueOf(warps[i].getTimesWarpedTo())).replace("%OWNERNAME%", Bukkit.getOfflinePlayer(warps[i].getOwner()).getName());
                    }
                }
            }
            return toRet;
        }
    }

    public int getDeletedWarpsAmount() {
        if (cfg.getCustomConfig(config).isSet("total")) {
            return cfg.getCustomConfig(config).getInt("total");
        } else {
            return 0;
        }
    }

    private ArrayList<Warp> searchWarps(String toFind) {
        toFind = toFind.toLowerCase();
        ArrayList<Warp> warps = new ArrayList<>();
        for (Warp warp : getWarps()) {
            if (warp.getName().toLowerCase().contains(toFind)) {
                warps.add(warp);
            }
        }
        return warps;
    }

    public String formatWarps(String toSearch, int page) {
        ArrayList<Warp> foundWarps = searchWarps(toSearch);
        int total = (int) Math.ceil(((double) foundWarps.size()) / 9);
        String toRet = plugin.getStrings().warpSearchHeader.replace("%PAGE%", String.valueOf(page)).replace("%MAXPAGE%", String.valueOf(total));
        page = page - 1;
        if (page > foundWarps.size() / 9 || page < 0) {
            return plugin.getStrings().noValidPage.replace("%MAXPAGE%", String.valueOf(total));
        } else if (foundWarps.size() == 0) {
            return plugin.getStrings().noWarpsFoundForQuery.replace("%QUERY%", toSearch);
        } else {
            int min = page * 9;
            int max = min + 9 < foundWarps.size() ? min + 9 : foundWarps.size();
            for (int i = min; i < max; i++) {
                toRet += getWarpListString(foundWarps.get(i), i);
            }
            return toRet;
        }
    }

    private String getWarpListString(Warp warp, int i) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(warp.getOwner());
        String toRet;
        if (offlinePlayer != null && offlinePlayer.getName() != null) {
            if (!warp.isPrivate()) {
                toRet = "\n" + plugin.getStrings().warpListSub.replace("%NAME%", warp.getName()).replace("%OWNER%", offlinePlayer.getName()).replace("%COUNT%", String.valueOf(i + 1));
            } else {
                toRet = "\n" + plugin.getStrings().warpListSubPrivate.replace("%NAME%", warp.getName()).replace("%OWNER%", offlinePlayer.getName()).replace("%COUNT%", String.valueOf(i + 1));
            }
        } else {
            if (!warp.isPrivate()) {
                toRet = "\n" + plugin.getStrings().warpListSub.replace("%NAME%", warp.getName()).replace("%OWNER%", "unknown owner").replace("%COUNT%", String.valueOf(i + 1));
            } else {
                toRet = "\n" + plugin.getStrings().warpListSubPrivate.replace("%NAME%", warp.getName()).replace("%OWNER%", "unknown owner").replace("%COUNT%", String.valueOf(i + 1));
            }
        }
        return toRet;
    }
}
