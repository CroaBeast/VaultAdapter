package me.croabeast.vault.permission;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

final class ProviderLuckPerms implements PermissionProvider {

    private final LuckPerms source;
    private final Plugin plugin;

    ProviderLuckPerms(LuckPerms source, Plugin plugin) {
        this.source = Objects.requireNonNull(source, "source");
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    @Override
    public boolean isEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("LuckPerms");
    }

    @NotNull
    public String getName() {
        return "LuckPerms";
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    <T> T getAsUser(Player player, Function<User, T> function) {
        User user = source.getUserManager().getUser(player.getUniqueId());
        return user != null ? function.apply(user) : null;
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
        return getAsUser(player, User::getPrimaryGroup);
    }

    @Nullable
    @Override
    public String getPrimaryGroup(@Nullable World world, Player player) {
        return getPrimaryGroup(player);
    }

    @Override
    public boolean isInGroup(Player player, String group) {
        return getGroups(player).stream().anyMatch(g -> g.equalsIgnoreCase(group));
    }

    @Override
    public boolean isInGroup(@Nullable World world, Player player, String group) {
        return isInGroup(player, group);
    }

    @NotNull
    public List<String> getGroups(Player player) {
        List<Node> builder = getAsUser(player, u -> new ArrayList<>(u.getNodes()));
        return builder != null ?
                builder.stream().filter(n -> n instanceof InheritanceNode)
                        .map(n -> ((InheritanceNode) n).getGroupName())
                        .collect(Collectors.toList()) :
                new ArrayList<>();
    }

    @NotNull
    @Override
    public List<String> getGroups(@Nullable World world, Player player) {
        return getGroups(player);
    }

    @NotNull
    public List<String> getGroups() {
        return source.getGroupManager().getLoadedGroups()
                .stream().map(Group::getName)
                .collect(Collectors.toList());
    }

    @Override
    public boolean addPermission(@Nullable World world, Player player, String permission) {
        try {
            User user = source.getUserManager().getUser(player.getUniqueId());
            if (user == null) return false;

            boolean changed = user.data().add(permissionNode(world, permission)).wasSuccessful();
            if (changed) source.getUserManager().saveUser(user);
            return changed;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean removePermission(@Nullable World world, Player player, String permission) {
        try {
            User user = source.getUserManager().getUser(player.getUniqueId());
            if (user == null) return false;

            boolean changed = user.data().remove(permissionNode(world, permission)).wasSuccessful();
            if (changed) source.getUserManager().saveUser(user);
            return changed;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean hasGroupPermission(@Nullable World world, String group, String permission) {
        try {
            Group target = source.getGroupManager().getGroup(group);
            return target != null && target.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean addGroupPermission(@Nullable World world, String group, String permission) {
        try {
            Group target = source.getGroupManager().getGroup(group);
            if (target == null) return false;

            boolean changed = target.data().add(permissionNode(world, permission)).wasSuccessful();
            if (changed) source.getGroupManager().saveGroup(target);
            return changed;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean removeGroupPermission(@Nullable World world, String group, String permission) {
        try {
            Group target = source.getGroupManager().getGroup(group);
            if (target == null) return false;

            boolean changed = target.data().remove(permissionNode(world, permission)).wasSuccessful();
            if (changed) source.getGroupManager().saveGroup(target);
            return changed;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean addGroup(@Nullable World world, Player player, String group) {
        try {
            User user = source.getUserManager().getUser(player.getUniqueId());
            if (user == null) return false;

            boolean changed = user.data().add(groupNode(world, group)).wasSuccessful();
            if (changed) source.getUserManager().saveUser(user);
            return changed;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean removeGroup(@Nullable World world, Player player, String group) {
        try {
            User user = source.getUserManager().getUser(player.getUniqueId());
            if (user == null) return false;

            boolean changed = user.data().remove(groupNode(world, group)).wasSuccessful();
            if (changed) source.getUserManager().saveUser(user);
            return changed;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean hasGroupsSupport() {
        return true;
    }

    @NotNull
    private Node permissionNode(@Nullable World world, @NotNull String permission) {
        if (world == null) return Node.builder(permission).build();
        return Node.builder(permission).withContext("world", world.getName()).build();
    }

    @NotNull
    private InheritanceNode groupNode(@Nullable World world, @NotNull String group) {
        InheritanceNode.Builder builder = InheritanceNode.builder(group);
        if (world != null) builder.withContext("world", world.getName());
        return builder.build();
    }

    @Override
    public String toString() {
        String version = plugin.getDescription().getVersion();
        return "PermissionProvider{provider='LuckPerms', version=" + version + "}";
    }
}
