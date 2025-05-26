package com.derp.derpymod.capabilities;

import com.derp.derpymod.arena.normalupgrades.*;
import com.derp.derpymod.arena.Upgrade;
import com.derp.derpymod.arena.onetimebuyableupgrades.FlingingUpgrade;
import com.derp.derpymod.arena.onetimebuyableupgrades.MinigunUnlock;
import com.derp.derpymod.arena.permanentskilltree.ArmourUpgrade1;
import com.derp.derpymod.arena.permanentskilltree.MaxHealthUpgrade1;
import com.derp.derpymod.arena.permanentskilltree.MeleeDamageUpgrade1;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@AutoRegisterCapability
public class UpgradeData {

    public UpgradeData() {

    }

    private Map<String, Upgrade> upgrades = new HashMap<>();

    public Upgrade getUpgrade(String key) {
        return upgrades.get(key);
    }

    public Collection<Upgrade> getUpgrades() {
        return upgrades.values();
    }

    public void addUpgrade(String key, Upgrade upgrade) {
        upgrades.put(key, upgrade);
    }

    public void saveNBTData(CompoundTag nbt) {
        ListTag upgradeList = new ListTag();
        for (Map.Entry<String, Upgrade> entry : upgrades.entrySet()) {
            CompoundTag upgradeTag = new CompoundTag();
            upgradeTag.putString("Key", entry.getKey());
            CompoundTag upgradeData = new CompoundTag();
            entry.getValue().save(upgradeData);  // Ensure Upgrade class has save method
            upgradeTag.put("Data", upgradeData);
            upgradeList.add(upgradeTag);
        }
        nbt.put("Upgrades", upgradeList);
    }

    public void loadNBTData(CompoundTag nbt) {
        ListTag upgradeList = nbt.getList("Upgrades", CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < upgradeList.size(); i++) {
            CompoundTag upgradeTag = upgradeList.getCompound(i);
            String key = upgradeTag.getString("Key");
            CompoundTag upgradeData = upgradeTag.getCompound("Data");
            Upgrade upgrade = createUpgradeFromKey(key);
            if (upgrade != null) {
                upgrade.load(upgradeData);  // Ensure Upgrade class has load method
                upgrades.put(key, upgrade);
            }
        }
    }

    public void copyFrom(UpgradeData source) {
        this.upgrades = new HashMap<>(source.upgrades);
        System.out.println("UpgradeData copied: " + this.upgrades);
    }

    private Upgrade createUpgradeFromKey(String key) {
        return switch (key) {
            case "swordDamageUpgradeInfinite" -> new SwordDamageUpgradeInfinite();
            case "bowDamageUpgradeInfinite" -> new BowDamageUpgradeInfinite();
            case "armourUpgradeInfinite" -> new ArmourUpgradeInfinite();
            case "movementSpeedUpgradeInfinite" -> new MovementSpeedUpgradeInfinite();
            case "maxHealthUpgradeInfinite" -> new MaxHealthUpgradeInfinite();
            case "maxHealthUpgrade1" -> new MaxHealthUpgrade1();
            case "armourUpgrade1" -> new ArmourUpgrade1();
            case "meleeDamageUpgrade1" -> new MeleeDamageUpgrade1();
            case "flingingUpgrade" -> new FlingingUpgrade();
            case "minigunUnlock" -> new MinigunUnlock();
            default -> null;
        };
    }
}
