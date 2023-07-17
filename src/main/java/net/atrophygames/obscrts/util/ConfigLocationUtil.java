package net.atrophygames.obscrts.util;

import net.atrophygames.obscrts.TTT;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigLocationUtil {


    private TTT plugin;
    private Location location;
    private String root;

    public ConfigLocationUtil(TTT plugin, Location location, String root) {
        this.plugin = plugin;
        this.location = location;
        this.root = root;
    }

    public ConfigLocationUtil(TTT plugin, String root) {
        this(plugin, null, root);
    }

    public void saveBlockLocation() {
        FileConfiguration fileConfiguration = plugin.getConfig();
        fileConfiguration.set(root + ".world", location.getWorld().getName());
        fileConfiguration.set(root + ".x", location.getBlockX());
        fileConfiguration.set(root + ".y", location.getBlockY());
        fileConfiguration.set(root + ".z", location.getBlockZ());
        plugin.saveConfig();
    }

    public Block loadBlockLocation() {
        FileConfiguration fileConfiguration = plugin.getConfig();

        if(fileConfiguration.contains(root)) {
            World world = Bukkit.getWorld(fileConfiguration.getString(root + ".world"));
            int x = plugin.getConfig().getInt(root + ".x"),
                y = plugin.getConfig().getInt(root + ".y"),
                z = plugin.getConfig().getInt(root + ".z");

            return new Location(world, x, y, z).getBlock();
        }
        return null;
    }

    public void saveLocation() {
        FileConfiguration fileConfiguration = plugin.getConfig();
        fileConfiguration.set(root + ".world", location.getWorld().getName());
        fileConfiguration.set(root + ".x", location.getX());
        fileConfiguration.set(root + ".y", location.getY());
        fileConfiguration.set(root + ".z", location.getZ());
        fileConfiguration.set(root + ".yaw", location.getYaw());
        fileConfiguration.set(root + ".pitch", location.getPitch());
        plugin.saveConfig();
    }

    public Location loadLocation() {
        FileConfiguration fileConfiguration = plugin.getConfig();

        if(fileConfiguration.contains(root)) {
            World world = Bukkit.getWorld(fileConfiguration.getString(root + ".world"));
            double x = fileConfiguration.getDouble(root + ".x"),
                   y = fileConfiguration.getDouble(root + ".y"),
                   z = fileConfiguration.getDouble(root + ".z");
            float yaw = (float) fileConfiguration.getDouble(root + ".yaw"),
                  pitch = (float) fileConfiguration.getDouble(root + ".pitch");

            return new Location(world, x, y, z, yaw, pitch);
        }
        return null;
    }
}
