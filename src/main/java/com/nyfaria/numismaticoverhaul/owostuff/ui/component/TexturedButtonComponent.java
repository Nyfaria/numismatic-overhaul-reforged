package com.nyfaria.numismaticoverhaul.owostuff.ui.component;

import com.mojang.blaze3d.systems.RenderSystem;
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
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIModel;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIParsing;
import com.nyfaria.numismaticoverhaul.owostuff.ui.util.Drawer;
import com.nyfaria.numismaticoverhaul.owostuff.ui.util.FocusHandler;
import com.nyfaria.numismaticoverhaul.owostuff.util.EventSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

/**
 * @deprecated Replaced by {@link com.nyfaria.numismaticoverhaul.owostuff.ui.component.ButtonComponent.Renderer#texture(ResourceLocation, int, int, int, int)}
 */
@Deprecated(forRemoval = true)
public class TexturedButtonComponent extends Button implements ModComponent {

    protected final ResourceLocation texture;
    protected final int textureWidth, textureHeight;
    protected final int u, v;

    protected boolean textShadow = true;

    protected TexturedButtonComponent(ResourceLocation texture, int width, int height, int u, int v, int textureWidth, int textureHeight, Component message, OnPress onPress) {
        super(0, 0, width, height, message, onPress);
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.texture);

        int renderV = this.v;
        if (!this.active) {
            renderV += this.height * 2;
        } else if (this.isHoveredOrFocused()) {
            renderV += this.height;
        }

        RenderSystem.enableDepthTest();
        Drawer.blit(matrices, this.x, this.y, this.u, renderV, this.width, this.height, this.textureWidth, this.textureHeight);

        var textRenderer = Minecraft.getInstance().font;
        if (this.textShadow) {
            Drawer.drawCenteredString(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, 0xFFFFFF);
        } else {
            textRenderer.draw(matrices, this.getMessage(), this.x + this.width / 2f - textRenderer.width(this.getMessage()) / 2f, this.y + (this.height - 8) / 2f, 0xFFFFFF);
        }

