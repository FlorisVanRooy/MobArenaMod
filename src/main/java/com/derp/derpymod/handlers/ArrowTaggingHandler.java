package com.derp.derpymod.handlers;

import com.derp.derpymod.arena.upgrades.IUpgrade;
import com.derp.derpymod.arena.upgrades.leveledupgrades.BowDamageUpgradeInfinite;
import com.derp.derpymod.capabilities.UpgradeDataProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ArrowTaggingHandler {

    @SubscribeEvent
    public static void onProjectileLaunch(EntityJoinLevelEvent evt) {
        if (!(evt.getEntity() instanceof AbstractArrow arrow)) return;
        if (!(arrow.getOwner() instanceof Player player)) return;

        // what the player was holding when they shot:
        ItemStack bowStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!(bowStack.getItem() instanceof BowItem)) return;

        player.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(data -> {
            IUpgrade up = data.getUpgrade(BowDamageUpgradeInfinite.ID);
            int level = up.getLevel();
            if (level > 0) {
                // store in the arrow so we can read it later:
                CompoundTag tag = arrow.getPersistentData();
                tag.putInt("increaseDamageLevel", level);
            }
        });
    }
}

