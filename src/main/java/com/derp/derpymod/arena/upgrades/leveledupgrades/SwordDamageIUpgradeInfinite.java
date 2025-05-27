package com.derp.derpymod.arena.upgrades.leveledupgrades;

import com.derp.derpymod.arena.upgrades.LeveledIUpgrade;
import com.derp.derpymod.util.SwordDamageUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SwordDamageIUpgradeInfinite extends LeveledIUpgrade {
    public static final String ID = "sword_damage_inf";

    public SwordDamageIUpgradeInfinite() {
        super(ID, 999);
        //setId("swordDamageUpgradeInfinite");
        //setName("Sword Damage Upgrade");
    }

    @Override
    protected void applyLevel(Player player, int level) {
        System.out.println("Applying upgrade");
        // your damage-boost logic
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() == Items.WOODEN_SWORD) {
                SwordDamageUtils.addAttackDamageModifier(stack, 1);
            }
        }
    }

    @Override
    public double calculateCost(int level) {
        return ((100 * (level)));

    }
}