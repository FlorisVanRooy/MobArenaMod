package com.derp.derpymod.arena;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

// Mutation strategy
public interface IWaveMutation {
    int   weight();
    void  apply(ServerLevel lvl);
    Component message();
}
