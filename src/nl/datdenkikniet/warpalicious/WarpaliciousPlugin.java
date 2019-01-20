package nl.datdenkikniet.warpalicious;

import nl.datdenkikniet.warpalicious.commands.*;
import nl.datdenkikniet.warpalicious.config.CustomConfig;
import nl.datdenkikniet.warpalicious.config.CustomConfigHandler;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.IO.IOHandler;
import nl.datdenkikniet.warpalicious.handling.IO.YAMLIOHandler;
import nl.datdenkikniet.warpalicious.handling.MessageGenerator;
import nl.datdenkikniet.warpalicious.handling.TeleportMode;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import nl.datdenkikniet.warpalicious.listeners.SignEventListener;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class WarpaliciousPlugin extends JavaPlugin {

    private static WarpaliciousPlugin instance;

    private CustomConfigHandler cfgHandler = new CustomConfigHandler(this);

    private CustomConfig messages = new CustomConfig("messages", cfgHandler);
    private CustomConfig warps = new CustomConfig("warps", cfgHandler);
    private CustomConfig config = new CustomConfig("config", cfgHandler);

    private Strings str;
    private WarpHandler handler;
    private IOHandler ioHandler;
    private MessageGenerator messageGenerator;

    public void onEnable() {
        instance = this;
        try {
            new Metrics(this);
        } catch (Exception ex) {
            getLogger().info("Couldn't enable plugin metrics");
        }
        checkTeleportModes();

        str = new Strings(cfgHandler, messages, this);

        ioHandler = new YAMLIOHandler(cfgHandler, warps);

        handler = new WarpHandler(ioHandler);
        handler.loadWarps();

        messageGenerator = new MessageGenerator(handler, str);

        getServer().getPluginManager().registerEvents(new SignEventListener(this, str, handler), this);
        getCommand("warpalicious").setExecutor(new WarpaliciousCommand(this, str));

        loadCommands();

        getLogger().info("Warpalicious version " + getDescription().getVersion() + " has been enabled!");
    }

    public void onDisable() {
        handler.saveWarps();
        getLogger().info("Succesfully saved warps");
    }

    private void checkTeleportModes() {
        FileConfiguration cfg = cfgHandler.getCustomConfig(config);

        boolean allowMoveSignSurv = cfg.getBoolean("settings.survival.sign.allow-move"), allowMoveCommandSurv = cfg.getBoolean("settings.survival.command.allow-move"), allowMoveSignCrea = cfg.getBoolean("settings.creative.sign.allow-move"), allowMoveCommandCrea = cfg.getBoolean("settings.creative.command.allow-move");

        boolean survSignPerm = cfg.isString("settings.survival.sign.delay"), survCommandPerm = cfg.isString("settings.survival.command.delay"), creaSignPerm = cfg.isString("settings.creative.sign.delay"), creaCommandPerm = cfg.isString("settings.creative.command.delay");

        int delaySignSurv = survSignPerm ? 0 : cfg.getInt("settings.survival.sign.delay"), delayCommandSurv = survCommandPerm ? 0 : cfg.getInt("settings.survival.command.delay"), delaySignCrea = creaSignPerm ? 0 : cfg.getInt("settings.creative.sign.delay"), delayCommandCrea = creaCommandPerm ? 0 : cfg.getInt("settings.creative.command.delay");

        int arriveSignCount = cfg.getInt("effects.arrival.sign.count"), arriveCommandCount = cfg.getInt("effects.arrival.command.count"), departSignCount = cfg.getInt("effects.departure.sign.count"), departCommandCount = cfg.getInt("effects.departure.command.count");

        Particle arriveSignEffect = null, arriveCommandEffect = null, departSignEffect = null, departCommandEffect = null;
        try {
            arriveSignEffect = Particle.valueOf(cfg.getString("effects.arrival.sign.effect").toUpperCase());
        } catch (Exception ignored) {
        }
        try {
            arriveCommandEffect = Particle.valueOf(cfg.getString("effects.arrival.command.effect").toUpperCase());
        } catch (Exception ignored) {
        }
        try {
            departSignEffect = Particle.valueOf(cfg.getString("effects.departure.sign.effect").toUpperCase());
        } catch (Exception ignored) {
        }
        try {
            departCommandEffect = Particle.valueOf(cfg.getString("effects.departure.command.effect").toUpperCase());
        } catch (Exception ignored) {
        }

        TeleportMode.SIGN.setValues(delaySignCrea, delaySignSurv, creaSignPerm, survSignPerm, arriveSignEffect, departSignEffect, arriveSignCount, departSignCount, allowMoveSignCrea, allowMoveSignSurv);
        TeleportMode.COMMAND.setValues(delayCommandCrea, delayCommandSurv, survCommandPerm, creaCommandPerm, arriveCommandEffect, departCommandEffect, arriveCommandCount, departCommandCount, allowMoveCommandCrea, allowMoveCommandSurv);
    }

    private void loadCommands() {
        getCommand("warp").setExecutor(new WarpCommand(str, handler));
        getCommand("setwarp").setExecutor(new SetWarpCommand(str, handler));
        getCommand("delwarp").setExecutor(new DelWarpCommand(str, handler));
        getCommand("warplist").setExecutor(new WarplistCommand(str, messageGenerator));
        getCommand("editwarp").setExecutor(new EditWarpCommand(str, handler));
        getCommand("warpinfo").setExecutor(new WarpinfoCommand(handler, str, messageGenerator));
        getCommand("findwarp").setExecutor(new FindWarpCommand(str, messageGenerator));
        getCommand("warpinvite").setExecutor(new WarpInviteCommand(handler, str));
        getCommand("warpuninvite").setExecutor(new WarpInviteCommand(handler, str));
    }

    public void reloadStrings() {
        str.loadMessages();
    }

    public static WarpaliciousPlugin getInstance() {
        return instance;
    }
}
