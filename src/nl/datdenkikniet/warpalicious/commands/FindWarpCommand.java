package nl.datdenkikniet.warpalicious.commands;

import java.util.List;
import nl.datdenkikniet.warpalicious.WarpaliciousPlugin;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.config.messages.StringUtils;
import nl.datdenkikniet.warpalicious.handling.Warp;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FindWarpCommand implements CommandExecutor {

  private Strings str;
  private WarpHandler handler;

  public FindWarpCommand(WarpaliciousPlugin instance) {
    str = instance.getStrings();
    handler = instance.getWarpHandler();
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      return true;
    }
    Player player = (Player) sender;
    if (args.length == 1) {
      if (str.checkPermission(sender, str.searchWarpPerm)) {
        sender.sendMessage(searchWarpsPage(args[0], 1, player));
      } else {
        sender.sendMessage(str.noperm);
      }
      return true;
    } else if (args.length == 2) {
      if (str.checkPermission(sender, str.searchWarpPerm)) {
        try {
          sender.sendMessage(
              searchWarpsPage(args[0], Integer.parseInt(args[1]), player));
        } catch (NumberFormatException ex) {
          sender.sendMessage(str.noValidNumber);
        }
      } else {
        sender.sendMessage(str.noperm);
      }
      return true;
    } else {
      sender.sendMessage(str.getUsage(cmd, label));
      return true;
    }
  }

  private String searchWarpsPage(String toFind, int page, Player player) {
    List<Warp> foundWarps = handler.searchWarpsPage(toFind, page, player);
    if (foundWarps != null && foundWarps.size() == 0) {
      return str.noWarpsFoundForQuery.replace("%QUERY%", toFind);
    } else {
      String title = str.warpSearchHeader;
      return StringUtils.toWarpListPageString(str, player, title, page,
          handler.searchWarpsPagesCount(toFind, player), foundWarps);
    }
  }
}

