package com.nyfaria.numismaticoverhaul.mixin;

import net.minecraft.world.inventory.MerchantContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MerchantContainer.class)
public class MerchantContainerMixin {

    @Redirect(method = "setItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/MerchantContainer;getMaxStackSize()I"))
    private int redirectGetMaxStackSize(MerchantContainer merchantContainer) {
        return 99;
    }
}