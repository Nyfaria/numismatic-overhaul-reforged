package com.nyfaria.numismaticoverhaul.event;

import com.nyfaria.numismaticoverhaul.NumismaticOverhaul;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIModelLoader;
import com.nyfaria.numismaticoverhaul.villagers.data.NumismaticVillagerTradesRegistry;
import com.nyfaria.numismaticoverhaul.villagers.data.VillagerTradesResourceListener;
import com.nyfaria.numismaticoverhaul.villagers.json.adapters.NumOTrade;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = NumismaticOverhaul.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonForgeEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onVillagerTrades(VillagerTradesEvent event) {
        if(event.getType() == VillagerProfession.NONE)return;
        for(int i = 1; i<= event.getTrades().size(); i++) {
            NumismaticVillagerTradesRegistry.registerFabricVillagerTrades(event.getType(),i, event.getTrades().get(i));
        }
//        for(int i = 1; i<= event.getTrades().size(); i++) {
//            if(event.getTrades().get(i).stream().filter(trade -> trade instanceof NumOTrade).count() == 0) {
//                continue;
//            }
//            for(int j = event.getTrades().get(i).size() - 1; j >=0; j--){
//                VillagerTrades.ItemListing trade = event.getTrades().get(i).get(j);
//                if(!(trade instanceof NumOTrade)){
//                    event.getTrades().get(i).remove(j);
//                }
//            }
//        }
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onWanderingTrades(WandererTradesEvent event) {
        for(int i = 0; i< event.getGenericTrades().size(); i++) {
            NumismaticVillagerTradesRegistry.registerWanderingTraderTrade(i, event.getGenericTrades().get(i));
        }
        for(int i = 0; i< event.getRareTrades().size(); i++) {
            NumismaticVillagerTradesRegistry.registerWanderingTraderTrade(i, event.getRareTrades().get(i));
        }
    }
    @SubscribeEvent
    public static void reloadListener(AddReloadListenerEvent event){
        event.addListener(new VillagerTradesResourceListener());
    }

}
