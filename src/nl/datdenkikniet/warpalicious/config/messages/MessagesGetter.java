/*
 * Copyright � 2015 Jona D
 */
package nl.datdenkikniet.warpalicious.config.messages;


import nl.datdenkikniet.warpalicious.MyWarpsPlugin;
import nl.datdenkikniet.warpalicious.config.Config;
import nl.datdenkikniet.warpalicious.config.CustomConfig;

import java.util.ArrayList;

class MessagesGetter {
    private ArrayList<Message> messages = new ArrayList<>();
    private MyWarpsPlugin plugin;
    MessagesGetter(CustomConfig handler, Config cfg, MyWarpsPlugin instance) {
        plugin = instance;
        if ((cfg.file == null) || (cfg.fileConfig == null)) {
            handler.saveDefaultConfig(cfg);
        }
        handler.reloadCustomConfig(cfg);
        System.out.println("loading messages....");
        handler.getCustomConfig(cfg).getKeys(false).stream().forEach(key -> messages.add(new Message(key, handler.getCustomConfig(cfg).getString(key))));
    }

    String getMessage(String name) throws MessageNotFoundException{
        for (Message message : messages) {
            if (message.getName().equals(name)) {
                return message.getString();
            }
        }
        throw new MessageNotFoundException(name, plugin);
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }
}
