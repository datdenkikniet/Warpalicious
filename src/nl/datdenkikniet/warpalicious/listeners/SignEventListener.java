package nl.datdenkikniet.warpalicious.listeners;

import net.md_5.bungee.api.ChatColor;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Jona on 30/04/2017.
 */
public class SignEventListener implements Listener {

    private Strings str;
    private WarpHandler handler;
    public SignEventListener(Strings instance, WarpHandler hd){
        str = instance;
        handler = hd;
    }

    @EventHandler
    public void sign(SignChangeEvent evt){
        String l0 = evt.getLine(0);
        String l1 = evt.getLine(1);
        Player p = evt.getPlayer();
        if (ChatColor.stripColor(l0).equalsIgnoreCase("[warp]")){
            if (evt.getPlayer().hasPermission(str.createWarpSignPerm)){
                if (l1 != null && handler.getWarp(l1, false) != null){
                    evt.setLine(0, str.warpSignHeader);
                    p.sendMessage(str.createdWarpSign.replace("%WARP%",  l1));
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
    public void interact(PlayerInteractEvent e){
        Block b = e.getClickedBlock();
        Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && b != null && (b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN)){
            Sign sign = (Sign) b.getState();
            if (str.checkPermission(p, str.useWarpSignPerm)){
                if (sign.getLine(0).equalsIgnoreCase(str.warpSignHeader)){
                    e.getPlayer().teleport(handler.getWarp(sign.getLine(1), true).getLocation());
                    e.getPlayer().sendMessage(str.warpToWarp.replace("%NAME%", sign.getLine(1)));
                }
            }
        }
    }

}
