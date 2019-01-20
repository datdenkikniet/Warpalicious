package nl.datdenkikniet.warpalicious.handling;

import nl.datdenkikniet.warpalicious.PermissionStorage;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MessageGenerator {

    private WarpHandler warpHandler;
    private Strings str;


    private final int WARPS_PER_PAGE = 9;

    public MessageGenerator(WarpHandler handler, Strings str) {
        this.warpHandler = handler;
        this.str = str;
    }

    private final BiPredicate<Player, Warp> allowedAccess = (player, warp) -> {
        if (PermissionStorage.hasPermission(player, PermissionStorage.LIST_PRIVATE)) {
            return true;
        }
        if (!warp.isPrivate()) {
            return true;
        }
        if (warp.isInvited(player.getUniqueId())) {
            return true;
        }
        return false;
    };

    private Stream<Warp> warps(Predicate<Warp> predicate) {
        return warpHandler.filterWarps(predicate);
    }

    private int pages(double warps) {
        return (int) Math.ceil(warps / WARPS_PER_PAGE);

    }

    public String getWarpListPages(Player player) {
        return String.valueOf(pages(warps((warp) -> allowedAccess.test(player, warp)).count()));
    }

    public String getWarpList(Player player, int page, String header, List<Warp> warps) {
        int min = (page - 1) * WARPS_PER_PAGE;
        int maxPage = pages(warps.size());

        if (testPageRange(page, maxPage)) {
            warps = page(warps, page);
            StringBuilder sb = new StringBuilder(header.replace("%PAGE%", its(page)).replace("%MAXPAGE%", its(maxPage)));
            appendWarpList(warps, sb, min, player);
            return sb.toString();
        } else {
            return str.warpPageNotExists;
        }
    }

    public String getWarpList(Player player, int page, String header, Predicate<Warp> predicate) {
        List<Warp> warps = warps(predicate).collect(Collectors.toList());
        return getWarpList(player, page, header, warps);
    }

    public String getWarpListPage(Player player, int page) {
        return getWarpList(player, page, str.warpList, (warp) -> allowedAccess.test(player, warp));
    }

    public String getWarpListPageSelf(Player player, int page) {
        return getWarpList(player, page, str.warpsOwnList, warp -> warp.getOwner().equals(player.getUniqueId()));
    }

    public String getWarpListPageOther(Player player, OfflinePlayer pl, Integer page) {
        //TODO finish this method
        if (pl.hasPlayedBefore()) {
            return getWarpList(player, page, str.warpOthersList.replace("%PLAYERNAME%", pl.getName()), warp -> allowedAccess.test(player, warp) && warp.getOwner().equals(pl.getUniqueId()));
        } else {
            return getWarpList(player, page, str.warpOthersList.replace("%PLAYERNAME%", "unknown owner"), warp -> allowedAccess.test(player, warp) && warp.getOwner().equals(pl.getUniqueId()));
        }
    }

    public String sortPage(Player player, int page, boolean noPrivate) {
        Stream<Warp> warpsStream = warps(warp -> allowedAccess.test(player, warp));
        if (noPrivate) {
            warpsStream = warpsStream.filter(warp -> !warp.isPrivate());
        }
        List<Warp> warps = warpsStream.sorted(Comparator.comparingInt(Warp::getTimesWarpedTo)).collect(Collectors.toList());
        int min = (page - 1) * 9;
        int maxPage = pages(warps.size());
        if (testPageRange(page, maxPage)) {
            warps = page(warps, page);
            StringBuilder sb = new StringBuilder(str.warpTopHeader.replace("%PAGE%", its(page)).replace("%MAXPAGE%", its(maxPage)));
            for (int i = 0; i < WARPS_PER_PAGE && i < warps.size(); i++) {
                Warp warp = warps.get(i);
                String toAppend;
                if (!warp.isPrivate()) {
                    toAppend = str.warpTopSub;
                } else {
                    toAppend = str.warpTopSubPrivate;
                }
                toAppend = toAppend.replace("%POSITION%", String.valueOf(min + i + 1)).
                        replace("%WARPNAME%", warp.getName()).
                        replace("%WARPAMOUNT%", String.valueOf(warp.getTimesWarpedTo()));

                if (Bukkit.getOfflinePlayer(warp.getOwner()).hasPlayedBefore()) {
                    toAppend = toAppend.replace("%OWNERNAME%", Bukkit.getOfflinePlayer(warp.getOwner()).getName());
                } else {
                    toAppend = toAppend.replace("%OWNERNAME%", "unknown owner");
                }
                sb.append("\n").append(toAppend);
            }

            return sb.toString();
        } else {
            return str.warpPageNotExists;
        }
    }

    public String formatWarps(String toSearch, int page, UUID player) {
        //TODO finish this method
        List<Warp> warps = warpHandler.getWarps();
        warps.sort(Comparator.comparingInt(Warp::getTimesWarpedTo));
        int min = (page - 1) * WARPS_PER_PAGE;
        int maxPage = pages(warps.size());

        if (testPageRange(page, maxPage)) {
            StringBuilder sb = new StringBuilder(str.warpTopHeader.replace("%PAGE%", its(page)).replace("%MAXPAGE%", its(maxPage)));
            return sb.toString();
        } else {
            return str.warpPageNotExists;
        }
    }

    public static boolean parseBoolean(String bool) {
        if (bool.equalsIgnoreCase("yes") || bool.equalsIgnoreCase("y") || bool.equalsIgnoreCase("true") || bool.equalsIgnoreCase("allow")) {
            return true;
        } else if (bool.equalsIgnoreCase("no") || bool.equalsIgnoreCase("n") || bool.equalsIgnoreCase("false") || bool.equalsIgnoreCase("deny")) {
            return false;
        }
        return false;
    }

    private List<Warp> page(List<Warp> list, int page) {
        List<Warp> toReturn = new ArrayList<>();

        int min = (page - 1) * WARPS_PER_PAGE;
        int max = page * WARPS_PER_PAGE;

        for (int i = min; i < max && i < list.size(); i++) {
            toReturn.add(list.get(i));
        }
        return toReturn;
    }


    private String getWarpListString(Warp warp, int i, UUID requester) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(warp.getOwner());
        boolean invited = warp.isInvited(requester);
        String toRet;
        if (offlinePlayer != null && offlinePlayer.getName() != null) {
            if (!warp.isPrivate()) {
                toRet = "\n" + str.warpListSub.replace("%NAME%", warp.getName()).replace("%OWNER%", offlinePlayer.getName()).replace("%COUNT%", String.valueOf(i + 1));
            } else if (invited && !warp.getOwner().equals(requester)) {
                toRet = "\n" + str.warpListSubInvited.replace("%NAME%", warp.getName()).replace("%OWNER%", offlinePlayer.getName()).replace("%COUNT%", String.valueOf(i + 1));
            } else {
                toRet = "\n" + str.warpListSubPrivate.replace("%NAME%", warp.getName()).replace("%OWNER%", offlinePlayer.getName()).replace("%COUNT%", String.valueOf(i + 1));
            }
        } else {
            if (!warp.isPrivate()) {
                toRet = "\n" + str.warpListSub.replace("%NAME%", warp.getName()).replace("%OWNER%", "unknown owner").replace("%COUNT%", String.valueOf(i + 1));
            } else if (invited  && !warp.getOwner().equals(requester)) {
                toRet = "\n" + str.warpListSubInvited.replace("%NAME%", warp.getName()).replace("%OWNER%", "unknown owner").replace("%COUNT%", String.valueOf(i + 1));
            } else {
                toRet = "\n" + str.warpListSubPrivate.replace("%NAME%", warp.getName()).replace("%OWNER%", "unknown owner").replace("%COUNT%", String.valueOf(i + 1));
            }
        }
        return toRet;
    }

    private void appendWarpList(List<Warp> warps, StringBuilder sb, int min, Player player) {
        for (int i = 0; i < WARPS_PER_PAGE && i < warps.size(); i++) {
            sb.append("\n").append(getWarpListString(warps.get(i), min + i, player.getUniqueId()));
        }
    }

    private boolean testPageRange(int page, int range) {
        return page >= 1 && page <= range;
    }

    private String its(int i) {
        return String.valueOf(i);
    }

}
