package com.kliminskyi.ffregions;

import java.io.Serializable;

import org.bukkit.Location;

public class Chunk implements Serializable {
    Chunk() {

    }

    Chunk(Location location) {
        x = location.getChunk().getX();
        z = location.getChunk().getZ();
    }

    public int x;
    public int z;

    public boolean equals(Chunk chunk) {
        return x == chunk.x && z == chunk.z;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != other.getClass()) {
            return false;
        }

        return equals((Chunk)other);
    }
}
