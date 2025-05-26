package com.derp.derpymod.block.entity;

import com.derp.derpymod.DerpyMod;
import com.derp.derpymod.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, DerpyMod.MODID);

    public static final RegistryObject<BlockEntityType<UpgradeTableBlockEntity>> UPGRADE_TABLE = BLOCK_ENTITIES.register("upgrade_table",
            () -> BlockEntityType.Builder.of(UpgradeTableBlockEntity::new, ModBlocks.UPGRADE_TABLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<PermanentSkillTreeBlockEntity>> PERMANENT_SKILL_TREE = BLOCK_ENTITIES.register("permanent_skill_tree",
            () -> BlockEntityType.Builder.of(PermanentSkillTreeBlockEntity::new, ModBlocks.PERMANENT_SKILL_TREE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
