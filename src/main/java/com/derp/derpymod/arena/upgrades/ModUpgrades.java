package com.derp.derpymod.arena.upgrades;

import com.derp.derpymod.arena.upgrades.leveledupgrades.SwordDamageUpgradeInfinite;

public class ModUpgrades {
    public static void init() {
        // Leveled upgrades
        UpgradeRegistry.register(SwordDamageUpgradeInfinite.ID, SwordDamageUpgradeInfinite::new);

        // One-time upgrades
    }
}
