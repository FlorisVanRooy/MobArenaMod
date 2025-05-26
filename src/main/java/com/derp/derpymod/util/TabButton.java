package com.derp.derpymod.util;

import com.derp.derpymod.arena.permanentskilltree.SkillTreeType;
import com.derp.derpymod.screen.PermanentSkillTreeScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class TabButton extends AbstractWidget {
    private final PermanentSkillTreeScreen screen;
    private final SkillTreeType type;

    public TabButton(PermanentSkillTreeScreen screen, int x, int y, int width, int height, SkillTreeType type, String title) {
        super(x, y, width, height, Component.literal(title));
        this.screen = screen;
        this.type = type;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        screen.loadSkillTree(type); // Switch to the selected skill tree
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render the button background and label
        guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, isHovered() ? 0xFFAAAAAA : 0xFF888888);

        // Use the font renderer from the screen's `font` field to draw the label
        guiGraphics.drawString(screen.getMinecraft().font, getMessage().getString(), getX() + 5, getY() + (height - 8) / 2, 0xFFFFFF, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // Add narration logic if needed for accessibility
        narrationElementOutput.add(NarratedElementType.TITLE, getMessage());
    }
}