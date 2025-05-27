package com.derp.derpymod.arena.upgrades;

import com.derp.derpymod.arena.upgrades.leveledupgrades.SwordDamageIUpgradeInfinite;

public class ModUpgrades {
    public static void init() {
        // Leveled upgrades
        UpgradeRegistry.register(SwordDamageIUpgradeInfinite.ID, SwordDamageIUpgradeInfinite::new);

        // One-time upgrades
    }
}
