package com.derp.derpymod.arena.upgrades;

import com.derp.derpymod.arena.permanentskilltree.ArmourUpgrade1;
import com.derp.derpymod.arena.permanentskilltree.MaxHealthUpgrade1;
import com.derp.derpymod.arena.upgrades.leveledupgrades.*;
import com.derp.derpymod.arena.upgrades.onetimeupgrades.MinigunUnlock;

public class ModUpgrades {
    public static void init() {
        // Leveled upgrades
        UpgradeRegistry.register(SwordDamageUpgradeInfinite.ID, SwordDamageUpgradeInfinite::new);
        UpgradeRegistry.register(BowDamageUpgradeInfinite.ID, BowDamageUpgradeInfinite::new);
        UpgradeRegistry.register(MaxHealthUpgradeInfinite.ID, MaxHealthUpgradeInfinite::new);
        UpgradeRegistry.register(MovementSpeedUpgradeInfinite.ID, MovementSpeedUpgradeInfinite::new);
        UpgradeRegistry.register(ArmourUpgradeInfinite.ID, ArmourUpgradeInfinite::new);
        UpgradeRegistry.register(ArmourUpgrade1.ID, ArmourUpgrade1::new);
        UpgradeRegistry.register(MaxHealthUpgrade1.ID, MaxHealthUpgrade1::new);

        // One-time upgrades
        UpgradeRegistry.register(MinigunUnlock.ID, MinigunUnlock::new);
    }
}
