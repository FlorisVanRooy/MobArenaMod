package com.derp.derpymod.arena.normalupgrades;

import com.derp.derpymod.arena.Upgrade;
import com.derp.derpymod.arena.permanentskilltree.ArmourUpgrade1;
import com.derp.derpymod.arena.permanentskilltree.MaxHealthUpgrade1;
import com.derp.derpymod.packets.ScreenType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MaxHealthUpgradeInfinite extends Upgrade {
    private static final UUID MAX_HEALTH_MODIFIER_UUID = UUID.fromString("423e4567-e89b-12d3-a456-426614174000");

    public MaxHealthUpgradeInfinite() {
        super(138, 19);
        setId("maxHealthUpgradeInfinite");
        setName("Max Health Upgrade");
    }

    @Override
    public void executeUpgrade(Player player) {
        if (hasEnoughCurrency(player)) {
            payUpgrade(player);
            setLevel(getLevel() + 1);
            addMaxHealthModifier(player, 2 * getLevel());
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

    private  void addMaxHealthModifier(Player player, double amount) {
        var maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.removeModifier(MAX_HEALTH_MODIFIER_UUID);
            maxHealth.addPermanentModifier(new AttributeModifier(MAX_HEALTH_MODIFIER_UUID, "Max health modifier", amount, AttributeModifier.Operation.ADDITION));
            player.setHealth(player.getHealth());
        }
    }
}
