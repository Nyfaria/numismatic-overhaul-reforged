package com.nyfaria.numismaticoverhaul.client;

import com.mojang.datafixers.util.Either;
import com.nyfaria.numismaticoverhaul.NumismaticOverhaul;
import com.nyfaria.numismaticoverhaul.client.gui.CurrencyTooltipComponent;
import com.nyfaria.numismaticoverhaul.client.gui.PiggyBankScreen;
import com.nyfaria.numismaticoverhaul.client.gui.ShopScreen;
import com.nyfaria.numismaticoverhaul.init.BlockInit;
import com.nyfaria.numismaticoverhaul.init.ItemInit;
import com.nyfaria.numismaticoverhaul.init.MenuInit;
import com.nyfaria.numismaticoverhaul.item.CurrencyTooltipData;
import com.nyfaria.numismaticoverhaul.item.MoneyBagItem;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = NumismaticOverhaul.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class NumismaticOverhaulClient {

    @SubscribeEvent
    public static void onInitializeClient(FMLClientSetupEvent event) {
        MenuScreens.register(MenuInit.SHOP.get(), ShopScreen::new);
        MenuScreens.register(MenuInit.PIGGY_BANK.get(), PiggyBankScreen::new);

        ItemProperties.register(ItemInit.BRONZE_COIN.get(), new ResourceLocation("coins"), (stack, world, entity, seed) -> stack.getCount() / 100.0f);
        ItemProperties.register(ItemInit.SILVER_COIN.get(), new ResourceLocation("coins"), (stack, world, entity, seed) -> stack.getCount() / 100.0f);
        ItemProperties.register(ItemInit.GOLD_COIN.get(), new ResourceLocation("coins"), (stack, world, entity, seed) -> stack.getCount() / 100.0f);

        ItemProperties.register(ItemInit.MONEY_BAG.get(), new ResourceLocation("size"), (stack, world, entity, seed) -> {
            long[] values = ((MoneyBagItem) ItemInit.MONEY_BAG.get()).getCombinedValue(stack);
            if (values.length < 3) return 0;

            if (values[2] > 0) return 1;
            if (values[1] > 0) return .5f;

            return 0;
        });
    }


    @SubscribeEvent
    public static void registerBlockEntity(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockInit.SHOP_BE.get(), ShopBlockEntityRender::new);

    }

    @Mod.EventBusSubscriber(modid = NumismaticOverhaul.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    static class ForgeEvents {
        @SubscribeEvent
        public static void onToolTip(RenderTooltipEvent.GatherComponents event) {
            for (int i = 0; i < event.getTooltipElements().size(); i++) {
                if (event.getTooltipElements().get(i).right().isPresent()) {
                    if (event.getTooltipElements().get(i).right().get() instanceof CurrencyTooltipData component) {
                        Either<FormattedText, TooltipComponent> bope = event.getTooltipElements().get(i);
                        bope.mapRight((tooltipComponent) -> new CurrencyTooltipComponent(component));
                        event.getTooltipElements().set(i, bope);
                        break;
                    }
                }
            }
        }
    }
}
