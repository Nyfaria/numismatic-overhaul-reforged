package com.nyfaria.numismaticoverhaul.villagers.json;

import com.google.gson.JsonObject;
import com.nyfaria.numismaticoverhaul.villagers.exceptions.DeserializationException;
import net.minecraft.world.entity.npc.VillagerTrades;
import org.jetbrains.annotations.NotNull;

public abstract class TradeJsonAdapter {

    protected int max_uses;
    protected int villager_experience;
    protected float price_multiplier;

    @NotNull
    public abstract VillagerTrades.ItemListing deserialize(JsonObject json);

    protected void loadDefaultStats(JsonObject jsonObject, boolean verifyPrice) {

        this.max_uses = VillagerJsonHelper.int_getOrDefault(jsonObject, "max_uses", 12);
        this.villager_experience = VillagerJsonHelper.int_getOrDefault(jsonObject, "villager_experience", 5);
        this.price_multiplier = VillagerJsonHelper.float_getOrDefault(jsonObject, "price_multiplier", 0.05f);

        if (verifyPrice) {
            if (!jsonObject.has("price")) throw new DeserializationException("Missing price");

            VillagerJsonHelper.assertInt(jsonObject, "price");

            if (jsonObject.get("price").getAsInt() == 0) throw new DeserializationException("Price must not be zero");
        }

    }

}
