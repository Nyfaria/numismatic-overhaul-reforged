package com.nyfaria.numismaticoverhaul.villagers.json.adapters;

import com.google.gson.JsonObject;
import com.nyfaria.numismaticoverhaul.currency.CurrencyHelper;
import com.nyfaria.numismaticoverhaul.villagers.json.TradeJsonAdapter;
import com.nyfaria.numismaticoverhaul.villagers.json.VillagerJsonHelper;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class SellPotionContainerItemAdapter extends TradeJsonAdapter {

    @Override
    @NotNull
    public VillagerTrades.ItemListing deserialize(JsonObject json) {

        loadDefaultStats(json, true);

        VillagerJsonHelper.assertJsonObject(json, "container_item");
        VillagerJsonHelper.assertJsonObject(json, "buy_item");

        int price = json.get("price").getAsInt();
        ItemStack container_item = VillagerJsonHelper.getItemStackFromJson(json.get("container_item").getAsJsonObject());
        ItemStack buy_item = VillagerJsonHelper.getItemStackFromJson(json.get("buy_item").getAsJsonObject());

        return new Factory(container_item, buy_item, price, max_uses, villager_experience, price_multiplier);
    }

    private static class Factory implements VillagerTrades.ItemListing {
        private final ItemStack containerItem;
        private final ItemStack buyItem;

        private final int price;
        private final int maxUses;
        private final int experience;

        private final float priceMultiplier;

        public Factory(ItemStack containerItem, ItemStack buyItem, int price, int maxUses, int experience, float priceMultiplier) {
            this.containerItem = containerItem;
            this.buyItem = buyItem;
            this.price = price;
            this.maxUses = maxUses;
            this.experience = experience;
            this.priceMultiplier = priceMultiplier;
        }

        public MerchantOffer getOffer(Entity entity, Random random) {
            List<Potion> list = Registry.POTION.stream().filter((potion) -> !potion.getEffects().isEmpty() && PotionBrewing.isBrewablePotion(potion)).toList();

            Potion potion = list.get(random.nextInt(list.size()));
            ItemStack itemStack2 = PotionUtils.setPotion(containerItem.copy(), potion);
            return new MerchantOffer(CurrencyHelper.getClosest(price), buyItem, itemStack2, this.maxUses, this.experience, this.priceMultiplier);
        }
    }
}
