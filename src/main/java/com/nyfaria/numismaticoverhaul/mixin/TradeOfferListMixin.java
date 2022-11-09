package com.nyfaria.numismaticoverhaul.mixin;

import com.nyfaria.numismaticoverhaul.villagers.data.NumismaticTradeOfferExtensions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MerchantOffers.class)
public class TradeOfferListMixin {

    @Inject(method = "writeToStream", at = @At(value = "INVOKE", ordinal = 4, target = "Lnet/minecraft/network/FriendlyByteBuf;writeInt(I)Lio/netty/buffer/ByteBuf;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void writeReputation(FriendlyByteBuf pBuffer, CallbackInfo ci, int i, MerchantOffer offer) {
        pBuffer.writeVarInt(((NumismaticTradeOfferExtensions) offer).numismatic$getReputation());
    }

    @Inject(method = "createFromStream", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/trading/MerchantOffer;setSpecialPriceDiff(I)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void readReputation(FriendlyByteBuf buf, CallbackInfoReturnable<MerchantOffer> cir, MerchantOffers offers, int ik, int jk,ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3, boolean bl, int i, int j, int k, int l, float f, int m, MerchantOffer tradeOffer) {
        ((NumismaticTradeOfferExtensions) tradeOffer).numismatic$setReputation(buf.readVarInt());
    }

}
