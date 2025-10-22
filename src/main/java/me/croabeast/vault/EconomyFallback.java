package me.croabeast.vault;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

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
    public double getBalance(OfflinePlayer player) {
        return 0;
    }

    @Override
    public boolean withdraw(OfflinePlayer player, double amount) {
        return false;
    }

    @Override
    public boolean deposit(OfflinePlayer player, double amount) {
        return false;
    }
}
