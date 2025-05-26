package com.derp.derpymod.arena.guns;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

import java.util.Optional;

public class RayTraceUtils {
//    private static final double REACH_DISTANCE = 6.0;

    public static HitResult rayTrace(Player player, Level world, double range) {
        Vec3 start = getPlayerEyePosition(player);
        Vec3 direction = getPlayerViewDirection(player);
        Vec3 end = start.add(direction.scale(range));

        ClipContext context = new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
        BlockHitResult blockHitResult = world.clip(context);

        Vec3 finalEnd = blockHitResult.getLocation();

        AABB aabb = new AABB(start, finalEnd).inflate(1.0D);
        EntityHitResult entityHitResult = rayTraceEntities(world, player, start, finalEnd, aabb, range);

        if (entityHitResult != null) {
            return entityHitResult;
        }

        return blockHitResult;
    }

    private static EntityHitResult rayTraceEntities(Level world, Player player, Vec3 start, Vec3 end, AABB aabb, double range) {
        EntityHitResult result = null;
        double closestDistance = range;

        for (Entity entity : world.getEntities(player, aabb)) {
            AABB entityAABB = entity.getBoundingBox().inflate(0.5D);
            Optional<Vec3> optionalHitResult = entityAABB.clip(start, end);

            if (entityAABB.contains(start)) {
                if (closestDistance >= 0.0D) {
                    result = new EntityHitResult(entity);
                    closestDistance = 0.0D;
                }
            } else if (optionalHitResult.isPresent()) {
                double distance = start.distanceTo(optionalHitResult.get());

                if (distance < closestDistance || closestDistance == 0.0D) {
                    result = new EntityHitResult(entity);
                    closestDistance = distance;
                }
            }
        }

        return result;
    }

    private static Vec3 getPlayerEyePosition(Player player) {
        return new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
    }

    private static Vec3 getPlayerViewDirection(Player player) {
        float pitch = player.getXRot();
        float yaw = player.getYRot();
        float f = (float) Math.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = (float) Math.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = (float) -Math.cos(-pitch * 0.017453292F);
        float f3 = (float) Math.sin(-pitch * 0.017453292F);
        return new Vec3(f1 * f2, f3, f * f2);
    }
}
