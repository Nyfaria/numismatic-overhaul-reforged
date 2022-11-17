package com.nyfaria.numismaticoverhaul.mixin;

import com.nyfaria.numismaticoverhaul.item.CoinItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MerchantOffer.class)
public abstract class MerchantOfferMixin {

    @Redirect(method = "isRequiredItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasTag()Z"))
    private boolean hasTag(ItemStack stack) {
        return !(stack.getItem() instanceof CoinItem) && stack.hasTag();
    }
}