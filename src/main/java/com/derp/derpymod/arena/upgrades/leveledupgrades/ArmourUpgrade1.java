package com.derp.derpymod.arena.upgrades.leveledupgrades;

import com.derp.derpymod.arena.upgrades.LeveledUpgrade;
import com.derp.derpymod.util.AttributeModifierUtils;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import static com.derp.derpymod.util.AttributeModifierUUIDs.PERM_ARMOUR_1;

public class ArmourUpgrade1 extends LeveledUpgrade {
    public static final String ID = "perm_armour_1";

    public ArmourUpgrade1() {
        super(ID, 5, "+1 armour");
    }

    @Override
    protected void applyLevel(Player player, int level) {
        // level == number of armour points to add
        AttributeModifierUtils.applyModifier(
                player,
                Attributes.ARMOR,
                PERM_ARMOUR_1,
                getDisplayName(),
                level,
                AttributeModifier.Operation.ADDITION
        );
    }

    @Override
    public void reset(Player player) {
        // clear out any old armour modifier
        AttributeModifierUtils.removeModifier(
                player,
                Attributes.ARMOR,
                PERM_ARMOUR_1
        );
        super.reset(player);
    }

    @Override
    public int calculateCost(int level) {
        return (200 * level);
    }

    @Override
    public boolean isPermanent() {
        return true;
    }
}
