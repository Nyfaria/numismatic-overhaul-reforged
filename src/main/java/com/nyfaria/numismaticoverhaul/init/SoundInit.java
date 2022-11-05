package com.nyfaria.numismaticoverhaul.init;

import com.nyfaria.numismaticoverhaul.NumismaticOverhaul;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundInit {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, NumismaticOverhaul.MODID);

    public static final RegistryObject<SoundEvent> PIGGY_BANK_BREAK = registerSound("piggy_bank_break");




    protected static RegistryObject<SoundEvent> registerSound(String name) {
        return SOUNDS.register(name, () -> new SoundEvent(new ResourceLocation(NumismaticOverhaul.MODID, name)));
    }
}
