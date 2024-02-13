package com.kliminskyi.ffregions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

class DataSaverLoader {
    public DataSaverLoader(JavaPlugin plugin) {
        this.filePath = plugin.getDataFolder() + "/ffregions.save";
        this.plugin = plugin;
    }

    public static void initialize(JavaPlugin plugin) {
        instance = new DataSaverLoader(plugin);
    }

    public static DataSaverLoader getInstance() {
        if (instance == null) {
            throw new IllegalStateException("The DataSaverLoader is not initialized.");
        }
        return instance;
    }

    public void saveDatabase() {
        try {
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new FileOutputStream(filePath));
            out.writeObject(Database.getInstance());
            out.close();
        }
        catch (IOException e) {
            plugin.getLogger().severe("Failed to save the FFRegions database.");
        }
    }

    public void loadDatabase() {
        if (!new File(filePath).exists()) {
            plugin.getLogger().info("The FFRegions save file does not exist. Using the empty database...");
            return;
        }

        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new FileInputStream(filePath));
            Database db = (Database)in.readObject();
            in.close();
            Database.setInstance(db);
            return;
        }
        catch (IOException e) {
            plugin.getLogger().severe("Failed to load the FFRegions save file.");
        }
        catch (ClassNotFoundException e) {
            plugin.getLogger().severe("Failed to load the FFRegions save file.");
        }
    }

    private static DataSaverLoader instance = null;
    private String filePath;
    private JavaPlugin plugin;
}