package com.nyfaria.numismaticoverhaul.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nyfaria.numismaticoverhaul.block.PiggyBankScreenHandler;
import com.nyfaria.numismaticoverhaul.owostuff.ui.base.BaseUIModelHandledScreen;
import com.nyfaria.numismaticoverhaul.owostuff.ui.base.BaseUIModelScreen;
import com.nyfaria.numismaticoverhaul.owostuff.ui.component.TextureComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.container.FlowLayout;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Sizing;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PiggyBankScreen extends BaseUIModelHandledScreen<FlowLayout, PiggyBankScreenHandler> {

    private TextureComponent bronzeHint, silverHint, goldHint;

    public PiggyBankScreen(PiggyBankScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title, FlowLayout.class, BaseUIModelScreen.DataSource.file("../src/main/resources/assets/numismaticoverhaul/owo_ui/piggy_bank.xml"));
        this.imageHeight = 145;
        this.inventoryLabelY = this.imageHeight - 94;
        this.titleLabelX = (this.imageWidth - Minecraft.getInstance().font.width(title)) / 2;
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        this.bronzeHint = this.uiAdapter.rootComponent.childById(TextureComponent.class, "bronze-hint");
        this.silverHint = this.uiAdapter.rootComponent.childById(TextureComponent.class, "silver-hint");
        this.goldHint = this.uiAdapter.rootComponent.childById(TextureComponent.class, "gold-hint");
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        this.bronzeHint.sizing(this.menu.getSlot(0).hasItem() ? Sizing.fixed(0) : Sizing.fixed(16));
        this.silverHint.sizing(this.menu.getSlot(1).hasItem() ? Sizing.fixed(0) : Sizing.fixed(16));
        this.goldHint.sizing(this.menu.getSlot(2).hasItem() ? Sizing.fixed(0) : Sizing.fixed(16));
    }
}
