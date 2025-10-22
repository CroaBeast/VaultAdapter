package me.croabeast.vault;

import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
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

@Getter
final class ChatLuckPerms implements ChatAdapter<LuckPerms> {

    private final LuckPerms source;

    ChatLuckPerms() {
        source = Objects.requireNonNull(Bukkit.getServicesManager().getRegistration(LuckPerms.class)).getProvider();
    }

    @Override
    public Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("LuckPerms");
    }

    @Override
    public boolean isEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("LuckPerms");
    }

    <T> T getAsUser(Player player, Function<User, T> function) {
        User user = source.getUserManager().getUser(player.getUniqueId());
        return user != null ? function.apply(user) : null;
    }

    @Nullable
    public String getPrimaryGroup(Player player) {
        return getAsUser(player, User::getPrimaryGroup);
    }

    @Override
    public boolean isInGroup(Player player, String group) {
        return player.hasPermission("group." + group);
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

    @Nullable
    public String getPrefix(Player player) {
        return getAsUser(player, u -> u.getCachedData().getMetaData().getPrefix());
    }

    @Nullable
    public String getSuffix(Player player) {
        return getAsUser(player, u -> u.getCachedData().getMetaData().getSuffix());
    }

    @Nullable
    public String getGroupPrefix(String group) {
        Group g = source.getGroupManager().getGroup(group);
        return g != null ? g.getCachedData().getMetaData().getPrefix() : null;
    }

    @Nullable
    public String getGroupPrefix(World world, String group) {
        return getGroupPrefix(group);
    }

    @Nullable
    public String getGroupSuffix(String group) {
        Group g = source.getGroupManager().getGroup(group);
        return g != null ? g.getCachedData().getMetaData().getSuffix() : null;
    }

    @Nullable
    public String getGroupSuffix(World world, String group) {
        return getGroupSuffix(group);
    }

    @NotNull
    public List<String> getGroups() {
        return new ArrayList<>(source.getGroupManager().getLoadedGroups())
                .stream().map(Group::getName)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "ChatAdapter{provider='LuckPerms', version=" + getPlugin().getDescription().getVersion() + "}";
    }
}
