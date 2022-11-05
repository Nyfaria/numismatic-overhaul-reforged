package com.nyfaria.numismaticoverhaul.owostuff.ui.util;

import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ModComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ParentComponent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class FocusHandler {

    protected final ParentComponent root;
    @Nullable protected ModComponent focused = null;
    @Nullable protected ModComponent.FocusSource lastFocusSource = null;

    public FocusHandler(ParentComponent root) {
        this.root = root;
    }

    public void updateClickFocus(double mouseX, double mouseY) {
        var clicked = this.root.childAt((int) mouseX, (int) mouseY);
        this.focus(clicked != null && clicked.canFocus(ModComponent.FocusSource.MOUSE_CLICK) ? clicked : null, ModComponent.FocusSource.MOUSE_CLICK);
    }

    @Contract(pure = true)
    public @Nullable ModComponent focused() {
        return this.focused;
    }

    public ModComponent.FocusSource lastFocusSource() {
        return this.lastFocusSource;
    }

    public void cycle(boolean forwards) {
        var allChildren = new ArrayList<ModComponent>();
        this.root.collectChildren(allChildren);

        allChildren.removeIf(component -> !component.canFocus(ModComponent.FocusSource.KEYBOARD_CYCLE));
        if (allChildren.isEmpty()) return;

        int index = (this.focused == null ? 0 : allChildren.indexOf(this.focused)) + (forwards ? 1 : -1);
        if (index >= allChildren.size()) index -= allChildren.size();
        if (index < 0) index += allChildren.size();

        this.focus(allChildren.get(index), ModComponent.FocusSource.KEYBOARD_CYCLE);
    }

    public void moveFocus(int keyCode) {
        if (this.focused == null) return;

        var allChildren = new ArrayList<ModComponent>();
        this.root.collectChildren(allChildren);

        allChildren.removeIf(component -> !component.canFocus(ModComponent.FocusSource.KEYBOARD_CYCLE));
        if (allChildren.isEmpty()) return;

        var closest = this.focused;
        switch (keyCode) {
            case GLFW.GLFW_KEY_RIGHT -> {
                int closestX = Integer.MAX_VALUE, closestY = Integer.MAX_VALUE;

                for (var child : allChildren) {
                    if (child == this.focused) continue;
                    if (child.x() < this.focused.x() + this.focused.width() ||
                            child.x() > closestX || Math.abs(child.y() - this.focused.y()) > closestY) continue;

                    closest = child;
                    closestX = child.x();
                    closestY = Math.abs(child.y() - this.focused.y());
                }
            }
            case GLFW.GLFW_KEY_LEFT -> {
                int closestX = 0, closestY = Integer.MAX_VALUE;

                for (var child : allChildren) {
                    if (child == this.focused) continue;
                    if (child.x() + child.width() > this.focused.x() ||
                            child.x() + child.width() < closestX || Math.abs(child.y() - this.focused.y()) > closestY) continue;

                    closest = child;
                    closestX = child.x() + child.width();
                    closestY = Math.abs(child.y() - this.focused.y());
                }
            }
            case GLFW.GLFW_KEY_UP -> {
                int closestX = Integer.MAX_VALUE, closestY = 0;

                for (var child : allChildren) {
                    if (child == this.focused) continue;
                    if (child.y() + child.height() > this.focused.y() ||
                            child.y() + child.height() < closestY || Math.abs(child.x() - this.focused.x()) > closestX) continue;

                    closest = child;
                    closestX = Math.abs(child.x() - this.focused.x());
                    closestY = child.y() + child.height();
                }
            }
            case GLFW.GLFW_KEY_DOWN -> {
                int closestX = Integer.MAX_VALUE, closestY = Integer.MAX_VALUE;

                for (var child : allChildren) {
                    if (child == this.focused) continue;
                    if (child.y() < this.focused.y() + this.focused.height() ||
                            child.y() + child.height() > closestY || Math.abs(child.x() - this.focused.x()) > closestX) continue;

                    closest = child;
                    closestX = Math.abs(child.x() - this.focused.x());
                    closestY = child.y() + child.height();
                }
            }
        }

        this.focus(closest, ModComponent.FocusSource.KEYBOARD_CYCLE);
    }

    public void focus(@Nullable ModComponent component, ModComponent.FocusSource source) {
        if (this.focused != component) {
            if (this.focused != null) {
                this.focused.onFocusLost();
            }

            if ((this.focused = component) != null) {
                this.focused.onFocusGained(source);
                this.lastFocusSource = source;
            } else {
                this.lastFocusSource = null;
            }
        }
    }

}
