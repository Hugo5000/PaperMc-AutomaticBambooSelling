package at.hugob.papermc.automaticbambooselling.listener;

import at.hugob.papermc.automaticbambooselling.plugin.AutomaticBambooSellingPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class PlayerItemPickupListener implements Listener {
    private final AutomaticBambooSellingPlugin plugin;

    public PlayerItemPickupListener(AutomaticBambooSellingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerItemPickup(EntityPickupItemEvent event) {
        if(!(event.getEntity() instanceof Player player)) return;
        boolean shouldRemove = plugin.payoutManager.pickedUp(player, event.getItem().getItemStack());
        if(shouldRemove) {
            event.getItem().setItemStack(new ItemStack(Material.AIR));
            event.getItem().remove();
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerPickupItemFromContainer(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player player)) return;
        if(event.getView().getType() == InventoryType.CREATIVE) return;

        {
            ItemStack itemStack = event.getCurrentItem();
            if(itemStack == null) return;
            boolean shouldRemove = plugin.payoutManager.pickedUp(player, itemStack);
            if (shouldRemove) {
                event.setCurrentItem(new ItemStack(Material.AIR));
                itemStack.setAmount(0);
                event.setCancelled(true);
            }
        }
    }
}
