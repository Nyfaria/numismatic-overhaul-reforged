package com.nyfaria.numismaticoverhaul.client.gui;

import com.nyfaria.numismaticoverhaul.NumismaticOverhaul;
import com.nyfaria.numismaticoverhaul.block.ShopOffer;
import com.nyfaria.numismaticoverhaul.block.ShopScreenHandler;
import com.nyfaria.numismaticoverhaul.currency.CurrencyResolver;
import com.nyfaria.numismaticoverhaul.network.UpdateShopScreenS2CPacket;
import com.nyfaria.numismaticoverhaul.owostuff.ops.TextOps;
import com.nyfaria.numismaticoverhaul.owostuff.ui.base.BaseUIModelHandledScreen;
import com.nyfaria.numismaticoverhaul.owostuff.ui.base.BaseUIModelScreen;
import com.nyfaria.numismaticoverhaul.owostuff.ui.component.ButtonComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.component.ItemComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.component.LabelComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.component.TextureComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.container.FlowLayout;
import com.nyfaria.numismaticoverhaul.owostuff.ui.container.ScrollContainer;
import com.nyfaria.numismaticoverhaul.owostuff.ui.core.ModComponent;
import com.nyfaria.numismaticoverhaul.owostuff.ui.inject.ButtonWidgetExtension;
import com.nyfaria.numismaticoverhaul.owostuff.ui.util.UISounds;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

;

