/*
 * Copyright � 2015 Jona D
 */
package nl.datdenkikniet.warpalicious.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class CustomConfig {

    public String name;
    public File file;
    public FileConfiguration fileConfig;

    public CustomConfig(String name, CustomConfigHandler handler) {
        this.name = name;
        handler.saveDefaultConfig(this);
    }
}