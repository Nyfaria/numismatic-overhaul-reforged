package com.nyfaria.numismaticoverhaul.villagers.data;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

;

public class NumismaticVillagerTradesRegistry {

    private static final HashMap<VillagerProfession, Int2ObjectOpenHashMap<List<VillagerTrades.ItemListing>>> TRADES_REGISTRY = new HashMap<>();
    private static final Int2ObjectOpenHashMap<List<VillagerTrades.ItemListing>> WANDERING_TRADER_REGISTRY = new Int2ObjectOpenHashMap<>();

    private static final HashMap<VillagerProfession, Int2ObjectOpenHashMap<List<VillagerTrades.ItemListing>>> REMAPPED_FABRIC_TRADES = new HashMap<>();
    private static final Int2ObjectOpenHashMap<List<VillagerTrades.ItemListing>> REMAPPED_FABRIC_WANDERING_TRADER_TRADES = new Int2ObjectOpenHashMap<>();

    private static final AtomicBoolean MOD_VILLAGERS_WRAPPED = new AtomicBoolean(false);

    // -- Fabric API trades - these are stored persistently --

    public static void registerFabricVillagerTrades(VillagerProfession profession, int level, List<VillagerTrades.ItemListing> factories) {
        getVillagerTradeList(REMAPPED_FABRIC_TRADES, profession, level).addAll(factories.stream().map(RemappingTradeWrapper::wrap).toList());
    }

    public static void registerFabricWanderingTraderTrades(int level, List<VillagerTrades.ItemListing> factories) {
        getOrDefaultAndAdd(REMAPPED_FABRIC_WANDERING_TRADER_TRADES, level, new ArrayList<>()).addAll(factories.stream().map(RemappingTradeWrapper::wrap).toList());
    }

    // -- NO datapack trades - this registry is cleared on reload--

    public static void registerVillagerTrade(VillagerProfession profession, int level, VillagerTrades.ItemListing trade) {
        getVillagerTradeList(TRADES_REGISTRY, profession, level).add(trade);
    }

    public static void registerWanderingTraderTrade(int level, VillagerTrades.ItemListing trade) {
        getOrDefaultAndAdd(WANDERING_TRADER_REGISTRY, level, new ArrayList<>()).add(trade);
    }

    // -- Helper Methods --

    public static void wrapModVillagers() {
        if (MOD_VILLAGERS_WRAPPED.get()) return;

        VillagerTrades.TRADES.forEach((profession, int2TradesMap) -> {
            if (TRADES_REGISTRY.containsKey(profession)) return;
            int2TradesMap.forEach((integer, factories) -> {
                registerFabricVillagerTrades(profession, integer, Arrays.asList(factories));
            });
        });

        MOD_VILLAGERS_WRAPPED.set(true);
    }

    private static List<VillagerTrades.ItemListing> getVillagerTradeList(HashMap<VillagerProfession, Int2ObjectOpenHashMap<List<VillagerTrades.ItemListing>>> registry, VillagerProfession profession, int level) {
        Int2ObjectOpenHashMap<List<VillagerTrades.ItemListing>> villagerMap = getOrDefaultAndAdd(registry, profession, new Int2ObjectOpenHashMap<>());
        return getOrDefaultAndAdd(villagerMap, level, new ArrayList<>());
    }

    public static <K, V> V getOrDefaultAndAdd(Map<K, V> map, K key, V defaultValue) {
        if (map.containsKey(key)) return map.get(key);
        map.put(key, defaultValue);
        return defaultValue;
    }

    public static void clearRegistries() {
        TRADES_REGISTRY.clear();
        WANDERING_TRADER_REGISTRY.clear();
    }

    public static Tuple<HashMap<VillagerProfession, Int2ObjectOpenHashMap<VillagerTrades.ItemListing[]>>, Int2ObjectOpenHashMap<VillagerTrades.ItemListing[]>> getRegistryForLoading() {

        final var processor = RegistryProcessor.begin();

        TRADES_REGISTRY.forEach(processor::processProfession);
        REMAPPED_FABRIC_TRADES.forEach((villagerProfession, listInt2ObjectOpenHashMap) -> {
            if (TRADES_REGISTRY.containsKey(villagerProfession)) return;
            processor.processProfession(villagerProfession, listInt2ObjectOpenHashMap);
        });

        WANDERING_TRADER_REGISTRY.forEach(processor::processWanderingTrader);
        REMAPPED_FABRIC_WANDERING_TRADER_TRADES.forEach(processor::processWanderingTrader);

        return processor.finish();
    }

    private static class RegistryProcessor {

        private final HashMap<VillagerProfession, Int2ObjectOpenHashMap<VillagerTrades.ItemListing[]>> villagerTrades;
        private final Int2ObjectOpenHashMap<VillagerTrades.ItemListing[]> wanderingTraderTrades;

        private RegistryProcessor() {
            this.villagerTrades = new HashMap<>();
            this.wanderingTraderTrades = new Int2ObjectOpenHashMap<>();
        }

        public static RegistryProcessor begin() {
            return new RegistryProcessor();
        }

        public void processProfession(VillagerProfession profession, Int2ObjectOpenHashMap<List<VillagerTrades.ItemListing>> professionTradesPerLevel) {
            Int2ObjectOpenHashMap<VillagerTrades.ItemListing[]> factories = villagerTrades.getOrDefault(profession, new Int2ObjectOpenHashMap<>());

            professionTradesPerLevel.forEach((level, factoryList) -> {
                final var oldFactories = factories.getOrDefault(level.intValue(), new VillagerTrades.ItemListing[0]);
                factories.put(level.intValue(), ArrayUtils.addAll(oldFactories, factoryList.toArray(new VillagerTrades.ItemListing[0])));
            });

            villagerTrades.put(profession, factories);
        }

        public void processWanderingTrader(Integer level, List<VillagerTrades.ItemListing> trades) {
            final var oldFactories = wanderingTraderTrades.getOrDefault(level.intValue(), new VillagerTrades.ItemListing[0]);
            wanderingTraderTrades.put(level.intValue(), ArrayUtils.addAll(oldFactories, trades.toArray(new VillagerTrades.ItemListing[0])));
        }

        public Tuple<HashMap<VillagerProfession, Int2ObjectOpenHashMap<VillagerTrades.ItemListing[]>>, Int2ObjectOpenHashMap<VillagerTrades.ItemListing[]>> finish() {
            return new Tuple<>(villagerTrades, wanderingTraderTrades);
        }

    }

}
