package com.derp.derpymod.arena;

import com.derp.derpymod.arena.mutations.WaveMutation;
import com.derp.derpymod.capabilities.UpgradeDataProvider;
import com.derp.derpymod.savedata.CustomWorldData;
import com.derp.derpymod.util.SwordDamageUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import com.derp.derpymod.capabilities.CurrencyDataProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LossHandler {
    private static LossHandler lossHandler;

    private LossHandler() {
    }

    public static LossHandler getInstance() {
        if (lossHandler == null) {
            lossHandler = new LossHandler();
        }
        return lossHandler;
    }

    public void checkLoss(ServerLevel level) {
            // Check if all players are in spectator mode
            if (getPlayersInSpectatorMode(level) >= getTotalPlayers(level)) {
                // All players are in spectator mode, perform reset
                resetGame(level);
            }
    }

    public static int getPlayersInSpectatorMode(ServerLevel level) {
        int spectators = 0;
        for (ServerPlayer player : level.players()) {
            if (player.isSpectator()) {
                spectators++;
            }
        }
        return spectators;
    }

    public static int getTotalPlayers(ServerLevel level) {
        return level.players().size();
    }

    private static void resetGame(ServerLevel level) {
        // Reset all game states

        CustomWorldData data = CustomWorldData.get(level);
        List<Entity> mobsToRemove = new ArrayList<>();
        var allEntities = level.getAllEntities();
        allEntities.forEach(entity -> {
            if (entity instanceof Mob) {
                mobsToRemove.add(entity);
            }
        });

        // Remove all collected mobs
        mobsToRemove.forEach(entity -> entity.remove(Entity.RemovalReason.DISCARDED));
        Wave.getInstance().setWaveMutation(0);

        // Clear the spawnedMobs list in CustomWorldData
        CustomWorldData customWorldData = CustomWorldData.get(level);
        if (customWorldData != null) {
            customWorldData.clearSpawnedMobs();
        }

        // give permanent currency
        level.getPlayers(serverPlayer -> true).forEach(serverPlayer -> {
            serverPlayer.getCapability(CurrencyDataProvider.CURRENCY_DATA).ifPresent(currencyData -> {
                currencyData.setCurrency(0);
                currencyData.addPermanentCurrency(data.getWaveNumber());
            });

            // reset non-permanent upgrades
            serverPlayer.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgradeData -> {
                for (Upgrade upgrade : upgradeData.getUpgrades()) {
                    if (!upgrade.isPermanent()) {
                        upgrade.resetUpgrade(serverPlayer);
                    }
                }
                for (var itemstack : serverPlayer.getInventory().items) {
                    if (itemstack.getItem() == Items.WOODEN_SWORD) {
                        SwordDamageUtils.resetDamage(itemstack);
                        for (Upgrade upgrade : upgradeData.getUpgrades()) {
                            if (upgrade.isPermanent() && upgrade.isSwordDamage()) {
                                SwordDamageUtils.addAttackDamageModifier(itemstack, upgrade.getLevel());
                            }
                        }
                    }
                }
            });
        });

        // Space for adding roguelite permanent upgrade currency
        Wave.getInstance().resetWaves(level);
        level.players().forEach(serverPlayer -> {
            serverPlayer.sendSystemMessage(Component.literal("Game lost").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLACK));
        });
        Wave.getInstance().revivePlayers(level);
    }
}
