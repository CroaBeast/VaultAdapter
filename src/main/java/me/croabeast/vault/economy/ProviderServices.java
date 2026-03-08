package me.croabeast.vault.economy;

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

    private static final Map<EconomyProvider, ServiceRegistration> REGISTRATIONS = new IdentityHashMap<>();

    private ProviderServices() {}

    @NotNull
    static EconomyProvider detect(@Nullable EconomyProvider self) {
        if (self != null && self.isEnabled()) return self;

        EconomyProvider fromVault2 = detectVault2();
        if (fromVault2 != null) return fromVault2;

        EconomyProvider fromVault = detectVault();
        return fromVault == null ? ProviderFallback.INSTANCE : fromVault;
    }

    static boolean register(@NotNull Plugin plugin, @NotNull EconomyProvider backend) {
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

    static boolean unregister(@NotNull EconomyProvider backend) {
        Objects.requireNonNull(backend, "backend");

        synchronized (REGISTRATIONS) {
            return unregister0(backend);
        }
    }

    private static boolean unregister0(EconomyProvider backend) {
        ServiceRegistration registration = REGISTRATIONS.remove(backend);
        if (registration == null) return false;

        Bukkit.getServicesManager().unregister(registration.serviceType, registration.provider);
        return true;
    }

    @Nullable
    private static EconomyProvider detectVault2() {
        try {
            Class<?> type = Class.forName("net.milkbowl.vault2.economy.Economy");
            RegisteredServiceProvider<?> registration = Bukkit.getServicesManager().getRegistration(type);
            if (registration == null) return null;

            Object provider = registration.getProvider();
            if (provider instanceof net.milkbowl.vault2.economy.Economy)
                return new ProviderVault2((net.milkbowl.vault2.economy.Economy) provider);
        } catch (Throwable ignored) {}

        return null;
    }

    @Nullable
    private static EconomyProvider detectVault() {
        try {
            Class<?> type = Class.forName("net.milkbowl.vault.economy.Economy");
            RegisteredServiceProvider<?> registration = Bukkit.getServicesManager().getRegistration(type);
            if (registration == null) return null;

            Object provider = registration.getProvider();
            if (provider instanceof net.milkbowl.vault.economy.Economy)
                return new ProviderVault((net.milkbowl.vault.economy.Economy) provider);
        } catch (Throwable ignored) {}

        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ServiceRegistration registerVault2(Plugin plugin, EconomyProvider backend) {
        try {
            Class<?> serviceType = Class.forName("net.milkbowl.vault2.economy.Economy");
            Object bridge = new Vault2Bridge(backend);
            Bukkit.getServicesManager().register((Class) serviceType, bridge, plugin, ServicePriority.Highest);
            return new ServiceRegistration(serviceType, bridge);
        } catch (Throwable ignored) {
            return null;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ServiceRegistration registerVault(Plugin plugin, EconomyProvider backend) {
        try {
            Class<?> serviceType = Class.forName("net.milkbowl.vault.economy.Economy");
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

