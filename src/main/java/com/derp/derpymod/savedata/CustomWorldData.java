package com.derp.derpymod.savedata;

import com.google.gson.Gson;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomWorldData extends SavedData {
    private static final String DATA_NAME = "custom_world_data";

    private final Map<BlockPos, BlockState> originalBlockStates = new HashMap<>();
    private final List<Vec3> spawnPositions = new ArrayList<>();
    private final List<CompoundTag> spawnedMobs = new ArrayList<>();
    private int waveNumber;

    public static CustomWorldData load(CompoundTag tag) {
        CustomWorldData data = new CustomWorldData();

        // Deserialize originalBlockStates
        ListTag blockStatesList = tag.getList("originalBlockStates", CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < blockStatesList.size(); i++) {
            CompoundTag blockStateTag = blockStatesList.getCompound(i);
            BlockPos pos = BlockPos.of(blockStateTag.getLong("pos"));
            BlockState state = BlockState.CODEC.parse(NbtOps.INSTANCE, blockStateTag.get("state")).result().orElseThrow();
            data.originalBlockStates.put(pos, state);
        }

        // Deserialize spawnPositions
        ListTag spawnPositionsTag = tag.getList("spawnPositions", CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < spawnPositionsTag.size(); i++) {
            CompoundTag posTag = spawnPositionsTag.getCompound(i);
            Vec3 pos = new Vec3(posTag.getDouble("x"), posTag.getDouble("y"), posTag.getDouble("z"));
            data.spawnPositions.add(pos);
        }

        // Deserialize spawnedMobs
        ListTag spawnedMobsTag = tag.getList("spawnedMobs", Tag.TAG_COMPOUND);
        for (int i = 0; i < spawnedMobsTag.size(); i++) {
            CompoundTag mobTag = spawnedMobsTag.getCompound(i);
            data.spawnedMobs.add(mobTag);
        }

        // Deserialize waveNumber
        data.waveNumber = tag.getInt("waveNumber");

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        // Serialize originalBlockStates
        ListTag blockStatesList = new ListTag();
        for (Map.Entry<BlockPos, BlockState> entry : originalBlockStates.entrySet()) {
            CompoundTag blockStateTag = new CompoundTag();
            blockStateTag.putLong("pos", entry.getKey().asLong());
            blockStateTag.put("state", BlockState.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue()).result().orElseThrow());
            blockStatesList.add(blockStateTag);
        }
        tag.put("originalBlockStates", blockStatesList);

        // Serialize spawnPositions
        ListTag spawnPositionsTag = new ListTag();
        for (Vec3 pos : spawnPositions) {
            CompoundTag posTag = new CompoundTag();
            posTag.putDouble("x", pos.x);
            posTag.putDouble("y", pos.y);
            posTag.putDouble("z", pos.z);
            spawnPositionsTag.add(posTag);
        }
        tag.put("spawnPositions", spawnPositionsTag);

        // Serialize spawnedMobs
        ListTag spawnedMobsTag = new ListTag();
        for (CompoundTag mobTag : spawnedMobs) {
            spawnedMobsTag.add(mobTag);
        }
        tag.put("spawnedMobs", spawnedMobsTag);

        // Serialize waveNumber
        tag.putInt("waveNumber", waveNumber);

        return tag;
    }

    public static CustomWorldData get(Level level) {
        if (!level.isClientSide) {
            return ((ServerLevel) level).getDataStorage().computeIfAbsent(CustomWorldData::load, CustomWorldData::new, DATA_NAME);
        }
        return null;
    }

    public Map<BlockPos, BlockState> getOriginalBlockStates() {
        return new HashMap<>(originalBlockStates);
    }

    public void setOriginalBlockStates(Map<BlockPos, BlockState> originalBlockStates) {
        this.originalBlockStates.clear();
        this.originalBlockStates.putAll(originalBlockStates);
        setDirty();
    }

    public List<Vec3> getSpawnPositions() {
        return spawnPositions;
    }

    public void setSpawnPositions(List<Vec3> spawnPositions) {
        this.spawnPositions.clear();
        this.spawnPositions.addAll(spawnPositions);
        setDirty();
    }

    public List<CompoundTag> getSpawnedMobs() {
        return spawnedMobs;
    }

    public void addSpawnedMob(LivingEntity mob) {
        CompoundTag mobTag = new CompoundTag();
        mob.save(mobTag);
        this.spawnedMobs.add(mobTag);
        setDirty();
    }

    public void removeSpawnedMob(LivingEntity mob) {
        this.spawnedMobs.removeIf(mobTag -> mobTag.getUUID("UUID").equals(mob.getUUID()));
        setDirty();
    }

    public void clearSpawnedMobs() {
        this.spawnedMobs.clear();
        setDirty();
    }

    public int getWaveNumber() {
        return waveNumber;
    }

    public void setWaveNumber(int waveNumber) {
        this.waveNumber = waveNumber;
        setDirty();
    }

    public void resetWaves() {
        this.waveNumber = 1;
        this.spawnedMobs.clear();
        setDirty();
    }
}
