package me.croabeast.vault.economy;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@SuppressWarnings("deprecation")
final class VaultBridge extends AbstractEconomy {

    private final EconomyProvider backend;

    VaultBridge(EconomyProvider backend) {
        this.backend = Objects.requireNonNull(backend, "backend");
    }

    @Override
    public boolean isEnabled() {
        return backend.isEnabled();
    }

    @Override
    public String getName() {
        return backend.getName();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return backend.getDecimals();
    }

    @Override
    public String format(double amount) {
        return String.format(Locale.US, "%.2f", amount);
    }

    @Override
    public String currencyNamePlural() {
        return backend.getCurrencyName(false);
    }

    @Override
    public String currencyNameSingular() {
        return backend.getCurrencyName(true);
    }

    @Override
    public boolean hasAccount(String playerName) {
        OfflinePlayer player = player(playerName);
        return player != null && backend.hasAccount(player);
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public double getBalance(String playerName) {
        OfflinePlayer player = player(playerName);
        return player == null ? 0.0 : backend.getBalance(player).doubleValue();
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, double amount) {
        OfflinePlayer player = player(playerName);
        return player != null && backend.hasAmount(player, BigDecimal.valueOf(amount));
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        OfflinePlayer player = player(playerName);
        if (player == null)
            return response(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "Unknown account");

        Transaction tx = backend.withdraw(player, BigDecimal.valueOf(amount));
        return response(tx);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        OfflinePlayer player = player(playerName);
        if (player == null)
            return response(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "Unknown account");

        Transaction tx = backend.deposit(player, BigDecimal.valueOf(amount));
        return response(tx);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return notImplemented();
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return notImplemented();
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return notImplemented();
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return notImplemented();
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return notImplemented();
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return notImplemented();
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return notImplemented();
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return notImplemented();
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        OfflinePlayer player = player(playerName);
        return player != null && backend.createAccount(player);
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }

    private OfflinePlayer player(String playerName) {
        if (playerName == null || playerName.isEmpty())
            return null;
        return Bukkit.getOfflinePlayer(playerName);
    }

    private EconomyResponse notImplemented() {
        return response(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    }

    private EconomyResponse response(Transaction tx) {
        EconomyResponse.ResponseType type = tx.isSuccessful() ?
                EconomyResponse.ResponseType.SUCCESS :
                EconomyResponse.ResponseType.FAILURE;

        return response(tx.getAmount().doubleValue(), tx.getBalance().doubleValue(), type, "");
    }

    private EconomyResponse response(double amount, double balance, EconomyResponse.ResponseType type, String errorMessage) {
        return new EconomyResponse(amount, balance, type, errorMessage == null ? "" : errorMessage);
    }
}


