package com.kliminskyi.ffregions;

import org.bukkit.plugin.java.JavaPlugin;

public class FFRegions extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("onEnable is called!");
        getCommand("mkrg").setExecutor(new CommandMkRg());
        getCommand("claim").setExecutor(new CommandClaim());
        getCommand("lsrg").setExecutor(new CommandLsRg());
        getServer().getPluginManager().registerEvents(new PlayerActionListener(), this);
    }
    @Override
    public void onDisable() {
        getLogger().info("onDisable is called!");
    }
}