package com.derp.derpymod.util;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class AttributeModifierUtils {
    /**
     * Ensures the given UUID modifier is first removed,
     * then (if amount!=0) added back at `amount`.
     */
    public static void applyModifier(
            Player player,
            Attribute attribute,
            UUID modifierUUID,
            String modifierName,
            double amount,
            AttributeModifier.Operation op
    ) {
        AttributeInstance inst = player.getAttribute(attribute);
        if (inst == null) return;

        // always strip out any old modifier
        inst.removeModifier(modifierUUID);

        // only re-add if it's non-zero
        if (amount != 0.0) {
            AttributeModifier mod = new AttributeModifier(
                    modifierUUID,
                    modifierName,
                    amount,
                    op
            );
            inst.addPermanentModifier(mod);
        }
    }

    /** Remove a modifier by UUID (no-op if it wasn’t present). */
    public static void removeModifier(
            Player player,
            Attribute attribute,
            UUID modifierUUID
    ) {
        AttributeInstance inst = player.getAttribute(attribute);
        if (inst != null) {
            inst.removeModifier(modifierUUID);
        }
    }
}
