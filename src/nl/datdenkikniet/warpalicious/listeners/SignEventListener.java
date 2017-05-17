package nl.datdenkikniet.warpalicious.listeners;

import net.md_5.bungee.api.ChatColor;
import nl.datdenkikniet.warpalicious.WarpaliciousPlugin;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.Flag;
import nl.datdenkikniet.warpalicious.handling.TeleportMode;
import nl.datdenkikniet.warpalicious.handling.Warp;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignEventListener implements Listener {

    private WarpaliciousPlugin plugin;
    private Strings str;
    private WarpHandler handler;

    public SignEventListener(WarpaliciousPlugin plugin) {
        this.plugin = plugin;
        str = plugin.getStrings();
        handler = plugin.getWarpHandler();
    }

    @EventHandler
    public void sign(SignChangeEvent evt) {
        String l0 = evt.getLine(0);
        String l1 = evt.getLine(1);
        Player p = evt.getPlayer();
        if (ChatColor.stripColor(l0).equalsIgnoreCase("[warp]")) {
            if (evt.getPlayer().hasPermission(str.createWarpSignPerm)) {
                if (l1 != null && handler.getWarp(l1) != null) {
                    evt.setLine(0, str.warpSignHeader);
                    plugin.getLogger().info(evt.getPlayer() + " created a signwarp with the warp: " + l1);
                    Location loc = evt.getBlock().getLocation();
                    plugin.getLogger().info("It is located at X: " + loc.getBlockX() + ", Y: " + loc.getBlockY()
                            + " and Z: " + loc.getBlockZ() + " in the world: " + loc.getWorld());
                    p.sendMessage(str.createdWarpSign.replace("%WARP%", l1));
                } else {
                    evt.setLine(0, "[warp]");
                    p.sendMessage(str.warpNotExists);
                }
            } else {
                p.sendMessage(str.noSignPerm);
            }
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent e) {
        Block b = e.getClickedBlock();
        Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && b != null && (b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN)) {
            Sign sign = (Sign) b.getState();
            Warp warp = handler.getWarp(sign.getLine(1));
            if (warp != null) {
                if (str.checkPermission(p, str.useWarpSignPerm)) {
                    boolean signPrivate = warp.getFlag(Flag.SIGNPRIVATE) && !str.checkPermission(e.getPlayer(), str.warpToPrivatePerm) && !warp.getOwner().equals(e.getPlayer().getUniqueId());
                    if (!signPrivate) {
                        if (sign.getLine(0).equalsIgnoreCase(str.warpSignHeader)) {
                            plugin.getLogger().info(e.getPlayer().getName() + " used a signwarp with the warp: " + warp.getName());
                            warp.warp(e.getPlayer(), TeleportMode.SIGN, str);
                        }
                    } else {
                        e.getPlayer().sendMessage(str.warpIsPrivate);
                    }
                }
            }
        }
    }

}
