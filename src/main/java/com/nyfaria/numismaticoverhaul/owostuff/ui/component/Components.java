package com.nyfaria.numismaticoverhaul.owostuff.ui.component;

import com.nyfaria.numismaticoverhaul.owostuff.ui.container.Containers;
import com.nyfaria.numismaticoverhaul.owostuff.ui.container.FlowLayout;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ModComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Sizing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

// TODO paginated and tabbed containers

/**
 * Utility methods for creating UI components
 */
public class Components {

    // -----------------------
    // Wrapped Vanilla Widgets
    // -----------------------

    /**
     * @deprecated Replaced by {@link com.nyfaria.numismaticoverhaul.owostuff.ui.component.ButtonComponent.Renderer#texture(ResourceLocation, int, int, int, int)}
     */
    public static TexturedButtonComponent texturedButton(ResourceLocation texture, net.minecraft.network.chat.Component message, int width, int height, int u, int v, int textureWidth, int textureHeight, Button.OnPress onPress) {
        return new TexturedButtonComponent(texture, width, height, u, v, textureWidth, textureHeight, message, onPress);
    }

    /**
     * @deprecated Replaced by {@link com.nyfaria.numismaticoverhaul.owostuff.ui.component.ButtonComponent.Renderer#texture(ResourceLocation, int, int, int, int)}
     */
    public static TexturedButtonComponent texturedButton(ResourceLocation texture, net.minecraft.network.chat.Component message, int width, int height, int u, int v, Button.OnPress onPress) {
        return new TexturedButtonComponent(texture, width, height, u, v, 256, 256, message, onPress);
    }

    /**
     * @deprecated Use {@link #button(net.minecraft.network.chat.Component, Consumer)} instead
     */
    @Deprecated(forRemoval = true)
    public static Button button(net.minecraft.network.chat.Component message, int width, int height, Button.OnPress onPress) {
        return createWithSizing(() -> new ButtonComponent(message, onPress::onPress), Sizing.fixed(width), Sizing.fixed(height));
    }

    /**
     * @deprecated Use {@link #button(net.minecraft.network.chat.Component, Consumer)} instead
     */
    @Deprecated(forRemoval = true)
    public static Button button(net.minecraft.network.chat.Component message, Button.OnPress onPress) {
        final var button = new ButtonComponent(message, onPress::onPress);
        button.sizing(Sizing.content(1), Sizing.content());
        return button;
    }

    public static ButtonComponent button(net.minecraft.network.chat.Component message, Consumer<ButtonComponent> onPress) {
        final var button = new ButtonComponent(message, onPress);
        button.sizing(Sizing.content(1), Sizing.content());
        return button;
    }

    public static EditBox textBox(Sizing horizontalSizing) {
        return createWithSizing(
                () -> new ModEditBox(Minecraft.getInstance().font, 0, 0, 0, 0, new TextComponent("")),
                horizontalSizing,
                Sizing.fixed(20)
        );
    }

    public static EditBox textBox(Sizing horizontalSizing, String text) {
        final var textBox = textBox(horizontalSizing);
        textBox.setValue(text);
        textBox.moveCursorToStart();
        return textBox;
    }

    // ------------------
    // Default Components
    // ------------------

    public static <E extends Entity> EntityComponent<E> entity(Sizing sizing, EntityType<E> type, @Nullable CompoundTag nbt) {
        return new EntityComponent<>(sizing, type, nbt);
    }

    public static <E extends Entity> EntityComponent<E> entity(Sizing sizing, E entity) {
        return new EntityComponent<>(sizing, entity);
    }

    public static ItemComponent item(ItemStack item) {
        return new ItemComponent(item);
    }

    public static BlockComponent block(BlockState state) {
        return new BlockComponent(state, null);
    }

    public static BlockComponent block(BlockState state, BlockEntity blockEntity) {
        return new BlockComponent(state, blockEntity);
    }

    public static BlockComponent block(BlockState state, @Nullable CompoundTag nbt) {
        final var client = Minecraft.getInstance();

        BlockEntity blockEntity = null;

        if (state.getBlock() instanceof EntityBlock provider) {
            blockEntity = provider.newBlockEntity(client.player.blockPosition(), state);
            BlockComponent.prepareBlockEntity(state, blockEntity, nbt);
        }

        return new BlockComponent(state, blockEntity);
    }

    public static LabelComponent label(net.minecraft.network.chat.Component text) {
        return new LabelComponent(text);
    }

    public static CheckboxComponent checkbox(net.minecraft.network.chat.Component message) {
        return new CheckboxComponent(message);
    }

    public static SliderComponent slider(Sizing horizontalSizing) {
        return new SliderComponent(horizontalSizing);
    }

    public static DiscreteSliderComponent discreteSlider(Sizing horizontalSizing, double min, double max) {
        return new DiscreteSliderComponent(horizontalSizing, min, max);
    }

    public static SpriteComponent sprite(Material spriteId) {
        return new SpriteComponent(spriteId.sprite());
    }

    public static SpriteComponent sprite(TextureAtlasSprite sprite) {
        return new SpriteComponent(sprite);
    }

    public static TextureComponent texture(ResourceLocation texture, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        return new TextureComponent(texture, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
    }

    public static TextureComponent texture(ResourceLocation texture, int u, int v, int regionWidth, int regionHeight) {
        return new TextureComponent(texture, u, v, regionWidth, regionHeight, 256, 256);
    }

    public static BoxComponent box(Sizing horizontalSizing, Sizing verticalSizing) {
        return new BoxComponent(horizontalSizing, verticalSizing);
    }

    public static DropdownComponent dropdown(Sizing horizontalSizing) {
        return new DropdownComponent(horizontalSizing);
    }

    // -------
    // Utility
    // -------

    public static <T, C extends ModComponent> FlowLayout list(List<T> data, Consumer<FlowLayout> layoutConfigurator, Function<T, C> componentMaker, boolean vertical) {
        var layout = vertical ? Containers.verticalFlow(Sizing.content(), Sizing.content()) : Containers.horizontalFlow(Sizing.content(), Sizing.content());
        layoutConfigurator.accept(layout);

        for (var value : data) {
            layout.child(componentMaker.apply(value));
        }

        return layout;
    }

    public static VanillaWidgetComponent wrapVanillaWidget(AbstractWidget widget) {
        return new VanillaWidgetComponent(widget);
    }

    public static <T extends ModComponent> T createWithSizing(Supplier<T> componentMaker, Sizing horizontalSizing, Sizing verticalSizing) {
        var component = componentMaker.get();
        component.sizing(horizontalSizing, verticalSizing);
        return component;
    }

}
