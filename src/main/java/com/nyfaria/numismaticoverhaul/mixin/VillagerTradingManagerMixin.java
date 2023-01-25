package com.nyfaria.numismaticoverhaul.mixin;

import com.nyfaria.numismaticoverhaul.villagers.data.NumismaticVillagerTradesRegistry;
import net.minecraftforge.common.VillagerTradingManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerTradingManager.class)
public class VillagerTradingManagerMixin {
    @Inject(method = "loadTrades", at = @At("TAIL"), remap = false)
    private static void loadTrades(CallbackInfo ci) {
        NumismaticVillagerTradesRegistry.wrapModVillagers();
    }
}
