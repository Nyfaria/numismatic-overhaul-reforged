package com.nyfaria.numismaticoverhaul.cap;

import com.nyfaria.numismaticoverhaul.currency.CurrencyConverter;
import com.nyfaria.numismaticoverhaul.item.CoinItem;
import com.nyfaria.numismaticoverhaul.network.NetworkHandler;
import dev._100media.capabilitysyncer.core.PlayerCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.ArrayList;
import java.util.List;

public class CurrencyHolder extends PlayerCapability {

    private long value;
    private final List<Long> transactions;

    protected CurrencyHolder(Player player) {
        super(player);
        this.transactions = new ArrayList<Long>();
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
        updateTracking();
    }
    public void silentModify(long value) {
        setValue(this.value + value);
    }
    public Long popTransaction() {
        return this.transactions.remove(this.transactions.size() - 1);
    }
    public void pushTransaction(long value) {
        this.transactions.add(value);
    }
    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putLong("value", value);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        value = nbt.getLong("value");
    }

    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(entity.getId(), CurrencyHolderAttacher.EXAMPLE_RL, this);
    }
    public void modify(long value) {
        setValue(this.value + value);

        long tempValue = value < 0 ? -value : value;

        List<ItemStack> transactionStacks = CurrencyConverter.getAsItemStackList(tempValue);
        if (transactionStacks.isEmpty()) return;

        MutableComponent message = value < 0 ? net.minecraft.network.chat.Component.literal("§c- ") : net.minecraft.network.chat.Component.literal("§a+ ");
        message.append(net.minecraft.network.chat.Component.literal("§7["));
        for (ItemStack stack : transactionStacks) {
            message.append(net.minecraft.network.chat.Component.literal("§b" + stack.getCount() + " "));
            message.append(net.minecraft.network.chat.Component.translatable("currency.numismaticoverhaul." + ((CoinItem) stack.getItem()).currency.name().toLowerCase()));
            if (transactionStacks.indexOf(stack) != transactionStacks.size() - 1) message.append(net.minecraft.network.chat.Component.literal(", "));
        }
        message.append(net.minecraft.network.chat.Component.literal("§7]"));

        player.displayClientMessage(message, true);
    }
    @Override
    public SimpleChannel getNetworkChannel() {
        return NetworkHandler.INSTANCE;
    }
    public void commitTransactions() {
        this.modify(this.transactions.stream().mapToLong(Long::longValue).sum());
        this.transactions.clear();
    }
}
