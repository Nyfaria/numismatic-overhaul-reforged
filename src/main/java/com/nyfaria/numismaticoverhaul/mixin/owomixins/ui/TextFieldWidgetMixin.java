package com.nyfaria.numismaticoverhaul.mixin.owomixins.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.CursorStyle;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Insets;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ModComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Positioning;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Sizing;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIModel;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIParsing;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.w3c.dom.Element;

import java.util.Map;

@SuppressWarnings("ConstantConditions")
@Mixin(EditBox.class)
public abstract class TextFieldWidgetMixin extends AbstractWidget implements ModComponent {

    @Shadow
    public abstract void setBordered(boolean drawsBackground);

    public TextFieldWidgetMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    public void drawFocusHighlight(PoseStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        // noop, since TextFieldWidget already does this
    }

    @Override
    public void parseProperties(UIModel spec, Element element, Map<String, Element> children) {
        superparseProperties(spec, element, children);
        UIParsing.apply(children, "text", e -> e.getTextContent().strip(), text -> {
            ((EditBox) (Object) this).setValue(text);
            ((EditBox) (Object) this).moveCursorToStart();
        });
        UIParsing.apply(children, "show-background", UIParsing::parseBool, this::setBordered);
    }

    @SuppressWarnings("ReferenceToMixin")
    @Inject(method = "setX", at = @At("HEAD"), cancellable = true)
    private void applyToWrapper(int x, CallbackInfo ci) {
        final var wrapper = ((ClickableWidgetMixin) (Object) this).owo$wrapper;
        if (wrapper != null) {
            wrapper.setX(x);
            ci.cancel();
        }
    }

    protected CursorStyle owo$preferredCursorStyle() {
        return CursorStyle.TEXT;
    }

    public void superparseProperties(UIModel spec, Element element, Map<String, Element> children) {
        // --- copied from Component, because you can't invoke interface super methods in mixins - very cool ---

        if (!element.getAttribute("id").isBlank()) {
            this.id(element.getAttribute("id").strip());
        }

        UIParsing.apply(children, "margins", Insets::parse, this::margins);
        UIParsing.apply(children, "positioning", Positioning::parse, this::positioning);
        UIParsing.apply(children, "z-index", UIParsing::parseSignedInt, this::zIndex);
        UIParsing.apply(children, "cursor-style", UIParsing.parseEnum(CursorStyle.class), this::cursorStyle);
        UIParsing.apply(children, "tooltip-text", UIParsing::parseText, this::tooltip);

        if (children.containsKey("sizing")) {
            var sizingValues = UIParsing.childElements(children.get("sizing"));
            UIParsing.apply(sizingValues, "vertical", Sizing::parse, this::verticalSizing);
            UIParsing.apply(sizingValues, "horizontal", Sizing::parse, this::horizontalSizing);
        }

        // --- end ---

        UIParsing.apply(children, "active", UIParsing::parseBool, active -> this.active = active);
    }
}
