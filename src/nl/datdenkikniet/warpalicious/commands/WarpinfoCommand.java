package nl.datdenkikniet.warpalicious.commands;

import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.Warp;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

/**
 * Created by Jona on 23/10/2016.
 */
public class WarpinfoCommand implements CommandExecutor {
    private Strings str;
    private WarpHandler handler;

    public WarpinfoCommand(Strings strings, WarpHandler wh) {
        str = strings;
        handler = wh;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0){
            if (sender.hasPermission(str.warpInfoPerm)){
                int amount = handler.getWarps().size();
                int timesWarped = 0;
                int amountPrivate = 0;
                for (Warp warp : handler.getWarps()){
                    timesWarped += warp.getTimesWarpedTo();
                    if (warp.isPrivate()){
                        amountPrivate++;
                    }
                }
                double percentage = (Double.valueOf(amountPrivate)/Double.valueOf(amount))*100D;
                DecimalFormat numberFormat = new DecimalFormat("##.##");
                sender.sendMessage(str.warpInfoTotalMain);
                if (amountPrivate != 0) {
                    sender.sendMessage(str.warpInfoTotalAmount.replace("%AMOUNT%", String.valueOf(amount)).replace("%AMOUNTPRIVATE%", String.valueOf(amountPrivate)).replace("%PERCENTAGE%", numberFormat.format(percentage)));
                } else {
                    sender.sendMessage(str.warpInfoTotalAmount.replace("%AMOUNT%", String.valueOf(amount)).replace("%AMOUNTPRIVATE%", String.valueOf(amountPrivate)).replace("%PERCENTAGE%", String.valueOf(0)));
                }
                    sender.sendMessage(str.warpInfoTotalWarped.replace("%TIMESWARPED%", String.valueOf(timesWarped)));
                return true;
            } else {
                sender.sendMessage(str.getUsage(cmd, label));
                return true;
            }
        } else if (args.length == 1) {
            Warp warp = handler.getWarp(args[0], false);
            if (warp != null) {
                if (hasPermToViewWarp(player, warp)) {
                    sender.sendMessage(str.warpInfoMain.replace("%WARPNAME%", warp.getName()));
                    Location loc = warp.getLocation();
                    sender.sendMessage(str.warpInfoBy.replace("%PLAYERNAME%", Bukkit.getOfflinePlayer(warp.getOwner()).getName()));
                    sender.sendMessage(str.warpInfoLocation.replace("%X%", String.valueOf(Math.round(loc.getX()))).replace("%Y%", String.valueOf(Math.round(loc.getY()))).replace("%Z%", String.valueOf(Math.round(loc.getZ()))).replace("%WORLD%", loc.getWorld().getName()));
                    sender.sendMessage(str.warpInfoAmount.replace("%AMOUNT%", String.valueOf(warp.getTimesWarpedTo())));
                    return true;
                } else {
                    sender.sendMessage(str.noperm);
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
    }

    private boolean hasPermToViewWarp(Player player, Warp warp) {
        if (warp.getOwner().equals(player.getUniqueId()) && player.hasPermission(str.warpInfoPerm)) {
            return true;
        } else if (player.hasPermission(str.warpInfoOthersPerm) || player.hasPermission(str.universalPerm)) {
            return true;
        }
        return false;
    }
}
