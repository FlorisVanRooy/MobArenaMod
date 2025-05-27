package com.derp.derpymod.arena.mobs;

import com.derp.derpymod.init.EntityInit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public record CustomEnemy(
        String id,
        int minWave,
        int cost,
        EntityType<? extends LivingEntity> baseType,
        Consumer<LivingEntity> customizer
) {
    public static final List<CustomEnemy> ENEMIES = List.of(
            new CustomEnemy("zombie", 1, 2, EntityType.ZOMBIE, mob -> {}),
            new CustomEnemy("creeper", 1, 3, EntityType.CREEPER, mob -> {}),
            new CustomEnemy("skeleton", 1, 5, EntityType.SKELETON, mob -> {}),
            new CustomEnemy("spider", 1, 4, EntityType.SPIDER, mob -> {}),
            new CustomEnemy("strong_zombie", 6, 10, EntityInit.STRONG_ZOMBIE.get(), mob -> {}),
            new CustomEnemy("iron_zombie", 1, 12, EntityType.ZOMBIE, mob -> {
                mob.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0);
                mob.setHealth(40);
                mob.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35);
                mob.setHealth(40.0f);
                mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            })
    );

    public static Optional<CustomEnemy> getMatchingEnemy(LivingEntity entity) {
        return ENEMIES.stream()
                .filter(enemy -> enemy.baseType().equals(entity.getType()))
                .filter(enemy -> enemy.id().equals(entity.getPersistentData().getString("EnemyId")))
                .findFirst(); // You could also use a more complex match strategy here
    }
}

