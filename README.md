# VaultAdapter

Provider-agnostic facades for Bukkit/Spigot/Paper plugins:

- `EconomyProvider` for balances and account transactions
- `PermissionProvider` for permissions and groups
- `ChatProvider` for prefixes, suffixes, and metadata

The goal is to keep plugin code stable while supporting multiple backends at runtime.

## Features

- No hard dependency on a single permission/chat/economy ecosystem
- Runtime provider detection with safe fallback providers
- Optional registration into Bukkit services (VaultUnlocked and Vault)
- Economy operations return a rich `Transaction` model
- Explicit nullability for metadata that may not exist

## Provider Detection Order

- `ChatProvider`: LuckPerms -> VaultUnlocked -> Vault -> Fallback
- `PermissionProvider`: LuckPerms -> VaultUnlocked -> Vault -> Fallback
- `EconomyProvider`: VaultUnlocked -> Vault -> Fallback

Registration order for all providers is:

- VaultUnlocked first
- Vault second

## Installation

Replace `{version}` with your target release.

### Gradle (Kotlin DSL)

```kts
repositories {
    maven("https://croabeast.github.io/repo/")
}

dependencies {
    implementation("me.croabeast:VaultAdapter:{version}")
}
```

### Gradle (Groovy)

```groovy
repositories {
    maven { url "https://croabeast.github.io/repo/" }
}

dependencies {
    implementation "me.croabeast:VaultAdapter:{version}"
}
```

### Maven

```xml
<repositories>
  <repository>
    <id>croabeast-repo</id>
    <url>https://croabeast.github.io/repo/</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>me.croabeast</groupId>
    <artifactId>VaultAdapter</artifactId>
    <version>{version}</version>
  </dependency>
</dependencies>
```

If you are building a plugin, `compileOnly` + runtime providers is usually preferred.

## `plugin.yml` Example

```yaml
name: YourPlugin
main: your.package.YourPlugin
version: 1.0.0
api-version: 1.16
softdepend:
  - LuckPerms
  - VaultUnlocked
  - Vault
```

## Usage

### Chat + Permission

`ChatProvider` now exposes permission/group operations through `getPermissionProvider()`.

```java
import me.croabeast.vault.chat.ChatProvider;
import me.croabeast.vault.permission.PermissionProvider;
import org.bukkit.entity.Player;

public final class ChatExample {

    public void show(Player player) {
        ChatProvider chat = ChatProvider.detect();
        if (!chat.isEnabled()) return;

        String prefix = chat.getPrefix(player);
        String suffix = chat.getSuffix(player);

        PermissionProvider permissions = chat.getPermissionProvider();
        boolean canModerate = permissions.hasPermission(player, "myplugin.moderate");
        String primaryGroup = permissions.getPrimaryGroup(player);
        boolean staff = permissions.isInGroup(player, "staff");
    }
}
```

### Economy

```java
import me.croabeast.vault.economy.EconomyProvider;
import me.croabeast.vault.economy.Transaction;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;

public final class EconomyExample {

    public boolean buy(OfflinePlayer player, BigDecimal price) {
        EconomyProvider eco = EconomyProvider.detect();
        if (!eco.isEnabled()) return false;
        if (!eco.hasAmount(player, price)) return false;

        Transaction tx = eco.withdraw(player, price);
        return tx.isSuccessful();
    }
}
```

### Register Your Own Economy Provider

```java
import me.croabeast.vault.economy.EconomyProvider;
import me.croabeast.vault.economy.Transaction;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;

public final class MyEconomyBackend implements EconomyProvider {

    @Override public String getName() { return "MyEconomy"; }
    @Override public boolean isEnabled() { return true; }
    @Override public boolean hasAccount(OfflinePlayer player) { return true; }
    @Override public boolean createAccount(OfflinePlayer player) { return true; }
    @Override public BigDecimal getBalance(OfflinePlayer player) { return BigDecimal.TEN; }

    @Override
    public Transaction withdraw(OfflinePlayer player, BigDecimal amount) {
        BigDecimal current = getBalance(player);
        BigDecimal next = current.subtract(amount);

        return new Transaction(amount, next.signum() < 0 ? BigDecimal.ZERO : next, Transaction.Type.WITHDRAW)
                .setSuccessful(next.signum() >= 0)
                .setReceiver(player);
    }

    @Override
    public Transaction deposit(OfflinePlayer player, BigDecimal amount) {
        return new Transaction(amount, getBalance(player).add(amount), Transaction.Type.DEPOSIT)
                .setSuccessful(true)
                .setReceiver(player);
    }
}
```

```java
// onEnable
EconomyProvider backend = new MyEconomyBackend();
boolean registered = EconomyProvider.register(this, backend);
if (!registered) return;

EconomyProvider active = EconomyProvider.detect(backend);

// onDisable
EconomyProvider.unregister(backend);
```

### Register Your Own Permission Provider

```java
import me.croabeast.vault.permission.PermissionProvider;

public final class YourPlugin extends JavaPlugin {

    private PermissionProvider permissionBackend;

    @Override
    public void onEnable() {
        permissionBackend = new MyPermissionBackend(); // your implementation
        boolean registered = PermissionProvider.register(this, permissionBackend);
        if (!registered) return;

        PermissionProvider active = PermissionProvider.detect(permissionBackend);
    }

    @Override
    public void onDisable() {
        if (permissionBackend != null) {
            PermissionProvider.unregister(permissionBackend);
        }
    }
}
```

