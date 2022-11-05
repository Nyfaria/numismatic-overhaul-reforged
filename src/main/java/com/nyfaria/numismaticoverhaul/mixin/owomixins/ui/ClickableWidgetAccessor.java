package com.nyfaria.numismaticoverhaul.mixin.owomixins.ui;

import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractWidget.class)
public interface ClickableWidgetAccessor {

    @Accessor("height")
    void owo$setHeight(int height);

}
