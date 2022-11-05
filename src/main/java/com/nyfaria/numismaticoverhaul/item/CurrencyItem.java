package com.nyfaria.numismaticoverhaul.item;

import net.minecraft.world.item.ItemStack;

public interface CurrencyItem {


    static void setOriginalValue(ItemStack stack, long value) {
        stack.getOrCreateTag().putLong("OriginalValue", value);
    }

    static long getOriginalValue(ItemStack stack) {
        return stack.getOrCreateTag().getLong("OriginalValue");
    }

    static boolean hasOriginalValue(ItemStack stack) {
        return stack.getOrCreateTag().contains("OriginalValue");
    }

    boolean wasAdjusted(ItemStack other);

    long getValue(ItemStack stack);

    long[] getCombinedValue(ItemStack stack);

}
