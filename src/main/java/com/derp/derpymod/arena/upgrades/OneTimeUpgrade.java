package com.derp.derpymod.arena.upgrades;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public abstract class OneTimeUpgrade implements IUpgrade {
    private final String id;
    private boolean unlocked = false;
    protected OneTimeUpgrade(String id) { this.id = id; }

    @Override public String getId() { return id; }
    @Override public boolean purchase(Player player) {
        if (unlocked) return false;
        unlocked = true;
        return true;
    }
    @Override public void apply(Player p) {
        if (unlocked) applyUnlocked(p);
    }
    protected abstract void applyUnlocked(Player p);

    @Override public void save(CompoundTag tag) { tag.putBoolean("unlocked", unlocked); }
    @Override public void load(CompoundTag tag) { unlocked = tag.getBoolean("unlocked"); }

    @Override
    public void reset() {
        unlocked = false;
    }

    @Override
    public int getLevel() {
        return unlocked ? 1 : 0;
    }

}

