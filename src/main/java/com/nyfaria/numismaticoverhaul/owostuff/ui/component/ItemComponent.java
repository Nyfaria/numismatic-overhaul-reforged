package com.nyfaria.numismaticoverhaul.owostuff.ui.component;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.nyfaria.numismaticoverhaul.owostuff.ui.base.BaseComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Sizing;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIModel;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIModelParsingException;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIParsing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import org.w3c.dom.Element;

import java.util.Map;

public class ItemComponent extends BaseComponent {

    protected final MultiBufferSource.BufferSource entityBuffers;
    protected final ItemRenderer itemRenderer;
    protected ItemStack stack;
    protected boolean showOverlay = false;

    protected ItemComponent(ItemStack stack) {
        this.entityBuffers = Minecraft.getInstance().renderBuffers().bufferSource();
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
        this.stack = stack;
    }

    @Override
    protected void applyHorizontalContentSizing(Sizing sizing) {
        this.width = 16;
    }

    @Override
    protected void applyVerticalContentSizing(Sizing sizing) {
        this.height = 16;
    }

    @Override
    public void draw(PoseStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        final boolean notSideLit = !this.itemRenderer.getModel(this.stack, null, null, 0).usesBlockLight();
        if (notSideLit) {
            Lighting.setupForFlatItems();
        }

        var modelView = RenderSystem.getModelViewStack();
        modelView.pushPose();

        // Translate to the root of the component
        modelView.translate(x, y, 100);

        // Scale according to component size and translate to the center
        modelView.scale(this.width / 16f, this.height / 16f, 1);
        modelView.translate(8.0, 8.0, 0.0);

        // Vanilla scaling and y inversion
        modelView.scale(16, -16, 16);
        RenderSystem.applyModelViewMatrix();

        this.itemRenderer.renderStatic(this.stack, ItemTransforms.TransformType.GUI, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, new PoseStack(), entityBuffers, 0);
        this.entityBuffers.endBatch();

        // Clean up
        modelView.popPose();
        RenderSystem.applyModelViewMatrix();

        if (this.showOverlay) this.itemRenderer.renderGuiItemDecorations(Minecraft.getInstance().font, this.stack, this.x, this.y);
        if (notSideLit) {
            Lighting.setupFor3DItems();
        }
    }

    public ItemComponent stack(ItemStack stack) {
        this.stack = stack;
        return this;
    }

    public ItemStack stack() {
        return this.stack;
    }

    public ItemComponent showOverlay(boolean drawOverlay) {
        this.showOverlay = drawOverlay;
        return this;
    }

    public boolean showOverlay() {
        return this.showOverlay;
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        UIParsing.apply(children, "show-overlay", UIParsing::parseBool, this::showOverlay);
        UIParsing.apply(children, "item", UIParsing::parseIdentifier, itemId -> {
            var item = Registry.ITEM.getOptional(itemId).orElseThrow(() -> new UIModelParsingException("Unknown item " + itemId));
            this.stack(item.getDefaultInstance());
        });
    }
}
