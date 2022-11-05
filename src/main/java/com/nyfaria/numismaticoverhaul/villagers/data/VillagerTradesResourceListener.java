package com.nyfaria.numismaticoverhaul.villagers.data;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nyfaria.numismaticoverhaul.villagers.json.VillagerTradesHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.Tuple;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.util.HashMap;
import java.util.Map;

public class VillagerTradesResourceListener extends SimpleJsonResourceReloadListener  {

    public VillagerTradesResourceListener() {
        //Fortnite
        super(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create(), "villager_trades");
    }


    @Override
    protected void apply(Map<ResourceLocation, JsonElement> loader, ResourceManager manager, ProfilerFiller profiler) {
//        if (!NumismaticOverhaul.CONFIG.enableVillagerTrading()) return;
//todo: config
        NumismaticVillagerTradesRegistry.clearRegistries();

        loader.forEach((identifier, jsonElement) -> {
            if (!jsonElement.isJsonObject()) return;
            JsonObject root = jsonElement.getAsJsonObject();
            VillagerTradesHandler.loadProfession(identifier, root);
        });

        NumismaticVillagerTradesRegistry.wrapModVillagers();

        final Tuple<HashMap<VillagerProfession, Int2ObjectOpenHashMap<VillagerTrades.ItemListing[]>>, Int2ObjectOpenHashMap<VillagerTrades.ItemListing[]>> registry = NumismaticVillagerTradesRegistry.getRegistryForLoading();
        VillagerTrades.TRADES.putAll(registry.getA());

        if (!registry.getB().isEmpty()) {
            VillagerTrades.WANDERING_TRADER_TRADES.clear();
            VillagerTrades.WANDERING_TRADER_TRADES.putAll(registry.getB());
        }

    }
}
