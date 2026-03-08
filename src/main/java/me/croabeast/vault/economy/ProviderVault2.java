package me.croabeast.vault.economy;

import lombok.Getter;
import net.milkbowl.vault2.economy.Economy;
import net.milkbowl.vault2.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
final class ProviderVault2 implements EconomyProvider {

    private final Economy source;

    ProviderVault2(Economy source) {
        this.source = Objects.requireNonNull(source, "source");
    }

    @NotNull
    public String getName() {
        return source.getName();
    }

    @Override
    public boolean isEnabled() {
        return source.isEnabled();
    }

    @Override
    public @NotNull String getCurrencyName(boolean single) {
        try {
            String pluginName = source.getName();
            String currencyName = single ?
                    source.defaultCurrencyNameSingular(pluginName) :
                    source.defaultCurrencyNamePlural(pluginName);

            return currencyName.isEmpty() ?
                    EconomyProvider.super.getCurrencyName(single) :
                    currencyName;
        } catch (Exception ignored) {
            return EconomyProvider.super.getCurrencyName(single);
        }
    }

    @Override
    public int getDecimals() {
        try {
            int decimals = source.fractionalDigits(source.getName());
            return decimals < 0 ? EconomyProvider.super.getDecimals() : decimals;
        } catch (Exception ignored) {
            return EconomyProvider.super.getDecimals();
        }
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        try {
            return source.hasAccount(uuid(player));
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean createAccount(OfflinePlayer player) {
        String name = player.getName();
        String resolvedName = name == null || name.isEmpty() ? uuid(player).toString() : name;

        try {
            return source.createAccount(uuid(player), resolvedName, true);
        } catch (Exception ignored) {
            return false;
        }
    }

    @NotNull
    public BigDecimal getBalance(OfflinePlayer player) {
        try {
            return source.balance(source.getName(), uuid(player));
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
            EconomyResponse response = source.withdraw(source.getName(), uuid(player), amount);
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
            EconomyResponse response = source.deposit(source.getName(), uuid(player), amount);
            return response(response, amount, Transaction.Type.DEPOSIT).setReceiver(player);
        } catch (Exception e) {
            return new Transaction(amount, getBalance(player), Transaction.Type.DEPOSIT).setSuccessful(false)
                    .setReceiver(player);
        }
    }

    private Transaction response(EconomyResponse response, BigDecimal amount, Transaction.Type type) {
        if (response == null)
            return new Transaction(amount, BigDecimal.ZERO, type).setSuccessful(false);

        return new Transaction(amount, response.balance, type)
                .setSuccessful(response.transactionSuccess());
    }

    private UUID uuid(OfflinePlayer player) {
        return player.getUniqueId();
    }
}


