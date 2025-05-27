package com.derp.derpymod.arena;

import com.derp.derpymod.arena.upgrades.IUpgrade;
import com.derp.derpymod.capabilities.CurrencyDataProvider;
import com.derp.derpymod.capabilities.UpgradeDataProvider;
import com.derp.derpymod.network.PacketHandler;
import com.derp.derpymod.packets.SSyncDataPacket;
import com.derp.derpymod.savedata.CustomWorldData;
import com.derp.derpymod.util.SwordDamageUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

import java.util.ArrayList;
import java.util.List;

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

        CustomWorldData worldData = CustomWorldData.get(level);
        List<Entity> mobsToRemove = new ArrayList<>();
        var allEntities = level.getAllEntities();
        allEntities.forEach(entity -> {
            if (entity instanceof Mob) {
                mobsToRemove.add(entity);
            }
        });

        // Remove all collected mobs
        mobsToRemove.forEach(entity -> entity.remove(Entity.RemovalReason.DISCARDED));

        assert worldData != null;
        worldData.setWaveMutation(0);

        worldData.resetWaves();

        level.getPlayers(serverPlayer -> true).forEach(serverPlayer -> {
            // 1) Currency reset
            serverPlayer.getCapability(CurrencyDataProvider.CURRENCY_DATA).ifPresent(cd -> {
                cd.setCurrency(0);
                cd.addPermanentCurrency(worldData.getWaveNumber());
            });

            // 2) Reset inventory
            for (ItemStack stack : serverPlayer.getInventory().items) {
                if (stack.getItem() == Items.WOODEN_SWORD) {
                    SwordDamageUtils.resetDamage(stack);
                }
            }

            // 3) Reset upgrades
            serverPlayer.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgData -> {
                // Reset non-permanent
                upgData.getUpgrades().stream()
                        .filter(u -> !u.isPermanent())
                        .forEach(upgrade -> upgrade.reset(serverPlayer));

                // Re-apply all remaining (permanent) upgrades
                upgData.getUpgrades().stream()
                        .filter(IUpgrade::isPermanent)
                        .forEach(upgrade -> upgrade.apply(serverPlayer));
            });

            // 3) Sync back to client
            syncAllToClient(serverPlayer);
        });

        level.players().forEach(serverPlayer -> serverPlayer.sendSystemMessage(Component.literal("Game lost").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLACK)));
        revivePlayers(level);
    }

    private static void revivePlayers(ServerLevel level) {
        level.getPlayers(serverPlayer -> true).forEach(serverPlayer -> {
            if (serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
                // Teleport player to their spawn point or world spawn
                BlockPos spawnPos = serverPlayer.getRespawnPosition() != null ? serverPlayer.getRespawnPosition() : level.getSharedSpawnPos();
                serverPlayer.teleportTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
                serverPlayer.setGameMode(GameType.ADVENTURE);
            }
        });
    }

    /**
     * Gather both UpgradeData and CurrencyData from the given player
     * and send a single SSyncDataPacket back to their client.
     */
    public static void syncAllToClient(ServerPlayer player) {
        // 1) Serialize upgrade data
        CompoundTag upgradesNBT = new CompoundTag();
        player.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(up -> up.saveNBTData(upgradesNBT));

        // 2) Serialize currency data
        CompoundTag currencyNBT = new CompoundTag();
        player.getCapability(CurrencyDataProvider.CURRENCY_DATA).ifPresent(cd -> cd.saveNBTData(currencyNBT));

        // 3) Send the unified sync packet (no GUI open, so screenType = null)
        SSyncDataPacket pkt = new SSyncDataPacket(upgradesNBT, currencyNBT, null);
        PacketHandler.sendToPlayer(pkt, player);
    }
}
