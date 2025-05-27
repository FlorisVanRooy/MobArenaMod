package com.derp.derpymod.arena.upgrades.leveledupgrades;

import com.derp.derpymod.arena.upgrades.LeveledUpgrade;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class BowDamageUpgradeInfinite extends LeveledUpgrade {
    public static final String ID = "bow_damage_inf";

    public BowDamageUpgradeInfinite() {
        super(ID, 999, "Upgrade bow damage");
    }

    @Override
    protected void applyLevel(Player player, int level) {

    }

    @Override
    public int calculateCost(int level) {

        return (100 * level);
    }
}
