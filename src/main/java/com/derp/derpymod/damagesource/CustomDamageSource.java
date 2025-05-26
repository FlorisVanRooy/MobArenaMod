package com.derp.derpymod.damagesource;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class CustomDamageSource extends DamageSource {

    public CustomDamageSource(Holder<DamageType> damageType, Entity sourceEntity) {
        super(damageType, sourceEntity);
    }
}
