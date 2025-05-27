package com.derp.derpymod;

import com.derp.derpymod.arena.LossHandler;
import com.derp.derpymod.arena.Wave;
import com.derp.derpymod.arena.mobs.CustomEnemy;
import com.derp.derpymod.arena.upgrades.ModUpgrades;
import com.derp.derpymod.arena.upgrades.IUpgrade;
import com.derp.derpymod.arena.upgrades.UpgradeRegistry;
import com.derp.derpymod.block.ModBlocks;
import com.derp.derpymod.block.entity.ModBlockEntities;
import com.derp.derpymod.capabilities.*;
import com.derp.derpymod.client.models.StrongZombieModel;
import com.derp.derpymod.client.renderer.StrongZombieRenderer;
import com.derp.derpymod.dispatchers.RightClickDispatcher;
import com.derp.derpymod.entities.StrongZombie;
import com.derp.derpymod.init.EntityInit;
import com.derp.derpymod.item.ModCreativeModeTabs;
import com.derp.derpymod.item.ModItems;
import com.derp.derpymod.packets.SSyncDataPacket;
import com.derp.derpymod.network.PacketHandler;
import com.derp.derpymod.screen.ScreenType;
import com.derp.derpymod.savedata.CustomWorldData;
import com.derp.derpymod.screen.ModMenuTypes;
import com.derp.derpymod.util.AttributeModifierUtils;
import com.derp.derpymod.util.SwordDamageUtils;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.Map;
import java.util.UUID;

//import static com.derp.derpymod.arena.permanentskilltree.MaxHealthUpgrade1.MAX_HEALTH_MODIFIER1_UUID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DerpyMod.MODID)
public class DerpyMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "derpymod";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public DerpyMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        ModBlocks.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ModItems.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        ModCreativeModeTabs.register(modEventBus);
        EntityInit.ENTITIES.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);


        ModMenuTypes.register(modEventBus);
        ModBlockEntities.register(modEventBus);

        // register the mod upgrades
        ModUpgrades.init();

        // Register the item to a creative tab
