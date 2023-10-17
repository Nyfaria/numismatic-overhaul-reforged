package com.nyfaria.numismaticoverhaul.mixin.ninetynine;

import com.nyfaria.numismaticoverhaul.item.CoinItem;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public abstract class ContainerMixin implements Container {
    @Inject(method = "hasRemainingSpaceForItem", at = @At("HEAD"), cancellable = true)
    private void increaseStackLimit(ItemStack pDestination, ItemStack pOrigin, CallbackInfoReturnable<Boolean> cir) {
        boolean room = pDestination.getCount() < this.getMaxStackSize();
        if (pOrigin.getItem() instanceof CoinItem && pDestination.getItem() instanceof CoinItem) {
            room = pDestination.getCount() + pOrigin.getCount() <= 99;
        }
        cir.setReturnValue(!pDestination.isEmpty() && ItemStack.isSameItemSameTags(pDestination, pOrigin) && pDestination.isStackable() && pDestination.getCount() < pDestination.getMaxStackSize() && room);
    }
}
