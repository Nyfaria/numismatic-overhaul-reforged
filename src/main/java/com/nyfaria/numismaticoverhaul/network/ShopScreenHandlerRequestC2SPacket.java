package com.nyfaria.numismaticoverhaul.network;

import com.nyfaria.numismaticoverhaul.block.ShopOffer;
import com.nyfaria.numismaticoverhaul.block.ShopScreenHandler;
import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public record ShopScreenHandlerRequestC2SPacket(Action action, long value) implements IPacket {

    public ShopScreenHandlerRequestC2SPacket(FriendlyByteBuf packetBuf) {
        this(packetBuf.readEnum(Action.class), packetBuf.readLong());
    }

    public ShopScreenHandlerRequestC2SPacket(Action action) {
        this(action, 0);
    }

    public void handle(NetworkEvent.Context context) {
        final var player = context.getSender();

        if (!(player.containerMenu instanceof ShopScreenHandler shopHandler)) return;

        switch (action) {
            case LOAD_OFFER -> shopHandler.loadOffer(value);
            case CREATE_OFFER -> shopHandler.createOffer(value);
            case DELETE_OFFER -> shopHandler.deleteOffer();
            case EXTRACT_CURRENCY -> shopHandler.extractCurrency();
            case TOGGLE_TRANSFER -> shopHandler.toggleTransfer();
        }
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeEnum(action);
        packetBuf.writeLong(value);
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, ShopScreenHandlerRequestC2SPacket.class, ShopScreenHandlerRequestC2SPacket::new);
    }
    public enum Action {
        CREATE_OFFER, DELETE_OFFER, LOAD_OFFER, EXTRACT_CURRENCY, TOGGLE_TRANSFER
    }

}
