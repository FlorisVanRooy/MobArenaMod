package com.derp.derpymod.dispatchers;

import com.derp.derpymod.arena.Wave;
import com.derp.derpymod.arena.upgrades.IUpgrade;
import com.derp.derpymod.capabilities.CurrencyDataProvider;
import com.derp.derpymod.capabilities.UpgradeDataProvider;
import com.derp.derpymod.handlers.IRightClickHandler;
import com.derp.derpymod.item.ModItems;
import com.derp.derpymod.network.PacketHandler;
import com.derp.derpymod.packets.SSyncDataPacket;
import com.derp.derpymod.savedata.CustomWorldData;
import com.derp.derpymod.util.SwordDamageUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RightClickDispatcher {
    private static final Map<Item, IRightClickHandler> MAP = new HashMap<>();

    public static void register(Item item, IRightClickHandler handler) {
        MAP.put(item, handler);
    }

    private static final long COOLDOWN_MILLIS = 100; // 0.1 seconds cooldown
    private static long lastRightClickTime = 0;

    public static void init() {
        // wave start (stick)
        register(Items.STICK, (player, world, event) -> {
            CustomWorldData worldData = CustomWorldData.get(world);
            if (worldData.isAllMobsDead()) {
                Wave wave = new Wave();
                if (!world.isClientSide) {
                    wave.startWave((ServerLevel) world);
                }
                return true;
            }
            return false;
        });

        // wave reset (golden shovel)
        register(Items.GOLDEN_SHOVEL, (player, world, event) -> {
            CustomWorldData worldData = CustomWorldData.get(world);
            worldData.resetWaves();
            return true;
        });

        // minigun fire
        register(ModItems.MINIGUN_ITEM.get(), (player, world, event) -> {
            long currentTime = System.currentTimeMillis();

            // Check cooldown
            if (currentTime - lastRightClickTime < COOLDOWN_MILLIS) {
                return false;
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
            return true;
        });

        // grant currency (amethyst shard)
        register(Items.AMETHYST_SHARD, (player, world, event) -> {
            player.getCapability(CurrencyDataProvider.CURRENCY_DATA).ifPresent(cd -> {
                cd.addCurrency(100);
                cd.addPermanentCurrency(100);
                player.sendSystemMessage(Component.literal(
                        "You have " + cd.getCurrency() + " coins (+" + cd.getPermanentCurrency() + " perm)!"));
            });
            return true;
        });

        // purchase sword damage on minecart
        register(Items.MINECART, (player, world, event) -> {
            player.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(data -> {
                // 2) Find the sword damage upgrade by its ID
                IUpgrade swordUp = data.getUpgrade("sword_damage_inf");
                System.out.println("Sword upgrade: "+swordUp);
                if (swordUp != null && swordUp.purchase(player)) {
                    // 3) Apply the new level’s effect immediately
                    swordUp.apply(player);

                    // 4) Sync EVERYTHING back to the client in one packet
                    CompoundTag upNBT = new CompoundTag();
                    data.saveNBTData(upNBT);

                    CompoundTag currNBT = new CompoundTag();
                    player.getCapability(CurrencyDataProvider.CURRENCY_DATA)
                            .ifPresent(cd -> cd.saveNBTData(currNBT));

                    PacketHandler.sendToPlayer(
                            new SSyncDataPacket(upNBT, currNBT, /* no GUI */ null),
                            (ServerPlayer) player
                    );
                }
            });
            return true;
        });

        register(Items.BLAZE_ROD, (player, world, event) -> {
//          player.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgradeData -> {
//                    String upgradeKey = "maxHealthUpgrade1";
//                    if (upgradeData.getUpgrade(upgradeKey) == null) {
//                        MaxHealthUpgrade1 maxHealthUpgrade1 = new MaxHealthUpgrade1();
//                        upgradeData.addUpgrade(upgradeKey, maxHealthUpgrade1);
//                    }
//                    upgradeData.getUpgrade(upgradeKey).executeUpgrade(player);
//                });
            return true;
        });

        register(Items.CARROT_ON_A_STICK, (player, world, event) -> {
//                player.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgradeData -> {
//                    String upgradeKey = "armourUpgrade1";
//                    if (upgradeData.getUpgrade(upgradeKey) == null) {
//                        ArmourUpgrade1 armourUpgrade1 = new ArmourUpgrade1();
//                        upgradeData.addUpgrade(upgradeKey, armourUpgrade1);
//                    }
//                    upgradeData.getUpgrade(upgradeKey).executeUpgrade(player);
//                });
            return true;
        });

        register(Items.COMPASS, (player, world, event) -> {
//                player.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgradeData -> {
//                    upgradeData.getUpgrade("minigunUnlock").executeUpgrade(player);
//                });
            return true;
        });

        register(Items.WOODEN_SWORD, (player, world, event) -> {
            player.getCapability(CurrencyDataProvider.CURRENCY_DATA).ifPresent(currencyData -> {
                player.sendSystemMessage(Component.literal("You have " + currencyData.getCurrency() + " monez right now!"));
            });
            return true;
        });

        register(Items.FERMENTED_SPIDER_EYE, (player, world, event) -> {
                player.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgradeData -> {
                    for (IUpgrade upgrade : upgradeData.getUpgrades()) {
                        if (upgrade.getId() == "minigun_unlock") {
                            System.out.println("Logging minigun unlock " + upgrade);
                        }
                        upgrade.reset(player);
                    }
                });
            for (ItemStack stack : player.getInventory().items) {
                if (stack.getItem() == Items.WOODEN_SWORD) {
                    SwordDamageUtils.resetDamage(stack);
                }
            }
            return true;
        });

        register(Items.ARROW, (player, world, event) -> {
            // give starting sword
            ItemStack sword = Items.WOODEN_SWORD.getDefaultInstance();

            CompoundTag tag = sword.getOrCreateTag();
            tag.putBoolean("Unbreakable", true);

            SwordDamageUtils.addAttackDamageModifier(sword, 4);

            // give attack damage
            CompoundTag modifierTag = new CompoundTag();
            modifierTag.putString("AttributeName", "generic.attack_damage");
            modifierTag.putDouble("Amount", 4);
            modifierTag.putInt("Operation", AttributeModifier.Operation.ADDITION.toValue());
            modifierTag.putUUID("UUID", UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
            modifierTag.putString("Name", "generic.attack_damage");
            modifierTag.putString("Slot", "mainhand");

            ListTag modifiers = sword.getOrCreateTag().getList("AttributeModifiers", Tag.TAG_COMPOUND);
            modifiers.add(modifierTag);
            sword.getOrCreateTag().put("AttributeModifiers", modifiers); // Update the item stack's tag
            System.out.println("Added modifier: " + modifierTag);
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
            return true;
        });

        // …and so on for blaze rod, compass, wooden sword, fermented spider eye, arrow, etc…
    }

    public static void dispatch(PlayerInteractEvent.RightClickItem event) {
        if (event.getLevel().isClientSide()) return;
        Player p = event.getEntity();
        if (!(p instanceof ServerPlayer player)) return;

        Item clicked = event.getItemStack().getItem();
        IRightClickHandler handler = MAP.get(clicked);
        if (handler != null && handler.handle(player, player.level(), event)) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.sidedSuccess(false));
        }
    }
}
