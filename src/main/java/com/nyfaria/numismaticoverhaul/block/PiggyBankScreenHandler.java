package com.nyfaria.numismaticoverhaul.block;

import com.nyfaria.numismaticoverhaul.init.BlockInit;
import com.nyfaria.numismaticoverhaul.init.ItemInit;
import com.nyfaria.numismaticoverhaul.init.MenuInit;
import com.nyfaria.numismaticoverhaul.owostuff.client.screens.ScreenUtils;
import com.nyfaria.numismaticoverhaul.owostuff.client.screens.SlotGenerator;
import com.nyfaria.numismaticoverhaul.owostuff.client.screens.ValidatingSlot;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;

public class PiggyBankScreenHandler extends AbstractContainerMenu {

    private final ContainerLevelAccess context;

    public PiggyBankScreenHandler(int index, Inventory playerInventory) {
        this(index, playerInventory, ContainerLevelAccess.NULL, new SimpleContainer(3));
    }

    public PiggyBankScreenHandler(int syncId, Inventory playerInventory, ContainerLevelAccess context, Container piggyBankInventory) {
        super(MenuInit.PIGGY_BANK.get(), syncId);
        this.context = context;

        this.addSlot(new ValidatingSlot(piggyBankInventory, 0, 62, 26, stack -> stack.is(ItemInit.BRONZE_COIN.get())));
        this.addSlot(new ValidatingSlot(piggyBankInventory, 1, 80, 26, stack -> stack.is(ItemInit.SILVER_COIN.get())));
        this.addSlot(new ValidatingSlot(piggyBankInventory, 2, 98, 26, stack -> stack.is(ItemInit.GOLD_COIN.get())));

        SlotGenerator.begin(this::addSlot, 8, 63).playerInventory(playerInventory);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ScreenUtils.handleSlotTransfer(this, index, 3);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(context, player, BlockInit.PIGGY_BANK.get());
    }
}
