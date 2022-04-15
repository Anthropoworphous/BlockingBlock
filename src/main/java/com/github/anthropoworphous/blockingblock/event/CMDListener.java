package com.github.anthropoworphous.blockingblock.event;

import com.github.anthropoworphous.blockingblock.BlockingBlock;
import com.github.anthropoworphous.blockingblock.data.BlockLocations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.util.Vector;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class CMDListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (BlockingBlock
                .getPlugin()
                .getConfig()
                .getStringList("blocked_commands")
                .stream()
                .noneMatch(s -> event.getMessage().startsWith('/'+s))
        ) { return; }

        List<Location> blockedBy = BlockingBlock.getBlockLocations().blocked(
                event.getPlayer().getLocation(),
                10
        );

        if (blockedBy.isEmpty()) { return; }

        Map<Location, Long> blockLocations = BlockingBlock.getBlockLocations().getBlockLocations();

        for (Location l : blockedBy) {
            long t = blockLocations.get(l);
            if (new Date(t).toInstant().isBefore(new Date().toInstant())) {
                blockLocations.remove(l);
            }
            Location ploc = event.getPlayer().getEyeLocation();
            Vector v = new Vector(
                    l.getX() - ploc.getX(),
                    l.getY() - ploc.getY(),
                    l.getZ() - ploc.getZ()
            );
            int i = (int) v.length();
            v.normalize();

            for (; i > 0; i--) {
                ploc.getWorld().spawnParticle(Particle.BARRIER, ploc.add(v), 1);
            }
        }

        event.getPlayer().sendMessage(
                Component.text()
                        .append(
                                Component.text("Command blocked by blocking block")
                                        .color(TextColor.color(255, 0, 0)))
                        .build()
        );

        event.setCancelled(true);
    }
}
