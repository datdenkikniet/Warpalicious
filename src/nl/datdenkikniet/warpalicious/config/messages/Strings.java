package nl.datdenkikniet.warpalicious.config.messages;

import nl.datdenkikniet.warpalicious.WarpaliciousPlugin;
import nl.datdenkikniet.warpalicious.config.Config;
import nl.datdenkikniet.warpalicious.config.CustomConfig;
import org.bukkit.command.Command;
import org.bukkit.permissions.Permissible;

public class Strings
{

    private boolean isInit = false;
    private CustomConfig configHandler;
    private Config config;
    private String permission = "warpalicious.";
    private MessagesGetter messages;

    public Strings(CustomConfig cu, Config cfg, WarpaliciousPlugin plugin)
    {
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
    public String warpInfoPerm = permission + "warpinfo";
    public String warpTopPerm = permission + "top";
    public String searchWarpPerm = permission + "searchwarps";
    public String setPrivateWarpPerm = permission + "set.private";
    public String createWarpSignPerm = permission + "makewarpsign";
    public String useWarpSignPerm = permission + "usewarpsign";
    public String nodelayperm = permission + "nodelay";
    public String noParticlePerm = permission + "noparticle";

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
    public String noValidNumber;
    public String noValidPage;
    public String warpTopHeader;
    public String warpTopSub;
    public String warpTopSubPrivate;
    public String noDots;
    public String warpSearchHeader;
    public String noWarpsFoundForQuery;
    public String madeWarpPrivate;
    public String madeWarpPublic;
    public String privateWarpSet;
    private String correctUsage;
    public String noSignPerm;
    public String createdWarpSign;
    public String warpSignHeader;
    public String noMove;
    public String tpInTime;
    public String youMoved;

    /*
    String functions
     */
    private String r(String str)
    {
        return str.replace("%PREFIX%", prefix);
    }

    public String getUsage(Command command, String alias)
    {
        return r(correctUsage.replace("%USAGE%", command.getUsage().replace("<command>", alias)));
    }

    public void loadMessages()
    {
        if (isInit)
        {
            configHandler.reloadCustomConfig(config);
        }
        try
        {
            prefix = messages.getMessage("prefix");
        }
        catch (Exception ex)
        {
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
        noValidNumber = s("no-valid-number");
        noValidPage = s("no-valid-page");
        warpTopHeader = s("warptop-header");
        warpTopSub = s("warptop-sub");
        warpTopSubPrivate = s("warptop-sub-private");
        noDots = s("no-dots-in-names");
        warpSearchHeader = s("warp-search-header");
        noWarpsFoundForQuery = s("no-warps-found-with-name");
        madeWarpPrivate = s("made-warp-private");
        madeWarpPublic = s("made-warp-public");
        privateWarpSet = s("set-private-warp");
        correctUsage = s("correct-usage");
        noSignPerm = s("no-sign-permission");
        createdWarpSign = s("created-warp-sign");
        warpSignHeader = s("warpsign-header");
        noMove = s("dont-move");
        youMoved = s("you-moved");
        tpInTime = s("teleport-in-time");
    }

    private String s(String s)
    {
        try
        {
            return r(messages.getMessage(s));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "";
    }

    public boolean checkPermission(Permissible p, String permission)
    {
        return p.hasPermission(permission) || p.hasPermission(universalPerm);
    }
}
