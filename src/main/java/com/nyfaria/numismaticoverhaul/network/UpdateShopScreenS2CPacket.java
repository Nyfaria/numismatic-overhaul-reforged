package com.nyfaria.numismaticoverhaul.network;

import com.nyfaria.numismaticoverhaul.block.ShopBlockEntity;
import com.nyfaria.numismaticoverhaul.block.ShopOffer;
import com.nyfaria.numismaticoverhaul.client.gui.ShopScreen;
import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;

public record UpdateShopScreenS2CPacket(List<ShopOffer> offers, long storedCurrency, boolean transferEnabled) implements IPacket {

    public UpdateShopScreenS2CPacket(FriendlyByteBuf packetBuf) {
        this(packetBuf.readList((buf)-> ShopOffer.fromNbt(buf.readNbt())), packetBuf.readLong(), packetBuf.readBoolean());
    }
    public UpdateShopScreenS2CPacket(ShopBlockEntity shop) {
        this(shop.getOffers(), shop.getStoredCurrency(), shop.isTransferEnabled());
    }

    public void handle(NetworkEvent.Context context) {
        if (!(Minecraft.getInstance().screen instanceof ShopScreen screen)) return;
        screen.update(this);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeCollection(offers, (buf, shopOffer) -> buf.writeNbt(shopOffer.toNbt()));
        packetBuf.writeLong(storedCurrency);
        packetBuf.writeBoolean(transferEnabled);
    }
//    public static void initialize() {
//        //noinspection ConstantConditions
//        PacketBufSerializer.register(
//                ShopOffer.class,
//                (buf, shopOffer) -> buf.writeNbt(shopOffer.toNbt()),
//                buf -> ShopOffer.fromNbt(buf.readNbt())
//        );
//    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_CLIENT, UpdateShopScreenS2CPacket.class, UpdateShopScreenS2CPacket::new);
    }
}
