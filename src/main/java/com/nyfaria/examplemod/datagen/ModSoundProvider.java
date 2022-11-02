package com.nyfaria.examplemod.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSoundProvider extends SoundDefinitionsProvider {

    public ModSoundProvider(DataGenerator generator, String modId, ExistingFileHelper helper) {
        super(generator, modId, helper);
    }

    @Override
    public void registerSounds() {
//        SoundInit.SOUNDS.getEntries().forEach(this::reSound);
    }

    public void reSound(RegistryObject<SoundEvent> entry){
        add(entry,SoundDefinition.definition().with(sound(ForgeRegistries.SOUND_EVENTS.getKey(entry.get()))));
    }
}
