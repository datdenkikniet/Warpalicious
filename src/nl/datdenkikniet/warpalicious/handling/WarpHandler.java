package nl.datdenkikniet.warpalicious.handling;

import nl.datdenkikniet.warpalicious.WarpaliciousPlugin;
import nl.datdenkikniet.warpalicious.commands.*;
import nl.datdenkikniet.warpalicious.config.Config;
import nl.datdenkikniet.warpalicious.config.CustomConfig;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class WarpHandler {

    private WarpaliciousPlugin plugin;
    private Config config;
    private CustomConfig cfg;
    private ArrayList<Warp> warps = new ArrayList<>();
    private Strings str;

    public WarpHandler(WarpaliciousPlugin instance, Config config){
        plugin = instance;
        this.config = config;
        cfg = plugin.cfgHandler;
        str = plugin.getStrings();
    }

    public ArrayList<Warp> getWarps(){
        return warps;
    }

    void addWarp(Warp warp){
        warps.add(warp);
    }

    public HashMap<Flag, Boolean> getDefaultFlags(){
        HashMap<Flag, Boolean> toRet = new HashMap<>();
        toRet.put(Flag.PRIVATE, false);
        toRet.put(Flag.SIGNPRIVATE, true);
        return toRet;
    }

    private void loadWarps(){
        FileConfiguration c = cfg.getCustomConfig(config);
        for (String key : c.getKeys(false)){
            try{
                if (!key.equalsIgnoreCase("total")){
                    if (!c.isSet(key + ".timeswarpedto")){
                        c.set(key + ".timeswarpedto", 0);
                    }
                    HashMap<Flag, Boolean> flags = new HashMap<>();
                    for (String k2 : c.getConfigurationSection(key + ".flags").getKeys(false)){
                        flags.put(Flag.valueOf(k2.toUpperCase()), c.getBoolean(key + ".flags." + k2));
                    }
                    for (Flag flag : Flag.values()){
                        if (!flags.containsKey(flag)){
                            flags.put(flag, getDefaultFlags().get(flag));
                        }
                    }
                    UUID owner = UUID.fromString(c.getString(key + ".owner"));
                    String worldName = c.getString(key + ".location").split(",")[0];
                    Location loc = plugin.stringToLoc(c.getString(key + ".location"));
                    int times = c.getInt(key + ".timeswarpedto");
                    ArrayList<UUID> invitedPlayers = new ArrayList<>();
                    c.getStringList(key + ".invited").stream().forEach(str -> invitedPlayers.add(UUID.fromString(str)));
                    new Warp(getPlugin(), owner, loc, key, flags, this, times, invitedPlayers, worldName);
                }
            } catch (Exception ex){
                System.out.println("Error while loading flag " + key);
            }
        }
    }

    private void loadCommands(){
        plugin.getCommand("warp").setExecutor(new WarpCommand(str, this));
        plugin.getCommand("setwarp").setExecutor(new SetWarpCommand(str, this));
        plugin.getCommand("delwarp").setExecutor(new DelWarpCommand(str, this));
        plugin.getCommand("warplist").setExecutor(new WarplistCommand(str, this));
        plugin.getCommand("editwarp").setExecutor(new EditWarpCommand(str, this));
        plugin.getCommand("warpinfo").setExecutor(new WarpinfoCommand(str, this));
        plugin.getCommand("findwarp").setExecutor(new FindWarpCommand(plugin));
        plugin.getCommand("warpinvite").setExecutor(new WarpInviteCommand(plugin, str));
        plugin.getCommand("warpuninvite").setExecutor(new WarpInviteCommand(plugin, str));
    }

    public void delWarp(Warp warp){
        warps.remove(warp);
        if (cfg.getCustomConfig(config).isSet("total")){
            cfg.getCustomConfig(config).set("total", cfg.getCustomConfig(config).getInt("total") + warp.getTimesWarpedTo());
        } else {
            cfg.getCustomConfig(config).set("total", warp.getTimesWarpedTo());
        }
        cfg.getCustomConfig(config).set(warp.getName(), null);
        cfg.saveCustomConfig(config);
    }

    public Warp getWarp(String name){
        for (Warp warp : warps){
            if (warp.getName().equalsIgnoreCase(name)){
                return warp;
            }
        }
        return null;
    }

    public void load(){
        loadWarps();
        loadCommands();
    }

    public ArrayList<Warp> getWarps(UUID player){
        ArrayList<Warp> toRet = new ArrayList<>();
        warps.stream().filter(w -> w.getOwner().equals(player)).forEach(toRet::add);
        return toRet;
    }

    public void saveWarps(){
        FileConfiguration c = cfg.getCustomConfig(config);
        for (Warp warp : warps){
            c.set(warp.getName() + ".owner", warp.getOwner().toString());
            if (warp.getLocation(false).getWorld() != null){
                c.set(warp.getName() + ".location", plugin.locationToString(warp.getLocation(false)));
            } else {
                plugin.getLogger().log(Level.WARNING, "Warning: warp \"" + warp.getName() + "\" has an invalid world!");
            }
            for (Flag flag : Flag.values()){
                c.set(warp.getName() + ".flags." + flag.name(), warp.getFlags().get(flag));
            }
            ArrayList<String> invited = new ArrayList<>();
            warp.getInvitedPlayers().stream().forEach(uuid -> invited.add(uuid.toString()));
            c.set(warp.getName() + ".invited", invited);
        }
        cfg.saveCustomConfig(config);
    }

    public boolean isFlag(String flag){
        try{
            Flag.valueOf(flag.toUpperCase());
            return true;
        } catch (Exception ex){
            return false;
        }
    }

    public String getFlags(){
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < Flag.values().length; i++){
            strBuilder.append(Flag.values()[i]);
            if (i != Flag.values().length - 1){
                strBuilder.append(", ");
            }
        }
        return strBuilder.toString();
    }

    public String getWarpListPages(Player player){
        if (str.checkPermission(player, str.warpListPrivatePerm)){
            return String.valueOf((int) Math.ceil(((double) warps.size()) / 9));
        } else {
            double amount = 0;
            for (Warp warp : warps){
                if (!warp.isPrivate() || warp.isInvited(player.getUniqueId())){
                    amount++;
                }
            }
            return String.valueOf((int) Math.ceil(amount / 9));
        }
    }

    private int getWarpListPagesAmt(Player player){
        if (str.checkPermission(player, str.warpListPrivatePerm)){
            return (int) Math.ceil(((double) warps.size()) / 9);
        } else {
            double amount = 0;
            for (Warp warp : warps){
                if (!warp.isPrivate() || warp.isInvited(player.getUniqueId()) || warp.getOwner().equals(player.getUniqueId())){
                    amount++;
                }
            }
            return (int) Math.ceil(amount / 9);
        }
    }

    private int getWarplistPagesSelfAmt(Player player){
        double amount = 0;
        for (Warp warp : warps){
            if (warp.getOwner().equals(player.getUniqueId())){
                amount++;
            }
        }
        return (int) Math.ceil(amount / 9);
    }

    public String getWarpListPage(Player player, int page){
        if (page > getWarpListPagesAmt(player) || page < 1){
            return str.warpPageNotExists;
        } else {
            page = page - 1;
            String toRet = str.warpList.replace("%PAGE%", String.valueOf(page + 1)).replace("%MAXPAGE%", String.valueOf(getWarpListPagesAmt(player)));
            int min = page * 9;
            int max = (page * 9) + 9;
            if (str.checkPermission(player, str.warpListPrivatePerm)){
                for (int i = min; i < (max > warps.size() ? warps.size() : max); i++){
                    toRet += getWarpListString(warps.get(i), i, player.getUniqueId());
                }
            } else {
                ArrayList<Warp> warpsFiltered = new ArrayList<>();
                warps.stream().filter(warp -> !warp.isPrivate() || warp.getOwner().equals(player.getUniqueId()) || warp.isInvited(player.getUniqueId())).forEach(warpsFiltered::add);
                for (int i = min; i < (max > warpsFiltered.size() ? warpsFiltered.size() : max); i++){
                    toRet += getWarpListString(warpsFiltered.get(i), i, player.getUniqueId());
                }
            }
            return toRet;
        }
    }

    public String getWarpListPageSelf(Player player, int page){
        if (page > getWarplistPagesSelfAmt(player) || page < 1){
            return str.warpPageNotExists;
        } else {
            page = page - 1;
            int min = page * 9;
            int max = (page * 9) + 9;
            ArrayList<Warp> warps2 = new ArrayList<>();
            warps.stream().filter(warp -> warp.getOwner().equals(player.getUniqueId())).forEach(warps2::add);
            String toRet = str.warpsOwnList.replace("%PAGE%", String.valueOf(page + 1)).replace("%MAXPAGE%", String.valueOf(getWarplistPagesSelfAmt(player)));
            for (int i = min; i < (max > warps2.size() ? warps2.size() : max); i++){
                toRet += getWarpListString(warps2.get(i), i, player.getUniqueId());
            }
            return toRet;
        }
    }

    public String getWarpListPageOther(Player player, OfflinePlayer pl, Integer page){
        if (page > getWarpListPagesAmtOther(player, pl) || page < 1){
            return str.warpPageNotExists;
        } else {
            page = page - 1;
            int min = page * 9;
            int max = (page * 9) + 9;
            ArrayList<Warp> warps2 = new ArrayList<>();
            warps.stream().filter(warp -> (warp.isInvited(player.getUniqueId()) || !warp.isPrivate() || str.checkPermission(player, str.warpListPrivatePerm)) && warp.getOwner().equals(pl.getUniqueId())).forEach(warps2::add);
            String toRet = str.warpOthersList.replace("%PAGE%", String.valueOf(page + 1)).replace("%MAXPAGE%", String.valueOf(getWarpListPagesAmtOther(player, pl))).replace("%PLAYERNAME%", pl.getName());
            for (int i = min; i < (max > warps2.size() ? warps2.size() : max); i++){
                toRet += getWarpListString(warps2.get(i), i, player.getUniqueId());
            }
            return toRet;
        }
    }

    private int getWarpListPagesAmtOther(Player player, OfflinePlayer pl){
        double amount = 0;
        for (Warp warp : warps){
            if ((warp.isInvited(player.getUniqueId()) || !warp.isPrivate() || str.checkPermission(player, str.warpListPrivatePerm)) && warp.getOwner().equals(pl.getUniqueId())){
                amount++;
            }
        }
        return (int) Math.ceil(amount / 9);
    }

    public boolean parseBoolean(String bool){
        if (bool.equalsIgnoreCase("yes") || bool.equalsIgnoreCase("y") || bool.equalsIgnoreCase("true") || bool.equalsIgnoreCase("allow")){
            return true;
        } else if (bool.equalsIgnoreCase("no") || bool.equalsIgnoreCase("n") || bool.equalsIgnoreCase("false") || bool.equalsIgnoreCase("deny")){
            return false;
        }
        return false;
    }

    public String sortPage(Player player, int page, boolean noPrivate){
        String toRet;
        int actualPage = page - 1;
        int availablePages = 0;
        if (str.checkPermission(player, str.warpListPrivatePerm) && !noPrivate){
            availablePages = getWarps().size() / 7;
        } else {
            for (Warp warp : warps){
                if (!warp.isPrivate() || warp.isInvited(player.getUniqueId())){
                    availablePages++;
                }
            }
            availablePages = availablePages / 7;
        }
        if (actualPage > availablePages || actualPage < 0){
            return str.noValidPage.replace("%PAGES%", String.valueOf(availablePages + 1));
        } else {
            toRet = str.warpTopHeader.replace("%PAGE%", String.valueOf(page)).replace("%MAXPAGE%", String.valueOf(availablePages + 1));
            @SuppressWarnings("unchecked") ArrayList<Warp> tempWarps = (ArrayList<Warp>) getWarps().clone();
            if (noPrivate){
                ArrayList<Warp> toRemove = new ArrayList<>();
                tempWarps.stream().filter(warp -> warp.isPrivate() && !warp.isInvited(player.getUniqueId())).forEach(toRemove::add);
                tempWarps.removeAll(toRemove);
            }
            Warp currWarp = null;
            Warp[] warps = new Warp[tempWarps.size()];
            for (int i = 0; i < warps.length; i++){
                for (Warp warp : tempWarps){
                    if (currWarp == null){
                        if (!warp.isPrivate() || str.checkPermission(player, str.warpListPrivatePerm) || warp.isInvited(player.getUniqueId())){
                            currWarp = warp;
                        }
                    } else {
                        if (currWarp.getTimesWarpedTo() < warp.getTimesWarpedTo()){
                            if (!warp.isPrivate() || str.checkPermission(player, str.warpListPrivatePerm) || warp.isInvited(player.getUniqueId())){
                                currWarp = warp;
                            }
                        }
                    }
                }
                warps[i] = currWarp;
                tempWarps.remove(currWarp);
                currWarp = null;
            }
            for (int i = actualPage * 7; i < actualPage * 7 + 7; i++){
                if (!(i >= warps.length)){
                    if (Bukkit.getOfflinePlayer(warps[i].getOwner()).hasPlayedBefore()){
                        if (!warps[i].isPrivate() || warps[i].isInvited(player.getUniqueId())){
                            toRet += str.warpTopSub.replace("%POSITION%", String.valueOf(i + 1)).replace("%WARPNAME%", warps[i].getName()).replace("%WARPAMOUNT%", String.valueOf(warps[i].getTimesWarpedTo())).replace("%OWNERNAME%", Bukkit.getOfflinePlayer(warps[i].getOwner()).getName());
                        } else {
                            toRet += str.warpTopSubPrivate.replace("%POSITION%", String.valueOf(i + 1)).replace("%WARPNAME%", warps[i].getName()).replace("%WARPAMOUNT%", String.valueOf(warps[i].getTimesWarpedTo())).replace("%OWNERNAME%", Bukkit.getOfflinePlayer(warps[i].getOwner()).getName());
                        }
                    } else {
                        if (!warps[i].isPrivate() || warps[i].isInvited(player.getUniqueId())){
                            toRet += str.warpTopSub.replace("%POSITION%", String.valueOf(i + 1)).replace("%WARPNAME%", warps[i].getName()).replace("%WARPAMOUNT%", String.valueOf(warps[i].getTimesWarpedTo())).replace("%OWNERNAME%", warps[i].getOwner().toString());
                        } else {
                            toRet += str.warpTopSubPrivate.replace("%POSITION%", String.valueOf(i + 1)).replace("%WARPNAME%", warps[i].getName()).replace("%WARPAMOUNT%", String.valueOf(warps[i].getTimesWarpedTo())).replace("%OWNERNAME%", warps[i].getOwner().toString());
                        }
                    }
                }
            }
            return toRet;
        }
    }

    public int getDeletedWarpsAmount(){
        if (cfg.getCustomConfig(config).isSet("total")){
            return cfg.getCustomConfig(config).getInt("total");
        } else {
            return 0;
        }
    }

    private ArrayList<Warp> searchWarps(String toFind){
        toFind = toFind.toLowerCase();
        ArrayList<Warp> warps = new ArrayList<>();
        for (Warp warp : getWarps()){
            if (warp.getName().toLowerCase().contains(toFind)){
                warps.add(warp);
            }
        }
        return warps;
    }

    public String formatWarps(String toSearch, int page, UUID player){
        ArrayList<Warp> foundWarps = searchWarps(toSearch);
        int total = (int) Math.ceil(((double) foundWarps.size()) / 9);
        String toRet = str.warpSearchHeader.replace("%PAGE%", String.valueOf(page)).replace("%MAXPAGE%", String.valueOf(total));
        page = page - 1;
        if (page > foundWarps.size() / 9 || page < 0){
            return str.noValidPage.replace("%MAXPAGE%", String.valueOf(total));
        } else if (foundWarps.size() == 0){
            return str.noWarpsFoundForQuery.replace("%QUERY%", toSearch);
        } else {
            int min = page * 9;
            int max = min + 9 < foundWarps.size() ? min + 9 : foundWarps.size();
            for (int i = min; i < max; i++){
                toRet += getWarpListString(foundWarps.get(i), i, player);
            }
            return toRet;
        }
    }

    private String getWarpListString(Warp warp, int i, UUID requester){
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(warp.getOwner());
        boolean invited = warp.isInvited(requester);
        String toRet;
        if (offlinePlayer != null && offlinePlayer.getName() != null){
            if (!warp.isPrivate()){
                toRet = "\n" + str.warpListSub.replace("%NAME%", warp.getName()).replace("%OWNER%", offlinePlayer.getName()).replace("%COUNT%", String.valueOf(i + 1));
            } else if (invited){
                toRet = "\n" + str.warpListSubInvited.replace("%NAME%", warp.getName()).replace("%OWNER%", offlinePlayer.getName()).replace("%COUNT%", String.valueOf(i + 1));
            } else {
                toRet = "\n" + str.warpListSubPrivate.replace("%NAME%", warp.getName()).replace("%OWNER%", offlinePlayer.getName()).replace("%COUNT%", String.valueOf(i + 1));
            }
        } else {
            if (!warp.isPrivate()){
                toRet = "\n" + str.warpListSub.replace("%NAME%", warp.getName()).replace("%OWNER%", "unknown owner").replace("%COUNT%", String.valueOf(i + 1));
            } else if (invited){
                toRet = "\n" + str.warpListSubInvited.replace("%NAME%", warp.getName()).replace("%OWNER%", "unknown owner").replace("%COUNT%", String.valueOf(i + 1));
            } else {
                toRet = "\n" + str.warpListSubPrivate.replace("%NAME%", warp.getName()).replace("%OWNER%", "unknown owner").replace("%COUNT%", String.valueOf(i + 1));
            }
        }
        return toRet;
    }

    public WarpaliciousPlugin getPlugin(){
        return plugin;
    }

    public boolean allowedToWarp(Warp warp, Player player, TeleportMode mode){
        if (mode == TeleportMode.COMMAND){
            return (!warp.isPrivate() || str.checkPermission(player, str.warpToPrivatePerm) || warp.getOwner().equals(player.getUniqueId()) || warp.isInvited(player.getUniqueId()));
        } else {
            return (!warp.getFlag(Flag.SIGNPRIVATE) || str.checkPermission(player, str.warpToPrivatePerm) || warp.getOwner().equals(player.getUniqueId()) || warp.isInvited(player.getUniqueId()));
        }
    }
}
