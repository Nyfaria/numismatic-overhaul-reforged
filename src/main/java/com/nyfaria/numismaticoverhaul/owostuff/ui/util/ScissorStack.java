package com.nyfaria.numismaticoverhaul.owostuff.ui.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector4f;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ModComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.PositionedRectangle;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.ArrayDeque;
import java.util.Deque;

public class ScissorStack {

    private static final PoseStack EMPTY_STACK = new PoseStack();
    private static final Deque<PositionedRectangle> STACK = new ArrayDeque<>();

    public static void pushDirect(int x, int y, int width, int height) {
        var window = Minecraft.getInstance().getWindow();
        var scale = window.getGuiScale();

        push(
                (int) (x / scale),
                (int) (window.getGuiScaledHeight() - (y / scale) - height / scale),
                (int) (width / scale),
                (int) (height / scale),
                null
        );
    }

    public static void push(int x, int y, int width, int height, @Nullable PoseStack matrices) {
        final var newFrame = withGlTransform(x, y, width, height, matrices);

        if (STACK.isEmpty()) {
            STACK.push(newFrame);
        } else {
            var top = STACK.peek();
            STACK.push(top.intersection(newFrame));
        }

        applyState();
    }

    public static void pop() {
        if (STACK.isEmpty()) {
            throw new IllegalStateException("Cannot pop frame from empty scissor stack");
        }

        STACK.pop();

        if (STACK.isEmpty()) {
            var window = Minecraft.getInstance().getWindow();
            GL11.glScissor(0, 0, window.getWidth(), window.getHeight());
        } else {
            applyState();
        }
    }

    private static void applyState() {
        if (!GL11.glIsEnabled(GL11.GL_SCISSOR_TEST)) return;

        var newFrame = STACK.peek();
        var window = Minecraft.getInstance().getWindow();
        var scale = window.getGuiScale();

        GL11.glScissor(
                (int) (newFrame.x() * scale),
                (int) (window.getHeight() - (newFrame.y() * scale) - newFrame.height() * scale),
                (int) (newFrame.width() * scale),
                (int) (newFrame.height() * scale)
        );
    }

    public static void drawUnclipped(Runnable action) {
        boolean scissorEnabled = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);

        if (scissorEnabled) GlStateManager._disableScissorTest();
        action.run();
        if (scissorEnabled) GlStateManager._enableScissorTest();
    }

    public static boolean isVisible(int x, int y, @Nullable PoseStack matrices) {
        var top = STACK.peek();
        if (top == null) return true;

        return top.intersects(
                withGlTransform(
                        x, y, 0, 0, matrices
                )
        );
    }

    public static boolean isVisible(ModComponent component, @Nullable PoseStack matrices) {
        var top = STACK.peek();
        if (top == null) return true;

        var margins = component.margins().get();
        return top.intersects(
                withGlTransform(
                        component.x() - margins.left(),
                        component.y() - margins.top(),
                        component.width() + margins.right(),
                        component.height() + margins.bottom(),
                        matrices
                )
        );
    }

    private static PositionedRectangle withGlTransform(int x, int y, int width, int height, @Nullable PoseStack matrices) {
        if (matrices == null) matrices = EMPTY_STACK;

        matrices.pushPose();
        matrices.mulPoseMatrix(RenderSystem.getModelViewMatrix());

        var root = new Vector4f(x, y, 0, 1);
        var end = new Vector4f(x + width, y + height, 0, 1);

        root.transform(matrices.last().pose());
        end.transform(matrices.last().pose());

        x = (int) root.x();
        y = (int) root.y();

        width = (int) Math.ceil(end.x() - root.x());
        height = (int) Math.ceil(end.y() - root.y());

        matrices.popPose();

        return PositionedRectangle.of(x, y, width, height);
    }
}
