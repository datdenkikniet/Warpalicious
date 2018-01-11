package nl.datdenkikniet.warpalicious.commands;

import me.odium.warptastic.DBConnection;
import me.odium.warptastic.warptastic;
import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.bukkit.MyWarpPlugin;
import me.taylorkelly.mywarp.warp.EventfulPopulatableWarpManager;
import me.taylorkelly.mywarp.warp.MemoryPopulatableWarpManager;
import me.taylorkelly.mywarp.warp.PopulatableWarpManager;
import me.taylorkelly.mywarp.warp.Warp;
import nl.datdenkikniet.warpalicious.WarpaliciousPlugin;
import nl.datdenkikniet.warpalicious.config.messages.Strings;
import nl.datdenkikniet.warpalicious.handling.Flag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class WarpaliciousCommand implements CommandExecutor {

    private WarpaliciousPlugin plugin;
    private Strings str;

    public WarpaliciousCommand(WarpaliciousPlugin pl, Strings instance) {
        str = instance;
        plugin = pl;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reloadmessages") && str.checkPermission(sender, str.universalPerm)) {
                plugin.getStrings().loadMessages();
                sender.sendMessage(str.prefix + " Succesfully reloaded messages!");
                return true;
            } else {
                sender.sendMessage(str.getUsage(cmd, label));
                return true;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("import")) {
                if (str.checkPermission(sender, str.universalPerm)) {
                    if (args[1].equalsIgnoreCase("mywarp")) {
                        MyWarpPlugin mwp = (MyWarpPlugin) plugin.getServer().getPluginManager().getPlugin("MyWarp");
                        if (mwp == null) {
                            sender.sendMessage("The MyWarp plugin is not installed on this server!");
                            return true;
                        }
                        try {
                            Field myWarpField = mwp.getClass().getDeclaredField("myWarp");
                            myWarpField.setAccessible(true);
                            EventfulPopulatableWarpManager ewm = (EventfulPopulatableWarpManager) ((MyWarp) myWarpField.get(mwp)).getWarpManager();

                            Method delegateEPWM = ewm.getClass().getDeclaredMethod("delegate");
                            delegateEPWM.setAccessible(true);
                            PopulatableWarpManager pwm = (PopulatableWarpManager) delegateEPWM.invoke(ewm);

                            Method delegatePWM = pwm.getClass().getDeclaredMethod("delegate");
                            delegatePWM.setAccessible(true);
                            MemoryPopulatableWarpManager mpw = (MemoryPopulatableWarpManager) delegatePWM.invoke(pwm);

                            Field warpMapField = mpw.getClass().getDeclaredField("warpMap");
                            warpMapField.setAccessible(true);

                            @SuppressWarnings("unchecked")
                            HashMap<String, me.taylorkelly.mywarp.warp.Warp> warpMap = (HashMap<String, me.taylorkelly.mywarp.warp.Warp>) warpMapField.get(mpw);

                            int addedWarps = 0;
                            int failedWarps = 0;
                            int totalWarps = warpMap.size();
                            for (String wName : warpMap.keySet()) {

                                Object initialObj = warpMap.get(wName);

                                Field warpField = initialObj.getClass().getDeclaredField("delegate");
                                warpField.setAccessible(true);

                                Warp delegateFirst = (Warp) warpField.get(initialObj);

                                Field warpFieldSecond = delegateFirst.getClass().getDeclaredField("delegate");
                                warpFieldSecond.setAccessible(true);

                                Warp warpMyWarp = (Warp) warpFieldSecond.get(delegateFirst);

                                Field posField = warpMyWarp.getClass().getDeclaredField("position");
                                Field rotField = warpMyWarp.getClass().getDeclaredField("rotation");
                                Field worldUUIDField = warpMyWarp.getClass().getDeclaredField("worldIdentifier");
                                posField.setAccessible(true);
                                rotField.setAccessible(true);
                                worldUUIDField.setAccessible(true);

                                UUID worldUUID = (UUID) worldUUIDField.get(warpMyWarp);

                                Field xField = posField.get(warpMyWarp).getClass().getDeclaredField("x");
                                Field yField = posField.get(warpMyWarp).getClass().getDeclaredField("y");
                                Field zField = posField.get(warpMyWarp).getClass().getDeclaredField("z");
                                xField.setAccessible(true);
                                yField.setAccessible(true);
                                zField.setAccessible(true);

                                Field pitchField = rotField.get(warpMyWarp).getClass().getDeclaredField("x");
                                Field yawField = rotField.get(warpMyWarp).getClass().getDeclaredField("y");
                                yawField.setAccessible(true);
                                pitchField.setAccessible(true);

                                double x = (double) xField.get(posField.get(warpMyWarp));
                                double y = (double) yField.get(posField.get(warpMyWarp));
                                double z = (double) zField.get(posField.get(warpMyWarp));

                                float yaw = (float) yawField.get(rotField.get(warpMyWarp));
                                float pitch = (float) pitchField.get(rotField.get(warpMyWarp));

                                Location loc = new Location(Bukkit.getWorld(worldUUID), x, y, z, yaw, pitch);
                                ArrayList<UUID> invitedPlayers = new ArrayList<>();
                                invitedPlayers.addAll(warpMyWarp.getInvitedPlayers());

                                HashMap<Flag, Boolean> defFlags = plugin.getWarpHandler().getDefaultFlags();

                                if (warpMyWarp.getType() == Warp.Type.PRIVATE) {
                                    defFlags.put(Flag.PRIVATE, true);
                                }

                                if (plugin.getWarpHandler().getWarp(wName) == null) {
                                    new nl.datdenkikniet.warpalicious.handling.Warp(plugin, warpMyWarp.getCreator(), loc, warpMyWarp.getName(), defFlags, warpMyWarp.getVisits(), invitedPlayers);
                                    System.out.println("Imported " + (defFlags.get(Flag.PRIVATE) ? "private" : "") + " warp with name " + wName + " at location x:" + x + ", y:" + y + ", z:" + z + ", yaw:" + yaw + ", pitch:" + pitch + " and " + invitedPlayers.size() + " invited players");
                                    addedWarps++;
                                } else {
                                    System.out.println("Failed to add import warp " + wName + ". A warp with that name already exists!");
                                    failedWarps++;
                                }
                            }
                            sender.sendMessage("Attempting to import " + totalWarps + " warps...");
                            sender.sendMessage("Imported " + addedWarps + " warps from mywarp.");
                            sender.sendMessage("Failed to add " + failedWarps + " warps (check the console for more information).");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            sender.sendMessage("Something went wrong!");
                            return true;
                        }
                        return true;
                    } else if (args[1].equalsIgnoreCase("warptastic")) {
                        int addedWarps = 0;
                        int failedWarps = 0;
                        int totalWarps = 0;
                        try {
                            warptastic wp = (warptastic) plugin.getServer().getPluginManager().getPlugin("warptastic");
                            Field serviceField = wp.getClass().getDeclaredField("service");
                            serviceField.setAccessible(true);
                            DBConnection service = (DBConnection) serviceField.get(wp);

                            Connection stmt = service.getConnection();
                            Statement rs = stmt.createStatement();
                            ResultSet target = rs.executeQuery("SELECT * FROM W_Warps;");
                            while (target.next()) {
                                totalWarps++;
                                String name = target.getString("warpname");
                                UUID owner = Bukkit.getOfflinePlayer(target.getString("owner")).getUniqueId();
                                String world = target.getString("world");
                                double x = target.getDouble("x");
                                double y = target.getDouble("y");
                                double z = target.getDouble("z");
                                float yaw = target.getFloat("f");
                                float pitch = target.getFloat("p");
                                boolean isPrivate = target.getBoolean("privacy");
                                int timesWarpedTo = target.getInt("popularity");
                                HashMap<Flag, Boolean> defFlags = plugin.getWarpHandler().getDefaultFlags();
                                if (isPrivate) {
                                    defFlags.put(Flag.PRIVATE, true);
                                }
                                if (plugin.getWarpHandler().getWarp(name) == null) {
                                    new nl.datdenkikniet.warpalicious.handling.Warp(plugin, owner, new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch), name, defFlags, timesWarpedTo, new ArrayList<>());
                                    System.out.println("Imported " + (defFlags.get(Flag.PRIVATE) ? "private" : "") + " warp with name " + name + " at location x:" + x + ", y:" + y + ", z:" + z + ", yaw:" + yaw + ", pitch:" + pitch);
                                    addedWarps++;
                                } else {
                                    System.out.println("Failed to add import warp " + name + ". A warp with that name already exists!");
                                    failedWarps++;
                                }
                            }
                            target.close();
                            rs.close();
                            plugin.getWarpHandler().saveWarps();
                            sender.sendMessage("Ateempted to import " + totalWarps + " warps...");
                            sender.sendMessage("Imported " + addedWarps + " warps from warptastic.");
                            sender.sendMessage("Failed to add " + failedWarps + " warps (check the console for more information).");
                            return true;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            sender.sendMessage("Something went wrong while trying to import warptastic warps!");
                            return true;
                        }
                    } else {
                        sender.sendMessage(str.getUsage(cmd, label));
                        return true;
                    }
                } else {
                    sender.sendMessage(str.noperm);
                    return true;
                }
            } else {
                sender.sendMessage(str.getUsage(cmd, label));
                return true;
            }

        } else {
            sender.sendMessage(str.prefix + " This server is running Warpalicious version " + plugin.getDescription().getVersion() + " by datdenkikniet.");
            return true;
        }
    }

}
