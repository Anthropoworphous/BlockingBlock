package com.github.anthropoworphous.blockingblock.command;

import com.github.anthropoworphous.blockingblock.BlockingBlock;
import com.github.anthropoworphous.cmdlib.arg.analyst.ArgsAnalyst;
import com.github.anthropoworphous.cmdlib.arg.route.IRoute;
import com.github.anthropoworphous.cmdlib.cmd.annotation.Command;
import com.github.anthropoworphous.cmdlib.cmd.implementation.CMD;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Command
public class TestCMD extends CMD {
    @Override
    public Boolean execute(CommandSender commandSender, ArgsAnalyst argsAnalyst) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        ((InventoryHolder) commandSender).getInventory().addItem(BlockingBlock.getBlockItem());
        return true;
    }

    @Override
    public @Nullable List<IRoute> cmdRoutes() {
        return null;
    }

    @Override
    public @NotNull String cmdName() {
        return "give-item";
    }
}
