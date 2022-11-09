package com.nyfaria.numismaticoverhaul.owostuff.ui.container;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.nyfaria.numismaticoverhaul.owostuff.ui.component.Components;
import com.nyfaria.numismaticoverhaul.owostuff.ui.component.LabelComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.CursorStyle;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Insets;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ModComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Sizing;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Surface;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIParsing;
import com.nyfaria.numismaticoverhaul.owostuff.ui.util.Drawer;
import com.nyfaria.numismaticoverhaul.owostuff.ui.util.UISounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import org.lwjgl.glfw.GLFW;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollapsibleContainer extends VerticalFlowLayout {

    public static final Surface SURFACE = (matrices, component) -> Drawer.fill(matrices,
            component.x() + 5,
            component.y(),
            component.x() + 6,
            component.y() + component.height(),
            0x77FFFFFF
    );

    protected List<ModComponent> collapsibleChildren = new ArrayList<>();
    protected boolean expanded;

    protected final SpinnyBoiComponent spinnyBoi;
    protected final FlowLayout titleLayout;

    protected CollapsibleContainer(Sizing horizontalSizing, Sizing verticalSizing, net.minecraft.network.chat.Component title, boolean expanded) {
        super(horizontalSizing, verticalSizing);
        this.surface(SURFACE);
        this.padding(Insets.left(15));

        this.titleLayout = Containers.horizontalFlow(Sizing.content(), Sizing.content());
        this.titleLayout.padding(Insets.vertical(5));
        this.titleLayout.margins(Insets.left(-7));
        this.allowOverflow(true);

        this.spinnyBoi = new SpinnyBoiComponent();
        this.titleLayout.child(spinnyBoi);

        title = title.copy().withStyle(ChatFormatting.UNDERLINE);
        this.titleLayout.child(Components.label(title).cursorStyle(CursorStyle.HAND));

        this.expanded = expanded;
        this.spinnyBoi.targetRotation = expanded ? 90 : 0;
        this.spinnyBoi.rotation = this.spinnyBoi.targetRotation;

        super.child(this.titleLayout);
    }

    protected void toggleExpansion() {
        if (expanded) {
            this.children.removeAll(collapsibleChildren);
            this.spinnyBoi.targetRotation = 0;
        } else {
            this.children.addAll(this.collapsibleChildren);
            this.spinnyBoi.targetRotation = 90;
        }
        this.updateLayout();

        this.expanded = !this.expanded;
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return source == FocusSource.KEYBOARD_CYCLE;
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            this.toggleExpansion();

            super.onKeyPress(keyCode, scanCode, modifiers);
            return true;
        }

        return super.onKeyPress(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        final var superResult = super.onMouseDown(mouseX, mouseY, button);

        if (mouseY <= this.titleLayout.fullSize().height() && !superResult) {
            this.toggleExpansion();
            UISounds.playInteractionSound();
            return true;
        } else {
            return superResult;
        }
    }

    @Override
    public FlowLayout child(ModComponent child) {
        this.collapsibleChildren.add(child);
        if (this.expanded) super.child(child);
        return this;
    }

    @Override
    public FlowLayout children(Collection<ModComponent> children) {
        this.collapsibleChildren.addAll(children);
        if (this.expanded) super.children(children);
        return this;
    }

    @Override
    public FlowLayout child(int index, ModComponent child) {
        this.collapsibleChildren.add(index, child);
        if (this.expanded) super.child(index + this.children.size() - this.collapsibleChildren.size(), child);
        return this;
    }

    @Override
    public FlowLayout children(int index, Collection<ModComponent> children) {
        this.collapsibleChildren.addAll(index, children);
        if (this.expanded) super.children(index + this.children.size() - this.collapsibleChildren.size(), children);
        return this;
    }

    @Override
    public FlowLayout removeChild(ModComponent child) {
        this.collapsibleChildren.remove(child);
        return super.removeChild(child);
    }

    public static CollapsibleContainer parse(Element element) {
        var textElement = UIParsing.childElements(element).get("text");
        var title = textElement == null ? new TextComponent("") : UIParsing.parseText(textElement);

        return element.getAttribute("expanded").equals("true")
                ? Containers.collapsible(Sizing.content(), Sizing.content(), title, true)
                : Containers.collapsible(Sizing.content(), Sizing.content(), title, false);
    }

    protected static class SpinnyBoiComponent extends LabelComponent {

        protected float rotation = 90;
        protected float targetRotation = 90;

        public SpinnyBoiComponent() {
            super(new TextComponent(">"));
            this.margins(Insets.horizontal(4));
        }

        @Override
        public void update(float delta, int mouseX, int mouseY) {
            super.update(delta, mouseX, mouseY);
            this.rotation += (this.targetRotation - this.rotation) * delta * .65;
        }

        @Override
        public void draw(PoseStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
            matrices.pushPose();
            matrices.translate(this.x + this.width / 2f - 1, this.y + this.height / 2f - 1, 0);
            matrices.mulPose(Vector3f.ZP.rotationDegrees(this.rotation));
            matrices.translate(-(this.x + this.width / 2f - 1), -(this.y + this.height / 2f - 1), 0);

            super.draw(matrices, mouseX, mouseY, partialTicks, delta);
            matrices.popPose();
        }
    }
}
