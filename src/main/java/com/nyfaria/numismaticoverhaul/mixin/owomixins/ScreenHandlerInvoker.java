package com.nyfaria.numismaticoverhaul.mixin.owomixins;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerMenu.class)
public interface ScreenHandlerInvoker {

    @Invoker("moveItemStackTo")
    boolean owo$insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast);

}
