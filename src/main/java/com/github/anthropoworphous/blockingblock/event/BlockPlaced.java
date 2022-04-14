package com.github.anthropoworphous.blockingblock.event;

import com.github.anthropoworphous.blockingblock.BlockingBlock;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaced implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().isSimilar(BlockingBlock.getBlockItem())) {
            Bukkit.getLogger().info("block placed");
            BlockingBlock.getBlockLocations().addBlock(event.getBlock().getLocation().toCenterLocation());
        }
    }
}
