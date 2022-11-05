package com.nyfaria.numismaticoverhaul.mixin;

import com.nyfaria.numismaticoverhaul.currency.CurrencyHelper;
import com.nyfaria.numismaticoverhaul.item.CurrencyItem;
import com.nyfaria.numismaticoverhaul.villagers.data.NumismaticTradeOfferExtensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collections;

@Mixin(MerchantOffer.class)
public class TradeOfferMixin implements NumismaticTradeOfferExtensions {

    @Shadow
    @Final
    private ItemStack baseCostA;
    private int numismatic$reputation = 0;

    @Override
    public void numismatic$setReputation(int reputation) {
        this.numismatic$reputation = reputation;
    }

    @Override
    public int numismatic$getReputation() {
        return numismatic$reputation;
    }

    @Inject(method = "createTag", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void saveReputation(CallbackInfoReturnable<CompoundTag> cir, CompoundTag nbt) {
        nbt.putInt("Reputation", numismatic$reputation);
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"))
    private void loadReputation(CompoundTag nbt, CallbackInfo ci) {
        this.numismatic$reputation = nbt.getInt("Reputation");
    }

    @Inject(method = "getCostA", at = @At("HEAD"), cancellable = true)
    private void adjustFirstStack(CallbackInfoReturnable<ItemStack> cir) {
        if (this.numismatic$reputation == -69420) return;

        if (!(this.baseCostA.getItem() instanceof CurrencyItem currencyItem)) return;

        long originalValue = currencyItem.getValue(this.baseCostA);
        long adjustedValue = numismatic$reputation < 0
                ? (long) (originalValue + Math.abs(numismatic$reputation) * (Math.abs(originalValue) * .02))
                : (long) Math.max(1, originalValue - Math.abs(originalValue) * (numismatic$reputation / (numismatic$reputation + 100f)));

        adjustedValue = Math.min(adjustedValue, 990000);

        final var roundedStack = CurrencyHelper.getClosest(adjustedValue);
        if (originalValue != CurrencyHelper.getValue(Collections.singletonList(roundedStack)) && !roundedStack.is(this.baseCostA.getItem())) {
            CurrencyItem.setOriginalValue(roundedStack, originalValue);
        }
        cir.setReturnValue(roundedStack);
    }

}
