package at.hugob.papermc.automaticbambooselling.provider;

import at.hugob.papermc.automaticbambooselling.provider.exception.VaultEconomySetupException;
import at.hugob.papermc.automaticbambooselling.provider.exception.VaultNotInstalledException;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultAPIProvider {
    public final Economy economy;

    public VaultAPIProvider() {
        if (isVaultNotInstalled()) {
            throw new VaultNotInstalledException("Vault is not installed");
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            throw new VaultEconomySetupException("No Vault Economy Set up!");
        }
        this.economy = rsp.getProvider();
    }

    public static boolean isVaultNotInstalled() {
        return Bukkit.getServer().getPluginManager().getPlugin("Vault") == null;
    }
}
