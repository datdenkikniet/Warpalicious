package nl.datdenkikniet.warpalicious;

import nl.datdenkikniet.warpalicious.commands.WarpaliciousCommand;
import nl.datdenkikniet.warpalicious.config.Config;
import nl.datdenkikniet.warpalicious.config.CustomConfig;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.TeleportMode;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import nl.datdenkikniet.warpalicious.listeners.SignEventListener;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class WarpaliciousPlugin extends JavaPlugin {

    public CustomConfig cfgHandler = new CustomConfig(this);

    private Config messages = new Config("messages", cfgHandler);
    private Config warps = new Config("warps", cfgHandler);
    private Config config = new Config("config", cfgHandler);

    private Strings str;
    private WarpHandler handler;

    public void onEnable(){
        try{
            new Metrics(this);
        } catch (Exception ex){
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

    public void onDisable(){
        handler.saveWarps();
        getLogger().info("Succesfully saved warps");
    }

    public Strings getStrings(){
        return str;
    }

    public Location stringToLoc(String location){
        String[] stringslist = location.split(",");
        return new Location(getServer().getWorld(stringslist[0]), Double.valueOf(stringslist[1]), Double.valueOf(stringslist[2]), Double.valueOf(stringslist[3]), Float.valueOf(stringslist[4]), Float.valueOf(stringslist[5]));
    }

    public String locationToString(Location loc){
        return String.format("%s,%s,%s,%s,%s,%s", loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    public WarpHandler getWarpHandler(){
        return handler;
    }

    private void checkTeleportModes(){
        FileConfiguration cfg = cfgHandler.getCustomConfig(config);

        boolean allowMoveSignSurv = cfg.getBoolean("settings.survival.sign.allow-move"), allowMoveCommandSurv = cfg.getBoolean("settings.survival.command.allow-move"), allowMoveSignCrea = cfg.getBoolean("settings.creative.sign.allow-move"), allowMoveCommandCrea = cfg.getBoolean("settings.creative.command.allow-move");

        boolean survSignPerm = cfg.isString("settings.survival.sign.delay"), survCommandPerm = cfg.isString("settings.survival.command.delay"), creaSignPerm = cfg.isString("settings.creative.sign.delay"), creaCommandPerm = cfg.isString("settings.creative.command.delay");

        int delaySignSurv = survSignPerm ? 0 : cfg.getInt("settings.survival.sign.delay"), delayCommandSurv = survCommandPerm ? 0 : cfg.getInt("settings.survival.command.delay"), delaySignCrea = creaSignPerm ? 0 : cfg.getInt("settings.creative.sign.delay"), delayCommandCrea = creaCommandPerm ? 0 : cfg.getInt("settings.creative.command.delay");

        int arriveSignCount = cfg.getInt("effects.arrival.sign.count"), arriveCommandCount = cfg.getInt("effects.arrival.command.count"), departSignCount = cfg.getInt("effects.departure.sign.count"), departCommandCount = cfg.getInt("effects.departure.command.count");

        Particle arriveSignEffect = null, arriveCommandEffect = null, departSignEffect = null, departCommandEffect = null;
        try{
            arriveSignEffect = Particle.valueOf(cfg.getString("effects.arrival.sign.effect").toUpperCase());
        } catch (Exception ignored){
        }
        try{
            arriveCommandEffect = Particle.valueOf(cfg.getString("effects.arrival.command.effect").toUpperCase());
        } catch (Exception ignored){
        }
        try{
            departSignEffect = Particle.valueOf(cfg.getString("effects.departure.sign.effect").toUpperCase());
        } catch (Exception ignored){
        }
        try{
            departCommandEffect = Particle.valueOf(cfg.getString("effects.departure.command.effect").toUpperCase());
        } catch (Exception ignored){
        }

        TeleportMode.SIGN.setValues(delaySignCrea, delaySignSurv, creaSignPerm, survSignPerm, arriveSignEffect, departSignEffect, arriveSignCount, departSignCount, allowMoveSignCrea, allowMoveSignSurv);
        TeleportMode.COMMAND.setValues(delayCommandCrea, delayCommandSurv, survCommandPerm, creaCommandPerm, arriveCommandEffect, departCommandEffect, arriveCommandCount, departCommandCount, allowMoveCommandCrea, allowMoveCommandSurv);
    }
}
