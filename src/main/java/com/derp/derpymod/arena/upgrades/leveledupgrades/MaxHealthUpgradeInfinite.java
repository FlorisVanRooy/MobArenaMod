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

import static com.derp.derpymod.util.AttributeModifierUUIDs.INFINITE_MAX_HEALTH;

public class MaxHealthUpgradeInfinite extends LeveledUpgrade {
    public static final String ID = "max_health_inf";

    public MaxHealthUpgradeInfinite() {
        super(ID, 999, "Increase max health");
    }

    @Override
    protected void applyLevel(Player player, int level) {
        AttributeModifierUtils.applyModifier(
                player,
                Attributes.MAX_HEALTH,
                INFINITE_MAX_HEALTH,
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
                INFINITE_MAX_HEALTH
        );
        player.setHealth(20);
        super.reset(player);
    }

    @Override
    public int calculateCost(int level) {
        return (100 * level);
    }
}
