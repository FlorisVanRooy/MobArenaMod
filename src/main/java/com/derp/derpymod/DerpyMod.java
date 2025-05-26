package com.derp.derpymod;

import com.derp.derpymod.arena.LossHandler;
import com.derp.derpymod.arena.Wave;
import com.derp.derpymod.arena.mutations.WaveMutation;
import com.derp.derpymod.arena.normalupgrades.*;
import com.derp.derpymod.arena.Upgrade;
import com.derp.derpymod.arena.onetimebuyableupgrades.FlingingUpgrade;
import com.derp.derpymod.arena.onetimebuyableupgrades.MinigunUnlock;
import com.derp.derpymod.arena.permanentskilltree.ArmourUpgrade1;
import com.derp.derpymod.arena.permanentskilltree.MaxHealthUpgrade1;
import com.derp.derpymod.arena.permanentskilltree.MeleeDamageUpgrade1;
import com.derp.derpymod.block.ModBlocks;
import com.derp.derpymod.block.entity.ModBlockEntities;
import com.derp.derpymod.capabilities.*;
import com.derp.derpymod.client.models.StrongZombieModel;
import com.derp.derpymod.client.renderer.StrongZombieRenderer;
import com.derp.derpymod.entities.StrongZombie;
import com.derp.derpymod.init.EntityInit;
import com.derp.derpymod.item.ModCreativeModeTabs;
import com.derp.derpymod.item.ModItems;
import com.derp.derpymod.packets.CSyncUpgradePacket;
import com.derp.derpymod.network.PacketHandler;
import com.derp.derpymod.packets.CurrencySyncPacket;
import com.derp.derpymod.packets.ScreenType;
import com.derp.derpymod.savedata.CustomWorldData;
import com.derp.derpymod.screen.ModMenuTypes;
import com.derp.derpymod.screen.PermanentSkillTreeScreen;
import com.derp.derpymod.util.AttributeModifierUtils;
import com.derp.derpymod.util.SwordDamageUtils;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.derp.derpymod.arena.permanentskilltree.MaxHealthUpgrade1.MAX_HEALTH_MODIFIER1_UUID;

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
//        ModDamageTypes.register(modEventBus);

        ModMenuTypes.register(modEventBus);
        ModBlockEntities.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
//        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::commonSetup);
        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
