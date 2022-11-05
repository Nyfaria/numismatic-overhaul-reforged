package com.nyfaria.numismaticoverhaul.owostuff.registration.reflect;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;

public interface EntityRegistryContainer extends AutoRegistryContainer<EntityType<?>> {

    @Override
    default Registry<EntityType<?>> getRegistry() {
        return Registry.ENTITY_TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    default Class<EntityType<?>> getTargetFieldType() {
        return (Class<EntityType<?>>) (Object) EntityType.class;
    }
}
