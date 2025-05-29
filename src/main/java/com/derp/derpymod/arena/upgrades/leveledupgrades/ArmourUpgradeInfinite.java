package com.derp.derpymod.arena.upgrades.leveledupgrades;

import com.derp.derpymod.arena.upgrades.LeveledUpgrade;
import com.derp.derpymod.util.AttributeModifierUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

import static com.derp.derpymod.util.AttributeModifierUUIDs.INFINITE_ARMOUR;

public class ArmourUpgradeInfinite extends LeveledUpgrade {
    public static final String ID = "armour_inf";

    public ArmourUpgradeInfinite() {
        super(ID, 999, "Upgrade armour");
    }

    @Override
    protected void applyLevel(Player player, int level) {
        // level == number of armour points to add
        AttributeModifierUtils.applyModifier(
                player,
                Attributes.ARMOR,
                INFINITE_ARMOUR,
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
                INFINITE_ARMOUR
        );
        super.reset(player);
    }

    @Override
    public int calculateCost(int level) {
        return (100 * level);
    }
}
