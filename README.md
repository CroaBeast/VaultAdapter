# VaultAdapter

Provider-agnostic facades for **chat/permission metadata** and **economy** in Bukkit/Spigot/Paper plugins.

This library exposes two tiny interfaces:

- `ChatAdapter<T>` — read prefixes, suffixes, primary group, and memberships from the active provider.
- `EconomyAdapter<T>` — query balances and perform deposits/withdrawals via the active economy service.

No hard dependency on a specific ecosystem: the adapter chooses the first available provider at runtime and falls back safely if none are present.

---

## ✨ Features

- **Zero hard-coupling**: target a stable API while supporting multiple backends.
- **Graceful fallback**: never throws just because Vault/LuckPerms are missing.
- **Rich economy result model**: mutations return `Transaction` with status, amount, and resulting balance.
- **Consistent nullability**: `null` when metadata is genuinely absent.
- **Optional provider access**: `fromSource(Function<T,V>)` for advanced use without polluting your codebase.

### Provider selection order

- **ChatAdapter**: LuckPerms → VaultUnlocked → Vault → Fallback
- **EconomyAdapter**: VaultUnlocked → Vault → Fallback

---

## 📦 Installation

Replace `{version}` with the latest release tag.

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

> **Tip:** If you ship a plugin, consider `compileOnly` + runtime presence of providers (Vault/LuckPerms/VaultUnlocked) and add `softdepend` in your `plugin.yml`.

---

## ⚙️ `plugin.yml`

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

---

## 🧩 Usage

### Chat metadata

```java
import me.croabeast.vault.chat.ChatAdapter;
import org.bukkit.entity.Player;

public class ChatExample {
    public void show(Player player) {
        ChatAdapter<?> chat = ChatAdapter.create();
        if (!chat.isEnabled()) return;

        String prefix  = chat.getPrefix(player);          // may be null
        String suffix  = chat.getSuffix(player);          // may be null
        String primary = chat.getPrimaryGroup(player);    // may be null

        boolean isStaff = chat.isInGroup(player, "staff");
        boolean isModPrimary = chat.isPrimaryGroup(player, "moderator");

        // Group metadata (world-aware example)
        String vipPrefix = chat.getGroupPrefix(player.getWorld(), "vip");
        // ...format and display as you like
    }
}
```

### Economy

```java
import me.croabeast.vault.economy.EconomyAdapter;
import me.croabeast.vault.economy.Transaction;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;

public class EconomyExample {
    public boolean buy(OfflinePlayer player, BigDecimal price) {
        EconomyAdapter<?> eco = EconomyAdapter.create();
        if (!eco.isEnabled()) return false;

        if (!eco.hasAmount(player, price)) return false;

        Transaction tx = eco.withdraw(player, price);
        if (!tx.isSuccessful()) return false;

        // grant item/service here
        // BigDecimal currentBalance = tx.getBalance();
        return true;
    }
}
```

### Optional: provider-specific access

If you need a one-off capability not covered by the facade (e.g., calling a LuckPerms method directly), use `fromSource` to avoid hard dependencies in most of your code:

```java
var chat = ChatAdapter.<Object>create();
var providerInfo = chat.fromSource(src -> src.getClass().getSimpleName()); // example: "LPApi" / "VaultChat"
```

---

## 🔍 API Overview

### `ChatAdapter<T>`

* `@Nullable String getPrimaryGroup(Player)`
* `boolean isInGroup(Player, String)`
* `@NotNull List<String> getGroups(Player)`
* `@Nullable String getPrefix(Player)`
* `@Nullable String getSuffix(Player)`
* `@Nullable String getGroupPrefix(@Nullable World, String)` + convenience overload
* `@Nullable String getGroupSuffix(@Nullable World, String)` + convenience overload
* `boolean isEnabled()`, `Plugin getPlugin()`, `T getSource()`, `<V> V fromSource(Function<T,V>)`
* `static ChatAdapter<?> create()`

### `EconomyAdapter<T>`

* `@NotNull BigDecimal getBalance(OfflinePlayer)`
* `boolean hasAccount(OfflinePlayer)`
* `boolean createAccount(OfflinePlayer)`
* `boolean hasAmount(OfflinePlayer, BigDecimal)` + convenience overload
* `@NotNull Transaction withdraw(OfflinePlayer, BigDecimal)` + convenience overload
* `@NotNull Transaction deposit(OfflinePlayer, BigDecimal)` + convenience overload
* `boolean isEnabled()`, `Plugin getPlugin()`, `T getSource()`, `<V> V fromSource(Function<T,V>)`
* `static EconomyAdapter<?> create()`

### `Transaction`

* `@NotNull BigDecimal getAmount()`
* `@NotNull BigDecimal getBalance()`
* `@NotNull Transaction.Type getType()` (`DEPOSIT` / `WITHDRAW`)
* `boolean isSuccessful()`
* `OfflinePlayer getPlayer()`

---

## 🧠 Conventions & Notes

* **Nullability**: Some metadata (prefix/suffix/primary group) may be absent → returns `null`.
* **World context**: If a provider doesn’t support per-world values, the world parameter is ignored.
* **Threading**: Call from the **main server thread** unless your provider explicitly states otherwise.
* **Formatting**: Returned prefixes/suffixes may contain legacy color codes. Translate or strip as needed.
* **Amounts**: Prefer `BigDecimal` overloads for exact values; invalid/negative amounts produce failed transactions (`isSuccessful() == false`).
* **Fallback source**: `getSource()` on fallback adapters throws `IllegalStateException`; use `isEnabled()` checks first.

---

## 🛠 Extending

You can add new providers by implementing the interfaces and adjusting the detection inside `create()`:

* For chat: implement `ChatAdapter<T>` (e.g., `ChatLuckPerms`, `ChatVault2`, `ChatVaultImpl`, `ChatFallback`).
* For economy: implement `EconomyAdapter<T>` (e.g., `Economy2`, `EconomyImpl`, `EconomyFallback`).

Follow the same semantics:

* Never throw for “provider not present”; report safe defaults.
* Keep world optional and case-insensitive group comparisons.
* Ensure `isEnabled()` reflects actual availability.

---

## ✅ Quick Checklist

* [ ] Added the repository and dependency with `{version}`
* [ ] Declared `softdepend` on `LuckPerms`, `VaultUnlocked`, and `Vault`
* [ ] Accessed adapters only on the main thread
* [ ] Handled `null` prefixes/suffixes/primary groups
* [ ] Checked `Transaction#isSuccessful()` for economy mutations

---