//        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::commonSetup);
        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        event.enqueueWork(RightClickDispatcher::init);
        PacketHandler.register();

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide) {
            return; // Early exit for client-side logic
        }

        ServerLevel world = (ServerLevel) event.getLevel();
        BlockPos pos = event.getPos();
        BlockState blockState = world.getBlockState(pos);
        Player player = event.getEntity();

        // Handle Spawn Selector Wand logic
        if (event.getItemStack().getItem() == ModItems.SPAWN_SELECTOR_WAND.get()) {
            CustomWorldData data = CustomWorldData.get(world);
            Map<BlockPos, BlockState> originalBlockStates = data.getOriginalBlockStates();
            if (blockState.is(Blocks.BEDROCK)) {
                if (data.getOriginalBlockStates().containsKey(pos)) {
                    world.setBlock(pos, data.getOriginalBlockStates().get(pos), 3);
                    data.removeOriginalBlockState(pos);
                }
            } else {
                data.putOriginalBlockState(pos, blockState);
                world.setBlock(pos, Blocks.BEDROCK.defaultBlockState(), 3);
            }

            data.toggleSpawnPosition(pos);
            data.setDirty();

            // Set interaction result to SUCCESS and cancel event
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
            event.setCanceled(true);
            return; // Exit early after handling the custom logic
        }

        if (blockState.getBlock() == ModBlocks.UPGRADE_TABLE.get() ||
                blockState.getBlock() == ModBlocks.PERMANENT_SKILL_TREE.get()) {

            if (!(player instanceof ServerPlayer serverPlayer)) return;

            // 1) Prepare upgrade data NBT
            CompoundTag upgradesNBT = new CompoundTag();
            serverPlayer.getCapability(UpgradeDataProvider.UPGRADE_DATA)
                    .ifPresent(upData -> upData.saveNBTData(upgradesNBT));

            // 2) Prepare currency data NBT
            CompoundTag currencyNBT = new CompoundTag();
            serverPlayer.getCapability(CurrencyDataProvider.CURRENCY_DATA)
                    .ifPresent(cd -> cd.saveNBTData(currencyNBT));

            // 3) Determine which screen to open
            ScreenType screenType = (blockState.getBlock() == ModBlocks.UPGRADE_TABLE.get())
                    ? ScreenType.UPGRADE_TABLE
                    : ScreenType.PERMANENT_SKILL_TREE;

            // 4) Send one packet with both data + the desired GUI
            SSyncDataPacket packet = new SSyncDataPacket(upgradesNBT, currencyNBT, screenType);
            PacketHandler.sendToPlayer(packet, serverPlayer);

            // 5) Finish the interaction
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
        RightClickDispatcher.dispatch(event);
    }

    @SubscribeEvent
    public void onPlayerFallDamage(LivingFallEvent event) {
        if ((event.getEntity() instanceof Player)) {
            if (!event.getEntity().level().isClientSide) {
                Player player = (Player) event.getEntity();
                player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(playerData -> {
                    if (playerData.isFlinging()) {
                        event.setCanceled(true);
                        playerData.setFlinging(false);
                        LOGGER.info("Cancelled fall damage");
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!event.getEntity().level().isClientSide) {
            if (event.getEntity() instanceof Player player && !player.isOnFire()) {
                CustomWorldData worldData = CustomWorldData.get(player.level());
                // On player hurt
                if (worldData.getWaveMutation() == 2) {
                    player.setRemainingFireTicks(60);
                    player.hurtMarked = true;
                }
            } else {
                // Early exit if the damage source is not an arrow
                if (!(event.getSource().getDirectEntity() instanceof AbstractArrow arrow)) {
                    return;
                }

                CompoundTag tag = arrow.getPersistentData();
                if (!tag.contains("increaseDamageLevel")) return;

                int level = tag.getInt("increaseDamageLevel");
                if (level <= 0) return;

                // e.g. +1 damage per level
                float bonus = level;
                float newDamage = event.getAmount() + bonus;
                event.setAmount(newDamage);
            }
        }
    }

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        if (!event.getEntity().level().isClientSide) {
            if (event.getEntity() instanceof AbstractArrow arrow) {
                arrow.discard();
            }
        }
    }

    @SubscribeEvent
    public void onAttackWithSword(AttackEntityEvent event) {
//        Player player = event.getEntity();
//        if (!player.level().isClientSide && player.getMainHandItem().getItem() == Items.WOODEN_SWORD) {
//            player.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgradeData -> {
//                if (upgradeData.getUpgrade("flingingUpgradeInfinite").getLevel() == 1) {
//                    player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(playerData -> {
//                        player.addDeltaMovement(new Vec3(0, 2, 0));
//                        player.hurtMarked = true;
//                        playerData.setFlinging(true);
//                    });
//                }
//            });
//        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            Player oldPlayer = event.getOriginal();
            Player newPlayer = event.getEntity();
            oldPlayer.reviveCaps();

            oldPlayer.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(oldStore -> {
                newPlayer.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });

            oldPlayer.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(oldStore -> {
                newPlayer.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });

            oldPlayer.getCapability(CurrencyDataProvider.CURRENCY_DATA).ifPresent(oldStore -> {
                newPlayer.getCapability(CurrencyDataProvider.CURRENCY_DATA).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });

            oldPlayer.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(oldUp ->
                    newPlayer.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(newUp -> {
                        // 1) Copy the raw upgrade levels into the brand-new capability
                        newUp.copyFrom(oldUp);

                        // 2) Apply each upgrade onto the new player so their attributes get set
                        newUp.getUpgrades().forEach(up -> up.apply(newPlayer));
                    }));
        }
    }

    private static void reapplyMaxHealthModifier(Player player, double amount) {
        var maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
//            maxHealth.removeModifier(MAX_HEALTH_MODIFIER1_UUID);
            //maxHealth.addPermanentModifier(new AttributeModifier(MAX_HEALTH_MODIFIER1_UUID, "Max health modifier", amount, AttributeModifier.Operation.ADDITION));
            player.setHealth(player.getHealth());
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
                // Handle wave system
                LivingEntity entity = event.getEntity();
                if (!entity.level().isClientSide) {
                    Wave wave = new Wave();
                    if (wave.removeMob(entity, (ServerLevel) entity.level())) {
                        if (event.getSource().getEntity() instanceof ServerPlayer player) {
                            CustomEnemy.getMatchingEnemy(entity).ifPresent(enemy -> {
                                int currency = enemy.cost();

                                player.getCapability(CurrencyDataProvider.CURRENCY_DATA).ifPresent(currencyData -> {
                                    currencyData.addCurrency(currency);
                                    LOGGER.info("Added {} currency for killing {}", currency, entity.getType());
                                });
                            });
                        }
                    }
                }
        } else {
            // On player death
            Player player = (Player) event.getEntity();
            if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.setGameMode(GameType.SPECTATOR);
                ServerLevel serverLevel = (ServerLevel) serverPlayer.level();
                // Optionally disable interactions here
                LossHandler.getInstance().checkLoss(serverLevel);
            }
        }
    }

    @SubscribeEvent
    public void onCreeperExplosion(ExplosionEvent.Start event) {
        if (!event.getLevel().isClientSide) {
            Entity entity = event.getExplosion().getExploder();
            if (entity instanceof Creeper creeper) {
                Wave wave = new Wave();
                wave.removeMob(creeper, (ServerLevel) entity.level());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            // 1) Ensure the player has an instance of every registered upgrade
            serverPlayer.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(data -> {
                for (String id : UpgradeRegistry.ids()) {
                    if (data.getUpgrade(id) == null) {
                        data.addUpgrade(UpgradeRegistry.create(id));
                    }
                }

                // 2) Build the two NBT blobs
                CompoundTag upNBT = new CompoundTag();
                data.saveNBTData(upNBT);

                CompoundTag currencyNBT = new CompoundTag();
                serverPlayer.getCapability(CurrencyDataProvider.CURRENCY_DATA)
                        .ifPresent(cd -> cd.saveNBTData(currencyNBT));

                // 3) Send one sync packet (no GUI open—screenType = null)
                PacketHandler.sendToPlayer(
                        new SSyncDataPacket(upNBT, currencyNBT, null),
                        serverPlayer
                );
            });
        }
    }


    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }

        @SubscribeEvent
        public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EntityInit.STRONG_ZOMBIE.get(), StrongZombieRenderer::new);
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(StrongZombieModel.LAYER_LOCATION, StrongZombieModel::createBodyLayer);
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegisteringEntities {
        @SubscribeEvent
        public static void entityAttributes(EntityAttributeCreationEvent event) {
            event.put(EntityInit.STRONG_ZOMBIE.get(), StrongZombie.getStrongZombieAttributes().build());
        }
    }

    @Mod.EventBusSubscriber(modid = DerpyMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ModEventSubscriber {
        @SubscribeEvent
        public static void onAttachCapabilitiesPlayer(final AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player) {
                event.addCapability(new ResourceLocation(DerpyMod.MODID, "player_data"), new PlayerDataProvider());
                LOGGER.info("PlayerState Capabilities attached");
                event.addCapability(new ResourceLocation(DerpyMod.MODID, "currency"), new CurrencyDataProvider());
                LOGGER.info("Currency Capabilities attached");
                event.addCapability(new ResourceLocation(DerpyMod.MODID, "upgrades"), new UpgradeDataProvider());
                LOGGER.info("Upgrade Capabilities attached");
            }
        }
    }
}