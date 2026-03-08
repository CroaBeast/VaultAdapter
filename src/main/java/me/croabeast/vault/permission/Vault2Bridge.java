package me.croabeast.vault.permission;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class Vault2Bridge extends net.milkbowl.vault2.permission.Permission {

    private static final String[] EMPTY = new String[0];

    private final PermissionProvider backend;

    Vault2Bridge(PermissionProvider backend) {
        this.backend = backend;
    }

    @NotNull
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
        if (resolved == null) return false;
        return backend.hasPermission(world(world), resolved, permission);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        Player resolved = player(player);
        if (resolved == null) return false;
        return backend.addPermission(world(world), resolved, permission);
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        Player resolved = player(player);
        if (resolved == null) return false;
        return backend.removePermission(world(world), resolved, permission);
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
        if (resolved == null) return false;
        return backend.isInGroup(world(world), resolved, group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        Player resolved = player(player);
        if (resolved == null) return false;
        return backend.addGroup(world(world), resolved, group);
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        Player resolved = player(player);
        if (resolved == null) return false;
        return backend.removeGroup(world(world), resolved, group);
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        Player resolved = player(player);
        if (resolved == null) return EMPTY;
        return backend.getGroups(world(world), resolved).toArray(new String[0]);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        Player resolved = player(player);
        if (resolved == null) return null;
        return backend.getPrimaryGroup(world(world), resolved);
    }

    @Override
    public String[] getGroups() {
        return backend.getGroups().toArray(new String[0]);
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
