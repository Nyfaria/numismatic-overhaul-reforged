package com.nyfaria.numismaticoverhaul.owostuff.registration.reflect;

import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;

public interface ItemRegistryContainer extends AutoRegistryContainer<Item> {
    @Override
    default Registry<Item> getRegistry() {
        return Registry.ITEM;
    }

    @Override
    default Class<Item> getTargetFieldType() {
        return Item.class;
    }
}
