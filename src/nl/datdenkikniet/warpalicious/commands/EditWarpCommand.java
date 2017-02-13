package nl.datdenkikniet.warpalicious.commands;

import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.Warp;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditWarpCommand implements CommandExecutor {
    private Strings str;
    private WarpHandler handler;

    public EditWarpCommand(Strings instance, WarpHandler hd) {
        str = instance;
        handler = hd;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You can't do this as non-player!");
            return true;
        }
        Player player = (Player) sender;
        if (player.hasPermission(str.warpEditPerm) || sender.hasPermission(str.universalPerm)) {
            if (args.length == 2) {
                Warp warp = handler.getWarp(args[0], false);
                if (warp != null) {
                    if (warp.getOwner().equals(player.getUniqueId()) || sender.hasPermission(str.universalPerm)) {
                        boolean value;
                        if (args[1].equalsIgnoreCase("private")){
                            value = true;
                        } else if (args[1].equalsIgnoreCase("public")) {
                            value = false;
                        } else {
                            sender.sendMessage(str.warpNotFlag.replace("%FLAGS%", handler.getFlags()));
                            return true;
                        }
                        warp.setFlag("private", value);
                        if (value) {
                            sender.sendMessage(str.madeWarpPrivate.replace("%WARPNAME%", warp.getName()));
                        } else {
                            sender.sendMessage(str.madeWarpPublic.replace("%WARPNAME%", warp.getName()));
                        }
                        return true;
                    } else {
                        sender.sendMessage(str.warpNotOwned);
                        return true;
                    }
                } else {
                    sender.sendMessage(str.warpNotExists);
                    return true;
                }
            } else if (args.length == 3) {
                if (handler.isFlag(args[1])) {
                    Warp warp = handler.getWarp(args[0], false);
                    if (warp != null) {
                        if (warp.getOwner().equals(player.getUniqueId()) || sender.hasPermission(str.universalPerm)) {
                            boolean value = handler.parseBoolean(args[2]);
                            warp.setFlag(args[1], value);
                            sender.sendMessage(str.warpSetFlag.replace("%WARPNAME%", warp.getName()).replace("%FLAG%", args[1]).replace("%VALUE%", String.valueOf(value)));
                            System.out.println(warp.isPrivate());
                            return true;
                        } else {
                            sender.sendMessage(str.warpNotOwned);
                            return true;
                        }
                    } else {
                        sender.sendMessage(str.warpNotExists);
                        return true;
                    }
                } else {
                    sender.sendMessage(str.warpNotFlag.replace("%FLAGS%", handler.getFlags()));
                    return true;
                }
            } else {
                sender.sendMessage(str.getUsage(cmd, label));
                return true;
            }
        } else {
            sender.sendMessage(str.noperm);
            return true;
        }
    }
}
