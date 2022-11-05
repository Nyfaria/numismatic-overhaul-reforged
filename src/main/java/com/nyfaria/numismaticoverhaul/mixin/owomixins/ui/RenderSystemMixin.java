package com.nyfaria.numismaticoverhaul.mixin.owomixins.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.OwoUIAdapter;
import com.nyfaria.numismaticoverhaul.owostuff.ui.util.ScissorStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderSystem.class, remap = false)
public class RenderSystemMixin {

    @Inject(method = "enableScissor", at = @At("HEAD"), cancellable = true)
    private static void pushScissors(int x, int y, int width, int height, CallbackInfo ci) {
        if (!OwoUIAdapter.isRendering()) return;

        ScissorStack.pushDirect(x, y, width, height);
        ci.cancel();
    }

    @Inject(method = "disableScissor", at = @At("HEAD"), cancellable = true)
    private static void popScissors(CallbackInfo ci) {
        if (!OwoUIAdapter.isRendering()) return;

        ScissorStack.pop();
        ci.cancel();
    }
}
