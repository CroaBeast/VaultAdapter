package me.croabeast.vault.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

/**
 * Provider-agnostic economy facade for Bukkit plugins.
 *
 * <p>This contract exposes the minimum set of player-account operations needed by
 * most economy-enabled plugins: balance lookup, account creation/existence checks,
 * deposits, withdrawals, direct balance setting, and sender-to-player transfers.
 * The API is intentionally small and stable so plugin code can target one interface
 * regardless of which concrete economy service is active at runtime.</p>
 *
 * <p>Typical lifecycle usage is:</p>
 * <ol>
 *   <li>Implement this interface in your own backend.</li>
 *   <li>Publish it with {@link #register(Plugin, EconomyProvider)} during plugin enable.</li>
 *   <li>Resolve the active backend through {@link #detect()} or {@link #detect(EconomyProvider)}.</li>
 *   <li>Use this interface for all economy reads/mutations in your plugin code.</li>
 * </ol>
 *
 * <p><strong>Detection/registration order:</strong></p>
 * <ul>
 *   <li>Registration prefers VaultUnlocked, then Vault.</li>
 *   <li>Detection prefers VaultUnlocked, then Vault, then fallback.</li>
 * </ul>
 *
 * <p><strong>Failure semantics:</strong> methods should return safe values and failed
 * {@link Transaction} results instead of throwing for common provider/lookup issues.
 * This keeps command and gameplay flows predictable even when external services are
 * unavailable or partially configured.</p>
 *
 * <p><strong>Amount rules:</strong> amounts are expected to be non-negative. Use the
 * {@link BigDecimal}-based overloads when precision matters; primitive overloads are
 * convenience methods that delegate to {@code BigDecimal} variants.</p>
 *
 * <p><strong>Threading:</strong> call methods on the server main thread unless the active
 * provider explicitly documents thread-safe asynchronous access.</p>
 *
 * <pre>{@code
 * // Example:
 * EconomyProvider backend = new MyEconomyBackend();
 * boolean registered = EconomyProvider.register(plugin, backend);
 * if (!registered) return;
 *
 * EconomyProvider eco = EconomyProvider.detect(backend);
 * if (!eco.isEnabled()) return;
 *
 * if (eco.hasAmount(player, new BigDecimal("250.00"))) {
 *     Transaction tx = eco.withdraw(player, new BigDecimal("250.00"));
 *     if (tx.isSuccessful()) {
 *         // grant item/service
 *     }
 * }
 *
 * // On disable:
 * EconomyProvider.unregister(backend);
 * }</pre>
 *
 * @since 1.3
 */
public interface EconomyProvider {

    /**
     * Gets the provider name that should be exposed to consumers.
     *
     * @return non-null provider name
     */
    @NotNull
    String getName();

    /**
     * Returns the owning plugin for this economy backend.
     *
     * @return owner plugin, or {@code null} when unsupported
     */
    Plugin getPlugin();

    /**
     * Checks whether this economy backend is currently available.
     *
     * @return {@code true} if this backend can process economy operations
     */
    boolean isEnabled();

    /**
     * Returns the configured currency name for display.
     *
     * @param single {@code true} for singular form, {@code false} for plural form
     * @return non-null currency display name
     */
    @NotNull
    default String getCurrencyName(boolean single) {
        return single ? "coin" : "coins";
    }

    /**
     * Returns how many fractional decimal digits this provider supports.
     *
     * @return decimal precision (typically {@code 2})
     */
    default int getDecimals() {
        return 2;
    }

    /**
     * Returns the current account balance for the given player.
     * <p>
     * Implementations should avoid throwing and prefer returning
     * {@link BigDecimal#ZERO} when the account cannot be resolved.
     * </p>
     *
     * @param player account owner (offline-safe)
     * @return current balance
     */
    @NotNull
    BigDecimal getBalance(OfflinePlayer player);

    /**
     * Checks whether an account exists for the given player.
     *
     * @param player account owner
     * @return {@code true} if an account exists
     */
    boolean hasAccount(OfflinePlayer player);

