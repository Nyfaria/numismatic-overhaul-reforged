package com.nyfaria.numismaticoverhaul.owostuff.ui.core;

import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIModelParsingException;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIParsing;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public record Insets(int top, int bottom, int left, int right) implements Animatable<Insets> {

    private static final Insets NONE = new Insets(0, 0, 0, 0);

    @ApiStatus.Internal
    @Deprecated(forRemoval = true)
    public Insets {}

    public Insets inverted() {
        return new Insets(-this.top, -this.bottom, -this.left, -this.right);
    }

    public Insets add(int top, int bottom, int left, int right) {
        return new Insets(this.top + top, this.bottom + bottom, this.left + left, this.right + right);
    }

    public Insets withTop(int top) {
        return new Insets(top, this.bottom, this.left, this.right);
    }

    public Insets withBottom(int bottom) {
        return new Insets(this.top, bottom, this.left, this.right);
    }

    public Insets withLeft(int left) {
        return new Insets(this.top, this.bottom, left, this.right);
    }

    public Insets withRight(int right) {
        return new Insets(this.top, this.bottom, this.left, right);
    }

    public int horizontal() {
        return this.left + this.right;
    }

    public int vertical() {
        return this.top + this.bottom;
    }

    @Override
    public Insets interpolate(Insets next, float delta) {
        return new Insets(
                (int) Mth.lerp(delta, this.top, next.top),
                (int) Mth.lerp(delta, this.bottom, next.bottom),
                (int) Mth.lerp(delta, this.left, next.left),
                (int) Mth.lerp(delta, this.right, next.right)
        );
    }

    public static Insets both(int horizontal, int vertical) {
        return new Insets(vertical, vertical, horizontal, horizontal);
    }

    public static Insets top(int top) {
        return new Insets(top, 0, 0, 0);
    }

    public static Insets bottom(int bottom) {
        return new Insets(0, bottom, 0, 0);
    }

    public static Insets left(int left) {
        return new Insets(0, 0, left, 0);
    }

    public static Insets right(int right) {
        return new Insets(0, 0, 0, right);
    }

    public static Insets of(int top, int bottom, int left, int right) {
        return new Insets(top, bottom, left, right);
    }

    public static Insets of(int inset) {
        return new Insets(inset, inset, inset, inset);
    }

    public static Insets vertical(int inset) {
        return new Insets(inset, inset, 0, 0);
    }

    public static Insets horizontal(int inset) {
        return new Insets(0, 0, inset, inset);
    }

    public static Insets none() {
        return NONE;
    }

    public static Insets parse(Element insetsElement) {
        int top = 0, bottom = 0, left = 0, right = 0;

        for (var node : UIParsing.<Element>allChildrenOfType(insetsElement, Node.ELEMENT_NODE)) {
            try {
                int value = Integer.parseInt(node.getTextContent().strip());

                switch (node.getNodeName()) {
                    case "top" -> top = value;
                    case "bottom" -> bottom = value;
                    case "left" -> left = value;
                    case "right" -> right = value;
                    case "all" -> right = left = top = bottom = value;
                    case "vertical" -> top = bottom = value;
                    case "horizontal" -> left = right = value;
                }
            } catch (NumberFormatException exception) {
                throw new UIModelParsingException("Non-int value in inset declaration");
            }
        }

        return of(top, bottom, left, right);
    }
}
