package com.nyfaria.numismaticoverhaul.mixin;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class NUSlotMixin {

    @Inject(method = "getMaxStackSize()I", at = @At("HEAD"), cancellable = true)
    private void injectOverride2(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(99);
    }
}
