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

public class BuyStackAdapter extends TradeJsonAdapter {

    @Override
    @NotNull
    public VillagerTrades.ItemListing deserialize(JsonObject json) {

        loadDefaultStats(json, true);

        VillagerJsonHelper.assertJsonObject(json, "buy");

        int price = json.get("price").getAsInt();
        ItemStack buy = VillagerJsonHelper.getItemStackFromJson(json.get("buy").getAsJsonObject());

        return new Factory(buy, price, max_uses, villager_experience, price_multiplier);
    }

    private static class Factory implements VillagerTrades.ItemListing, NumOTrade {
        private final ItemStack buy;
        private final int price;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public Factory(ItemStack buy, int price, int maxUses, int experience, float multiplier) {
            this.buy = buy;
            this.price = price;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            return new MerchantOffer(buy, CurrencyHelper.getClosest(price), this.maxUses, this.experience, this.multiplier);
        }
    }
}
