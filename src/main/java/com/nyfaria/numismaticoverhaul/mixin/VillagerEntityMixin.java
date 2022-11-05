package com.nyfaria.numismaticoverhaul.mixin;

import com.nyfaria.numismaticoverhaul.villagers.data.NumismaticTradeOfferExtensions;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerEntityMixin extends AbstractVillager {

    public VillagerEntityMixin(EntityType<? extends AbstractVillager> entityType, Level world) {
        super(entityType, world);
    }

    @Shadow
    public abstract int getPlayerReputation(Player player);

    @Inject(method = "updateSpecialPrices", at = @At("TAIL"))
    private void captureReputation(Player player, CallbackInfo ci) {
        final int reputation = this.getPlayerReputation(player);

        final int adjustedReputation = (
                reputation + (player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)
                        ? ((player.getEffect(MobEffects.HERO_OF_THE_VILLAGE).getAmplifier() + 1) * 10)
                        : 0)
        );

        this.getOffers().forEach(offer -> ((NumismaticTradeOfferExtensions) offer).numismatic$setReputation(adjustedReputation));
    }

}
