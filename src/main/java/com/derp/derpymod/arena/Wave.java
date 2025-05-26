package com.derp.derpymod.arena;

import com.derp.derpymod.DerpyMod;
import com.derp.derpymod.init.EntityInit;
import com.derp.derpymod.savedata.CustomWorldData;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.internal.reflect.ReflectionHelper;
import it.unimi.dsi.fastutil.Hash;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class Wave {
    private static Wave wave;
    private final Map<EntityType<?>, Integer> CURRENCY_VALUES = new HashMap<>();
    private int waveMutation = 0;
    private int mutationChance = 1;

    private Wave() {
        initializeCurrencyValues();
    }

    public static Wave getInstance() {
        if (wave == null) {
            wave = new Wave();
        }
        return wave;
    }

    private void initializeCurrencyValues() {
        CURRENCY_VALUES.put(EntityType.SILVERFISH, 0);
        CURRENCY_VALUES.put(EntityType.ZOMBIE, 2);
        CURRENCY_VALUES.put(EntityType.CREEPER, 3);
        CURRENCY_VALUES.put(EntityType.SKELETON, 5);
        CURRENCY_VALUES.put(EntityType.SPIDER, 4);
        CURRENCY_VALUES.put(EntityInit.STRONG_ZOMBIE.get(), 10);
    }

    private int getWaveCurrency(int waveNumber) {
        int currency = 100;
        for (int i = 1; i <= waveNumber; i++) {
            if (i < 5) {
                currency += 20;
            } else if (i < 10) {
                currency += 50;
            } else if (i < 20) {
                currency += 68;
            } else if (i < 25) {
                currency += 100;
            } else {
                currency += 200;
            }
        }
        return currency;
    }

    private List<EntityType<?>> getAvailableEnemies(int waveNumber) {
        List<EntityType<?>> availableEnemies = new ArrayList<>();
        availableEnemies.add(EntityType.ZOMBIE);
        availableEnemies.add(EntityType.CREEPER);
        availableEnemies.add(EntityType.SPIDER);
        availableEnemies.add(EntityType.SKELETON);
        if (waveNumber > 5) {
            availableEnemies.add(EntityInit.STRONG_ZOMBIE.get());
            if (waveNumber > 10) {
                availableEnemies.add(EntityType.WITHER_SKELETON);
                availableEnemies.add(EntityType.STRAY);
                if (waveNumber > 20) {
                    availableEnemies.add(EntityType.VINDICATOR);
                }
            }
        }
        return availableEnemies;
    }

    public void changeSpawnPositions(BlockPos pos, Player player) {
        Vec3 spawn = Vec3.atCenterOf(pos).add(new Vec3(0, 1, 0));
        ServerLevel level = (ServerLevel) player.level();
        CustomWorldData data = CustomWorldData.get(level);

        if (data.getSpawnPositions().contains(spawn)) {
            data.getSpawnPositions().remove(spawn);
            player.sendSystemMessage(Component.literal("Spawn removed!"));
        } else {
            data.getSpawnPositions().add(spawn);
            player.sendSystemMessage(Component.literal("Spawn added!"));
        }
        data.setDirty();
    }

    public void startWave(ServerLevel level) {
        CustomWorldData data = CustomWorldData.get(level);
        List<Vec3> spawnPositions = data.getSpawnPositions();
        var players = level.players();
        if (!spawnPositions.isEmpty()) {
            int waveNumber = data.getWaveNumber();
            int waveCurrency = getWaveCurrency(waveNumber);
            List<EntityType<?>> availableEnemies = getAvailableEnemies(waveNumber);
            Random random = new Random();
            int waveMutationRandom = random.nextInt(100);
            if (waveMutationRandom == 99) {
                level.players().forEach(player -> {
                    player.sendSystemMessage(Component.literal("You feel an evil presence watching you..."));
                });
            }
            if (waveMutationRandom < mutationChance) {
                int mutationNumber = random.nextInt(1,3);
                activateWaveMutation(mutationNumber, level);
                mutationChance = 1;
            }

            while (waveCurrency > 0) {
                EntityType<?> enemyType = availableEnemies.get(random.nextInt(availableEnemies.size()));
                int enemyCost = getEnemyCost(enemyType);
                Vec3 spawnPos = spawnPositions.get(random.nextInt(spawnPositions.size()));
                LivingEntity enemy = (LivingEntity) enemyType.create(level);
                if (enemy != null) {
                    enemy.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                    level.addFreshEntity(enemy);
                    data.addSpawnedMob(enemy);
                    waveCurrency -= enemyCost;
                }
            }

            if (waveMutation == 3) {
                var entities = level.getAllEntities();
                for (var entity : entities) {
                    if (entity instanceof Mob) {
                        entity.setCustomName(Component.literal("Dinnerbone"));
                    }
                }
            }
            data.setWaveNumber(waveNumber + 1);
            players.forEach(player -> {
                player.sendSystemMessage(Component.literal("Wave " + waveNumber + " started!").withStyle(ChatFormatting.GOLD));
            });
        } else {
            players.forEach(player -> {
                player.sendSystemMessage(Component.literal("No spawn positions set!").withStyle(ChatFormatting.GOLD));
            });
        }
    }

    public int getWaveMutation() {
        return waveMutation;
    }

    public void setWaveMutation(int waveMutation) {
        this.waveMutation = waveMutation;
    }

    private void activateWaveMutation(int mutationNumber, ServerLevel level) {
        Component message;
        if (mutationNumber == 1) {
            message = Component.literal("You feel vibrations from deep below").withStyle(ChatFormatting.GREEN);
        }
        else if (mutationNumber == 2) {
            message = Component.literal("The air is getting warmer around you").withStyle(ChatFormatting.GOLD);
        }
        else if (mutationNumber == 3) {
            message = Component.literal("You have been teleported to Australia").withStyle(ChatFormatting.GOLD);
        } else {
            message = Component.empty();
        }
        level.players().forEach(player -> {
            player.sendSystemMessage(message);
        });
        waveMutation = mutationNumber;
    }

    private void removeMutation() {
        waveMutation = 0;
    }

    public int getEnemyCost(EntityType<?> enemyType) {
        return CURRENCY_VALUES.getOrDefault(enemyType, 10); // Default cost of 10 if not found
    }

    public void removeMob(LivingEntity entity, Level level) {
        CustomWorldData data = CustomWorldData.get(level);
        data.removeSpawnedMob(entity);
        int mobCount = data.getSpawnedMobs().size();
        if (mobCount == 10 || mobCount == 5 || mobCount < 4) {
            Player player = Minecraft.getInstance().player;
            if (!data.getSpawnedMobs().isEmpty()) {
                if (player != null) {
                    if (mobCount == 1) {
                        player.sendSystemMessage(Component.literal("Only " + mobCount + " mob left"));
                    } else {
                        player.sendSystemMessage(Component.literal("Only " + mobCount + " mobs left"));
                    }
                }
            } else {
                endWave(level);
            }
        }
    }

    private void endWave(Level level) {
        CustomWorldData data = CustomWorldData.get(level);
        Player player = Minecraft.getInstance().player;
        removeMutation();
        player.sendSystemMessage(Component.literal("You beat wave " + (data.getWaveNumber() - 1))
                .withStyle(ChatFormatting.BOLD)
                .withStyle(ChatFormatting.RED));
        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            revivePlayers(serverLevel);
        }
        mutationChance += 10;
    }

    public void revivePlayers(ServerLevel level) {
        level.getPlayers(serverPlayer -> true).forEach(serverPlayer -> {
            if (serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
                // Teleport player to their spawn point or world spawn
                BlockPos spawnPos = serverPlayer.getRespawnPosition() != null ? serverPlayer.getRespawnPosition() : level.getSharedSpawnPos();
                serverPlayer.teleportTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
                serverPlayer.setGameMode(GameType.ADVENTURE);
            }
        });
    }


    public boolean areAllMobsDead(Level level) {
        CustomWorldData data = CustomWorldData.get(level);
        return data.getSpawnedMobs().isEmpty();
    }

    public void resetWaves(ServerLevel serverLevel) {
        CustomWorldData data = CustomWorldData.get(serverLevel);
        data.resetWaves();
    }
}
