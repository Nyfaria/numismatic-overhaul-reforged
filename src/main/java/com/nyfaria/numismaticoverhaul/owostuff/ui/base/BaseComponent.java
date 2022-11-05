package com.nyfaria.numismaticoverhaul.owostuff.ui.base;

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
import com.nyfaria.numismaticoverhaul.owostuff.ui.event.UIEvents;
import com.nyfaria.numismaticoverhaul.owostuff.ui.util.FocusHandler;
import com.nyfaria.numismaticoverhaul.owostuff.util.EventSource;
import com.nyfaria.numismaticoverhaul.owostuff.util.EventStream;
import com.nyfaria.numismaticoverhaul.owostuff.util.Observable;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The reference implementation of the {@link ModComponent} interface,
 * ideally you should extend this when making your own components
 */
public abstract class BaseComponent implements ModComponent {

    @Nullable protected ParentComponent parent = null;
    @Nullable protected String id = null;
    protected int zIndex = 0;

    protected boolean mounted = false;

    protected AnimatableProperty<Insets> margins = AnimatableProperty.of(Insets.none());

    protected AnimatableProperty<Positioning> positioning = AnimatableProperty.of(Positioning.layout());
    protected AnimatableProperty<Sizing> horizontalSizing = AnimatableProperty.of(Sizing.content());
    protected AnimatableProperty<Sizing> verticalSizing = AnimatableProperty.of(Sizing.content());

    protected final EventStream<MouseDown> mouseDownEvents = UIEvents.newMouseDownStream();
    protected final EventStream<MouseUp> mouseUpEvents = UIEvents.newMouseUpStream();
    protected final EventStream<MouseScroll> mouseScrollEvents = UIEvents.newMouseScrollStream();
    protected final EventStream<MouseDrag> mouseDragEvents = UIEvents.newMouseDragStream();
    protected final EventStream<KeyPress> keyPressEvents = UIEvents.newKeyPressStream();
    protected final EventStream<CharTyped> charTypedEvents = UIEvents.newCharTypedStream();
    protected final EventStream<FocusGained> focusGainedEvents = UIEvents.newFocusGainedStream();
    protected final EventStream<FocusLost> focusLostEvents = UIEvents.newFocusLostStream();

    protected final EventStream<MouseEnter> mouseEnterEvents = UIEvents.newMouseEnterStream();
    protected final EventStream<MouseLeave> mouseLeaveEvents = UIEvents.newMouseLeaveStream();
    protected boolean hovered = false;

    protected CursorStyle cursorStyle = CursorStyle.POINTER;
    protected List<ClientTooltipComponent> tooltip = List.of();

    protected int x, y;
    protected int width, height;

    protected Size space = Size.zero();

    protected BaseComponent() {
        Observable.observeAll(this::notifyParentIfMounted, margins, positioning, horizontalSizing, verticalSizing);
    }

