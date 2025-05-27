package com.derp.derpymod.savedata;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;
import java.util.stream.Collectors;

public class CustomWorldData extends SavedData {
    private static final String DATA_NAME = "custom_world_data";

    private final Map<BlockPos, BlockState> originalBlockStates = new HashMap<>();
    private final Set<BlockPos> spawnPositions = new HashSet<>();

    // NEW: track alive mobs by UUID
    private final List<UUID> aliveMobs = new ArrayList<>();

    private int waveNumber     = 1;
    private int mutationChance = 1;
    private int waveMutation   = 0;

    public static CustomWorldData load(CompoundTag tag) {
        CustomWorldData data = new CustomWorldData();

        // originalBlockStates
        ListTag bsList = tag.getList("originalBlockStates", Tag.TAG_COMPOUND);
        bsList.forEach(t -> {
            CompoundTag b = (CompoundTag) t;
            BlockPos pos = BlockPos.of(b.getLong("pos"));
            BlockState st = BlockState.CODEC
                    .parse(NbtOps.INSTANCE, b.get("state"))
                    .result()
                    .orElseThrow();
            data.originalBlockStates.put(pos, st);
        });

        // spawnPositions
        tag.getList("spawnPositions", Tag.TAG_COMPOUND)
                .forEach(t -> {
                    CompoundTag p = (CompoundTag) t;
                    data.spawnPositions.add(new BlockPos(p.getInt("x"), p.getInt("y"), p.getInt("z")));
                });

        // aliveMobs
        ListTag aliveMobList = tag.getList("aliveMobs", Tag.TAG_STRING);
        for (Tag uuidTag : aliveMobList) {
            String uuidString = ((StringTag) uuidTag).getAsString();
            data.aliveMobs.add(UUID.fromString(uuidString));
        }

        data.waveNumber     = tag.getInt("waveNumber");
        data.mutationChance = tag.getInt("mutationChance");
        data.waveMutation   = tag.getInt("waveMutation");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        // originalBlockStates
        ListTag bsList = new ListTag();
        originalBlockStates.forEach((pos, st) -> {
            CompoundTag b = new CompoundTag();
            b.putLong("pos", pos.asLong());
            b.put("state", BlockState.CODEC.encodeStart(NbtOps.INSTANCE, st).result().orElseThrow());
            bsList.add(b);
        });
        tag.put("originalBlockStates", bsList);

        // spawnPositions
        ListTag spList = new ListTag();
        spawnPositions.forEach(v -> {
            CompoundTag p = new CompoundTag();
            p.putInt("x", v.getX());
            p.putInt("y", v.getY());
            p.putInt("z", v.getZ());
            spList.add(p);
        });
        tag.put("spawnPositions", spList);

        // aliveMobs
        ListTag aliveMobList = new ListTag();
        for (UUID uuid : aliveMobs) {
            aliveMobList.add(StringTag.valueOf(uuid.toString()));
        }
        tag.put("aliveMobs", aliveMobList);

        // wave state
        tag.putInt("waveNumber", waveNumber);
        tag.putInt("mutationChance", mutationChance);
        tag.putInt("waveMutation", waveMutation);
        return tag;
    }

    public static CustomWorldData get(Level level) {
        if (level.isClientSide) return null;
        return ((ServerLevel) level).getDataStorage()
                .computeIfAbsent(CustomWorldData::load, CustomWorldData::new, DATA_NAME);
    }

    // --- getters / setters ---

    public Set<BlockPos> getSpawnPositions() {
        return Collections.unmodifiableSet(spawnPositions);
    }

    public void toggleSpawnPosition(BlockPos pos) {
        if (!spawnPositions.remove(pos)) {
            spawnPositions.add(pos);
        }
        setDirty();
    }

    public int getWaveNumber()     { return waveNumber;     }
    public void setWaveNumber(int n)     { waveNumber = n; setDirty(); }
    public int getMutationChance() { return mutationChance; }
    public void setMutationChance(int c) { mutationChance = c; setDirty(); }
    public int getWaveMutation()   { return waveMutation;   }
    public void setWaveMutation(int m)   { waveMutation = m; setDirty(); }

    public void resetWaves() {
        waveNumber     = 1;
        mutationChance = 1;
        waveMutation   = 0;
        aliveMobs.clear();
        setDirty();
    }

    public List<UUID> getAliveMobs() {
        return aliveMobs;
    }

    public void addAliveMob(UUID id)    { aliveMobs.add(id);    setDirty(); }
    public void removeAliveMob(UUID id) { aliveMobs.remove(id); setDirty(); }
    public boolean isAllMobsDead()      { return aliveMobs.isEmpty(); }

    public Map<BlockPos, BlockState> getOriginalBlockStates() {
        return originalBlockStates;
    }

    public void putOriginalBlockState(BlockPos pos, BlockState state) {
        originalBlockStates.put(pos, state);
        setDirty();
    }

    public void removeOriginalBlockState(BlockPos pos) {
        originalBlockStates.remove(pos);
        setDirty();
    }

}
