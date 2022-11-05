package com.nyfaria.numismaticoverhaul.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.nyfaria.numismaticoverhaul.currency.CurrencyConverter;
import com.nyfaria.numismaticoverhaul.item.CurrencyTooltipData;
import com.nyfaria.numismaticoverhaul.owostuff.ops.ItemOps;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CurrencyTooltipComponent implements ClientTooltipComponent {

    private final CurrencyTooltipData data;
    private final List<Component> text;

    private int widthCache = -1;

    public CurrencyTooltipComponent(CurrencyTooltipData data) {
        this.data = data;
        this.text = new ArrayList<>();

        if (data.original()[0] != -1) {
            CurrencyConverter.getAsItemStackList(data.original()).forEach(stack -> text.add(createPlaceholder(stack.getCount())));
            text.add(Component.nullToEmpty(" "));
        }

        CurrencyConverter.getAsItemStackList(data.value()).forEach(stack -> text.add(createPlaceholder(stack.getCount())));
    }

    @Override
    public int getHeight() {
        return 10 * text.size();
    }

    @Override
    public int getWidth(Font textRenderer) {
        if (widthCache == -1) {
            widthCache = textRenderer.width(text.stream()
                    .max(Comparator.comparingInt(textRenderer::width)).orElse(Component.nullToEmpty("")));
        }
        return widthCache;
    }

    @Override
    public void renderText(Font textRenderer, int x, int y, Matrix4f matrix4f, MultiBufferSource.BufferSource immediate) {
        for (int i = 0; i < text.size(); i++) {
            textRenderer.drawInBatch(text.get(i), x, y + i * 10, -1, true, matrix4f, immediate, false, 0, LightTexture.FULL_BRIGHT);
        }
    }

    @Override
    public void renderImage(Font textRenderer, int x, int y, PoseStack matrices, ItemRenderer itemRenderer, int z) {
        List<ItemStack> originalCoins = data.original()[0] != -1 ? CurrencyConverter.getAsItemStackList(data.original()) : new ArrayList<>();
        List<ItemStack> coins = CurrencyConverter.getAsItemStackList(data.value());

        RenderSystem.setShaderTexture(0, new ResourceLocation("textures/gui/container/villager2.png"));
        for (int i = 0; i < originalCoins.size(); i++) {
            GuiComponent.blit(matrices, x + (originalCoins.get(i).getCount() > 9 ? 14 : 11), y + 3, z, 0, 176, 9, 2, 512, 256);
            itemRenderer.renderGuiItem(ItemOps.singleCopy(originalCoins.get(i)), x - 4, y - 5 + i * 10);
        }

        for (int i = 0; i < coins.size(); i++) {
            itemRenderer.renderGuiItem(ItemOps.singleCopy(coins.get(i)), x - 4, y - 5 + i * 10 + (originalCoins.size() == 0 ? 0 : 10 + originalCoins.size() * 10));
        }
    }

    private static Component createPlaceholder(int count) {
        String placeholder = "§7   " + count + " ";
        return Component.literal(placeholder).withStyle(ChatFormatting.GRAY);
    }

}
