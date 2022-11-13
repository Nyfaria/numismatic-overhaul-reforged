package com.nyfaria.numismaticoverhaul.villagers.json.adapters;

import com.google.gson.JsonObject;
import com.nyfaria.numismaticoverhaul.currency.CurrencyHelper;
import com.nyfaria.numismaticoverhaul.villagers.json.TradeJsonAdapter;
import com.nyfaria.numismaticoverhaul.villagers.json.VillagerJsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProcessItemAdapter extends TradeJsonAdapter {

    @Override
    @NotNull
    public VillagerTrades.ItemListing deserialize(JsonObject json) {

        loadDefaultStats(json, true);

        VillagerJsonHelper.assertJsonObject(json, "buy");
        VillagerJsonHelper.assertJsonObject(json, "sell");

        ItemStack sell = VillagerJsonHelper.getItemStackFromJson(json.get("sell").getAsJsonObject());
        ItemStack buy = VillagerJsonHelper.getItemStackFromJson(json.get("buy").getAsJsonObject());

        int price = json.get("price").getAsInt();

        return new Factory(buy, sell, price, max_uses, villager_experience, price_multiplier);
    }

    private static class Factory implements VillagerTrades.ItemListing, NumOTrade {
        private final ItemStack buy;
        private final int price;
        private final ItemStack sell;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public Factory(ItemStack buy, ItemStack sell, int price, int maxUses, int experience, float multiplier) {
            this.buy = buy;
            this.price = price;
            this.sell = sell;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        @Nullable
        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            return new MerchantOffer(CurrencyHelper.getClosest(price), buy, sell, this.maxUses, this.experience, this.multiplier);
        }
    }
}
