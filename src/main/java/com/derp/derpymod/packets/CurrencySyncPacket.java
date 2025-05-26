package com.derp.derpymod.packets;

import com.derp.derpymod.capabilities.CurrencyData;
import com.derp.derpymod.capabilities.CurrencyDataProvider;
import com.derp.derpymod.capabilities.UpgradeDataProvider;
import com.derp.derpymod.network.ClientData;
import com.derp.derpymod.screen.UpgradeTableScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CurrencySyncPacket {
    private final CompoundTag currencyData;

    public CurrencySyncPacket(CompoundTag currencyData) {
        this.currencyData = currencyData;
    }

    public CurrencySyncPacket(FriendlyByteBuf buffer) {
        this.currencyData = buffer.readNbt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeNbt(currencyData);
    }

    public static void handle(CurrencySyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                // Get the client-side player
                Minecraft mc = Minecraft.getInstance();
                var player = mc.player;

                // Update the player's upgrade capability with the received data
                if (player != null) {
                    player.getCapability(CurrencyDataProvider.CURRENCY_DATA).ifPresent(currencyData -> {
                        currencyData.loadNBTData(packet.getCurrencyData()); // Assuming loadNBTData is your deserialization method
                    });
                }
            }
        });

        context.setPacketHandled(true);
    }

    public CompoundTag getCurrencyData() {
        return currencyData;
    }
}
