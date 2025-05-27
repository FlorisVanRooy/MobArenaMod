package com.derp.derpymod.arena.upgrades.leveledupgrades;

import com.derp.derpymod.arena.upgrades.LeveledUpgrade;
import com.derp.derpymod.util.SwordDamageUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SwordDamageUpgradeInfinite extends LeveledUpgrade {
    public static final String ID = "sword_damage_inf";

    public SwordDamageUpgradeInfinite() {
        super(ID, 999, "Upgrade sword damage");
    }

    @Override
    protected void applyLevel(Player player, int level) {
        System.out.println("Applying upgrade");
        // your damage-boost logic
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() == Items.WOODEN_SWORD) {
                SwordDamageUtils.removeAttackDamageModifier(stack);
                SwordDamageUtils.addAttackDamageModifier(stack, level + 4);
            }
        }
    }

    @Override
    public int calculateCost(int level) {
        return ((100 * (level)));

    }
}