package com.github.anthropoworphous.blockingblock.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class BlockLocations {
    private Map<Location, Long> blockLocations = new HashMap<>();
    private transient int angle = 0;

    public void initLoop(Plugin p) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (blockLocations.size() > 300) {
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage("Warning, over 300"));
                }
                blockLocations.forEach((l, t) -> {
                    if (new Date(t).toInstant().isBefore(new Date().toInstant())) {
                        Bukkit.getLogger().info("block outta date");
                        blockLocations.remove(l);
                    } else {
                        int r = 3;
                        double radians = Math.toRadians(angle);
                        angle = (angle + 15) % 360;

                        double x = Math.cos(radians) * r;
                        double z = Math.sin(radians) * r;

                        l.getWorld().spawnParticle(
                                Particle.END_ROD,
                                new Location(
                                        l.getWorld(),
                                        l.getX() + x,
                                        l.getY(),
                                        l.getX() + z),
                                1
                        );
                        l.getWorld().spawnParticle(
                                Particle.END_ROD,
                                new Location(
                                        l.getWorld(),
                                        l.getX() - x,
                                        l.getY(),
                                        l.getX() - z),
                                1
                        );
                    }
                });
            }
        }.runTaskTimer(p, 0, 10);
    }

    public void addBlock(Location location) {
        blockLocations.put(
                location,
                new Date().toInstant().plus(1, ChronoUnit.DAYS).toEpochMilli()
        );
    }
    public boolean removeBlock(Location location) {
        return blockLocations.remove(location) != null;
    }

    public boolean blocked(Location location, int radius) {
        return blockLocations.keySet().stream().anyMatch(l -> l.distance(location) < radius);
    }

    // for json
    public Map<Location, Long> getBlockLocations() {
        return blockLocations;
    }
}
