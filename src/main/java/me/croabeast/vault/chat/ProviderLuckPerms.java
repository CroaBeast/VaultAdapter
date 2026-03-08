package me.croabeast.vault.chat;

import me.croabeast.vault.permission.PermissionProvider;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

final class ProviderLuckPerms implements ChatProvider {

    private final LuckPerms source;
    private final PermissionProvider permissions;

    ProviderLuckPerms(LuckPerms source) {
        this.source = Objects.requireNonNull(source, "source");
        this.permissions = PermissionProvider.detect();
    }

    @Override
    public boolean isEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("LuckPerms");
    }

    @NotNull
    public String getName() {
        return "LuckPerms";
    }

    @NotNull
    @Override
    public PermissionProvider getPermissionProvider() {
        return permissions;
    }

    @Nullable
    private <T> T getAsUser(Player player, Function<User, T> function) {
        User user = source.getUserManager().getUser(player.getUniqueId());
        return user != null ? function.apply(user) : null;
    }

    @Nullable
    private <T> T getAsGroup(String group, Function<Group, T> function) {
        Group loaded = source.getGroupManager().getGroup(group);
        return loaded != null ? function.apply(loaded) : null;
    }

    @Nullable
    public String getPrefix(Player player) {
        return getAsUser(player, u -> u.getCachedData().getMetaData().getPrefix());
    }

    @Nullable
    @Override
    public String getPrefix(@Nullable World world, Player player) {
        return getPrefix(player);
    }

    @Override
    public void setPrefix(@Nullable World world, Player player, @Nullable String prefix) {
        setMetadataString(world, player, "prefix", prefix);
    }

    @Nullable
    public String getSuffix(Player player) {
        return getAsUser(player, u -> u.getCachedData().getMetaData().getSuffix());
    }

    @Nullable
    @Override
    public String getSuffix(@Nullable World world, Player player) {
        return getSuffix(player);
    }

    @Override
    public void setSuffix(@Nullable World world, Player player, @Nullable String suffix) {
        setMetadataString(world, player, "suffix", suffix);
    }

    @Nullable
    public String getGroupPrefix(@Nullable World world, String group) {
        return getAsGroup(group, g -> g.getCachedData().getMetaData().getPrefix());
    }

    @Override
    public void setGroupPrefix(@Nullable World world, String group, @Nullable String prefix) {
        setGroupMetadataString(world, group, "prefix", prefix);
    }

    @Nullable
    public String getGroupSuffix(@Nullable World world, String group) {
        return getAsGroup(group, g -> g.getCachedData().getMetaData().getSuffix());
    }

    @Override
    public void setGroupSuffix(@Nullable World world, String group, @Nullable String suffix) {
        setGroupMetadataString(world, group, "suffix", suffix);
    }

    @Override
    public int getMetadataInt(@Nullable World world, Player player, String node, int defaultValue) {
        String value = getMetadataString(world, player, node, null);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    @Override
    public void setMetadataInt(@Nullable World world, Player player, String node, int value) {
        setMetadataString(world, player, node, String.valueOf(value));
    }

    @Override
    public int getGroupMetadataInt(@Nullable World world, String group, String node, int defaultValue) {
        String value = getGroupMetadataString(world, group, node, null);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    @Override
    public void setGroupMetadataInt(@Nullable World world, String group, String node, int value) {
        setGroupMetadataString(world, group, node, String.valueOf(value));
    }

    @Override
    public double getMetadataDouble(@Nullable World world, Player player, String node, double defaultValue) {
        String value = getMetadataString(world, player, node, null);
        if (value == null) return defaultValue;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    @Override
    public void setMetadataDouble(@Nullable World world, Player player, String node, double value) {
        setMetadataString(world, player, node, String.valueOf(value));
    }

    @Override
    public double getGroupMetadataDouble(@Nullable World world, String group, String node, double defaultValue) {
        String value = getGroupMetadataString(world, group, node, null);
        if (value == null) return defaultValue;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    @Override
    public void setGroupMetadataDouble(@Nullable World world, String group, String node, double value) {
        setGroupMetadataString(world, group, node, String.valueOf(value));
    }

    @Override
    public boolean getMetadataBoolean(@Nullable World world, Player player, String node, boolean defaultValue) {
        String value = getMetadataString(world, player, node, null);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    @Override
    public void setMetadataBoolean(@Nullable World world, Player player, String node, boolean value) {
        setMetadataString(world, player, node, String.valueOf(value));
    }

    @Override
    public boolean getGroupMetadataBoolean(@Nullable World world, String group, String node, boolean defaultValue) {
        String value = getGroupMetadataString(world, group, node, null);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    @Override
    public void setGroupMetadataBoolean(@Nullable World world, String group, String node, boolean value) {
        setGroupMetadataString(world, group, node, String.valueOf(value));
    }

    @Nullable
    @Override
    public String getMetadataString(@Nullable World world, Player player, String node, @Nullable String defaultValue) {
        String value = getAsUser(player, u -> u.getCachedData().getMetaData().getMetaValue(node));
        return value == null ? defaultValue : value;
    }

    @Override
    public void setMetadataString(@Nullable World world, Player player, String node, @Nullable String value) {
        try {
            User user = source.getUserManager().getUser(player.getUniqueId());
            if (user == null || value == null) return;

            boolean changed = user.data().add(metaNode(world, node, value)).wasSuccessful();
            if (changed) source.getUserManager().saveUser(user);
        } catch (Exception ignored) {}
    }

    @Nullable
    @Override
    public String getGroupMetadataString(@Nullable World world, String group, String node, @Nullable String defaultValue) {
        String value = getAsGroup(group, g -> g.getCachedData().getMetaData().getMetaValue(node));
        return value == null ? defaultValue : value;
    }

    @Override
    public void setGroupMetadataString(@Nullable World world, String group, String node, @Nullable String value) {
        try {
            Group loaded = source.getGroupManager().getGroup(group);
            if (loaded == null || value == null) return;

            boolean changed = loaded.data().add(metaNode(world, node, value)).wasSuccessful();
            if (changed) source.getGroupManager().saveGroup(loaded);
        } catch (Exception ignored) {}
    }

    @NotNull
    private MetaNode metaNode(@Nullable World world, @NotNull String node, @NotNull String value) {
        MetaNode.Builder builder = MetaNode.builder(node, value);
        if (world != null) builder.withContext("world", world.getName());
        return builder.build();
    }

    @Override
    public String toString() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("LuckPerms");
        String version = plugin == null ? "unknown" : plugin.getDescription().getVersion();
        return "ChatProvider{provider='LuckPerms', version=" + version + "}";
    }
}
