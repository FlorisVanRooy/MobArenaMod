//package com.derp.derpymod.arena.upgrades.leveledupgrades;
//
//import net.minecraft.ChatFormatting;
//import net.minecraft.network.chat.Component;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.entity.player.Player;
//
//public class BowDamageUpgradeInfinite {
//
//    public BowDamageUpgradeInfinite() {
//        super(18, 19);
//        setId("bowDamageUpgradeInfinite");
//        setName("Bow Damage Upgrade");
//    }
//
//    @Override
//    public void executeUpgrade(Player player) {
//        if (hasEnoughCurrency(player)) {
//            payUpgrade(player);
//            setLevel(getLevel() + 1);
//            confirmationMessage(player);
//            syncData((ServerPlayer) player);
//        }
//        else {
//            player.sendSystemMessage(Component.literal("You don't have enough currency!").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA));
//        }
//    }
//
//    @Override
//    public double calculateCost() {
//
//        return ((100 * 0.85 * getLevel()) + (int)(Math.pow(3.2,getLevel())));
//    }
//}
