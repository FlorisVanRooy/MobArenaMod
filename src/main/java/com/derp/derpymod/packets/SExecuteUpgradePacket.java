package com.derp.derpymod.packets;

import com.derp.derpymod.arena.Upgrade;
import com.derp.derpymod.capabilities.UpgradeDataProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SExecuteUpgradePacket {
    private final String upgradeId;

    public SExecuteUpgradePacket(String upgradeId) {
        this.upgradeId = upgradeId;
    }

    public SExecuteUpgradePacket(FriendlyByteBuf buf) {
        this.upgradeId = buf.readUtf(32767);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.upgradeId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Server-side logic
            ctx.get().getSender().getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgradeData -> {
                Upgrade upgrade = upgradeData.getUpgrade(upgradeId);
                if (upgrade != null) {
                    upgrade.executeUpgrade(ctx.get().getSender());
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
