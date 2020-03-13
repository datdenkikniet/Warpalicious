package nl.datdenkikniet.warpalicious.listeners.tabcomplete;

import nl.datdenkikniet.warpalicious.WarpaliciousPlugin;
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
  public void complete(TabCompleteEvent evt) {

  }
}
