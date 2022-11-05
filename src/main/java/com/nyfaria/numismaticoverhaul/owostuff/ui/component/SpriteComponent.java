package com.nyfaria.numismaticoverhaul.owostuff.ui.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.nyfaria.numismaticoverhaul.owostuff.ui.base.BaseComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Sizing;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIParsing;
import com.nyfaria.numismaticoverhaul.owostuff.ui.util.Drawer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import org.w3c.dom.Element;

public class SpriteComponent extends BaseComponent {

    protected final TextureAtlasSprite sprite;

    protected SpriteComponent(TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    @Override
    protected void applyHorizontalContentSizing(Sizing sizing) {
        this.width = this.sprite.getWidth();
    }

    @Override
    protected void applyVerticalContentSizing(Sizing sizing) {
        this.height = this.sprite.getHeight();
    }

    @Override
    public void draw(PoseStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        RenderSystem.setShaderTexture(0, this.sprite.atlas().location());
        Drawer.blit(matrices, this.x, this.y, 0, this.width, this.height, this.sprite);
    }

    public static SpriteComponent parse(Element element) {
        UIParsing.expectAttributes(element, "atlas", "sprite");

        var atlas = UIParsing.parseIdentifier(element.getAttributeNode("atlas"));
        var sprite = UIParsing.parseIdentifier(element.getAttributeNode("sprite"));

        return Components.sprite(new Material(atlas, sprite));
    }
}
