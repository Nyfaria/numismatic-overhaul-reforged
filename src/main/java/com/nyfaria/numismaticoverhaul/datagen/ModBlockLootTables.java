package com.nyfaria.numismaticoverhaul.datagen;

import com.nyfaria.numismaticoverhaul.init.BlockInit;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.OreBlock;
import net.minecraftforge.registries.RegistryObject;

import java.util.stream.Collectors;

public class ModBlockLootTables extends BlockLoot {
    @Override
    protected void addTables() {
        BlockInit.BLOCKS.getEntries().stream()
                .map(RegistryObject::get)
                .filter(block -> block.asItem() != Items.AIR && !(block instanceof OreBlock))
                .forEach(this::dropSelf);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BlockInit.BLOCKS.getEntries().stream().map(RegistryObject::get).filter(block -> block.asItem() != Items.AIR && !(block instanceof OreBlock)).collect(Collectors.toList());
    }

}
