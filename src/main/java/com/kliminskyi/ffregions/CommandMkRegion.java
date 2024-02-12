package com.kliminskyi.ffregions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMkRegion implements CommandExecutor {
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

        if (Database.getInstance().getRegionByName(args[0]).isPresent()) {
            player.sendMessage("A region with the same name already exists.");
            return false;
        }

        Region region = new Region(args[0], player.getUniqueId());
        Database.getInstance().addRegion(region);
        return true;
    }
}
