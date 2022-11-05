package com.nyfaria.numismaticoverhaul.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.nyfaria.numismaticoverhaul.block.ShopBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class ShopBlockEntityRender implements BlockEntityRenderer<ShopBlockEntity> {

    public ShopBlockEntityRender(BlockEntityRendererProvider.Context context) {
        super();
    }

    @Override
    public void render(ShopBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {

        var client = Minecraft.getInstance();

        if (entity.getOffers().isEmpty()) return;

        ItemStack toRender = entity.getItemToRender();
        boolean isBlockItem = toRender.getItem() instanceof BlockItem;

        int lightAbove = LevelRenderer.getLightColor(entity.getLevel(), entity.getBlockPos().above());

        matrices.pushPose();
        matrices.translate(0.5, isBlockItem ? 0.85 : 0.95, 0.5);

        float scale = isBlockItem ? 0.95f : 0.85f;
        matrices.scale(scale, scale, scale);

        matrices.mulPose(Vector3f.YP.rotationDegrees((float) (System.currentTimeMillis() / 20d % 360d)));

        client.getItemRenderer().renderStatic(toRender, ItemTransforms.TransformType.GROUND, lightAbove, OverlayTexture.NO_OVERLAY, matrices, vertexConsumers, 0);

        matrices.popPose();

    }
}
