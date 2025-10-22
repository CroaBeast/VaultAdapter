package me.croabeast.vault;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

/**
 * A thin, provider-agnostic facade for chat/permission metadata (prefixes, suffixes, groups).
 *
 * <p> Implementations are expected to bridge to a concrete provider such as
 * <em>LuckPerms</em>, <em>VaultUnlocked</em> or <em>Vault</em> (via its Chat/Permissions services).
 * This interface lets your plugin read common player/group attributes without
 * taking a hard dependency on any single ecosystem.
 *
 * <pre>{@code
 * // Example:
 * ChatAdapter<?> chat = ChatAdapter.create();
 * if (chat.isEnabled()) {
 *     String prefix = chat.getPrefix(player);
 *     String primary = chat.getPrimaryGroup(player);
 *     boolean staff  = chat.isInGroup(player, "staff");
 * }
 * }</pre>
 *
 * @param <T> the underlying provider type (e.g., LuckPerms API or Vault service)
 * @since 1.1
 */
public interface ChatAdapter<T> {

    /**
     * Returns the underlying provider instance used by this adapter.
     * <p>
     * Typical examples are an instance of the LuckPerms API or a Vault Chat/Permissions
     * service. Consumers can use {@link #fromSource(Function)} to safely extract
     * provider-specific details without introducing hard dependencies.
     * </p>
     *
     * @return the non-null underlying provider instance
     */
    @NotNull
    T getSource();

    /**
     * The plugin that owns the underlying provider, when available.
     * <p>
     * For example, the LuckPerms or Vault plugin. Fallback implementations may
     * return {@code null}.
     * </p>
     *
     * @return the provider plugin or {@code null} if unknown/unavailable
     */
    Plugin getPlugin();

    /**
     * Indicates whether the underlying provider is currently usable.
     * <p>
     * Providers may become unavailable if the backing plugin is disabled at runtime.
     * </p>
     *
     * @return {@code true} if the adapter is backed by an active provider
     */
    boolean isEnabled();

    /**
     * Applies a mapping function to the underlying provider.
     * <p>
     * Useful for optional, provider-specific access without introducing a compile-time
     * dependency in your main code path.
     * </p>
     *
     * @param <V>      result type
     * @param function the function applied to {@link #getSource()}
     * @return the function result
     */
    default <V> V fromSource(Function<T, V> function) {
        return function.apply(getSource());
    }

    /**
     * Returns the player's primary/parent group name, as defined by the provider.
     * <p>
     * The semantics of "primary" are provider-specific. May return {@code null} if
     * the concept is unsupported or no group is assigned.
     * </p>
     *
     * @param player the target player
     * @return the primary group or {@code null}
     */
    @Nullable
    String getPrimaryGroup(Player player);

    /**
     * Checks whether the player belongs to the given group (case-insensitive).
     *
     * @param player the player to check
     * @param group  group name to test membership for
     * @return {@code true} if the player is a member of {@code group}
     */
    boolean isInGroup(Player player, String group);

    /**
     * Convenience check that compares {@link #getPrimaryGroup(Player)} with {@code group}.
     *
     * @param player the player to check
     * @param group  expected primary group name
     * @return {@code true} if the player's primary group equals {@code group} (case-insensitive)
     */
    default boolean isPrimaryGroup(Player player, String group) {
        String primary = getPrimaryGroup(player);
        return primary != null && primary.equalsIgnoreCase(group);
    }

    /**
     * Returns all group names the player currently belongs to.
     * <p>
     * Ordering is unspecified. The list is typically non-null and may be empty.
     * </p>
     *
     * @param player the player
     * @return a non-null list of group names
     */
    @NotNull
    List<String> getGroups(Player player);

    /**
     * Returns the player's chat prefix, if any.
     * <p>
     * The returned value may include legacy color codes or styling tokens as provided
     * by the underlying system. Consumers should format/translate as needed.
     * </p>
     *
     * @param player the player
     * @return the prefix or {@code null} if undefined
     */
    @Nullable
    String getPrefix(Player player);

    /**
     * Returns the player's chat suffix, if any.
     * <p>
     * The returned value may include legacy color codes or styling tokens as provided
     * by the underlying system.
     * </p>
     *
     * @param player the player
     * @return the suffix or {@code null} if undefined
     */
    @Nullable
    String getSuffix(Player player);

    /**
     * Returns the prefix configured for a group within the given world context.
     * <p>
     * If {@code world} is {@code null}, implementations should resolve the "global/default"
     * context where applicable. Providers without per-world context should ignore the world.
     * </p>
     *
     * @param world world context, or {@code null} for global/default
     * @param group the group name
     * @return the group prefix or {@code null} if none
     */
    @Nullable
    String getGroupPrefix(World world, String group);

    /**
     * Convenience overload for {@link #getGroupPrefix(World, String)} with no world context.
     *
     * @param group the group name
     * @return the group prefix or {@code null} if none
     */
    @Nullable
    default String getGroupPrefix(String group) {
        return getGroupPrefix(null, group);
    }

    /**
     * Returns the suffix configured for a group within the given world context.
     * <p>
     * If {@code world} is {@code null}, implementations should resolve the "global/default"
     * context where applicable. Providers without per-world context should ignore the world.
     * </p>
     *
     * @param world world context, or {@code null} for global/default
     * @param group the group name
     * @return the group suffix or {@code null} if none
     */
    @Nullable
    String getGroupSuffix(World world, String group);

    /**
     * Convenience overload for {@link #getGroupSuffix(World, String)} with no world context.
     *
     * @param group the group name
     * @return the group suffix or {@code null} if none
     */
    @Nullable
    default String getGroupSuffix(String group) {
        return getGroupSuffix(null, group);
    }

    /**
     * Returns all known groups from the underlying provider.
     * <p>
     * Useful for administrative UIs, tab completion, or validation. Ordering is unspecified.
     * </p>
     *
     * @return a non-null list of group names (possibly empty)
     */
    @NotNull
    List<String> getGroups();

    /**
     * Creates a {@link ChatAdapter} bound to the first available provider.
     * <p><strong>Selection order:</strong> LuckPerms → VaultUnlocked → Vault → Fallback.</p>
     *
     * @return a non-null adapter (never {@code null})
     */
    @NotNull
    static ChatAdapter<?> create() {
        if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms"))
            return new ChatLuckPerms();

        if (Bukkit.getPluginManager().isPluginEnabled("VaultUnlocked"))
            return new ChatVault2();

        if (Bukkit.getPluginManager().isPluginEnabled("Vault"))
            return new ChatVaultImpl();

        return new ChatFallback();
    }
}
