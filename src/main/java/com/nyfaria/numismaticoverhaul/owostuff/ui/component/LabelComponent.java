package com.nyfaria.numismaticoverhaul.owostuff.ui.component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nyfaria.numismaticoverhaul.owostuff.ui.base.BaseComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.AnimatableProperty;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Color;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.HorizontalAlignment;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Size;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Sizing;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.VerticalAlignment;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIModel;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIParsing;
import com.nyfaria.numismaticoverhaul.owostuff.ui.util.Drawer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LabelComponent extends BaseComponent {

    protected final Font textRenderer = Minecraft.getInstance().font;

    protected Component text;
    protected List<FormattedCharSequence> wrappedText;

    protected VerticalAlignment verticalTextAlignment = VerticalAlignment.TOP;
    protected HorizontalAlignment horizontalTextAlignment = HorizontalAlignment.LEFT;

    protected final AnimatableProperty<Color> color = AnimatableProperty.of(Color.WHITE);
    protected boolean shadow;
    protected int maxWidth;

    protected LabelComponent(Component text) {
        this.text = text;
        this.wrappedText = new ArrayList<>();

        this.shadow = false;
        this.maxWidth = Integer.MAX_VALUE;
    }

    public LabelComponent text(Component text) {
        this.text = text;
        this.notifyParentIfMounted();
        return this;
    }

    public Component text() {
        return this.text;
    }

    public LabelComponent maxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        this.notifyParentIfMounted();
        return this;
    }

    public int maxWidth() {
        return this.maxWidth;
    }

    public LabelComponent shadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    public boolean shadow() {
        return this.shadow;
    }

    public LabelComponent color(Color color) {
        this.color.set(color);
        return this;
    }

    public AnimatableProperty<Color> color() {
        return this.color;
    }

    public LabelComponent verticalTextAlignment(VerticalAlignment verticalAlignment) {
        this.verticalTextAlignment = verticalAlignment;
        return this;
    }

    public VerticalAlignment verticalTextAlignment() {
        return this.verticalTextAlignment;
    }

    public LabelComponent horizontalTextAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalTextAlignment = horizontalAlignment;
        return this;
    }

    public HorizontalAlignment horizontalTextAlignment() {
        return this.horizontalTextAlignment;
    }

    @Override
    protected void applyHorizontalContentSizing(Sizing sizing) {
        int widestText = 0;
        for (var line : this.wrappedText) {
            int width = this.textRenderer.width(line);
            if (width > widestText) widestText = width;
        }

        if (widestText > this.maxWidth) {
            this.wrapLines();
            this.applyHorizontalContentSizing(sizing);
        } else {
            this.width = widestText + sizing.value * 2;
        }
    }

    @Override
    protected void applyVerticalContentSizing(Sizing sizing) {
        this.wrapLines();
        this.height = (this.wrappedText.size() * (this.textRenderer.lineHeight + 2)) - 2 + sizing.value * 2;
    }

    @Override
    public void inflate(Size space) {
        super.inflate(space);
        this.wrapLines();
    }

    private void wrapLines() {
        this.wrappedText = this.textRenderer.split(this.text, this.horizontalSizing.get().method != Sizing.Method.CONTENT ? this.width : this.maxWidth);
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);
        this.color.update(delta);
    }

    @Override
    public void draw(PoseStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        int x = this.x;
        int y = this.y;

        if (this.horizontalSizing.get().method == Sizing.Method.CONTENT) {
            x += this.horizontalSizing.get().value;
        }
        if (this.verticalSizing.get().method == Sizing.Method.CONTENT) {
            y += this.verticalSizing.get().value;
        }

        switch (this.verticalTextAlignment) {
            case CENTER -> y += (this.height - ((this.wrappedText.size() * (this.textRenderer.lineHeight + 2)) - 2)) / 2;
            case BOTTOM -> y += this.height - ((this.wrappedText.size() * (this.textRenderer.lineHeight + 2)) - 2);
        }

        for (int i = 0; i < this.wrappedText.size(); i++) {
            var renderText = this.wrappedText.get(i);
            int renderX = x;

            switch (this.horizontalTextAlignment) {
                case CENTER -> renderX += (this.width - this.textRenderer.width(renderText)) / 2;
                case RIGHT -> renderX += this.width - this.textRenderer.width(renderText);
            }

            if (this.shadow) {
                this.textRenderer.drawShadow(matrices, renderText, renderX, y + i * 11, this.color.get().argb());
            } else {
                this.textRenderer.draw(matrices, renderText, renderX, y + i * 11, this.color.get().argb());
            }
        }
    }

    @Override
    public void drawTooltip(PoseStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        super.drawTooltip(matrices, mouseX, mouseY, partialTicks, delta);

        if (!this.isInBoundingBox(mouseX, mouseY)) return;
        Drawer.utilityScreen().renderComponentHoverEffect(matrices, this.text.getStyle(), mouseX, mouseY);
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        return Drawer.utilityScreen().handleComponentClicked(this.text.getStyle()) | super.onMouseDown(mouseX, mouseY, button);
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        UIParsing.apply(children, "text", UIParsing::parseText, this::text);
        UIParsing.apply(children, "max-width", UIParsing::parseUnsignedInt, this::maxWidth);
        UIParsing.apply(children, "color", Color::parse, this::color);
        UIParsing.apply(children, "shadow", UIParsing::parseBool, this::shadow);

        UIParsing.apply(children, "vertical-text-alignment", VerticalAlignment::parse, this::verticalTextAlignment);
        UIParsing.apply(children, "horizontal-text-alignment", HorizontalAlignment::parse, this::horizontalTextAlignment);
    }
}
