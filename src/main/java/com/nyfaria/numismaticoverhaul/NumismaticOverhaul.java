package com.nyfaria.numismaticoverhaul;


import com.nyfaria.numismaticoverhaul.cap.CurrencyHolderAttacher;
import com.nyfaria.numismaticoverhaul.config.ExampleClientConfig;
import com.nyfaria.numismaticoverhaul.config.ExampleConfig;
import com.nyfaria.numismaticoverhaul.currency.MoneyBagLootEntry;
import com.nyfaria.numismaticoverhaul.datagen.ModBlockStateProvider;
import com.nyfaria.numismaticoverhaul.datagen.ModItemModelProvider;
import com.nyfaria.numismaticoverhaul.datagen.ModLangProvider;
import com.nyfaria.numismaticoverhaul.datagen.ModLootTableProvider;
import com.nyfaria.numismaticoverhaul.datagen.ModRecipeProvider;
import com.nyfaria.numismaticoverhaul.datagen.ModSoundProvider;
import com.nyfaria.numismaticoverhaul.init.BlockInit;
import com.nyfaria.numismaticoverhaul.init.EntityInit;
import com.nyfaria.numismaticoverhaul.init.ItemInit;
import com.nyfaria.numismaticoverhaul.init.MenuInit;
import com.nyfaria.numismaticoverhaul.item.MoneyBagItem;
import com.nyfaria.numismaticoverhaul.network.NetworkHandler;
import com.nyfaria.numismaticoverhaul.villagers.json.VillagerTradesHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(NumismaticOverhaul.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class NumismaticOverhaul {
    public static final String MODID = "numismaticoverhaul";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final LootPoolEntryType MONEY_BAG_ENTRY = new LootPoolEntryType(new MoneyBagLootEntry.Serializer());
    public static final CreativeModeTab NUMISMATIC_GROUP = new CreativeModeTab(MODID) {
        @Override
        public ItemStack makeIcon() {
            return MoneyBagItem.createCombined(new long[]{0,1,0});
        }
    };
    public static final GameRules.Key<GameRules.IntegerValue> MONEY_DROP_PERCENTAGE
            = GameRules.register("moneyDropPercentage", GameRules.Category.PLAYER, GameRules.IntegerValue.create(10));

    public NumismaticOverhaul() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ExampleConfig.CONFIG_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ExampleClientConfig.CLIENT_SPEC);
        ItemInit.ITEMS.register(bus);
        EntityInit.ENTITIES.register(bus);
        BlockInit.BLOCKS.register(bus);
        BlockInit.BLOCK_ENTITIES.register(bus);
        CurrencyHolderAttacher.register();
        MenuInit.MENU_TYPES.register(bus);
        VillagerTradesHandler.registerDefaultAdapters();

    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        NetworkHandler.register();
    }



    public static final Component PREFIX = new TextComponent("").withStyle(ChatFormatting.GRAY)
            .append(withColor("o", 0x3955e5))
            .append(withColor("ω", 0x13a6f0))
            .append(withColor("o", 0x3955e5))
            .append(new TextComponent(" > ").withStyle(ChatFormatting.GRAY));
    public static MutableComponent withColor(String text, int color) {
        return new TextComponent(text).setStyle(Style.EMPTY.withColor(color));
    }
    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }
}
