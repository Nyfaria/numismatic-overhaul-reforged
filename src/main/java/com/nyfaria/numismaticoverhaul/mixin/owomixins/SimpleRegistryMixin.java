package com.nyfaria.numismaticoverhaul.mixin.owomixins;

import com.nyfaria.numismaticoverhaul.owostuff.util.RegistryAccess;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(MappedRegistry.class)
public class SimpleRegistryMixin<T> implements RegistryAccess.AccessibleRegistry<T> {

    @Shadow
    @Final
    private Map<ResourceLocation, Holder.Reference<T>> idToEntry;

    @Shadow
    @Final
    private Map<T, Holder.Reference<T>> valueToEntry;

    @Override
    public @Nullable Holder<T> getEntry(ResourceLocation id) {
        return this.idToEntry.get(id);
    }

    @Override
    public @Nullable Holder<T> getEntry(T value) {
        return this.valueToEntry.get(value);
    }
}
