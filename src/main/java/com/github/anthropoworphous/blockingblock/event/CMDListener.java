package com.github.anthropoworphous.blockingblock.event;

import com.github.anthropoworphous.blockingblock.BlockingBlock;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CMDListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Bukkit.getLogger().info(event.getMessage());
        if (BlockingBlock.getBlockLocations().blocked(event.getPlayer().getLocation(), 10)) {
            if (BlockingBlock
                    .getPlugin()
                    .getConfig()
                    .getStringList("blocked_commands")
                    .stream()
                    .anyMatch(s -> event.getMessage().startsWith('/'+s))
            ) {
                event.getPlayer().sendMessage("Command blocked by blocking block");
                event.setCancelled(true);
            }
        }
    }
}
