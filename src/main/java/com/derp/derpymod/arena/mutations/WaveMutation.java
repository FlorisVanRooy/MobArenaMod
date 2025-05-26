package com.derp.derpymod.arena.mutations;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class WaveMutation {
    private static WaveMutation waveMutation;

    private WaveMutation() {

    }

    public static WaveMutation getInstance() {
        if (waveMutation == null) {
            waveMutation = new WaveMutation();
        }
        return waveMutation;
    }

    public void summonSilverfish(Player player) {
        Level world = player.level();
        Silverfish silverfish = new Silverfish(EntityType.SILVERFISH, world);
        silverfish.setPos(player.position());
        world.addFreshEntity(silverfish);
    }
}
