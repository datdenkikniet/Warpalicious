/*
 * Copyright � 2015 Jona D
 */
package nl.datdenkikniet.warpalicious.config.messages;


import nl.datdenkikniet.warpalicious.MyWarpsPlugin;
import nl.datdenkikniet.warpalicious.config.Config;
import nl.datdenkikniet.warpalicious.config.CustomConfig;

import java.util.ArrayList;

public class MessagesGetter {
    private ArrayList<Message> messages = new ArrayList<Message>();
    private MyWarpsPlugin plugin;
    MessagesGetter(CustomConfig handler, Config cfg, MyWarpsPlugin instance) {
        plugin = instance;
        if ((cfg.file == null) || (cfg.fileConfig == null)) {
            handler.saveDefaultConfig(cfg);
        }
        handler.reloadCustomConfig(cfg);
        System.out.println("loading messages....");
        for (String key : handler.getCustomConfig(cfg).getKeys(false)) {
            messages.add(new Message(key, handler.getCustomConfig(cfg).getString(key)));
        }
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
