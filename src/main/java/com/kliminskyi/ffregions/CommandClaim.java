package com.kliminskyi.ffregions;

import java.util.Optional;

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
            player.sendMessage("Please specify the region name.");
            return false;
        }

        Optional<Region> region = Database.getInstance().getRegionByName(args[0]);
        if (region.isEmpty()) {
            player.sendMessage("There is no region with a such name.");
            return false;
        }

        Chunk chunk = new Chunk(player.getLocation());

        if (Database.getInstance().isChunkClaimed(chunk)) {
            player.sendMessage("This chunk is already claimed.");
            return false;
        }

        region.get().addChunk(chunk);
        player.sendMessage("Chunk claimed.");
        return true;
    }
}
