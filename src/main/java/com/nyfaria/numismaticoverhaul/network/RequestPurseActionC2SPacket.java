package com.nyfaria.numismaticoverhaul.network;

import com.nyfaria.numismaticoverhaul.cap.CurrencyHolderAttacher;
import com.nyfaria.numismaticoverhaul.currency.CurrencyConverter;
import com.nyfaria.numismaticoverhaul.currency.CurrencyHelper;
import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public record RequestPurseActionC2SPacket(Action action, long value) implements IPacket {

    public RequestPurseActionC2SPacket(FriendlyByteBuf packetBuf) {
        this(packetBuf.readEnum(Action.class), packetBuf.readLong());
    }
    public void handle(NetworkEvent.Context context) {
        final ServerPlayer player = context.getSender();

        if (player.containerMenu instanceof InventoryMenu || isInventorioHandler(player)) {
            switch (action) {
                case STORE_ALL -> CurrencyHolderAttacher.getExampleHolderUnwrap(player).modify(CurrencyHelper.getMoneyInInventory(player, true));
                case EXTRACT -> {
                    //Check if we can actually extract this much money to prevent cheeky packet forgery
                    if (CurrencyHolderAttacher.getExampleHolderUnwrap(player).getValue() < value) return;

                    CurrencyConverter.getAsItemStackList(value).forEach(stack -> {
                                while(player.addItem(stack)){}
                            }
                    );
                    CurrencyHolderAttacher.getExampleHolderUnwrap(player).modify(-value);
                }
                case EXTRACT_ALL -> {
                    CurrencyConverter.getAsValidStacks(CurrencyHolderAttacher.getExampleHolderUnwrap(player).getValue())
                            .forEach(stack -> player.getInventory().placeItemBackInInventory(stack));

                    CurrencyHolderAttacher.getExampleHolderUnwrap(player).modify(-CurrencyHolderAttacher.getExampleHolderUnwrap(player).getValue());
                }
            }
        }
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeEnum(action);
        packetBuf.writeLong(value);
    }

    private static boolean isInventorioHandler(ServerPlayer player) {
        return false;
//                FabricLoader.getInstance().isModLoaded("inventorio")
//                && player.currentScreenHandler.getClass().getName().equals("me.lizardofoz.inventorio.player.InventorioScreenHandler");
    }

    public static RequestPurseActionC2SPacket storeAll() {
        return new RequestPurseActionC2SPacket(Action.STORE_ALL, 0);
    }

    public static RequestPurseActionC2SPacket extractAll() {
        return new RequestPurseActionC2SPacket(Action.EXTRACT_ALL, 0);
    }

    public static RequestPurseActionC2SPacket extract(long amount) {
        return new RequestPurseActionC2SPacket(Action.EXTRACT, amount);
    }

    public enum Action {
        STORE_ALL, EXTRACT, EXTRACT_ALL
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, RequestPurseActionC2SPacket.class, RequestPurseActionC2SPacket::new);
    }
}
