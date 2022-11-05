package com.nyfaria.numismaticoverhaul.owostuff.ui.component;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.nyfaria.numismaticoverhaul.owostuff.ui.base.BaseComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.Sizing;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIModel;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIModelParsingException;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIParsing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.w3c.dom.Element;

import java.util.Map;

public class EntityComponent<E extends Entity> extends BaseComponent {

    protected final EntityRenderDispatcher dispatcher;
    protected final MultiBufferSource.BufferSource entityBuffers;
    protected final E entity;

    protected float mouseRotation = 0;
    protected float scale = 1;
    protected boolean lookAtCursor = false;
    protected boolean allowMouseRotation = false;
    protected boolean scaleToFit = false;

    protected EntityComponent(Sizing sizing, E entity) {
        final var client = Minecraft.getInstance();
        this.dispatcher = client.getEntityRenderDispatcher();
        this.entityBuffers = client.renderBuffers().bufferSource();

        this.entity = entity;

        this.sizing(sizing);
    }

    protected EntityComponent(Sizing sizing, EntityType<E> type, @Nullable CompoundTag nbt) {
        final var client = Minecraft.getInstance();
        this.dispatcher = client.getEntityRenderDispatcher();
        this.entityBuffers = client.renderBuffers().bufferSource();

        this.entity = type.create(client.level);
        if (nbt != null) entity.load(nbt);
        entity.absMoveTo(client.player.getX(), client.player.getY(), client.player.getZ());

        this.sizing(sizing);
    }

    @Override
    public void draw(PoseStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        matrices.pushPose();

        matrices.translate(x + this.width / 2f, y + this.height / 2f, 100);
        matrices.scale(75 * this.scale * this.width / 64f, -75 * this.scale * this.height / 64f, 75 * this.scale);

        matrices.translate(0, entity.getBbHeight() / -2f, 0);

        if (this.lookAtCursor) {
            float xRotation = (float) Math.toDegrees(Math.atan((mouseY - this.y - this.height / 2f) / 40f));
            float yRotation = (float) Math.toDegrees(Math.atan((mouseX - this.x - this.width / 2f) / 40f));

            if (this.entity instanceof LivingEntity living) {
                living.yHeadRotO = -yRotation;
            }

            this.entity.yRotO = -yRotation;
            this.entity.xRotO = xRotation * .65f;

            // We make sure the xRotation never becomes 0, as the lighting otherwise becomes very unhappy
            if (xRotation == 0) xRotation = .1f;
            matrices.mulPose(Vector3f.XP.rotationDegrees(xRotation * .15f));
            matrices.mulPose(Vector3f.YP.rotationDegrees(yRotation * .15f));
        } else {
            matrices.mulPose(Vector3f.XP.rotationDegrees(35));
            matrices.mulPose(Vector3f.YP.rotationDegrees(-45 + this.mouseRotation));
        }

        RenderSystem.setShaderLights(new Vector3f(.15f, 1, 0), new Vector3f(.15f, -1, 0));
        this.dispatcher.setRenderShadow(false);
        this.dispatcher.render(this.entity, 0, 0, 0, 0, 0, matrices, this.entityBuffers, LightTexture.FULL_BRIGHT);
        this.dispatcher.setRenderShadow(true);
        this.entityBuffers.endBatch();
        Lighting.setupFor3DItems();

        matrices.popPose();
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
        if (this.allowMouseRotation && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.mouseRotation += deltaX;

            super.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
            return true;
        } else {
            return super.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
        }
    }

    public E entity() {
        return this.entity;
    }

    public EntityComponent<E> allowMouseRotation(boolean allowMouseRotation) {
        this.allowMouseRotation = allowMouseRotation;
        return this;
    }

    public boolean allowMouseRotation() {
        return this.allowMouseRotation;
    }

    public EntityComponent<E> lookAtCursor(boolean lookAtCursor) {
        this.lookAtCursor = lookAtCursor;
        return this;
    }

    public boolean lookAtCursor() {
        return this.lookAtCursor;
    }

    public EntityComponent<E> scale(float scale) {
        this.scale = scale;
        return this;
    }

    public float scale() {
        return this.scale;
    }

    public EntityComponent<E> scaleToFit(boolean scaleToFit) {
        this.scaleToFit = scaleToFit;

        if (scaleToFit) {
            float xScale = .5f / entity.getBbWidth();
            float yScale = .5f / entity.getBbHeight();

            this.scale(Math.min(xScale, yScale));
        }

        return this;
    }

    public boolean scaleToFit() {
        return this.scaleToFit;
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return source == FocusSource.MOUSE_CLICK;
    }

    public static RenderablePlayerEntity createRenderablePlayer(GameProfile profile) {
        return new RenderablePlayerEntity(profile);
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        UIParsing.apply(children, "scale", UIParsing::parseFloat, this::scale);
        UIParsing.apply(children, "look-at-cursor", UIParsing::parseBool, this::lookAtCursor);
        UIParsing.apply(children, "mouse-rotation", UIParsing::parseBool, this::allowMouseRotation);
        UIParsing.apply(children, "scale-to-fit", UIParsing::parseBool, this::scaleToFit);
    }

    public static EntityComponent<?> parse(Element element) {
        UIParsing.expectAttributes(element, "type");
        var entityId = UIParsing.parseIdentifier(element.getAttributeNode("type"));
        var entityType = Registry.ENTITY_TYPE.getOptional(entityId).orElseThrow(() -> new UIModelParsingException("Unknown entity type " + entityId));

        return new EntityComponent<>(Sizing.content(), entityType, null);
    }

    protected static class RenderablePlayerEntity extends LocalPlayer {

        protected ResourceLocation skinTextureId = null;
        protected String model = null;

        public RenderablePlayerEntity(GameProfile profile) {
            super(Minecraft.getInstance(),
                    Minecraft.getInstance().level,
                    new ClientPacketListener(Minecraft.getInstance(),
                            null,
                            new Connection(PacketFlow.CLIENTBOUND),
                            profile,
                            Minecraft.getInstance().createTelemetryManager()
                    ),
                    null, null, false, false
            );

            this.minecraft.getSkinManager().registerSkins(this.getGameProfile(), (type, identifier, texture) -> {
                if (type != MinecraftProfileTexture.Type.SKIN) return;

                this.skinTextureId = identifier;
                this.model = texture.getMetadata("model");
                if (this.model == null) this.model = "default";

            }, true);
        }

        @Override
        public boolean isSkinLoaded() {
            return skinTextureId != null;
        }

        @Override
        public ResourceLocation getSkinTextureLocation() {
            return this.skinTextureId != null ? this.skinTextureId : super.getSkinTextureLocation();
        }


        @Override
        public boolean isModelPartShown(PlayerModelPart modelPart) {
            return true;
        }

        @Override
        public String getModelName() {
            return this.model != null ? this.model : super.getModelName();
        }

        @Nullable
        @Override
        protected PlayerInfo getPlayerInfo() {
            return null;
        }
    }
}
