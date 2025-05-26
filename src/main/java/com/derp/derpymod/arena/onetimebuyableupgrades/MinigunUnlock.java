package com.derp.derpymod.arena.onetimebuyableupgrades;

import com.derp.derpymod.arena.Upgrade;
import com.derp.derpymod.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class MinigunUnlock extends Upgrade {

    public MinigunUnlock() {
        super(500, 1000);
    }

    @Override
    public void executeUpgrade(Player player) {
        if (alreadyUnlocked(player)) {
            player.sendSystemMessage(Component.literal("You have already unlocked this upgrade!").withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.ITALIC));
        }
        else {
            if (hasEnoughCurrency(player)) {
                setLevel(getLevel() + 1);
                if (getLevel() == 1) {
                    player.getInventory().placeItemBackInInventory(ModItems.MINIGUN_ITEM.get().getDefaultInstance());
                }
            }
        }
    }

    @Override
    public double calculateCost() {
        return 0;
    }
}
