package me.croabeast.vault.permission;

import org.bukkit.command.CommandSender;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

final class ProviderFallback implements PermissionProvider {

    @Override
    public boolean isEnabled() {
        return false;
    }

    @NotNull
    public String getName() {
        return "None";
    }

    @Override
    public boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean hasPermission(@Nullable World world, Player player, String permission) {
        return hasPermission(player, permission);
    }

    @Nullable
    public String getPrimaryGroup(Player player) {
        return null;
    }

    @Nullable
    @Override
    public String getPrimaryGroup(@Nullable World world, Player player) {
        return getPrimaryGroup(player);
    }

    @Override
    public boolean isInGroup(Player player, String group) {
        return false;
    }

    @Override
    public boolean isInGroup(@Nullable World world, Player player, String group) {
        return false;
    }

    @NotNull
    public List<String> getGroups(Player player) {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public List<String> getGroups(@Nullable World world, Player player) {
        return Collections.emptyList();
    }

    @NotNull
    public List<String> getGroups() {
        return Collections.emptyList();
    }

    @Override
    public boolean addPermission(@Nullable World world, Player player, String permission) {
        return false;
    }

    @Override
    public boolean removePermission(@Nullable World world, Player player, String permission) {
        return false;
    }

    @Override
    public boolean hasGroupPermission(@Nullable World world, String group, String permission) {
        return false;
    }

    @Override
    public boolean addGroupPermission(@Nullable World world, String group, String permission) {
        return false;
    }

    @Override
    public boolean removeGroupPermission(@Nullable World world, String group, String permission) {
        return false;
    }

    @Override
    public boolean addGroup(@Nullable World world, Player player, String group) {
        return false;
    }

    @Override
    public boolean removeGroup(@Nullable World world, Player player, String group) {
        return false;
    }

    @Override
    public boolean hasGroupsSupport() {
        return false;
    }

    @Override
    public String toString() {
        return "PermissionProvider{provider='NONE'}";
    }
}
