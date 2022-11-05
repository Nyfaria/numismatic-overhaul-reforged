package com.nyfaria.numismaticoverhaul.init;

import com.nyfaria.numismaticoverhaul.NumismaticOverhaul;
import com.nyfaria.numismaticoverhaul.block.PiggyBankScreenHandler;
import com.nyfaria.numismaticoverhaul.block.ShopScreenHandler;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuInit {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, NumismaticOverhaul.MODID);

    public static final RegistryObject<MenuType<ShopScreenHandler>> SHOP = MENU_TYPES.register("shop", () -> new MenuType<>(ShopScreenHandler::new));
    public static final RegistryObject<MenuType<PiggyBankScreenHandler>> PIGGY_BANK = MENU_TYPES.register("piggy_bank", () -> new MenuType<>(PiggyBankScreenHandler::new));
}
