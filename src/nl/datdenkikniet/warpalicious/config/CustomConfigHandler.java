/*
 * Copyright � 2015 Jona D
 */
package nl.datdenkikniet.warpalicious.config;

import nl.datdenkikniet.warpalicious.WarpaliciousPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class CustomConfigHandler {

    private WarpaliciousPlugin plugin;

    public CustomConfigHandler(WarpaliciousPlugin instance) {
        plugin = instance;
    }

    public FileConfiguration getCustomConfig(CustomConfig config) {
        if (config.fileConfig == null) {
            reloadCustomConfig(config);
        }
        return config.fileConfig;
    }

    public void reloadCustomConfig(CustomConfig config) {
        if (config.fileConfig == null) {
            config.file = new File(plugin.getDataFolder(), config.name + ".yml");
        }
        config.fileConfig = YamlConfiguration.loadConfiguration(config.file);

        InputStream defConfigStream = plugin.getResource(config.name + ".yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            config.fileConfig.setDefaults(defConfig);
        }
    }

    public void saveCustomConfig(CustomConfig config) {
        if (config.fileConfig == null || config.file == null) {
            return;
        }
        try {
            getCustomConfig(config).save(config.file);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + config.file, ex);
        }
    }

    public void saveDefaultConfig(CustomConfig config) {
        if (config.file == null) {
            config.file = new File(plugin.getDataFolder(), config.name + ".yml");
        }
        if (!config.file.exists()) {
            plugin.saveResource(config.name + ".yml", false);
        }
    }
}