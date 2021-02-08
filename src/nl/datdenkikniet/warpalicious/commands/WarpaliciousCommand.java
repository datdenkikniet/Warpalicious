package nl.datdenkikniet.warpalicious.commands;

import java.util.Iterator;
import nl.datdenkikniet.warpalicious.WarpaliciousPlugin;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.Warp;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WarpaliciousCommand implements CommandExecutor {

  private WarpaliciousPlugin plugin;
  private Strings str;

  public WarpaliciousCommand(WarpaliciousPlugin pl, Strings instance) {
    str = instance;
    plugin = pl;
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (args.length == 1) {
      if (args[0].equalsIgnoreCase("reloadmessages") && str
          .checkPermission(sender, str.universalPerm)) {
        plugin.getStrings().loadMessages();
        sender.sendMessage(str.prefix + " Succesfully reloaded messages!");
      } else if (args[0].equalsIgnoreCase("prune-invalid")) {
        WarpHandler handler = plugin.getWarpHandler();
        int removed = 0;
        for (Warp next : handler.getWarps()) {
          if (next.getLocation(false).getWorld() == null) {
            handler.delWarp(next);
            removed++;
          }
        }
        sender.sendMessage("Successfully pruned " + removed + " warps with invalid/inaccessible worlds.");
      } else {
        sender.sendMessage(str.getUsage(cmd, label));
      }
      return true;
    } else {
      sender.sendMessage(
          str.prefix + " This server is running Warpalicious version " + plugin.getDescription()
              .getVersion() + " by datdenkikniet.");
      return true;
    }
  }

}
