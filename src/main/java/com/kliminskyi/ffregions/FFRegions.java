package com.kliminskyi.ffregions;

import org.bukkit.plugin.java.JavaPlugin;

public class FFRegions extends JavaPlugin {
    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        DataSaverLoader.initialize(this);
        DataSaverLoader.getInstance().loadDatabase();

        CommandFFRg commandFFRg = new CommandFFRg();
        getCommand("ffrg").setExecutor(commandFFRg);
        getServer().getPluginManager().registerEvents(new PlayerActionListener(), this);
        getServer().getPluginManager().registerEvents((CommandShow)commandFFRg.getCommand("show"), this);
    }
    @Override
    public void onDisable() {
        DataSaverLoader.getInstance().saveDatabase();
    }
}