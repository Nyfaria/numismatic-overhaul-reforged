package com.nyfaria.numismaticoverhaul.owostuff.ui.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nyfaria.numismaticoverhaul.mixin.owomixins.ui.SlotAccessor;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.OwoUIAdapter;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ParentComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.PositionedRectangle;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Sizing;
import com.nyfaria.numismaticoverhaul.owostuff.ui.inject.GreedyInputComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.util.Drawer;
import com.nyfaria.numismaticoverhaul.owostuff.ui.util.UIErrorToast;
import com.nyfaria.numismaticoverhaul.owostuff.util.pond.OwoSlotExtension;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.function.BiFunction;

public abstract class BaseOwoHandledScreen<R extends ParentComponent, S extends AbstractContainerMenu> extends AbstractContainerScreen<S> {

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

    protected BaseOwoHandledScreen(S handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
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
        super.init();

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
//                //Owo.LOGGER.warn("Could not initialize owo screen", error);
                UIErrorToast.report(error);
                this.invalid = true;
            }
        }
    }

    /**
     * Disable the slot at the given index. Note
     * that this is hard override and the slot cannot
     * re-enable itself
     *
     * @param index The index of the slot to disable
     */
    protected void disableSlot(int index) {
        ((OwoSlotExtension) this.menu.slots.get(index)).owo$setDisabledOverride(true);
    }

    /**
     * Disable the given slot. Note that
     * this is hard override and the slot cannot
     * re-enable itself
     */
    protected void disableSlot(Slot slot) {
        ((OwoSlotExtension) slot).owo$setDisabledOverride(true);
    }

    /**
     * Enable the slot at the given index. Note
     * that this is an override and cannot enable
     * a slot that is disabled through its own will
     *
     * @param index The index of the slot to enable
     */
    protected void enableSlot(int index) {
        ((OwoSlotExtension) this.menu.slots.get(index)).owo$setDisabledOverride(false);
    }

    /**
     * Enable the given slot. Note that
     * this is an override and cannot enable
     * a slot that is disabled through its own will
     */
    protected void enableSlot(Slot slot) {
        ((OwoSlotExtension) slot).owo$setDisabledOverride(true);
    }

    protected boolean isSlotEnabled(int index) {
        return ((OwoSlotExtension) this.menu.slots.get(index)).owo$getDisabledOverride();
    }

    protected boolean isSlotEnabled(Slot slot) {
        return ((OwoSlotExtension) slot).owo$getDisabledOverride();
    }

    /**
     * Wrap the slot and the given index in this screen's
     * handler into a component, so it can be managed by the UI system
     *
     * @param index The index the slot occupies in the handler's slot list
     * @return The wrapped slot
     */
    protected SlotComponent slotAsComponent(int index) {
        return new SlotComponent(index);
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        if (!this.invalid) {
            super.render(matrices, mouseX, mouseY, delta);

            if (this.uiAdapter.enableInspector) {
                matrices.translate(0, 0, 500);

                for (int i = 0; i < this.menu.slots.size(); i++) {
                    var slot = this.menu.slots.get(i);
                    if (!slot.isActive()) continue;

                    Drawer.drawText(matrices, Component.literal(String.valueOf(i)),
                            this.leftPos + slot.x + 15, this.topPos + slot.y + 9, .5f, 0x0096FF,
                            Drawer.TextAnchor.BOTTOM_RIGHT
                    );
                    Drawer.drawText(matrices, Component.literal("(" + slot.getContainerSlot() + ")"),
                            this.leftPos + slot.x + 15, this.topPos + slot.y + 15, .5f, 0x5800FF,
                            Drawer.TextAnchor.BOTTOM_RIGHT
                    );
                }

                matrices.translate(0, 0, -500);
            }

            this.renderTooltip(matrices, mouseX, mouseY);
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

        return this.uiAdapter.rootComponent.focusHandler().focused() instanceof GreedyInputComponent inputComponent
                ? inputComponent.onKeyPress(keyCode, scanCode, modifiers)
                : super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.uiAdapter.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
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
        super.removed();
    }

    @Override
    protected void renderBg(PoseStack matrices, float delta, int mouseX, int mouseY) {}

    public class SlotComponent extends BaseComponent {

        protected final Slot slot;
        protected boolean didDraw = false;

        protected SlotComponent(int index) {
            this.slot = BaseOwoHandledScreen.this.menu.getSlot(index);
        }

        @Override
        public void draw(PoseStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
            this.didDraw = true;

            int[] scissor = new int[4];
            GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, scissor);

            ((OwoSlotExtension) this.slot).owo$setScissorArea(PositionedRectangle.of(
                    scissor[0], scissor[1], scissor[2], scissor[3]
            ));
        }

        @Override
        public void update(float delta, int mouseX, int mouseY) {
            super.update(delta, mouseX, mouseY);

            ((OwoSlotExtension) this.slot).owo$setDisabledOverride(!this.didDraw);

            this.didDraw = false;
        }

        @Override
        public void drawTooltip(PoseStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
            if (!this.slot.hasItem()) {
                super.drawTooltip(matrices, mouseX, mouseY, partialTicks, delta);
            }
        }

        @Override
        public boolean shouldDrawTooltip(double mouseX, double mouseY) {
            return super.shouldDrawTooltip(mouseX, mouseY);
        }

        @Override
        protected void applyHorizontalContentSizing(Sizing sizing) {
            this.width = 16;
        }

        @Override
        protected void applyVerticalContentSizing(Sizing sizing) {
            this.height = 16;
        }

        @Override
        public void setX(int x) {
            super.setX(x);
            ((SlotAccessor) this.slot).owo$setX(x - BaseOwoHandledScreen.this.leftPos);
        }

        @Override
        public void setY(int y) {
            super.setY(y);
            ((SlotAccessor) this.slot).owo$setY(y - BaseOwoHandledScreen.this.topPos);
        }
    }
}
