package com.nyfaria.numismaticoverhaul.owostuff.ui.inject;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.AnimatableProperty;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.CursorStyle;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Insets;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ModComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ParentComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Positioning;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Size;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Sizing;
import com.nyfaria.numismaticoverhaul.owostuff.ui.event.CharTyped;
import com.nyfaria.numismaticoverhaul.owostuff.ui.event.FocusGained;
import com.nyfaria.numismaticoverhaul.owostuff.ui.event.FocusLost;
import com.nyfaria.numismaticoverhaul.owostuff.ui.event.KeyPress;
import com.nyfaria.numismaticoverhaul.owostuff.ui.event.MouseDown;
import com.nyfaria.numismaticoverhaul.owostuff.ui.event.MouseDrag;
import com.nyfaria.numismaticoverhaul.owostuff.ui.event.MouseEnter;
import com.nyfaria.numismaticoverhaul.owostuff.ui.event.MouseLeave;
import com.nyfaria.numismaticoverhaul.owostuff.ui.event.MouseScroll;
import com.nyfaria.numismaticoverhaul.owostuff.ui.event.MouseUp;
import com.nyfaria.numismaticoverhaul.owostuff.ui.util.FocusHandler;
import com.nyfaria.numismaticoverhaul.owostuff.util.EventSource;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Stub-version of component which adds implementations for all methods
 * which unconditionally throw - used for interface-injecting onto
 * vanilla widgets
 */
public interface ComponentStub extends ModComponent {

    @Override
    default void draw(PoseStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default @Nullable ParentComponent parent() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default @Nullable FocusHandler focusHandler() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default ModComponent positioning(Positioning positioning) {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default AnimatableProperty<Positioning> positioning() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default ModComponent margins(Insets margins) {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default AnimatableProperty<Insets> margins() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default ModComponent horizontalSizing(Sizing horizontalSizing) {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default ModComponent verticalSizing(Sizing verticalSizing) {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default AnimatableProperty<Sizing> horizontalSizing() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default AnimatableProperty<Sizing> verticalSizing() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default EventSource<MouseEnter> mouseEnter() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default EventSource<MouseLeave> mouseLeave() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default CursorStyle cursorStyle() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default ModComponent cursorStyle(CursorStyle style) {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default ModComponent tooltip(List<ClientTooltipComponent> tooltip) {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default List<ClientTooltipComponent> tooltip() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default ModComponent zIndex(int zIndex) {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default int zIndex() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default void inflate(Size space) {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default void mount(ParentComponent parent, int x, int y) {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default void dismount(DismountReason reason) {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default int width() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default int height() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default boolean onMouseDown(double mouseX, double mouseY, int button) {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default EventSource<MouseDown> mouseDown() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default boolean onMouseUp(double mouseX, double mouseY, int button) {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default EventSource<MouseUp> mouseUp() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default boolean onMouseScroll(double mouseX, double mouseY, double amount) {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default EventSource<MouseScroll> mouseScroll() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default EventSource<MouseDrag> mouseDrag() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default EventSource<KeyPress> keyPress() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default boolean onCharTyped(char chr, int modifiers) {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default EventSource<CharTyped> charTyped() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default void onFocusGained(FocusSource source) {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default EventSource<FocusGained> focusGained() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default void onFocusLost() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default EventSource<FocusLost> focusLost() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default int x() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default void setX(int x) {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default int y() {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default void setY(int y) {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default ModComponent id(@Nullable String id) {
        throw new IllegalStateException("Interface stub method called");
    }

    @Override
    default @Nullable String id() {
        throw new IllegalStateException("Interface stub method called");
    }
}
