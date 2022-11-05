package com.nyfaria.numismaticoverhaul.owostuff.ui.container;

import com.nyfaria.numismaticoverhaul.owostuff.ui.base.BaseParentComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ModComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ParentComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Size;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Sizing;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIModel;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIModelParsingException;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIParsing;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class WrappingParentComponent<C extends ModComponent> extends BaseParentComponent {

    protected C child;
    protected List<ModComponent> childView;

    protected WrappingParentComponent(Sizing horizontalSizing, Sizing verticalSizing, C child) {
        super(horizontalSizing, verticalSizing);
        this.child = child;
        this.childView = Collections.singletonList(this.child);
    }

    @Override
    protected void applyHorizontalContentSizing(Sizing sizing) {
        this.width = this.child.fullSize().width() + this.padding.get().horizontal();
    }

    @Override
    protected void applyVerticalContentSizing(Sizing sizing) {
        this.height = this.child.fullSize().height() + this.padding.get().vertical();
    }

    @Override
    public void layout(Size space) {
        this.child.inflate(this.calculateChildSpace(space));
        this.child.mount(this, this.childMountX(), this.childMountY());
    }

    /**
     * @return The x-coordinate at which to mount the child
     */
    protected int childMountX() {
        return this.x + child.margins().get().left() + this.padding.get().left();
    }

    /**
     * @return The y-coordinate at which to mount the child
     */
    protected int childMountY() {
        return this.y + child.margins().get().top() + this.padding.get().top();
    }

    public WrappingParentComponent<C> child(C newChild) {
        if (this.child != null) {
            this.child.dismount(DismountReason.REMOVED);
        }

        this.child = newChild;
        this.childView = Collections.singletonList(this.child);

        this.updateLayout();
        return this;
    }

    public C child() {
        return this.child;
    }

    @Override
    public List<ModComponent> children() {
        return this.childView;
    }

    @Override
    public ParentComponent removeChild(ModComponent child) {
        throw new UnsupportedOperationException("Cannot remove the child of a wrapping component");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        try {
            var childList = UIParsing.<Element>allChildrenOfType(element, Node.ELEMENT_NODE);
            this.child((C) model.parseComponent(ModComponent.class, childList.get(0)));
        } catch (UIModelParsingException exception) {
            throw new UIModelParsingException("Could not initialize container child", exception);
        }
    }
}
