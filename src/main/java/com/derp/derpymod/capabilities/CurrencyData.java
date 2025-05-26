package com.derp.derpymod.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public class CurrencyData {
    double currency = 0;
    double permanentCurrency = 0;

    public CurrencyData() {

    }

    public double getCurrency() {
        return currency;
    }

    public double getPermanentCurrency() {
        return permanentCurrency;
    }

    public void setCurrency(double currency) {
        this.currency = currency;
    }

    public void setPermanentCurrency(double permanentCurrency) {
        this.permanentCurrency = permanentCurrency;
    }

    public void addCurrency(double currency) {
        this.currency += currency;
    }

    public void addPermanentCurrency(double permanentCurrency) {
        this.permanentCurrency += permanentCurrency;
    }

    public void subtractCurrency(double currency) {
        this.currency -= currency;
    }

    public void subtractPermanentCurrency(double permanentCurrency) {
        this.permanentCurrency -= permanentCurrency;
    }

    public void copyFrom(CurrencyData source) {
        this.currency = source.currency;
        this.permanentCurrency = source.permanentCurrency;
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putDouble("currency", currency);
        nbt.putDouble("permanentCurrency", permanentCurrency);
    }

    public void loadNBTData(CompoundTag nbt) {
        currency = nbt.getDouble("currency");
        permanentCurrency = nbt.getDouble("permanentCurrency");
    }
}
