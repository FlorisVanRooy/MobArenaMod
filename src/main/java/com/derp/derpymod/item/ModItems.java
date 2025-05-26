package com.derp.derpymod.item;

import com.derp.derpymod.DerpyMod;
import com.derp.derpymod.init.EntityInit;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, DerpyMod.MODID);

    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build())));
    public static final RegistryObject<Item> MINIGUN_ITEM = ITEMS.register("minigun", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> STRONG_ZOMBIE_SPAWN_EGG = ITEMS.register("strong_zombie_spawn_egg", () -> new ForgeSpawnEggItem(EntityInit.STRONG_ZOMBIE, 0x88FFAA, 0x554466, new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> SPAWN_SELECTOR_WAND = ITEMS.register("spawn_selector_wand", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
