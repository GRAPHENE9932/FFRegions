package com.kliminskyi.ffregions;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandClaim implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.getServer().getLogger().warning("Only players can send the mkregion command.");
            return false;
        }

        Player player = (Player)sender;

        if (args.length < 1 || args[0].isEmpty()) {
            player.sendMessage(
                String.format("%sPlease specify the region name.%s", ChatColor.YELLOW, ChatColor.RESET)
            );
            return false;
        }

        Optional<Region> region = Database.getInstance().getRegionByName(args[0]);
        if (region.isEmpty()) {
            player.sendMessage(
                String.format("%sThere is no region with a such name.%s", ChatColor.RED, ChatColor.RESET)
            );
            return false;
        }

        Chunk chunk = new Chunk(player.getLocation());

        if (Database.getInstance().isChunkClaimed(chunk)) {
            player.sendMessage(
                String.format("%sThis chunk is already claimed.%s", ChatColor.RED, ChatColor.RESET)
            );
            return false;
        }

        region.get().addChunk(chunk);
        player.sendMessage(
            String.format("%sChunk claimed.%s", ChatColor.GREEN, ChatColor.RESET)
        );
        return true;
    }
}
