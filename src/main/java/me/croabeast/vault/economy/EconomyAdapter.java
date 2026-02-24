package me.croabeast.vault.economy;

import me.croabeast.vault.BaseAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Minimal, provider-agnostic facade for economy operations (balance, deposit, withdraw).
 *
 * <p>Implementations typically delegate to the <em>Vault</em> or
 * <em>VaultUnlocked</em> economy service. If neither is present,
 * {@link #create()} returns a safe fallback that reports zero balances.</p>
 *
 * <pre>{@code
 * // Example:
 * EconomyAdapter<?> eco = EconomyAdapter.create();
 * if (eco.isEnabled() && eco.hasAmount(player, 250.0)) {
 *     eco.withdraw(player, 250.0);
 *     // give the player an item, etc.
 * }
 * }</pre>
 *
 * @param <T> the underlying economy provider type (e.g., Vault Economy)
 * @since 1.1
 */
public interface EconomyAdapter<T> extends BaseAdapter<T> {

    /**
     * Returns the current account balance for the given player.
     * <p>
     * Implementations should never throw; if the account does not exist or no
     * provider is present, {@link BigDecimal#ZERO} is a reasonable return value.
     * </p>
     *
     * @param player the account owner (offline-safe)
     * @return the current balance (non-negative)
     */
    @NotNull
    BigDecimal getBalance(OfflinePlayer player);

    /**
     * Checks whether an account already exists for the given player.
     *
     * @param player the account owner
     * @return {@code true} if an account exists
     */
    boolean hasAccount(OfflinePlayer player);

    /**
     * Attempts to create an account for the given player.
     *
     * @param player the account owner
     * @return {@code true} if the account was created successfully
     */
    boolean createAccount(OfflinePlayer player);

    /**
     * Convenience check for whether a player has at least {@code amount}.
     * <p>
     * Implemented as: {@code amount.compareTo(getBalance(player)) <= 0}.
     * </p>
     *
     * @param player the account owner
     * @param amount the required amount (expected &ge; 0)
     * @return {@code true} if the balance is sufficient
     */
    default boolean hasAmount(OfflinePlayer player, BigDecimal amount) {
        return amount.compareTo(getBalance(player)) <= 0;
    }

    /**
     * Convenience check for whether a player has at least {@code amount}.
     * <p>
     * Implemented as: {@code amount <= getBalance(player)}.
     * </p>
     *
     * @param player the account owner
     * @param amount the required amount (expected &ge; 0)
     * @return {@code true} if the balance is sufficient
     */
    default boolean hasAmount(OfflinePlayer player, double amount) {
        return hasAmount(player, new BigDecimal(amount));
    }

    /**
     * Attempts to withdraw {@code amount} from the player's account.
     *
     * @param player the account owner
     * @param amount amount to withdraw (expected &ge; 0)
     * @return the transaction result; inspect {@link Transaction#isSuccessful()} for status
     */
    @NotNull
    Transaction withdraw(OfflinePlayer player, BigDecimal amount);

    /**
     * Attempts to withdraw {@code amount} from the player's account.
     *
     * @param player the account owner
     * @param amount amount to withdraw (expected &ge; 0)
     * @return the transaction result; inspect {@link Transaction#isSuccessful()} for status
     */
    @NotNull
    default Transaction withdraw(OfflinePlayer player, double amount) {
        return withdraw(player, new BigDecimal(amount));
    }

    /**
     * Attempts to deposit {@code amount} into the player's account.
     *
     * @param player the account owner
     * @param amount amount to deposit (expected &ge; 0)
     * @return the transaction result; inspect {@link Transaction#isSuccessful()} for status
     */
    @NotNull
    Transaction deposit(OfflinePlayer player, BigDecimal amount);

    /**
     * Attempts to deposit {@code amount} into the player's account.
     *
     * @param player the account owner
     * @param amount amount to deposit (expected &ge; 0)
     * @return the transaction result; inspect {@link Transaction#isSuccessful()} for status
     */
    @NotNull
    default Transaction deposit(OfflinePlayer player, double amount) {
        return deposit(player, new BigDecimal(amount));
    }

    /**
     * Creates an {@link EconomyAdapter} bound to the first available provider.
     * <p><strong>Selection order:</strong> VaultUnlocked -> Vault -> Fallback.</p>
     *
     * @return a non-null adapter (never {@code null})
     */
    @NotNull
    static EconomyAdapter<?> create() {
        try {
            if (Bukkit.getPluginManager().isPluginEnabled("VaultUnlocked"))
                return new Economy2();

            return Bukkit.getPluginManager().isPluginEnabled("Vault") ?
                    new EconomyImpl() :
                    new EconomyFallback();
        } catch (Exception e) {
            return new EconomyFallback();
        }
    }
}
