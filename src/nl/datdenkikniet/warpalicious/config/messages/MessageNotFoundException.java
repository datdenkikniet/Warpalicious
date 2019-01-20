package nl.datdenkikniet.warpalicious.config.messages;

import nl.datdenkikniet.warpalicious.WarpaliciousPlugin;

class MessageNotFoundException extends Exception {

    private String messageName;
    private WarpaliciousPlugin plugin;

    MessageNotFoundException(String name, WarpaliciousPlugin instance) {
        messageName = name;
        plugin = instance;
    }

    @Override
    public void printStackTrace() {
        plugin.getLogger().severe("Could not find message " + messageName + ".");
        plugin.getLogger().severe("Please regenerate your messages.yml or find the missing messages at https://goo.gl/UqXISh");
        plugin.getLogger().severe("The plugin will continue to function.");
        plugin.getLogger().severe("Some features will have no proper messages");
    }
}
