package com.derp.derpymod.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class SwordDamageUtils {
    public static void addAttackDamageModifier(ItemStack stack, double additionalAmount) {
        ListTag modifiers = stack.getOrCreateTag().getList("AttributeModifiers", Tag.TAG_COMPOUND);
        boolean found = false;

        for (int i = 0; i < modifiers.size(); i++) {
            CompoundTag modifierTag = modifiers.getCompound(i);
            UUID uuid = modifierTag.getUUID("UUID");

            if (uuid.equals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))) {
                // Found existing modifier, update its amount
                double currentAmount = modifierTag.getDouble("Amount");
                modifierTag.putDouble("Amount", currentAmount + additionalAmount);
                found = true;
                break;
            }
        }

        if (!found) {
            // If not found, create a new modifier
            CompoundTag newModifierTag = new CompoundTag();
            newModifierTag.putString("AttributeName", "generic.attack_damage");
            newModifierTag.putDouble("Amount", additionalAmount);
            newModifierTag.putInt("Operation", AttributeModifier.Operation.ADDITION.toValue());
            newModifierTag.putUUID("UUID", UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
            newModifierTag.putString("Name", "generic.attack_damage");
            newModifierTag.putString("Slot", "mainhand");

            modifiers.add(newModifierTag);
        }

        stack.getOrCreateTag().put("AttributeModifiers", modifiers); // Update the item stack's tag
    }

    public static void removeAttackDamageModifier(ItemStack stack) {
        ListTag modifiers = stack.getOrCreateTag().getList("AttributeModifiers", Tag.TAG_COMPOUND);
        ListTag newModifiers = new ListTag();

        for (int i = 0; i < modifiers.size(); i++) {
            CompoundTag modifierTag = modifiers.getCompound(i);
            UUID uuid = modifierTag.getUUID("UUID");

            if (!uuid.equals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))) {
                newModifiers.add(modifierTag); // Preserve other modifiers
            }
        }

        stack.getOrCreateTag().put("AttributeModifiers", newModifiers); // Set the updated list of modifiers
    }

    public static void resetDamage(ItemStack stack) {
        removeAttackDamageModifier(stack);
        addAttackDamageModifier(stack, 4);
    }
}