### Register Your Own Chat Provider

`ChatProvider` must expose a `PermissionProvider` through `getPermissionProvider()`, so when you own both backends, register permission first.

```java
import me.croabeast.vault.chat.ChatProvider;
import me.croabeast.vault.permission.PermissionProvider;

public final class YourPlugin extends JavaPlugin {

    private PermissionProvider permissionBackend;
    private ChatProvider chatBackend;

    @Override
    public void onEnable() {
        permissionBackend = new MyPermissionBackend(); // your implementation
        PermissionProvider.register(this, permissionBackend);

        chatBackend = new MyChatBackend(permissionBackend); // your implementation
        boolean registered = ChatProvider.register(this, chatBackend);
        if (!registered) return;

        ChatProvider activeChat = ChatProvider.detect(chatBackend);
        PermissionProvider activePermissions = activeChat.getPermissionProvider();
    }

    @Override
    public void onDisable() {
        if (chatBackend != null) {
            ChatProvider.unregister(chatBackend);
        }
        if (permissionBackend != null) {
            PermissionProvider.unregister(permissionBackend);
        }
    }
}
```

## API Summary

### `ChatProvider`

- `@NotNull String getName()`
- `boolean isEnabled()`
- `@NotNull PermissionProvider getPermissionProvider()`
- `@Nullable String getPrefix(Player)`
- `@Nullable String getPrefix(@Nullable World, Player)`
- `void setPrefix(@Nullable World, Player, @Nullable String)`
- `@Nullable String getSuffix(Player)`
- `@Nullable String getSuffix(@Nullable World, Player)`
- `void setSuffix(@Nullable World, Player, @Nullable String)`
- `@Nullable String getGroupPrefix(@Nullable World, String)`
- `void setGroupPrefix(@Nullable World, String, @Nullable String)`
- `@Nullable String getGroupSuffix(@Nullable World, String)`
- `void setGroupSuffix(@Nullable World, String, @Nullable String)`
- `get/set` player and group metadata for `int`, `double`, `boolean`, `String`
- `static ChatProvider detect()`
- `static ChatProvider detect(ChatProvider self)`
- `static boolean register(Plugin, ChatProvider)`
- `static boolean unregister(ChatProvider)`

### `PermissionProvider`

- `@NotNull String getName()`
- `boolean isEnabled()`
- `boolean hasPermission(CommandSender, String)`
- `boolean hasPermission(@Nullable World, Player, String)`
- `@Nullable String getPrimaryGroup(Player)`
- `@Nullable String getPrimaryGroup(@Nullable World, Player)`
- `boolean isInGroup(Player, String)`
- `boolean isInGroup(@Nullable World, Player, String)`
- `@NotNull List<String> getGroups(Player)`
- `@NotNull List<String> getGroups(@Nullable World, Player)`
- `@NotNull List<String> getGroups()`
- `boolean addPermission(@Nullable World, Player, String)`
- `boolean removePermission(@Nullable World, Player, String)`
- `boolean hasGroupPermission(@Nullable World, String, String)`
- `boolean addGroupPermission(@Nullable World, String, String)`
- `boolean removeGroupPermission(@Nullable World, String, String)`
- `boolean addGroup(@Nullable World, Player, String)`
- `boolean removeGroup(@Nullable World, Player, String)`
- `boolean hasGroupsSupport()`
- `static PermissionProvider detect()`
- `static PermissionProvider detect(PermissionProvider self)`
- `static boolean register(Plugin, PermissionProvider)`
- `static boolean unregister(PermissionProvider)`

### `EconomyProvider`

- `@NotNull String getName()`
- `boolean isEnabled()`
- `@NotNull String getCurrencyName(boolean single)`
- `int getDecimals()`
- `@NotNull BigDecimal getBalance(OfflinePlayer)`
- `boolean hasAccount(OfflinePlayer)`
- `boolean createAccount(OfflinePlayer)`
- `boolean hasAmount(OfflinePlayer, BigDecimal)` (+ primitive overload)
- `@NotNull Transaction withdraw(OfflinePlayer, BigDecimal)` (+ primitive overload)
- `@NotNull Transaction deposit(OfflinePlayer, BigDecimal)` (+ primitive overload)
- `@NotNull Transaction set(OfflinePlayer, BigDecimal)` (+ primitive overload)
- `@NotNull Transaction transfer(CommandSender, OfflinePlayer, BigDecimal)` (+ primitive overload)
- `static EconomyProvider detect()`
- `static EconomyProvider detect(EconomyProvider self)`
- `static boolean register(Plugin, EconomyProvider)`
- `static boolean unregister(EconomyProvider)`

### `Transaction`

- `BigDecimal getAmount()`
- `BigDecimal getBalance()`
- `Transaction.Type getType()`:
  - `DEPOSIT`
  - `WITHDRAW`
  - `SET`
  - `TRANSFER`
- `boolean isSuccessful()`
- `CommandSender getSender()`
- `OfflinePlayer getReceiver()`

## Notes

- Metadata methods may return `null` when the provider has no value.
- World context is optional and may be ignored by providers without per-world data.
- Run provider operations on the main server thread unless your backend explicitly supports async access.
- Prefer `BigDecimal` economy overloads for exact amounts.