public class ShopScreen extends BaseUIModelHandledScreen<FlowLayout, ShopScreenHandler> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(NumismaticOverhaul.MODID,"textures/gui/shop_gui.png");
    public static final ResourceLocation TRADES_TEXTURE = new ResourceLocation(NumismaticOverhaul.MODID,"textures/gui/shop_gui_trades.png");

    private final List<Button> tabButtons = new ArrayList<>();
    private final List<ShopOffer> offers = new ArrayList<>();

    private Runnable afterDataUpdate = () -> {};
    private Consumer<String> priceDisplay = s -> {};
    private int tab = 0;

    public ShopScreen(ShopScreenHandler handler, Inventory inventory, net.minecraft.network.chat.Component title) {
//        super(handler, inventory, title, FlowLayout.class, BaseUIModelScreen.DataSource.file("../src/main/resources/assets/numismaticoverhaul/owo_ui/shop.xml"));
        super(handler, inventory, title, FlowLayout.class, BaseUIModelScreen.DataSource.asset(new ResourceLocation(NumismaticOverhaul.MODID,"shop")));
        this.inventoryLabelY += 1;
        this.titleLabelY = 5;
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        this.tabButtons.clear();

        var leftColumn = rootComponent.childById(FlowLayout.class, "left-column");
        leftColumn.child(makeTabButton(Items.CHEST, false, button -> selectTab(0)));
        leftColumn.child(makeTabButton(Items.EMERALD, true, button -> this.selectTab(1)));

        ((ButtonWidgetExtension)rootComponent.childById(ButtonComponent.class, "extract-button")).onPress(button ->
                this.menu.extractCurrency());

        rootComponent.childById(FlowLayout.class, "transfer-button").mouseDown().subscribe((x, y, button) -> {
            if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;

            this.menu.toggleTransfer();
            UISounds.playInteractionSound();
            return true;
        });
    }

    public void update(UpdateShopScreenS2CPacket data) {
        if (this.uiAdapter == null) return;

        long[] storedCurrency = CurrencyResolver.splitValues(data.storedCurrency());
        this.component(LabelComponent.class, "bronze-count").text(net.minecraft.network.chat.Component.literal(String.valueOf(storedCurrency[0])));
        this.component(LabelComponent.class, "silver-count").text(net.minecraft.network.chat.Component.literal(String.valueOf(storedCurrency[1])));
        this.component(LabelComponent.class, "gold-count").text(net.minecraft.network.chat.Component.literal(String.valueOf(storedCurrency[2])));

        int prevOffers = this.offers.size();
        this.offers.clear();
        this.offers.addAll(data.offers());
        this.populateTrades(this.tab);

        if (this.tab == 1 && this.offers.size() > prevOffers) {
            var offersScroll = this.component(ScrollContainer.class, "offer-container");
            var leftColumn = offersScroll.childById(FlowLayout.class, "first-trades-column");

            offersScroll.scrollTo(leftColumn.children().get(leftColumn.children().size() - 1));
        }

        this.component(FlowLayout.class, "transfer-button").tooltip(
                data.transferEnabled()
                        ? net.minecraft.network.chat.Component.translatable("gui.numismaticoverhaul.shop.transfer_tooltip.enabled")
                        : net.minecraft.network.chat.Component.translatable("gui.numismaticoverhaul.shop.transfer_tooltip.disabled")
        );
        this.component(LabelComponent.class, "transfer-label").text(
                data.transferEnabled()
                        ? TextOps.withColor("✔", 0x28FFBF)
                        : TextOps.withColor("✘", 0xEB1D36)
        );

        this.afterDataUpdate();
    }

    public void afterDataUpdate() {
        this.afterDataUpdate.run();
    }

    private void selectTab(int index) {
        if (this.tab == index) return;

        if (index == 0) {
            this.swapBackgroundTexture(TEXTURE);
            this.titleLabelY = 5;

            this.component(FlowLayout.class, "right-column").removeChild(this.component(FlowLayout.class, "trade-edit-widget"));
            this.afterDataUpdate = () -> {};
            this.priceDisplay = s -> {};
        } else {
            this.swapBackgroundTexture(TRADES_TEXTURE);
            this.titleLabelY = 69420;

            final var editWidget = this.model.expandTemplate(FlowLayout.class, "trade-edit-widget", Map.of());
            ButtonComponent submitButton = editWidget.childByIdOther(ButtonComponent.class, "submit-button");
            ButtonComponent deleteButton = editWidget.childByIdOther(ButtonComponent.class, "delete-button");

            EditBox priceField = editWidget.childByIdOther(EditBox.class, "price-field");
            priceField.setMaxLength(7);
            priceField.setFilter(s -> s.matches("\\d*"));
            priceField.setResponder(s -> {
                this.afterDataUpdate();

                var price = CurrencyResolver.splitValues(s.isBlank() ? 0 : Integer.parseInt(s));
                this.component(LabelComponent.class, "offer-bronze-count").text(net.minecraft.network.chat.Component.literal(String.valueOf(price[0])));
                this.component(LabelComponent.class, "offer-silver-count").text(net.minecraft.network.chat.Component.literal(String.valueOf(price[1])));
                this.component(LabelComponent.class, "offer-gold-count").text(net.minecraft.network.chat.Component.literal(String.valueOf(price[2])));
            });

            submitButton.onPress((ButtonComponent button) -> this.menu.createOffer(Integer.parseInt(priceField.getValue())));
            deleteButton.onPress((ButtonComponent button) -> this.menu.deleteOffer());

            this.priceDisplay = priceField::setValue;
            this.afterDataUpdate = () -> {
                var priceText = priceField.getValue();
                boolean hasOffer = this.hasOfferFor(this.menu.getBufferStack());

                submitButton.active = !priceText.isBlank()
                        && Integer.parseInt(priceText) > 0
                        && !this.menu.getBufferStack().isEmpty()
                        && (this.offers.size() < 24 || hasOffer);
                deleteButton.active = hasOffer;
            };

            this.component(FlowLayout.class, "right-column").child(0, editWidget);
        }

        this.populateTrades(index);
        for (int i = 0; i < this.tabButtons.size(); i++) {
            this.tabButtons.get(i).active = i != index;
        }

        this.tab = index;
    }

    private boolean hasOfferFor(ItemStack stack) {
        return this.offers.stream().anyMatch(offer -> ItemStack.matches(stack, offer.getSellStack()));
    }

    private void populateTrades(int tab) {
        var firstColumn = this.component(FlowLayout.class, "first-trades-column");
        var secondColumn = this.component(FlowLayout.class, "second-trades-column");

        firstColumn.clearChildren();
        secondColumn.clearChildren();

        if (tab == 0) return;

        for (int i = 0; i < this.offers.size(); i++) {
            final int offerIndex = i;
            var offer = this.offers.get(offerIndex);

            var component = this.model.expandTemplate(FlowLayout.class, "trade-button", Map.of("price", String.valueOf(offer.getPrice())));
            component.childById(ItemComponent.class, "item-display").stack(offer.getSellStack());
            ((ButtonWidgetExtension)component.childByIdOther(Button.class, "trade-button")).onPress(button -> {
                this.menu.loadOffer(offerIndex);
                this.priceDisplay.accept(String.valueOf(offer.getPrice()));
            });

            (i % 2 == 0 ? firstColumn : secondColumn).child(component);
        }
    }

    private void swapBackgroundTexture(ResourceLocation newTexture) {
        final var background = this.component(FlowLayout.class, "background");
        background.removeChild(background.children().get(0));
        background.child(0, this.model.expandTemplate(TextureComponent.class, "background-texture", Map.of("texture", newTexture.toString())));
    }

    private FlowLayout makeTabButton(Item icon, boolean active, Button.OnPress onPress) {
        var buttonContainer = this.model.expandTemplate(FlowLayout.class, "tab-button", Map.of("icon-item", Registry.ITEM.getKey(icon).toString()));

        final Button button = buttonContainer.childByIdOther(Button.class, "tab-button");
        this.tabButtons.add(button);

        button.active = active;
        ((ButtonWidgetExtension)button).onPress(onPress);

        return buttonContainer;
    }

    private <C extends ModComponent> C component(Class<C> componentClass, String id) {
        return this.uiAdapter.rootComponent.childById(componentClass, id);
    }

    public int tab() {
        return this.tab;
    }
}
