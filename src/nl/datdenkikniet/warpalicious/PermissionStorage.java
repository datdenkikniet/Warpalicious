package nl.datdenkikniet.warpalicious;

import nl.datdenkikniet.warpalicious.handling.Flag;
import nl.datdenkikniet.warpalicious.handling.TeleportMode;
import nl.datdenkikniet.warpalicious.handling.Warp;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PermissionStorage {

    private static final String PERMISSION_PREFIX = "warpalicious.";


    /*
    PermissionStorage
     */
    public static final String UNIVERSAL = PERMISSION_PREFIX + "*";
    public static final String DELETE = PERMISSION_PREFIX + "delete";
    public static final String EDIT = PERMISSION_PREFIX + "edit";
    public static final String SET = PERMISSION_PREFIX + "set";
    public static final String TP_TO_PRIVATE = PERMISSION_PREFIX + "bypassprivate";
    public static final String TP = PERMISSION_PREFIX + "warp";
    public static final String LIST = PERMISSION_PREFIX + "list";
    public static final String LIST_PRIVATE = PERMISSION_PREFIX + "list.private";
    public static final String LIST_OTHERS = PERMISSION_PREFIX + "list.other";
    public static final String LIST_SELF = PERMISSION_PREFIX + "list.self";
    public static final String DELETE_OTHERS = PERMISSION_PREFIX + "delete.other";
    public static final String INFO_OTHERS = PERMISSION_PREFIX + "warpinfo.other";
    public static final String INFO = PERMISSION_PREFIX + "warpinfo";
    public static final String TOP = PERMISSION_PREFIX + "top";
    public static final String LIST_SEARCH = PERMISSION_PREFIX + "searchwarps";
    public static final String SET_PRIVATE = PERMISSION_PREFIX + "set.private";
    public static final String SIGN_CREATE = PERMISSION_PREFIX + "makewarpsign";
    public static final String SIGN_USE = PERMISSION_PREFIX + "usewarpsign";
    public static final String NO_DELAY = PERMISSION_PREFIX + "nodelay";
    public static final String NO_PARTICLE = PERMISSION_PREFIX + "noparticle";
    public static final String INVITE_PLAYER = PERMISSION_PREFIX + "inviteplayers";
    public static final String SET_PRIVATE_ONLY = PERMISSION_PREFIX + "onlyprivate";

    public static boolean hasPermission(Permissible p, String permission) {
        return hasPermission(p, true, permission);
    }

    public static boolean hasPermission(Permissible p, boolean allowOp, String permission) {
        if (allowOp && p.isOp()) {
            return true;
        }

        boolean hasNegator = false;
        boolean hasPerm = false;

        for (PermissionAttachmentInfo at : p.getEffectivePermissions()) {
            if (at.getPermission().equalsIgnoreCase(permission)) {
                hasPerm = true;
            }
            if (at.getPermission().equalsIgnoreCase(permission) && !at.getValue()) {
                hasNegator = true;
            }
        }
        return (hasPerm && !hasNegator) || p.hasPermission(UNIVERSAL);
    }

    public static boolean hasPermissions(Permissible p, boolean allowOp, String... permissions) {
        if (allowOp && p.isOp()) {
            return true;
        }
        int negatorCount = 0;
        int permCount = 0;
        for (PermissionAttachmentInfo at : p.getEffectivePermissions()) {
            for (String permission : permissions) {
                if (at.getPermission().equalsIgnoreCase(permission)) {
                    permCount++;
                }
                if (at.getPermission().equalsIgnoreCase(permission) && !at.getValue()) {
                    negatorCount++;
                }
            }
        }
        return permCount > negatorCount || p.hasPermission(UNIVERSAL);
    }


    public static boolean allowedToWarp(Warp warp, Player player, TeleportMode mode) {
        if (mode == TeleportMode.COMMAND) {
            return !warp.isPrivate() || hasPermission(player, TP_TO_PRIVATE) || warp.getOwner().equals(player.getUniqueId()) || warp.isInvited(player.getUniqueId());
        } else {
            return !warp.getFlag(Flag.SIGNPRIVATE) || hasPermission(player, TP_TO_PRIVATE) || warp.getOwner().equals(player.getUniqueId()) || warp.isInvited(player.getUniqueId());
        }
    }

}
