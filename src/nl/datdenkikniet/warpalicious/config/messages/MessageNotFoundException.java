package nl.datdenkikniet.warpalicious.config.messages;

import nl.datdenkikniet.warpalicious.MyWarpsPlugin;

/**
 * Created by Jona on 23/10/2016.
 */
public class MessageNotFoundException extends Exception {
    private String messageName;
    private MyWarpsPlugin plugin;

    public MessageNotFoundException(String name, MyWarpsPlugin instance) {
        messageName = name;
        plugin = instance;
    }

    @Override
    public void printStackTrace() {
        plugin.getLogger().severe("Could not find message " + messageName + ".");
        plugin.getLogger().severe("Please regenerate your messages.yml");
        plugin.getLogger().severe("The plugin will continue to function.");
        plugin.getLogger().severe("Some features have no proper messages");
    }
}
