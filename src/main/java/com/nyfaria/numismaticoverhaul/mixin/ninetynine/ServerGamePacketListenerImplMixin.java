package com.nyfaria.numismaticoverhaul.mixin.ninetynine;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin
{
    /**
     * Removes the hard coded limit to disallow giving more than 64 items in creative mode.
     */
    @ModifyConstant(method = "handleSetCreativeModeSlot", constant = @Constant(intValue = 64))
    private int largerCoinStacks(int value)
    {
        return 99;
    }
}