package com.nyfaria.numismaticoverhaul.owostuff.ui.util;

import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ModComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Positioning;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Size;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MountingHelper {

    protected final ComponentSink sink;
    protected final List<ModComponent> lateChildren;
    protected final Size childSpace;

    protected MountingHelper(ComponentSink sink, List<ModComponent> children, Size childSpace) {
        this.sink = sink;
        this.lateChildren = children;
        this.childSpace = childSpace;
    }

    public static MountingHelper mountEarly(ComponentSink sink, List<ModComponent> children, Size childSpace, Consumer<ModComponent> layoutFunc) {
        var lateChildren = new ArrayList<ModComponent>();

        for (var child : children) {
            if (child.positioning().get().type != Positioning.Type.RELATIVE) {
                sink.accept(child, childSpace, layoutFunc);
            } else {
                lateChildren.add(child);
            }
        }

        return new MountingHelper(sink, lateChildren, childSpace);
    }

    public void mountLate() {
        for (var child : lateChildren) {
            this.sink.accept(child, this.childSpace, component -> {throw new IllegalStateException("A layout-positioned child was mounted late");});
        }
        this.lateChildren.clear();
    }

    public interface ComponentSink {
        void accept(@Nullable ModComponent child, Size space, Consumer<ModComponent> layoutFunc);
    }

}
