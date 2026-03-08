package me.croabeast.vault.permission;

import org.bukkit.command.CommandSender;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Provider-agnostic facade for permission and group lookup operations.
 *
 * <p>This contract focuses on read-style permission capabilities commonly needed
 * by plugins: direct permission checks, primary group lookup, group membership
 * checks, and listing available groups.</p>
 *
 * <p><strong>Detection order:</strong> LuckPerms -> VaultUnlocked -> Vault -> Fallback.</p>
 *
 * <pre>{@code
 * PermissionProvider permissions = PermissionProvider.detect();
 * if (!permissions.isEnabled()) return;
 *
 * if (permissions.hasPermission(player, "myplugin.admin")) {
 *     // execute admin flow
 * }
 *
 * String primary = permissions.getPrimaryGroup(player);
 * boolean staff = permissions.isInGroup(player, "staff");
 * }</pre>
 *
 * @since 1.3
 */
public interface PermissionProvider {

    /**
     * Gets the provider name that should be exposed to consumers.
     *
     * @return non-null provider name
     */
    @NotNull
    String getName();

    /**
     * Checks whether this permission backend is currently available.
     *
     * @return {@code true} if this backend can process permission operations
     */
    boolean isEnabled();

    /**
     * Checks whether a sender has the given permission node.
     *
     * @param sender sender to evaluate
     * @param permission permission node
     * @return {@code true} if permission is granted
     */
    boolean hasPermission(CommandSender sender, String permission);

    /**
     * Convenience overload for player checks.
     *
     * @param player player to evaluate
     * @param permission permission node
     * @return {@code true} if permission is granted
     */
    default boolean hasPermission(Player player, String permission) {
        return hasPermission((CommandSender) player, permission);
    }

    /**
     * Checks whether a player has a permission node in a world context.
     * <p>
     * Providers that do not support per-world permissions should ignore {@code world}.
     * </p>
     *
     * @param world world context (nullable)
     * @param player player to evaluate
     * @param permission permission node
     * @return {@code true} if permission is granted
     */
    boolean hasPermission(@Nullable World world, Player player, String permission);

    /**
     * Checks whether at least one permission is granted.
     *
     * @param sender sender to evaluate
     * @param permissions candidate permission nodes
     * @return {@code true} if any node is granted
     */
    default boolean hasAnyPermission(CommandSender sender, String... permissions) {
        if (permissions == null) return false;
        for (String permission : permissions)
            if (permission != null && hasPermission(sender, permission)) return true;
        return false;
    }

    /**
     * Checks whether all permissions are granted.
     *
     * @param sender sender to evaluate
     * @param permissions required permission nodes
     * @return {@code true} if all nodes are granted
     */
    default boolean hasAllPermissions(CommandSender sender, String... permissions) {
        if (permissions == null) return false;
        for (String permission : permissions)
            if (permission == null || !hasPermission(sender, permission)) return false;
        return true;
    }

    /**
     * Returns the player's primary group, if supported.
     *
     * @param player target player
     * @return primary group or {@code null}
     */
    @Nullable
    String getPrimaryGroup(Player player);

    /**
     * Returns the player's primary group for a world context.
     * <p>
     * Providers that do not support per-world groups should ignore {@code world}.
     * </p>
     *
     * @param world world context (nullable)
     * @param player target player
     * @return primary group or {@code null}
     */
    @Nullable
    String getPrimaryGroup(@Nullable World world, Player player);

    /**
     * Checks whether the player belongs to a group.
     *
     * @param player target player
     * @param group group name
     * @return {@code true} if the player belongs to {@code group}
     */
    boolean isInGroup(Player player, String group);

    /**
     * Checks whether the player belongs to a group in a world context.
     *
     * @param world world context (nullable)
     * @param player target player
     * @param group group name
     * @return {@code true} if the player belongs to {@code group}
     */
    boolean isInGroup(@Nullable World world, Player player, String group);

