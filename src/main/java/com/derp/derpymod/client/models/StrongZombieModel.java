package com.derp.derpymod.client.models;


import com.derp.derpymod.DerpyMod;
import com.derp.derpymod.entities.StrongZombie;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class StrongZombieModel extends EntityModel<StrongZombie> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(DerpyMod.MODID, "strong_zombie"), "main");
	private final ModelPart body;

	public StrongZombieModel(ModelPart root) {
		this.body = root.getChild("body");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 26).addBox(-5.0F, -34.0F, -3.0F, 10.0F, 9.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition arms = body.addOrReplaceChild("arms", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		arms.addOrReplaceChild("rightarm", CubeListBuilder.create().texOffs(34, 26).addBox(-10.0F, -24.0F, -3.0F, 3.0F, 13.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(42, 0).addBox(-12.0F, -22.0F, -3.0F, 2.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		arms.addOrReplaceChild("leftarm", CubeListBuilder.create().texOffs(0, 42).addBox(7.0F, -24.0F, -3.0F, 3.0F, 13.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(46, 39).addBox(10.0F, -22.0F, -3.0F, 2.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition legs = body.addOrReplaceChild("legs", CubeListBuilder.create().texOffs(34, 45).addBox(2.0F, -13.0F, -2.0F, 3.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		legs.addOrReplaceChild("rightleg", CubeListBuilder.create().texOffs(18, 42).addBox(-5.0F, -13.0F, -2.0F, 3.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		legs.addOrReplaceChild("leftleg", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		body.addOrReplaceChild("belly", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -25.0F, -8.0F, 14.0F, 12.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(StrongZombie entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}