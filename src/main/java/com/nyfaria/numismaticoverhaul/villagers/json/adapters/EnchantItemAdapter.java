package com.nyfaria.numismaticoverhaul.villagers.json.adapters;

import com.google.gson.JsonObject;
import com.nyfaria.numismaticoverhaul.currency.CurrencyHelper;
import com.nyfaria.numismaticoverhaul.villagers.json.TradeJsonAdapter;
import com.nyfaria.numismaticoverhaul.villagers.json.VillagerJsonHelper;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class EnchantItemAdapter extends TradeJsonAdapter {

    @Override
    @NotNull
    public VillagerTrades.ItemListing deserialize(JsonObject json) {

        loadDefaultStats(json, false);
        VillagerJsonHelper.assertInt(json, "level");

        boolean allow_treasure = VillagerJsonHelper.boolean_getOrDefault(json, "allow_treasure", false);

        int level = json.get("level").getAsInt();
        ItemStack item = VillagerJsonHelper.ItemStack_getOrDefault(json, "item", new ItemStack(Items.BOOK));
        int base_price = GsonHelper.getAsInt(json, "base_price", 200);

        return new Factory(item, max_uses, villager_experience, level, allow_treasure, price_multiplier, base_price);
    }

    private static class Factory implements VillagerTrades.ItemListing {
        private final int experience;
        private final int maxUses;
        private final int level;
        private final boolean allowTreasure;
        private final ItemStack toEnchant;
        private final float multiplier;
        private final int basePrice;

        public Factory(ItemStack item, int maxUses, int experience, int level, boolean allowTreasure, float multiplier, int basePrice) {
            this.experience = experience;
            this.maxUses = maxUses;
            this.level = level;
            this.allowTreasure = allowTreasure;
            this.toEnchant = item;
            this.multiplier = multiplier;
            this.basePrice = basePrice;
        }

        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            ItemStack itemStack = toEnchant.copy();
            itemStack = EnchantmentHelper.enchantItem(random, itemStack, level, allowTreasure);

            int price = basePrice;
            for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(itemStack).entrySet()) {
                price += price * 0.10f + basePrice * (entry.getKey().isTreasureOnly() ? 2f : 1f) *
                        entry.getValue() * Mth.nextFloat(random, .8f, 1.2f)
                        * (5f / (float) entry.getKey().getRarity().getWeight());
            }

            return new MerchantOffer(CurrencyHelper.getClosest(price), toEnchant, itemStack, maxUses, this.experience, multiplier);
        }
    }
}
