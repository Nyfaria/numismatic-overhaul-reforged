package com.nyfaria.numismaticoverhaul.owostuff.ui.container;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nyfaria.numismaticoverhaul.owostuff.ui.base.BaseParentComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ModComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Size;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Sizing;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIModel;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIParsing;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class FlowLayout extends BaseParentComponent {

    protected final List<ModComponent> children = new ArrayList<>();
    protected final List<ModComponent> childrenView = Collections.unmodifiableList(this.children);
    protected Size contentSize = Size.zero();

    protected FlowLayout(Sizing horizontalSizing, Sizing verticalSizing) {
        super(horizontalSizing, verticalSizing);
    }

    @Override
    protected void applyHorizontalContentSizing(Sizing sizing) {
        this.width = this.contentSize.width() + this.padding.get().horizontal() + sizing.value * 2;
    }

    @Override
    protected void applyVerticalContentSizing(Sizing sizing) {
        this.height = this.contentSize.height() + this.padding.get().vertical() + sizing.value * 2;
    }

    public FlowLayout child(ModComponent child) {
        this.children.add(child);
        this.updateLayout();
        return this;
    }

    public FlowLayout children(Collection<ModComponent> children) {
        this.children.addAll(children);
        this.updateLayout();
        return this;
    }

    public FlowLayout child(int index, ModComponent child) {
        this.children.add(index, child);
        this.updateLayout();
        return this;
    }

    public FlowLayout children(int index, Collection<ModComponent> children) {
        this.children.addAll(index, children);
        this.updateLayout();
        return this;
    }

    @Override
    public FlowLayout removeChild(ModComponent child) {
        if (this.children.remove(child)) {
            child.dismount(DismountReason.REMOVED);
            this.updateLayout();
        }

        return this;
    }

    public FlowLayout clearChildren() {
        for (var child : this.children) {
            child.dismount(DismountReason.REMOVED);
        }

        this.children.clear();
        this.updateLayout();

        return this;
    }

    @Override
    public List<ModComponent> children() {
        return this.childrenView;
    }

    @Override
    public void draw(PoseStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        super.draw(matrices, mouseX, mouseY, partialTicks, delta);
        this.drawChildren(matrices, mouseX, mouseY, partialTicks, delta, this.children);
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        final var components = UIParsing
                .get(children, "children", e -> UIParsing.<Element>allChildrenOfType(e, Node.ELEMENT_NODE))
                .orElse(Collections.emptyList());

        for (var child : components) {
            this.child(model.parseComponent(ModComponent.class, child));
        }
    }

    public static FlowLayout parse(Element element) {
        UIParsing.expectAttributes(element, "direction");

        return element.getAttribute("direction").equals("vertical")
                ? Containers.verticalFlow(Sizing.content(), Sizing.content())
                : Containers.horizontalFlow(Sizing.content(), Sizing.content());
    }
}
