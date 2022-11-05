package com.nyfaria.numismaticoverhaul.init;

import com.nyfaria.numismaticoverhaul.NumismaticOverhaul;
import com.nyfaria.numismaticoverhaul.currency.Currency;
import com.nyfaria.numismaticoverhaul.item.CoinItem;
import com.nyfaria.numismaticoverhaul.item.MoneyBagItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NumismaticOverhaul.MODID);
    public static final RegistryObject<Item> BRONZE_COIN = ITEMS.register("bronze_coin", () -> new CoinItem(Currency.BRONZE));
    public static final RegistryObject<Item> SILVER_COIN = ITEMS.register("silver_coin", () -> new CoinItem(Currency.SILVER));
    public static final RegistryObject<Item> GOLD_COIN = ITEMS.register("gold_coin", () -> new CoinItem(Currency.GOLD));
    public static final RegistryObject<Item> MONEY_BAG = ITEMS.register("money_bag", MoneyBagItem::new);


    public static Item.Properties getItemProperties() {
        return new Item.Properties().tab(CreativeModeTab.TAB_MISC);
    }
}