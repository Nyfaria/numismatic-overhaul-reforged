/*
package com.nyfaria.numismaticoverhaul.mixin;

import com.nyfaria.numismaticoverhaul.ModComponents;
import com.nyfaria.numismaticoverhaul.NumismaticOverhaul;
import com.nyfaria.numismaticoverhaul.cap.CurrencyHolderAttacher;
import com.nyfaria.numismaticoverhaul.client.gui.purse.PurseButton;
import com.nyfaria.numismaticoverhaul.client.gui.purse.PurseWidget;
import com.nyfaria.numismaticoverhaul.network.NetworkHandler;
import com.nyfaria.numismaticoverhaul.network.RequestPurseActionC2SPacket;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@SuppressWarnings("UnresolvedMixinReference")
@Mixin(targets = "me.lizardofoz.inventorio.client.ui.InventorioScreen")
public abstract class InventorioScreenMixin extends EffectRenderingInventoryScreen<AbstractContainerMenu> {

    private InventorioScreenMixin(AbstractContainerMenu screenHandler, Inventory playerInventory, Component text) {
        super(screenHandler, playerInventory, text);
    }

    public PurseWidget purse;
    public PurseButton button;

    //The purse is injected via mixin instead of event because I need special callbacks in render(...) and mouseClicked(...) to handle
    //the non-button widget anyway, so I can just inject them here

    @Inject(method = "method_25426", at = @At(value = "FIELD", target = "me.lizardofoz.inventorio.client.ui.InventorioScreen.open:Z", opcode = Opcodes.PUTFIELD), remap = false)
    public void addButton(CallbackInfo ci) {
        purse = new PurseWidget(this.leftPos + 134, this.topPos + 20, minecraft, ModComponents.CURRENCY.get(minecraft.player));

        button = new PurseButton(this.leftPos + 163, this.topPos + 6, button -> {
            if (Screen.hasShiftDown()) {
                NetworkHandler.INSTANCE.sendToServer(RequestPurseActionC2SPacket.storeAll());
            } else {
                purse.toggleActive();
            }
        }, minecraft.player, this);

        this.addRenderableWidget(button);
    }

    // it used to be a lambda mixin, now it's French bread
    @Inject(method = "findLeftEdge", at = @At("TAIL"), remap = false)
    private void updateWidgetPosition(RecipeBookComponent recipeBook, int width, int parentWidth, CallbackInfoReturnable<Integer> ci) {
        final int x = ci.getReturnValueI();
        purse = new PurseWidget(x + 134, topPos + 20, Minecraft.getInstance(), CurrencyHolderAttacher.getExampleHolderUnwrap(Minecraft.getInstance().player));
        button.setPosition(x + 163, topPos + 6);
    }

    @Inject(method = "method_25394", at = @At("TAIL"), remap = false)
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        purse.render(matrices, mouseX, mouseY, delta);
    }

    @Inject(method = "method_25402", at = @At("HEAD"), cancellable = true, remap = false)
    public void onMouse(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (purse.mouseClicked(mouseX, mouseY, button)) cir.setReturnValue(true);
    }

    @Override
    protected void renderTooltip(PoseStack matrices, int x, int y) {
        if (purse.isMouseOver(x, y)) return;
        super.renderTooltip(matrices, x, y);
    }
}
*/
