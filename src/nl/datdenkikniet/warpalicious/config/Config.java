/*
 * Copyright � 2015 Jona D
 */
package nl.datdenkikniet.warpalicious.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class Config {

    public String name;
    public File file;
    public FileConfiguration fileConfig;

    public Config(String name, CustomConfig handler){
        this.name = name;
        if (this.file == null || this.fileConfig == null){
            handler.saveDefaultConfig(this);
        }
    }
}