package com.derp.derpymod.arena.normalupgrades;

import com.derp.derpymod.arena.Upgrade;
import com.derp.derpymod.packets.ScreenType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class MovementSpeedUpgradeInfinite extends Upgrade {
    private static final UUID MOVEMENT_SPEED_MODIFIER_UUID = UUID.fromString("323e4567-e89b-12d3-a456-426614174000");

    public MovementSpeedUpgradeInfinite() {
        super(78, 51);
        setId("movementSpeedUpgradeInfinite");
        setName("Movement Speed Upgrade");
    }

    @Override
    public void executeUpgrade(Player player) {
        if (hasEnoughCurrency(player)) {
            payUpgrade(player);
            setLevel(getLevel() + 1);
            addMovementSpeedModifier(player, 0.01 * getLevel());
            confirmationMessage(player);
            syncData((ServerPlayer) player);
        }
        else {
            player.sendSystemMessage(Component.literal("You don't have enough currency!").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA));
        }
    }

    @Override
    public double calculateCost() {
        return (100 + (100 * getLevel()));
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
