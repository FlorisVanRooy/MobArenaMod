package com.derp.derpymod.arena.normalupgrades;

import com.derp.derpymod.arena.Upgrade;
import com.derp.derpymod.packets.ScreenType;
import com.derp.derpymod.util.SwordDamageUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.UUID;

public class SwordDamageUpgradeInfinite extends Upgrade {

    public SwordDamageUpgradeInfinite() {
        super(58, 19);
        setId("swordDamageUpgradeInfinite");
        setName("Sword Damage Upgrade");
    }

    @Override
    public void executeUpgrade(Player player) {
        if (hasEnoughCurrency(player)) {
            payUpgrade(player);
            setLevel(getLevel() + 1);
            for (ItemStack stack : player.getInventory().items) {
                if (stack.getItem() == Items.WOODEN_SWORD) {
//                    SwordDamageUtils.removeAttackDamageModifier(stack); // Remove existing modifier first
                    SwordDamageUtils.addAttackDamageModifier(stack, getLevel());  // Add attack damage as an example
                    confirmationMessage(player);
                }
            }
            syncData((ServerPlayer) player);
        }
        else {
            player.sendSystemMessage(Component.literal("You don't have enough currency!").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA));
        }
    }

    @Override
    public double calculateCost() {
        return ((100 * 1.5 * (getLevel() + 1)) + (int)(Math.pow(1.5,getLevel() + 3)));

    }
}