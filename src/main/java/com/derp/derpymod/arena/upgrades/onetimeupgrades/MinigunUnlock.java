package com.derp.derpymod.arena.upgrades.onetimeupgrades;

import com.derp.derpymod.arena.upgrades.OneTimeUpgrade;
import com.derp.derpymod.item.ModItems;
import com.derp.derpymod.util.AttributeModifierUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MinigunUnlock extends OneTimeUpgrade {
    public static final String ID = "minigun_unlock";

    public MinigunUnlock() {
        super(ID, "Unlock minigun weapon");
    }

    @Override
    protected void applyUnlocked(Player player) {
        if (player instanceof ServerPlayer) {
            boolean alreadyHasMinigun = player.getInventory().items.stream()
                    .anyMatch(stack -> stack.getItem() == ModItems.MINIGUN_ITEM.get());

            if (!alreadyHasMinigun) {
                ItemStack minigun = ModItems.MINIGUN_ITEM.get().getDefaultInstance();
                player.getInventory().placeItemBackInInventory(minigun);
            }
        }
    }

    @Override
    public void reset(Player player) {
        // Remove ALL Minigun items from the player's inventory
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() == ModItems.MINIGUN_ITEM.get()) {
                player.getInventory().setItem(i, ItemStack.EMPTY); // Clear the slot
            }
        }

        // Call super to mark the upgrade as locked again
        super.reset(player);
    }

    @Override
    public int calculateCost(int level) {
        return 1000;
    }
}
