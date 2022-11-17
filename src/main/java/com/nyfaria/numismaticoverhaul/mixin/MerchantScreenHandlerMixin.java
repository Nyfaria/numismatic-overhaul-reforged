package com.nyfaria.numismaticoverhaul.mixin;

import com.nyfaria.numismaticoverhaul.cap.CurrencyHolder;
import com.nyfaria.numismaticoverhaul.cap.CurrencyHolderAttacher;
import com.nyfaria.numismaticoverhaul.currency.CurrencyHelper;
import com.nyfaria.numismaticoverhaul.init.ItemInit;
import com.nyfaria.numismaticoverhaul.item.CoinItem;
import com.nyfaria.numismaticoverhaul.item.MoneyBagItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantMenu.class)
public class MerchantScreenHandlerMixin {

    @Shadow
    @Final
    private Merchant trader;

    //Autofill with coins from the player's purse if the trade requires it
    //Injected at TAIL to let normal autofill run and fill up if anything is missing
    @Inject(method = "moveFromInventoryToPaymentSlot", at = @At("TAIL"))
    public void autofillOverride(int slot, ItemStack stack, CallbackInfo ci) {
        MerchantMenu handler = (MerchantMenu) (Object) this;
        CurrencyHolder playerBalance = CurrencyHolderAttacher.getExampleHolderUnwrap(((Inventory) handler.getSlot(3).container).player);

        if (stack.getItem() instanceof CoinItem) {
            numismatic$autofillWithCoins(slot, stack, handler, playerBalance);
        } else if (stack.getItem() == ItemInit.MONEY_BAG.get()) {
            autofillWithMoneyBag(slot, stack, handler, playerBalance);
        }

        if (slot == 1) playerBalance.commitTransactions();
    }
    @Redirect(method = "moveFromInventoryToPaymentSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameTags(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    public boolean isSameItemSameTags(ItemStack stack1, ItemStack stack2) {
        if (stack1.getItem() instanceof CoinItem) {
            return stack1.getItem() == stack2.getItem();
        }
        return ItemStack.isSameItemSameTags(stack1, stack2);
    }
    private static void numismatic$autofillWithCoins(int slot, ItemStack stack, MerchantMenu handler, CurrencyHolder playerBalance) {
        //See how much is required and how much was already autofilled
        long requiredCurrency = ((CoinItem) stack.getItem()).currency.getRawValue(stack.getCount());
        long presentCurrency = ((CoinItem) stack.getItem()).currency.getRawValue(handler.getSlot(slot).getItem().getCount());

        if (requiredCurrency <= presentCurrency) return;

        //Find out how much we still need to fill
        long neededCurrency = requiredCurrency - presentCurrency;

        //Is that even possible?
        if (!(neededCurrency <= playerBalance.getValue())) return;

        playerBalance.pushTransaction(-neededCurrency);

        handler.slots.get(slot).set(stack.copy());
    }

    private static void autofillWithMoneyBag(int slot, ItemStack stack, MerchantMenu handler, CurrencyHolder playerBalance) {
        if (ItemStack.isSameItemSameTags(stack, handler.getSlot(slot).getItem())) return;
        Player player = ((Inventory) handler.getSlot(3).container).player;

        //See how much is required and how much in present in the player's inventory
        long requiredCurrency = ((MoneyBagItem)ItemInit.MONEY_BAG.get()).getValue(stack);
        long availableCurrencyInPlayerInventory = CurrencyHelper.getMoneyInInventory(player, false);

        //Find out how much we still need to fill
        long neededCurrency = requiredCurrency - availableCurrencyInPlayerInventory;

        //Is that even possible?
        if (neededCurrency > playerBalance.getValue()) return;

        if (neededCurrency <= 0) {
            CurrencyHelper.deduceFromInventory(player, requiredCurrency);
        } else {
            CurrencyHelper.deduceFromInventory(player, availableCurrencyInPlayerInventory);
            playerBalance.pushTransaction(-neededCurrency);
        }

        handler.slots.get(slot).set(stack.copy());
    }

    @Inject(method = "playTradeSound", at = @At("HEAD"), cancellable = true)
    public void checkForEntityOnYes(CallbackInfo ci) {
        if (!(trader instanceof Entity)) ci.cancel();
    }

}
