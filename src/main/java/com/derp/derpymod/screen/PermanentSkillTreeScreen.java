package com.derp.derpymod.screen;

import com.derp.derpymod.arena.permanentskilltree.SkillNode;
import com.derp.derpymod.arena.permanentskilltree.SkillTree;
import com.derp.derpymod.arena.permanentskilltree.SkillTreeRegistry;
import com.derp.derpymod.arena.upgrades.IUpgrade;
import com.derp.derpymod.arena.upgrades.LeveledUpgrade;
import com.derp.derpymod.arena.upgrades.OneTimeUpgrade;
import com.derp.derpymod.capabilities.UpgradeDataProvider;
import com.derp.derpymod.network.PacketHandler;
import com.derp.derpymod.packets.CBuyUpgradePacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class PermanentSkillTreeScreen extends Screen {
    private static final ResourceLocation BACKGROUND =
            new ResourceLocation("derpymod","textures/gui/permanent_skill_tree_background.png");
    private static final ResourceLocation FOREGROUND =
            new ResourceLocation("derpymod","textures/gui/permanent_skill_tree_defence_foreground.png");

    // -- dimensions --
    private final int guiW = 176, guiH = 166;        // visible GUI window
    private final int canvasW = 256, canvasH = 256;  // full PNG size

    // -- pan & drag state --
    private int offsetX = 0, offsetY = 0;
    private boolean dragging = false;
    private int dragStartX, dragStartY;

    // -- category tabs --
    private String currentCategory = "defense";

    public PermanentSkillTreeScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        // create category tabs
        int x0 = (width - guiW)/2;
        int y0 = (height - guiH)/2 - 20;
        int i = 0;
        for (String cat : SkillTreeRegistry.categories()) {
            Button tab = Button.builder(Component.literal(cat.toUpperCase()), b -> {
                        currentCategory = cat;
                        rebuildWidgets();
                    })
                    .pos(x0 + i*60, y0)
                    .size(58, 20)
                    .build();
            addRenderableWidget(tab);
            i++;
        }
        // initial node buttons
        rebuildWidgets();
    }

    /** Clear & recreate all node buttons at current offsets/category */
    protected void rebuildWidgets() {
        clearWidgets();
        SkillTree tree = SkillTreeRegistry.get(currentCategory);
        if (tree == null) return;

        Player p = minecraft.player;
        int baseX = (width - guiW)/2;
        int baseY = (height - guiH)/2;

        tree.nodes.forEach(node -> {
            int bx = baseX + node.x + offsetX;
            int by = baseY + node.y + offsetY;

            p.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(data -> {
                IUpgrade up = data.getUpgrade(node.upgradeId);
                if (up == null) return;

                boolean maxed;
                if (up instanceof LeveledUpgrade lvl) {
                    maxed = up.getLevel() >= lvl.getMaxLevel();
                } else if (up instanceof OneTimeUpgrade ot) {
                    maxed = up.getLevel() == 1;
                } else {
                    maxed = false;
                }

                String cost = maxed
                        ? "MAX"
                        : String.valueOf(up.calculateCost(up.getLevel() + 1));

                Component tip = Component.literal(up.getDisplayName() + "\nCost: " + cost);

                Button btn = Button.builder(Component.empty(), b -> {
                            if (!dragging && !maxed && prereqsMet(node, p)) {
                                PacketHandler.sendToServer(new CBuyUpgradePacket(node.upgradeId));
                            }
                        })
                        .pos(bx, by)
                        .size(20, 20)
                        .tooltip(Tooltip.create(tip))
                        .build();

                btn.setAlpha(0);

                addRenderableWidget(btn);
            });
        });
    }

    /** All prerequisites of this node must be satisfied */
    private boolean prereqsMet(SkillNode node, Player p) {
        var data = p.getCapability(UpgradeDataProvider.UPGRADE_DATA)
                .orElseThrow(() -> new RuntimeException("No upgrade data"));
        for (Map.Entry<String,Integer> req : node.prereq.entrySet()) {
            var u2 = data.getUpgrade(req.getKey());
            if (u2 == null || u2.getLevel() < req.getValue()) {
                p.sendSystemMessage(Component.literal("Previous upgrade levels too low").withStyle(ChatFormatting.RED));
                return false;
            }
        }
        return true;
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        // 1) Draw static GUI background
        int x0 = (width - guiW) / 2;
        int y0 = (height - guiH) / 2;
        RenderSystem.setShaderTexture(0, BACKGROUND);
        g.blit(BACKGROUND, x0, y0, 0, 0, guiW, guiH);

        // 2) Enable scissor to clip the FG – inset by 5px on each side
        int pad = 5;
        int insetX = x0 + pad;
        int insetY = y0 + pad;
        int insetW = guiW - pad * 2;
        int insetH = guiH - pad * 2;

        double scale = minecraft.getWindow().getGuiScale();
        int sx = (int)(insetX * scale);
        int sy = (int)((minecraft.getWindow().getHeight() - (insetY + insetH) * scale));
        int sw = (int)(insetW * scale);
        int sh = (int)(insetH * scale);

        RenderSystem.enableScissor(sx, sy, sw, sh);

        // 3) Draw the full canvas (may be larger than viewport), now clipped 5px before edges
        RenderSystem.setShaderTexture(0, FOREGROUND);
        g.blit(FOREGROUND,
                x0 + offsetX, y0 + offsetY,
                0, 0,
                canvasW, canvasH);

        // 4) Draw connection lines
        SkillTree tree = SkillTreeRegistry.get(currentCategory);
        Player p = minecraft.player;
        if (tree != null) {
            tree.nodes.forEach(parent -> {
                int parentX = x0 + parent.x + offsetX;
                int parentY = y0 + parent.y + offsetY;

                parent.prereq.keySet().forEach(childId -> {
                    SkillNode child = tree.nodes.stream()
                            .filter(n -> n.upgradeId.equals(childId))
                            .findFirst().orElse(null);

                    if (child != null) {
                        int childX = x0 + child.x + offsetX;
                        int childY = y0 + child.y + offsetY;

                        var data = p.getCapability(UpgradeDataProvider.UPGRADE_DATA)
                                .orElseThrow(() -> new RuntimeException("No upgrade data found"));

                        int requiredLevel = parent.prereq.getOrDefault(child.upgradeId, 0);
                        int parentLevel = data.getUpgrade(child.upgradeId) != null
                                ? data.getUpgrade(child.upgradeId).getLevel()
                                : 0;

                        boolean ok = parentLevel >= requiredLevel;
                        int color = ok ? 0xFF00FF00 : 0xFFFF0000;

                        int parentEdgeX = (childX + 10) < (parentX + 10)
                                ? parentX  // left edge of parent
                                : parentX + 20;  // right edge of parent

                        int midX = childX + 10;  // center of child button
                        int topChildY = childY; // top of child button
                        int midParentY = parentY + 10; // center of parent button

                        // Horizontal from parent's left/right to midX
                        g.hLine(Math.min(parentEdgeX, midX), Math.max(parentEdgeX, midX), midParentY, color);

                        // Vertical down to top of child
                        g.vLine(midX, Math.min(midParentY, topChildY), Math.max(midParentY, topChildY), color);
                    }
                });
            });
        }




        // 5) Disable scissor so UI buttons draw normally
        RenderSystem.disableScissor();

        // 6) Draw all buttons and tooltips
        super.render(g, mx, my, pt);
    }


    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        // Let buttons (and other widgets) handle the click first:
        boolean handled = super.mouseClicked(mx, my, btn);
        if (handled) {
            // A button was clicked; don’t treat it as the start of a drag
            return true;
        }

        // Otherwise, it's an empty-space click, so start panning
        if (btn == 0) {
            dragging = true;
            dragStartX = (int) mx;
            dragStartY = (int) my;
            return true;
        }
        return false;
    }


    @Override
    public boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
        if (dragging && btn == 0) {
            int deltaX = Math.round((float)(mx - dragStartX));
            int deltaY = Math.round((float)(my - dragStartY));
            offsetX += deltaX;
            offsetY += deltaY;
            dragStartX += deltaX;
            dragStartY += deltaY;
            rebuildWidgets();
            return true;
        }
        return super.mouseDragged(mx, my, btn, dx, dy);
    }


    @Override
    public boolean mouseReleased(double mx, double my, int btn) {
        if (btn == 0) {
            dragging = false;
        }
        return super.mouseReleased(mx, my, btn);
    }

    /** Called by packet handler to refresh without reopening */
    public void refresh() {
        rebuildWidgets();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
