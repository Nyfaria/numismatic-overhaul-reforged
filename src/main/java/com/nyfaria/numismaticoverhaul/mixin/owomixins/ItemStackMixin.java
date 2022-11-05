package com.nyfaria.numismaticoverhaul.mixin.owomixins;

import com.nyfaria.numismaticoverhaul.owostuff.nbt.NbtCarrier;
import com.nyfaria.numismaticoverhaul.owostuff.nbt.NbtKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements NbtCarrier {

    @Shadow
    private @Nullable CompoundTag nbt;

    @Shadow
    public abstract CompoundTag getOrCreateNbt();

    @Override
    public <T> T get(@NotNull NbtKey<T> key) {
        return key.get(this.getOrCreateNbt());
    }

    @Override
    public <T> void put(@NotNull NbtKey<T> key, @NotNull T value) {
        key.put(this.getOrCreateNbt(), value);
    }

    @Override
    public <T> void delete(@NotNull NbtKey<T> key) {
        if (this.nbt == null) return;
        key.delete(this.nbt);
    }

    @Override
    public <T> boolean has(@NotNull NbtKey<T> key) {
        return this.nbt != null && key.isIn(this.nbt);
    }
}
