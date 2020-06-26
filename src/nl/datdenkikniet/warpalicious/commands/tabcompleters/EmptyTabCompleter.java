package nl.datdenkikniet.warpalicious.commands.tabcompleters;

import java.util.ArrayList;
import java.util.List;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class EmptyTabCompleter extends WarpaliciousTabCompleter {

  public EmptyTabCompleter(Strings str,
      WarpHandler handler) {
    super(str, handler);
  }

  @Override
  public List<String> onTabComplete(CommandSender commandSender, Command command, String s,
      String[] strings) {
    return new ArrayList<>();
  }
}
