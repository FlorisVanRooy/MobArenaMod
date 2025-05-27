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

public class ArmourUpgradeInfinite extends LeveledUpgrade {
    public static final String ID = "armour_inf";

    private static final UUID ARMOUR_MODIFIER_UUID = UUID.fromString("723e4567-e89b-12d3-a456-426614174000");


    public ArmourUpgradeInfinite() {
        super(ID, 999, "Upgrade armour");
    }

    @Override
    protected void applyLevel(Player player, int level) {
        // level == number of armour points to add
        AttributeModifierUtils.applyModifier(
                player,
                Attributes.ARMOR,
                ARMOUR_MODIFIER_UUID,
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
                ARMOUR_MODIFIER_UUID
        );
        super.reset(player);
    }

    @Override
    public int calculateCost(int level) {
        return (100 * level);
    }
}
