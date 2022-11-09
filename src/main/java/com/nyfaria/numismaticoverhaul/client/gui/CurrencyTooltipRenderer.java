package com.nyfaria.numismaticoverhaul.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nyfaria.numismaticoverhaul.currency.CurrencyConverter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CurrencyTooltipRenderer {

    public static void renderTooltip(long value, PoseStack matrices, Screen screen, Component title, int x, int y) {

        List<Component> tooltip = new ArrayList<>();
        tooltip.add(title);

        y += 10;

        List<ItemStack> coins = CurrencyConverter.getAsItemStackList(value);

        Minecraft.getInstance().getItemRenderer().blitOffset = 700.0f;

        for (int i = 0; i < coins.size(); i++) {
            renderStack(matrices, coins.get(i), tooltip, i, x, y, screen.getBlitOffset() + 1000);
        }

        if (tooltip.size() == 1) {
            tooltip.add(new TranslatableComponent("numismaticoverhaul.empty").withStyle(ChatFormatting.GRAY));
        }

        screen.renderComponentTooltip(matrices, tooltip, x, y - 15);
    }

    private static void renderStack(PoseStack matrices, ItemStack stack, List<Component> tooltip, int index, int x, int y, int z) {
        tooltip.add(createPlaceholder(String.valueOf(stack.getCount())));

        ItemStack toRender = stack.copy();
        toRender.setCount(1);

        int localX = x + 8;
        int localY = y - (2 - index) * 10;

        Minecraft.getInstance().getItemRenderer().renderGuiItem(toRender, localX, localY);
    }

    private static Component createPlaceholder(String text) {
        String placeholder = "§7   " + text + " ";
        return Component.nullToEmpty(placeholder);
    }

}
