package me.croabeast.vault.permission;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

final class ProviderServices {

    private static final Map<PermissionProvider, ServiceRegistration> REGISTRATIONS = new IdentityHashMap<>();

    private ProviderServices() {}

    @NotNull
    static PermissionProvider detect(@Nullable PermissionProvider self) {
        if (self != null && self.isEnabled()) return self;

        PermissionProvider fromLuckPerms = detectLuckPerms();
        if (fromLuckPerms != null) return fromLuckPerms;

        PermissionProvider fromVault2 = detectVault2();
        if (fromVault2 != null) return fromVault2;

        PermissionProvider fromVault = detectVault();
        return fromVault == null ? new ProviderFallback() : fromVault;
    }

    static boolean register(@NotNull Plugin plugin, @NotNull PermissionProvider backend) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(backend, "backend");

        synchronized (REGISTRATIONS) {
            unregister0(backend);

            ServiceRegistration vault2 = registerVault2(plugin, backend);
            if (vault2 != null) {
                REGISTRATIONS.put(backend, vault2);
                return true;
            }

            ServiceRegistration vault = registerVault(plugin, backend);
            if (vault != null) {
                REGISTRATIONS.put(backend, vault);
                return true;
            }

            return false;
        }
    }

    static boolean unregister(@NotNull PermissionProvider backend) {
        Objects.requireNonNull(backend, "backend");

        synchronized (REGISTRATIONS) {
            return unregister0(backend);
        }
    }

    private static boolean unregister0(PermissionProvider backend) {
        ServiceRegistration registration = REGISTRATIONS.remove(backend);
        if (registration == null) return false;

        Bukkit.getServicesManager().unregister(registration.serviceType, registration.provider);
        return true;
    }

    @Nullable
    private static PermissionProvider detectLuckPerms() {
        try {
            Class<?> type = Class.forName("net.luckperms.api.LuckPerms");
            RegisteredServiceProvider<?> registration = Bukkit.getServicesManager().getRegistration(type);
            if (registration == null) return null;

            Object provider = registration.getProvider();
            if (provider instanceof net.luckperms.api.LuckPerms)
                return new ProviderLuckPerms((net.luckperms.api.LuckPerms) provider, registration.getPlugin());
        } catch (Throwable ignored) {}

        return null;
    }

    @Nullable
    private static PermissionProvider detectVault2() {
        try {
            Class<?> type = Class.forName("net.milkbowl.vault2.permission.Permission");
            RegisteredServiceProvider<?> registration = Bukkit.getServicesManager().getRegistration(type);
            if (registration == null) return null;

            Object provider = registration.getProvider();
            if (provider instanceof net.milkbowl.vault2.permission.Permission)
                return new ProviderVault2((net.milkbowl.vault2.permission.Permission) provider, registration.getPlugin());
        } catch (Throwable ignored) {}

        return null;
    }

    @Nullable
    private static PermissionProvider detectVault() {
        try {
            Class<?> type = Class.forName("net.milkbowl.vault.permission.Permission");
            RegisteredServiceProvider<?> registration = Bukkit.getServicesManager().getRegistration(type);
            if (registration == null) return null;

            Object provider = registration.getProvider();
            if (provider instanceof net.milkbowl.vault.permission.Permission)
                return new ProviderVault((net.milkbowl.vault.permission.Permission) provider, registration.getPlugin());
        } catch (Throwable ignored) {}

        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ServiceRegistration registerVault2(Plugin plugin, PermissionProvider backend) {
        try {
            Class<?> serviceType = Class.forName("net.milkbowl.vault2.permission.Permission");
            Object bridge = new Vault2Bridge(backend);
            Bukkit.getServicesManager().register((Class) serviceType, bridge, plugin, ServicePriority.Highest);
            return new ServiceRegistration(serviceType, bridge);
        } catch (Throwable ignored) {
            return null;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ServiceRegistration registerVault(Plugin plugin, PermissionProvider backend) {
        try {
            Class<?> serviceType = Class.forName("net.milkbowl.vault.permission.Permission");
            Object bridge = new VaultBridge(backend);
            Bukkit.getServicesManager().register((Class) serviceType, bridge, plugin, ServicePriority.Highest);
            return new ServiceRegistration(serviceType, bridge);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static final class ServiceRegistration {
        private final Class<?> serviceType;
        private final Object provider;

        private ServiceRegistration(Class<?> serviceType, Object provider) {
            this.serviceType = serviceType;
            this.provider = provider;
        }
    }
}
