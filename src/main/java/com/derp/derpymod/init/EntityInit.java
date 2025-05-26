package com.derp.derpymod.init;

import com.derp.derpymod.DerpyMod;
import com.derp.derpymod.entities.StrongZombie;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityInit {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, DerpyMod.MODID);
    public static final RegistryObject<EntityType<StrongZombie>> STRONG_ZOMBIE = ENTITIES.register("strong_zombie",
            () -> EntityType.Builder.of(StrongZombie::new, MobCategory.MONSTER)
                    .build(new ResourceLocation(DerpyMod.MODID, "strong_zombie").toString()));
}
