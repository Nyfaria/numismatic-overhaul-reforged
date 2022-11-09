package com.nyfaria.numismaticoverhaul.villagers.json.adapters;

import com.google.gson.JsonObject;
import com.nyfaria.numismaticoverhaul.currency.CurrencyHelper;
import com.nyfaria.numismaticoverhaul.villagers.json.TradeJsonAdapter;
import com.nyfaria.numismaticoverhaul.villagers.json.VillagerJsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class DimensionAwareSellStackAdapter extends TradeJsonAdapter {

    @Override
    @NotNull
    public VillagerTrades.ItemListing deserialize(JsonObject json) {

        loadDefaultStats(json, true);

        VillagerJsonHelper.assertJsonObject(json, "sell");
        VillagerJsonHelper.assertString(json, "dimension");

        ItemStack sell = VillagerJsonHelper.getItemStackFromJson(json.get("sell").getAsJsonObject());
        String dimension = json.get("dimension").getAsString();

        int price = json.get("price").getAsInt();

        return new Factory(sell, price, dimension, max_uses, villager_experience, price_multiplier);
    }

    private static class Factory implements VillagerTrades.ItemListing {
        private final ItemStack sell;
        private final int maxUses;
        private final int experience;
        private final int price;
        private final float multiplier;
        private final String targetDimensionId;

        public Factory(ItemStack sell, int price, String targetDimensionId, int maxUses, int experience, float multiplier) {
            this.sell = sell;
            this.maxUses = maxUses;
            this.experience = experience;
            this.price = price;
            this.multiplier = multiplier;
            this.targetDimensionId = targetDimensionId;
        }

        public MerchantOffer getOffer(Entity entity, Random random) {
            if (!entity.level.dimension().location().toString().equals(targetDimensionId)) return null;

            return new MerchantOffer(CurrencyHelper.getClosest(price), sell, this.maxUses, this.experience, multiplier);
        }
    }
}
