package com.derp.derpymod.damagesource;

import com.derp.derpymod.DerpyMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

public class ModDamageTypes {
    public static final DeferredRegister<DamageType> DAMAGE_TYPES = DeferredRegister.create(Registries.DAMAGE_TYPE, DerpyMod.MODID);

//    public static final RegistryObject<DamageType> GUN_DAMAGE = DAMAGE_TYPES.register("gun_damage",
//            () -> new DamageType("gun_damage", 0));  // Customize the damage type as needed

    public static void register(IEventBus event) {
        DAMAGE_TYPES.register(event);
    }
}
