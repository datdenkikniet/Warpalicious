package nl.datdenkikniet.warpalicious.commands;

import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.Warp;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelWarpCommand implements CommandExecutor {
	private Strings str;
	private WarpHandler handler;
	public DelWarpCommand(Strings instance, WarpHandler hd){
		str = instance;
		handler = hd;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (!(sender instanceof Player)){
			sender.sendMessage("You can't do this as non-player!");
			return true;
		}
		Player player = (Player) sender;
		if (str.checkPermission(sender, str.warpDelPerm)){
			if (args.length == 1){
				Warp warp = handler.getWarp(args[0], false);
				if (warp != null){
					if (warp.getOwner().equals(player.getUniqueId()) || str.checkPermission(sender, str.delOtherWarpPerm)){
						handler.delWarp(warp);
						sender.sendMessage(str.warpDeleted.replace("%WARPNAME%", warp.getName()));
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
				sender.sendMessage(str.getUsage(cmd, label));
				return true;
			}
		} else {
			sender.sendMessage(str.noperm);
			return true;
		}
	}
}
