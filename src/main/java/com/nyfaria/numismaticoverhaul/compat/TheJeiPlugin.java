package com.nyfaria.numismaticoverhaul.compat;

import com.nyfaria.numismaticoverhaul.NumismaticOverhaul;
import com.nyfaria.numismaticoverhaul.client.gui.ShopScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.common.gui.GuiProperties;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class TheJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(NumismaticOverhaul.MODID, "jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiScreenHandler(ShopScreen.class, (bop)->null);
    }
}