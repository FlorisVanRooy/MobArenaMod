package com.derp.derpymod.arena.upgrades;

import com.derp.derpymod.arena.upgrades.leveledupgrades.*;

public class ModUpgrades {
    public static void init() {
        // Leveled upgrades
        UpgradeRegistry.register(SwordDamageUpgradeInfinite.ID, SwordDamageUpgradeInfinite::new);
        UpgradeRegistry.register(BowDamageUpgradeInfinite.ID, BowDamageUpgradeInfinite::new);
        UpgradeRegistry.register(MaxHealthUpgradeInfinite.ID, MaxHealthUpgradeInfinite::new);
        UpgradeRegistry.register(MovementSpeedUpgradeInfinite.ID, MovementSpeedUpgradeInfinite::new);
        UpgradeRegistry.register(ArmourUpgradeInfinite.ID, ArmourUpgradeInfinite::new);

        // One-time upgrades
    }
}
