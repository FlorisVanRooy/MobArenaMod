package com.derp.derpymod.datagen;

import com.derp.derpymod.DerpyMod;
import com.derp.derpymod.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, DerpyMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // Generate block states and models
        blockWithItem(ModBlocks.EXAMPLE_BLOCK);
        simpleBlockWithItem(ModBlocks.UPGRADE_TABLE.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/upgrade_table")));
        simpleBlockWithItem(ModBlocks.PERMANENT_SKILL_TREE.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/permanent_skill_tree")));
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
