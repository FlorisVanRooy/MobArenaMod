package com.derp.derpymod.arena.upgrades;

import com.derp.derpymod.capabilities.CurrencyDataProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class LeveledUpgrade implements IUpgrade {
    private final String id;
    private final int maxLevel;
    private final String displayName;
    private int level = 0;
    protected LeveledUpgrade(String id, int maxLevel, String displayName) {
        this.id = id;
        this.maxLevel = maxLevel;
        this.displayName = displayName;
    }

    @Override public String getId() { return id; }

    public boolean purchase(Player player) {
        if (level >= maxLevel) {
            player.sendSystemMessage(Component.literal("You already have the maximum level!").withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_AQUA));
            return false;
        }

        if (!hasEnoughCurrency(player)) {
            player.sendSystemMessage(Component.literal("You don't have enough currency!")
                    .withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA));
            return false;
        }

        level++;  // increase level
        payUpgrade(player);  // deduct cost
        confirmationMessage(player);  // tell the player
        return true;
    }
    @Override public void apply(Player p) {
        if (level > 0) applyLevel(p, level);
    }
    protected abstract void applyLevel(Player p, int level);

    @Override
    public void reset(Player player) {
        level = 0;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public int getMaxLevel() {
        return maxLevel;
    }


    @Override public void save(CompoundTag tag) { tag.putInt("level", level); }
    @Override public void load(CompoundTag tag) { level = tag.getInt("level"); }



    protected boolean hasEnoughCurrency(Player player) {
        return player.getCapability(CurrencyDataProvider.CURRENCY_DATA)
                .map(currencyData -> {
                    int cost = calculateCost(level + 1);
                    return isPermanent()
                            ? currencyData.getPermanentCurrency() >= cost
                            : currencyData.getCurrency() >= cost;
                })
                .orElse(false);
    }

    /**
     * Deduct the cost of the *next* level from their currency.
     */
    protected void payUpgrade(Player player) {
        player.getCapability(CurrencyDataProvider.CURRENCY_DATA)
                .ifPresent(currencyData -> {
                    int cost = calculateCost(level);
                    if (isPermanent()) {
                        currencyData.subtractPermanentCurrency(cost);
                    } else {
                        currencyData.subtractCurrency(cost);
                    }
                });
    }

    /**
     * Send a simple chat confirmation (you can customize the text or styling).
     */
    protected void confirmationMessage(Player player) {
        player.sendSystemMessage(
                Component.literal("Upgrade purchased! Level: " + level)
                        .withStyle(ChatFormatting.GREEN)
        );
    }
}
