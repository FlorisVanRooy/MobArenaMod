package com.derp.derpymod.arena.permanentskilltree;

import com.derp.derpymod.arena.upgrades.LeveledUpgrade;
import com.derp.derpymod.util.AttributeModifierUtils;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import static com.derp.derpymod.util.AttributeModifierUUIDs.PERM_MAX_HEALTH_1;

public class MaxHealthUpgrade1 extends LeveledUpgrade {
    public static final String ID = "perm_max_health_1";

    public MaxHealthUpgrade1() {
        super(ID, 5, "+1 heart");
    }

    @Override
    protected void applyLevel(Player player, int level) {
        AttributeModifierUtils.applyModifier(
                player,
                Attributes.MAX_HEALTH,
                PERM_MAX_HEALTH_1,
                getDisplayName(),
                level * 2,
                AttributeModifier.Operation.ADDITION
        );
    }

    @Override
    public void reset(Player player) {
        // clear out any old armour modifier
        AttributeModifierUtils.removeModifier(
                player,
                Attributes.MAX_HEALTH,
                PERM_MAX_HEALTH_1
        );
        player.setHealth(20);
        super.reset(player);
    }

    @Override
    public int calculateCost(int level) {
        return 200 * level;
    }

    @Override
    public boolean isPermanent() {
        return true;
    }
}
