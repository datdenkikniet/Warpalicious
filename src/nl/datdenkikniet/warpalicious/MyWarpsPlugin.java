package nl.datdenkikniet.warpalicious;

import nl.datdenkikniet.warpalicious.commands.MyWarpsCommand;
import nl.datdenkikniet.warpalicious.config.Config;
import nl.datdenkikniet.warpalicious.config.CustomConfig;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.WarpHandler;
import nl.datdenkikniet.warpalicious.listeners.SignEventListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public class MyWarpsPlugin extends JavaPlugin {

    public CustomConfig cfgHandler = new CustomConfig(this);

    private Config messages = new Config("messages", cfgHandler);
    private Config warps = new Config("warps", cfgHandler);

    private Strings str;
    private WarpHandler handler;

    public void onEnable(){
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (Exception ex){
            getLogger().info("Couldn't enable plugin metrics");
        }
        str = new Strings(cfgHandler, messages, this);
        handler = new WarpHandler(this, warps);
        handler.load();
        getServer().getPluginManager().registerEvents(new SignEventListener(str, handler), this);
        getCommand("warpalicious").setExecutor(new MyWarpsCommand(this, str));
        getLogger().info("Warpalicious version " + getDescription().getVersion() + " has been enabled!");
    }
    public void onDisable(){
        handler.saveWarps();
        getLogger().info("Succesfully saved warps");
    }
    public Strings getStrings(){
        return str;
    }
    public Location stringToLoc(String location)
    {
        String[] stringslist = location.split(",");
        return new Location(Bukkit.getWorld(stringslist[0]),
                Double.valueOf(stringslist[1]),
                Double.valueOf(stringslist[2]),
                Double.valueOf(stringslist[3]),
                Float.valueOf(stringslist[4]),
                Float.valueOf(stringslist[5]));
    }
    public String locationToString(Location loc)
    {
        return String.format("%s,%s,%s,%s,%s,%s", loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }
    public WarpHandler getWarpHandler(){
        return handler;
    }
}
