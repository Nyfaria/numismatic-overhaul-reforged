package com.nyfaria.numismaticoverhaul.event;

import com.nyfaria.numismaticoverhaul.NumismaticOverhaul;
import com.nyfaria.numismaticoverhaul.client.gui.CurrencyTooltipComponent;
import com.nyfaria.numismaticoverhaul.item.CurrencyTooltipData;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIModelLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = NumismaticOverhaul.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    @SubscribeEvent
    public static void onTooltip(FMLClientSetupEvent event) {
        MinecraftForgeClient.registerTooltipComponentFactory(CurrencyTooltipData.class, CurrencyTooltipComponent::new);
    }
    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new UIModelLoader());
    }
}
