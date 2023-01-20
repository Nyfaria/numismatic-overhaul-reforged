
package com.nyfaria.numismaticoverhaul.mixin.ninetynine;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {



    @Inject(method = "getMaxStackSize", at = @At("RETURN"), cancellable = true)
    private void increaseStackLimit(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(99);
    }

    @Redirect(method = "save",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;putByte(Ljava/lang/String;B)V"))
    private void saveBigStack(CompoundTag tag, String key, byte p_128346_) {
        int count = ((ItemStack) (Object) this).getCount();
        tag.putInt(key, count);
    }

    @Redirect(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/world/item/ItemStack;count:I",
                    opcode = Opcodes.PUTFIELD))
    private void readBigStack(ItemStack instance, int value, CompoundTag tag) {
        instance.count = tag.getInt("Count");
    }

}