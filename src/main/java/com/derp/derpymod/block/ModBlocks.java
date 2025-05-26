package com.derp.derpymod.block;

import com.derp.derpymod.DerpyMod;
import com.derp.derpymod.block.custom.PermanentSkillTreeBlock;
import com.derp.derpymod.block.custom.UpgradeTableBlock;
import com.derp.derpymod.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, DerpyMod.MODID);


    public static final RegistryObject<Block> EXAMPLE_BLOCK = registerBlock("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Block> UPGRADE_TABLE = registerBlock("upgrade_table", () -> new UpgradeTableBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> PERMANENT_SKILL_TREE = registerBlock("permanent_skill_tree", () -> new PermanentSkillTreeBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
