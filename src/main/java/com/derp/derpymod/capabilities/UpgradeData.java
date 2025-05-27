package com.derp.derpymod.capabilities;

import com.derp.derpymod.arena.upgrades.IUpgrade;
import com.derp.derpymod.arena.upgrades.UpgradeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@AutoRegisterCapability
public class UpgradeData {
    // Map of all loaded upgrades for this player
    private final Map<String, IUpgrade> upgrades = new HashMap<>();

    public IUpgrade getUpgrade(String id) {
        return upgrades.get(id);
    }
    public Collection<IUpgrade> getUpgrades() {
        return upgrades.values();
    }
    public void addUpgrade(IUpgrade u) {
        upgrades.put(u.getId(), u);
    }

    // --- SERIALIZATION ---
    public void saveNBTData(CompoundTag nbt) {
        ListTag list = new ListTag();
        for (IUpgrade u : upgrades.values()) {
            CompoundTag tag = new CompoundTag();
            tag.putString("id", u.getId());
            u.save(tag);
            list.add(tag);
        }
        nbt.put("Upgrades", list);
    }

    public void loadNBTData(CompoundTag nbt) {
        ListTag list = nbt.getList("Upgrades", Tag.TAG_COMPOUND);
        upgrades.clear();
        for (Tag raw : list) {
            CompoundTag tag = (CompoundTag) raw;
            String id = tag.getString("id");
            IUpgrade u = UpgradeRegistry.create(id);
            if (u != null) {
                u.load(tag);
                upgrades.put(id, u);
            } else {
                // optionally log a warning: unknown id
            }
        }
    }

    // Copy (e.g. for player clone)
    public void copyFrom(UpgradeData src) {
        this.upgrades.clear();
        // create fresh instances to avoid shared state:
        for (IUpgrade u0 : src.upgrades.values()) {
            IUpgrade u = UpgradeRegistry.create(u0.getId());
            if (u != null) {
                // copy their data via NBT
                CompoundTag tmp = new CompoundTag();
                u0.save(tmp);
                u.load(tmp);
                this.upgrades.put(u.getId(), u);
            }
        }
    }
}

