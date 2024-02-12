package com.kliminskyi.ffregions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

public class Region {
    public Region(String name, UUID ownerUUID) {
        this.name = name;
        this.ownerUUID = ownerUUID;
    }

    public void rename(String newName) {
        name = newName;
    }

    public String getName() {
        return name;
    }

    public void addChunk(Chunk chunk) {
        if (!isChunkClaimed(chunk)) {
            chunks.add(chunk);
        }
    }

    public List<Chunk> getChunks() {
        return Collections.unmodifiableList(chunks);
    }

    public boolean isChunkClaimed(Chunk chunk) {
        return chunks.contains(chunk);
    }

    public boolean isLocationClaimed(Location location) {
        return chunks.stream().anyMatch(c -> c.x == location.getChunk().getX() && c.z == location.getChunk().getZ());
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID newUUID) {
        ownerUUID = newUUID;
    }

    public List<UUID> getMembersUUID() {
        return Collections.unmodifiableList(membersUUID);
    }

    public void addMember(UUID newUUID) {
        membersUUID.add(newUUID);
    }

    public void removeMember(UUID UUIDToRemove) {
        membersUUID.remove(UUIDToRemove);
    }

    public boolean isMemberOrOwner(UUID playerUUID) {
        return ownerUUID.equals(playerUUID) || membersUUID.contains(playerUUID);
    }

    private String name;
    private ArrayList<Chunk> chunks = new ArrayList<Chunk>();
    private UUID ownerUUID;
    private ArrayList<UUID> membersUUID = new ArrayList<UUID>();
}