    /**
     * Convenience check that compares {@link #getPrimaryGroup(Player)} with {@code group}.
     *
     * @param player target player
     * @param group expected primary group
     * @return {@code true} if primary group matches (case-insensitive)
     */
    default boolean isPrimaryGroup(Player player, String group) {
        String primary = getPrimaryGroup(player);
        return primary != null && primary.equalsIgnoreCase(group);
    }

    /**
     * Returns all groups the player belongs to.
     *
     * @param player target player
     * @return non-null list of groups
     */
    @NotNull
    List<String> getGroups(Player player);

    /**
     * Returns all groups the player belongs to in a world context.
     *
     * @param world world context (nullable)
     * @param player target player
     * @return non-null list of groups
     */
    @NotNull
    List<String> getGroups(@Nullable World world, Player player);

    /**
     * Returns all known groups in the active provider.
     *
     * @return non-null list of groups (possibly empty)
     */
    @NotNull
    List<String> getGroups();

    /**
     * Attempts to add a permission to a player in a world context.
     *
     * @param world world context (nullable)
     * @param player target player
     * @param permission permission node
     * @return {@code true} if the provider accepted the operation
     */
    boolean addPermission(@Nullable World world, Player player, String permission);

    /**
     * Attempts to remove a permission from a player in a world context.
     *
     * @param world world context (nullable)
     * @param player target player
     * @param permission permission node
     * @return {@code true} if the provider accepted the operation
     */
    boolean removePermission(@Nullable World world, Player player, String permission);

    /**
     * Checks whether a group has a permission node in a world context.
     *
     * @param world world context (nullable)
     * @param group group name
     * @param permission permission node
     * @return {@code true} if permission is granted
     */
    boolean hasGroupPermission(@Nullable World world, String group, String permission);

    /**
     * Attempts to add a permission to a group in a world context.
     *
     * @param world world context (nullable)
     * @param group group name
     * @param permission permission node
     * @return {@code true} if the provider accepted the operation
     */
    boolean addGroupPermission(@Nullable World world, String group, String permission);

    /**
     * Attempts to remove a permission from a group in a world context.
     *
     * @param world world context (nullable)
     * @param group group name
     * @param permission permission node
     * @return {@code true} if the provider accepted the operation
     */
    boolean removeGroupPermission(@Nullable World world, String group, String permission);

    /**
     * Attempts to add a player to a group in a world context.
     *
     * @param world world context (nullable)
     * @param player target player
     * @param group group name
     * @return {@code true} if the provider accepted the operation
     */
    boolean addGroup(@Nullable World world, Player player, String group);

    /**
     * Attempts to remove a player from a group in a world context.
     *
     * @param world world context (nullable)
     * @param player target player
     * @param group group name
     * @return {@code true} if the provider accepted the operation
     */
    boolean removeGroup(@Nullable World world, Player player, String group);

    /**
     * Indicates whether group-related methods are supported.
     *
     * @return {@code true} if group operations are supported
     */
    boolean hasGroupsSupport();

    /**
     * Detects the first available permission provider.
     * <p><strong>Selection order:</strong> LuckPerms -> VaultUnlocked -> Vault -> Fallback.</p>
     *
     * @return non-null detected provider
     */
    @NotNull
    static PermissionProvider detect() {
        return ProviderServices.detect(null);
    }

    /**
     * Detects the first available permission provider.
     * <p>
     * If {@code self} is non-null and enabled, {@code self} is preferred.
     * Otherwise, provider lookup follows:
     * LuckPerms -> VaultUnlocked -> Vault -> Fallback.
     * </p>
     *
     * @param self preferred provider when enabled
     * @return non-null detected provider
     */
    @NotNull
    static PermissionProvider detect(@Nullable PermissionProvider self) {
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
    static boolean register(@NotNull Plugin plugin, @NotNull PermissionProvider backend) {
        return ProviderServices.register(plugin, backend);
    }

    /**
     * Unregisters a previously registered {@code backend}.
     *
     * @param backend backend to unpublish from Bukkit services
     * @return {@code true} if a registration existed and was removed
     */
    static boolean unregister(@NotNull PermissionProvider backend) {
        return ProviderServices.unregister(backend);
    }
}
