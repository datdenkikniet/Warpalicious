package nl.datdenkikniet.warpalicious.config.messages;

import nl.datdenkikniet.warpalicious.WarpaliciousPlugin;
import nl.datdenkikniet.warpalicious.config.CustomConfig;
import nl.datdenkikniet.warpalicious.config.CustomConfigHandler;
import org.bukkit.command.Command;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class Strings {

    private boolean isInit;
    private CustomConfigHandler configHandler;
    private CustomConfig config;
    private MessagesGetter messages;

    public Strings(CustomConfigHandler cu, CustomConfig cfg, WarpaliciousPlugin plugin) {
        configHandler = cu;
        config = cfg;
        messages = new MessagesGetter(configHandler, config, plugin);
        loadMessages();
        isInit = true;
    }

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
    public String warpInfoMain;
    public String warpInfoLocation;
    public String warpInfoAmount;
    public String warpInfoBy;
    public String warpInfoTotalMain;
    public String warpInfoTotalAmount;
    public String warpInfoTotalWarped;
    public String noValidNumber;
    public String noDots;
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
    public String neverPlayed;
    public String notPrivate;
    public String removedInvitedPlayers;
    public String addedInvitedPlayer;
    public String notInvited;
    public String alreadyInvited;
    public String warpInvited;
    public String warpUninvited;
    public String invalidWorld;
    public String warpInvitedList;

    /*
    Warp list strings
     */
    public String warpList;
    public String warpPageNotExists;
    public String warpListSub;
    public String warpListSubPrivate;
    public String warpListSubInvited;
    public String warpsOwnList;
    public String warpOthersList;

    public String noValidPage;
    public String warpTopHeader;
    public String warpTopSub;
    public String warpTopSubPrivate;

    public String warpSearchHeader;
    public String noWarpsFoundForQuery;

    /*
    String functions
     */
    private String r(String str) {
        return str.replace("%PREFIX%", prefix);
    }

    private String s(String m) {
        try {
            return r(messages.getMessage(m));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public String getUsage(Command command, String alias) {
        return r(correctUsage.replace("%USAGE%", command.getUsage().replace("<command>", alias)));
    }

    public void loadMessages() {
        if (isInit) {
            configHandler.reloadCustomConfig(config);
        }
        try {
            prefix = messages.getMessage("prefix");
        } catch (Exception ex) {
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
        warpListSubInvited = s("warp-list-sub-invited");
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
        neverPlayed = s("never-played");
        removedInvitedPlayers = s("removed-invited-player");
        addedInvitedPlayer = s("added-invited-player");
        notInvited = s("player-is-not-invited");
        alreadyInvited = s("player-already-invited");
        notPrivate = s("warp-not-private");
        warpInvited = s("warp-invited");
        warpUninvited = s("warp-uninvited");
        invalidWorld = s("warp-has-invalid-world");
        warpInvitedList = s("warp-invited-list");
    }

}
