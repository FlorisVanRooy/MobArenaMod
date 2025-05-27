package com.derp.derpymod.arena.upgrades.leveledupgrades;

import com.derp.derpymod.arena.upgrades.LeveledUpgrade;
import com.derp.derpymod.util.AttributeModifierUtils;
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
        super(ID, 999, "Increase max health");
    }

    @Override
    protected void applyLevel(Player player, int level) {
        AttributeModifierUtils.applyModifier(
                player,
                Attributes.MAX_HEALTH,
                MAX_HEALTH_MODIFIER_UUID,
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
                MAX_HEALTH_MODIFIER_UUID
        );
        player.setHealth(20);
        super.reset(player);
    }

    @Override
    public int calculateCost(int level) {
        return (100 * level);
    }
}
