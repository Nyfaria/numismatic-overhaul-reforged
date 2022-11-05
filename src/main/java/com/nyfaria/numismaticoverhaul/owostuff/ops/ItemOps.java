package com.nyfaria.numismaticoverhaul.owostuff.ops;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * A collection of common checks and operations done on {@link ItemStack}
 */
public class ItemOps {

    private ItemOps() {}

    /**
     * Checks if stack one can stack onto stack two
     *
     * @param base     The base stack
     * @param addition The stack to be added
     * @return {@code true} if addition can stack onto base
     */
    public static boolean canStack(ItemStack base, ItemStack addition) {
        return base.isEmpty() || (canIncreaseBy(base, addition.getCount()) && ItemStack.isSameIgnoreDurability(base, addition) && ItemStack.tagMatches(base, addition));
    }

    /**
     * Checks if a stack can increase
     *
     * @param stack The stack to test
     * @return stack.getCount() &lt; stack.getMaxCount()
     */
    public static boolean canIncrease(ItemStack stack) {
        return stack.isStackable() && stack.getCount() < stack.getMaxStackSize();
    }

    /**
     * Checks if a stack can increase by the given amount
     *
     * @param stack The stack to test
     * @param by    The amount to test for
     * @return {@code true} if the stack can increase by the given amount
     */
    public static boolean canIncreaseBy(ItemStack stack, int by) {
        return stack.isStackable() && stack.getCount() + by <= stack.getMaxStackSize();
    }

    /**
     * Returns a copy of the given stack with count set to 1
     */
    public static ItemStack singleCopy(ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.setCount(1);
        return copy;
    }

    /**
     * Decrements the stack
     *
     * @param stack The stack to decrement
     * @return {@code false} if the stack is empty after the operation
     */
    public static boolean emptyAwareDecrement(ItemStack stack) {
        stack.shrink(1);
        return !stack.isEmpty();
    }

    /**
     * Decrements the stack in the players hand and replaces it with {@link ItemStack#EMPTY}
     * if the result would be an empty stack
     *
     * @param player The player to operate on
     * @param hand   The hand to affect
     * @return {@code false} if the stack is empty after the operation
     */
    public static boolean decrementPlayerHandItem(Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (!player.isCreative()) {
            if (!emptyAwareDecrement(stack)) player.setItemInHand(hand, ItemStack.EMPTY);
        }
        return !stack.isEmpty();
    }

    /**
     * Stores the given ItemStack with the specified key
     * into the given nbt compound
     *
     * @param stack The stack to store
     * @param nbt   The nbt compound to write to
     * @param key   The key to prefix the stack with
     */
    public static void store(ItemStack stack, CompoundTag nbt, String key) {
        if (stack.isEmpty()) return;

        var stackNbt = new CompoundTag();
        stack.save(stackNbt);
        nbt.put(key, stackNbt);
    }

    /**
     * Loads the ItemStack stored at the specified key
     * in the given nbt compound
     *
     * @param nbt The nbt compound to read from
     * @param key The key to load from
     * @return The deserialized stack
     */
    public static ItemStack get(CompoundTag nbt, String key) {
        if (!nbt.contains(key, Tag.TAG_COMPOUND)) return ItemStack.EMPTY;

        var stackNbt = nbt.getCompound(key);
        return ItemStack.of(stackNbt);
    }

}
