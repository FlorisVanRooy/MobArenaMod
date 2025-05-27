package com.derp.derpymod.arena.upgrades.leveledupgrades;

import com.derp.derpymod.arena.upgrades.LeveledUpgrade;
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
        super(ID, 999);
    }

    @Override
    protected void applyLevel(Player player, int level) {
        addArmourModifier(player, level);
    }

    @Override
    public double calculateCost(int level) {
        return (100 * level);
    }

    private void addArmourModifier(Player player, double amount) {
        var armour = player.getAttribute(Attributes.ARMOR);
        if (armour != null) {
            armour.removeModifier(ARMOUR_MODIFIER_UUID);
            armour.addPermanentModifier(new AttributeModifier(ARMOUR_MODIFIER_UUID, "Armour modifier", amount, AttributeModifier.Operation.ADDITION));
            System.out.println("Changed player armour");
        }
    }
}
