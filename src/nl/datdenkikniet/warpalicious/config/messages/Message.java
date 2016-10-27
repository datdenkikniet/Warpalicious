/*
 * Copyright � 2015 Jona D
 */
package nl.datdenkikniet.warpalicious.config.messages;

import org.bukkit.ChatColor;

class Message {
    private String name;
    private String string;

    private String repl(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public Message(String name, String str) {
        this.name = name;
        this.string = repl(str);
    }

    public String getName() {
        return name;
    }

    public String getString() {
        return string;
    }
}
