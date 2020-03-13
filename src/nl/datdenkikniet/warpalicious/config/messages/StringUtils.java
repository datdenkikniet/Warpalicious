package nl.datdenkikniet.warpalicious.config.messages;

import java.util.List;
import java.util.UUID;
import nl.datdenkikniet.warpalicious.handling.Warp;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class StringUtils {

  public static String getWarpListString(Strings str, Warp warp, int index, UUID requester) {
    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(warp.getOwner());
    boolean invited = warp.isInvited(requester);
    String result;
    if (offlinePlayer.getName() != null) {
      if (!warp.isPrivate()) {
        result = str.warpListSub;
      } else if (invited) {
        result = str.warpListSubInvited;
      } else {
        result = str.warpListSubPrivate;
      }
      result = result.replace("%NAME%", warp.getName())
          .replace("%OWNER%", offlinePlayer.getName())
          .replace("%COUNT%", String.valueOf(index));
    } else {
      if (!warp.isPrivate()) {
        result = str.warpListSub;
      } else if (invited) {
        result = str.warpListSubInvited;
      } else {
        result = str.warpListSubPrivate;
      }
      result = result.replace("%NAME%", warp.getName())
          .replace("%OWNER%", "unknown owner")
          .replace("%COUNT%", String.valueOf(index));
    }
    return result;
  }

  public static String toWarpListPageString(Strings str, Player requester, String title, int page,
      int maxPage, List<Warp> warps) {
    if (warps == null) {
      return str.warpPageNotExists;
    } else {
      StringBuilder res = new StringBuilder(title.replace("%PAGE%", String.valueOf(page))
          .replace("%MAXPAGE%", String.valueOf(maxPage)));
      int startWarp = (page - 1) * WarpHandler.WARPLIST_PAGE_SIZE + 1;
      for (int i = 0; i < warps.size(); i++) {
        res.append("\n").append(StringUtils
            .getWarpListString(str, warps.get(i), startWarp + i, requester.getUniqueId()));
      }
      return res.toString();
    }
  }


  public static boolean parseBoolean(String bool) {
    if (bool.equalsIgnoreCase("yes") || bool.equalsIgnoreCase("y") || bool.equalsIgnoreCase("true")
        || bool.equalsIgnoreCase("allow")) {
      return true;
    } else if (bool.equalsIgnoreCase("no") || bool.equalsIgnoreCase("n") || bool
        .equalsIgnoreCase("false") || bool.equalsIgnoreCase("deny")) {
      return false;
    }
    return false;
  }

}
