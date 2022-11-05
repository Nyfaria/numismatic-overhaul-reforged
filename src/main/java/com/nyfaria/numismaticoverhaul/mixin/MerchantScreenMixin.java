package com.nyfaria.numismaticoverhaul.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nyfaria.numismaticoverhaul.item.CurrencyItem;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends Screen {

    private MerchantScreenMixin(Component title) {
        super(title);
    }

    private ItemStack numismatic$adjustedFirstBuyItem;
    private ItemStack numismatic$originalFirstBuyItem;

    @Inject(method = "renderAndDecorateCostA", at = @At("HEAD"))
    private void captureFirstBuyItem(PoseStack matrices, ItemStack adjustedFirstBuyItem, ItemStack originalFirstBuyItem, int x, int y, CallbackInfo ci) {
        this.numismatic$originalFirstBuyItem = originalFirstBuyItem;
        this.numismatic$adjustedFirstBuyItem = adjustedFirstBuyItem;
    }

    @ModifyVariable(method = "renderAndDecorateCostA", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private ItemStack dontShowBagDiscount(ItemStack original) {
        var adjustedItem = numismatic$adjustedFirstBuyItem.getItem();

        if (adjustedItem instanceof CurrencyItem adjustable && adjustable.wasAdjusted(numismatic$originalFirstBuyItem)) {
            var copy = this.numismatic$adjustedFirstBuyItem;
            this.numismatic$adjustedFirstBuyItem = null;
            return copy;
        } else {
            this.numismatic$adjustedFirstBuyItem = null;
            return original;
        }
    }
}
