package com.nyfaria.numismaticoverhaul.villagers.json.adapters;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.nyfaria.numismaticoverhaul.currency.CurrencyHelper;
import com.nyfaria.numismaticoverhaul.villagers.json.TradeJsonAdapter;
import com.nyfaria.numismaticoverhaul.villagers.json.VillagerJsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SellDyedArmorAdapter extends TradeJsonAdapter {

    @Override
    public @NotNull VillagerTrades.ItemListing deserialize(JsonObject json) {

        loadDefaultStats(json, true);

        VillagerJsonHelper.assertString(json, "item");

        int price = json.get("price").getAsInt();
        Item item = VillagerJsonHelper.getItemFromID(json.get("item").getAsString());

        return new Factory(item, price, max_uses, villager_experience, price_multiplier);
    }

    private static class Factory implements VillagerTrades.ItemListing {
        private final Item sell;
        private final int price;
        private final int maxUses;
        private final int experience;
        private final float priceMultiplier;

        public Factory(Item item, int price, int maxUses, int experience, float priceMultiplier) {
            this.sell = item;
            this.price = price;
            this.maxUses = maxUses;
            this.experience = experience;
            this.priceMultiplier = priceMultiplier;
        }

        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            ItemStack itemStack2 = new ItemStack(this.sell);
            if (this.sell instanceof DyeableLeatherItem) {
                List<DyeItem> list = Lists.newArrayList();
                list.add(getDye(random));
                if (random.nextFloat() > 0.7F) {
                    list.add(getDye(random));
                }

                if (random.nextFloat() > 0.8F) {
                    list.add(getDye(random));
                }

                itemStack2 = DyeableLeatherItem.dyeArmor(itemStack2, list);
            }

            return new MerchantOffer(CurrencyHelper.getClosest(price), itemStack2, this.maxUses, this.experience, priceMultiplier);

        }

        private static DyeItem getDye(RandomSource random) {
            return DyeItem.byColor(DyeColor.byId(random.nextInt(16)));
        }
    }
}
