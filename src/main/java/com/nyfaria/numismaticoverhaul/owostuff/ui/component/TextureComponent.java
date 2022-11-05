package com.nyfaria.numismaticoverhaul.owostuff.ui.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.nyfaria.numismaticoverhaul.owostuff.ui.base.BaseComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.AnimatableProperty;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.PositionedRectangle;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Sizing;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIModel;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIParsing;
import com.nyfaria.numismaticoverhaul.owostuff.ui.util.Drawer;
import net.minecraft.resources.ResourceLocation;
import org.w3c.dom.Element;

import java.util.Map;

public class TextureComponent extends BaseComponent {

    protected final ResourceLocation texture;
    protected final int u, v;
    protected final int regionWidth, regionHeight;
    protected final int textureWidth, textureHeight;

    protected final AnimatableProperty<PositionedRectangle> visibleArea;
    protected boolean blend = false;

    protected TextureComponent(ResourceLocation texture, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.regionWidth = regionWidth;
        this.regionHeight = regionHeight;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;

        this.visibleArea = AnimatableProperty.of(PositionedRectangle.of(0, 0, this.regionWidth, this.regionHeight));
    }

    @Override
    protected void applyHorizontalContentSizing(Sizing sizing) {
        this.width = this.regionWidth;
    }

    @Override
    protected void applyVerticalContentSizing(Sizing sizing) {
        this.height = this.regionHeight;
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);
        this.visibleArea.update(delta);
    }

    @Override
    public void draw(PoseStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        RenderSystem.setShaderTexture(0, this.texture);
        RenderSystem.enableDepthTest();

        if (this.blend) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        }

        matrices.pushPose();
        matrices.translate(x, y, 0);
        matrices.scale(this.width / (float) this.regionWidth, this.height / (float) this.regionHeight, 0);

        var visibleArea = this.visibleArea.get();

        int bottomEdge = Math.min(visibleArea.y() + visibleArea.height(), regionHeight);
        int rightEdge = Math.min(visibleArea.x() + visibleArea.width(), regionWidth);

        Drawer.blit(matrices,
                visibleArea.x(),
                visibleArea.y(),
                rightEdge - visibleArea.x(),
                bottomEdge - visibleArea.y(),
                this.u + visibleArea.x(),
                this.v + visibleArea.y(),
                rightEdge - visibleArea.x(),
                bottomEdge - visibleArea.y(),
                this.textureWidth, this.textureHeight
        );

        if (this.blend) {
            RenderSystem.disableBlend();
        }

        matrices.popPose();
    }

    public TextureComponent visibleArea(PositionedRectangle visibleArea) {
        this.visibleArea.set(visibleArea);
        return this;
    }

    public TextureComponent resetVisibleArea() {
        this.visibleArea(PositionedRectangle.of(0, 0, this.regionWidth, this.regionHeight));
        return this;
    }

    public AnimatableProperty<PositionedRectangle> visibleArea() {
        return this.visibleArea;
    }

    public TextureComponent blend(boolean blend) {
        this.blend = blend;
        return this;
    }

    public boolean blend() {
        return this.blend;
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        UIParsing.apply(children, "blend", UIParsing::parseBool, this::blend);

        if (children.containsKey("visible-area")) {
            var areaChildren = UIParsing.childElements(children.get("visible-area"));

            int x = 0, y = 0, width = this.regionWidth, height = this.regionHeight;
            if (areaChildren.containsKey("x")) {
                x = UIParsing.parseSignedInt(areaChildren.get("x"));
            }

            if (areaChildren.containsKey("y")) {
                y = UIParsing.parseSignedInt(areaChildren.get("y"));
            }

            if (areaChildren.containsKey("width")) {
                width = UIParsing.parseSignedInt(areaChildren.get("width"));
            }

            if (areaChildren.containsKey("height")) {
                height = UIParsing.parseSignedInt(areaChildren.get("height"));
            }

            this.visibleArea(PositionedRectangle.of(x, y, width, height));
        }
    }

    public static TextureComponent parse(Element element) {
        UIParsing.expectAttributes(element, "texture");
        var textureId = UIParsing.parseIdentifier(element.getAttributeNode("texture"));

        int u = 0, v = 0, regionWidth = 0, regionHeight = 0, textureWidth = 256, textureHeight = 256;
        if (element.hasAttribute("u")) {
            u = UIParsing.parseSignedInt(element.getAttributeNode("u"));
        }

        if (element.hasAttribute("v")) {
            v = UIParsing.parseSignedInt(element.getAttributeNode("v"));
        }

        if (element.hasAttribute("region-width")) {
            regionWidth = UIParsing.parseSignedInt(element.getAttributeNode("region-width"));
        }

        if (element.hasAttribute("region-height")) {
            regionHeight = UIParsing.parseSignedInt(element.getAttributeNode("region-height"));
        }

        if (element.hasAttribute("texture-width")) {
            textureWidth = UIParsing.parseSignedInt(element.getAttributeNode("texture-width"));
        }

        if (element.hasAttribute("texture-height")) {
            textureHeight = UIParsing.parseSignedInt(element.getAttributeNode("texture-height"));
        }

        return new TextureComponent(textureId, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
    }
}
