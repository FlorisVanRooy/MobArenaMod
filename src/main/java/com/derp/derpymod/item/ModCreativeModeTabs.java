package com.derp.derpymod.item;

import com.derp.derpymod.DerpyMod;
import com.derp.derpymod.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DerpyMod.MODID);

    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab",
            () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .title(Component.translatable("creativetab.derpy_tab"))
                    .icon(() -> ModItems.STRONG_ZOMBIE_SPAWN_EGG.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
                        output.accept(ModItems.MINIGUN_ITEM.get());
                        output.accept(ModItems.STRONG_ZOMBIE_SPAWN_EGG.get());
                        output.accept(ModItems.SPAWN_SELECTOR_WAND.get());
                        output.accept(ModBlocks.EXAMPLE_BLOCK.get());
                        output.accept(ModBlocks.UPGRADE_TABLE.get());
                        output.accept(ModBlocks.PERMANENT_SKILL_TREE.get());
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
