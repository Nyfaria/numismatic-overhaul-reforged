package com.nyfaria.numismaticoverhaul.owostuff.ui.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.nyfaria.numismaticoverhaul.mixin.owomixins.ui.ScreenInvoker;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Insets;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ModComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ParentComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * An extension of vanilla's {@link GuiComponent} with all methods
 * statically accessible as well as extra convenience methods
 */
public class Drawer extends GuiComponent {

    private static boolean recording = false;

    private static final Drawer INSTANCE = new Drawer();
    private final DebugDrawer debug = new DebugDrawer();

    public static final ResourceLocation PANEL_TEXTURE = new ResourceLocation("owo", "textures/gui/panel.png");
    public static final ResourceLocation DARK_PANEL_TEXTURE = new ResourceLocation("owo", "textures/gui/dark_panel.png");

    private Drawer() {}

    /**
     * Draw the outline of a rectangle
     *
     * @param matrices The transformation matrix stack
     * @param x        The x-coordinate of top-left corner of the rectangle
     * @param y        The y-coordinate of top-left corner of the rectangle
     * @param width    The width of the rectangle
     * @param height   The height of the rectangle
     * @param color    The color of the rectangle
     */
    public static void drawRectOutline(PoseStack matrices, int x, int y, int width, int height, int color) {
        fill(matrices, x, y, x + width, y + 1, color);
        fill(matrices, x, y + height - 1, x + width, y + height, color);

        fill(matrices, x, y + 1, x + 1, y + height - 1, color);
        fill(matrices, x + width - 1, y + 1, x + width, y + height - 1, color);
    }

