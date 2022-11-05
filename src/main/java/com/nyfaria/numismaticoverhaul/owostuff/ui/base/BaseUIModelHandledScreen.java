package com.nyfaria.numismaticoverhaul.owostuff.ui.base;

import com.nyfaria.numismaticoverhaul.owostuff.ui.core.OwoUIAdapter;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ParentComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.parsing.UIModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

public abstract class BaseUIModelHandledScreen<R extends ParentComponent, S extends AbstractContainerMenu> extends BaseOwoHandledScreen<R, S> {

    /**
     * The UI model this screen is built upon, parsed from XML.
     * This is usually not relevant to subclasses, the UI adapter
     * inherited from {@link BaseOwoScreen} is more interesting
     */
    protected final UIModel model;
    protected final Class<R> rootComponentClass;

    protected BaseUIModelHandledScreen(S handler, Inventory inventory, Component title, Class<R> rootComponentClass, BaseUIModelScreen.DataSource source) {
        super(handler, inventory, title);
        var providedModel = source.get();
        if (providedModel == null) {
            source.reportError();
            this.invalid = true;
        }

        this.rootComponentClass = rootComponentClass;
        this.model = providedModel;
    }

    @Override
    protected @NotNull OwoUIAdapter<R> createAdapter() {
        return this.model.createAdapter(rootComponentClass, this);
    }
}
