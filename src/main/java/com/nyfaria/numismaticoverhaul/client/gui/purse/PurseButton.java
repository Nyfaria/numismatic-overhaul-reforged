package com.nyfaria.numismaticoverhaul.client.gui.purse;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nyfaria.numismaticoverhaul.cap.CurrencyHolder;
import com.nyfaria.numismaticoverhaul.cap.CurrencyHolderAttacher;
import com.nyfaria.numismaticoverhaul.client.gui.CurrencyTooltipRenderer;
import com.nyfaria.numismaticoverhaul.currency.Currency;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.player.Player;

public class PurseButton extends ImageButton {

    private final CurrencyHolder currencyStorage;
    private final Screen parent;
    private final Component TOOLTIP_TITLE;

    public PurseButton(int x, int y, OnPress pressAction, Player player, Screen parent) {
        super(x, y, 11, 13, 62, 0, 13, PurseWidget.TEXTURE, pressAction);
        this.currencyStorage = CurrencyHolderAttacher.getExampleHolderUnwrap(player);
        this.parent = parent;
        this.TOOLTIP_TITLE = Component.translatable("gui.numismaticoverhaul.purse_title").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Currency.GOLD.getNameColor())));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (Minecraft.getInstance().player.isSpectator()) return false;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderToolTip(PoseStack matrices, int mouseX, int mouseY) {
        CurrencyTooltipRenderer.renderTooltip(
                currencyStorage.getValue(),
                matrices, parent,
                TOOLTIP_TITLE,
                x + 14, y + 5);
    }
}
