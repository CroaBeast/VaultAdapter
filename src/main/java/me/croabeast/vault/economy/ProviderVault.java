package me.croabeast.vault.economy;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
final class ProviderVault implements EconomyProvider {

    private final Economy source;
    private final Plugin plugin;

    ProviderVault(Economy source, Plugin plugin) {
        this.source = Objects.requireNonNull(source, "source");
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    @NotNull
    public String getName() {
        return source.getName();
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public boolean isEnabled() {
        return source.isEnabled();
    }

    @Override
    public @NotNull String getCurrencyName(boolean single) {
        try {
            String currencyName = single ? source.currencyNameSingular() : source.currencyNamePlural();
            return currencyName == null || currencyName.isEmpty() ?
                    EconomyProvider.super.getCurrencyName(single) :
                    currencyName;
        } catch (Exception ignored) {
            return EconomyProvider.super.getCurrencyName(single);
        }
    }

    @Override
    public int getDecimals() {
        try {
            int decimals = source.fractionalDigits();
            return decimals < 0 ? EconomyProvider.super.getDecimals() : decimals;
        } catch (Exception ignored) {
            return EconomyProvider.super.getDecimals();
        }
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        try {
            return source.hasAccount(player);
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean createAccount(OfflinePlayer player) {
        try {
            return source.createPlayerAccount(player);
        } catch (Exception ignored) {
            return false;
        }
    }

    @NotNull
    public BigDecimal getBalance(OfflinePlayer player) {
        try {
            return BigDecimal.valueOf(source.getBalance(player));
        } catch (Exception ignored) {
            return BigDecimal.ZERO;
        }
    }

    @NotNull
    public Transaction withdraw(OfflinePlayer player, BigDecimal amount) {
        if (amount.signum() < 0)
            return new Transaction(amount, getBalance(player), Transaction.Type.WITHDRAW).setSuccessful(false)
                    .setReceiver(player);

        try {
            EconomyResponse response = source.withdrawPlayer(player, amount.doubleValue());
            return response(response, amount, Transaction.Type.WITHDRAW).setReceiver(player);
        } catch (Exception e) {
            return new Transaction(amount, getBalance(player), Transaction.Type.WITHDRAW).setSuccessful(false)
                    .setReceiver(player);
        }
    }

    @NotNull
    public Transaction deposit(OfflinePlayer player, BigDecimal amount) {
        if (amount.signum() < 0)
            return new Transaction(amount, getBalance(player), Transaction.Type.DEPOSIT).setSuccessful(false)
                    .setReceiver(player);

        try {
            EconomyResponse response = source.depositPlayer(player, amount.doubleValue());
            return response(response, amount, Transaction.Type.DEPOSIT).setReceiver(player);
        } catch (Exception e) {
            return new Transaction(amount, getBalance(player), Transaction.Type.DEPOSIT).setSuccessful(false)
                    .setReceiver(player);
        }
    }

    private Transaction response(EconomyResponse response, BigDecimal amount, Transaction.Type type) {
        if (response == null)
            return new Transaction(amount, BigDecimal.ZERO, type).setSuccessful(false);

        return new Transaction(amount, BigDecimal.valueOf(response.balance), type)
                .setSuccessful(response.transactionSuccess());
    }
}


