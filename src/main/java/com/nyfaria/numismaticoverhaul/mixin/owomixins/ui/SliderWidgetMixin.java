package com.nyfaria.numismaticoverhaul.mixin.owomixins.ui;

import com.nyfaria.numismaticoverhaul.owostuff.ui.component.DiscreteSliderComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.CursorStyle;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;

@SuppressWarnings("ConstantConditions")
@Mixin(AbstractSliderButton.class)
public abstract class SliderWidgetMixin extends AbstractWidget {
    @Shadow
    protected abstract void setValue(double value);

    public SliderWidgetMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Inject(method = "setValueFromMouse", at = @At("HEAD"), cancellable = true)
    private void makeItSnappyTeam(double mouseX, CallbackInfo ci) {
        if (!((Object) this instanceof DiscreteSliderComponent discrete)) return;
        if (!discrete.snap()) return;

        ci.cancel();

        double value = (mouseX - (this.x + 4d)) / (this.width - 8d);
        double min = discrete.min(), max = discrete.max();
        int decimalPlaces = discrete.decimalPlaces();

        this.setValue(
                (new BigDecimal(min + value * (max - min)).setScale(decimalPlaces, RoundingMode.HALF_UP).doubleValue() - min) / (max - min)
        );
    }

    protected CursorStyle owo$preferredCursorStyle() {
        return CursorStyle.MOVE;
    }
}