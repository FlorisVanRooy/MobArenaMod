package com.derp.derpymod.client.renderer;

import com.derp.derpymod.DerpyMod;
import com.derp.derpymod.client.models.StrongZombieModel;
import com.derp.derpymod.entities.StrongZombie;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class StrongZombieRenderer extends MobRenderer<StrongZombie, StrongZombieModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(DerpyMod.MODID, "textures/entities/strong_zombie.png");

    public StrongZombieRenderer(EntityRendererProvider.Context context) {
        super(context, new StrongZombieModel(context.bakeLayer(StrongZombieModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(StrongZombie entity) {
        return TEXTURE;
    }
}
