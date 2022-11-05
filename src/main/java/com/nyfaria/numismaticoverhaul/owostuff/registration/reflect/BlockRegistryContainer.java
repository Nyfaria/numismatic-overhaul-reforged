package com.nyfaria.numismaticoverhaul.owostuff.registration.reflect;

import com.nyfaria.numismaticoverhaul.owostuff.registration.annotations.AssignedName;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public interface BlockRegistryContainer extends AutoRegistryContainer<Block> {

    @Override
    default Registry<Block> getRegistry() {
        return Registry.BLOCK;
    }

    @Override
    default Class<Block> getTargetFieldType() {
        return Block.class;
    }

    @Override
    default void postProcessField(String namespace, Block value, String identifier, Field field) {
        if (field.isAnnotationPresent(NoBlockItem.class)) return;
        Registry.register(Registry.ITEM, new ResourceLocation(namespace, identifier), createBlockItem(value, identifier));
    }

    /**
     * Creates a block item for the given block
     *
     * @param block      The block to create an item for
     * @param identifier The identifier the field was assigned, possibly overridden by an {@link AssignedName}
     *                   annotation and always fully lowercase
     * @return The created BlockItem instance
     */
    default BlockItem createBlockItem(Block block, String identifier) {
        return new BlockItem(block, new Item.Properties());
    }

    /**
     * Declares that the annotated field should not
     * have a block item created for it
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface NoBlockItem {}
}
