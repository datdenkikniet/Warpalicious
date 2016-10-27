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
	public WarplistCommand(Strings instance, WarpHandler hd){
		str = instance;
		handler = hd;
	}
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (!(sender instanceof Player)){
			sender.sendMessage("You can't do this as non-player!");
			return true;
		}
		Player player = (Player) sender;
		if (player.hasPermission(str.warpListPerm) || sender.hasPermission(str.warpListPrivatePerm) || sender.hasPermission(str.warpListOthersPerm) || sender.hasPermission(str.warpListSelfPerm)|| player.hasPermission(str.universalPerm)){
			if (args.length == 0){
				sender.sendMessage(str.warpListHelp.replace("%PAGES%", handler.getWarpListPages(player)));
				return true;
			} else if (args.length == 1){
				if (StringUtils.isNumeric(args[0])){
					sender.sendMessage(handler.getWarpListPage(player, Integer.valueOf(args[0])));
					return true;
				} else {
					if (args[0].equalsIgnoreCase("self")){
						if (sender.hasPermission(str.warpListSelfPerm)){
							sender.sendMessage(handler.getWarpListPageSelf(player, 1));
							return true;
						} else {
							sender.sendMessage(str.noperm);
							return true;
						}
					} else if (Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore()){
						if (sender.hasPermission(str.warpListOthersPerm)){
							OfflinePlayer pl = Bukkit.getOfflinePlayer(args[0]);
							sender.sendMessage(handler.getWarpListPageOther(player, pl, 1));
							return true;
						} else {
							sender.sendMessage(str.noperm);
							return true;
						}
					} else {
						sender.sendMessage(str.warpListHelp.replace("%PAGES%", handler.getWarpListPages(player)));
						return true;
					}
				}
			} else if (args.length == 2){
				if (args[0].equalsIgnoreCase("self") && StringUtils.isNumeric(args[1])){
					if (sender.hasPermission(str.warpListSelfPerm)){
						sender.sendMessage(handler.getWarpListPageSelf(player, Integer.valueOf(args[1])));
						return true;
					} else {
						sender.sendMessage(str.noperm);
						return true;
					}
				} else if (Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore() && StringUtils.isNumeric(args[1])){
					if (sender.hasPermission(str.warpListOthersPerm)){
						OfflinePlayer pl = Bukkit.getOfflinePlayer(args[0]);
						sender.sendMessage(handler.getWarpListPageOther(player, pl, Integer.valueOf(args[1])));
						return true;
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
		} else {
			sender.sendMessage(str.noperm);
			return true;
		}
	}
}
