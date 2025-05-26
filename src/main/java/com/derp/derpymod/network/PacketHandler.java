package com.derp.derpymod.network;

import com.derp.derpymod.DerpyMod;
import com.derp.derpymod.packets.CSyncUpgradePacket;
import com.derp.derpymod.packets.CurrencySyncPacket;
import com.derp.derpymod.packets.SExecuteUpgradePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    public static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(DerpyMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;

        // Register a packet that will be sent from the server to the client
        INSTANCE.messageBuilder(CSyncUpgradePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CSyncUpgradePacket::encode)
                .decoder(CSyncUpgradePacket::new)
                .consumerMainThread(CSyncUpgradePacket::handle)
                .add();

        // Register a packet that will be sent from the client to the server
        INSTANCE.messageBuilder(CurrencySyncPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CurrencySyncPacket::encode)
                .decoder(CurrencySyncPacket::new)
                .consumerMainThread(CurrencySyncPacket::handle)
                .add();

        INSTANCE.messageBuilder(SExecuteUpgradePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SExecuteUpgradePacket::encode)
                .decoder(SExecuteUpgradePacket::new)
                .consumerMainThread(SExecuteUpgradePacket::handle)
                .add();
    }

    public static void sendToServer(Object msg) {
        INSTANCE.send(PacketDistributor.SERVER.noArg(), msg);
    }

    public static void sendToPlayer(Object msg, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
}
