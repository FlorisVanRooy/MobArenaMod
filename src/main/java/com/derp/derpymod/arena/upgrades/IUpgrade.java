package com.derp.derpymod.arena.upgrades;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public interface IUpgrade {
    String getId();
    void apply(Player player);
    boolean purchase(Player player);
    void save(CompoundTag tag);
    void load(CompoundTag tag);
    default boolean isPermanent() { return false; }
    default void reset() { }
}
