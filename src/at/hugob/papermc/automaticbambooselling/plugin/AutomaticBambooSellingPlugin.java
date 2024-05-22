package at.hugob.papermc.automaticbambooselling.plugin;

import at.hugob.papermc.automaticbambooselling.listener.PlayerItemPickupListener;
import at.hugob.papermc.automaticbambooselling.manager.PayoutManager;
import at.hugob.papermc.automaticbambooselling.config.Config;
import at.hugob.papermc.automaticbambooselling.provider.VaultAPIProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class AutomaticBambooSellingPlugin extends JavaPlugin {
    public VaultAPIProvider vault;
    public PayoutManager payoutManager;
    @Override
    public void onEnable() {
        reloadConfig();

        if(VaultAPIProvider.isVaultNotInstalled()) {
            getLogger().severe("Vault is not installed, disabling");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        vault = new VaultAPIProvider();
        payoutManager = new PayoutManager(this);

        getServer().getPluginManager().registerEvents(new PlayerItemPickupListener(this), this);
    }

    @Override
    public void reloadConfig() {
        saveDefaultConfig();
        super.reloadConfig();
        Config.update(this);
    }
}
