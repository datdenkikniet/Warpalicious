package nl.datdenkikniet.warpalicious.commands;

import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.Warp;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.regex.Pattern;

public class SetWarpCommand implements CommandExecutor {
	private Strings str;
	private WarpHandler handler;
	public SetWarpCommand(Strings instance, WarpHandler hd){
		str = instance;
		handler = hd;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (!(sender instanceof Player)){
			sender.sendMessage("You can't do this as non-player!");
			return true;
		}
		Player player = (Player) sender;
		if (args.length == 1){
			int amt = handler.getWarps(player.getUniqueId()).size();
			boolean hasPerm = false;
			boolean hasEnoughWarps = false;
			int amount = 0;
			for (PermissionAttachmentInfo pai : player.getEffectivePermissions()){
				if (pai.getPermission().startsWith(str.setWarpPerm)){
					if (pai.getPermission().split(Pattern.quote("")).length == 3){
						System.out.println(pai.getPermission());
						hasPerm = true;
						amount = Integer.valueOf(pai.getPermission().split(Pattern.quote(""))[3]);
						if (amount > amt){
							hasEnoughWarps = true;
						}
					}
				}
			}
			if (player.hasPermission(str.setWarpPerm) || (hasPerm && hasEnoughWarps)){
				Warp warp = handler.getWarp(args[0], false);
				if (warp == null){
					new Warp(player.getUniqueId(), player.getLocation(), args[0], handler.getDefaultFlags(), handler, 0);
					handler.saveWarps();
					sender.sendMessage(str.warpSet.replace("%NAME%", args[0]));
					return true;
				}  else {
					sender.sendMessage(str.warpAlreadyExists);
					return true;
				}
			} else {
				if (hasPerm && !hasEnoughWarps){
					sender.sendMessage(str.warpCantSetThatMany.replace("%AMOUNT%", String.valueOf(amount)));
				} else {
					sender.sendMessage(str.noperm);
				}
				return true;
			}
		} else {
			sender.sendMessage(str.getUsage(cmd, label));
		}
		return true;
	}
}
