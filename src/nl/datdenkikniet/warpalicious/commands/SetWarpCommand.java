package nl.datdenkikniet.warpalicious.commands;

import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.Flag;
import nl.datdenkikniet.warpalicious.handling.Warp;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class SetWarpCommand implements CommandExecutor
{

    private Strings str;
    private WarpHandler handler;

    public SetWarpCommand(Strings instance, WarpHandler hd)
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
        if (args.length > 0)
        {
            int amt = handler.getWarps(player.getUniqueId()).size();
            boolean hasPerm = false;
            boolean hasEnoughWarps = sender.hasPermission(str.universalPerm);
            int amount = 0;
            for (PermissionAttachmentInfo pai : player.getEffectivePermissions())
            {
                if (pai.getPermission().startsWith(str.setWarpPerm) && pai.getPermission().split(Pattern.quote(".")).length == 3 && StringUtils.isNumeric(pai.getPermission().split(Pattern.quote("."))[2]))
                {
                    hasPerm = true;
                    amount = Integer.valueOf(pai.getPermission().split(Pattern.quote("."))[2]);
                    if (amount > amt)
                    {
                        hasEnoughWarps = true;
                    }
                }
            }
            if (str.checkPermission(sender, str.setWarpPerm) || (hasPerm && hasEnoughWarps))
            {
                if (args[0].contains("."))
                {
                    sender.sendMessage(str.noDots);
                    return true;
                }
                if (handler.getWarp(args[0]) == null)
                {
                    Warp warp = new Warp(handler.getPlugin(), player.getUniqueId(), player.getLocation(), args[0], handler.getDefaultFlags(), handler, 0, new ArrayList<>(), player.getLocation().getWorld().getName());
                    handler.saveWarps();
                    if ((args.length > 1 && args[1].equalsIgnoreCase("private") && str.checkPermission(sender, str.setPrivateWarpPerm)) || str.checkPermission(sender, str.onlySetPrivate))
                    {
                        System.out.println(sender.isPermissionSet(str.onlySetPrivate));
                        warp.setFlag(Flag.PRIVATE, true);
                        sender.sendMessage(str.privateWarpSet.replace("%NAME%", args[0]));
                        return true;
                    }
                    sender.sendMessage(str.warpSet.replace("%NAME%", args[0]));
                    return true;
                }
                else
                {
                    sender.sendMessage(str.warpAlreadyExists);
                    return true;
                }
            }
            else
            {
                if (hasPerm)
                {
                    sender.sendMessage(str.warpCantSetThatMany.replace("%AMOUNT%", String.valueOf(amount)));
                }
                else
                {
                    sender.sendMessage(str.noperm);
                }
                return true;
            }
        }
        else
        {
            sender.sendMessage(str.getUsage(cmd, label));
        }
        return true;
    }
}
