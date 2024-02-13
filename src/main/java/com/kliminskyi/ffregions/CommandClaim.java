package com.kliminskyi.ffregions;

import java.util.List;
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
            List<Region> playersRegions = Database.getInstance().getRegionsPlayerOwns(player);
            if (playersRegions.isEmpty()) {
                player.sendMessage(
                    String.format("%sYou don't own any regions.%s", ChatColor.RED, ChatColor.RESET)
                );
                return false;
            }
            else if (playersRegions.size() >= 2) {
                player.sendMessage(
                    String.format("%sAmbiguous region. Please specify its name.%s", ChatColor.RED, ChatColor.RESET)
                );
                return false;
            }
            
            return claimChunkForRegion(player, playersRegions.get(0));
        }

        Optional<Region> region = Database.getInstance().getRegionByName(args[0]);
        if (region.isEmpty()) {
            player.sendMessage(
                String.format("%sThere is no region with a such name.%s", ChatColor.RED, ChatColor.RESET)
            );
            return false;
        }

        return claimChunkForRegion(player, region.get());
    }

    private boolean claimChunkForRegion(Player player, Region region) {
        Chunk chunk = new Chunk(player.getLocation());

        if (Database.getInstance().isChunkClaimed(chunk)) {
            player.sendMessage(
                String.format("%sThis chunk is already claimed.%s", ChatColor.RED, ChatColor.RESET)
            );
            return false;
        }

        region.addChunk(chunk);
        player.sendMessage(
            String.format(
                "%sChunk claimed for %s\"%s\"%s%s.%s", ChatColor.GREEN, ChatColor.BOLD, region.getName(), ChatColor.RESET, ChatColor.GREEN, ChatColor.RESET
            )
        );
        return true;
    }
}
