package com.derp.derpymod.handlers;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

@FunctionalInterface
public interface IRightClickHandler {
    /**
     * @return true if this click was “handled” (equivalent to your old if-branch),
     *         false if we should fall through (or do nothing).
     */
    boolean handle(ServerPlayer player, Level world, PlayerInteractEvent.RightClickItem event);
}
