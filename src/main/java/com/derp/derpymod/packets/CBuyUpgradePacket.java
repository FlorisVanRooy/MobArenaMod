package com.derp.derpymod.packets;

import com.derp.derpymod.arena.upgrades.IUpgrade;
import com.derp.derpymod.capabilities.CurrencyDataProvider;
import com.derp.derpymod.capabilities.UpgradeDataProvider;
import com.derp.derpymod.network.PacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CBuyUpgradePacket {
    private final String upgradeId;

    public CBuyUpgradePacket(String upgradeId) {
        this.upgradeId = upgradeId;
    }

    public CBuyUpgradePacket(FriendlyByteBuf buf) {
        this.upgradeId = buf.readUtf(32767);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.upgradeId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            player.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgrades -> {
                IUpgrade u = upgrades.getUpgrade(upgradeId);
                if (u != null && u.purchase(player)) {
                    u.apply(player);
                    // Now send the unified sync packet back:
                    CompoundTag upgradeNBT = new CompoundTag();
                    upgrades.saveNBTData(upgradeNBT);

                    CompoundTag currencyNBT = new CompoundTag();
                    player.getCapability(CurrencyDataProvider.CURRENCY_DATA)
                            .ifPresent(cd -> cd.saveNBTData(currencyNBT));

                    PacketHandler.sendToPlayer(
                            new SSyncDataPacket(upgradeNBT, currencyNBT, /*screenType*/ null),
                            player
                    );
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }

}
