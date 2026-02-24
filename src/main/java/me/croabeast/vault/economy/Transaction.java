package me.croabeast.vault.economy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Represents the result of an economy operation for a player.
 *
 * <p>A transaction stores the requested {@link #amount}, the resulting
 * {@link #balance}, the operation {@link #type}, whether it was
 * {@link #successful}, and the target {@link #player}.</p>
 */
@RequiredArgsConstructor
@Accessors(chain = true)
@Getter @Setter
public class Transaction {

    /**
     * Requested amount for the operation.
     */
    private final BigDecimal amount;
    /**
     * Balance reported after the operation.
     */
    private final BigDecimal balance;
    /**
     * Operation type.
     */
    private final Type type;

    /**
     * Whether the provider reported success for this transaction.
     */
    private boolean successful = false;
    /**
     * Player associated with this transaction.
     */
    private OfflinePlayer player = null;

    /**
     * Creates a transaction from primitive {@code double} values.
     *
     * @param amount requested amount
     * @param balance resulting balance
     * @param type operation type
     */
    public Transaction(double amount, double balance, Type type) {
        this(BigDecimal.valueOf(amount), BigDecimal.valueOf(balance), type);
    }

    /**
     * Creates a successful transaction with a zero resulting balance.
     *
     * @param amount requested amount
     * @param type operation type
     * @return a successful transaction instance
     */
    @NotNull
    public static Transaction success(BigDecimal amount, Type type) {
        return new Transaction(amount, BigDecimal.ZERO, type).setSuccessful(true);
    }

    /**
     * Creates a failed transaction with a zero resulting balance.
     *
     * @param amount requested amount
     * @param type operation type
     * @return a failed transaction instance
     */
    @NotNull
    public static Transaction failure(BigDecimal amount, Type type) {
        return new Transaction(amount, BigDecimal.ZERO, type);
    }

    /**
     * Creates a successful transaction from a primitive amount.
     *
     * @param amount requested amount
     * @param type operation type
     * @return a successful transaction instance
     */
    @NotNull
    public static Transaction success(double amount, Type type) {
        return new Transaction(amount, 0.0, type).setSuccessful(true);
    }

    /**
     * Creates a failed transaction from a primitive amount.
     *
     * @param amount requested amount
     * @param type operation type
     * @return a failed transaction instance
     */
    @NotNull
    public static Transaction failure(double amount, Type type) {
        return new Transaction(amount, 0.0, type);
    }

    /**
     * Economy operation kind.
     */
    public enum Type {
        /**
         * Money is added to the account.
         */
        DEPOSIT,
        /**
         * Money is removed from the account.
         */
        WITHDRAW
    }
}