//        NetworkHandler.register();
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
                if (originalBlockStates.containsKey(pos)) {
                    world.setBlock(pos, originalBlockStates.get(pos), 3);
                    originalBlockStates.remove(pos);
                }
            } else {
                originalBlockStates.put(pos, blockState);
                world.setBlock(pos, Blocks.BEDROCK.defaultBlockState(), 3);
            }
            data.setOriginalBlockStates(originalBlockStates);
            Wave.getInstance().changeSpawnPositions(pos, player);
            data.setDirty();

            // Set interaction result to SUCCESS and cancel event
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
            event.setCanceled(true);
            return; // Exit early after handling the custom logic
        }

        // Handle Upgrade Table and Permanent Skill Tree logic
        if (blockState.getBlock() == ModBlocks.UPGRADE_TABLE.get() || blockState.getBlock() == ModBlocks.PERMANENT_SKILL_TREE.get()) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgradeData -> {
                    CompoundTag upgradeDataTag = new CompoundTag();
                    upgradeData.saveNBTData(upgradeDataTag);

                    ScreenType screenType = (blockState.getBlock() == ModBlocks.UPGRADE_TABLE.get())
                            ? ScreenType.UPGRADE_TABLE
                            : ScreenType.PERMANENT_SKILL_TREE;

                    CSyncUpgradePacket packet = new CSyncUpgradePacket(upgradeDataTag, screenType);
                    PacketHandler.sendToPlayer(packet, serverPlayer);
                });
            }

            // Handle Currency Sync logic
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.getCapability(CurrencyDataProvider.CURRENCY_DATA).ifPresent(currencyData -> {
                    CompoundTag currencyDataTag = new CompoundTag();
                    currencyData.saveNBTData(currencyDataTag);

                    CurrencySyncPacket packet = new CurrencySyncPacket(currencyDataTag);
                    PacketHandler.sendToPlayer(packet, serverPlayer);
                });
            }

            // Set interaction result to SUCCESS and cancel event
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
            event.setCanceled(true);
            return; // Exit early after handling the custom logic
        }

        // Allow normal block placement and interaction
        event.setCancellationResult(InteractionResult.PASS);
        event.setCanceled(false);
    }

    private static final long COOLDOWN_MILLIS = 100; // 0.1 seconds cooldown
    private static long lastRightClickTime = 0;

    @SubscribeEvent
    public void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        Level world = event.getLevel();
        Item clickedItem = event.getItemStack().getItem();
        if (!event.getLevel().isClientSide && !player.level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) world;
            // if item clicked is stick
            if (clickedItem == Items.STICK && Wave.getInstance().areAllMobsDead(event.getLevel())) {
                Wave wave = Wave.getInstance();
                wave.startWave(serverLevel);
            }

            if (clickedItem == Items.GOLDEN_SHOVEL) {
                Wave.getInstance().resetWaves(serverLevel);
            }


            // if clicked item is a minigun
            else if (clickedItem == ModItems.MINIGUN_ITEM.get()) {
                long currentTime = System.currentTimeMillis();

                // Check cooldown
                if (currentTime - lastRightClickTime < COOLDOWN_MILLIS) {
                    LOGGER.info("Minigun right-click on cooldown...");
                    return;
                }
                double distanceInFront = 1.5;
                double motionMultiplier = 0.75;
                double yaw = Math.toRadians(player.getYRot());
                double pitch = Math.toRadians(player.getXRot());
                // Calculate unit vectors for direction
                double directionX = -Math.sin(yaw) * Math.cos(pitch);
                double directionY = -Math.sin(pitch);
                double directionZ = Math.cos(yaw) * Math.cos(pitch);
                double spawnX = player.getX() + directionX * distanceInFront;
                double spawnY = player.getY() + directionY * distanceInFront;
                double spawnZ = player.getZ() + directionZ * distanceInFront;

                Arrow arrow = new Arrow(world, player);
                arrow.setDeltaMovement(directionX * motionMultiplier, directionY * motionMultiplier, directionZ * motionMultiplier);
                arrow.setPos(spawnX, spawnY + 1.5, spawnZ);
                arrow.setBaseDamage(1.5);
                arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;

                world.addFreshEntity(arrow);
                lastRightClickTime = currentTime;
            }

            // if clicked item is an amethyst shard
            else if (event.getItemStack().getItem() == Items.AMETHYST_SHARD) {
                player.getCapability(CurrencyDataProvider.CURRENCY_DATA).ifPresent(currencyData -> {
                    currencyData.addCurrency(100);
                    currencyData.addPermanentCurrency(100);
                    player.sendSystemMessage(Component.literal("You have " + currencyData.getCurrency() + " monez right now!"));
                    player.sendSystemMessage(Component.literal("You have " + currencyData.getPermanentCurrency() + " permanent monez right now!!"));
                });
            }

            // if clicked item is a blaze rod
            else if (event.getItemStack().getItem() == Items.BLAZE_ROD) {
                System.out.println("MaxHealth1 upgrade launched in main file");
                player.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgradeData -> {
                    String upgradeKey = "maxHealthUpgrade1";
                    if (upgradeData.getUpgrade(upgradeKey) == null) {
                        MaxHealthUpgrade1 maxHealthUpgrade1 = new MaxHealthUpgrade1();
                        upgradeData.addUpgrade(upgradeKey, maxHealthUpgrade1);
                    }
                    upgradeData.getUpgrade(upgradeKey).executeUpgrade(player);
                });
            }
            // if clicked item is a blaze rod
            else if (event.getItemStack().getItem() == Items.CARROT_ON_A_STICK) {
                System.out.println("Armour1 upgrade launched in main file");
                player.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgradeData -> {
                    String upgradeKey = "armourUpgrade1";
                    if (upgradeData.getUpgrade(upgradeKey) == null) {
                        ArmourUpgrade1 armourUpgrade1 = new ArmourUpgrade1();
                        upgradeData.addUpgrade(upgradeKey, armourUpgrade1);
                    }
                    upgradeData.getUpgrade(upgradeKey).executeUpgrade(player);
                });
            }


            // if clicked item is a compass
            else if (event.getItemStack().getItem() == Items.COMPASS) {
                System.out.println("Minigun unlock launched in main file");
                player.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgradeData -> {
                    upgradeData.getUpgrade("minigunUnlock").executeUpgrade(player);
                });
            }

            // if clicked item is a wooden sword
            else if (event.getItemStack().getItem() == Items.WOODEN_SWORD) {
                player.getCapability(CurrencyDataProvider.CURRENCY_DATA).ifPresent(currencyData -> {
                    player.sendSystemMessage(Component.literal("You have " + currencyData.getCurrency() + " monez right now!"));
                });
            } else if (event.getItemStack().getItem() == Items.FERMENTED_SPIDER_EYE) {
                player.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgradeData -> {
                    for (Upgrade upgrade : upgradeData.getUpgrades()) {
                        upgrade.resetUpgrade(player);
                    }
                });
                // reset sword damage
                for (ItemStack stack : player.getInventory().items) {
                    if (stack.getItem() == Items.WOODEN_SWORD) {
                        SwordDamageUtils.resetDamage(stack);
                    }
                }

            } else if (event.getItemStack().getItem() == Items.ARROW) {
                // give starting sword
                ItemStack sword = Items.WOODEN_SWORD.getDefaultInstance();

                CompoundTag tag = sword.getOrCreateTag();
                tag.putBoolean("Unbreakable", true);

                SwordDamageUtils.addAttackDamageModifier(sword, 4);

                // give attack damage
//                CompoundTag modifierTag = new CompoundTag();
//                modifierTag.putString("AttributeName", "generic.attack_damage");
//                modifierTag.putDouble("Amount", 4);
//                modifierTag.putInt("Operation", AttributeModifier.Operation.ADDITION.toValue());
//                modifierTag.putUUID("UUID", UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
//                modifierTag.putString("Name", "generic.attack_damage");
//                modifierTag.putString("Slot", "mainhand");
//
//                ListTag modifiers = sword.getOrCreateTag().getList("AttributeModifiers", Tag.TAG_COMPOUND);
//                modifiers.add(modifierTag);
//                sword.getOrCreateTag().put("AttributeModifiers", modifiers); // Update the item stack's tag
//                System.out.println("Added modifier: " + modifierTag);
                player.getInventory().placeItemBackInInventory(sword);


                // give starting bow
                ItemStack bow = Items.BOW.getDefaultInstance();
                tag = bow.getOrCreateTag();
                tag.putBoolean("Unbreakable", true);
                bow.enchant(Enchantments.INFINITY_ARROWS, 1);
                player.getInventory().placeItemBackInInventory(bow);


                // alternative armour
                var armour = player.getAttribute(Attributes.ARMOR);
                UUID armourModifierId = UUID.fromString("223e4567-e89b-12d3-a456-426614174000");
                if (armour != null) {
                    armour.removeModifier(armourModifierId);
                    armour.addPermanentModifier(new AttributeModifier(armourModifierId, "Armour modifier", 5, AttributeModifier.Operation.ADDITION));
                    player.getPersistentData().putDouble("baseArmour", 5);
                    System.out.println("Changed player armour");
                }

//                // give starting armour
//                ItemStack helmet = Items.CHAINMAIL_HELMET.getDefaultInstance();
//
//                tag = helmet.getOrCreateTag();
//                tag.putBoolean("Unbreakable", true);
//
//                modifierTag = new CompoundTag();
//                modifierTag.putString("AttributeName", "generic.armor");
//                modifierTag.putDouble("Amount", 0);
//                modifierTag.putInt("Operation", AttributeModifier.Operation.ADDITION.toValue());
//                modifierTag.putUUID("UUID", UUID.fromString("INSERT UUID STRING"));
//                modifierTag.putString("Name", "generic.armor");
//                modifierTag.putString("Slot", "head");
//
//                modifiers = helmet.getOrCreateTag().getList("AttributeModifiers", Tag.TAG_COMPOUND);
//                modifiers.add(modifierTag);
//                helmet.getOrCreateTag().put("AttributeModifiers", modifiers); // Update the item stack's tag
//                System.out.println("Added modifier: " + modifierTag);
//                player.getInventory().placeItemBackInInventory(helmet);
            }
        }
    }

    //TODO om een aantal ronden een special wave met special effect maken: silverfish on jump.
    @SubscribeEvent
    public void onPlayerJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            if (Wave.getInstance().getWaveMutation() == 1) {
                WaveMutation.getInstance().summonSilverfish(player);
            }
        }

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
                // On player hurt
                if (Wave.getInstance().getWaveMutation() == 2) {
                    player.setRemainingFireTicks(60);
                    player.hurtMarked = true;
                }
            } else {
                // Early exit if the damage source is not an arrow
                if (!(event.getSource().getDirectEntity() instanceof AbstractArrow arrow)) {
                    return;
                }

                // Check if the shooter is a player
                if (arrow.getOwner() instanceof Player player) {
                    System.out.println("Detected");
                    player.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgradeData -> {
                        var multiplier = upgradeData.getUpgrade("bowDamageUpgradeInfinite").getLevel() + 1;
                        float newDamage = (event.getAmount() + multiplier - 1);
                        System.out.println("Modified arrow damage to " + newDamage + " because of multiplier: " + multiplier + " and normal damage : " + event.getAmount());
                        event.setAmount(newDamage);
                    });
                }
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
        Player player = event.getEntity();
        if (!player.level().isClientSide && player.getMainHandItem().getItem() == Items.WOODEN_SWORD) {
            player.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgradeData -> {
                if (upgradeData.getUpgrade("flingingUpgradeInfinite").getLevel() == 1) {
                    player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(playerData -> {
                        player.addDeltaMovement(new Vec3(0, 2, 0));
                        player.hurtMarked = true;
                        playerData.setFlinging(true);
                    });
                }
            });
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            Player oldPlayer = event.getOriginal();
            Player newPlayer = event.getEntity();
            oldPlayer.reviveCaps();
            var debug = oldPlayer.getCapability(UpgradeDataProvider.UPGRADE_DATA);
            var debug2 = newPlayer.getCapability(UpgradeDataProvider.UPGRADE_DATA);

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

            if (oldPlayer.getPersistentData().contains("customMaxHealth1")) {
                double maxHealthAmount = oldPlayer.getPersistentData().getDouble("customMaxHealth1");
                AttributeModifierUtils.addModifier(newPlayer, Attributes.MAX_HEALTH, UUID.fromString("523e4567-e89b-12d3-a456-426614174000"), "Max health modifier", maxHealthAmount, "customMaxHealth");
            }

            // Reapply armour modifier
            if (oldPlayer.getPersistentData().contains("customArmour1")) {
                double armourAmount = oldPlayer.getPersistentData().getDouble("customArmour1");
                AttributeModifierUtils.addModifier(newPlayer, Attributes.ARMOR, UUID.fromString("623e4567-e89b-12d3-a456-426614174000"), "Armour modifier", armourAmount, "customArmour1");
            }

            // Reapply armour modifier
            if (oldPlayer.getPersistentData().contains("baseArmour")) {
                AttributeModifierUtils.addModifier(newPlayer, Attributes.ARMOR, UUID.fromString("223e4567-e89b-12d3-a456-426614174000"), "Armour modifier", 5, "baseArmour");
            }
        }
    }

    private static void reapplyMaxHealthModifier(Player player, double amount) {
        var maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
//            maxHealth.removeModifier(MAX_HEALTH_MODIFIER1_UUID);
            maxHealth.addPermanentModifier(new AttributeModifier(MAX_HEALTH_MODIFIER1_UUID, "Max health modifier", amount, AttributeModifier.Operation.ADDITION));
            player.setHealth(player.getHealth());
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            if (!(event.getEntity() instanceof Silverfish)) {
                // On mob death / kill
                if (event.getSource().getEntity() instanceof Player) {
                    Player player = (Player) event.getSource().getEntity();
                    if (!player.level().isClientSide) {
                        player.getCapability(CurrencyDataProvider.CURRENCY_DATA).ifPresent(currencyData -> {
                            LivingEntity entity = event.getEntity();
                            Integer currency = Wave.getInstance().getEnemyCost(entity.getType());

                            if (currency != null) {
                                currencyData.addCurrency(currency);
                                LOGGER.info("Added {} currency for killing {}", currency, entity.getType());
                            }
                        });
                    }
                }
                // Handle wave system
                LivingEntity entity = event.getEntity();
                if (!entity.level().isClientSide) {
                    if (!(entity instanceof Player)) {
                        Wave.getInstance().removeMob(entity, entity.level());
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
                Wave.getInstance().removeMob(creeper, entity.level());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide) {
            event.getEntity().getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgradeData -> {
                Map<String, Upgrade> upgrades = new HashMap<>();
                upgrades.put("bowDamageUpgradeInfinite", new BowDamageUpgradeInfinite());
                upgrades.put("swordDamageUpgradeInfinite", new SwordDamageUpgradeInfinite());
                upgrades.put("armourUpgradeInfinite", new ArmourUpgradeInfinite());
                upgrades.put("maxHealthUpgradeInfinite", new MaxHealthUpgradeInfinite());
                upgrades.put("movementSpeedUpgradeInfinite", new MovementSpeedUpgradeInfinite());
                upgrades.put("maxHealthUpgrade1", new MaxHealthUpgrade1());
                upgrades.put("armourUpgrade1", new MaxHealthUpgrade1());
                upgrades.put("meleeDamageUpgrade1", new MeleeDamageUpgrade1());
                upgrades.put("flingingUpgradeInfinite", new FlingingUpgrade());
                upgrades.put("minigunUnlockInfinite", new MinigunUnlock());

                upgrades.forEach((key, upgrade) -> {
                    if (upgradeData.getUpgrade(key) == null) {
                        upgradeData.addUpgrade(key, upgrade);
                    }
                });
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