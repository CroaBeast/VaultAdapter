package me.croabeast.vault;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

final class ChatFallback implements ChatAdapter<Object> {

    @NotNull
    public Object getSource() {
        throw new IllegalStateException("No source was found");
    }

    @Override
    public Plugin getPlugin() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Nullable
    public String getPrimaryGroup(Player player) {
        return null;
    }

    @Override
    public boolean isInGroup(Player player, String group) {
        return false;
    }

    @NotNull
    public List<String> getGroups(Player player) {
        return new ArrayList<>();
    }

    @Nullable
    public String getPrefix(Player player) {
        return null;
    }

    @Nullable
    public String getSuffix(Player player) {
        return null;
    }

    @Nullable
    public String getGroupPrefix(World world, String group) {
        return null;
    }

    @Nullable
    public String getGroupSuffix(World world, String group) {
        return null;
    }

    @Override
    public @NotNull List<String> getGroups() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ChatAdapter{provider='NONE'}";
    }
}
