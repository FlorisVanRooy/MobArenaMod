package com.derp.derpymod.arena.upgrades.leveledupgrades;

import com.derp.derpymod.arena.upgrades.LeveledUpgrade;
import com.derp.derpymod.util.AttributeModifierUtils;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import static com.derp.derpymod.util.AttributeModifierUUIDs.PERM_KNOCKBACK_RESISTANCE_1;

public class KnockbackResistanceUpgrade1 extends LeveledUpgrade {
    public static final String ID = "perm_knockback_resistance_1";

    public KnockbackResistanceUpgrade1() {
        super(ID, 4, "+5% knockback resistance");
    }

    @Override
    protected void applyLevel(Player player, int level) {
        // 5% per level
        double resistance = level * 0.05;

        AttributeModifierUtils.applyModifier(
                player,
                Attributes.KNOCKBACK_RESISTANCE,
                PERM_KNOCKBACK_RESISTANCE_1,
                getDisplayName(),
                resistance,
                AttributeModifier.Operation.ADDITION
        );
    }

    @Override
    public void reset(Player player) {
        AttributeModifierUtils.removeModifier(
                player,
                Attributes.KNOCKBACK_RESISTANCE,
                PERM_KNOCKBACK_RESISTANCE_1
        );
        super.reset(player);
    }

    @Override
    public int calculateCost(int level) {
        return 150 * level; // slightly cheaper than armor/health
    }

    @Override
    public boolean isPermanent() {
        return true;
    }
}

