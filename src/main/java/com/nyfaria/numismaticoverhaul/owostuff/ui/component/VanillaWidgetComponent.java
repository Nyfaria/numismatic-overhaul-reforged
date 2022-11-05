package com.nyfaria.numismaticoverhaul.owostuff.ui.component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nyfaria.numismaticoverhaul.mixin.owomixins.ui.ClickableWidgetAccessor;
import com.nyfaria.numismaticoverhaul.owostuff.ui.base.BaseComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Insets;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ParentComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Sizing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;

public class VanillaWidgetComponent extends BaseComponent {

    private final AbstractWidget widget;

    protected VanillaWidgetComponent(AbstractWidget widget) {
        this.widget = widget;

        this.horizontalSizing.set(Sizing.fixed(this.widget.getWidth()));
        this.verticalSizing.set(Sizing.fixed(this.widget.getHeight()));

        if (widget instanceof EditBox) {
            this.margins(Insets.none());
        }
    }

    @Override
    public void mount(ParentComponent parent, int x, int y) {
        super.mount(parent, x, y);
        this.applyToWidget();
    }

    @Override
    protected void applyVerticalContentSizing(Sizing sizing) {
        if (this.widget instanceof Button || this.widget instanceof Checkbox || this.widget instanceof SliderComponent || this.widget instanceof EditBox) {
            this.height = 20;
        } else {
            super.applyVerticalContentSizing(sizing);
        }

        this.applyToWidget();
    }

    @Override
    protected void applyHorizontalContentSizing(Sizing sizing) {
        if (this.widget instanceof Button button) {
            this.width = Minecraft.getInstance().font.width(button.getMessage()) + 6 + sizing.value * 2;
        } else if (this.widget instanceof Checkbox checkbox) {
            this.width = Minecraft.getInstance().font.width(checkbox.getMessage()) + 24;
        } else {
            super.applyHorizontalContentSizing(sizing);
        }

        this.applyToWidget();
    }

    @Override
    public BaseComponent margins(Insets margins) {
        if (widget instanceof EditBox) {
            return super.margins(margins.add(1, 1, 1, 1));
        } else {
            return super.margins(margins);
        }
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        this.applyToWidget();
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        this.applyToWidget();
    }

    private void applyToWidget() {
        this.widget.x = this.x;
        this.widget.y = this.y;

        this.widget.setWidth(this.width);
        ((ClickableWidgetAccessor) this.widget).owo$setHeight(this.height);
    }

    @Override
    public void draw(PoseStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        this.widget.render(matrices, mouseX, mouseY, 0);
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        return this.widget.mouseClicked(this.x + mouseX, this.y + mouseY, button)
                | super.onMouseDown(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseUp(double mouseX, double mouseY, int button) {
        return this.widget.mouseReleased(this.x + mouseX, this.y + mouseY, button)
                | super.onMouseUp(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
        return this.widget.mouseScrolled(this.x + mouseX, this.y + mouseY, amount)
                | super.onMouseScroll(mouseX, mouseY, amount);
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
        return this.widget.mouseDragged(this.x + mouseX, this.y + mouseY, button, deltaX, deltaY)
                | super.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
    }

    @Override
    public boolean onCharTyped(char chr, int modifiers) {
        return this.widget.charTyped(chr, modifiers)
                | super.onCharTyped(chr, modifiers);
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        return this.widget.keyPressed(keyCode, scanCode, modifiers)
                | super.onKeyPress(keyCode, scanCode, modifiers);
    }
}
