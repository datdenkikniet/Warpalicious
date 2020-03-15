package nl.datdenkikniet.warpalicious.commands.tabcompleters;

import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.bukkit.command.TabCompleter;

public abstract class WarpaliciousTabCompleter implements TabCompleter {

  protected WarpHandler handler;
  protected Strings str;

  public WarpaliciousTabCompleter(Strings str, WarpHandler handler) {
    this.handler = handler;
    this.str = str;
  }
}
