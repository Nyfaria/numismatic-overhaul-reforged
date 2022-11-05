package com.nyfaria.numismaticoverhaul.owostuff.ui.inject;

import net.minecraft.client.gui.components.Button;

public interface ButtonWidgetExtension {

    default Button onPress(Button.OnPress pressAction) {
        throw new IllegalStateException("Interface default method called");
    }

}
