package me.croabeast.vault;

import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
final class ChatVaultImpl implements ChatAdapter<Chat> {

    private final Chat source;

    ChatVaultImpl() {
        source = Objects.requireNonNull(Bukkit.getServicesManager().getRegistration(Chat.class)).getProvider();
    }

    public Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin(source.getName());
    }

    @Override
    public boolean isEnabled() {
        return source.isEnabled();
    }

    @Nullable
    public String getPrimaryGroup(Player player) {
        return source.getPrimaryGroup(player);
    }

    @Override
    public boolean isInGroup(Player player, String group) {
        return source.playerInGroup(player, group);
    }

    @NotNull
    public List<String> getGroups(Player player) {
        return Arrays.asList(source.getPlayerGroups(player));
    }

    @Nullable
    public String getPrefix(Player player) {
        return source.getPlayerPrefix(player);
    }

    @Nullable
    public String getSuffix(Player player) {
        return source.getPlayerSuffix(player);
    }

    @Nullable
    public String getGroupPrefix(World world, String group) {
        return source.getGroupPrefix(world, group);
    }

    @Nullable
    public String getGroupSuffix(World world, String group) {
        return source.getGroupSuffix(world, group);
    }

    @NotNull
    public List<String> getGroups() {
        return Arrays.asList(source.getGroups());
    }

    @Override
    public String toString() {
        return "ChatAdapter{provider='Vault', plugin='" + source.getName() + "'}";
    }
}
