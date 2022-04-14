package com.github.anthropoworphous.blockingblock.event;

import com.github.anthropoworphous.blockingblock.BlockingBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBroken implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation().toCenterLocation();
        if (BlockingBlock.getBlockLocations().removeBlock(loc)) { // side effect: remove block from list
            Bukkit.getLogger().info("block broken");
            event.setCancelled(true);
            loc.getWorld().dropItemNaturally(loc, BlockingBlock.getBlockItem());
            loc.getBlock().setType(Material.AIR);
        }
    }
}
