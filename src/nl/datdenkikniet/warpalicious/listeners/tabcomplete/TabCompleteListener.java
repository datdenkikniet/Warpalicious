package nl.datdenkikniet.warpalicious.listeners.tabcomplete;

import nl.datdenkikniet.warpalicious.WarpaliciousPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

public abstract class TabCompleteListener implements Listener {

  protected WarpaliciousPlugin plugin;

  public TabCompleteListener(WarpaliciousPlugin instance) {
    this.plugin = instance;
  }

  @EventHandler
  public void tabCmplt(TabCompleteEvent evt){
    String buffer = evt.getBuffer();
    if (buffer.startsWith("/" + getCommandName())){
      complete(evt);
    }
  }

  protected abstract String getCommandName();

  public abstract void complete(TabCompleteEvent evt);

}
