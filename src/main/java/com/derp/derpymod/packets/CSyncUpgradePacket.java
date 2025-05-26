package com.derp.derpymod.packets;

import com.derp.derpymod.capabilities.UpgradeDataProvider;
import com.derp.derpymod.screen.PermanentSkillTreeScreen;
import com.derp.derpymod.screen.UpgradeTableScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CSyncUpgradePacket {
    private final CompoundTag data;
    private final ScreenType screenType;

    public CSyncUpgradePacket(CompoundTag data, ScreenType screenType) {
        this.data = data;
        this.screenType = screenType;
    }

    public CSyncUpgradePacket(FriendlyByteBuf buffer) {
        this.data = buffer.readNbt();
        this.screenType = buffer.readEnum(ScreenType.class);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeNbt(this.data);
        buffer.writeEnum(screenType);
    }

    public static void handle(CSyncUpgradePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                // Get the client-side player
                Minecraft mc = Minecraft.getInstance();
                var player = mc.player;

                // Update the player's upgrade capability with the received data
                if (player != null) {
                    player.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgradeData -> {
                        upgradeData.loadNBTData(packet.getData());
                    });
                }

                // Open the appropriate GUI based on the screen type
                switch (packet.screenType) {
                    case UPGRADE_TABLE:
                        mc.setScreen(new UpgradeTableScreen(Component.literal("Upgrade Table")));
                        break;
                    case PERMANENT_SKILL_TREE:
                        mc.setScreen(new PermanentSkillTreeScreen(Component.literal("Permanent Skill Tree")));
                        break;
                    default:
                        if (mc.screen instanceof UpgradeTableScreen) {
                            ((UpgradeTableScreen)mc.screen).refresh();
                        }
                        else if (mc.screen instanceof PermanentSkillTreeScreen) {
                            ((PermanentSkillTreeScreen)mc.screen).refresh();
                        }
                        break;
                }
            }
        });

        context.setPacketHandled(true);
    }

    public CompoundTag getData() {
        return data;
    }
}