        if (this.isHovered) this.renderToolTip(matrices, mouseX, mouseY);
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        superparseProperties(model, element, children);
        UIParsing.apply(children, "text-shadow", UIParsing::parseBool, this::textShadow);
    }

    public TexturedButtonComponent textShadow(boolean textShadow) {
        this.textShadow = textShadow;
        return this;
    }

    public boolean textShadow() {
        return this.textShadow;
    }

    public static TexturedButtonComponent parse(Element element) {
        UIParsing.expectAttributes(element, "texture", "width", "height");
        var textureId = UIParsing.parseIdentifier(element.getAttributeNode("texture"));

        int width = UIParsing.parseSignedInt(element.getAttributeNode("width"));
        int height = UIParsing.parseSignedInt(element.getAttributeNode("height"));

        int u = 0, v = 0, textureWidth = 256, textureHeight = 256;
        if (element.hasAttribute("u")) {
            u = UIParsing.parseSignedInt(element.getAttributeNode("u"));
        }

        if (element.hasAttribute("v")) {
            v = UIParsing.parseSignedInt(element.getAttributeNode("v"));
        }

        if (element.hasAttribute("texture-width")) {
            textureWidth = UIParsing.parseSignedInt(element.getAttributeNode("texture-width"));
        }

        if (element.hasAttribute("texture-height")) {
            textureHeight = UIParsing.parseSignedInt(element.getAttributeNode("texture-height"));
        }

        return Components.texturedButton(textureId, Component.empty(), width, height, u, v, textureWidth, textureHeight, button -> {});
    }

    @Unique
    protected VanillaWidgetComponent owo$wrapper = null;

    @Unique
    protected CursorStyle preferredCursorStyle = CursorStyle.POINTER;

    @Override
    public void inflate(Size space) {
        this.owo$getWrapper().inflate(space);
    }

    @Override
    public void mount(ParentComponent parent, int x, int y) {
        this.owo$getWrapper().mount(parent, x, y);
    }

    @Override
    public void dismount(DismountReason reason) {
        this.owo$getWrapper().dismount(reason);
    }

    @Nullable
    @Override
    public ParentComponent parent() {
        return this.owo$getWrapper().parent();
    }

    @Override
    public @Nullable FocusHandler focusHandler() {
        return this.owo$getWrapper().focusHandler();
    }

    @Override
    public ModComponent positioning(Positioning positioning) {
        this.owo$getWrapper().positioning(positioning);
        return this;
    }

    @Override
    public AnimatableProperty<Positioning> positioning() {
        return this.owo$getWrapper().positioning();
    }

    @Override
    public ModComponent margins(Insets margins) {
        this.owo$getWrapper().margins(margins);
        return this;
    }

    @Override
    public AnimatableProperty<Insets> margins() {
        return this.owo$getWrapper().margins();
    }

    @Override
    public ModComponent horizontalSizing(Sizing horizontalSizing) {
        this.owo$getWrapper().horizontalSizing(horizontalSizing);
        return this;
    }

    @Override
    public ModComponent verticalSizing(Sizing verticalSizing) {
        this.owo$getWrapper().verticalSizing(verticalSizing);
        return this;
    }

    @Override
    public AnimatableProperty<Sizing> horizontalSizing() {
        return this.owo$getWrapper().horizontalSizing();
    }

    @Override
    public AnimatableProperty<Sizing> verticalSizing() {
        return this.owo$getWrapper().horizontalSizing();
    }

    @Override
    public EventSource<MouseDown> mouseDown() {
        return this.owo$getWrapper().mouseDown();
    }

    @Override
    public int x() {
        return this.owo$getWrapper().x();
    }

    @Override
    public void setX(int x) {
        this.owo$getWrapper().setX(x);
    }

    @Override
    public int y() {
        return this.owo$getWrapper().y();
    }

    @Override
    public void setY(int y) {
        this.owo$getWrapper().setY(y);
    }

    @Override
    public int width() {
        return this.owo$getWrapper().width();
    }

    @Override
    public int height() {
        return this.owo$getWrapper().height();
    }

    @Override
    public void draw(PoseStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        this.owo$getWrapper().draw(matrices, mouseX, mouseY, partialTicks, delta);
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        this.owo$getWrapper().update(delta, mouseX, mouseY);
        this.cursorStyle(this.active ? this.owo$preferredCursorStyle() : CursorStyle.POINTER);
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        return this.owo$getWrapper().onMouseDown(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseUp(double mouseX, double mouseY, int button) {
        return this.owo$getWrapper().onMouseUp(mouseX, mouseY, button);
    }

    @Override
    public EventSource<MouseUp> mouseUp() {
        return this.owo$getWrapper().mouseUp();
    }

    @Override
    public EventSource<MouseScroll> mouseScroll() {
        return this.owo$getWrapper().mouseScroll();
    }

    @Override
    public EventSource<MouseDrag> mouseDrag() {
        return this.owo$getWrapper().mouseDrag();
    }

    @Override
    public EventSource<KeyPress> keyPress() {
        return this.owo$getWrapper().keyPress();
    }

    @Override
    public EventSource<CharTyped> charTyped() {
        return this.owo$getWrapper().charTyped();
    }

    @Override
    public EventSource<FocusGained> focusGained() {
        return this.owo$getWrapper().focusGained();
    }

    @Override
    public EventSource<FocusLost> focusLost() {
        return this.owo$getWrapper().focusLost();
    }

    @Override
    public EventSource<MouseEnter> mouseEnter() {
        return this.owo$getWrapper().mouseEnter();
    }

    @Override
    public EventSource<MouseLeave> mouseLeave() {
        return this.owo$getWrapper().mouseLeave();
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
        return this.owo$getWrapper().onMouseScroll(mouseX, mouseY, amount);
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
        return this.owo$getWrapper().onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        return this.owo$getWrapper().onKeyPress(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onCharTyped(char chr, int modifiers) {
        return this.owo$getWrapper().onCharTyped(chr, modifiers);
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return true;
    }

    @Override
    public void onFocusGained(FocusSource source) {
        this.setFocused(source == FocusSource.KEYBOARD_CYCLE);
        this.owo$getWrapper().onFocusGained(source);
    }

    @Override
    public void onFocusLost() {
        this.setFocused(false);
        this.owo$getWrapper().onFocusLost();
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

    @Override
    public CursorStyle cursorStyle() {
        return this.owo$getWrapper().cursorStyle();
    }

    @Override
    public ModComponent cursorStyle(CursorStyle style) {
        return this.owo$getWrapper().cursorStyle(style);
    }

    @Override
    public ModComponent tooltip(List<ClientTooltipComponent> tooltip) {
        return this.owo$getWrapper().tooltip(tooltip);
    }

    @Override
    public List<ClientTooltipComponent> tooltip() {
        return this.owo$getWrapper().tooltip();
    }

    @Override
    public ModComponent zIndex(int zIndex) {
        return this.owo$getWrapper().zIndex(zIndex);
    }

    @Override
    public int zIndex() {
        return this.owo$getWrapper().zIndex();
    }

    @Override
    public ModComponent id(@Nullable String id) {
        this.owo$getWrapper().id(id);
        return this;
    }

    @Override
    public @Nullable String id() {
        return this.owo$getWrapper().id();
    }

    @Unique
    protected VanillaWidgetComponent owo$getWrapper() {
        if (this.owo$wrapper == null) {
            this.owo$wrapper = Components.wrapVanillaWidget((AbstractWidget) (Object) this);
        }

        return this.owo$wrapper;
    }

    protected CursorStyle owo$preferredCursorStyle() {
        return CursorStyle.POINTER;
    }
}
