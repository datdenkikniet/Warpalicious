package nl.datdenkikniet.warpalicious.listeners.tabcomplete;

import nl.datdenkikniet.warpalicious.WarpaliciousPlugin;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

public abstract class TabCompleteListener implements Listener {

  protected WarpaliciousPlugin plugin;
  protected WarpHandler handler;

  public TabCompleteListener(WarpaliciousPlugin instance) {
    this.plugin = instance;
    this.handler = this.plugin.getWarpHandler();
  }

  @EventHandler
  public void tabCmplt(TabCompleteEvent evt) {
    String buffer = evt.getBuffer();
    if (buffer.startsWith("/" + getCommandName())) {
      String[] bufSplit = buffer.split(" ");
      String[] args = new String[bufSplit.length - 1];
      System.arraycopy(bufSplit, 1, args, 0, bufSplit.length - 1);
      complete(evt, args);
    }
  }

  protected abstract String getCommandName();

  public abstract void complete(TabCompleteEvent evt, String[] args);

}
