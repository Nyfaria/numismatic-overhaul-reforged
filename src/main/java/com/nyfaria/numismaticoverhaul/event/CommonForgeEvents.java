package com.nyfaria.numismaticoverhaul.event;

import com.nyfaria.numismaticoverhaul.NumismaticOverhaul;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIModelLoader;
import com.nyfaria.numismaticoverhaul.villagers.data.NumismaticVillagerTradesRegistry;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NumismaticOverhaul.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonForgeEvents {

    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event) {
        if(event.getType() == VillagerProfession.NONE)return;
        for(int i = 1; i<= event.getTrades().size(); i++) {
            NumismaticVillagerTradesRegistry.registerFabricVillagerTrades(event.getType(),i, event.getTrades().get(i));
        }
    }
    @SubscribeEvent
    public static void onVillagerTrades(WandererTradesEvent event) {
        for(int i = 0; i< event.getGenericTrades().size(); i++) {
            NumismaticVillagerTradesRegistry.registerWanderingTraderTrade(i, event.getGenericTrades().get(i));
        }
        for(int i = 0; i< event.getRareTrades().size(); i++) {
            NumismaticVillagerTradesRegistry.registerWanderingTraderTrade(i, event.getRareTrades().get(i));
        }
    }


}
