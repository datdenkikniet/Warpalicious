package nl.datdenkikniet.warpalicious.commands;

import nl.datdenkikniet.warpalicious.WarpaliciousPlugin;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Jona on 21/10/2016.
 */
public class WarpaliciousCommand implements CommandExecutor {
    private WarpaliciousPlugin plugin;
    private Strings str;
    public WarpaliciousCommand(WarpaliciousPlugin pl, Strings instance){
        str = instance;
        plugin = pl;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (args.length == 1 && args[0].equalsIgnoreCase("reloadmessages") && str.checkPermission(sender, str.universalPerm)) {
            plugin.getStrings().loadMessages();
            sender.sendMessage(str.prefix + " Succesfully reloaded messages!");
        } else {
            sender.sendMessage(str.prefix + " This server is running Warpalicious version " + plugin.getDescription().getVersion() + " by datdenkikniet.");
        }
        return true;
    }
}
