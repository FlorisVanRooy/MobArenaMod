package com.derp.derpymod.util;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class AttributeModifierUtils {
    public static void addModifier(Player player, Attribute attribute, UUID modifierUUID, String modifierName, double amount, String persistentDataKey) {
        AttributeInstance attributeInstance = player.getAttribute(attribute);
        if (attributeInstance != null) {
            attributeInstance.removeModifier(modifierUUID);
            attributeInstance.addPermanentModifier(new AttributeModifier(modifierUUID, modifierName, amount, AttributeModifier.Operation.ADDITION));
            player.getPersistentData().putDouble(persistentDataKey, amount);
        }
    }

    public static double getModifierAmount(Player player, String persistentDataKey) {
        return player.getPersistentData().getDouble(persistentDataKey);
    }
}
