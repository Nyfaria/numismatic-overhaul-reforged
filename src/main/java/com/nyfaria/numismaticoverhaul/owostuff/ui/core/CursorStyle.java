package com.nyfaria.numismaticoverhaul.owostuff.ui.core;

import org.lwjgl.glfw.GLFW;

public enum CursorStyle {
    /**
     * The default arrow-style pointing cursor
     */
    POINTER(GLFW.GLFW_ARROW_CURSOR),

    /**
     * The text selection, usually I-beam, cursor
     */
    TEXT(GLFW.GLFW_IBEAM_CURSOR),

    /**
     * The hand cursor which signals clickable areas
     */
    HAND(GLFW.GLFW_HAND_CURSOR),

    /**
     * The cross-shaped cursor which signals
     * draggable/movable areas
     */
    MOVE(GLFW.GLFW_CROSSHAIR_CURSOR);

    public final int glfw;

    CursorStyle(int glfw) {this.glfw = glfw;}
}
