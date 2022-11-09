package com.nyfaria.numismaticoverhaul.villagers.data;

import com.nyfaria.numismaticoverhaul.currency.CurrencyHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class RemappingTradeWrapper implements VillagerTrades.ItemListing {

    private final VillagerTrades.ItemListing delegate;

    private RemappingTradeWrapper(VillagerTrades.ItemListing delegate) {
        this.delegate = delegate;
    }

    public static RemappingTradeWrapper wrap(VillagerTrades.ItemListing delegate) {
        return new RemappingTradeWrapper(delegate);
    }

    @Nullable
    @Override
    public MerchantOffer getOffer(Entity entity, Random random) {
        final var tempOffer = delegate.getOffer(entity, random);

        if (tempOffer == null) return null;

        final var firstBuyRemapped = remap(tempOffer.getBaseCostA());
        final var secondBuyRemapped = remap(tempOffer.getCostB());
        final var sellRemapped = remap(tempOffer.getResult());

        return new MerchantOffer(firstBuyRemapped, secondBuyRemapped, sellRemapped, tempOffer.getUses(), tempOffer.getMaxUses(), tempOffer.getXp(), tempOffer.getPriceMultiplier(), tempOffer.getDemand());
    }

    private static ItemStack remap(ItemStack stack) {
        if (stack.getItem() != Items.EMERALD) return stack;

        final int moneyWorth = stack.getCount() * 125;

        return CurrencyHelper.getClosest(moneyWorth);
    }
}
