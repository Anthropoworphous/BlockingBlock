package com.github.anthropoworphous.blockingblock;

import com.github.anthropoworphous.blockingblock.command.TestCMD;
import com.github.anthropoworphous.blockingblock.data.BlockLocations;
import com.github.anthropoworphous.blockingblock.event.BlockBroken;
import com.github.anthropoworphous.blockingblock.event.BlockPlaced;
import com.github.anthropoworphous.blockingblock.event.CMDListener;
import com.github.anthropoworphous.cmdlib.processor.CMDRegister;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getPluginManager;

public final class BlockingBlock extends JavaPlugin {
    private static Plugin p = null;
    private static BlockLocations bl = null;
    private static ItemStack i = null;

    public static Plugin getPlugin() { return p; }
    public static BlockLocations getBlockLocations() {
        if (bl == null) { bl = getLocations(); }
        return bl;
    }
    public static ItemStack getBlockItem() {
        if (i == null) { throw new NullPointerException(); }
        return i;
    }

    @Override
    public void onEnable() {
        p = this;

        registerRecipe();

        saveDefaultConfig();
        reloadConfig();

        getPluginManager().registerEvents(new CMDListener(), this);
        getPluginManager().registerEvents(new BlockBroken(), this);
        getPluginManager().registerEvents(new BlockPlaced(), this);

        CMDRegister.registerCMD(new TestCMD(), this);
    }

    @Override
    public void onDisable() {
        saveLocations(bl);
    }

    private static File getLocationFile() {
        Path p = getPlugin().getDataFolder().toPath();
        //noinspection ResultOfMethodCallIgnored
        p.toFile().mkdirs();
        return p.resolve("locations.json").toFile();
    }

    private static BlockLocations getLocations() {
        File f = getLocationFile();
        Gson g = new GsonBuilder().registerTypeAdapter(
                BlockLocations.class,
                (JsonDeserializer<BlockLocations>) (json, typeOfT, context) -> {
                    Map<Location, Long> map = new HashMap<>();
                    json.getAsJsonArray().forEach(obj -> {
                        JsonObject o2 = obj.getAsJsonObject();
                        JsonObject o1 = o2.get("location").getAsJsonObject();
                        map.put(
                                new Location(
                                    Bukkit.getWorld(o1.get("world").getAsString()),
                                    o1.get("x").getAsDouble(),
                                    o1.get("y").getAsDouble(),
                                    o1.get("z").getAsDouble()
                                ),
                                o2.get("time").getAsLong()
                        );
                    });
                    Bukkit.getLogger().info("recovered %d blocks".formatted(map.size()));
                    return map.size() == 0 ? new BlockLocations() : new BlockLocations(map);
                }
        ).create();

        try (Reader fr = new FileReader(f)) {
            return f.createNewFile() ? new BlockLocations() : g.fromJson(fr, BlockLocations.class);
        } catch (Exception ignored) {
            return new BlockLocations();
        }
    }

    private static void saveLocations(BlockLocations locations) {
        File f = getLocationFile();
        Gson g = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(
                BlockLocations.class,
                (JsonSerializer<BlockLocations>) (obj, typeOfT, context) -> {
                    JsonArray a = new JsonArray();
                    obj.getBlockLocations().forEach((l, t) -> {
                        JsonObject o1 = new JsonObject(), o2 = new JsonObject();
                        o1.add("world", new JsonPrimitive(l.getWorld().getName()));
                        o1.add("x", new JsonPrimitive(l.getX()));
                        o1.add("y", new JsonPrimitive(l.getY()));
                        o1.add("z", new JsonPrimitive(l.getZ()));
                        o2.add("time", new JsonPrimitive(t));
                        o2.add("location", o1);
                        a.add(o2);
                    });
                    return a;
                }
        ).create();

        try (Writer fw = new FileWriter(f)) {
            Bukkit.getLogger().info(g.toJson(locations));
            fw.write(g.toJson(locations));
            fw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerRecipe() {
        i = new ItemStack(Material.CRYING_OBSIDIAN);
        i.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        ItemMeta m = i.getItemMeta();
        m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        m.setCustomModelData(1);
        m.displayName(
                Component.text()
                        .append(Component.text("Blocking block"))
                        .color(TextColor.color(255, 0, 0))
                        .build()
        );
        m.lore(List.of(
                Component.text()
                        .append(Component.text("Blocks usage of some commands"))
                        .color(TextColor.color(255, 0, 0))
                        .build()
        ));
        i.setItemMeta(m);

        ShapedRecipe r = new ShapedRecipe(
                new NamespacedKey(this, "blocking_block"),
                i
        );

        r.shape(
                "121",
                "232",
                "121")
                .setIngredient('1', Material.END_CRYSTAL)
                .setIngredient('2', Material.END_ROD)
                .setIngredient('3', Material.NETHER_STAR);
        getServer().addRecipe(r);
    }


}
