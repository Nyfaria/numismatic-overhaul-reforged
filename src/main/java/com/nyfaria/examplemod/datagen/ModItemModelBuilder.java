package com.nyfaria.examplemod.datagen;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelBuilder extends ItemModelBuilder {
    public ModItemModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
        super(outputLocation, existingFileHelper);
    }

    @Override
    public ItemModelBuilder texture(String key, ResourceLocation texture) {
        Preconditions.checkNotNull(key, "Key must not be null");
        Preconditions.checkNotNull(texture, "Texture must not be null");
        // Do not require existence of texture
        // Preconditions.checkArgument(existingFileHelper.exists(texture, ModelProvider.TEXTURE), "Texture %s does not exist in any known resource pack", texture);
        this.textures.put(key, texture.toString());
        return this;
    }
}
