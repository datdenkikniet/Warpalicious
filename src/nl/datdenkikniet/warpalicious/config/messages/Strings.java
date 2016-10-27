package nl.datdenkikniet.warpalicious.config.messages;

import nl.datdenkikniet.warpalicious.MyWarpsPlugin;
import nl.datdenkikniet.warpalicious.config.Config;
import nl.datdenkikniet.warpalicious.config.CustomConfig;
import org.bukkit.command.Command;

/**
 * Created by Jona on 21/10/2016.
 */
public class Strings {

    private boolean isInit = false;
    private CustomConfig configHandler;
    private Config config;
    private String permission = "warpalicious.";
    private MessagesGetter messages;

    public Strings(CustomConfig cu, Config cfg, MyWarpsPlugin plugin) {
        configHandler = cu;
        config = cfg;
        messages = new MessagesGetter(configHandler, config, plugin);
        loadMessages();
        isInit = true;
    }


    /*
    Permissions
     */
    public String universalPerm = permission + "*";
    public String warpDelPerm = permission + "delete";
    public String warpEditPerm = permission + "edit";
    public String setWarpPerm = permission + "set";
    public String warpToPrivatePerm = permission + "bypassprivate";
    public String warpPerm = permission + "warp";
    public String warpListPerm = permission + "list";
    public String warpListPrivatePerm = permission + "list.private";
    public String warpListOthersPerm = permission + "list.other";
    public String warpListSelfPerm = permission + "list.self";
    public String delOtherWarpPerm = permission + "delete.other";
    public String warpInfoOthersPerm = permission + "warpinfo.other";
    public String warpInfoPerm = permission + ".warpinfo";

    /*
    Messages
     */
    public String prefix;
    public String noperm;
    public String warpNotExists;
    public String warpNotOwned;
    public String warpDeleted;
    public String warpSetFlag;
    public String warpNotFlag;
    public String warpCantSetThatMany;
    public String warpSet;
    public String warpAlreadyExists;
    public String warpIsPrivate;
    public String warpToWarp;
    public String warpListHelp;
    public String warpList;
    public String warpPageNotExists;
    public String warpListSub;
    public String warpListSubPrivate;
    public String warpsOwnList;
    public String warpOthersList;
    public String warpInfoMain;
    public String warpInfoLocation;
    public String warpInfoAmount;
    public String warpInfoBy;
    public String warpInfoTotalMain;
    public String warpInfoTotalAmount;
    public String warpInfoTotalWarped;

    /*
    String functions
     */
    public String r(String str) {
        return str.replace("%PREFIX%", prefix);
    }

    public String getUsage(Command command, String alias) {
        return prefix + " Wrong usage! Correct usage: " + command.getUsage().replace("<command>", alias);
    }

    public void loadMessages() {
        if (isInit) {
            configHandler.reloadCustomConfig(config);
        }
        try {
            prefix = messages.getMessage("prefix");
        } catch (Exception ex){
            ex.printStackTrace();
        }
        noperm = s("no-permission");
        warpNotExists = s("warp-doesnt-exist");
        warpNotOwned = s("not-owner-of-warp");
        warpDeleted = s("warp-was-deleted");
        warpSetFlag = s("warp-set-flag");
        warpNotFlag = s("string-not-valid-flag");
        warpCantSetThatMany = s("warp-limit-reached");
        warpSet = s("warp-set");
        warpAlreadyExists = s("warp-already-exists");
        warpIsPrivate = s("warp-is-private");
        warpToWarp = s("warp-to-warp");
        warpListHelp = s("warp-list-help");
        warpList = s("warp-list");
        warpPageNotExists = s("warp-page-not-exists");
        warpListSub = s("warp-list-sub");
        warpListSubPrivate = s("warp-list-sub-private");
        warpsOwnList = s("warp-list-self");
        warpOthersList = s("warp-list-other");
        warpInfoMain = s("warp-info-header");
        warpInfoLocation = s("warp-info-location");
        warpInfoAmount = s("warp-info-amount");
        warpInfoBy = s("warp-info-owner");
        warpInfoTotalMain = s("warp-info-total-header");
        warpInfoTotalAmount = s("warp-info-total-amount");
        warpInfoTotalWarped = s("warp-info-total-warped");
    }
    public String s(String s){
        try {
            return r(messages.getMessage(s));
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }
}
