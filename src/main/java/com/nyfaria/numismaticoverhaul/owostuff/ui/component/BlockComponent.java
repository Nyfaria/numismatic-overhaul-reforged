package com.nyfaria.numismaticoverhaul.owostuff.ui.component;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.nyfaria.numismaticoverhaul.mixin.owomixins.ui.BlockEntityAccessor;
import com.nyfaria.numismaticoverhaul.owostuff.ui.base.BaseComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockComponent extends BaseComponent {

    private final Minecraft client = Minecraft.getInstance();

    private final BlockState state;
    private final @Nullable BlockEntity entity;

    protected BlockComponent(BlockState state, @Nullable BlockEntity entity) {
        this.state = state;
        this.entity = entity;
    }

    @Override
    @SuppressWarnings("NonAsciiCharacters")
    public void draw(PoseStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        matrices.pushPose();

        matrices.translate(x + this.width / 2f, y + this.height / 2f, 100);
        matrices.scale(40 * this.width / 64f, -40 * this.height / 64f, 40);

        matrices.mulPose(Vector3f.XP.rotationDegrees(30));
        matrices.mulPose(Vector3f.YP.rotationDegrees(45 + 180));

        matrices.translate(-.5, -.5, -.5);

        RenderSystem.runAsFancy(() -> {
            final var vertexConsumers = client.renderBuffers().bufferSource();
            if (this.state.getRenderShape() != RenderShape.ENTITYBLOCK_ANIMATED) {
                this.client.getBlockRenderer().renderSingleBlock(
                        this.state, matrices, vertexConsumers,
                        LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY
                );
            }

            if (this.entity != null) {
                var bop = this.client.getBlockEntityRenderDispatcher().getRenderer(this.entity);
                if (bop != null) {
                    bop.render(entity, partialTicks, matrices, vertexConsumers, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                }
            }

            RenderSystem.setShaderLights(new Vector3f(-1.5f, -.5f, 0), new Vector3f(0, -1, 0));
            vertexConsumers.endBatch();
            Lighting.setupFor3DItems();
        });

        matrices.popPose();
    }

    protected static void prepareBlockEntity(BlockState state, BlockEntity blockEntity, @Nullable CompoundTag nbt) {
        if (blockEntity == null) return;

        ((BlockEntityAccessor) blockEntity).owo$setCachedState(state);
        blockEntity.setLevel(Minecraft.getInstance().level);

        if (nbt == null) return;

        final var nbtCopy = nbt.copy();

        nbtCopy.putInt("x", 0);
        nbtCopy.putInt("y", 0);
        nbtCopy.putInt("z", 0);

        blockEntity.load(nbtCopy);
    }
}
