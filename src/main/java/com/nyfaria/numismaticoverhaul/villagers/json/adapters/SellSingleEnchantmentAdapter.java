package com.nyfaria.numismaticoverhaul.villagers.json.adapters;

import com.google.gson.JsonObject;
import com.nyfaria.numismaticoverhaul.currency.CurrencyHelper;
import com.nyfaria.numismaticoverhaul.villagers.json.TradeJsonAdapter;
import net.minecraft.core.Registry;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class SellSingleEnchantmentAdapter extends TradeJsonAdapter {

    @Override
    @NotNull
    public VillagerTrades.ItemListing deserialize(JsonObject json) {
        loadDefaultStats(json, false);
        return new Factory(max_uses, villager_experience, price_multiplier);
    }

    private static class Factory implements VillagerTrades.ItemListing {
        private final int experience;
        private final int maxUses;
        private final float multiplier;

        public Factory(int maxUses, int experience, float multiplier) {
            this.experience = experience;
            this.maxUses = maxUses;
            this.multiplier = multiplier;
        }

        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            List<Enchantment> list = Registry.ENCHANTMENT.stream().filter(Enchantment::isTradeable).collect(Collectors.toList());
            Enchantment enchantment = list.get(random.nextInt(list.size()));

            int enchantmentLevel = Mth.nextInt(random, enchantment.getMinLevel(), enchantment.getMaxLevel());

            ItemStack itemStack = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, enchantmentLevel));
            int cost = 100 * (10 / enchantment.getRarity().getWeight()) + (random.nextInt(50) + enchantmentLevel) * enchantmentLevel * enchantmentLevel * (10 / enchantment.getRarity().getWeight());
            if (enchantment.isTreasureOnly()) {
                cost *= 2;
            }

            return new MerchantOffer(CurrencyHelper.getClosest(cost), new ItemStack(Items.BOOK), itemStack, maxUses, this.experience, multiplier);
        }
    }
}