    /**
     * Set the horizontal size of this component, based on its content
     */
    protected void applyHorizontalContentSizing(Sizing sizing) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not support horizontal Sizing.content()");
    }

    /**
     * Set the vertical size of this component, based on its content
     */
    protected void applyVerticalContentSizing(Sizing sizing) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not support vertical Sizing.content()");
    }

    @Override
    public void inflate(Size space) {
        this.space = space;

        final var horizontalSizing = this.horizontalSizing.get();
        final var verticalSizing = this.verticalSizing.get();

        final var margins = this.margins.get();

        if (horizontalSizing.method == Sizing.Method.CONTENT) {
            verticalSizing.inflate(space.height() - margins.vertical(), height -> this.height = height, this::applyVerticalContentSizing);
            horizontalSizing.inflate(space.width() - margins.horizontal(), width -> this.width = width, this::applyHorizontalContentSizing);
        } else {
            horizontalSizing.inflate(space.width() - margins.horizontal(), width -> this.width = width, this::applyHorizontalContentSizing);
            verticalSizing.inflate(space.height() - margins.vertical(), height -> this.height = height, this::applyVerticalContentSizing);
        }
    }

    protected void notifyParentIfMounted() {
        if (!this.hasParent()) return;
        this.parent.onChildMutated(this);
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        ModComponent.super.update(delta, mouseX, mouseY);

        if (this.hovered != this.isInBoundingBox(mouseX, mouseY)) {
            if (this.hovered) {
                this.mouseLeaveEvents.sink().onMouseLeave();
            } else {
                this.mouseEnterEvents.sink().onMouseEnter();
            }
            this.hovered = this.isInBoundingBox(mouseX, mouseY);
        }
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        return this.mouseDownEvents.sink().onMouseDown(mouseX, mouseY, button);
    }

    @Override
    public EventSource<MouseDown> mouseDown() {
        return this.mouseDownEvents.source();
    }

    @Override
    public boolean onMouseUp(double mouseX, double mouseY, int button) {
        return this.mouseUpEvents.sink().onMouseUp(mouseX, mouseY, button);
    }

    @Override
    public EventSource<MouseUp> mouseUp() {
        return this.mouseUpEvents.source();
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
        return this.mouseScrollEvents.sink().onMouseScroll(mouseX, mouseY, amount);
    }

    @Override
    public EventSource<MouseScroll> mouseScroll() {
        return this.mouseScrollEvents.source();
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
        return this.mouseDragEvents.sink().onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
    }

    @Override
    public EventSource<MouseDrag> mouseDrag() {
        return this.mouseDragEvents.source();
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        return this.keyPressEvents.sink().onKeyPress(keyCode, scanCode, modifiers);
    }

    @Override
    public EventSource<KeyPress> keyPress() {
        return this.keyPressEvents.source();
    }

    @Override
    public boolean onCharTyped(char chr, int modifiers) {
        return this.charTypedEvents.sink().onCharTyped(chr, modifiers);
    }

    @Override
    public EventSource<CharTyped> charTyped() {
        return this.charTypedEvents.source();
    }

    @Override
    public void onFocusGained(FocusSource source) {
        this.focusGainedEvents.sink().onFocusGained(source);
    }

    @Override
    public EventSource<FocusGained> focusGained() {
        return this.focusGainedEvents.source();
    }

    @Override
    public void onFocusLost() {
        this.focusLostEvents.sink().onFocusLost();
    }

    @Override
    public EventSource<FocusLost> focusLost() {
        return this.focusLostEvents.source();
    }

    @Override
    public EventSource<MouseEnter> mouseEnter() {
        return this.mouseEnterEvents.source();
    }

    @Override
    public EventSource<MouseLeave> mouseLeave() {
        return this.mouseLeaveEvents.source();
    }

    @Override
    public CursorStyle cursorStyle() {
        return this.cursorStyle;
    }

    @Override
    public BaseComponent cursorStyle(CursorStyle style) {
        this.cursorStyle = style;
        return this;
    }

    @Override
    public ModComponent tooltip(List<ClientTooltipComponent> tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    @Override
    public List<ClientTooltipComponent> tooltip() {
        return this.tooltip;
    }

    @Override
    public void mount(ParentComponent parent, int x, int y) {
        this.parent = parent;
        this.mounted = true;
        this.moveTo(x, y);
    }

    @Override
    public void dismount(DismountReason reason) {
        this.parent = null;
        this.mounted = false;
    }

    @Override
    public ParentComponent parent() {
        return this.parent;
    }

    @Override
    public @Nullable FocusHandler focusHandler() {
        return this.hasParent() ? this.parent.focusHandler() : null;
    }

    @Override
    public BaseComponent positioning(Positioning positioning) {
        this.positioning.set(positioning);
        return this;
    }

    @Override
    public AnimatableProperty<Positioning> positioning() {
        return this.positioning;
    }

    @Override
    public BaseComponent margins(Insets margins) {
        this.margins.set(margins);
        return this;
    }

    @Override
    public AnimatableProperty<Insets> margins() {
        return this.margins;
    }

    @Override
    public ModComponent horizontalSizing(Sizing horizontalSizing) {
        this.horizontalSizing.set(horizontalSizing);
        return this;
    }

    @Override
    public AnimatableProperty<Sizing> horizontalSizing() {
        return this.horizontalSizing;
    }

    @Override
    public ModComponent verticalSizing(Sizing verticalSizing) {
        this.verticalSizing.set(verticalSizing);
        return this;
    }

    @Override
    public AnimatableProperty<Sizing> verticalSizing() {
        return this.verticalSizing;
    }

    @Override
    public ModComponent id(@Nullable String id) {
        this.id = id;
        return this;
    }

    @Override
    public @Nullable String id() {
        return this.id;
    }

    @Override
    public ModComponent zIndex(int zIndex) {
        this.zIndex = zIndex;
        return this;
    }

    @Override
    public int zIndex() {
        return this.zIndex;
    }

    @Override
    public int x() {
        return this.x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int y() {
        return this.y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }
}