    /**
     * Draw a filled rectangle with a gradient
     *
     * @param matrices         The transformation matrix stack
     * @param x                The x-coordinate of top-left corner of the rectangle
     * @param y                The y-coordinate of top-left corner of the rectangle
     * @param width            The width of the rectangle
     * @param height           The height of the rectangle
     * @param topLeftColor     The color at the rectangle's top left corner
     * @param topRightColor    The color at the rectangle's top right corner
     * @param bottomRightColor The color at the rectangle's bottom right corner
     * @param bottomLeftColor  The color at the rectangle's bottom left corner
     */
    public static void drawGradientRect(PoseStack matrices, int x, int y, int width, int height, int topLeftColor, int topRightColor, int bottomRightColor, int bottomLeftColor) {
        var buffer = Tesselator.getInstance().getBuilder();
        var matrix = matrices.last().pose();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(matrix, x + width, y, 0).color(topRightColor).endVertex();
        buffer.vertex(matrix, x, y, 0).color(topLeftColor).endVertex();
        buffer.vertex(matrix, x, y + height, 0).color(bottomLeftColor).endVertex();
        buffer.vertex(matrix, x + width, y + height, 0).color(bottomRightColor).endVertex();

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator.getInstance().end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    /**
     * Draw a panel that looks like the background of a vanilla
     * inventory screen
     *
     * @param matrices The transformation matrix stack
     * @param x        The x-coordinate of top-left corner of the panel
     * @param y        The y-coordinate of top-left corner of the panel
     * @param width    The width of the panel
     * @param height   The height of the panel
     * @param dark     Whether to use the dark version of the panel texture
     */
    public static void drawPanel(PoseStack matrices, int x, int y, int width, int height, boolean dark) {
        (dark ? OwoNinePatchRenderers.DARK_PANEL : OwoNinePatchRenderers.LIGHT_PANEL).draw(matrices, x, y, width, height);
    }

    public static void drawText(PoseStack matrices, net.minecraft.network.chat.Component text, float x, float y, float scale, int color) {
        drawText(matrices, text, x, y, scale, color, TextAnchor.TOP_LEFT);
    }

    public static void drawText(PoseStack matrices, net.minecraft.network.chat.Component text, float x, float y, float scale, int color, TextAnchor anchorPoint) {
        final var textRenderer = Minecraft.getInstance().font;

        matrices.pushPose();
        matrices.scale(scale, scale, 1);

        switch (anchorPoint) {
            case TOP_RIGHT -> x -= textRenderer.width(text) * scale;
            case BOTTOM_LEFT -> y -= textRenderer.lineHeight * scale;
            case BOTTOM_RIGHT -> {
                x -= textRenderer.width(text) * scale;
                y -= textRenderer.lineHeight * scale;
            }
        }

        textRenderer.draw(matrices, text, x * (1 / scale), y * (1 / scale), color);
        matrices.popPose();
    }

    public static void drawTooltip(PoseStack matrices, int x, int y, List<ClientTooltipComponent> tooltip) {
        ((ScreenInvoker) utilityScreen()).owo$renderTooltipFromComponents(matrices, tooltip, x, y);
    }

    public static UtilityScreen utilityScreen() {
        return UtilityScreen.get();
    }

    public static DebugDrawer debug() {
        return INSTANCE.debug;
    }

    public static void recordQuads() {
        recording = true;
    }

    public static boolean recording() {
        return recording;
    }

    public static void submitQuads() {
        recording = false;
        Tesselator.getInstance().end();
    }

    public enum TextAnchor {
        TOP_RIGHT, BOTTOM_RIGHT, TOP_LEFT, BOTTOM_LEFT
    }

    public static class DebugDrawer {

        private DebugDrawer() {}

        /**
         * Draw the area around the given rectangle which
         * the given insets describe
         *
         * @param matrices The transformation matrix stack
         * @param x        The x-coordinate of top-left corner of the rectangle
         * @param y        The y-coordinate of top-left corner of the rectangle
         * @param width    The width of the rectangle
         * @param height   The height of the rectangle
         * @param insets   The insets to draw around the rectangle
         * @param color    The color to draw the inset area with
         */
        public void drawInsets(PoseStack matrices, int x, int y, int width, int height, Insets insets, int color) {
            fill(matrices, x - insets.left(), y - insets.top(), x + width + insets.right(), y, color);
            fill(matrices, x - insets.left(), y + height, x + width + insets.right(), y + height + insets.bottom(), color);

            fill(matrices, x - insets.left(), y, x, y + height, color);
            fill(matrices, x + width, y, x + width + insets.right(), y + height, color);
        }

        /**
         * Draw the element inspector for the given tree, detailing the position,
         * bounding box, margins and padding of each component
         *
         * @param matrices    The transformation matrix stack
         * @param root        The root component of the hierarchy to draw
         * @param mouseX      The x-coordinate of the mouse pointer
         * @param mouseY      The y-coordinate of the mouse pointer
         * @param onlyHovered Whether to only draw the inspector for the hovered widget
         */
        public void drawInspector(PoseStack matrices, ParentComponent root, double mouseX, double mouseY, boolean onlyHovered) {
            RenderSystem.disableDepthTest();
            var client = Minecraft.getInstance();
            var textRenderer = client.font;

            var children = new ArrayList<ModComponent>();
            if (!onlyHovered) {
                root.collectChildren(children);
            } else if (root.childAt((int) mouseX, (int) mouseY) != null) {
                children.add(root.childAt((int) mouseX, (int) mouseY));
            }

            for (var child : children) {
                if (child instanceof ParentComponent parentComponent) {
                    this.drawInsets(matrices, parentComponent.x(), parentComponent.y(), parentComponent.width(),
                            parentComponent.height(), parentComponent.padding().get().inverted(), 0xA70CECDD);
                }

                final var margins = child.margins().get();
                this.drawInsets(matrices, child.x(), child.y(), child.width(), child.height(), margins, 0xA7FFF338);
                drawRectOutline(matrices, child.x(), child.y(), child.width(), child.height(), 0xFF3AB0FF);

                if (onlyHovered) {

                    int inspectorX = child.x() + 1;
                    int inspectorY = child.y() + child.height() + child.margins().get().bottom() + 1;
                    int inspectorHeight = textRenderer.lineHeight * 2 + 4;

                    if (inspectorY > client.getWindow().getGuiScaledHeight() - inspectorHeight) {
                        inspectorY -= child.fullSize().height() + inspectorHeight + 1;
                        if (inspectorY < 0) inspectorY = 1;
                        if (child instanceof ParentComponent parentComponent) {
                            inspectorX += parentComponent.padding().get().left();
                            inspectorY += parentComponent.padding().get().top();
                        }
                    }

                    final var nameText = net.minecraft.network.chat.Component.nullToEmpty(child.getClass().getSimpleName() + (child.id() != null ? " '" + child.id() + "'" : ""));
                    final var descriptor =new TranslatableComponent(child.x() + "," + child.y() + " (" + child.width() + "," + child.height() + ")"
                            + " <" + margins.top() + "," + margins.bottom() + "," + margins.left() + "," + margins.right() + "> ");
                    if (child instanceof ParentComponent parentComponent) {
                        var padding = parentComponent.padding().get();
                        descriptor.append(" >" + padding.top() + "," + padding.bottom() + "," + padding.left() + "," + padding.right() + "<");
                    }

                    int width = Math.max(textRenderer.width(nameText), textRenderer.width(descriptor));
                    fill(matrices, inspectorX, inspectorY, inspectorX + width + 3, inspectorY + inspectorHeight, 0xA7000000);
                    drawRectOutline(matrices, inspectorX, inspectorY, width + 3, inspectorHeight, 0xA7000000);

                    textRenderer.draw(matrices, nameText,
                            inspectorX + 2, inspectorY + 2, 0xFFFFFF);
                    textRenderer.draw(matrices, descriptor,
                            inspectorX + 2, inspectorY + textRenderer.lineHeight + 2, 0xFFFFFF);
                }
            }

            RenderSystem.enableDepthTest();
        }
    }

    public static class UtilityScreen extends Screen {

        private static UtilityScreen INSTANCE;

        private UtilityScreen() {
            super(new TextComponent(""));
        }

        @Override
        public void renderComponentHoverEffect(PoseStack matrices, @Nullable Style style, int x, int y) {
            super.renderComponentHoverEffect(matrices, style, x, y);
        }

        public static UtilityScreen get() {
            if (INSTANCE == null) {
                INSTANCE = new UtilityScreen();

                final var client = Minecraft.getInstance();
                INSTANCE.init(
                        client,
                        client.getWindow().getGuiScaledWidth(),
                        client.getWindow().getGuiScaledHeight()
                );
            }

            return INSTANCE;
        }

        static {
//            WindowResizeCallback.EVENT.register((client, window) -> {
//                if (INSTANCE == null) return;
//                INSTANCE.init(client, window.getGuiScaledWidth(), window.getGuiScaledHeight());
//            });
        }
    }

}
