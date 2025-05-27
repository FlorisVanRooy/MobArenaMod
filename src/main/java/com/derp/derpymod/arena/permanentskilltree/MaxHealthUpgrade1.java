//package com.derp.derpymod.arena.permanentskilltree;
//
//import com.derp.derpymod.util.AttributeModifierUtils;
//import net.minecraft.ChatFormatting;
//import net.minecraft.network.chat.Component;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.entity.ai.attributes.Attributes;
//import net.minecraft.world.entity.player.Player;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//public class MaxHealthUpgrade1 {
//    public static final UUID MAX_HEALTH_MODIFIER1_UUID = UUID.fromString("523e4567-e89b-12d3-a456-426614174000");
//
//    public MaxHealthUpgrade1() {
//        super(38, 51);
//        setPermanent(true);
//        setId("maxHealthUpgrade1");
//        setName("Max Health Upgrade");
//        setDescription("+1 Heart");
//        List<Upgrade> prerequisites = new ArrayList<>();
//        prerequisites.add(new ArmourUpgrade1());
//        setPrerequisites(prerequisites);
//        setType(SkillTreeType.DEFENSE);
//    }
//
//    @Override
//    public void executeUpgrade(Player player) {
//        if (hasPrerequisites(player)) {
//            if (!alreadyUnlocked(player)) {
//                if (hasEnoughCurrency(player)) {
//                    payUpgrade(player);
//                    setLevel(getLevel() + 1);
//                    AttributeModifierUtils.addModifier(player, Attributes.MAX_HEALTH, MAX_HEALTH_MODIFIER1_UUID, "Max health modifier", 2 * getLevel(), "customMaxHealth1");
//                    player.setHealth(player.getHealth());
//                    confirmationMessage(player);
//                    syncData((ServerPlayer) player);
//                } else {
//                    player.sendSystemMessage(Component.literal("You don't have enough currency!").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA));
//                }
//            }
//        }
//    }
//
//    @Override
//    public double calculateCost() {
//        return (10 + (100 * getLevel()));
//    }
//}
