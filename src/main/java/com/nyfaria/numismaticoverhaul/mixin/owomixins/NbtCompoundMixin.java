package com.nyfaria.numismaticoverhaul.mixin.owomixins;

import com.nyfaria.numismaticoverhaul.owostuff.nbt.NbtCarrier;
import com.nyfaria.numismaticoverhaul.owostuff.nbt.NbtKey;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CompoundTag.class)
public abstract class NbtCompoundMixin implements NbtCarrier {
    @Shadow
    public abstract boolean contains(String key, int type);

    @Override
    public <T> T get(@NotNull NbtKey<T> key) {
        return key.get((CompoundTag) (Object) this);
    }

    @Override
    public <T> void put(@NotNull NbtKey<T> key, @NotNull T value) {
        key.put((CompoundTag) (Object) this, value);
    }

    @Override
    public <T> void delete(@NotNull NbtKey<T> key) {
        key.delete((CompoundTag) (Object) this);
    }

    @Override
    public <T> boolean has(@NotNull NbtKey<T> key) {
        return key.isIn((CompoundTag) (Object) this);
    }
}
