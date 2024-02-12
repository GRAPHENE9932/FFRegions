package com.kliminskyi.ffregions;

import java.util.Optional;

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
            e.getPlayer().sendTitle("Untaken territory", "Public land", 1, 1, 60);
        }
        else {
            e.getPlayer().sendTitle(region.get().getName(), "Private land", 1, 1, 60);
        }
    }
}
