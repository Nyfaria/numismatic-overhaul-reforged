package com.nyfaria.numismaticoverhaul.owostuff.client.screens;

import com.nyfaria.numismaticoverhaul.mixin.owomixins.ScreenHandlerInvoker;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

/**
 * A collection of utilities to ease implementing a simple {@link net.minecraft.client.gui.screens.inventory.AbstractContainerScreen}
 */
public class ScreenUtils {

    /**
     * Generate a grid of slots with indices ascending
     * left-to-right, top-to-bottom
     *
     * @param anchorX      The {@code x} coordinate of the top-, leftmost slot
     * @param anchorY      The {@code y} coordinate of the top-, leftmost slot
     * @param inventory    The inventory to associate the slots with
     * @param slotConsumer Some method that accepts the generated slots
     */
    @Deprecated(forRemoval = true)
    public static void generateSlotGrid(int anchorX, int anchorY, int width, int height, int startIndex, Container inventory, Consumer<Slot> slotConsumer) {
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                slotConsumer.accept(new Slot(inventory, startIndex + row * width + column, anchorX + column * 18, anchorY + row * 18));
            }
        }
    }

    /**
     * Generate the player inventory and hotbar
     * slots seen in most normal inventory screens
     *
     * @param anchorX         The {@code x} coordinate of the top-, leftmost slot
     * @param anchorY         The {@code y} coordinate of the top-, leftmost slot
     * @param playerInventory The inventory to associate the slots with
     * @param slotConsumer    Some method that accepts the generated slots
     */
    @Deprecated(forRemoval = true)
    public static void generatePlayerSlots(int anchorX, int anchorY, Inventory playerInventory, Consumer<Slot> slotConsumer) {
        generateSlotGrid(anchorX, anchorY, 9, 3, 9, playerInventory, slotConsumer);
        generateSlotGrid(anchorX, anchorY + 58, 9, 1, 0, playerInventory, slotConsumer);
    }

    /**
     * Can be used as an implementation of {@link AbstractContainerMenu#quickMoveStack(Player, int)}
     * for simple screens with a lower (player) and upper (main) inventory
     *
     * <pre>
     * {@code
     * @Override
     * public ItemStack transferSlot(PlayerEntity player, int invSlot) {
     *     return ScreenUtils.handleSlotTransfer(this.slots, invSlot, this.inventory.size());
     * }
     * }
     * </pre>
     *
     * @param handler            The target ScreenHandler
     * @param clickedSlotIndex   The slot index that was clicked
     * @param upperInventorySize The size of the upper (main) inventory
     * @return The return value for {{@link AbstractContainerMenu#quickMoveStack(Player, int)}}
     */
    public static ItemStack handleSlotTransfer(AbstractContainerMenu handler, int clickedSlotIndex, int upperInventorySize) {
        final var slots = handler.slots;
        final var clickedSlot = slots.get(clickedSlotIndex);
        if (!clickedSlot.hasItem()) return ItemStack.EMPTY;

        final var clickedStack = clickedSlot.getItem();

        if (clickedSlotIndex < upperInventorySize) {
            if (!insertIntoSlotRange(handler, clickedStack, upperInventorySize, slots.size())) return ItemStack.EMPTY;
        } else {
            if (!insertIntoSlotRange(handler, clickedStack, 0, upperInventorySize)) return ItemStack.EMPTY;
        }

        if (clickedStack.isEmpty()) {
            clickedSlot.set(ItemStack.EMPTY);
        } else {
            clickedSlot.setChanged();
        }

        return clickedStack;
    }

    /**
     * Tries to insert the {@code addition} stack into all slots in the given range
     *
     * @param handler    The ScreenHandler to operate on
     * @param beginIndex The index of the first slot to check
     * @param endIndex   The index of the last slot to check
     * @param addition   The ItemStack to try and insert, this gets mutated
     *                   if insertion (partly) succeeds
     * @return {@code true} if state was modified
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean insertIntoSlotRange(AbstractContainerMenu handler, ItemStack addition, int beginIndex, int endIndex) {
        return ((ScreenHandlerInvoker) handler).owo$insertItem(addition, beginIndex, endIndex, false);
    }

}
