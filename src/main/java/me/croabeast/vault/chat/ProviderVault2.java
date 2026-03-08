package me.croabeast.vault.chat;

import me.croabeast.vault.permission.PermissionProvider;
import net.milkbowl.vault2.chat.Chat;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

final class ProviderVault2 implements ChatProvider {

    private final Chat source;
    private final PermissionProvider permissions;

    ProviderVault2(Chat source) {
        this.source = Objects.requireNonNull(source, "source");
        this.permissions = PermissionProvider.detect();
    }

    @Override
    public boolean isEnabled() {
        return source.isEnabled();
    }

    @NotNull
    public String getName() {
        return source.getName();
    }

    @NotNull
    @Override
    public PermissionProvider getPermissionProvider() {
        return permissions;
    }

    @Nullable
    public String getPrefix(Player player) {
        try {
            return source.getPlayerPrefix(player);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nullable
    @Override
    public String getPrefix(@Nullable World world, Player player) {
        try {
            return source.getPlayerPrefix(worldName(world), player);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public void setPrefix(@Nullable World world, Player player, @Nullable String prefix) {
        try {
            source.setPlayerPrefix(worldName(world), player, prefix);
        } catch (Exception ignored) {}
    }

    @Nullable
    public String getSuffix(Player player) {
        try {
            return source.getPlayerSuffix(player);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nullable
    @Override
    public String getSuffix(@Nullable World world, Player player) {
        try {
            return source.getPlayerSuffix(worldName(world), player);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public void setSuffix(@Nullable World world, Player player, @Nullable String suffix) {
        try {
            source.setPlayerSuffix(worldName(world), player, suffix);
        } catch (Exception ignored) {}
    }

    @Nullable
    public String getGroupPrefix(@Nullable World world, String group) {
        try {
            return source.getGroupPrefix(world, group);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public void setGroupPrefix(@Nullable World world, String group, @Nullable String prefix) {
        try {
            source.setGroupPrefix(worldName(world), group, prefix);
        } catch (Exception ignored) {}
    }

    @Nullable
    public String getGroupSuffix(@Nullable World world, String group) {
        try {
            return source.getGroupSuffix(world, group);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public void setGroupSuffix(@Nullable World world, String group, @Nullable String suffix) {
        try {
            source.setGroupSuffix(worldName(world), group, suffix);
        } catch (Exception ignored) {}
    }

    @Override
    public int getMetadataInt(@Nullable World world, Player player, String node, int defaultValue) {
        try {
            return source.getPlayerInfoInteger(worldName(world), player, node, defaultValue);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    @Override
    public void setMetadataInt(@Nullable World world, Player player, String node, int value) {
        try {
            source.setPlayerInfoInteger(worldName(world), player, node, value);
        } catch (Exception ignored) {}
    }

    @Override
    public int getGroupMetadataInt(@Nullable World world, String group, String node, int defaultValue) {
        try {
            return source.getGroupInfoInteger(worldName(world), group, node, defaultValue);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    @Override
    public void setGroupMetadataInt(@Nullable World world, String group, String node, int value) {
        try {
            source.setGroupInfoInteger(worldName(world), group, node, value);
        } catch (Exception ignored) {}
    }

    @Override
    public double getMetadataDouble(@Nullable World world, Player player, String node, double defaultValue) {
        try {
            return source.getPlayerInfoDouble(worldName(world), player, node, defaultValue);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    @Override
    public void setMetadataDouble(@Nullable World world, Player player, String node, double value) {
        try {
            source.setPlayerInfoDouble(worldName(world), player, node, value);
        } catch (Exception ignored) {}
    }

    @Override
    public double getGroupMetadataDouble(@Nullable World world, String group, String node, double defaultValue) {
        try {
            return source.getGroupInfoDouble(worldName(world), group, node, defaultValue);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    @Override
    public void setGroupMetadataDouble(@Nullable World world, String group, String node, double value) {
        try {
            source.setGroupInfoDouble(worldName(world), group, node, value);
        } catch (Exception ignored) {}
    }

    @Override
    public boolean getMetadataBoolean(@Nullable World world, Player player, String node, boolean defaultValue) {
        try {
            return source.getPlayerInfoBoolean(worldName(world), player, node, defaultValue);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    @Override
    public void setMetadataBoolean(@Nullable World world, Player player, String node, boolean value) {
        try {
            source.setPlayerInfoBoolean(worldName(world), player, node, value);
        } catch (Exception ignored) {}
    }

    @Override
    public boolean getGroupMetadataBoolean(@Nullable World world, String group, String node, boolean defaultValue) {
        try {
            return source.getGroupInfoBoolean(worldName(world), group, node, defaultValue);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    @Override
    public void setGroupMetadataBoolean(@Nullable World world, String group, String node, boolean value) {
        try {
            source.setGroupInfoBoolean(worldName(world), group, node, value);
        } catch (Exception ignored) {}
    }

    @Nullable
    @Override
    public String getMetadataString(@Nullable World world, Player player, String node, @Nullable String defaultValue) {
        try {
            return source.getPlayerInfoString(worldName(world), player, node, defaultValue);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    @Override
    public void setMetadataString(@Nullable World world, Player player, String node, @Nullable String value) {
        try {
            source.setPlayerInfoString(worldName(world), player, node, value);
        } catch (Exception ignored) {}
    }

    @Nullable
    @Override
    public String getGroupMetadataString(@Nullable World world, String group, String node, @Nullable String defaultValue) {
        try {
            return source.getGroupInfoString(worldName(world), group, node, defaultValue);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    @Override
    public void setGroupMetadataString(@Nullable World world, String group, String node, @Nullable String value) {
        try {
            source.setGroupInfoString(worldName(world), group, node, value);
        } catch (Exception ignored) {}
    }

    @Nullable
    private String worldName(@Nullable World world) {
        return world == null ? null : world.getName();
    }

    @Override
    public String toString() {
        return "ChatProvider{provider='VaultUnlocked', plugin='" + source.getName() + "'}";
    }
}
