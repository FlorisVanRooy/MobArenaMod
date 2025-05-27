//package com.derp.derpymod.arena.upgrades.onetimeupgrades;
//
//import net.minecraft.ChatFormatting;
//import net.minecraft.network.chat.Component;
//import net.minecraft.world.entity.player.Player;
//
//public class FlingingUpgrade extends Upgrade {
//
//    public FlingingUpgrade() {
//        super(0, 500);
//    }
//
//    @Override
//    public void executeUpgrade(Player player) {
//        if (alreadyUnlocked(player)) {
//            player.sendSystemMessage(Component.literal("You have already unlocked this upgrade!").withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.ITALIC));
//        }
//        else {
//            setLevel(getLevel() + 1);
//        }
//    }
//
//
//    @Override
//    public double calculateCost() {
//        return 0;
//    }
//}
