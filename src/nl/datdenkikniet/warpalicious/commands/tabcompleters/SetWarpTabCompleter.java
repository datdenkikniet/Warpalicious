package nl.datdenkikniet.warpalicious.commands.tabcompleters;

import java.util.ArrayList;
import java.util.List;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SetWarpTabCompleter extends WarpaliciousTabCompleter {

  public SetWarpTabCompleter(Strings str, WarpHandler handler) {
    super(str, handler);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, String label,
      String[] args) {
    List<String> result = new ArrayList<>();
    if (args.length == 0){
      result.add("<warp name> [private]");
    } else if (args.length == 1) {
      result.add("[private]");
    } else {
      result.add(str.unknownUsage);
    }
    return result;
  }
}
