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

public class CurrencyDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<CurrencyData> CURRENCY_DATA = CapabilityManager.get(new CapabilityToken<CurrencyData>() {
    });

    private CurrencyData currencyData = null;
    private final LazyOptional<CurrencyData> optional = LazyOptional.of(this::getCurrencyData);

    private CurrencyData getCurrencyData() {
        if (this.currencyData == null) {
            this.currencyData = new CurrencyData();
        }
        return this.currencyData;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if(capability == CURRENCY_DATA) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        getCurrencyData().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        getCurrencyData().loadNBTData(compoundTag);
    }
}
