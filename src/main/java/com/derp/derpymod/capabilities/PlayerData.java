package com.derp.derpymod.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public class PlayerData {
    private boolean flinged = false;

    public PlayerData() {

    }

    public boolean isFlinging() {
        return flinged;
    }

    public void setFlinging(boolean flinging) {
        this.flinged = flinging;
    }

    public void copyFrom(PlayerData source) {
        this.flinged = source.flinged;
        System.out.println("PlayerData copied: " + this.flinged);
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putBoolean("flinged", flinged);
    }

    public void loadNBTData(CompoundTag nbt) {
        flinged = nbt.getBoolean("flinged");
    }
}
