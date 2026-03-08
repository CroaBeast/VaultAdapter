package me.croabeast.vault.economy;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

enum ProviderFallback implements EconomyProvider {
    INSTANCE;

    @NotNull
    public String getName() {
        return "None";
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return true;
    }

    @Override
    public boolean createAccount(OfflinePlayer player) {
        return false;
    }

    @NotNull
    public BigDecimal getBalance(OfflinePlayer player) {
        return BigDecimal.ZERO;
    }

    @NotNull
    public Transaction withdraw(OfflinePlayer player, BigDecimal amount) {
        return Transaction.failure(amount, Transaction.Type.WITHDRAW).setReceiver(player);
    }

    @NotNull
    public Transaction deposit(OfflinePlayer player, BigDecimal amount) {
        return Transaction.failure(amount, Transaction.Type.DEPOSIT).setReceiver(player);
    }
}


