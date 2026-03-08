package me.croabeast.vault.economy;

import net.milkbowl.vault2.economy.AccountPermission;
import net.milkbowl.vault2.economy.Economy;
import net.milkbowl.vault2.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

final class Vault2Bridge implements Economy {

    private static final String DEFAULT_CURRENCY = "default";
    private static final Collection<String> CURRENCIES = Collections.singleton(DEFAULT_CURRENCY);

    private final EconomyProvider backend;

    Vault2Bridge(EconomyProvider backend) {
        this.backend = Objects.requireNonNull(backend, "backend");
    }

    @Override
    public boolean isEnabled() {
        return backend.isEnabled();
    }

    @Override
    public @NotNull String getName() {
        return backend.getName();
    }

    @Override
    public boolean hasSharedAccountSupport() {
        return false;
    }

    @Override
    public boolean hasMultiCurrencySupport() {
        return false;
    }

    @Override
    public int fractionalDigits(@NotNull String pluginName) {
        return backend.getDecimals();
    }

    @Override
    public @NotNull String format(@NotNull BigDecimal amount) {
        return amount.toPlainString();
    }

    @Override
    public @NotNull String format(@NotNull String pluginName, @NotNull BigDecimal amount) {
        return amount.toPlainString();
    }

    @Override
    public @NotNull String format(@NotNull BigDecimal amount, @NotNull String currency) {
        return amount.toPlainString();
    }

    @Override
    public @NotNull String format(@NotNull String pluginName, @NotNull BigDecimal amount, @NotNull String currency) {
        return amount.toPlainString();
    }

    @Override
    public boolean hasCurrency(@NotNull String currency) {
        return DEFAULT_CURRENCY.equalsIgnoreCase(currency);
    }

    @Override
    public @NotNull String getDefaultCurrency(@NotNull String pluginName) {
        return DEFAULT_CURRENCY;
    }

    @Override
    public @NotNull String defaultCurrencyNamePlural(@NotNull String pluginName) {
        return backend.getCurrencyName(false);
    }

    @Override
    public @NotNull String defaultCurrencyNameSingular(@NotNull String pluginName) {
        return backend.getCurrencyName(true);
    }

    @Override
    public @NotNull Collection<String> currencies() {
        return CURRENCIES;
    }

    @Override
    public boolean createAccount(@NotNull UUID accountID, @NotNull String name) {
        return createAccount(accountID, name, true);
    }

    @Override
    public boolean createAccount(@NotNull UUID accountID, @NotNull String name, boolean player) {
        return backend.createAccount(player(accountID));
    }

    @Override
    public boolean createAccount(@NotNull UUID accountID, @NotNull String name, @NotNull String worldName) {
        return createAccount(accountID, name, true);
    }

    @Override
    public boolean createAccount(@NotNull UUID accountID, @NotNull String name, @NotNull String worldName, boolean player) {
        return backend.createAccount(player(accountID));
    }

    @Override
    public @NotNull Map<UUID, String> getUUIDNameMap() {
        return Collections.emptyMap();
    }

    @Override
    public Optional<String> getAccountName(@NotNull UUID accountID) {
        return Optional.empty();
    }

    @Override
    public boolean hasAccount(@NotNull UUID accountID) {
        return backend.hasAccount(player(accountID));
    }

    @Override
    public boolean hasAccount(@NotNull UUID accountID, @NotNull String worldName) {
        return backend.hasAccount(player(accountID));
    }

    @Override
    public boolean renameAccount(@NotNull UUID accountID, @NotNull String name) {
        return false;
    }

    @Override
    public boolean renameAccount(@NotNull String plugin, @NotNull UUID accountID, @NotNull String name) {
        return false;
    }

    @Override
    public boolean deleteAccount(@NotNull String plugin, @NotNull UUID accountID) {
        return false;
    }

    @Override
    public boolean accountSupportsCurrency(@NotNull String plugin, @NotNull UUID accountID, @NotNull String currency) {
        return hasCurrency(currency);
    }

    @Override
    public boolean accountSupportsCurrency(@NotNull String plugin, @NotNull UUID accountID, @NotNull String currency, @NotNull String world) {
        return hasCurrency(currency);
    }

    @Override
    public @NotNull BigDecimal getBalance(@NotNull String pluginName, @NotNull UUID accountID) {
        return backend.getBalance(player(accountID));
    }

    @Override
    public @NotNull BigDecimal getBalance(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String world) {
        return backend.getBalance(player(accountID));
    }

    @Override
    public @NotNull BigDecimal getBalance(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String world, @NotNull String currency) {
        return backend.getBalance(player(accountID));
    }

    @Override
    public boolean has(@NotNull String pluginName, @NotNull UUID accountID, @NotNull BigDecimal amount) {
        return backend.hasAmount(player(accountID), amount);
    }

    @Override
    public boolean has(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull BigDecimal amount) {
        return backend.hasAmount(player(accountID), amount);
    }

    @Override
    public boolean has(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull String currency, @NotNull BigDecimal amount) {
        return backend.hasAmount(player(accountID), amount);
    }

    @Override
    public @NotNull EconomyResponse withdraw(@NotNull String pluginName, @NotNull UUID accountID, @NotNull BigDecimal amount) {
        return response(backend.withdraw(player(accountID), amount));
    }

    @Override
    public @NotNull EconomyResponse withdraw(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull BigDecimal amount) {
        return response(backend.withdraw(player(accountID), amount));
    }

    @Override
    public @NotNull EconomyResponse withdraw(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull String currency, @NotNull BigDecimal amount) {
        return response(backend.withdraw(player(accountID), amount));
    }

    @Override
    public @NotNull EconomyResponse deposit(@NotNull String pluginName, @NotNull UUID accountID, @NotNull BigDecimal amount) {
        return response(backend.deposit(player(accountID), amount));
    }

    @Override
    public @NotNull EconomyResponse deposit(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull BigDecimal amount) {
        return response(backend.deposit(player(accountID), amount));
    }

    @Override
    public @NotNull EconomyResponse deposit(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull String currency, @NotNull BigDecimal amount) {
        return response(backend.deposit(player(accountID), amount));
    }

    @Override
    public boolean createSharedAccount(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String name, @NotNull UUID owner) {
        return false;
    }

    @Override
    public boolean isAccountOwner(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return false;
    }

    @Override
    public boolean setOwner(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return false;
    }

    @Override
    public boolean isAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return false;
    }

    @Override
    public boolean addAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return false;
    }

    @Override
    public boolean addAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid, @NotNull AccountPermission... initialPermissions) {
        return false;
    }

    @Override
    public boolean removeAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return false;
    }

    @Override
    public boolean hasAccountPermission(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid, @NotNull AccountPermission permission) {
        return false;
    }

    @Override
    public boolean updateAccountPermission(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid, @NotNull AccountPermission permission, boolean value) {
        return false;
    }

    private EconomyResponse response(Transaction tx) {
        EconomyResponse.ResponseType type = tx.isSuccessful() ?
                EconomyResponse.ResponseType.SUCCESS :
                EconomyResponse.ResponseType.FAILURE;

        return new EconomyResponse(tx.getAmount(), tx.getBalance(), type, "");
    }

    private OfflinePlayer player(UUID accountID) {
        return Bukkit.getOfflinePlayer(accountID);
    }
}


