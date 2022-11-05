package com.nyfaria.numismaticoverhaul.item;

import com.nyfaria.numismaticoverhaul.NumismaticOverhaul;
import com.nyfaria.numismaticoverhaul.cap.CurrencyHolderAttacher;
import com.nyfaria.numismaticoverhaul.currency.Currency;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class CoinItem extends Item implements CurrencyItem {

    public final Currency currency;
    public final Style NAME_STYLE;

    public CoinItem(Currency currency) {
        super(new Properties().tab(NumismaticOverhaul.NUMISMATIC_GROUP).stacksTo(99));
        this.currency = currency;
        this.NAME_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(currency.getNameColor()));
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack clickedStack, ItemStack otherStack, Slot slot, ClickAction clickType, Player player, SlotAccess cursorStackReference) {
        if (slot instanceof MerchantResultSlot) return false;
        if (clickType != ClickAction.PRIMARY) return false;

        if ((otherStack.getItem() == this && otherStack.getCount() + clickedStack.getCount() <= otherStack.getMaxStackSize()) || !(otherStack.getItem() instanceof CurrencyItem currencyItem))
            return false;

        long[] values = currencyItem.getCombinedValue(otherStack);
        values[this.currency.ordinal()] += clickedStack.getCount();

        final var stack = MoneyBagItem.createCombined(values);
        if (!slot.mayPlace(stack)) return false;

        slot.set(stack);

        cursorStackReference.set(ItemStack.EMPTY);
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {

        ItemStack clickedStack = user.getItemInHand(hand);
        long rawValue = ((CoinItem) clickedStack.getItem()).currency.getRawValue(clickedStack.getCount());

        if (!world.isClientSide) {
            CurrencyHolderAttacher.getExampleHolderUnwrap(user).modify(rawValue);
        }

        return InteractionResultHolder.success(ItemStack.EMPTY);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new CurrencyTooltipData(this.currency.getRawValue(stack.getCount()),
                CurrencyItem.hasOriginalValue(stack) ? CurrencyItem.getOriginalValue(stack) : -1));
    }

    @Override
    public Component getDescription() {
        return super.getDescription().copy().setStyle(NAME_STYLE);
    }

    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack).copy().setStyle(NAME_STYLE);
    }

    @Override
    public boolean wasAdjusted(ItemStack other) {
        return other.getItem() != this;
    }

    @Override
    public long getValue(ItemStack stack) {
        return this.currency.getRawValue(stack.getCount());
    }

    @Override
    public long[] getCombinedValue(ItemStack stack) {
        final long[] values = new long[3];
        values[this.currency.ordinal()] = stack.getCount();
        return values;
    }
}
