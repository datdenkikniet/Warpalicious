package nl.datdenkikniet.warpalicious.commands;

import java.util.List;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.config.messages.StringUtils;
import nl.datdenkikniet.warpalicious.handling.Flag;
import nl.datdenkikniet.warpalicious.handling.Warp;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

public class WarpinfoCommand implements CommandExecutor {

  private Strings str;
  private WarpHandler handler;

  public WarpinfoCommand(Strings strings, WarpHandler wh) {
    str = strings;
    handler = wh;
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      return true;
    }
    Player player = (Player) sender;
    if (args.length == 0) {
      if (str.checkPermission(sender, str.warpInfoPerm)) {
        int amount = handler.getWarps().size();
        int timesWarped = 0;
        double amountPrivate = 0;
        for (Warp warp : handler.getWarps()) {
          timesWarped += warp.getTimesWarpedTo();
          if (warp.isPrivate()) {
            amountPrivate++;
          }
        }
        double percentage = (amountPrivate / amount) * 100D;
        DecimalFormat numberFormat = new DecimalFormat("##.##");
        DecimalFormat formatTwo = new DecimalFormat("###");
        timesWarped += handler.getDeletedWarpsAmount();
        sender.sendMessage(str.warpInfoTotalMain);
        if (amountPrivate != 0) {
          sender.sendMessage(str.warpInfoTotalAmount.replace("%AMOUNT%", String.valueOf(amount))
              .replace("%AMOUNTPRIVATE%", String.valueOf(formatTwo.format(amountPrivate)))
              .replace("%PERCENTAGE%", numberFormat.format(percentage)));
        } else {
          sender.sendMessage(str.warpInfoTotalAmount.replace("%AMOUNT%", String.valueOf(amount))
              .replace("%AMOUNTPRIVATE%", String.valueOf(amountPrivate))
              .replace("%PERCENTAGE%", String.valueOf(0)));
        }
        sender.sendMessage(
            str.warpInfoTotalWarped.replace("%TIMESWARPED%", String.valueOf(timesWarped)));
        return true;
      } else {
        sender.sendMessage(str.getUsage(cmd, label));
        return true;
      }
    } else if (args.length == 1) {
      if (args[0].equalsIgnoreCase("-top")) {
        ((Player) sender).performCommand(label + " -top 1");
        return true;
      } else {
        Warp warp = handler.getWarp(args[0]);
        if (warp != null) {
          if (hasPermToViewWarp(player, warp)) {
            sender.sendMessage(str.warpInfoMain.replace("%WARPNAME%", warp.getName()));
            Location loc = warp.getLocation(false);
            if (Bukkit.getOfflinePlayer(warp.getOwner()).hasPlayedBefore()) {
              sender.sendMessage(str.warpInfoBy
                  .replace("%PLAYERNAME%", Bukkit.getOfflinePlayer(warp.getOwner()).getName()));
            } else {
              sender
                  .sendMessage(str.warpInfoBy.replace("%PLAYERNAME%", warp.getOwner().toString()));
            }
            sender.sendMessage(
                str.warpInfoLocation.replace("%X%", String.valueOf(Math.round(loc.getX())))
                    .replace("%Y%", String.valueOf(Math.round(loc.getY())))
                    .replace("%Z%", String.valueOf(Math.round(loc.getZ())))
                    .replace("%WORLD%", loc.getWorld().getName()));
            sender.sendMessage(
                str.warpInfoAmount.replace("%AMOUNT%", String.valueOf(warp.getTimesWarpedTo())));
            if (warp.getOwner().equals(((Player) sender).getUniqueId())
                || warp.isInvited(((Player) sender).getUniqueId())
                && warp.getInvitedPlayers().size() != 0) {
              StringBuilder st = new StringBuilder();
              for (UUID u : warp.getInvitedPlayers()) {
                st.append(Bukkit.getOfflinePlayer(u).getName()).append(", ");
              }
              sender.sendMessage(str.warpInvitedList.replace("%INVITED%", st.toString()));
            }
            StringBuilder fin = new StringBuilder(ChatColor.YELLOW + "Flags:\n");
            for (Flag flag : Flag.values()) {
              fin.append(ChatColor.YELLOW).append(flag.name()).append(": ")
                  .append(warp.getFlag(flag)).append("\n");
            }
            sender.sendMessage(fin.toString());
            return true;
          } else {
            sender.sendMessage(str.noperm);
            return true;
          }
        } else {
          sender.sendMessage(str.warpNotExists);
          return true;
        }
      }
    } else if (args.length == 2) {
      if (args[0].equalsIgnoreCase("-top")) {
        if (str.checkPermission(sender, str.warpTopPerm)) {
          try {
            int page = Integer.parseInt(args[1]);
            sender.sendMessage(getWarpListSortedPage(player, page, true));
            return true;
          } catch (NumberFormatException ex) {
            if (args[1].equalsIgnoreCase("-noprivate")) {
              ((Player) sender).performCommand(label + " -top 1 -noprivate");
              return true;
            } else {
              sender.sendMessage(str.noValidNumber);
              return true;
            }
          }
        } else {
          sender.sendMessage(str.noperm);
          return true;
        }
      } else {
        sender.sendMessage(str.getUsage(cmd, label));
        return true;
      }
    } else if (args.length == 3) {
      if (args[0].equalsIgnoreCase("-top") && args[2].equalsIgnoreCase("-noprivate")) {
        if (str.checkPermission(sender, str.warpTopPerm)) {
          try {
            int page = Integer.parseInt(args[1]);
            sender.sendMessage(getWarpListSortedPage(player, page, false));
            return true;
          } catch (NumberFormatException ex) {
            sender.sendMessage(str.noValidNumber);
            return true;
          }
        } else {
          sender.sendMessage(str.noperm);
          return true;
        }
      } else {
        sender.sendMessage(str.getUsage(cmd, label));
        return true;
      }
    } else {
      sender.sendMessage(str.getUsage(cmd, label));
      return true;
    }
  }

  private boolean hasPermToViewWarp(Player player, Warp warp) {
    if (warp.getOwner().equals(player.getUniqueId()) && str
        .checkPermission(player, str.warpInfoPerm)) {
      return true;
    } else {
      return str.checkPermission(player, str.warpInfoOthersPerm);
    }
  }

  private String getWarpListSortedPage(Player player, int page, boolean includePrivate) {
    List<Warp> warps = handler.getWarpListSorted(player, includePrivate);
    int total = handler.toPageCount(warps.size());
    StringBuilder result = new StringBuilder(
        str.warpTopHeader.replace("%PAGE%", String.valueOf(page))
            .replace("%MAXPAGE%", String.valueOf(total)));
    List<Warp> pageWarps = handler.getWarpListPage(page, total, warps);
    if (pageWarps == null || warps.size() == 0){
      return str.warpPageNotExists;
    }
    for (int i = 0; i < pageWarps.size(); i++) {
      Warp currentWarp = pageWarps.get(i);
      String res;
      if (Bukkit.getOfflinePlayer(currentWarp.getOwner()).hasPlayedBefore()) {
        if (!currentWarp.isPrivate() || currentWarp.isInvited(player.getUniqueId())) {
          res = str.warpTopSub
              .replace("%OWNERNAME%", Bukkit.getOfflinePlayer(currentWarp.getOwner()).getName());
        } else {
          res = str.warpTopSubPrivate
              .replace("%OWNERNAME%", Bukkit.getOfflinePlayer(currentWarp.getOwner()).getName());
        }
      } else {
        if (!currentWarp.isPrivate() || currentWarp.isInvited(player.getUniqueId())) {
          res = str.warpTopSub.replace("%OWNERNAME%", "unknown owner");
        } else {
          res = str.warpTopSubPrivate.replace("%OWNERNAME%", "unknown owner");
        }
      }
      res = res.replace("%POSITION%", String.valueOf(i + 1 + ((page - 1) * WarpHandler.WARPLIST_PAGE_SIZE)))
          .replace("%WARPNAME%", currentWarp.getName())
          .replace("%WARPAMOUNT%", String.valueOf(currentWarp.getTimesWarpedTo()));
      result.append('\n').append(res);
    }
    return result.toString();
  }
}
