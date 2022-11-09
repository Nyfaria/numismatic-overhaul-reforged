package com.nyfaria.numismaticoverhaul.block;

import com.nyfaria.numismaticoverhaul.currency.CurrencyConverter;
import com.nyfaria.numismaticoverhaul.init.BlockInit;
import com.nyfaria.numismaticoverhaul.network.NetworkHandler;
import com.nyfaria.numismaticoverhaul.network.UpdateShopScreenS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class ShopBlock extends BaseEntityBlock {

    private static final VoxelShape MAIN_PILLAR = Block.box(1, 0, 1, 14, 8, 14);

    private static final VoxelShape PLATE = Block.box(0, 8, 0, 16, 12, 16);

    private static final VoxelShape PILLAR_1 = Block.box(13, 0, 0, 16, 8, 3);
    private static final VoxelShape PILLAR_2 = Block.box(0, 0, 0, 3, 8, 3);
    private static final VoxelShape PILLAR_3 = Block.box(0, 0, 13, 3, 8, 16);
    private static final VoxelShape PILLAR_4 = Block.box(13, 0, 13, 16, 8, 16);

    private static final VoxelShape SHAPE = Shapes.or(MAIN_PILLAR, PLATE, PILLAR_1, PILLAR_2, PILLAR_3, PILLAR_4);

    private final boolean inexhaustible;

    public ShopBlock(boolean inexhaustible) {
        super(BlockBehaviour.Properties.of(Material.STONE).noOcclusion().destroyTime(5.0f));
        this.inexhaustible = inexhaustible;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide) {

            ShopBlockEntity shop = (ShopBlockEntity) world.getBlockEntity(pos);

            if (shop.getOwner().equals(player.getUUID())) {
                if (player.isShiftKeyDown()) {
                    return openShopMerchant(player, shop);
                } else {
                    player.openMenu(state.getMenuProvider(world, pos));
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(()->(ServerPlayer)player), new UpdateShopScreenS2CPacket(shop));
                }
            } else {
                return openShopMerchant(player, shop);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult openShopMerchant(Player player, ShopBlockEntity shop) {
        if (shop.getMerchant().getTradingPlayer() != null) return InteractionResult.SUCCESS;

        ((ShopMerchant) shop.getMerchant()).updateTrades();
        shop.getMerchant().setTradingPlayer(player);
        shop.getMerchant().openTradingScreen(player, new TranslatableComponent("gui.numismaticoverhaul.shop.merchant_title"), 0);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClientSide) return;

        if (!(placer instanceof ServerPlayer)) {
            world.destroyBlock(pos, true);
            return;
        }

        ((ShopBlockEntity) world.getBlockEntity(pos)).setOwner(placer.getUUID());
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (world.getBlockEntity(pos) instanceof ShopBlockEntity shop) {
                CurrencyConverter.getAsValidStacks(shop.getStoredCurrency())
                        .forEach(stack -> Containers.dropItemStack(shop.getLevel(), pos.getX(), pos.getY(), pos.getZ(), stack));

                Containers.dropContents(world, pos, shop);
            }
            super.onRemove(state, world, pos, newState, moved);
        }
    }

    public boolean inexhaustible() {
        return this.inexhaustible;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ShopBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, BlockInit.SHOP_BE.get(), ShopBlockEntity::tick);
    }
}
