package com.derp.derpymod.arena.upgrades.leveledupgrades;

import com.derp.derpymod.arena.upgrades.LeveledUpgrade;
import com.derp.derpymod.util.SwordDamageUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.UUID;

public class MovementSpeedUpgradeInfinite extends LeveledUpgrade {
    public static final String ID = "movement_speed_inf";
    private static final UUID MOVEMENT_SPEED_MODIFIER_UUID = UUID.fromString("323e4567-e89b-12d3-a456-426614174000");

    public MovementSpeedUpgradeInfinite() {
        super(ID, 999);
    }

    @Override
    protected void applyLevel(Player player, int level) {
        addMovementSpeedModifier(player, 0.01 * level);
    }

    @Override
    public double calculateCost(int level) {
        return (100 * level);
    }

    private  void addMovementSpeedModifier(Player player, double amount) {
        var movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.removeModifier(MOVEMENT_SPEED_MODIFIER_UUID);
            movementSpeed.addPermanentModifier(new AttributeModifier(MOVEMENT_SPEED_MODIFIER_UUID, "Movement speed modifier", amount, AttributeModifier.Operation.ADDITION));
            System.out.println("Changed player movement speed");
        }
    }
}
