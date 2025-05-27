package com.derp.derpymod.arena.upgrades;

import com.derp.derpymod.capabilities.CurrencyDataProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public abstract class LeveledIUpgrade implements IUpgrade {
    private final String id;
    private final int maxLevel;
    private int level = 0;
    protected LeveledIUpgrade(String id, int maxLevel) {
        this.id = id;
        this.maxLevel = maxLevel;
    }

    @Override public String getId() { return id; }

    public boolean purchase(Player player) {
        if (!hasEnoughCurrency(player)) {
            player.sendSystemMessage(Component.literal("You don't have enough currency!")
                    .withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA));
            return false;
        }

        if (level >= maxLevel) {
            player.sendSystemMessage(Component.literal("You already have the maximum level!").withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_AQUA));
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
    public void reset() {
        level = 0;
    }


    @Override public void save(CompoundTag tag) { tag.putInt("level", level); }
    @Override public void load(CompoundTag tag) { level = tag.getInt("level"); }



    protected boolean hasEnoughCurrency(Player player) {
        return player.getCapability(CurrencyDataProvider.CURRENCY_DATA)
                .map(cd -> cd.getCurrency() >= calculateCost(level + 1))
                .orElse(false);
    }

    /**
     * Deduct the cost of the *next* level from their currency.
     */
    protected void payUpgrade(Player player) {
        player.getCapability(CurrencyDataProvider.CURRENCY_DATA).ifPresent(cd -> {
            cd.subtractCurrency((int) calculateCost(level));
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

    public abstract double calculateCost(int level);
}
