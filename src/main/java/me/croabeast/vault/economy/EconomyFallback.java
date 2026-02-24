package me.croabeast.vault.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

final class EconomyFallback implements EconomyAdapter<Object> {

    @NotNull
    public Object getSource() {
        throw new IllegalStateException("No source was found");
    }

    @Override
    public Plugin getPlugin() {
        return null;
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

    @NotNull
    public Transaction set(OfflinePlayer player, BigDecimal amount) {
        return Transaction.failure(amount, Transaction.Type.SET).setReceiver(player);
    }

    @NotNull
    public Transaction transfer(CommandSender sender, OfflinePlayer receiver, BigDecimal amount) {
        return Transaction.failure(amount, Transaction.Type.TRANSFER).setSender(sender).setReceiver(receiver);
    }
}
