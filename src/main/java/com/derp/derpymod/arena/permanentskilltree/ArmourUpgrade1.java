package com.derp.derpymod.arena.permanentskilltree;

import com.derp.derpymod.arena.Upgrade;
import com.derp.derpymod.packets.ScreenType;
import com.derp.derpymod.util.AttributeModifierUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArmourUpgrade1 extends Upgrade {

    public static final UUID ARMOUR_MODIFIER1_UUID = UUID.fromString("623e4567-e89b-12d3-a456-426614174000");

    public ArmourUpgrade1() {
        super(78, 19);
        setPermanent(true);
        setId("armourUpgrade1");
        setName("Armour Upgrade");
        setDescription("+1 armour");
        setType(SkillTreeType.DEFENSE);
    }

    @Override
    public void executeUpgrade(Player player) {
        if (!alreadyUnlocked(player)) {
            if (hasEnoughCurrency(player)) {
                payUpgrade(player);
                setLevel(getLevel() + 1);
                AttributeModifierUtils.addModifier(player, Attributes.ARMOR, ARMOUR_MODIFIER1_UUID, "Armour modifier", getLevel(), "customArmour1");
                confirmationMessage(player);
                syncData((ServerPlayer) player);
            } else {
                player.sendSystemMessage(Component.literal("You don't have enough currency!").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA));
            }
        }

    }

    @Override
    public double calculateCost() {
        return (10 + (100 * getLevel()));
    }
}
