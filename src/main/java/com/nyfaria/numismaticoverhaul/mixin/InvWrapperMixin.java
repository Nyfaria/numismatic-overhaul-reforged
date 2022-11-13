package com.nyfaria.numismaticoverhaul.mixin;

import net.minecraftforge.items.wrapper.InvWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InvWrapper.class)
public class InvWrapperMixin
{
    @Redirect(method = "insertItem",
              at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/wrapper/InvWrapper;getSlotLimit(I)I"),
              remap = false)
    private int increaseStackLimit(InvWrapper instance, int slot)
    {
        return 99;
    }
}