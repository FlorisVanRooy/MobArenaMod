package com.derp.derpymod.packets;

import com.derp.derpymod.capabilities.CurrencyDataProvider;
import com.derp.derpymod.capabilities.UpgradeDataProvider;
import com.derp.derpymod.screen.PermanentSkillTreeScreen;
import com.derp.derpymod.screen.ScreenType;
import com.derp.derpymod.screen.UpgradeTableScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SSyncDataPacket {
    private final CompoundTag upgradeData;
    private final CompoundTag currencyData;
    private final ScreenType screenType;  // can be null if you’re just refreshing

    public SSyncDataPacket(CompoundTag upgradeData,
                           CompoundTag currencyData,
                           ScreenType screenType) {
        this.upgradeData  = upgradeData;
        this.currencyData = currencyData;
        this.screenType   = screenType;
    }

    public SSyncDataPacket(FriendlyByteBuf buf) {
        this.upgradeData  = buf.readNbt();
        this.currencyData = buf.readNbt();
        this.screenType   = buf.readBoolean()
                ? buf.readEnum(ScreenType.class)
                : null;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(upgradeData);
        buf.writeNbt(currencyData);
        buf.writeBoolean(screenType != null);
        if (screenType != null) buf.writeEnum(screenType);
    }

    public static void handle(SSyncDataPacket pkt,
                              Supplier<NetworkEvent.Context> ctxSup) {
        NetworkEvent.Context ctx = ctxSup.get();
        ctx.enqueueWork(() -> {
            if (ctx.getDirection().getReceptionSide().isClient()) {
                Minecraft mc = Minecraft.getInstance();
                var player = mc.player;
                if (player == null) return;

                // 1) Sync upgrades
                player.getCapability(UpgradeDataProvider.UPGRADE_DATA)
                        .ifPresent(data -> data.loadNBTData(pkt.upgradeData));

                // 2) Sync currency
                player.getCapability(CurrencyDataProvider.CURRENCY_DATA)
                        .ifPresent(cd -> cd.loadNBTData(pkt.currencyData));

                // 3) Open or refresh UI
                if (pkt.screenType != null) {
                    switch (pkt.screenType) {
                        case UPGRADE_TABLE:
                            mc.setScreen(new UpgradeTableScreen(Component.literal("Upgrades")));
                            break;
                        case PERMANENT_SKILL_TREE:
                            mc.setScreen(new PermanentSkillTreeScreen(Component.literal("Skills")));
                            break;
                    }
                } else {
                    // Just refresh current screen if it’s one of ours
                    if (mc.screen instanceof UpgradeTableScreen) {
                        ((UpgradeTableScreen) mc.screen).refresh();
                    } else if (mc.screen instanceof PermanentSkillTreeScreen) {
                        ((PermanentSkillTreeScreen) mc.screen).refresh();
                    }
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
