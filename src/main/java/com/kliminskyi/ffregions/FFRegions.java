package com.kliminskyi.ffregions;

import org.bukkit.plugin.java.JavaPlugin;

public class FFRegions extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("onEnable is called!");
        getCommand("mkregion").setExecutor(new CommandMkRegion());
        getCommand("claim").setExecutor(new CommandClaim());
        getServer().getPluginManager().registerEvents(new PlayerActionListener(), this);
    }
    @Override
    public void onDisable() {
        getLogger().info("onDisable is called!");
    }
}