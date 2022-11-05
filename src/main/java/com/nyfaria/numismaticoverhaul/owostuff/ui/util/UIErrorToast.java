package com.nyfaria.numismaticoverhaul.owostuff.ui.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nyfaria.numismaticoverhaul.owostuff.ops.TextOps;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

;

@ApiStatus.Internal
public class UIErrorToast implements Toast {

    private final List<FormattedCharSequence> errorMessage;
    private final Font textRenderer;
    private final int width;

    public UIErrorToast(Throwable error) {
        this.textRenderer = Minecraft.getInstance().font;
        var texts = this.initText(String.valueOf(error.getMessage()), (consumer) -> {
            var stackTop = error.getStackTrace()[0];
            var errorLocation = stackTop.getClassName().split("\\.");

            consumer.accept(Component.literal("Type: ").withStyle(ChatFormatting.RED)
                    .append(Component.literal(error.getClass().getSimpleName()).withStyle(ChatFormatting.GRAY)));
            consumer.accept(Component.literal("Thrown by: ").withStyle(ChatFormatting.RED)
                    .append(Component.literal(errorLocation[errorLocation.length - 1] + ":" + stackTop.getLineNumber()).withStyle(ChatFormatting.GRAY)));
        });

        this.width = Math.min(240, TextOps.width(textRenderer, texts) + 8);
        this.errorMessage = this.wrap(texts);
    }

    public UIErrorToast(String message) {
        this.textRenderer = Minecraft.getInstance().font;
        var texts = this.initText(message, (consumer) -> {
            consumer.accept(Component.literal("No context provided").withStyle(ChatFormatting.GRAY));
        });
        this.width = Math.min(240, TextOps.width(textRenderer, texts) + 8);
        this.errorMessage = this.wrap(texts);
    }

    public static void report(String message) {
        Minecraft.getInstance().getToasts().addToast(new UIErrorToast(message));
    }

    public static void report(Throwable error) {
        Minecraft.getInstance().getToasts().addToast(new UIErrorToast(error));
    }

    @Override
    public Visibility render(PoseStack matrices, ToastComponent manager, long startTime) {
        Drawer.fill(matrices, 0, 0, this.width(), this.height(), 0x77000000);
        Drawer.drawRectOutline(matrices, 0, 0, this.width(), this.height(), 0xA7FF0000);

        int xOffset = this.width() / 2 - this.textRenderer.width(this.errorMessage.get(0)) / 2;
        this.textRenderer.drawShadow(matrices, this.errorMessage.get(0), 4 + xOffset, 4, 0xFFFFFF);

        for (int i = 1; i < this.errorMessage.size(); i++) {
            this.textRenderer.draw(matrices, this.errorMessage.get(i), 4, 4 + i * 11, 0xFFFFFF);
        }

        return startTime > 10000 ? Visibility.HIDE : Visibility.SHOW;
    }


    @Override
    public int height() {
        return 6 + this.errorMessage.size() * 11;
    }

    @Override
    public int width() {
        return this.width;
    }

    private List<Component> initText(String errorMessage, Consumer<Consumer<Component>> contextAppender) {
        final var texts = new ArrayList<Component>();
        texts.add(Component.literal("owo-ui error").withStyle(ChatFormatting.RED));

        texts.add(Component.literal(" "));
        contextAppender.accept(texts::add);
        texts.add(Component.literal(" "));

        texts.add(Component.literal(errorMessage));

        texts.add(Component.literal(" "));
        texts.add(Component.literal("Check your log for details").withStyle(ChatFormatting.GRAY));

        return texts;
    }

    private List<FormattedCharSequence> wrap(List<Component> message) {
        var list = new ArrayList<FormattedCharSequence>();
        for (var text : message) list.addAll(this.textRenderer.split(text, this.width() - 8));
        return list;
    }

    @Override
    public Object getToken() {
        return Type.VERY_TYPE;
    }

    enum Type {
        VERY_TYPE
    }
}
