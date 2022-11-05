package com.nyfaria.numismaticoverhaul.mixin.owomixins.ui;

import com.nyfaria.numismaticoverhaul.owostuff.ui.inject.ButtonWidgetExtension;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("ConstantConditions")
@Mixin(Button.class)
public abstract class ButtonWidgetMixin extends AbstractWidget implements ButtonWidgetExtension {
    @Mutable
    @Shadow
    @Final
    protected Button.OnPress onPress;

    public ButtonWidgetMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    public Button onPress(Button.OnPress pressAction) {
        this.onPress = pressAction;
        return (Button) (Object) this;
    }
}
