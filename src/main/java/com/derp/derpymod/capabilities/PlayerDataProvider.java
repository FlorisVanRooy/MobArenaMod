package com.derp.derpymod.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<PlayerData> PLAYER_DATA = CapabilityManager.get(new CapabilityToken<PlayerData>() {
    });

    private PlayerData playerData = null;
    private final LazyOptional<PlayerData> optional = LazyOptional.of(this::getPlayerData);

    private PlayerData getPlayerData() {
        if (this.playerData == null) {
            this.playerData = new PlayerData();
        }
        return this.playerData;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if(capability == PLAYER_DATA) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        getPlayerData().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        getPlayerData().loadNBTData(compoundTag);
    }
}
