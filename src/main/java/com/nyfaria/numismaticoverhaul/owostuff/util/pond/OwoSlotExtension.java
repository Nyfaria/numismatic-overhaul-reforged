package com.nyfaria.numismaticoverhaul.owostuff.util.pond;

import com.nyfaria.numismaticoverhaul.owostuff.ui.core.PositionedRectangle;
import org.jetbrains.annotations.Nullable;

public interface OwoSlotExtension {

    void owo$setDisabledOverride(boolean disabled);

    boolean owo$getDisabledOverride();

    void owo$setScissorArea(@Nullable PositionedRectangle scissor);

    @Nullable PositionedRectangle owo$getScissorArea();
}
