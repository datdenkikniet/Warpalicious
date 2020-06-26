package nl.datdenkikniet.warpalicious.commands.tabcompleters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.Warp;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpTabCompleter extends WarpaliciousTabCompleter {

  public WarpTabCompleter(Strings str, WarpHandler handler) {
    super(str, handler);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, String label,
      String[] args) {
    List<String> completions = new ArrayList<>();
    if (sender instanceof Player) {
      Player player = (Player) sender;
      List<Warp> warps = handler.getWarpList(player);
      if (args.length == 1) {
        final List<Warp> filteredWarps = warps.stream()
            .filter(warp -> warp.getName().toLowerCase().startsWith(args[0].toLowerCase()))
            .collect(Collectors.toList());
        if (filteredWarps.size() != 0) {
          filteredWarps.forEach((warp) -> completions.add(warp.getName()));
        }
      }
    }
    return completions;
  }
}
