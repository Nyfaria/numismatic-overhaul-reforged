package com.nyfaria.numismaticoverhaul.event;

import com.nyfaria.numismaticoverhaul.NumismaticOverhaul;
import com.nyfaria.numismaticoverhaul.init.ItemInit;
import com.nyfaria.numismaticoverhaul.init.TagInit;
import com.nyfaria.numismaticoverhaul.villagers.data.NumismaticVillagerTradesRegistry;
import com.nyfaria.numismaticoverhaul.villagers.data.VillagerTradesResourceListener;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NumismaticOverhaul.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonForgeEvents {

    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.NONE) return;
        for (int i = 1; i <= event.getTrades().size(); i++) {
            NumismaticVillagerTradesRegistry.registerFabricVillagerTrades(event.getType(), i, event.getTrades().get(i));
        }
    }

    @SubscribeEvent
    public static void onVillagerTrades(WandererTradesEvent event) {
        for (int i = 0; i < event.getGenericTrades().size(); i++) {
            NumismaticVillagerTradesRegistry.registerFabricWanderingTraderTrades(i, event.getGenericTrades());
        }

        for (int i = 0; i < event.getRareTrades().size(); i++) {
            NumismaticVillagerTradesRegistry.registerFabricWanderingTraderTrades(i, event.getRareTrades());
        }
    }
    @SubscribeEvent
    public static void reloadListener(AddReloadListenerEvent event){
        event.addListener(new VillagerTradesResourceListener());
    }
    @SubscribeEvent
    public static void dropTheCoins(LivingDropsEvent event){
        if (!event.getEntityLiving().getType().is(TagInit.THE_BOURGEOISIE)) return;
        LivingEntity entity = event.getEntityLiving();
        if (entity.getRandom().nextFloat() > .5f)
            entity.spawnAtLocation(new ItemStack(ItemInit.BRONZE_COIN.get(), entity.getRandom().nextInt( 35-9+1)+9));
        if (entity.getRandom().nextFloat() > .2f) entity.spawnAtLocation(new ItemStack(ItemInit.SILVER_COIN.get()));
    }

}
