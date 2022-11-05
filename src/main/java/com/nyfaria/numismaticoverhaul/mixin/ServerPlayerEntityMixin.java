package com.nyfaria.numismaticoverhaul.mixin;

import com.nyfaria.numismaticoverhaul.NumismaticOverhaul;
import com.nyfaria.numismaticoverhaul.cap.CurrencyHolder;
import com.nyfaria.numismaticoverhaul.cap.CurrencyHolderAttacher;
import com.nyfaria.numismaticoverhaul.currency.CurrencyConverter;
import com.nyfaria.numismaticoverhaul.owostuff.ops.ItemOps;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "destroyVanishingCursedItems", at = @At("TAIL"))
    public void onServerDeath(CallbackInfo ci) {
        var player = (Player) (Object) this;

        final var world = player.level;
        if (world.isClientSide) return;

        final CurrencyHolder component = CurrencyHolderAttacher.getExampleHolderUnwrap(player);

        var dropPercentage = world.getGameRules().getRule(NumismaticOverhaul.MONEY_DROP_PERCENTAGE).get() * .01f;
        int dropped = (int) (component.getValue() * dropPercentage);

        var stacksDropped = CurrencyConverter.getAsValidStacks(dropped);
        for (var drop : stacksDropped) {
            for (int i = 0; i < drop.getCount(); i++) {
                player.drop(ItemOps.singleCopy(drop), true, false);
            }
        }

        component.modify(-dropped);
    }

}
