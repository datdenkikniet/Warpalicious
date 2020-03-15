package nl.datdenkikniet.warpalicious.handling;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import nl.datdenkikniet.warpalicious.WarpaliciousPlugin;
import nl.datdenkikniet.warpalicious.commands.*;
import nl.datdenkikniet.warpalicious.config.Config;
import nl.datdenkikniet.warpalicious.config.CustomConfig;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class WarpHandler {

  public static final int WARPLIST_PAGE_SIZE = 7;

  private WarpaliciousPlugin plugin;
  private Config config;
  private CustomConfig cfg;
  private List<Warp> warps = new ArrayList<>();
  private Strings str;

  public WarpHandler(WarpaliciousPlugin instance, Config config) {
    plugin = instance;
    this.config = config;
    cfg = plugin.cfgHandler;
    str = plugin.getStrings();
  }

  public WarpaliciousPlugin getPlugin() {
    return plugin;
  }

  public List<Warp> getWarps() {
    return warps;
  }

  void addWarp(Warp warp) {
    warps.add(warp);
  }

  public static HashMap<Flag, Boolean> getDefaultFlags() {
    HashMap<Flag, Boolean> toRet = new HashMap<>();
    toRet.put(Flag.PRIVATE, false);
    toRet.put(Flag.SIGNPRIVATE, true);
    return toRet;
  }

  public void loadWarps() {
    FileConfiguration c = cfg.getCustomConfig(config);
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
              flags.put(flag, getDefaultFlags().get(flag));
            }
          }
          UUID owner = UUID.fromString(c.getString(key + ".owner"));
          Location loc = plugin.stringToLoc(c.getString(key + ".location"));
          int times = c.getInt(key + ".timeswarpedto");
          Set<UUID> invitedPlayers = new HashSet<>();
          c.getStringList(key + ".invited").stream()
              .forEach(str -> invitedPlayers.add(UUID.fromString(str)));
          new Warp(getPlugin(), owner, loc, key, flags, times, invitedPlayers);
        }
      } catch (Exception ex) {
        System.out.println("Error while loading flag " + key);
      }
    }
  }

  public void saveWarps() {
    FileConfiguration c = cfg.getCustomConfig(config);

    for (Warp warp : warps) {
      String key = warp.getName();
      c.set(key + ".owner", warp.getOwner().toString());
      if (warp.getLocation(false).getWorld() != null) {
        c.set(key + ".location", plugin.locationToString(warp.getLocation(false)));
      } else {
        plugin.getLogger()
            .log(Level.WARNING, "Warning: warp \"" + key + "\" has an invalid world!");
      }
      for (Flag flag : Flag.values()) {
        c.set(key + ".flags." + flag.name(), warp.getFlags().get(flag));
      }
      ArrayList<String> invited = new ArrayList<>();
      warp.getInvitedPlayers().forEach(uuid -> invited.add(uuid.toString()));
      c.set(key + ".invited", invited);
      c.set(key + ".timeswarpedto", warp.getTimesWarpedTo());
    }
    cfg.saveCustomConfig(config);
  }

  public void delWarp(Warp warp) {
    warps.remove(warp);
    if (cfg.getCustomConfig(config).isSet("total")) {
      cfg.getCustomConfig(config)
          .set("total", cfg.getCustomConfig(config).getInt("total") + warp.getTimesWarpedTo());
    } else {
      cfg.getCustomConfig(config).set("total", warp.getTimesWarpedTo());
    }
    cfg.getCustomConfig(config).set(warp.getName(), null);
    cfg.saveCustomConfig(config);
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
    ArrayList<Warp> toRet = new ArrayList<>();
    warps.stream().filter(w -> w.getOwner().equals(player)).forEach(toRet::add);
    return toRet;
  }

  public List<Warp> getWarpListPage(int page, int maxPage, List<Warp> allWarps) {
    if (page > maxPage || page < 1) {
      return null;
    } else {
      page = page - 1;
      int min = page * WARPLIST_PAGE_SIZE;
      int max = (page * WARPLIST_PAGE_SIZE) + WARPLIST_PAGE_SIZE;
      List<Warp> warpsPage = new ArrayList<>();
      for (int i = min; i < (Math.min(max, allWarps.size())); i++) {
        warpsPage.add(allWarps.get(i));
      }
      return warpsPage;
    }
  }

  public int toPageCount(int warpCount) {
    return (int) Math.ceil(((double) warpCount) / WARPLIST_PAGE_SIZE);
  }

  public int getWarplistPagesSelfCount(Player player) {
    return toPageCount(getWarps(player.getUniqueId()).size());
  }

  public List<Warp> getWarpListPageSelf(Player player, int page) {
    return getWarpListPage(page, getWarplistPagesSelfCount(player), getWarps(player.getUniqueId()));
  }

  public List<Warp> getWarpList(Player player) {
    if (str.checkPermission(player, str.warpListPrivatePerm)) {
      return warps;
    } else {
      List<Warp> visibleWarps = new ArrayList<>();
      for (Warp warp : warps) {
        if (!warp.isPrivate() || warp.isInvited(player.getUniqueId()) || warp.getOwner()
            .equals(player.getUniqueId())) {
          visibleWarps.add(warp);
        }
      }
      return visibleWarps;
    }
  }

  public int getWarpListPagesCount(Player player) {
    return toPageCount(getWarpList(player).size());
  }

  public List<Warp> getWarpListPage(Player player, int page) {
    return getWarpListPage(page, getWarpListPagesCount(player), getWarpList(player));
  }

  public List<Warp> getWarpListOther(Player player, OfflinePlayer pl) {
    ArrayList<Warp> otherWarps = new ArrayList<>();
    warps.stream().filter(warp -> (warp.isInvited(player.getUniqueId()) || !warp.isPrivate() || str
        .checkPermission(player, str.warpListPrivatePerm)) && warp.getOwner()
        .equals(pl.getUniqueId())).forEach(otherWarps::add);
    return otherWarps;
  }

  public int getWarpListPagesOtherCount(Player player, OfflinePlayer pl) {
    return toPageCount(getWarpListOther(player, pl).size());
  }

  public List<Warp> getWarpListPageOther(Player player, OfflinePlayer pl, int page) {
    return getWarpListPage(page, getWarpListPagesOtherCount(player, pl),
        getWarpListOther(player, pl));
  }

  public List<Warp> getWarpListSorted(Player player, boolean includePrivate) {
    List<Warp> warpList = new ArrayList<>();
    getWarpList(player).stream().filter((warp) -> includePrivate || !warp.isPrivate())
        .forEach(warpList::add);
    warpList.sort((w1, w2) -> w2.getTimesWarpedTo() - w1.getTimesWarpedTo());
    return warpList;
  }

  public int getDeletedWarpsAmount() {
    if (cfg.getCustomConfig(config).isSet("total")) {
      return cfg.getCustomConfig(config).getInt("total");
    } else {
      return 0;
    }
  }

  public int searchWarpsPagesCount(String toFind, Player player){
    return toPageCount(searchWarps(toFind, player).size());
  }

  public List<Warp> searchWarps(String toFind, Player player) {
    toFind = toFind.toLowerCase();
    ArrayList<Warp> warps = new ArrayList<>();
    for (Warp warp : getWarpList(player)) {
      if (warp.getName().toLowerCase().contains(toFind)) {
        warps.add(warp);
      }
    }
    return warps;
  }

  public List<Warp> searchWarpsPage(String toFind, int page, Player player) {
    List<Warp> foundWarps = searchWarps(toFind, player);
    int totalPages = toPageCount(foundWarps.size());
    return getWarpListPage(page, totalPages, foundWarps);
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

  public boolean allowedToWarp(Warp warp, Player player, TeleportMode mode) {
    if (mode == TeleportMode.COMMAND) {
      return (!warp.isPrivate() || str.checkPermission(player, str.warpToPrivatePerm) || warp
          .getOwner().equals(player.getUniqueId()) || warp.isInvited(player.getUniqueId()));
    } else {
      return (!warp.getFlag(Flag.SIGNPRIVATE) || str.checkPermission(player, str.warpToPrivatePerm)
          || warp.getOwner().equals(player.getUniqueId()) || warp.isInvited(player.getUniqueId()));
    }
  }


}
