package at.hugob.papermc.automaticbambooselling.manager;

import at.hugob.papermc.automaticbambooselling.config.Config;
import at.hugob.papermc.automaticbambooselling.plugin.AutomaticBambooSellingPlugin;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PayoutManager {
    private final static Sound payoutSound = Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_BELL, Sound.Source.NEUTRAL, 1, 1);
    private final static Map<UUID, PayoutInfo> payoutCache = new HashMap<>();
    private final AutomaticBambooSellingPlugin plugin;

    public PayoutManager(AutomaticBambooSellingPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean pickedUp(Player player, ItemStack itemStack) {
        Material itemMaterial = itemStack.getType();
        if (!Config.itemsThatCanBeSold().containsKey(itemMaterial)) return false;
        Config.ItemInfo itemInfo = Config.itemsThatCanBeSold().get(itemMaterial);
        int amount = itemStack.getAmount();

        double itemPrice = itemInfo.price();
        double payout = itemPrice * amount;

        var economyResponse = plugin.vault.economy.depositPlayer(player, payout);
        if (!economyResponse.transactionSuccess()) {
            plugin.getLogger().severe("Could not deposit the money!");
            return false;
        }
        itemStack.setAmount(0);
        updateActionBar(player, amount, payout, itemInfo);
        return true;
    }

    private void updateActionBar(Player player, int itemAmount, double payedOut, Config.ItemInfo itemInfo) {
        UUID playerId = player.getUniqueId();
        if (payoutCache.containsKey(playerId)) {
            var cached = payoutCache.get(playerId);
            cached.remover.cancel();
            payoutCache.remove(playerId);
            if (itemInfo.equals(cached.itemInfo)) {
                itemAmount += cached.count;
                payedOut += cached.payed;
            }
        }
        double finalPayedOut = payedOut;
        int finalItemAmount = itemAmount;
        player.sendActionBar(Config.actionBarText().replaceText(TextReplacementConfig.builder()
                .match("%([^ %]*)%")
                .replacement((matchResult, builder) -> switch (matchResult.group(1)) {
                    case "payout" -> builder.content(plugin.vault.economy.format(finalPayedOut));
                    case "item-count" -> builder.content(String.valueOf(finalItemAmount));
                    case "item" -> builder.content("").append(itemInfo.displayName());
                    default -> builder;
                })
                .build()));
        player.playSound(payoutSound);
        payoutCache.put(playerId, new PayoutInfo(itemInfo, itemAmount, payedOut,
                Bukkit.getScheduler().runTaskLater(plugin, () -> payoutCache.remove(playerId),60)
        ));
    }

    private record PayoutInfo(Config.ItemInfo itemInfo, int count, double payed, BukkitTask remover) {
    }
}
