package com.nyfaria.numismaticoverhaul.cap;

import com.nyfaria.numismaticoverhaul.NumismaticOverhaul;
import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NumismaticOverhaul.MODID)
public class CurrencyHolderAttacher extends CapabilityAttacher {

    public static final Capability<CurrencyHolder> EXAMPLE_CAPABILITY = getCapability(new CapabilityToken<>() {
    });
    public static final ResourceLocation EXAMPLE_RL = new ResourceLocation(NumismaticOverhaul.MODID, "example");
    private static final Class<CurrencyHolder> CAPABILITY_CLASS = CurrencyHolder.class;

    public static CurrencyHolder getExampleHolderUnwrap(Entity player) {
        return getExampleHolder(player).orElse(null);
    }

    public static LazyOptional<CurrencyHolder> getExampleHolder(Entity player) {
        return player.getCapability(EXAMPLE_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<Entity> event, Player player) {
        genericAttachCapability(event, new CurrencyHolder(player), EXAMPLE_CAPABILITY, EXAMPLE_RL);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerPlayerAttacher(CurrencyHolderAttacher::attach, CurrencyHolderAttacher::getExampleHolder,true);
    }

}
