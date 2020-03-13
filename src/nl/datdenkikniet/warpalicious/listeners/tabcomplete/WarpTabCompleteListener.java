package nl.datdenkikniet.warpalicious.listeners.tabcomplete;

import java.util.ArrayList;
import java.util.List;
import nl.datdenkikniet.warpalicious.WarpaliciousPlugin;
import nl.datdenkikniet.warpalicious.handling.Warp;
import org.bukkit.entity.Player;
import org.bukkit.event.server.TabCompleteEvent;

public class WarpTabCompleteListener extends TabCompleteListener {

  public WarpTabCompleteListener(WarpaliciousPlugin instance) {
    super(instance);
  }

  @Override
  protected String getCommandName() {
    return "warp";
  }

  @Override
  public void complete(TabCompleteEvent evt, String[] args) {
    List<String> completions = new ArrayList<>();
    evt.setCompletions(completions);
    if (evt.getSender() instanceof Player) {
      Player player = (Player) evt.getSender();
      List<Warp> warps = handler.getWarpList(player);
      final List<Warp> filteredWarps = new ArrayList<>();
      if (args.length == 1) {
        warps.iterator().forEachRemaining((warp) -> {
          if (warp.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
            filteredWarps.add(warp);
          }
        });
      } else {
        filteredWarps.addAll(warps);
      }
      System.out.println(filteredWarps.size());
      if (filteredWarps.size() != 0) {
        filteredWarps.forEach((warp) -> completions.add(warp.getName()));
      }
    }
  }
}
