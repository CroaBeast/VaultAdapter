package me.croabeast.vault;

import lombok.Getter;
import net.milkbowl.vault2.economy.Economy;
import net.milkbowl.vault2.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
final class Economy2 implements EconomyAdapter<Economy> {

    private final Economy source;

    Economy2() {
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
            return source.balance(source.getName(), player.getUniqueId()).doubleValue();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean withdraw(OfflinePlayer player, double amount) {
        if (amount < 0.0) return false;

        try {
            return source.withdraw(source.getName(), player.getUniqueId(), new BigDecimal(amount)).type == EconomyResponse.ResponseType.SUCCESS;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deposit(OfflinePlayer player, double amount) {
        if (amount < 0.0) return false;

        try {
            return source.deposit(source.getName(), player.getUniqueId(), new BigDecimal(amount)).type == EconomyResponse.ResponseType.SUCCESS;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "ChatAdapter{provider='Vault', plugin='" + source.getName() + "'}";
    }
}
