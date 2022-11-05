package com.nyfaria.numismaticoverhaul.block;


import com.nyfaria.numismaticoverhaul.init.BlockInit;
import com.nyfaria.numismaticoverhaul.init.SoundInit;
import com.nyfaria.numismaticoverhaul.init.TagInit;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class PiggyBankBlock extends HorizontalDirectionalBlock implements EntityBlock {

    private static final VoxelShape NORTH_SHAPE = Stream.of(
            Block.box(7, 2, 4, 9, 4, 5),
            Block.box(5, 1, 5, 11, 6, 11),
            Block.box(5, 0, 5, 6, 1, 7),
            Block.box(5, 0, 9, 6, 1, 11),
            Block.box(10, 0, 9, 11, 1, 11),
            Block.box(10, 0, 5, 11, 1, 7)
    ).reduce(Shapes::or).get();

    private static final VoxelShape SOUTH_SHAPE = Stream.of(
            Block.box(7, 2, 11, 9, 4, 12),
            Block.box(5, 1, 5, 11, 6, 11),
            Block.box(10, 0, 9, 11, 1, 11),
            Block.box(10, 0, 5, 11, 1, 7),
            Block.box(5, 0, 5, 6, 1, 7),
            Block.box(5, 0, 9, 6, 1, 11)
    ).reduce(Shapes::or).get();

    private static final VoxelShape EAST_SHAPE = Stream.of(
            Block.box(11, 2, 7, 12, 4, 9),
            Block.box(5, 1, 5, 11, 6, 11),
            Block.box(9, 0, 5, 11, 1, 6),
            Block.box(5, 0, 5, 7, 1, 6),
            Block.box(5, 0, 10, 7, 1, 11),
            Block.box(9, 0, 10, 11, 1, 11)
    ).reduce(Shapes::or).get();

    private static final VoxelShape WEST_SHAPE = Stream.of(
            Block.box(4, 2, 7, 5, 4, 9),
            Block.box(5, 1, 5, 11, 6, 11),
            Block.box(5, 0, 10, 7, 1, 11),
            Block.box(9, 0, 10, 11, 1, 11),
            Block.box(9, 0, 5, 11, 1, 6),
            Block.box(5, 0, 5, 7, 1, 6)
    ).reduce(Shapes::or).get();

    public PiggyBankBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.TERRACOTTA));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            default -> NORTH_SHAPE;
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

        if (!world.isClientSide) {
            if (world.getBlockEntity(pos) instanceof MenuProvider factory) {
                player.openMenu(factory);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (entity instanceof FallingBlockEntity fallingBlock && fallingBlock.getBlockState().is(TagInit.VERY_HEAVY_BLOCKS) && !world.isClientSide) {
            if (world.getBlockEntity(pos) instanceof PiggyBankBlockEntity piggyBank) {
                Containers.dropContents(world, pos.relative(world.getBlockState(pos).getValue(FACING).getOpposite()), piggyBank.inventory());
            }

            world.removeBlock(pos, false);

            world.playSound(null,pos, SoundInit.PIGGY_BANK_BREAK.get(), SoundSource.BLOCKS,1,1);
//            NumismaticOverhaul.PIGGY_BANK_BROKEN.spawn(world, Vec3.atLowerCornerOf(pos), Math.round(fallDistance));
        }

        super.fallOn(world, state, pos, entity, fallDistance);
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (world.getBlockEntity(pos) instanceof PiggyBankBlockEntity piggyBank && player.isCreative() && !world.isClientSide && !piggyBank.inventory().stream().allMatch(ItemStack::isEmpty)) {

            var stack = new ItemStack(BlockInit.PIGGY_BANK.get());
            piggyBank.saveToItem(stack);

            ItemEntity var = new ItemEntity(world, pos.getX() + .5d, pos.getY() + .5d, pos.getZ() + .5d, stack);
            var.setDefaultPickUpDelay();
            world.addFreshEntity(var);
        }

        super.playerWillDestroy(world, pos, state, player);
    }
//todo: particles
    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        if (builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof PiggyBankBlockEntity piggyBank) {
            var tool = builder.getOptionalParameter(LootContextParams.TOOL);
            if (tool != null && tool.hasCustomHoverName() && Objects.equals(tool.getHoverName().getString(), "Hammer")) {

                builder.getLevel().playSound(null, piggyBank.getBlockPos(), SoundInit.PIGGY_BANK_BREAK.get(), SoundSource.BLOCKS,1,1);
//                NumismaticOverhaul.PIGGY_BANK_BROKEN.spawn(piggyBank.getLevel(), Vec3.atLowerCornerOf(piggyBank.getBlockPos()), 5);

                var drops = new ArrayList<>(super.getDrops(state, builder));
                piggyBank.inventory().stream().filter(stack -> !stack.isEmpty()).forEach(drops::add);
                return drops;
            } else {
                builder.withDynamicDrop(new ResourceLocation("contents"), (context, consumer) -> {
                    piggyBank.inventory().forEach(consumer);
                });
            }
        }

        return super.getDrops(state, builder);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PiggyBankBlockEntity(pos, state);
    }
}
