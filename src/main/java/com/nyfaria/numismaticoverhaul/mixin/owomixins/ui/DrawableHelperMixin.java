package com.nyfaria.numismaticoverhaul.mixin.owomixins.ui;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.math.Matrix4f;
import com.nyfaria.numismaticoverhaul.owostuff.ui.util.Drawer;
import com.nyfaria.numismaticoverhaul.owostuff.util.pond.OwoBufferBuilderExtension;
import net.minecraft.client.gui.GuiComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GuiComponent.class)
public class DrawableHelperMixin {

    @Inject(method = "innerBlit(Lcom/mojang/math/Matrix4f;IIIIIFFFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;begin(Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;Lcom/mojang/blaze3d/vertex/VertexFormat;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void injectBufferBegin(Matrix4f matrix, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1, CallbackInfo ci, BufferBuilder bufferBuilder) {
        if (!Drawer.recording()) return;

        if (bufferBuilder.building()) {
            ((OwoBufferBuilderExtension) bufferBuilder).owo$skipNextBegin();
        }
    }

    @Inject(method = "innerBlit(Lcom/mojang/math/Matrix4f;IIIIIFFFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;end()Lcom/mojang/blaze3d/vertex/BufferBuilder$RenderedBuffer;"), cancellable = true)
    private static void skipDraw(Matrix4f matrix, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1, CallbackInfo ci) {
        if (Drawer.recording()) ci.cancel();
    }
}
