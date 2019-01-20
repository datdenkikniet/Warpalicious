package nl.datdenkikniet.warpalicious.commands;

import nl.datdenkikniet.warpalicious.PermissionStorage;
import nl.datdenkikniet.warpalicious.WarpaliciousPlugin;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.Warp;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpInviteCommand implements CommandExecutor {
    private WarpHandler handler;
    private Strings str;

    public WarpInviteCommand(WarpHandler handler, Strings str) {
        this.str = str;
        this.handler = handler;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Player only");
            return true;
        }
        Player player = (Player) sender;
        if (!PermissionStorage.hasPermission(sender, PermissionStorage.INVITE_PLAYER)) {
            sender.sendMessage(str.noperm);
            return true;
        }
        if (args.length == 2) {
            Warp warp = handler.getWarp(args[0]);
            Bukkit.getScheduler().runTaskAsynchronously(WarpaliciousPlugin.getInstance(), () -> {
                @SuppressWarnings("deprecation") OfflinePlayer otherPlayer = Bukkit.getOfflinePlayer(args[1]);
                if (warp == null) {
                    sender.sendMessage(str.warpNotExists);
                    return;
                }
                if (!otherPlayer.hasPlayedBefore()) {
                    sender.sendMessage(str.neverPlayed);
                    return;
                }
                if (cmd.getName().equalsIgnoreCase("warpinvite")) {
                    if (warp.getOwner().equals(player.getUniqueId())) {
                        if (warp.isPrivate()) {
                            if (!warp.isInvited(otherPlayer.getUniqueId())) {
                                warp.addInvitedPlayer(otherPlayer.getUniqueId());
                                player.sendMessage(str.addedInvitedPlayer.replace("%PLAYERNAME%", otherPlayer.getName()).replace("%WARPNAME%", warp.getName()));
                                if (Bukkit.getPlayer(otherPlayer.getUniqueId()) != null) {
                                    Bukkit.getPlayer(otherPlayer.getUniqueId()).sendMessage(str.warpInvited.replace("%PLAYERNAME%", player.getName()).replace("%WARPNAME%", warp.getName()));
                                }
                            } else {
                                player.sendMessage(str.alreadyInvited);
                            }
                        } else {
                            player.sendMessage(str.notPrivate);
                        }
                    } else {
                        player.sendMessage(str.warpNotOwned);
                    }
                } else if (cmd.getName().equalsIgnoreCase("warpuninvite")) {
                    if (warp.getOwner().equals(player.getUniqueId())) {
                        if (warp.isPrivate()) {
                            if (warp.isInvited(otherPlayer.getUniqueId())) {
                                warp.removeInvitedPlayer(otherPlayer.getUniqueId());
                                player.sendMessage(str.removedInvitedPlayers.replace("%PLAYERNAME%", otherPlayer.getName()).replace("%WARPNAME%", warp.getName()));
                                if (Bukkit.getPlayer(otherPlayer.getUniqueId()) != null) {
                                    Bukkit.getPlayer(otherPlayer.getUniqueId()).sendMessage(str.warpUninvited.replace("%PLAYERNAME%", player.getName()).replace("%WARPNAME%", warp.getName()));
                                }
                            } else {
                                player.sendMessage(str.notInvited);
                            }
                        } else {
                            player.sendMessage(str.notPrivate);
                        }
                    } else {
                        player.sendMessage(str.warpNotOwned);
                    }
                }
            });
            return true;
        } else {
            sender.sendMessage(str.getUsage(cmd, label));
            return true;
        }
    }
}
