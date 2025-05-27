//package com.derp.derpymod.arena.upgrades.leveledupgrades;
//
//import net.minecraft.ChatFormatting;
//import net.minecraft.network.chat.Component;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.entity.ai.attributes.AttributeModifier;
//import net.minecraft.world.entity.ai.attributes.Attributes;
//import net.minecraft.world.entity.player.Player;
//
//import java.util.UUID;
//
//public class ArmourUpgradeInfinite {
//    private static final UUID ARMOUR_MODIFIER_UUID = UUID.fromString("723e4567-e89b-12d3-a456-426614174000");
//
//
//    public ArmourUpgradeInfinite() {
//        super(98, 19);
//        setId("armourUpgradeInfinite");
//        setName("Armour Upgrade");
//    }
//
//    @Override
//    public void executeUpgrade(Player player) {
//        if (hasEnoughCurrency(player)) {
//            payUpgrade(player);
//            setLevel(getLevel() + 1);
//            addArmourModifier(player, getLevel());
//            confirmationMessage(player);
//            syncData((ServerPlayer) player);
//        } else {
//            player.sendSystemMessage(Component.literal("You don't have enough currency!").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA));
//        }
//    }
//
//    @Override
//    public double calculateCost() {
//        return ((100 * 1.5 * (getLevel() + 1)) + (int) (Math.pow(1.5, getLevel() + 3)));
//    }
//
//    private void addArmourModifier(Player player, double amount) {
//        var armour = player.getAttribute(Attributes.ARMOR);
//        if (armour != null) {
//            armour.removeModifier(ARMOUR_MODIFIER_UUID);
//            armour.addPermanentModifier(new AttributeModifier(ARMOUR_MODIFIER_UUID, "Armour modifier", amount, AttributeModifier.Operation.ADDITION));
//            System.out.println("Changed player armour");
//        }
//    }
//}