    /**
     * Attempts to create an account for the given player.
     *
     * @param player account owner
     * @return {@code true} if the account was created successfully
     */
    boolean createAccount(OfflinePlayer player);

    /**
     * Convenience check for whether a player has at least {@code amount}.
     * <p>
     * Implemented as: {@code amount.compareTo(getBalance(player)) <= 0}.
     * </p>
     *
     * @param player account owner
     * @param amount required amount (expected &ge; 0)
     * @return {@code true} if balance is sufficient
     */
    default boolean hasAmount(OfflinePlayer player, BigDecimal amount) {
        return amount.compareTo(getBalance(player)) <= 0;
    }

    /**
     * Convenience check for whether a player has at least {@code amount}.
     *
     * @param player account owner
     * @param amount required amount (expected &ge; 0)
     * @return {@code true} if balance is sufficient
     */
    default boolean hasAmount(OfflinePlayer player, double amount) {
        return hasAmount(player, new BigDecimal(amount));
    }

    /**
     * Attempts to withdraw {@code amount} from the player's account.
     *
     * @param player account owner
     * @param amount amount to withdraw (expected &ge; 0)
     * @return transaction result; inspect {@link Transaction#isSuccessful()}
     */
    @NotNull
    Transaction withdraw(OfflinePlayer player, BigDecimal amount);

    /**
     * Attempts to withdraw {@code amount} from the player's account.
     *
     * @param player account owner
     * @param amount amount to withdraw (expected &ge; 0)
     * @return transaction result; inspect {@link Transaction#isSuccessful()}
     */
    @NotNull
    default Transaction withdraw(OfflinePlayer player, double amount) {
        return withdraw(player, new BigDecimal(amount));
    }

    /**
     * Attempts to deposit {@code amount} into the player's account.
     *
     * @param player account owner
     * @param amount amount to deposit (expected &ge; 0)
     * @return transaction result; inspect {@link Transaction#isSuccessful()}
     */
    @NotNull
    Transaction deposit(OfflinePlayer player, BigDecimal amount);

    /**
     * Attempts to deposit {@code amount} into the player's account.
     *
     * @param player account owner
     * @param amount amount to deposit (expected &ge; 0)
     * @return transaction result; inspect {@link Transaction#isSuccessful()}
     */
    @NotNull
    default Transaction deposit(OfflinePlayer player, double amount) {
        return deposit(player, new BigDecimal(amount));
    }

    /**
     * Attempts to set the player's balance to exactly {@code amount}.
     *
     * <p>If no native "set" exists, this default implementation applies the
     * difference via {@link #deposit(OfflinePlayer, BigDecimal)} or
     * {@link #withdraw(OfflinePlayer, BigDecimal)}.</p>
     *
     * @param player account owner
     * @param amount desired resulting balance (expected &ge; 0)
     * @return transaction result; inspect {@link Transaction#isSuccessful()}
     */
    @NotNull
    default Transaction set(OfflinePlayer player, BigDecimal amount) {
        Transaction transaction = new Transaction(
                amount,
                getBalance(player),
                Transaction.Type.SET
        ).setSuccessful(false);

        if (amount != null && amount.signum() >= 0) {
            BigDecimal currentBalance = getBalance(player);

            Transaction response = null;
            int comparison = amount.compareTo(currentBalance);
            if (comparison > 0) {
                BigDecimal difference = amount.subtract(currentBalance);
                response = deposit(player, difference);
            } else if (comparison < 0) {
                BigDecimal difference = currentBalance.subtract(amount);
                response = withdraw(player, difference);
            }

            if (response == null) {
                transaction = new Transaction(amount, currentBalance, Transaction.Type.SET)
                        .setSuccessful(true);
            } else {
                transaction = new Transaction(amount, response.getBalance(), Transaction.Type.SET)
                        .setSuccessful(response.isSuccessful());
            }
        }

        return transaction.setReceiver(player);
    }

