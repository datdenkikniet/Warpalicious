package nl.datdenkikniet.warpalicious.commands;

import nl.datdenkikniet.warpalicious.WarpaliciousPlugin;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FindWarpCommand implements CommandExecutor {
    private Strings str;
    private WarpHandler handler;

    public FindWarpCommand(WarpaliciousPlugin instance){
        str = instance.getStrings();
        handler = instance.getWarpHandler();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            if (str.checkPermission(sender, str.searchWarpPerm)) {
                sender.sendMessage(handler.formatWarps(args[0], 1));
                return true;
            } else {
                sender.sendMessage(str.noperm);
                return true;
            }
        } else if (args.length == 2) {
            if (str.checkPermission(sender, str.searchWarpPerm)) {
                try {
                    sender.sendMessage(handler.formatWarps(args[0], Integer.parseInt(args[1])));
                } catch (NumberFormatException ex) {
                    sender.sendMessage(str.noValidNumber);
                }
                return true;
            } else {
                sender.sendMessage(str.noperm);
                return true;
            }
        } else {
            sender.sendMessage(str.getUsage(cmd, label));
            return true;
        }
    }
}

