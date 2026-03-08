package me.croabeast.vault.chat;

import me.croabeast.vault.permission.PermissionProvider;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ProviderFallback implements ChatProvider {

    private final PermissionProvider permissions = PermissionProvider.detect();

    @Override
    public boolean isEnabled() {
        return false;
    }

    @NotNull
    public String getName() {
        return "None";
    }

    @Override
    public Plugin getPlugin() {
        return null;
    }

    @NotNull
    @Override
    public PermissionProvider getPermissionProvider() {
        return permissions;
    }

    @Nullable
    public String getPrefix(Player player) {
        return null;
    }

    @Nullable
    @Override
    public String getPrefix(@Nullable World world, Player player) {
        return getPrefix(player);
    }

    @Override
    public void setPrefix(@Nullable World world, Player player, @Nullable String prefix) {}

    @Nullable
    public String getSuffix(Player player) {
        return null;
    }

    @Nullable
    @Override
    public String getSuffix(@Nullable World world, Player player) {
        return getSuffix(player);
    }

    @Override
    public void setSuffix(@Nullable World world, Player player, @Nullable String suffix) {}

    @Nullable
    public String getGroupPrefix(@Nullable World world, String group) {
        return null;
    }

    @Override
    public void setGroupPrefix(@Nullable World world, String group, @Nullable String prefix) {}

    @Nullable
    public String getGroupSuffix(@Nullable World world, String group) {
        return null;
    }

    @Override
    public void setGroupSuffix(@Nullable World world, String group, @Nullable String suffix) {}

    @Override
    public int getMetadataInt(@Nullable World world, Player player, String node, int defaultValue) {
        return defaultValue;
    }

    @Override
    public void setMetadataInt(@Nullable World world, Player player, String node, int value) {}

    @Override
    public int getGroupMetadataInt(@Nullable World world, String group, String node, int defaultValue) {
        return defaultValue;
    }

    @Override
    public void setGroupMetadataInt(@Nullable World world, String group, String node, int value) {}

    @Override
    public double getMetadataDouble(@Nullable World world, Player player, String node, double defaultValue) {
        return defaultValue;
    }

    @Override
    public void setMetadataDouble(@Nullable World world, Player player, String node, double value) {}

    @Override
    public double getGroupMetadataDouble(@Nullable World world, String group, String node, double defaultValue) {
        return defaultValue;
    }

    @Override
    public void setGroupMetadataDouble(@Nullable World world, String group, String node, double value) {}

    @Override
    public boolean getMetadataBoolean(@Nullable World world, Player player, String node, boolean defaultValue) {
        return defaultValue;
    }

    @Override
    public void setMetadataBoolean(@Nullable World world, Player player, String node, boolean value) {}

    @Override
    public boolean getGroupMetadataBoolean(@Nullable World world, String group, String node, boolean defaultValue) {
        return defaultValue;
    }

    @Override
    public void setGroupMetadataBoolean(@Nullable World world, String group, String node, boolean value) {}

    @Nullable
    @Override
    public String getMetadataString(@Nullable World world, Player player, String node, @Nullable String defaultValue) {
        return defaultValue;
    }

    @Override
    public void setMetadataString(@Nullable World world, Player player, String node, @Nullable String value) {}

    @Nullable
    @Override
    public String getGroupMetadataString(@Nullable World world, String group, String node, @Nullable String defaultValue) {
        return defaultValue;
    }

    @Override
    public void setGroupMetadataString(@Nullable World world, String group, String node, @Nullable String value) {}

    @Override
    public String toString() {
        return "ChatProvider{provider='NONE'}";
    }
}
