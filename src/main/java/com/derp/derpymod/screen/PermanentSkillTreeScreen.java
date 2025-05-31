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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PermanentSkillTreeScreen extends Screen {
    private static final ResourceLocation BACKGROUND =
            new ResourceLocation("derpymod","textures/gui/permanent_skill_tree_background.png");

    private ResourceLocation getForegroundTexture() {
        // Build the path: "textures/gui/permanent_skill_tree_<category>_foreground.png"
        String texturePath = String.format("textures/gui/permanent_skill_tree_%s_foreground.png", currentCategory);
        return new ResourceLocation("derpymod", texturePath);
    }

    // Keep a reference to each category‐tab so we can re‐add them after clearWidgets():
    private final List<Button> tabButtons = new ArrayList<>();

    // -- dimensions --
    private final int guiW = 176, guiH = 166;        // visible GUI window
    private final int canvasW = 256, canvasH = 256;  // full PNG size

    // -- pan & drag state --
    private int offsetX = 0, offsetY = 0;
    private boolean dragging = false;
    private int dragStartX, dragStartY;

    // -- category tabs --
    private String currentCategory = "defence";

    public PermanentSkillTreeScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();

        // -- (1) Compute where the tabs should start drawing --
        int x0 = (width - guiW) / 2;
        int y0 = (height - guiH) / 2 - 20;

        // -- (2) Build one Button per category, store into tabButtons, AND addRenderableWidget(tab) --
        tabButtons.clear();
        int i = 0;
        for (String cat : SkillTreeRegistry.categories()) {
            Button tab = Button.builder(Component.literal(cat.toUpperCase()), b -> {
                        currentCategory = cat;
                        rebuildWidgets();
                    })
                    .pos(x0 + i * 60, y0)
                    .size(58, 20)
                    .build();

            tabButtons.add(tab);
            addRenderableWidget(tab);
            i++;
        }

        // -- (3) Now create the node‐buttons for the initial category: --
        rebuildWidgets();
    }


    /** Clear & recreate all node buttons at current offsets/category */
    protected void rebuildWidgets() {
        // 1) Remove ALL widgets from the screen (tabs + old node‐buttons):
        clearWidgets();

        // 2) Re‐attach the tab‐buttons so they stay visible:
        for (Button tab : tabButtons) {
            addRenderableWidget(tab);
        }

        // 3) Now add the node‐buttons for the new category:
        SkillTree tree = SkillTreeRegistry.get(currentCategory);
        if (tree == null) return;

        Player p = minecraft.player;
        int baseX = (width - guiW) / 2;
        int baseY = (height - guiH) / 2;

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

        // 3) Draw the full canvas (may be larger than viewport), now clipped
        ResourceLocation foreground = getForegroundTexture();
        RenderSystem.setShaderTexture(0, foreground);
        g.blit(
                foreground,
                x0 + offsetX,
                y0 + offsetY,
                0, 0,
                canvasW, canvasH
        );

        // === Draw connection lines ===
        SkillTree tree = SkillTreeRegistry.get(currentCategory);
        Player p = minecraft.player;
        if (tree != null) {
            for (SkillNode child : tree.nodes) {
                int childX = x0 + child.x + offsetX;
                int childY = y0 + child.y + offsetY;

                for (Map.Entry<String, Integer> prereqEntry : child.prereq.entrySet()) {
                    String parentId = prereqEntry.getKey();
                    SkillNode parent = tree.nodes.stream()
                            .filter(n -> n.upgradeId.equals(parentId))
                            .findFirst()
                            .orElse(null);

                    if (parent != null) {
                        int parentX = x0 + parent.x + offsetX;
                        int parentY = y0 + parent.y + offsetY;

                        var data = p.getCapability(UpgradeDataProvider.UPGRADE_DATA)
                                .orElseThrow(() -> new RuntimeException("No upgrade data found"));

                        int requiredLevel = child.prereq.getOrDefault(parent.upgradeId, 0);
                        int parentLevel = data.getUpgrade(parent.upgradeId) != null
                                ? data.getUpgrade(parent.upgradeId).getLevel()
                                : 0;

                        boolean ok = parentLevel >= requiredLevel;
                        int color = ok ? 0xFF00FF00 : 0xFFFF0000;

                        // --- UPDATED LOGIC START ---
                        // Compute the “center” of the parent button vertically,
                        // and the “center” of the child button horizontally:
                        int parentCenterY = parentY + 10;       // vertical center of parent (20px high)
                        int childCenterX  = childX + 10;        // horizontal center of child (20px wide)
                        int childTopY     = childY;             // y-coordinate of top of child button

                        // Decide which side of the parent button to start from:
                        int parentButtonWidth = 20;              // your buttons are 20×20
                        int parentSideX;
                        if (childX > parentX) {
                            // child is to the right of parent → start at parent’s RIGHT edge
                            parentSideX = parentX + parentButtonWidth;
                        } else {
                            // child is to the left (or directly above) → start at parent’s LEFT edge
                            parentSideX = parentX - 1;
                        }

                        // 1) Draw horizontal line from parentSideX to childCenterX at parentCenterY.
                        //    hLine(x1, x2, y, color) expects x1 <= x2, so swap if needed:
                        int hx1 = Math.min(parentSideX, childCenterX);
                        int hx2 = Math.max(parentSideX, childCenterX);
                        g.hLine(hx1, hx2, parentCenterY, color);

                        // 2) Draw vertical line down (or up) from parentCenterY to childTopY at childCenterX.
                        //    vLine(x, y1, y2, color) expects y1 <= y2, so swap if needed:
                        int vy1 = Math.min(parentCenterY, childTopY);
                        int vy2 = Math.max(parentCenterY, childTopY);
                        g.vLine(childCenterX, vy1, vy2, color);
                        // --- UPDATED LOGIC END ---
                    }
                }
            }
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
