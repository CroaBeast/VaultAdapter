package me.croabeast.vault;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

@Getter
final class EconomyImpl implements EconomyAdapter<Economy> {

    private final Economy source;

    EconomyImpl() {
        source = Objects.requireNonNull(Bukkit.getServicesManager().getRegistration(Economy.class)).getProvider();
    }

    @Override
    public Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin(source.getName());
    }

    @Override
    public boolean isEnabled() {
        return source.isEnabled();
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        try {
            return source.getBalance(player);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean withdraw(OfflinePlayer player, double amount) {
        if (amount < 0.0) return false;

        try {
            return source.withdrawPlayer(player, amount).type == EconomyResponse.ResponseType.SUCCESS;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deposit(OfflinePlayer player, double amount) {
        if (amount < 0.0) return false;

        try {
            return source.depositPlayer(player, amount).type == EconomyResponse.ResponseType.SUCCESS;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "ChatAdapter{provider='Vault', plugin='" + source.getName() + "'}";
    }
}
