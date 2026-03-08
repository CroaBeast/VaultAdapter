package me.croabeast.vault.chat;

import me.croabeast.vault.permission.PermissionProvider;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

final class Vault2Bridge extends net.milkbowl.vault2.chat.Chat {

    private final ChatProvider backend;

    Vault2Bridge(ChatProvider backend) {
        super(new PermissionDelegate(backend));
        this.backend = backend;
    }

    public String getName() {
        return backend.getName();
    }

    @Override
    public boolean isEnabled() {
        return backend.isEnabled();
    }

    @Override
    public String getPlayerPrefix(String world, String player) {
        Player resolved = player(player);
        return resolved == null ? null : backend.getPrefix(world(world), resolved);
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        Player resolved = player(player);
        if (resolved != null) backend.setPrefix(world(world), resolved, prefix);
    }

    @Override
    public String getPlayerSuffix(String world, String player) {
        Player resolved = player(player);
        return resolved == null ? null : backend.getSuffix(world(world), resolved);
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
        Player resolved = player(player);
        if (resolved != null) backend.setSuffix(world(world), resolved, suffix);
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        return backend.getGroupPrefix(world(world), group);
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        backend.setGroupPrefix(world(world), group, prefix);
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        return backend.getGroupSuffix(world(world), group);
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        backend.setGroupSuffix(world(world), group, suffix);
    }

    @Override
    public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
        Player resolved = player(player);
        return resolved == null ? defaultValue : backend.getMetadataInt(world(world), resolved, node, defaultValue);
    }

    @Override
    public void setPlayerInfoInteger(String world, String player, String node, int value) {
        Player resolved = player(player);
        if (resolved != null) backend.setMetadataInt(world(world), resolved, node, value);
    }

    @Override
    public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
        return backend.getGroupMetadataInt(world(world), group, node, defaultValue);
    }

    @Override
    public void setGroupInfoInteger(String world, String group, String node, int value) {
        backend.setGroupMetadataInt(world(world), group, node, value);
    }

    @Override
    public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
        Player resolved = player(player);
        return resolved == null ? defaultValue : backend.getMetadataDouble(world(world), resolved, node, defaultValue);
    }

    @Override
    public void setPlayerInfoDouble(String world, String player, String node, double value) {
        Player resolved = player(player);
        if (resolved != null) backend.setMetadataDouble(world(world), resolved, node, value);
    }

    @Override
    public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
        return backend.getGroupMetadataDouble(world(world), group, node, defaultValue);
    }

    @Override
    public void setGroupInfoDouble(String world, String group, String node, double value) {
        backend.setGroupMetadataDouble(world(world), group, node, value);
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) {
        Player resolved = player(player);
        return resolved == null ? defaultValue : backend.getMetadataBoolean(world(world), resolved, node, defaultValue);
    }

    @Override
    public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {
        Player resolved = player(player);
        if (resolved != null) backend.setMetadataBoolean(world(world), resolved, node, value);
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
        return backend.getGroupMetadataBoolean(world(world), group, node, defaultValue);
    }

    @Override
    public void setGroupInfoBoolean(String world, String group, String node, boolean value) {
        backend.setGroupMetadataBoolean(world(world), group, node, value);
    }

    @Override
    public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
        Player resolved = player(player);
        return resolved == null ? defaultValue : backend.getMetadataString(world(world), resolved, node, defaultValue);
    }

    @Override
    public void setPlayerInfoString(String world, String player, String node, String value) {
        Player resolved = player(player);
        if (resolved != null) backend.setMetadataString(world(world), resolved, node, value);
    }

    @Override
    public String getGroupInfoString(String world, String group, String node, String defaultValue) {
        return backend.getGroupMetadataString(world(world), group, node, defaultValue);
    }

    @Override
    public void setGroupInfoString(String world, String group, String node, String value) {
        backend.setGroupMetadataString(world(world), group, node, value);
    }

    @Nullable
    private World world(@Nullable String name) {
        return name == null ? null : Bukkit.getWorld(name);
    }

    @Nullable
    private Player player(@Nullable String name) {
        return name == null || name.isEmpty() ? null : Bukkit.getPlayerExact(name);
    }

    private static final class PermissionDelegate extends net.milkbowl.vault2.permission.Permission {

        private static final String[] EMPTY = new String[0];

        private final PermissionProvider backend;

        private PermissionDelegate(ChatProvider backend) {
            this.backend = backend.getPermissionProvider();
        }

        public String getName() {
            return backend.getName();
        }

        @Override
        public boolean isEnabled() {
            return backend.isEnabled();
        }

        @Override
        public boolean hasSuperPermsCompat() {
            return true;
        }

        @Override
        public boolean playerHas(String world, String player, String permission) {
            Player resolved = player(player);
            return resolved != null && backend.hasPermission(world(world), resolved, permission);
        }

        @Override
        public boolean playerAdd(String world, String player, String permission) {
            Player resolved = player(player);
            return resolved != null && backend.addPermission(world(world), resolved, permission);
        }

        @Override
        public boolean playerRemove(String world, String player, String permission) {
            Player resolved = player(player);
            return resolved != null && backend.removePermission(world(world), resolved, permission);
        }

        @Override
        public boolean groupHas(String world, String group, String permission) {
            return backend.hasGroupPermission(world(world), group, permission);
        }

        @Override
        public boolean groupAdd(String world, String group, String permission) {
            return backend.addGroupPermission(world(world), group, permission);
        }

        @Override
        public boolean groupRemove(String world, String group, String permission) {
            return backend.removeGroupPermission(world(world), group, permission);
        }

        @Override
        public boolean playerInGroup(String world, String player, String group) {
            Player resolved = player(player);
            return resolved != null && backend.isInGroup(world(world), resolved, group);
        }

        @Override
        public boolean playerAddGroup(String world, String player, String group) {
            Player resolved = player(player);
            return resolved != null && backend.addGroup(world(world), resolved, group);
        }

        @Override
        public boolean playerRemoveGroup(String world, String player, String group) {
            Player resolved = player(player);
            return resolved != null && backend.removeGroup(world(world), resolved, group);
        }

        @Override
        public String[] getPlayerGroups(String world, String player) {
            Player resolved = player(player);
            if (resolved == null) return EMPTY;

            List<String> groups = backend.getGroups(world(world), resolved);
            return groups.toArray(new String[0]);
        }

        @Override
        public String getPrimaryGroup(String world, String player) {
            Player resolved = player(player);
            return resolved == null ? null : backend.getPrimaryGroup(world(world), resolved);
        }

        @Override
        public String[] getGroups() {
            List<String> groups = backend.getGroups();
            return groups.toArray(new String[0]);
        }

        @Override
        public boolean hasGroupSupport() {
            return backend.hasGroupsSupport();
        }

        @Nullable
        private World world(@Nullable String name) {
            return name == null ? null : Bukkit.getWorld(name);
        }

        @Nullable
        private Player player(@Nullable String name) {
            return name == null || name.isEmpty() ? null : Bukkit.getPlayerExact(name);
        }
    }
}

