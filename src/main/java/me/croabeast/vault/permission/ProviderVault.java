package me.croabeast.vault.permission;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class ProviderVault implements PermissionProvider {

    private final Permission source;
    private final Plugin plugin;

    ProviderVault(Permission source, Plugin plugin) {
        this.source = Objects.requireNonNull(source, "source");
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    @Override
    public boolean isEnabled() {
        return source.isEnabled();
    }

    @NotNull
    public String getName() {
        return source.getName();
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public boolean hasPermission(CommandSender sender, String permission) {
        try {
            return sender instanceof Player ?
                    source.playerHas((Player) sender, permission) :
                    source.has(sender, permission);
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean hasPermission(@Nullable World world, Player player, String permission) {
        try {
            return source.playerHas(worldName(world), player, permission);
        } catch (Exception ignored) {
            return false;
        }
    }

    @Nullable
    public String getPrimaryGroup(Player player) {
        try {
            return source.getPrimaryGroup(player);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nullable
    @Override
    public String getPrimaryGroup(@Nullable World world, Player player) {
        try {
            return source.getPrimaryGroup(worldName(world), player);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public boolean isInGroup(Player player, String group) {
        try {
            return source.playerInGroup(player, group);
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean isInGroup(@Nullable World world, Player player, String group) {
        try {
            return source.playerInGroup(worldName(world), player, group);
        } catch (Exception ignored) {
            return false;
        }
    }

    @NotNull
    public List<String> getGroups(Player player) {
        try {
            String[] groups = source.getPlayerGroups(player);
            return groups == null ? Collections.emptyList() : Arrays.asList(groups);
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    @NotNull
    @Override
    public List<String> getGroups(@Nullable World world, Player player) {
        try {
            String[] groups = source.getPlayerGroups(worldName(world), player);
            return groups == null ? Collections.emptyList() : Arrays.asList(groups);
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    @NotNull
    public List<String> getGroups() {
        try {
            String[] groups = source.getGroups();
            return groups == null ? Collections.emptyList() : Arrays.asList(groups);
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean addPermission(@Nullable World world, Player player, String permission) {
        try {
            return source.playerAdd(worldName(world), player, permission);
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean removePermission(@Nullable World world, Player player, String permission) {
        try {
            return source.playerRemove(worldName(world), player, permission);
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean hasGroupPermission(@Nullable World world, String group, String permission) {
        try {
            return source.groupHas(worldName(world), group, permission);
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean addGroupPermission(@Nullable World world, String group, String permission) {
        try {
            return source.groupAdd(worldName(world), group, permission);
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean removeGroupPermission(@Nullable World world, String group, String permission) {
        try {
            return source.groupRemove(worldName(world), group, permission);
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean addGroup(@Nullable World world, Player player, String group) {
        try {
            return source.playerAddGroup(worldName(world), player, group);
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean removeGroup(@Nullable World world, Player player, String group) {
        try {
            return source.playerRemoveGroup(worldName(world), player, group);
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean hasGroupsSupport() {
        try {
            return source.hasGroupSupport();
        } catch (Exception ignored) {
            return false;
        }
    }

    @Nullable
    private String worldName(@Nullable World world) {
        return world == null ? null : world.getName();
    }

    @Override
    public String toString() {
        return "PermissionProvider{provider='Vault', plugin='" + source.getName() + "'}";
    }
}
