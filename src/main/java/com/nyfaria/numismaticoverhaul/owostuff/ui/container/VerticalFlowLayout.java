package com.nyfaria.numismaticoverhaul.owostuff.ui.container;

import com.nyfaria.numismaticoverhaul.owostuff.ui.core.HorizontalAlignment;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ModComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Size;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Sizing;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.VerticalAlignment;
import com.nyfaria.numismaticoverhaul.owostuff.ui.util.MountingHelper;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;

public class VerticalFlowLayout extends FlowLayout {

    protected VerticalFlowLayout(Sizing horizontalSizing, Sizing verticalSizing) {
        super(horizontalSizing, verticalSizing);
    }

    @Override
    public void layout(Size space) {
        var layoutHeight = new MutableInt(0);
        var layoutWidth = new MutableInt(0);

        final var layout = new ArrayList<ModComponent>();
        final var padding = this.padding.get();
        final var childSpace = this.calculateChildSpace(space);

        var mountState = MountingHelper.mountEarly(this::mountChild, this.children, childSpace, child -> {
            layout.add(child);

            child.inflate(childSpace);
            child.mount(this,
                    this.x + padding.left() + child.margins().get().left(),
                    this.y + padding.top() + child.margins().get().top() + layoutHeight.intValue());

            final var childSize = child.fullSize();
            layoutHeight.add(childSize.height());
            if (childSize.width() > layoutWidth.intValue()) {
                layoutWidth.setValue(childSize.width());
            }
        });

        this.contentSize = Size.of(layoutWidth.intValue(), layoutHeight.intValue());
        if (this.horizontalSizing.get().method == Sizing.Method.CONTENT) {
            this.applyHorizontalContentSizing(this.horizontalSizing.get());
        }

        if (this.verticalSizing.get().method == Sizing.Method.CONTENT) {
            this.applyVerticalContentSizing(this.verticalSizing.get());
        }

        if (this.horizontalAlignment() != HorizontalAlignment.LEFT) {
            for (var component : layout) {
                component.setX(component.x() + this.horizontalAlignment().align(component.fullSize().width(), this.width - padding.horizontal()));
            }
        }

        if (this.verticalAlignment() != VerticalAlignment.TOP) {
            for (var component : layout) {
                if (this.verticalAlignment() == VerticalAlignment.CENTER) {
                    component.setY(component.y() + (this.height - padding.vertical() - layoutHeight.intValue()) / 2);
                } else {
                    component.setY(component.y() + (this.height - padding.vertical() - layoutHeight.intValue()));
                }
            }
        }

        mountState.mountLate();
    }
}
