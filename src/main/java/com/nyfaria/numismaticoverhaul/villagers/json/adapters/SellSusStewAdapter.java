package com.nyfaria.numismaticoverhaul.villagers.json.adapters;

import com.google.gson.JsonObject;
import com.nyfaria.numismaticoverhaul.currency.CurrencyHelper;
import com.nyfaria.numismaticoverhaul.villagers.exceptions.DeserializationException;
import com.nyfaria.numismaticoverhaul.villagers.json.TradeJsonAdapter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class SellSusStewAdapter extends TradeJsonAdapter {

    @Override
    public @NotNull VillagerTrades.ItemListing deserialize(JsonObject json) {
        this.loadDefaultStats(json, true);

        final int price = json.get("price").getAsInt();
        final int duration = GsonHelper.getAsInt(json, "duration", 100);

        final var effectId = new ResourceLocation(GsonHelper.getAsString(json, "effect_id"));
        final var effect = Registry.MOB_EFFECT.getOptional(effectId)
                .orElseThrow(() -> new DeserializationException("Unknown status effect '" + effectId + "'"));

        return new Factory(effect, price, duration, villager_experience, price_multiplier, max_uses);
    }

    static class Factory implements VillagerTrades.ItemListing {
        private final MobEffect effect;
        private final int price;
        private final int duration;
        private final int experience;
        private final int maxUses;
        private final float multiplier;

        public Factory(MobEffect effect, int price, int duration, int experience, float multiplier, int maxUses) {
            this.effect = effect;
            this.price = price;
            this.duration = duration;
            this.experience = experience;
            this.multiplier = multiplier;
            this.maxUses = maxUses;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            ItemStack susStew = new ItemStack(Items.SUSPICIOUS_STEW, 1);
            SuspiciousStewItem.saveMobEffect(susStew, this.effect, this.duration);

            return new MerchantOffer(CurrencyHelper.getClosest(price), susStew, this.maxUses, this.experience, this.multiplier);
        }
    }
}
