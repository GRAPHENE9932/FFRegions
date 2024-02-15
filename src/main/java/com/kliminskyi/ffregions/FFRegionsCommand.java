package com.kliminskyi.ffregions;

import java.util.List;
import java.util.Optional;

import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

public interface FFRegionsCommand extends CommandExecutor {
    List<String> getCompletes(Optional<Player> player, String[] args);
} 
