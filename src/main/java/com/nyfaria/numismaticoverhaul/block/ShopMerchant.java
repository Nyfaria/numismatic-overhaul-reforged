package com.nyfaria.numismaticoverhaul.block;

import com.nyfaria.numismaticoverhaul.currency.CurrencyHelper;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ShopMerchant implements Merchant {

    private final ShopBlockEntity shop;
    private final boolean inexhaustible;
    private MerchantOffers recipeList = new MerchantOffers();
    private Player customer;

    public ShopMerchant(ShopBlockEntity blockEntity, boolean inexhaustible) {
        this.shop = blockEntity;
        this.inexhaustible = inexhaustible;
    }

    public void updateTrades() {
        recipeList.clear();
        shop.getOffers().forEach(offer -> recipeList.add(offer.toTradeOffer(shop, this.inexhaustible)));
    }

    @Override
    public void setTradingPlayer(@Nullable Player customer) {
        this.customer = customer;
    }

    @Nullable
    @Override
    public Player getTradingPlayer() {
        return customer;
    }

    @Override
    public MerchantOffers getOffers() {
        return recipeList;
    }

    @Override
    public void overrideOffers(@Nullable MerchantOffers offers) {
        this.recipeList = offers;
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses();
        if (!this.inexhaustible) {
            ShopOffer.remove(shop.getItems(), offer.getResult());

            this.updateTrades();
            if (this.getTradingPlayer() instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundMerchantOffersPacket(
                        serverPlayer.containerMenu.containerId,
                        this.recipeList,
                        0, 0, false, false
                ));
            }
        }

        shop.addCurrency(CurrencyHelper.getValue(Arrays.asList(offer.getBaseCostA(), offer.getCostB())));
    }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {

    }

    @Override
    public int getVillagerXp() {
        return 0;
    }

    @Override
    public void overrideXp(int experience) {

    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.VILLAGER_YES;
    }

    @Override
    public boolean isClientSide() {
        return false;
    }
}
