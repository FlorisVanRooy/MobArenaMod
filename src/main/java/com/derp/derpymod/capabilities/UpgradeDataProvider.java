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

public class UpgradeDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<UpgradeData> UPGRADE_DATA = CapabilityManager.get(new CapabilityToken<UpgradeData>() {
    });

    private UpgradeData upgradeData = null;
    private final LazyOptional<UpgradeData> optional = LazyOptional.of(this::getUpgradeData);

    public UpgradeData getUpgradeData() {
        if (this.upgradeData == null) {
            this.upgradeData = new UpgradeData();
        }
        return this.upgradeData;
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if(capability == UPGRADE_DATA) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        getUpgradeData().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        getUpgradeData().loadNBTData(compoundTag);
    }

    public void invalidate() {
        optional.invalidate();
    }
}
