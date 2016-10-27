package nl.datdenkikniet.warpalicious.commands;


import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.Warp;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor {
	private Strings str;
	private WarpHandler handler;
	public WarpCommand(Strings instance, WarpHandler hd){
		str = instance;
		handler = hd;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (!(sender instanceof Player)){
			sender.sendMessage("You can't do this as non-player!");
			return true;
		}
		Player player = (Player) sender;
		if (sender.hasPermission(str.warpToPrivatePerm) || sender.hasPermission(str.warpPerm) || sender.hasPermission(str.universalPerm)){
			if (args.length == 1){
				Warp warp = handler.getWarp(args[0], true);
				if (warp != null){
					if (!warp.isPrivate() || sender.hasPermission(str.warpToPrivatePerm) || warp.getOwner().equals(player.getUniqueId())){
						player.sendMessage(str.warpToWarp.replace("%NAME%", warp.getName()));
						player.teleport(warp.getLocation());
						return true;
					} else {
						sender.sendMessage(str.warpIsPrivate);
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
