package com.github.anthropoworphous.blockingblock.data;

import org.bukkit.Location;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockLocations {
    public BlockLocations(Map<Location, Long> blockLocations) {
        if (blockLocations == null || blockLocations.isEmpty()) {
            this.blockLocations = new HashMap<>();
        } else {
            this.blockLocations = blockLocations;
        }
    }
    public BlockLocations() {
        blockLocations = new HashMap<>();
    }

    private final Map<Location, Long> blockLocations;

    public void addBlock(Location location) {
        blockLocations.put(
                location,
                new Date().toInstant().plus(1, ChronoUnit.DAYS).toEpochMilli()
        );
    }
    public boolean removeBlock(Location location) {
        return blockLocations.remove(location) != null;
    }

    public List<Location> blocked(Location location, int radius) {
        return blockLocations.keySet()
                .stream()
                .filter(l -> l.distance(location) < radius)
                .toList();
    }

    public Map<Location, Long> getBlockLocations() {
        return blockLocations;
    }
}
