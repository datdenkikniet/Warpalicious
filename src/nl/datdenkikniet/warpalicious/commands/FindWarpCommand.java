package nl.datdenkikniet.warpalicious.commands;

import nl.datdenkikniet.warpalicious.PermissionStorage;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.MessageGenerator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FindWarpCommand implements CommandExecutor {

    private Strings str;
    private MessageGenerator messageGenerator;

    public FindWarpCommand(Strings str, MessageGenerator messageGenerator) {
        this.str = str;
        this.messageGenerator = messageGenerator;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            if (PermissionStorage.hasPermission(sender, PermissionStorage.LIST_SEARCH)) {
                sender.sendMessage(messageGenerator.formatWarps(args[0], 1, player.getUniqueId()));
                return true;
            } else {
                sender.sendMessage(str.noperm);
                return true;
            }
        } else if (args.length == 2) {
            if (PermissionStorage.hasPermission(sender, PermissionStorage.LIST_SEARCH)) {
                try {
                    sender.sendMessage(messageGenerator.formatWarps(args[0], Integer.parseInt(args[1]), player.getUniqueId()));
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

