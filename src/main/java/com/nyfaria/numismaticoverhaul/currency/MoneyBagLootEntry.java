package com.nyfaria.numismaticoverhaul.currency;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.nyfaria.numismaticoverhaul.NumismaticOverhaul;
import com.nyfaria.numismaticoverhaul.item.MoneyBagItem;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.function.Consumer;

public class MoneyBagLootEntry extends LootPoolSingletonContainer {

    private final int min;
    private final int max;

    private MoneyBagLootEntry(int min, int max, int weight, int quality, LootItemCondition[] conditions, LootItemFunction[] functions) {
        super(weight, quality, conditions, functions);
        this.min = min;
        this.max = max;
    }

    @Override
    protected void createItemStack(Consumer<ItemStack> lootConsumer, LootContext context) {
        int value = Mth.nextInt(context.getRandom(), min, max);
        if (value == 0) return;

        lootConsumer.accept(MoneyBagItem.createCombined(CurrencyResolver.splitValues(value)));
    }

    public static Builder<?> builder(int min, int max) {
        return simpleBuilder((weight, quality, conditions, functions) -> new MoneyBagLootEntry(min, max, weight, quality, conditions, functions));
    }

    @Override
    public LootPoolEntryType getType() {
        return NumismaticOverhaul.MONEY_BAG_ENTRY;
    }

    public static class Serializer extends LootPoolSingletonContainer.Serializer<MoneyBagLootEntry> {


        @Override
        public void serializeCustom(JsonObject jsonObject, MoneyBagLootEntry moneyBagLootEntry, JsonSerializationContext jsonSerializationContext) {
            super.serializeCustom(jsonObject, moneyBagLootEntry, jsonSerializationContext);
            jsonObject.addProperty("min", moneyBagLootEntry.min);
            jsonObject.addProperty("max", moneyBagLootEntry.max);
        }

        @Override
        protected MoneyBagLootEntry deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootItemCondition[] lootConditions, LootItemFunction[] lootFunctions) {
            final int mix = GsonHelper.getAsInt(jsonObject, "min", 0);
            final int max = GsonHelper.getAsInt(jsonObject, "max");
            return new MoneyBagLootEntry(mix, max, i, j, lootConditions, lootFunctions);
        }
    }
}
