package com.derp.derpymod.util;

import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;

public class UpgradeButton {
    private final int initialX, initialY;  // Store the initial X and Y positions
    private int x, y;  // These are the current X and Y positions of the button
    private final int width, height;
    private final String upgradeName;
    private final double upgradeCost;

    public UpgradeButton(int x, int y, int width, int height, String upgradeName, double upgradeCost) {
        this.initialX = x;
        this.initialY = y;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.upgradeName = upgradeName;
        this.upgradeCost = upgradeCost;
    }

    // Method to render the button
    public void render(GuiGraphics guiGraphics) {
        guiGraphics.fill(x, y, x + width, y + height, Color.TRANSLUCENT);
    }

    // Method to check if the mouse is hovering over the button
    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    // Method to get the upgrade name
    public String getUpgradeName() {
        return upgradeName;
    }

    // Method to get the upgrade cost
    public double getUpgradeCost() {
        return upgradeCost;
    }

    // Method to get the tooltip for the upgrade cost
    public String getCostTooltip() {
        return getUpgradeCost() == 0 ? "Max" : String.valueOf(getUpgradeCost());
    }

    // Getters for the initial X and Y positions
    public int getInitialX() {
        return initialX;
    }

    public int getInitialY() {
        return initialY;
    }

    // Method to set the current position of the button
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
