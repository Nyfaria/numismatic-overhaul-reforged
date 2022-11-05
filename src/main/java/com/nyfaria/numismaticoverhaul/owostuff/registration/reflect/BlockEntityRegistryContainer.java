package com.nyfaria.numismaticoverhaul.owostuff.registration.reflect;

import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface BlockEntityRegistryContainer extends AutoRegistryContainer<BlockEntityType<?>> {

    @Override
    default Registry<BlockEntityType<?>> getRegistry() {
        return Registry.BLOCK_ENTITY_TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    default Class<BlockEntityType<?>> getTargetFieldType() {
        return (Class<BlockEntityType<?>>) (Object) BlockEntityType.class;
    }
}
