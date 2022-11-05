package com.nyfaria.numismaticoverhaul.owostuff.client.screens;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

/**
 * A slot that uses the provided {@code insertCondition}
 * to decide which items can be inserted
 */
public class ValidatingSlot extends Slot {

    private final Predicate<ItemStack> insertCondition;

    public ValidatingSlot(Container inventory, int index, int x, int y, Predicate<ItemStack> insertCondition) {
        super(inventory, index, x, y);
        this.insertCondition = insertCondition;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return insertCondition.test(stack);
    }

}
