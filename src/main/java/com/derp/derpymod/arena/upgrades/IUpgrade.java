package com.derp.derpymod.arena.upgrades;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public interface IUpgrade {
    String getId();
    void apply(Player player);
    boolean purchase(Player player);
    void save(CompoundTag tag);
    void load(CompoundTag tag);
    int calculateCost(int level);
    default boolean isPermanent() { return false; }
    default void reset(Player player) { }
    default int getLevel() { return 0;}
    default String getDisplayName() { return ""; }
}
