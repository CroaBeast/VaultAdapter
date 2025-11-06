package me.croabeast.vault;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Minimal, provider-agnostic facade for economy operations (balance, deposit, withdraw).
 *
 * <p> Implementations typically delegate to the <em>Vault</em> or <em>VaultUnlocked</em> Economy service. If Vault or VaultUnlocked is not
 * present, {@link #create()} returns a safe fallback that never throws and reports zero balances.

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
public interface EconomyAdapter<T> {

    /**
     * Returns the underlying provider instance (e.g., Vault Economy).
     *
     * @return non-null provider
     */
    @NotNull
    T getSource();

    /**
     * The plugin that supplies the economy provider (usually the Vault plugin).
     * <p>
     * Fallback implementations may return {@code null}.
     * </p>
     *
     * @return the backing plugin or {@code null} if unavailable
     */
    Plugin getPlugin();

    /**
     * Indicates whether the underlying provider is currently usable.
     *
     * @return {@code true} if the economy is available
     */
    boolean isEnabled();

    /**
     * Applies a mapping function to the underlying provider.
     *
     * @param <V>      result type
     * @param function function applied to {@link #getSource()}
     * @return function result
     */
    default <V> V fromSource(Function<T, V> function) {
        return function.apply(getSource());
    }

    /**
     * Returns the current account balance for the given player.
     * <p>
     * Implementations should never throw; if the account does not exist or no
     * provider is present, {@code 0.0} is a reasonable return value.
     * </p>
     *
     * @param player the account owner (offline-safe)
     * @return the current balance (non-negative)
     */
    double getBalance(OfflinePlayer player);

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
        return amount <= getBalance(player);
    }

    /**
     * Attempts to withdraw {@code amount} from the player's account.
     *
     * @param player the account owner
     * @param amount amount to withdraw (expected &ge; 0)
     * @return {@code true} on success; {@code false} if insufficient funds or invalid amount
     */
    boolean withdraw(OfflinePlayer player, double amount);

    /**
     * Attempts to deposit {@code amount} into the player's account.
     *
     * @param player the account owner
     * @param amount amount to deposit (expected &ge; 0)
     * @return {@code true} on success; {@code false} if invalid amount or provider refused
     */
    boolean deposit(OfflinePlayer player, double amount);

    /**
     * Creates an {@link EconomyAdapter} bound to the first available provider.
     * <p><strong>Selection order:</strong> VaultUnlocked → Vault → Fallback.</p>
     *
     * @return a non-null adapter (never {@code null})
     */
    @NotNull
    static EconomyAdapter<?> create() {
        try {
            if (Bukkit.getPluginManager().isPluginEnabled("VaultUnlocked"))
                return new Economy2();

            return Bukkit.getPluginManager().isPluginEnabled("Vault")
                    ? new EconomyImpl()
                    : new EconomyFallback();
        } catch (Exception e) {
            return new EconomyFallback();
        }
    }
}
