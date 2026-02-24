package me.croabeast.vault;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Common contract shared by adapter abstractions in this module.
 *
 * <p>Implementations expose a concrete provider instance and its availability state,
 * while still allowing provider-specific access through {@link #fromSource(Function)}
 * without hard-coupling call sites to a single implementation.
 * </p>
 *
 * @param <T> the underlying provider type
 * @since 1.1
 */
public interface BaseAdapter<T> {

    /**
     * Returns the underlying provider instance used by this adapter.
     *
     * @return the non-null provider instance
     */
    @NotNull
    T getSource();

    /**
     * The plugin that owns or supplies the provider, when available.
     * <p>
     * Implementations may return {@code null} for fallback/no-op adapters.
     * </p>
     *
     * @return the backing plugin or {@code null} if unavailable
     */
    Plugin getPlugin();

    /**
     * Indicates whether the underlying provider is currently usable.
     *
     * @return {@code true} if the adapter is backed by an active provider
     */
    boolean isEnabled();

    /**
     * Applies a mapping function to {@link #getSource()}.
     * <p>
     * Useful for extracting provider-specific details while keeping the caller
     * API-facing and implementation-agnostic.
     * </p>
     *
     * @param <V>      result type
     * @param function function applied to the underlying provider
     * @return the function result
     */
    default <V> V fromSource(Function<T, V> function) {
        return function.apply(this.getSource());
    }
}
