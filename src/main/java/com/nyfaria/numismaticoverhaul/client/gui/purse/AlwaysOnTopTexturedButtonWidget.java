package com.nyfaria.numismaticoverhaul.client.gui.purse;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.resources.ResourceLocation;

/**
 * Extension of a normal {@link ImageButton} that does no depth testing and thus always draws on top
 */
public class AlwaysOnTopTexturedButtonWidget extends ImageButton {

    //Replicate some fields from super because they are private for reason
    private final int u;
    private final int v;
    private final int hoveredVOffset;

    private final ResourceLocation texture;

    public AlwaysOnTopTexturedButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredVOffset, ResourceLocation texture, OnPress pressAction) {
        super(x, y, width, height, u, v, hoveredVOffset, texture, pressAction);

        this.u = u;
        this.v = v;
        this.hoveredVOffset = hoveredVOffset;
        this.texture = texture;
    }

    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderTexture(0, texture);
        int i = this.v;
        if (this.isHoveredOrFocused()) {
            i += this.hoveredVOffset;
        }

        RenderSystem.disableDepthTest();

        blit(matrices, this.x, this.y, this.u, i, this.width, this.height);
        if (this.isHoveredOrFocused()) this.renderToolTip(matrices, mouseX, mouseY);
    }
}
