package nl.datdenkikniet.warpalicious.commands;

import java.util.List;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.config.messages.StringUtils;
import nl.datdenkikniet.warpalicious.handling.Warp;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarplistCommand implements CommandExecutor {

  private Strings str;
  private WarpHandler handler;

  public WarplistCommand(Strings instance, WarpHandler hd) {
    str = instance;
    handler = hd;
  }

  @SuppressWarnings("deprecation")
  public boolean onCommand(CommandSender sen, Command cmd, String label, String[] args) {
    if (!(sen instanceof Player)) {
      sen.sendMessage("You can't do this as non-player!");
      return true;
    }
    Player player = (Player) sen;
    if (str.checkPermission(sen, str.warpListPerm) || str
        .checkPermission(sen, str.warpListPrivatePerm) || str
        .checkPermission(sen, str.warpListOthersPerm) || str
        .checkPermission(sen, str.warpListSelfPerm) || str
        .checkPermission(sen, str.universalPerm)) {
      if (args.length == 0) {
        sen.sendMessage(getWarpListPage(player, 1));
        return true;
      } else if (args.length == 1) {
        if (isNumeric(args[0])) {
          sen.sendMessage(getWarpListPage(player, Integer.parseInt(args[0])));
          return true;
        } else if (args[0].equalsIgnoreCase("-help")) {
          sen.sendMessage(str.getUsage(cmd, label));
          return true;
        } else {
          if (args[0].equalsIgnoreCase("self")) {
            if (str.checkPermission(sen, str.warpListSelfPerm)) {
              sen.sendMessage(getWarpListPageSelf(player, 1));
              return true;
            } else {
              sen.sendMessage(str.noperm);
              return true;
            }
          } else if (Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore()) {
            if (str.checkPermission(sen, str.warpListOthersPerm)) {
              OfflinePlayer pl = Bukkit.getOfflinePlayer(args[0]);
              sen.sendMessage(getWarpListPageOther(player, pl, 1));
              return true;
            } else {
              sen.sendMessage(str.noperm);
              return true;
            }
          } else {
            sen.sendMessage(
                str.warpListHelp
                    .replace("%PAGES%", String.valueOf(handler.getWarpListPagesCount(player))));
            return true;
          }
        }
      } else if (args.length == 2) {
        if (args[0].equalsIgnoreCase("self") && isNumeric(args[1])) {
          if (str.checkPermission(sen, str.warpListSelfPerm)) {
            sen.sendMessage(getWarpListPageSelf(player, Integer.parseInt(args[1])));
            return true;
          } else {
            sen.sendMessage(str.noperm);
            return true;
          }
        } else if (Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore() && isNumeric(args[1])) {
          if (str.checkPermission(sen, str.warpListOthersPerm)) {
            OfflinePlayer pl = Bukkit.getOfflinePlayer(args[0]);
            sen.sendMessage(getWarpListPageOther(player, pl, Integer.parseInt(args[1])));
            return true;
          } else {
            sen.sendMessage(str.noperm);
            return true;
          }
        } else {
          sen.sendMessage(str.getUsage(cmd, label));
          return true;
        }
      } else {
        sen.sendMessage(str.getUsage(cmd, label));
        return true;
      }
    } else {
      sen.sendMessage(str.noperm);
      return true;
    }
  }

  private boolean isNumeric(String str) {
    try {
      Integer.parseInt(str);
      return true;
    } catch (NumberFormatException ex) {
      return false;
    }
  }

  private String getWarpListPage(Player player, int page) {
    List<Warp> warps = handler.getWarpListPage(player, page);
    String title = str.warpList;
    return StringUtils
        .toWarpListPageString(str, player, title, page, handler.getWarpListPagesCount(player),
            warps);
  }

  private String getWarpListPageSelf(Player player, int page) {
    List<Warp> warps = handler.getWarpListPageSelf(player, page);
    String title = str.warpListSelf;
    return StringUtils
        .toWarpListPageString(str, player, title, page, handler.getWarplistPagesSelfCount(player),
            warps);
  }

  private String getWarpListPageOther(Player player, OfflinePlayer pl, int page) {
    List<Warp> warps = handler.getWarpListPageOther(player, pl, page);
    String title;
    if (pl != null && pl.getName() != null) {
      title = str.warpOthersList.replace("%PLAYERNAME%", pl.getName());
    } else {
      title = str.warpOthersList.replace("%PLAYERNAME%", "unknown player");
    }
    return StringUtils.toWarpListPageString(str, player, title, page,
        handler.getWarpListPagesOtherCount(player, pl),
        warps);
  }
}
