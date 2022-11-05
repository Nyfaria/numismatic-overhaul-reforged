package com.nyfaria.numismaticoverhaul.owostuff.util;

import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class RegistryAccess {

    private RegistryAccess() {}

    /**
     * Gets a {@link Holder} from its id
     *
     * @param registry The registry to operate on. Must be a {@link MappedRegistry} at some point in the hierarchy
     * @param id       The id to use
     * @param <T>      The type of the registry and returned entry
     * @return The entry, or {@code null} if it's not present
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> Holder<T> getEntry(Registry<T> registry, ResourceLocation id) {
        checkSimple(registry);
        return ((AccessibleRegistry<T>) registry).getEntry(id);
    }

    /**
     * Gets a {@link Holder} from its value
     *
     * @param registry The registry to operate on. Must be a {@link MappedRegistry} at some point in the hierarchy
     * @param value    The value to use
     * @param <T>      The type of the registry and returned entry
     * @return The entry, or {@code null} if it's not present
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> Holder<T> getEntry(Registry<T> registry, T value) {
        checkSimple(registry);
        return ((AccessibleRegistry<T>) registry).getEntry(value);
    }

    private static void checkSimple(Registry<?> registry) {
        if (registry instanceof MappedRegistry<?>) return;
        throw new IllegalArgumentException("[RegistryAccess] Tried to operate on Registry of class '"
                + registry.getClass() + "', but only 'SimpleRegistry' and descendants are supported");
    }

    public interface AccessibleRegistry<T> {
        @Nullable Holder<T> getEntry(ResourceLocation id);

        @Nullable Holder<T> getEntry(T value);
    }

}
