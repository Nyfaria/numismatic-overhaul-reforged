package com.nyfaria.numismaticoverhaul.villagers.json.adapters;

import com.google.gson.JsonObject;
import com.nyfaria.numismaticoverhaul.NumismaticOverhaul;
import com.nyfaria.numismaticoverhaul.currency.CurrencyHelper;
import com.nyfaria.numismaticoverhaul.owostuff.util.RegistryAccess;
import com.nyfaria.numismaticoverhaul.villagers.json.TradeJsonAdapter;
import com.nyfaria.numismaticoverhaul.villagers.json.VillagerJsonHelper;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.saveddata.maps.MapDecoration.Type;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class SellMapAdapter extends TradeJsonAdapter {

    @Override
    @NotNull
    public VillagerTrades.ItemListing deserialize(JsonObject json) {

        loadDefaultStats(json, true);

        VillagerJsonHelper.assertString(json, "structure");
        int price = json.get("price").getAsInt();

        final var structure = new ResourceLocation(GsonHelper.getAsString(json, "structure"));
        return new Factory(price, structure, max_uses, villager_experience, price_multiplier);
    }

    private static class Factory implements VillagerTrades.ItemListing {
        private final int price;
        private final ResourceLocation structureId;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public Factory(int price, ResourceLocation feature, int maxUses, int experience, float multiplier) {
            this.price = price;
            this.structureId = feature;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        @Nullable
        public MerchantOffer getOffer(Entity entity, Random random) {
            if (!(entity.level instanceof ServerLevel serverWorld)) return null;

            final var registry = serverWorld.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
            final var feature = RegistryAccess.getEntry(registry, this.structureId);

            if (feature == null || feature.unwrapKey().isEmpty()) {
                NumismaticOverhaul.LOGGER.error("Tried to create map to invalid structure " + this.structureId);
                return null;
            }

            final var result = serverWorld.getChunkSource().getGenerator().findNearestMapFeature(serverWorld, HolderSet.direct(feature),
                    entity.blockPosition(), 1500, true);

            if (result == null) return null;
            final var blockPos = result.getFirst();

            var iconType = Type.TARGET_X;
            if (feature.is(BuiltinStructures.OCEAN_MONUMENT.location()))
                iconType = Type.MONUMENT;
            if (feature.is(BuiltinStructures.WOODLAND_MANSION.location()))
                iconType = Type.MANSION;
            if (feature.is(BuiltinStructures.PILLAGER_OUTPOST.location()))
                iconType = Type.TARGET_POINT;

            ItemStack itemStack = MapItem.create(serverWorld, blockPos.getX(), blockPos.getZ(), (byte) 2, true, true);
            MapItem.renderBiomePreviewMap(serverWorld, itemStack);
            MapItemSavedData.addTargetDecoration(itemStack, blockPos, "+", iconType);
            itemStack.setHoverName(new TranslatableComponent("filled_map." + feature.unwrapKey().get().location().getPath().toLowerCase(Locale.ROOT)));
            return new MerchantOffer(CurrencyHelper.getClosest(price), new ItemStack(Items.MAP), itemStack, this.maxUses, this.experience, multiplier);
        }
    }
}
