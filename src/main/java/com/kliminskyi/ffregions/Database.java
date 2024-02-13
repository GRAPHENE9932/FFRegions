package com.kliminskyi.ffregions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Database {
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public void addRegion(Region region) {
        if (getRegionByName(region.getName()).isPresent()) {
            throw new IllegalArgumentException("Failed to add a region because a region with the same name already exists.");
        }

        regions.add(region);
    }

    public Optional<Region> getRegionByName(String name) {
        return regions.stream().filter(r -> r.getName().equals(name)).findAny();
    }

    public boolean isChunkClaimed(Chunk chunk) {
        return regions.stream().anyMatch(r -> r.isChunkClaimed(chunk));
    }

    public boolean isLocationClaimed(Location location) {
        return regions.stream().anyMatch(r -> r.isLocationClaimed(location));
    }

    public Optional<Region> getRegionByChunk(Chunk chunk) {
        return regions.stream().filter(r -> r.isChunkClaimed(chunk)).findAny();
    }

    public Optional<Region> getRegionByLocation(Location location) {
        return regions.stream().filter(r -> r.isLocationClaimed(location)).findAny();
    }

    public List<Region> getRegionsPlayerOwns(Player player) {
        return regions.stream().filter(r -> r.getOwnerUUID() == player.getUniqueId()).toList();
    }

    private static Database instance = null;
    private ArrayList<Region> regions = new ArrayList<Region>();
}
