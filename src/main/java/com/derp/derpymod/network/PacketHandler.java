package com.derp.derpymod.network;

import com.derp.derpymod.DerpyMod;
import com.derp.derpymod.packets.SSyncDataPacket;
import com.derp.derpymod.packets.CBuyUpgradePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    public static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(DerpyMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;

        // 1) Server → Client: full data sync
        CHANNEL.messageBuilder(SSyncDataPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SSyncDataPacket::encode)
                .decoder(SSyncDataPacket::new)
                .consumerMainThread(SSyncDataPacket::handle)
                .add();

        // 2) Client → Server: buy upgrade request
        CHANNEL.messageBuilder(CBuyUpgradePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(CBuyUpgradePacket::encode)
                .decoder(CBuyUpgradePacket::new)
                .consumerMainThread(CBuyUpgradePacket::handle)
                .add();
    }

    /** Call this from client code to request an upgrade purchase */
    public static void sendToServer(Object msg) {
        CHANNEL.send(PacketDistributor.SERVER.noArg(), msg);
    }

    /** Call this from server code to sync data back to a specific player */
    public static void sendToPlayer(Object msg, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
}
