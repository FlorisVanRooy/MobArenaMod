package com.derp.derpymod.arena.upgrades;

import com.derp.derpymod.capabilities.CurrencyDataProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public abstract class OneTimeUpgrade implements IUpgrade {
    private final String id;
    private boolean unlocked = false;
    private final String displayName;
    protected OneTimeUpgrade(String id, String displayName) { this.id = id; this.displayName = displayName; }

    @Override public String getId() { return id; }
    @Override
    public boolean purchase(Player player) {

        if (unlocked) {
            player.sendSystemMessage(
                    Component.literal(displayName + " is already unlocked")
                            .withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY)
            );
            return false;
        }

        if (!hasEnoughCurrency(player)) {
            player.sendSystemMessage(
                    Component.literal("Not enough currency to unlock " + displayName + "!")
                            .withStyle(ChatFormatting.BOLD, ChatFormatting.RED)
            );
            return false;
        }

        payUpgrade(player);
        unlocked = true;

        confirmationMessage(player);
        applyUnlocked(player);
        return true;
    }
    @Override public void apply(Player p) {
        if (unlocked) applyUnlocked(p);
    }
    protected abstract void applyUnlocked(Player p);

    @Override public void reset(Player player) {
        unlocked = false;
    }

    @Override public void save(CompoundTag tag) { tag.putBoolean("unlocked", unlocked); }
    @Override public void load(CompoundTag tag) { unlocked = tag.getBoolean("unlocked"); }

    @Override
    public int getLevel() {
        return unlocked ? 1 : 0;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    // —— Helper methods borrowed from LeveledUpgrade —— //
    protected boolean hasEnoughCurrency(Player player) {
        return player.getCapability(CurrencyDataProvider.CURRENCY_DATA)
                .map(cd -> cd.getCurrency() >= calculateCost(1))
                .orElse(false);
    }

    protected void payUpgrade(Player player) {
        player.getCapability(CurrencyDataProvider.CURRENCY_DATA)
                .ifPresent(cd -> cd.subtractCurrency((int) calculateCost(1)));
    }

    protected void confirmationMessage(Player player) {
        player.sendSystemMessage(
                Component.literal("Unlocked " + displayName + "!")
                        .withStyle(ChatFormatting.GREEN)
        );
    }
}

