package nl.datdenkikniet.warpalicious;

import nl.datdenkikniet.warpalicious.commands.WarpaliciousCommand;
import nl.datdenkikniet.warpalicious.config.Config;
import nl.datdenkikniet.warpalicious.config.CustomConfig;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.TeleportMode;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import nl.datdenkikniet.warpalicious.listeners.SignEventListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public class WarpaliciousPlugin extends JavaPlugin {

    public CustomConfig cfgHandler = new CustomConfig(this);

    private Config messages = new Config("messages", cfgHandler);
    private Config warps = new Config("warps", cfgHandler);
    private Config config = new Config("config", cfgHandler);

    private Strings str;
    private WarpHandler handler;

    public void onEnable() {
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (Exception ex) {
            getLogger().info("Couldn't enable plugin metrics");
        }
        checkTeleportModes();
        str = new Strings(cfgHandler, messages, this);
        handler = new WarpHandler(this, warps);
        handler.load();
        getServer().getPluginManager().registerEvents(new SignEventListener(this), this);
        getCommand("warpalicious").setExecutor(new WarpaliciousCommand(this, str));
        getLogger().info("Warpalicious version " + getDescription().getVersion() + " has been enabled!");
    }

    public void onDisable() {
        handler.saveWarps();
        getLogger().info("Succesfully saved warps");
    }

    public Strings getStrings() {
        return str;
    }

    public Location stringToLoc(String location) {
        String[] stringslist = location.split(",");
        return new Location(Bukkit.getWorld(stringslist[0]),
                Double.valueOf(stringslist[1]),
                Double.valueOf(stringslist[2]),
                Double.valueOf(stringslist[3]),
                Float.valueOf(stringslist[4]),
                Float.valueOf(stringslist[5]));
    }

    public String locationToString(Location loc) {
        return String.format("%s,%s,%s,%s,%s,%s", loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    public WarpHandler getWarpHandler() {
        return handler;
    }

    private void checkTeleportModes() {
        FileConfiguration cfg = cfgHandler.getCustomConfig(config);
        boolean signSurvMove = cfg.getBoolean("settings.survival.sign.allow-move"),
                commandSurvMove = cfg.getBoolean("settings.survival.command.allow-move"),
                signCreaMove = cfg.getBoolean("settings.creative.sign.allow-move"),
                commandCreaMove = cfg.getBoolean("settings.creative.command.allow-move");

        boolean survSignPerm = cfg.isString("settings.survival.sign.delay"),
                survCommandPerm = cfg.isString("settings.survival.command.delay"),
                creaSignPerm = cfg.isString("settings.creative.sign.delay"),
                creaCommandPerm = cfg.isString("settings.creative.command.delay");

        int survSign = survSignPerm ? 0 : cfg.getInt("settings.survival.sign.delay"),
                survCommand = survCommandPerm ? 0 : cfg.getInt("settings.survival.command.delay"),
                creaSign = creaSignPerm ? 0 : cfg.getInt("settings.creative.sign.delay"),
                creaCommand = creaCommandPerm ? 0 : cfg.getInt("settings.creative.command.delay");

        int arrivSignCount = cfg.getInt("effects.arrival.sign.count"),
                arrivCommandCount = cfg.getInt("effects.arrival.command.count"),
                depSignCount = cfg.getInt("effects.departure.sign.count"),
                depCommandCount = cfg.getInt("effects.departure.command.count");

        Particle arrivSignEff, arrivCommandEff, depSignEff, depCommandEff;
        try {
            arrivSignEff = Particle.valueOf(cfg.getString("effects.arrival.sign.effect").toUpperCase());
        } catch (Exception ex) {
            arrivSignEff = null;
        }
        try {
            arrivCommandEff = Particle.valueOf(cfg.getString("effects.arrival.command.effect").toUpperCase());
        } catch (Exception ex) {
            arrivCommandEff = null;
        }
        try {
            depSignEff = Particle.valueOf(cfg.getString("effects.departure.sign.effect").toUpperCase());
        } catch (Exception ex) {
            depSignEff = null;
        }
        try {
            depCommandEff = Particle.valueOf(cfg.getString("effects.departure.command.effect").toUpperCase());
        } catch (Exception ex) {
            depCommandEff = null;
        }
        TeleportMode.SIGN.setValues(creaSign, survSign, creaSignPerm, survSignPerm, arrivSignEff, depSignEff, arrivSignCount, depSignCount, signCreaMove, signSurvMove);
        TeleportMode.COMMAND.setValues(creaCommand, survCommand, survCommandPerm, creaCommandPerm, arrivCommandEff, depCommandEff, arrivCommandCount, depCommandCount, commandCreaMove, commandSurvMove);
    }
}
