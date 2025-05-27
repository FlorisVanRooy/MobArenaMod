// src/main/java/com/derp/derpymod/arena/Wave.java
package com.derp.derpymod.arena;

import com.derp.derpymod.arena.mobs.CustomEnemy;
import com.derp.derpymod.init.EntityInit;
import com.derp.derpymod.savedata.CustomWorldData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.stream.Collectors;

import static com.derp.derpymod.arena.mobs.CustomEnemy.ENEMIES;

public class Wave {

    private static final List<IWaveMutation> MUTATIONS = List.of(
            new IWaveMutation() { // vibration
                public int weight() { return 1; }
                public void apply(ServerLevel lvl) { /* no world effect */ }
                public Component message() {
                    return Component.literal("You feel vibrations from deep below")
                            .withStyle(ChatFormatting.GREEN);
                }
            },
            new IWaveMutation() {
                public int weight() { return 1; }
                public void apply(ServerLevel lvl) { /* no world effect */ }
                public Component message() {
                    return Component.literal("The air is getting warmer around you")
                            .withStyle(ChatFormatting.GOLD);
                }
            },
            new IWaveMutation() { // teleport prank
                public int weight() { return 1; }
                public void apply(ServerLevel lvl) {
                    // teleport all players randomly within 16 blocks
                    lvl.players().forEach(p -> {
                        double dx = (lvl.getRandom().nextDouble()*32) - 16;
                        double dz = (lvl.getRandom().nextDouble()*32) - 16;
                        p.teleportTo(p.getX()+dx, p.getY(), p.getZ()+dz);
                    });
                }
                public Component message() {
                    return Component.literal("You have been teleported to Australia")
                            .withStyle(ChatFormatting.RED);
                }
            }
    );

    private static final Random RNG = new Random();

    /** Start the next wave on this level */
    public void startWave(ServerLevel level) {
        CustomWorldData data = CustomWorldData.get(level);
        List<BlockPos> spawns = new ArrayList<>(data.getSpawnPositions());
        if (spawns.isEmpty()) {
            level.players().forEach(p ->
                    p.sendSystemMessage(Component.literal("No spawn positions set!").withStyle(ChatFormatting.RED))
            );
            return;
        }

        int waveNum = data.getWaveNumber();
        int budget  = getWaveCurrency(waveNum);
        List<CustomEnemy> pool = ENEMIES.stream()
                .filter(e -> waveNum >= e.minWave())
                .toList();

        // maybe mutate
        if (RNG.nextInt(100) < data.getMutationChance()) {
            IWaveMutation mut = pickWeighted(MUTATIONS);
            mut.apply(level);
            data.setWaveMutation(MUTATIONS.indexOf(mut)+1);
            level.players().forEach(p -> p.sendSystemMessage(mut.message()));
            data.setMutationChance(1);
        }

        // spawn until out of budget
        while (budget > 0 && !pool.isEmpty()) {
            CustomEnemy entry = pool.get(RNG.nextInt(pool.size()));
            if (entry.cost() > budget) break;

            BlockPos pos = spawns.get(RNG.nextInt(spawns.size()));
            Vec3 loc = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

            LivingEntity mob = (LivingEntity) entry.baseType().create(level);
            if (mob != null) {
                mob.getPersistentData().putString("EnemyId", entry.id());
                entry.customizer().accept(mob);
                mob.teleportTo(loc.x, loc.y+1, loc.z);
                level.addFreshEntity(mob);
                data.addAliveMob(mob.getUUID());
                budget -= entry.cost();
            }
        }


        // notify & advance
        level.players().forEach(p ->
                p.sendSystemMessage(Component.literal("Wave " + waveNum + " started!")
                        .withStyle(ChatFormatting.GOLD))
        );
        data.setWaveNumber(waveNum + 1);
    }

    /** Call this when a mob dies or is removed */
    public boolean removeMob(LivingEntity mob, ServerLevel level) {
        CustomWorldData data = CustomWorldData.get(level);
        if (!data.getAliveMobs().contains(mob.getUUID())) {
            return false;
        }
        data.removeAliveMob(mob.getUUID());
        int remaining = data.getAliveMobs().size();
        if (remaining > 0 && List.of(10, 5, 1).contains(remaining)) {
            level.players().forEach(p ->
                    p.sendSystemMessage(Component.literal("Only " + remaining + " mobs left"))
            );
        }
        if (remaining == 0) {
            endWave(level);
        }
        return true;
    }

    private void endWave(ServerLevel level) {
        CustomWorldData data = CustomWorldData.get(level);
        data.setWaveMutation(0);
        level.players().forEach(p ->
                p.sendSystemMessage(
                        Component.literal("You beat wave " + (data.getWaveNumber()-1))
                                .withStyle(ChatFormatting.BOLD, ChatFormatting.GREEN))
        );
        data.setMutationChance(data.getMutationChance() + 10);
    }

    public boolean areAllMobsDead(ServerLevel level) {
        return CustomWorldData.get(level).isAllMobsDead();
    }

    public void resetWaves(ServerLevel level) {
        CustomWorldData.get(level).resetWaves();
    }

    /** O(1) wave currency: base 100 + tiered increments */
    private static int getWaveCurrency(int wave) {
        if (wave < 5)  return 100 + 20*wave;
        if (wave < 10) return 100 + 20*4 + 50*(wave-4);
        if (wave < 20) return 100 + 20*4 + 50*5 + 68*(wave-9);
        if (wave < 25) return 100 + 20*4 + 50*5 + 68*10 + 100*(wave-19);
        return 100 + 20*4 + 50*5 + 68*10 + 100*5 + 200*(wave-24);
    }

    /** Helper: pick one mutation by weight */
    private static <T extends IWaveMutation> T pickWeighted(List<T> list) {
        int total = list.stream().mapToInt(IWaveMutation::weight).sum();
        int r = RNG.nextInt(total);
        for (T m : list) {
            r -= m.weight();
            if (r < 0) return m;
        }
        return list.get(0); // fallback
    }
}
