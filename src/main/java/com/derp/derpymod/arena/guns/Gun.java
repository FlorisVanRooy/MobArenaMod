package com.derp.derpymod.arena.guns;

import com.derp.derpymod.damagesource.CustomDamageSource;
import com.derp.derpymod.damagesource.ModDamageTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class Gun {
    private float damage;
    private double range;

    public void shoot(Player player, Level level) {
        HitResult hitResult = RayTraceUtils.rayTrace(player, level, range);

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity target = entityHitResult.getEntity();
//            CustomDamageSource gunDamage = new CustomDamageSource(Holder.direct(ModDamageTypes.GUN_DAMAGE.get()), player);
//            target.hurt(gunDamage, damage);
        }
    }

    public Gun(float damage, double range) {
        this.damage = damage;
        this.range = range;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }
}
