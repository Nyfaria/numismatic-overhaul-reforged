package com.nyfaria.numismaticoverhaul.mixin.owomixins.ui;

import net.minecraft.client.gui.components.Checkbox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Checkbox.class)
public interface CheckboxWidgetAccessor {
    @Accessor("selected")
    void owo$setChecked(boolean checked);
}
