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

public class MaxHealthUpgradeInfinite extends LeveledUpgrade {
    public static final String ID = "max_health_inf";
    private static final UUID MAX_HEALTH_MODIFIER_UUID = UUID.fromString("423e4567-e89b-12d3-a456-426614174000");

    public MaxHealthUpgradeInfinite() {
        super(ID, 999);
    }

    @Override
    protected void applyLevel(Player player, int level) {
        addMaxHealthModifier(player, 2 * level);
    }

    @Override
    public double calculateCost(int level) {
        return (100 * level);
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
