package com.kliminskyi.ffregions;

import java.util.List;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCreate implements FFRegionsCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player)sender;

        if (args.length < 1 || args[0].isEmpty()) {
            player.sendMessage(
                String.format("%sPlease specify the region name.%s", ChatColor.YELLOW, ChatColor.RESET)
            );
            return false;
        }

        if (Database.getInstance().getRegionByName(args[0]).isPresent()) {
            player.sendMessage(
                String.format("A region with the same name already exists.", ChatColor.RED, ChatColor.RESET)
            );
            return false;
        }

        Region region = new Region(args[0], player.getUniqueId());
        Database.getInstance().addRegion(region);

        player.sendMessage(
            String.format(
                "%sThe %s\"%s\"%s%s region has been created.%s",
                ChatColor.GREEN, ChatColor.BOLD,
                region.getName(), ChatColor.RESET,
                ChatColor.GREEN, ChatColor.RESET
            )
        );
        return true;
    }

    @Override
    public List<String> getCompletes(Optional<Player> player, String[] args) {
        return List.of();
    }
}
