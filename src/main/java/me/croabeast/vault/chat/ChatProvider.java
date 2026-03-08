package me.croabeast.vault.chat;

import me.croabeast.vault.permission.PermissionProvider;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provider-agnostic facade for chat metadata (prefixes, suffixes and info nodes).
 *
 * <p>Permission and group membership operations are exposed through
 * {@link #getPermissionProvider()}.</p>
 *
 * <p><strong>Detection order:</strong> LuckPerms -> VaultUnlocked -> Vault -> Fallback.</p>
 *
 * @since 1.3
 */
public interface ChatProvider {

    /**
     * Gets the provider name that should be exposed to consumers.
     *
     * @return non-null provider name
     */
    @NotNull
    String getName();

    /**
     * Checks whether this chat backend is currently available.
     *
     * @return {@code true} if this backend can process chat operations
     */
    boolean isEnabled();

    /**
     * Returns the permission backend associated with this chat provider.
     *
     * @return non-null permission provider
     */
    @NotNull
    PermissionProvider getPermissionProvider();

    /**
     * Returns the player's chat prefix, if any.
     *
     * @param player target player
     * @return the prefix or {@code null} if undefined
     */
    @Nullable
    String getPrefix(Player player);

    /**
     * Returns the player's chat prefix in a world context.
     *
     * @param world world context (nullable)
     * @param player target player
     * @return the prefix or {@code null}
     */
    @Nullable
    String getPrefix(@Nullable World world, Player player);

    /**
     * Attempts to set the player's chat prefix in a world context.
     *
     * @param world world context (nullable)
     * @param player target player
     * @param prefix new prefix (nullable)
     */
    void setPrefix(@Nullable World world, Player player, @Nullable String prefix);

    /**
     * Returns the player's chat suffix, if any.
     *
     * @param player target player
     * @return the suffix or {@code null} if undefined
     */
    @Nullable
    String getSuffix(Player player);

    /**
     * Returns the player's chat suffix in a world context.
     *
     * @param world world context (nullable)
     * @param player target player
     * @return the suffix or {@code null}
     */
    @Nullable
    String getSuffix(@Nullable World world, Player player);

    /**
     * Attempts to set the player's chat suffix in a world context.
     *
     * @param world world context (nullable)
     * @param player target player
     * @param suffix new suffix (nullable)
     */
    void setSuffix(@Nullable World world, Player player, @Nullable String suffix);

    /**
     * Returns the prefix configured for a group within the given world context.
     *
     * @param world world context (nullable)
     * @param group group name
     * @return group prefix or {@code null}
     */
    @Nullable
    String getGroupPrefix(@Nullable World world, String group);

    /**
     * Convenience overload for {@link #getGroupPrefix(World, String)} with no world context.
     *
     * @param group group name
     * @return group prefix or {@code null}
     */
    @Nullable
    default String getGroupPrefix(String group) {
        return getGroupPrefix(null, group);
    }

    /**
     * Attempts to set a group prefix in a world context.
     *
     * @param world world context (nullable)
     * @param group group name
     * @param prefix new prefix (nullable)
     */
    void setGroupPrefix(@Nullable World world, String group, @Nullable String prefix);

    /**
     * Returns the suffix configured for a group within the given world context.
     *
     * @param world world context (nullable)
     * @param group group name
     * @return group suffix or {@code null}
     */
    @Nullable
    String getGroupSuffix(@Nullable World world, String group);

    /**
     * Convenience overload for {@link #getGroupSuffix(World, String)} with no world context.
     *
     * @param group group name
     * @return group suffix or {@code null}
     */
    @Nullable
    default String getGroupSuffix(String group) {
        return getGroupSuffix(null, group);
    }

    /**
     * Attempts to set a group suffix in a world context.
     *
     * @param world world context (nullable)
     * @param group group name
     * @param suffix new suffix (nullable)
     */
    void setGroupSuffix(@Nullable World world, String group, @Nullable String suffix);

    /**
     * Returns an integer metadata value for a player.
     *
     * @param world world context (nullable)
     * @param player target player
     * @param node metadata key
     * @param defaultValue value returned when unsupported/missing
     * @return resolved metadata value
     */
    int getMetadataInt(@Nullable World world, Player player, String node, int defaultValue);

    /**
     * Attempts to set an integer metadata value for a player.
     */
    void setMetadataInt(@Nullable World world, Player player, String node, int value);

    /**
     * Returns an integer metadata value for a group.
     */
    int getGroupMetadataInt(@Nullable World world, String group, String node, int defaultValue);

    /**
     * Attempts to set an integer metadata value for a group.
     */
    void setGroupMetadataInt(@Nullable World world, String group, String node, int value);

    /**
     * Returns a double metadata value for a player.
     */
    double getMetadataDouble(@Nullable World world, Player player, String node, double defaultValue);

    /**
     * Attempts to set a double metadata value for a player.
     */
    void setMetadataDouble(@Nullable World world, Player player, String node, double value);

    /**
     * Returns a double metadata value for a group.
     */
    double getGroupMetadataDouble(@Nullable World world, String group, String node, double defaultValue);

    /**
     * Attempts to set a double metadata value for a group.
     */
    void setGroupMetadataDouble(@Nullable World world, String group, String node, double value);

    /**
     * Returns a boolean metadata value for a player.
     */
    boolean getMetadataBoolean(@Nullable World world, Player player, String node, boolean defaultValue);

    /**
     * Attempts to set a boolean metadata value for a player.
     */
    void setMetadataBoolean(@Nullable World world, Player player, String node, boolean value);

    /**
     * Returns a boolean metadata value for a group.
     */
    boolean getGroupMetadataBoolean(@Nullable World world, String group, String node, boolean defaultValue);

    /**
     * Attempts to set a boolean metadata value for a group.
     */
    void setGroupMetadataBoolean(@Nullable World world, String group, String node, boolean value);

    /**
     * Returns a string metadata value for a player.
     */
    @Nullable
    String getMetadataString(@Nullable World world, Player player, String node, @Nullable String defaultValue);

    /**
     * Attempts to set a string metadata value for a player.
     */
    void setMetadataString(@Nullable World world, Player player, String node, @Nullable String value);

    /**
     * Returns a string metadata value for a group.
     */
    @Nullable
    String getGroupMetadataString(@Nullable World world, String group, String node, @Nullable String defaultValue);

    /**
     * Attempts to set a string metadata value for a group.
     */
    void setGroupMetadataString(@Nullable World world, String group, String node, @Nullable String value);

    /**
     * Detects the first available provider and returns a bound chat provider.
     * <p><strong>Selection order:</strong> LuckPerms -> VaultUnlocked -> Vault -> Fallback.</p>
     *
     * @return a non-null provider (never {@code null})
     */
    @NotNull
    static ChatProvider detect() {
        return ProviderServices.detect(null);
    }

    /**
     * Detects the first available provider and returns a bound chat provider.
     * <p>
     * If {@code self} is non-null and enabled, {@code self} is preferred.
     * Otherwise, provider lookup follows:
     * LuckPerms -> VaultUnlocked -> Vault -> Fallback.
     * </p>
     *
     * @param self preferred provider to use when enabled (nullable)
     * @return a non-null provider (never {@code null})
     */
    @NotNull
    static ChatProvider detect(@Nullable ChatProvider self) {
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
    static boolean register(@NotNull Plugin plugin, @NotNull ChatProvider backend) {
        return ProviderServices.register(plugin, backend);
    }

    /**
     * Unregisters a previously registered {@code backend}.
     *
     * @param backend backend to unpublish from Bukkit services
     * @return {@code true} if a registration existed and was removed
     */
    static boolean unregister(@NotNull ChatProvider backend) {
        return ProviderServices.unregister(backend);
    }
}
