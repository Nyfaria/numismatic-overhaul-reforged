package com.nyfaria.numismaticoverhaul.owostuff.ui.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.OwoUIAdapter;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ParentComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.util.UIErrorToast;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.BiFunction;

/**
 * A minimal implementation of a Screen which fully
 * supports all aspects of the UI system. Implementing this class
 * is trivial, as you only need to provide implementations for
 * {@link #createAdapter()} to initialize the UI system and {@link #build(ParentComponent)}
 * which is where you declare your component hierarchy.
 * <p>
 * Should you be locked into a different superclass on your screen already,
 * you can easily copy all code from this class into your screen - as you
 * can see supporting the entire feature-set of owo-ui only requires
 * very few changes to how a vanilla screen works
 *
 * @param <R> The type of root component this screen uses
 */
public abstract class BaseOwoScreen<R extends ParentComponent> extends Screen {

    /**
     * The UI adapter of this screen. This handles
     * all user input as well as setting up GL state for rendering
     * and managing component focus
     */
    protected OwoUIAdapter<R> uiAdapter = null;

    /**
     * Whether this screen has encountered an unrecoverable
     * error during its lifecycle and should thus close
     * itself on the next frame
     */
    protected boolean invalid = false;

    protected BaseOwoScreen(Component title) {
        super(title);
    }

    protected BaseOwoScreen() {
        this(Component.empty());
    }

    /**
     * Initialize the UI adapter for this screen. Usually
     * the body of this method will simply consist of a call
     * to {@link OwoUIAdapter#create(Screen, BiFunction)}
     *
     * @return The UI adapter for this screen to use
     */
    protected abstract @NotNull OwoUIAdapter<R> createAdapter();

    /**
     * Build the component hierarchy of this screen,
     * called after the adapter and root component have been
     * initialized by {@link #createAdapter()}
     *
     * @param rootComponent The root component created
     *                      in the previous initialization step
     */
    protected abstract void build(R rootComponent);

    @Override
    protected void init() {
        if (this.invalid) return;

        // Check whether this screen was already initialized
        if (this.uiAdapter != null) {
            // If it was, only resize the adapter instead of recreating it - this preserves UI state
            this.uiAdapter.moveAndResize(0, 0, this.width, this.height);
            // Re-add it as a child to circumvent vanilla clearing them
            this.addRenderableWidget(this.uiAdapter);
        } else {
            try {
                this.uiAdapter = this.createAdapter();
                this.build(this.uiAdapter.rootComponent);

                this.uiAdapter.inflateAndMount();
                this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
            } catch (Exception error) {
                //Owo.LOGGER.warn("Could not initialize owo screen", error);
                UIErrorToast.report(error);
                this.invalid = true;
            }
        }
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        if (!this.invalid) {
            super.render(matrices, mouseX, mouseY, delta);
        } else {
            this.onClose();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        }

        return this.uiAdapter.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.uiAdapter.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return this.uiAdapter;
    }

    @Override
    public void removed() {
        if (this.uiAdapter != null) this.uiAdapter.dispose();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }
}
