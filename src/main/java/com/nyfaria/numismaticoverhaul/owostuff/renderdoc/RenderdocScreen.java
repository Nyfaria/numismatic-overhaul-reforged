package com.nyfaria.numismaticoverhaul.owostuff.renderdoc;

import com.nyfaria.numismaticoverhaul.owostuff.ops.TextOps;
import com.nyfaria.numismaticoverhaul.owostuff.ui.base.BaseOwoScreen;
import com.nyfaria.numismaticoverhaul.owostuff.ui.component.ButtonComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.component.CheckboxComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.component.Components;
import com.nyfaria.numismaticoverhaul.owostuff.ui.component.LabelComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.container.Containers;
import com.nyfaria.numismaticoverhaul.owostuff.ui.container.FlowLayout;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Color;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.HorizontalAlignment;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Insets;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.OwoUIAdapter;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Sizing;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Surface;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.VerticalAlignment;
import com.nyfaria.numismaticoverhaul.owostuff.ui.util.CommandOpenedScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class RenderdocScreen extends BaseOwoScreen<FlowLayout> implements CommandOpenedScreen {

    private int ticks = 0;
    private boolean setCaptureKey = false;
    private @Nullable RenderDoc.Key scheduledKey = null;

    private ButtonComponent captureKeyButton = null;
    private LabelComponent captureLabel = null;

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.surface(Surface.VANILLA_TRANSLUCENT);

        var overlayState = RenderDoc.getOverlayOptions();
        rootComponent.child(
                        Containers.verticalFlow(Sizing.content(), Sizing.content())
                                .child(Components.label(Component.literal("RenderDoc Controls")).shadow(true).margins(Insets.top(5).withBottom(10)))
                                .child(
                                        Containers.grid(Sizing.content(), Sizing.content(), 2, 2)
                                                .child(overlayControl(Component.nullToEmpty("Enabled"), overlayState, RenderDoc.OverlayOption.ENABLED), 0, 0)
                                                .child(overlayControl(Component.nullToEmpty("Capture List"), overlayState, RenderDoc.OverlayOption.CAPTURE_LIST), 0, 1)
                                                .child(overlayControl(Component.nullToEmpty("Frame Rate"), overlayState, RenderDoc.OverlayOption.FRAME_RATE), 1, 0)
                                                .child(overlayControl(Component.nullToEmpty("Frame Number"), overlayState, RenderDoc.OverlayOption.FRAME_NUMBER), 1, 1)
                                )
                                .child(
                                        Components.box(Sizing.fixed(175), Sizing.fixed(1))
                                                .color(Color.ofFormatting(ChatFormatting.DARK_GRAY))
                                                .fill(true)
                                                .margins(Insets.vertical(5))
                                )
                                .child(
                                        Containers.grid(Sizing.content(), Sizing.content(), 2, 2)
                                                .child(Components.button(
                                                        Component.nullToEmpty("Launch UI"),
                                                        (ButtonComponent button) -> RenderDoc.launchReplayUI(true)
                                                ).horizontalSizing(Sizing.fixed(90)).margins(Insets.of(2)), 0, 0)
                                                .child((this.captureKeyButton = Components.button(
                                                        Component.nullToEmpty("Capture Hotkey"),
                                                        (ButtonComponent button) -> {
                                                            button.active = false;
                                                            button.setMessage(Component.nullToEmpty("Press..."));

                                                            this.setCaptureKey = true;
                                                        }
                                                )).horizontalSizing(Sizing.fixed(90)).margins(Insets.of(2)), 1, 0)
                                                .child(Components.button(
                                                        Component.nullToEmpty("Capture Frame"),
                                                        (ButtonComponent button) -> RenderDoc.triggerCapture()
                                                ).horizontalSizing(Sizing.fixed(90)).margins(Insets.of(2)), 0, 1)
                                                .child(this.captureLabel = Components.label(
                                                        this.createCapturesText()
                                                ), 1, 1)
                                                .verticalAlignment(VerticalAlignment.CENTER).horizontalAlignment(HorizontalAlignment.CENTER)
                                )
                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                .padding(Insets.of(5))
                                .surface(Surface.flat(0x77000000).and(Surface.outline(0x77000000)))
                )
                .verticalAlignment(VerticalAlignment.CENTER)
                .horizontalAlignment(HorizontalAlignment.CENTER);
    }

    @Override
    public void tick() {
        super.tick();
        if (++this.ticks % 10 != 0) return;

        if (this.scheduledKey != null) {
            RenderDoc.setCaptureKeys(this.scheduledKey);
            this.scheduledKey = null;
        }

        this.captureLabel.text(this.createCapturesText());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.setCaptureKey) {
            this.captureKeyButton.active = true;
            this.captureKeyButton.setMessage(Component.nullToEmpty("Capture Hotkey"));

            this.setCaptureKey = false;

            var key = RenderDoc.Key.fromGLFW(keyCode);
            if (key != null) {
                this.ticks = 0;
                this.scheduledKey = key;

                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private Component createCapturesText() {
        return TextOps.withColor("Captures: §" + RenderDoc.getNumCaptures(), TextOps.color(ChatFormatting.WHITE), 0x00D7FF);
    }

    private static CheckboxComponent overlayControl(Component name, EnumSet<RenderDoc.OverlayOption> state, RenderDoc.OverlayOption option) {
        var checkbox = Components.checkbox(name);
        checkbox.margins(Insets.of(3)).horizontalSizing(Sizing.fixed(100));
        checkbox.checked(state.contains(option));
        checkbox.onChanged(enabled -> {
            if (enabled) {
                RenderDoc.enableOverlayOptions(option);
            } else {
                RenderDoc.disableOverlayOptions(option);
            }
        });
        return checkbox;
    }
}
