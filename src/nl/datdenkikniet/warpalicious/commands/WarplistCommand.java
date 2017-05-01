package nl.datdenkikniet.warpalicious.commands;

import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.apache.commons.lang3.StringUtils;
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
        if (str.checkPermission(sen, str.warpListPerm) || str.checkPermission(sen, str.warpListPrivatePerm) || str.checkPermission(sen, str.warpListOthersPerm) || str.checkPermission(sen, str.warpListSelfPerm) || str.checkPermission(sen, str.universalPerm)) {
            if (args.length == 0) {
                sen.sendMessage(handler.getWarpListPage(player, 1));
                return true;
            } else if (args.length == 1) {
                if (StringUtils.isNumeric(args[0])) {
                    sen.sendMessage(handler.getWarpListPage(player, Integer.valueOf(args[0])));
                    return true;
                } else if (args[0].equalsIgnoreCase("-help")) {
                    sen.sendMessage(str.getUsage(cmd, label));
                    return true;
                } else {
                    if (args[0].equalsIgnoreCase("self")) {
                        if (str.checkPermission(sen, str.warpListSelfPerm)) {
                            sen.sendMessage(handler.getWarpListPageSelf(player, 1));
                            return true;
                        } else {
                            sen.sendMessage(str.noperm);
                            return true;
                        }
                    } else if (Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore()) {
                        if (str.checkPermission(sen, str.warpListOthersPerm)) {
                            OfflinePlayer pl = Bukkit.getOfflinePlayer(args[0]);
                            sen.sendMessage(handler.getWarpListPageOther(player, pl, 1));
                            return true;
                        } else {
                            sen.sendMessage(str.noperm);
                            return true;
                        }
                    } else {
                        sen.sendMessage(str.warpListHelp.replace("%PAGES%", handler.getWarpListPages(player)));
                        return true;
                    }
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("self") && StringUtils.isNumeric(args[1])) {
                    if (str.checkPermission(sen, str.warpListSelfPerm)) {
                        sen.sendMessage(handler.getWarpListPageSelf(player, Integer.valueOf(args[1])));
                        return true;
                    } else {
                        sen.sendMessage(str.noperm);
                        return true;
                    }
                } else if (Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore() && StringUtils.isNumeric(args[1])) {
                    if (str.checkPermission(sen, str.warpListOthersPerm)) {
                        OfflinePlayer pl = Bukkit.getOfflinePlayer(args[0]);
                        sen.sendMessage(handler.getWarpListPageOther(player, pl, Integer.valueOf(args[1])));
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
}
