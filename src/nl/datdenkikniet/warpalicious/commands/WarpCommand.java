package nl.datdenkikniet.warpalicious.commands;

import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.TeleportMode;
import nl.datdenkikniet.warpalicious.handling.Warp;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor
{

    private Strings str;
    private WarpHandler handler;

    public WarpCommand(Strings instance, WarpHandler hd)
    {
        str = instance;
        handler = hd;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage("You can't do this as non-player!");
            return true;
        }
        Player player = (Player) sender;
        if (str.checkPermission(sender, str.warpToPrivatePerm) || str.checkPermission(sender, str.warpPerm))
        {
            if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("-help")))
            {
                sender.sendMessage(str.prefix + " Help menu\n" +
                        "/warp <name> to warp to a warp\n" +
                        "/setwarp <warp> [private] to create a (private) warp\n" +
                        "/delwarp <warp> to delete a warp\n" +
                        "/editwarp <warp> <public|private> to make a warp public or private\n" +
                        "/findwarp <warp> to search for a warp by name\n" +
                        "/warplist <page|self|username> [page] for your own/someone else's/the warp list\n" +
                        "/warpinfo <warp> to see info about a warp");
                return true;
            }
            else if (args.length == 1)
            {
                Warp warp = handler.getWarp(args[0]);
                if (warp != null)
                {
                    if (handler.allowedToWarp(warp, player, TeleportMode.COMMAND))
                    {
                        warp.warp(player, TeleportMode.COMMAND, str);
                        return true;
                    }
                    else
                    {
                        sender.sendMessage(str.warpIsPrivate);
                        return true;
                    }
                }
                else
                {
                    sender.sendMessage(str.warpNotExists);
                    return true;
                }
            }
            else
            {
                sender.sendMessage(str.getUsage(cmd, label));
                return true;
            }
        }
        else
        {
            sender.sendMessage(str.noperm);
            return true;
        }
    }
}
