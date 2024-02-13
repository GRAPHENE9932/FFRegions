package com.kliminskyi.ffregions;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerActionListener implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Optional<Region> regionFrom = Database.getInstance().getRegionByLocation(e.getFrom());
        Optional<Region> region = Database.getInstance().getRegionByLocation(e.getTo());

        if (regionFrom.equals(region)) {
            return;
        }

        if (region.isEmpty()) {
            e.getPlayer().sendTitle(
                String.format("%sUntaken territory%s", ChatColor.GRAY, ChatColor.RESET),
                String.format("%sPublic land%s", ChatColor.DARK_GRAY, ChatColor.RESET),
                1,
                1,
                60
            );
        }
        else if (!region.get().isMemberOrOwner(e.getPlayer().getUniqueId())) {
            e.getPlayer().sendTitle(
                String.format("%s%s%s%s", ChatColor.RED, ChatColor.BOLD, region.get().getName(), ChatColor.RESET),
                String.format("%sPrivate land%s", ChatColor.RED, ChatColor.RESET),
                1,
                1,
                60
            );
        }
        else {
            e.getPlayer().sendTitle(
                String.format("%s%s%s%s", ChatColor.GREEN, ChatColor.BOLD, region.get().getName(), ChatColor.RESET),
                String.format("%sPrivate land%s", ChatColor.GREEN, ChatColor.RESET),
                1,
                1,
                60
            );
        }
    }
}
