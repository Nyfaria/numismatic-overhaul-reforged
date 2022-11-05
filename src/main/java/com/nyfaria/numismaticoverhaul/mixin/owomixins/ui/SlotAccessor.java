package com.nyfaria.numismaticoverhaul.mixin.owomixins.ui;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Slot.class)
public interface SlotAccessor {
    @Mutable
    @Accessor("x")
    void owo$setX(int x);

    @Mutable
    @Accessor("y")
    void owo$setY(int y);
}
