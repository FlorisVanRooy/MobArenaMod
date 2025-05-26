package com.derp.derpymod.entities;

import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.common.Mod;

public class StrongZombie extends Zombie {
    public StrongZombie(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
//        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
//        this.addBehaviourGoals();
    }

//    protected void addBehaviourGoals() {
//        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0, false));
//        this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0, true, 4, this::canBreakDoors));
//        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
//        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers(new Class[]{ZombifiedPiglin.class}));
//    }


    @Override
    public boolean isBaby() {
        return false;
    }

    public static AttributeSupplier.Builder getStrongZombieAttributes() {
        // Retrieve attributes from Zombie class
        AttributeSupplier.Builder builder = Zombie.createAttributes();

        // Modify specific attributes for StrongZombie
        builder.add(Attributes.ARMOR, 10.0f)
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.235)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.0f);

        return builder;
    }
}
