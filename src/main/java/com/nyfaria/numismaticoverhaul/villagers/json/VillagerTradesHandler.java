package com.nyfaria.numismaticoverhaul.villagers.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nyfaria.numismaticoverhaul.NumismaticOverhaul;
import com.nyfaria.numismaticoverhaul.villagers.data.NumismaticVillagerTradesRegistry;
import com.nyfaria.numismaticoverhaul.villagers.exceptions.DeserializationContext;
import com.nyfaria.numismaticoverhaul.villagers.exceptions.DeserializationException;
import com.nyfaria.numismaticoverhaul.villagers.json.adapters.BuyStackAdapter;
import com.nyfaria.numismaticoverhaul.villagers.json.adapters.DimensionAwareSellStackAdapter;
import com.nyfaria.numismaticoverhaul.villagers.json.adapters.EnchantItemAdapter;
import com.nyfaria.numismaticoverhaul.villagers.json.adapters.ProcessItemAdapter;
import com.nyfaria.numismaticoverhaul.villagers.json.adapters.SellDyedArmorAdapter;
import com.nyfaria.numismaticoverhaul.villagers.json.adapters.SellMapAdapter;
import com.nyfaria.numismaticoverhaul.villagers.json.adapters.SellPotionContainerItemAdapter;
import com.nyfaria.numismaticoverhaul.villagers.json.adapters.SellSingleEnchantmentAdapter;
import com.nyfaria.numismaticoverhaul.villagers.json.adapters.SellStackAdapter;
import com.nyfaria.numismaticoverhaul.villagers.json.adapters.SellSusStewAdapter;
import com.nyfaria.numismaticoverhaul.villagers.json.adapters.SellTagAdapter;
import com.nyfaria.numismaticoverhaul.villagers.json.adapters.StackTradeAdapter;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class VillagerTradesHandler {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final Map<String, Integer> professionKeys = new HashMap<>();
    public static final Map<ResourceLocation, TradeJsonAdapter> tradeTypesRegistry = new HashMap<>();

    private static final List<DeserializationException> EXCEPTIONS_DURING_LOADING = new ArrayList<>();

    static {
        professionKeys.put("novice", 1);
        professionKeys.put("apprentice", 2);
        professionKeys.put("journeyman", 3);
        professionKeys.put("expert", 4);
        professionKeys.put("master", 5);
    }

    public static void registerDefaultAdapters() {
        tradeTypesRegistry.put(NumismaticOverhaul.id("sell_stack"), new SellStackAdapter());
        tradeTypesRegistry.put(NumismaticOverhaul.id("sell_sus_stew"), new SellSusStewAdapter());
        tradeTypesRegistry.put(NumismaticOverhaul.id("sell_tag"), new SellTagAdapter());
        tradeTypesRegistry.put(NumismaticOverhaul.id("dimension_sell_stack"), new DimensionAwareSellStackAdapter());
        tradeTypesRegistry.put(NumismaticOverhaul.id("sell_map"), new SellMapAdapter());
        tradeTypesRegistry.put(NumismaticOverhaul.id("sell_single_enchantment"), new SellSingleEnchantmentAdapter());
        tradeTypesRegistry.put(NumismaticOverhaul.id("enchant_item"), new EnchantItemAdapter());
        tradeTypesRegistry.put(NumismaticOverhaul.id("process_item"), new ProcessItemAdapter());
        tradeTypesRegistry.put(NumismaticOverhaul.id("sell_dyed_armor"), new SellDyedArmorAdapter());
        tradeTypesRegistry.put(NumismaticOverhaul.id("sell_potion_container"), new SellPotionContainerItemAdapter());
        tradeTypesRegistry.put(NumismaticOverhaul.id("buy_item"), new BuyStackAdapter());
        tradeTypesRegistry.put(NumismaticOverhaul.id("buy_stack"), new BuyStackAdapter());
        tradeTypesRegistry.put(NumismaticOverhaul.id("stack_for_stack"), new StackTradeAdapter());
    }

    public static void loadProfession(ResourceLocation fileId, JsonObject jsonRoot) {

        //Clear context
        DeserializationContext.clear();

        String fileName = "§a" + fileId.getNamespace() + "§f:§6" + fileId.getPath();
        ResourceLocation professionId = ResourceLocation.tryParse(jsonRoot.get("profession").getAsString());

        //Push path to context
        DeserializationContext.setFile(fileName);

        //Push profession to context
        DeserializationContext.setProfession(professionId.getPath());

        try {
            if (professionId.getPath().equals("wandering_trader")) {
                deserializeTrades(jsonRoot, NumismaticVillagerTradesRegistry::registerWanderingTraderTrade);
            } else {
                VillagerProfession profession = Registry.VILLAGER_PROFESSION.getOptional(professionId).orElseThrow(() -> new DeserializationException("Invalid profession"));
                deserializeTrades(jsonRoot, (integer, factory) -> NumismaticVillagerTradesRegistry.registerVillagerTrade(profession, integer, factory));
            }
        } catch (DeserializationException e) {
            addLoadingException(e);
        }

    }

    private static void deserializeTrades(@NotNull JsonObject jsonRoot, BiConsumer<Integer, VillagerTrades.ItemListing> tradeConsumer) {

        if (!jsonRoot.get("trades").isJsonObject())
            throw new DeserializationException(jsonRoot.get("trades") + " is not a JsonObject");

        //Iterate villager levels
        for (Map.Entry<String, JsonElement> entry : jsonRoot.get("trades").getAsJsonObject().entrySet()) {

            int level = professionKeys.get(entry.getKey());

            if (!entry.getValue().isJsonArray())
                throw new DeserializationException(entry.getValue() + " is not a JsonArray");
            JsonArray tradesArray = entry.getValue().getAsJsonArray();

            //Iterate trades in that level
            for (JsonElement tradeElement : tradesArray) {

                if (!tradeElement.isJsonObject()) {
                    addLoadingException(new DeserializationException(tradeElement + " is not a JsonObject"));
                    continue;
                }

                JsonObject trade = tradeElement.getAsJsonObject();

                //Push trade to context
                DeserializationContext.setTrade(trade);
                DeserializationContext.setLevel(level);

                if (!trade.has("type")) {
                    throw new DeserializationException("Type missing");
                }

                TradeJsonAdapter adapter = tradeTypesRegistry.get(ResourceLocation.tryParse(trade.get("type").getAsString()));

                if (adapter == null) {
                    throw new DeserializationException("Unknown trade type " + trade.get("type").getAsString());
                }

                //Register trade
                try {
                    tradeConsumer.accept(level, adapter.deserialize(trade));
                } catch (DeserializationException e) {
                    addLoadingException(e);
                }
            }
        }

    }

    public static void addLoadingException(DeserializationException e) {
        EXCEPTIONS_DURING_LOADING.add(e);

        NumismaticOverhaul.LOGGER.error("");
        NumismaticOverhaul.LOGGER.error(" -- Caught exception during loading trade definitions, full stacktrace commencing -- ");
        NumismaticOverhaul.LOGGER.error("");

        e.printStackTrace();
    }

    public static void broadcastErrors(MinecraftServer server) {
        broadcastErrors(server.getPlayerList().getPlayers());
    }

    public static void broadcastErrors(List<ServerPlayer> players) {
        if (!EXCEPTIONS_DURING_LOADING.isEmpty()) {
            players.forEach(playerEntity -> {
                playerEntity.displayClientMessage(new TextComponent("§cThe following errors have occurred during numismaticoverhaul reload:"), false);
                playerEntity.displayClientMessage(new TextComponent(""), false);
                EXCEPTIONS_DURING_LOADING.forEach(e -> {

                    MutableComponent message = new TextComponent("§7-> " + e.getMessage() + " §8(hover for more info)");

                    MutableComponent hoverText = new TextComponent("");
                    hoverText.append(new TextComponent("File: §7" + e.getContext().file + "\n\n"));
                    hoverText.append(new TextComponent("Profession: §a" + e.getContext().profession + "\n"));
                    hoverText.append(new TextComponent("Level: §6" + e.getContext().level + "\n\n"));

                    hoverText.append(new TextComponent("Problematic trade: \n§7" + GSON.toJson(e.getContext().trade)));

                    message.setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)));

                    playerEntity.displayClientMessage(message, false);

                });
            });
            EXCEPTIONS_DURING_LOADING.clear();
        }
    }
}
