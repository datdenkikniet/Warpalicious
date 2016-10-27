package nl.datdenkikniet.warpalicious.handling;

import nl.datdenkikniet.warpalicious.MyWarpsPlugin;
import nl.datdenkikniet.warpalicious.commands.*;
import nl.datdenkikniet.warpalicious.config.Config;
import nl.datdenkikniet.warpalicious.config.CustomConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
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


	public WarpHandler(MyWarpsPlugin instance, Config config){
		plugin = instance;
		this.config = config;
		cfg = plugin.cfgHandler;
		flags.add("private");
	}
	public ArrayList<Warp> getWarps(){
		return warps;
	}
	void addWarp(Warp warp){
		warps.add(warp);
	}
	public HashMap<String, Boolean> getDefaultFlags(){
		HashMap<String, Boolean> toRet = new HashMap<>();
		for (String flag : flags){
			toRet.put(flag, false);
		}
		return toRet;
	}
	private void loadWarps(){
		FileConfiguration c = cfg.getCustomConfig(config);
		for (String key : c.getKeys(false)){
            if (!c.isSet(key + ".timeswarpedto")){
                c.set(key + ".timeswarpedto", 0);
            }
			HashMap<String, Boolean> flags = new HashMap<>();
			for (String k2 : c.getConfigurationSection(key + ".flags").getKeys(false)){
				flags.put(k2, c.getBoolean(key + ".flags." + k2));
			}
			UUID owner = UUID.fromString(c.getString(key + ".owner"));
			Location loc = plugin.stringToLoc(c.getString(key + ".location"));
			int times = c.getInt(key + ".timeswarpedto");
			new Warp(owner, loc, key,flags, this, times);
		}
	}
    private void loadCommands(){
		plugin.getCommand("warp").setExecutor(new WarpCommand(plugin.getStrings(), this));
		plugin.getCommand("setwarp").setExecutor(new SetWarpCommand(plugin.getStrings(), this));
		plugin.getCommand("delwarp").setExecutor(new DelWarpCommand(plugin.getStrings(), this));
		plugin.getCommand("warplist").setExecutor(new WarplistCommand(plugin.getStrings(), this));
		plugin.getCommand("editwarp").setExecutor(new EditWarpCommand(plugin.getStrings(), this));
		plugin.getCommand("warpinfo").setExecutor(new WarpinfoCommand(plugin.getStrings(), this));
	}
    public void delWarp(Warp warp){
        warps.remove(warp);
        cfg.getCustomConfig(config).set(warp.getName(), null);
        cfg.saveCustomConfig(config);
    }
	/*public boolean warpPlayer(UUID player, String warpName){
		return warpPlayer(player, getWarp(warpName));
	}
    private boolean warpPlayer(UUID player, Warp warp){
		return ((!warp.isPrivate() || warp.getOwner().equals(player)) || Bukkit.getPlayer(player).hasPermission(plugin.getStrings().warpPrivatePerm));
	}*/
	public Warp getWarp(String name, boolean isWarp){
		for (Warp warp : warps){
			if (warp.getName().equalsIgnoreCase(name)){
				if (isWarp){
					warp.addWarpedTo();
				}
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
		for (Warp warp : warps){
			if (warp.getOwner().equals(player)){
				toRet.add(warp);
			}
		}
		return toRet;
	}
	public void saveWarps(){
		FileConfiguration c = cfg.getCustomConfig(config);
		for (Warp warp : warps){
			c.set(warp.getName () + ".owner", warp.getOwner().toString());
			c.set(warp.getName() + ".location", plugin.locationToString(warp.getLocation()));
			for (String flag : warp.getFlags().keySet()){
				c.set(warp.getName() + ".flags." + flag, warp.getFlags().get(flag));
			}
			c.set(warp.getName() + ".timeswarpedto", warp.getTimesWarpedTo());
		}
		cfg.saveCustomConfig(config);
	}
	public boolean isFlag(String flag){
		return (flags.contains(flag.toLowerCase()));
	}
	public String getFlags(){
		return (flags.toString().replace("[", "").replace("]", ""));
	}
	public String getWarpListPages(Player player){
		if (player.hasPermission(plugin.getStrings().warpListPrivatePerm) || player.hasPermission(plugin.getStrings().universalPerm)){
			return String.valueOf((int) Math.ceil( ((double) warps.size())/9));
		} else {
			double amount = 0;
			for (Warp warp : warps){
				if (!warp.isPrivate()){
					amount++;
				}
			}
			return String.valueOf((int) Math.ceil(amount/9));
		}
	}
	private int getWarpListPagesAmt(Player player){
		if (player.hasPermission(plugin.getStrings().warpListPrivatePerm) || player.hasPermission(plugin.getStrings().universalPerm)){
			return (int) Math.ceil(((double) warps.size())/9);
		} else {
			double amount = 0;
			for (Warp warp : warps){
				if (!warp.isPrivate()){
					amount++;
				}
			}
			return (int) Math.ceil(amount/9);
		}
	}
    private int getWarplistPagesSelfAmt(Player player){
		double amount = 0;
		for (Warp warp : warps){
			if (warp.getOwner().equals(player.getUniqueId())){
				amount++;
			}
		}
		return (int) Math.ceil(amount/9);
	}
	public String getWarpListPage(Player player, int page){
		if (page > getWarpListPagesAmt(player) || page < 1){
			return plugin.getStrings().warpPageNotExists;
		} else {
			page = page-1;
			String toRet = plugin.getStrings().warpList.replace("%PAGE%", String.valueOf(page+1)).replace("%MAXPAGE%", String.valueOf(getWarpListPagesAmt(player)));
			int min = page*9;
			int max =(page*9) + 9;
			if (player.hasPermission(plugin.getStrings().warpListPrivatePerm) || player.hasPermission(plugin.getStrings().universalPerm)){
				for (int i = min; i < (max > warps.size() ? warps.size() : max); i++){
					Warp warp = warps.get(i);
					if (warp != null){
						if (!warp.isPrivate()){
							toRet = toRet + "\n" + plugin.getStrings().warpListSub.replace("%NAME%", warp.getName()).replace("%OWNER%", Bukkit.getOfflinePlayer(warp.getOwner()).getName()).replace("%COUNT%", String.valueOf(i+1));
						} else {
							toRet = toRet + "\n" + plugin.getStrings().warpListSubPrivate.replace("%NAME%", warp.getName()).replace("%OWNER%", Bukkit.getOfflinePlayer(warp.getOwner()).getName()).replace("%COUNT%", String.valueOf(i+1));
						}
					}
				}
			} else {
				ArrayList<Warp> warps2 = new ArrayList<>();
				for (Warp warp : warps){
					if (!warp.isPrivate() || warp.getOwner().equals(player.getUniqueId())){
						warps2.add(warp);
					}
				}
				for (int i = min; i < (max > warps2.size() ? warps2.size() : max); i++){
					Warp warp = warps2.get(i);
					if (warp != null){
						toRet = toRet + "\n" + plugin.getStrings().warpListSub.replace("%NAME%", warp.getName()).replace("%OWNER%", Bukkit.getOfflinePlayer(warp.getOwner()).getName()).replace("%COUNT%", String.valueOf(i+1));
					}
				}
			}
			return toRet;
		}
	}
	public String getWarpListPageSelf(Player player, int page){
		if (page > getWarplistPagesSelfAmt(player) || page < 1){
			return plugin.getStrings().warpPageNotExists;
		} else {
			page = page-1;
			int min = page*9;
			int max =(page*9) + 9;
			ArrayList<Warp> warps2 = new ArrayList<>();
			for (Warp warp : warps){
				if (warp.getOwner().equals(player.getUniqueId())){
					warps2.add(warp);
				}
			}
			String toRet = plugin.getStrings().warpsOwnList.replace("%PAGE%", String.valueOf(page+1)).replace("%MAXPAGE%", String.valueOf(getWarplistPagesSelfAmt(player)));
			for (int i = min; i < (max > warps2.size() ? warps2.size() : max); i++){
				Warp warp = warps2.get(i);
				if (!warp.isPrivate()){
					toRet = toRet + "\n" + plugin.getStrings().warpListSub.replace("%NAME%", warp.getName()).replace("%OWNER%", Bukkit.getOfflinePlayer(warp.getOwner()).getName()).replace("%COUNT%", String.valueOf(i+1));
				} else {
					toRet = toRet + "\n" + plugin.getStrings().warpListSubPrivate.replace("%NAME%", warp.getName()).replace("%OWNER%", Bukkit.getOfflinePlayer(warp.getOwner()).getName()).replace("%COUNT%", String.valueOf(i+1));
				}
			}
			return toRet;
		}
	}

	public String getWarpListPageOther(Player player, OfflinePlayer pl,	Integer page) {
		if (page > getWarpListPagesAmtOther(player, pl) || page < 1){
			return plugin.getStrings().warpPageNotExists;
		} else {
			page = page-1;
			int min = page*9;
			int max =(page*9) + 9;
			ArrayList<Warp> warps2 = new ArrayList<>();
			for (Warp warp : warps){
				if ((!warp.isPrivate() || player.hasPermission(plugin.getStrings().warpListPrivatePerm)) && warp.getOwner().equals(pl.getUniqueId())){
					warps2.add(warp);
				}
			}
			String toRet = plugin.getStrings().warpOthersList.replace("%PAGE%", String.valueOf(page+1)).replace("%MAXPAGE%", String.valueOf(getWarpListPagesAmtOther(player, pl))).replace("%PLAYERNAME%", pl.getName());
			for (int i = min; i < (max > warps2.size() ? warps2.size() : max); i++){
				Warp warp = warps2.get(i);
				if (!warp.isPrivate()){
					toRet = toRet + "\n" + plugin.getStrings().warpListSub.replace("%NAME%", warp.getName()).replace("%OWNER%", Bukkit.getOfflinePlayer(warp.getOwner()).getName()).replace("%COUNT%", String.valueOf(i+1));
				} else {
					toRet = toRet + "\n" + plugin.getStrings().warpListSubPrivate.replace("%NAME%", warp.getName()).replace("%OWNER%", Bukkit.getOfflinePlayer(warp.getOwner()).getName()).replace("%COUNT%", String.valueOf(i+1));
				}
			}
			return toRet;
		}
	}

	private int getWarpListPagesAmtOther(Player player, OfflinePlayer pl) {
		double amount = 0;
		for (Warp warp : warps){
			if ((!warp.isPrivate() || player.hasPermission(plugin.getStrings().warpListPrivatePerm)) && warp.getOwner().equals(pl.getUniqueId())){
				amount++;
			}
		}
		return (int) Math.ceil(amount/9);
	}
    public boolean parseBoolean(String bool){
        if (bool.equalsIgnoreCase("yes") || bool.equalsIgnoreCase("y") || bool.equalsIgnoreCase("true") || bool.equalsIgnoreCase("allow")){
            return true;
        }
        else if (bool.equalsIgnoreCase("no") || bool.equalsIgnoreCase("n") || bool.equalsIgnoreCase("false") || bool.equalsIgnoreCase("deny")){
            return false;
        }
        return false;
    }
}
