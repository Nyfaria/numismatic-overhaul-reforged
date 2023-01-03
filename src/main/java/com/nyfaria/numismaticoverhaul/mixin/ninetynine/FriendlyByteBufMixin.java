package com.nyfaria.numismaticoverhaul.mixin.ninetynine;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This modifies the way items counts are stored in a byte buffer. Vanilla stores item counts as a byte, which only allows values
 * up to 127. These mixins change it to be an int, which goes up to 2 billion (more than enough).
 * In code, the item count is treated as an int even though it is only supposed to go up to 64, so this will work fine.
 */
@Mixin(FriendlyByteBuf.class)
public class FriendlyByteBufMixin
{

    @Redirect(method = "writeItemStack",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/network/FriendlyByteBuf;writeByte(I)Lio/netty/buffer/ByteBuf;"))
    private ByteBuf writeBiggerStackCount(FriendlyByteBuf instance, int count)
    {
        return instance.writeInt(count);
    }


    @Redirect(method = "readItem",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;readByte()B"))
    private byte doNothing(FriendlyByteBuf instance)
    {
        return 0;
    }


    @ModifyVariable(method = "readItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;<init>(Lnet/minecraft/world/level/ItemLike;I)V"), ordinal = 0)
    private int readStackItemCount(int value)
    {
        return ((FriendlyByteBuf) (Object) this).readInt();
    }
}