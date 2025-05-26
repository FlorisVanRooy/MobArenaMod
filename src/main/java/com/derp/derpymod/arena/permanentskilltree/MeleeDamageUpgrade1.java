package com.derp.derpymod.arena.permanentskilltree;

import com.derp.derpymod.arena.Upgrade;
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

public class MeleeDamageUpgrade1 extends Upgrade {

    public MeleeDamageUpgrade1() {
        super(78, 19);
        setPermanent(true);
        setId("meleeDamageUpgrade1");
        setName("Melee Damage Upgrade");
        setDescription("+1 damage");
        setType(SkillTreeType.MELEE);
        setSwordDamage(true);
    }

    @Override
    public void executeUpgrade(Player player) {
        if (!alreadyUnlocked(player)) {
            if (hasEnoughCurrency(player)) {
                payUpgrade(player);
                setLevel(getLevel() + 1);
                for (ItemStack stack : player.getInventory().items) {
                    if (stack.getItem() == Items.WOODEN_SWORD) {
                        SwordDamageUtils.addAttackDamageModifier(stack, getLevel());
                    }
                }
                confirmationMessage(player);
                syncData((ServerPlayer) player);
            }
            else {
                player.sendSystemMessage(Component.literal("You don't have enough currency!").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA));
            }
        }
    }

    @Override
    public double calculateCost() {
        return 10 + (100 * getLevel());
    }

}
