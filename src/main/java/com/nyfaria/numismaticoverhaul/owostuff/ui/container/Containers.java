package com.nyfaria.numismaticoverhaul.owostuff.ui.container;

import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ModComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Sizing;

public class Containers {

    // ------
    // Layout
    // ------

    public static GridLayout grid(Sizing horizontalSizing, Sizing verticalSizing, int rows, int columns) {
        return new GridLayout(horizontalSizing, verticalSizing, rows, columns);
    }

    public static VerticalFlowLayout verticalFlow(Sizing horizontalSizing, Sizing verticalSizing) {
        return new VerticalFlowLayout(horizontalSizing, verticalSizing);
    }

    public static HorizontalFlowLayout horizontalFlow(Sizing horizontalSizing, Sizing verticalSizing) {
        return new HorizontalFlowLayout(horizontalSizing, verticalSizing);
    }

    // ------
    // Scroll
    // ------

    public static <C extends ModComponent> ScrollContainer<C> verticalScroll(Sizing horizontalSizing, Sizing verticalSizing, C child) {
        return new ScrollContainer<>(ScrollContainer.ScrollDirection.VERTICAL, horizontalSizing, verticalSizing, child);
    }

    public static <C extends ModComponent> ScrollContainer<C> horizontalScroll(Sizing horizontalSizing, Sizing verticalSizing, C child) {
        return new ScrollContainer<>(ScrollContainer.ScrollDirection.HORIZONTAL, horizontalSizing, verticalSizing, child);
    }

    // ----------------
    // Utility wrappers
    // ----------------

    public static <C extends ModComponent> DraggableContainer<C> draggable(Sizing horizontalSizing, Sizing verticalSizing, C child) {
        return new DraggableContainer<>(horizontalSizing, verticalSizing, child);
    }

    public static CollapsibleContainer collapsible(Sizing horizontalSizing, Sizing verticalSizing, net.minecraft.network.chat.Component title, boolean expanded) {
        return new CollapsibleContainer(horizontalSizing, verticalSizing, title, expanded);
    }

}
