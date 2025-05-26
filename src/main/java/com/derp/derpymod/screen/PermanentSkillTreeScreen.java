package com.derp.derpymod.screen;

import com.derp.derpymod.DerpyMod;
import com.derp.derpymod.arena.permanentskilltree.SkillTreeType;
import com.derp.derpymod.capabilities.CurrencyDataProvider;
import com.derp.derpymod.capabilities.UpgradeDataProvider;
import com.derp.derpymod.network.PacketHandler;
import com.derp.derpymod.packets.SExecuteUpgradePacket;
import com.derp.derpymod.util.TabButton;
import com.derp.derpymod.util.UpgradeButton;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jline.reader.Widget;

import java.util.*;

public class PermanentSkillTreeScreen extends Screen {
    private static final ResourceLocation BACKGROUND_TEXTURE =
            new ResourceLocation(DerpyMod.MODID, "textures/gui/permanent_skill_tree_background.png");
    private static final ResourceLocation DEFENCE_TREE_TEXTURE =
            new ResourceLocation(DerpyMod.MODID, "textures/gui/permanent_skill_tree_defence_foreground.png");
    private static final ResourceLocation MELEE_TREE_TEXTURE =
            new ResourceLocation(DerpyMod.MODID, "textures/gui/permanent_skill_tree_melee_foreground.png");
    private static final ResourceLocation MAGIC_TREE_TEXTURE =
            new ResourceLocation(DerpyMod.MODID, "textures/gui/permanent_skill_tree_magic_foreground.png");
    private static final ResourceLocation CHECKMARK_TEXTURE = new ResourceLocation(DerpyMod.MODID, "textures/gui/checkmarkk.png");
    private final Map<String, UpgradeButton> upgradeButtons = new HashMap<>();

    private ResourceLocation currentForegroundTexture;
    private SkillTreeType currentSkillTreeType = SkillTreeType.DEFENSE;
    private final int imageWidth = 176;
    private final int imageHeight = 166;
    private final int foregroundWidth = 256;
    private final int foregroundHeight = 256;

    // Variables for dragging
    private int contentOffsetX = 0; // Offset for the content inside the GUI
    private int contentOffsetY = 0;
    private boolean isDragging = false;
    private boolean isClicking = false;
    private int lastMouseX, lastMouseY;

    public PermanentSkillTreeScreen(Component p_97743_) {
        super(p_97743_);
    }

    @Override
    protected void init() {
        super.init();

        // Tab buttons
        addRenderableWidget(new TabButton(this, width / 2 - imageWidth / 2, height / 2 - 98, imageWidth / 3, 15, SkillTreeType.DEFENSE, "Defense"));
        addRenderableWidget(new TabButton(this, width / 2 - imageWidth / 2 + imageWidth / 3, height / 2 - 98, imageWidth / 3, 15, SkillTreeType.MELEE, "Melee"));
        addRenderableWidget(new TabButton(this, width / 2 - imageWidth / 2 + imageWidth / 3 * 2, height / 2 - 98, imageWidth / 3, 15, SkillTreeType.MAGIC, "Magic"));

        loadSkillTree(currentSkillTreeType); // Load the default skill tree
    }

    public void loadSkillTree(SkillTreeType type) {
        // Update the current skill tree type
        currentSkillTreeType = type;

        // Existing logic to load the skill tree
        upgradeButtons.clear();
        Player player = getMinecraft().player;
        contentOffsetX = 0;
        contentOffsetY = 0;

        switch (type) {
            case DEFENSE:
                currentForegroundTexture = DEFENCE_TREE_TEXTURE;
                break;
            case MELEE:
                currentForegroundTexture = MELEE_TREE_TEXTURE;
                break;
            case MAGIC:
                currentForegroundTexture = MAGIC_TREE_TEXTURE;
                break;
            default:
                currentForegroundTexture = DEFENCE_TREE_TEXTURE;
        }

        player.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgradeData -> {
            for (var upgrade : upgradeData.getUpgrades()) {
                if (upgrade.isPermanent() && upgrade.getType() == type) {
                    upgradeButtons.put(upgrade.getId(), new UpgradeButton(upgrade.getX(), upgrade.getY(), 20, 20, upgrade.getName(), upgrade.calculateEffectiveCost()));
                }
            }
        });
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Calculate base x and y for centering
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Render tab buttons
        for (var widget : this.renderables) {
            if (widget instanceof TabButton) {
                widget.render(guiGraphics, mouseX, mouseY, partialTick);
            }
        }

