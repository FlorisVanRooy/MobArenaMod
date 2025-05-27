package com.derp.derpymod.screen;

import com.derp.derpymod.arena.upgrades.leveledupgrades.*;
import com.derp.derpymod.arena.upgrades.onetimeupgrades.MinigunUnlock;
import com.derp.derpymod.capabilities.CurrencyDataProvider;
import com.derp.derpymod.capabilities.UpgradeDataProvider;
import com.derp.derpymod.network.PacketHandler;
import com.derp.derpymod.packets.CBuyUpgradePacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class UpgradeTableScreen extends Screen {
    private record Slot(String upgradeId, int x, int y) {}

    private static final Slot[] SLOTS = new Slot[]{
            new Slot(BowDamageUpgradeInfinite.ID,    18,  19),
            new Slot(SwordDamageUpgradeInfinite.ID,  58,  19),
            new Slot(ArmourUpgradeInfinite.ID,       98,  19),
            new Slot(MaxHealthUpgradeInfinite.ID,    138,  19),
            new Slot(MovementSpeedUpgradeInfinite.ID,18,  49),
            new Slot(MinigunUnlock.ID,               58,  49)
    };

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("derpymod", "textures/gui/upgrade_table.png");

    private final int imageWidth = 176;
    private final int imageHeight = 166;

    public UpgradeTableScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        Player player = minecraft.player;

        if (player != null) {
            // Iterate over upgrade keys and positions
            for (Slot slot : SLOTS) {
                String upgradeId = slot.upgradeId;
                player.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgradeData -> {
                    var upgrade = upgradeData.getUpgrade(upgradeId);
                    if (upgrade != null) {
                        Component tooltip = Component.literal(upgrade.getDisplayName() + "\nCost: " + upgrade.calculateCost(upgrade.getLevel()+1));
                        Button button = Button.builder(Component.empty(), button1 -> onButtonClicked(upgradeId))
                                .pos(x + slot.x, y + slot.y)
                                .size(20, 20)
                                .tooltip(Tooltip.create(tooltip))
                                .build();
                        button.setAlpha(0);
                        addRenderableWidget(button);
                    }
                });
            }
        } else {
            throw new IllegalStateException("Player not found.");
        }
    }

    private void onButtonClicked(String upgradeKey) {
        CBuyUpgradePacket packet = new CBuyUpgradePacket(upgradeKey);
        PacketHandler.sendToServer(packet);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        getMinecraft().player.getCapability(CurrencyDataProvider.CURRENCY_DATA).ifPresent(currencyData -> {
            String currencyText = "Currency: " + currencyData.getCurrency();
            guiGraphics.drawString(this.font, currencyText, x + 5, y + 5, 0x000000, false);
        });
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void refresh() {
        this.rebuildWidgets();
    }
}
