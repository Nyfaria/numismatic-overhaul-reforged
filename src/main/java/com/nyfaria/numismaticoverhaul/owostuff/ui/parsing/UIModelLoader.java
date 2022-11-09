package com.nyfaria.numismaticoverhaul.owostuff.ui.parsing;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;

public class UIModelLoader implements ResourceManagerReloadListener {

    private static final HashMap<ResourceLocation, UIModel> LOADED_MODELS = new HashMap<>();

    public static @Nullable UIModel getPreloaded(ResourceLocation id) {
        return LOADED_MODELS.getOrDefault(id, null);
    }


    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        LOADED_MODELS.clear();

        manager.listResources("owo_ui", identifier -> identifier.endsWith(".xml")).forEach((resourceId) -> {
            try {
                var modelId = new ResourceLocation(
                        resourceId.getNamespace(),
                        resourceId.getPath().substring(7, resourceId.getPath().length() - 4)
                );

                LOADED_MODELS.put(modelId, UIModel.load(manager.getResource(resourceId).getInputStream()));
            } catch (ParserConfigurationException | IOException | SAXException e) {
                //Owo.LOGGER.error("Could not parse UI model {}", resourceId, e);
            }
        });
    }
}