        // Render the static background (stays fixed)
        guiGraphics.blit(BACKGROUND_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Define padding
        int topPadding = 15; // 15 pixels padding from the top
        int sidePadding = 10; // 10 pixels padding on other sides

        // Setup scissor box to restrict rendering area (to prevent overlap with text and add padding)
        int scissorX = x + sidePadding;
        int scissorY = y + topPadding;
        int scissorWidth = imageWidth - 2 * sidePadding;
        int scissorHeight = imageHeight - topPadding - sidePadding;

        // Convert scissor coordinates to OpenGL screen coordinates
        double scale = getMinecraft().getWindow().getGuiScale();
        int scissorXScaled = (int) (scissorX * scale);
        int scissorYScaled = (int) (getMinecraft().getWindow().getHeight() - (scissorY + scissorHeight) * scale);
        int scissorWidthScaled = (int) (scissorWidth * scale);
        int scissorHeightScaled = (int) (scissorHeight * scale);

        RenderSystem.enableScissor(scissorXScaled, scissorYScaled, scissorWidthScaled, scissorHeightScaled);

        // Render the draggable foreground (icons and lines)
        guiGraphics.blit(currentForegroundTexture, contentOffsetX + x, contentOffsetY + y, 0, 0, foregroundWidth, foregroundHeight);

        // Disable scissor after rendering
        RenderSystem.disableScissor();

        // Render buttons on top of the foreground and check if any button is hovered
        UpgradeButton hoveredButton = null;
        for (UpgradeButton button : upgradeButtons.values()) {
            int buttonX = button.getInitialX() + contentOffsetX + x;
            int buttonY = button.getInitialY() + contentOffsetY + y;

            // Update button position and render
            button.setPosition(buttonX, buttonY);
            button.render(guiGraphics);

            // Check if the upgrade is purchased, if so, draw a checkmark
            if (isUpgradePurchased(button)) {  // Replace this with your actual condition
                guiGraphics.blit(CHECKMARK_TEXTURE, buttonX, buttonY, 0, 0, 20, 20);
            }

            if (button.isHovered(mouseX, mouseY)) {
                hoveredButton = button;
            }
        }

        // Render tooltip if hovering over a button
        if (hoveredButton != null) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal(hoveredButton.getUpgradeName()));
            tooltip.add(Component.literal("Cost: " + hoveredButton.getCostTooltip()));

            guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
        }

        // Render currency information (outside the scissor box)
        getMinecraft().player.getCapability(CurrencyDataProvider.CURRENCY_DATA).ifPresent(currencyData -> {
            String currencyText = "Skill points: " + currencyData.getPermanentCurrency();
            guiGraphics.drawString(this.font, currencyText, x + 5, y + 5, 0x000000, false);
        });

        // Handle any additional elements
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private static final int DRAG_THRESHOLD = 5; // Minimum distance for drag detection
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            isClicking = true;
            lastMouseX = (int) mouseX;
            lastMouseY = (int) mouseY;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isClicking && button == 0) {
            // Calculate the distance the mouse has moved
            int dx = (int) mouseX - lastMouseX;
            int dy = (int) mouseY - lastMouseY;

            // If the mouse has moved more than the threshold, it's a drag
            if (Math.abs(dx) > DRAG_THRESHOLD || Math.abs(dy) > DRAG_THRESHOLD) {
                isDragging = true;

                // Update the content offset
                contentOffsetX += dx;
                contentOffsetY += dy;

                // Update last mouse positions
                lastMouseX = (int) mouseX;
                lastMouseY = (int) mouseY;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            // If not dragging, handle the click event
            if (!isDragging) {
                for (Map.Entry<String, UpgradeButton> entry : upgradeButtons.entrySet()) {
                    UpgradeButton upgradeButton = entry.getValue();
                    if (upgradeButton.isHovered(mouseX, mouseY)) {
                        // Execute upgrade logic
                        //upgradeButton.setUnlocked(true);
                        handleUpgradeClick(entry.getKey());
                        break;
                    }
                }
            }

            // Reset dragging and clicking states
            isDragging = false;
            isClicking = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void handleUpgradeClick(String upgradeId) {
        PacketHandler.sendToServer(new SExecuteUpgradePacket(upgradeId));
    }

    public void refresh() {
        rebuildWidgets();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private boolean isUpgradePurchased(UpgradeButton button) {
        return true;
    }
}
