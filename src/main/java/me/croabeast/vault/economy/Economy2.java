package me.croabeast.vault.economy;

import lombok.Getter;
import net.milkbowl.vault2.economy.Economy;
import net.milkbowl.vault2.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Supplier;

@Getter
final class Economy2 implements EconomyAdapter<Economy> {

    private final Economy source;

    Economy2() {
        source = Objects.requireNonNull(Bukkit.getServicesManager().getRegistration(Economy.class)).getProvider();
    }

    @Override
    public Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin(source.getName());
    }

    @Override
    public boolean isEnabled() {
        return source.isEnabled();
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return source.hasAccount(player.getUniqueId());
    }

    @Override
    public boolean createAccount(OfflinePlayer player) {
        String name = player.getName();
        return source.createAccount(player.getUniqueId(), name == null ? player.toString() : name, true);
    }

    @NotNull
    public BigDecimal getBalance(OfflinePlayer player) {
        try {
            return source.balance(source.getName(), player.getUniqueId());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    @NotNull
    private Transaction executeTransaction(OfflinePlayer player, BigDecimal amount, Transaction.Type type, Supplier<EconomyResponse> action) {
        Transaction transaction = Transaction.failure(amount, type);

        if (amount != null && amount.signum() >= 0)
            try {
                EconomyResponse response = action.get();
                transaction = new Transaction(amount, response.balance, type)
                        .setSuccessful(response.transactionSuccess());
            } catch (Exception ignored) {}

        return transaction.setReceiver(player);
    }

    @NotNull
    public Transaction withdraw(OfflinePlayer player, BigDecimal amount) {
        return executeTransaction(
                player, amount, Transaction.Type.WITHDRAW,
                () -> source.withdraw(source.getName(), player.getUniqueId(), amount)
        );
    }

    @NotNull
    public Transaction deposit(OfflinePlayer player, BigDecimal amount) {
        return executeTransaction(
                player, amount, Transaction.Type.DEPOSIT,
                () -> source.deposit(source.getName(), player.getUniqueId(), amount)
        );
    }

    @NotNull
    public Transaction set(OfflinePlayer player, BigDecimal amount) {
        return executeTransaction(
                player, amount, Transaction.Type.SET,
                () -> source.set(source.getName(), player.getUniqueId(), amount)
        );
    }

    @NotNull
    public Transaction transfer(CommandSender sender, OfflinePlayer receiver, BigDecimal amount) {
        Transaction transaction = Transaction.failure(amount, Transaction.Type.TRANSFER);

        if (amount != null && amount.signum() >= 0) {
            boolean canDeposit = true;
            try {
                if (sender instanceof OfflinePlayer)
                    canDeposit = withdraw((OfflinePlayer) sender, amount).isSuccessful();
            } catch (Exception ignored) {
                canDeposit = false;
            }

            boolean success = false;
            BigDecimal balance = getBalance(receiver);

            if (canDeposit)
                try {
                    Transaction temp = deposit(receiver, amount);
                    balance = temp.getBalance();
                    success = temp.isSuccessful();
                } catch (Exception ignored) {}

            (transaction = new Transaction(amount, balance, Transaction.Type.TRANSFER))
                    .setSuccessful(success);
        }

        return transaction.setReceiver(receiver).setSender(sender);
    }

    @Override
    public String toString() {
        return "ChatAdapter{provider='Vault', plugin='" + source.getName() + "'}";
    }
}