    /**
     * Attempts to set the player's balance to exactly {@code amount}.
     *
     * @param player account owner
     * @param amount desired resulting balance (expected &ge; 0)
     * @return transaction result; inspect {@link Transaction#isSuccessful()}
     */
    @NotNull
    default Transaction set(OfflinePlayer player, double amount) {
        return set(player, new BigDecimal(amount));
    }

    /**
     * Attempts to transfer {@code amount} from {@code sender} to {@code receiver}.
     *
     * <p>If {@code sender} is not an {@link OfflinePlayer}, sender debit is skipped
     * and only receiver credit is attempted.</p>
     *
     * @param sender transfer initiator
     * @param receiver transfer destination account
     * @param amount amount to transfer (expected &ge; 0)
     * @return transaction result; inspect {@link Transaction#isSuccessful()}
     */
    @NotNull
    default Transaction transfer(CommandSender sender, OfflinePlayer receiver, BigDecimal amount) {
        Transaction transaction;

        if (amount != null && amount.signum() >= 0) {
            boolean canDeposit = true;

            try {
                if (sender instanceof OfflinePlayer)
                    canDeposit = withdraw((OfflinePlayer) sender, amount).isSuccessful();
            } catch (Exception ignored) {
                canDeposit = false;
            }

            if (canDeposit) {
                Transaction temp = deposit(receiver, amount);
                transaction = new Transaction(amount, temp.getBalance(), Transaction.Type.TRANSFER)
                        .setSuccessful(temp.isSuccessful());
            } else {
                transaction = new Transaction(amount, getBalance(receiver), Transaction.Type.TRANSFER)
                        .setSuccessful(false);
            }
        } else {
            transaction = new Transaction(amount, getBalance(receiver), Transaction.Type.TRANSFER)
                    .setSuccessful(false);
        }

        return transaction.setSender(sender).setReceiver(receiver);
    }

    /**
     * Attempts to transfer {@code amount} from {@code sender} to {@code receiver}.
     *
     * @param sender transfer initiator
     * @param receiver transfer destination account
     * @param amount amount to transfer (expected &ge; 0)
     * @return transaction result; inspect {@link Transaction#isSuccessful()}
     */
    @NotNull
    default Transaction transfer(CommandSender sender, OfflinePlayer receiver, double amount) {
        return transfer(sender, receiver, new BigDecimal(amount));
    }

    /**
     * Detects the first available provider and returns a bound economy provider.
     * <p><strong>Selection order:</strong> VaultUnlocked -> Vault -> Fallback.</p>
     *
     * @return non-null detected port (never {@code null})
     */
    @NotNull
    static EconomyProvider detect() {
        return ProviderServices.detect(null);
    }

    /**
     * Detects the first available provider and returns a bound economy provider.
     * <p>
     * If {@code self} is non-null and enabled, {@code self} is preferred.
     * Otherwise, provider lookup follows:
     * VaultUnlocked -> Vault -> Fallback.
     * </p>
     *
     * @param self preferred backend to use when enabled (nullable)
     * @return non-null detected port (never {@code null})
     */
    @NotNull
    static EconomyProvider detect(@Nullable EconomyProvider self) {
        return ProviderServices.detect(self);
    }

    /**
     * Registers {@code backend} into Bukkit's service manager.
     * <p><strong>Registration order:</strong> VaultUnlocked -> Vault.</p>
     *
     * @param plugin owning plugin
     * @param backend backend to expose
     * @return {@code true} if registration succeeded
     */
    static boolean register(@NotNull Plugin plugin, @NotNull EconomyProvider backend) {
        return ProviderServices.register(plugin, backend);
    }

    /**
     * Unregisters a previously registered {@code backend}.
     *
     * @param backend backend to unpublish from Bukkit services
     * @return {@code true} if a registration existed and was removed
     */
    static boolean unregister(@NotNull EconomyProvider backend) {
        return ProviderServices.unregister(backend);
    }
}


