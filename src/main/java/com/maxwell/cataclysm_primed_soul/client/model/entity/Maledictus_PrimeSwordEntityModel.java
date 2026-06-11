package com.maxwell.cataclysm_primed_soul.client.model.entity;



import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.sub.Maledictus_PrimeSwordEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
@SuppressWarnings("removal")
public class Maledictus_PrimeSwordEntityModel extends HierarchicalModel<Maledictus_PrimeSwordEntity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Primed_Soul.MODID, "maledictusswordentitymodel"), "main");
    private final ModelPart Sword;

    public Maledictus_PrimeSwordEntityModel(ModelPart root) {
        this.Sword = root.getChild("Sword");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition Sword = partdefinition.addOrReplaceChild("Sword", CubeListBuilder.create().texOffs(0, 75).addBox(-1.0F, -2.0F, 18.0F, 2.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(44, 84).addBox(-1.5F, -5.0F, 16.0F, 3.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(54, 84).addBox(0.0F, 0.0F, 4.0F, 0.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(20, 84).addBox(0.0F, 0.0F, -14.7F, 0.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-0.5F, -1.4986F, -38.2074F, 1.0F, 1.0F, 58.0F, new CubeDeformation(0.0F))
                .texOffs(84, 59).addBox(0.0F, -4.0F, 4.0F, 0.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(32, 84).addBox(0.0F, -4.0F, -14.7F, 0.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(76, 75).addBox(0.0F, -1.9518F, -27.8769F, 0.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -1.0F, -1.5708F, 0.0F, 0.0F));
        PartDefinition cube_r1 = Sword.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(62, 75).addBox(0.0F, -1.0F, -5.0F, 0.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.4993F, -16.2463F, -0.3054F, 0.0F, 0.0F));
        PartDefinition cube_r2 = Sword.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 59).addBox(0.0F, -1.0F, -12.0F, 0.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.4993F, 2.4537F, -0.3054F, 0.0F, 0.0F));
        PartDefinition cube_r3 = Sword.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(48, 75).addBox(0.0F, -1.0F, -4.0F, 0.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.2F, -17.2F, 0.3054F, 0.0F, 0.0F));
        PartDefinition cube_r4 = Sword.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(56, 59).addBox(0.0F, -1.0F, -11.0F, 0.0F, 2.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(28, 59).addBox(0.0F, -1.0F, -11.0F, 0.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.2F, 1.5F, 0.3054F, 0.0F, 0.0F));
        PartDefinition cube_r5 = Sword.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(34, 75).addBox(0.0F, -1.0F, -5.5F, 0.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.4F, 14.9F, -0.3054F, 0.0F, 0.0F));
        PartDefinition cube_r6 = Sword.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(20, 75).addBox(0.0F, -1.0F, -5.5F, 0.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.6F, 14.9F, 0.3054F, 0.0F, 0.0F));
        PartDefinition cube_r7 = Sword.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(66, 84).addBox(-0.5F, -1.0F, -1.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -1.0F, 17.0F, 0.7418F, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(Maledictus_PrimeSwordEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Sword.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return Sword;
    }
}