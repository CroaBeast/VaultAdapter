package me.croabeast.vault.economy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Represents the result of an economy operation.
 *
 * <p>A transaction stores the requested {@link #amount}, the resulting
 * {@link #balance}, the operation {@link #type}, whether it was
 * {@link #successful}, and the related {@link #sender}/{@link #receiver}
 * participants.</p>
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
     * Command sender associated with this transaction.
     *
     * <p>Commonly set for transfer operations. May be {@code null} for
     * account-only operations.</p>
     */
    private CommandSender sender = null;
    /**
     * Receiver player associated with this transaction.
     *
     * <p>Represents the target account affected by the operation. May be
     * {@code null} when not applicable.</p>
     */
    private OfflinePlayer receiver = null;

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
     * <p>The returned instance enforces successful state and does not allow
     * {@link #setSuccessful(boolean)}.</p>
     *
     * @param amount requested amount
     * @param type operation type
     * @return a successful transaction instance
     */
    @NotNull
    public static Transaction success(BigDecimal amount, Type type) {
        return new Transaction(amount, BigDecimal.ZERO, type) {
            @Override
            public boolean isSuccessful() {
                return true;
            }
            @Override
            public Transaction setSuccessful(boolean successful) {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
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
        return success(BigDecimal.valueOf(amount), type);
    }

    /**
     * Creates a failed transaction with a zero resulting balance.
     *
     * <p>The returned instance enforces failed state and does not allow
     * {@link #setSuccessful(boolean)}.</p>
     *
     * @param amount requested amount
     * @param type operation type
     * @return a failed transaction instance
     */
    @NotNull
    public static Transaction failure(BigDecimal amount, Type type) {
        return new Transaction(amount, BigDecimal.ZERO, type) {
            @Override
            public Transaction setSuccessful(boolean successful) {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
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
        return failure(BigDecimal.valueOf(amount), type);
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
        WITHDRAW,
        /**
         * Account balance is set to an exact value.
         */
        SET,
        /**
         * Money is moved from a sender to a receiver account.
         */
        TRANSFER
    }
}
