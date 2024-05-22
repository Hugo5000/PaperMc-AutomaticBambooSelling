package at.hugob.papermc.automaticbambooselling.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumMap;

public class Config {
    private static Component actionBarText = Component.text("not-initialized");
    private static EnumMap<Material, ItemInfo> itemsThatCanBeSold = new EnumMap<>(Material.class);

    private Config() {
    }

    public static void update(JavaPlugin plugin) {
        actionBarText = MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("sold-message"));
        var itemsThatCanBeSold = new EnumMap<Material, ItemInfo>(Material.class);
        var itemsSection = plugin.getConfig().getConfigurationSection("items");
        for (String itemKey : itemsSection.getKeys(false)) {
            final Material material = Material.matchMaterial(itemKey);
            final var itemSection = itemsSection.getConfigurationSection(itemKey);
            final double price = itemSection.getDouble("price");
            final String displayString = itemSection.getString("display", "");
            final Component component;
            if (displayString.isBlank()) {
                component = new ItemStack(material).displayName();
            } else {
                component = MiniMessage.miniMessage().deserialize(displayString);
            }
            itemsThatCanBeSold.put(material, new ItemInfo(price, component));
        }
        Config.itemsThatCanBeSold = itemsThatCanBeSold;
    }

    public static Component actionBarText() {
        return actionBarText;
    }

    public static EnumMap<Material, ItemInfo> itemsThatCanBeSold() {
        return itemsThatCanBeSold;
    }

    public record ItemInfo(double price, Component displayName) {
    }
}
