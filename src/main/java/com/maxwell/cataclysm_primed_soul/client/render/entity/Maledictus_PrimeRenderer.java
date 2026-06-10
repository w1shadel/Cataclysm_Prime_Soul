package com.maxwell.cataclysm_primed_soul.client.render.entity;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.client.model.entity.Maledictus_PrimeModel;
import com.maxwell.cataclysm_primed_soul.client.render.layer.Maledictus_PrimeGhost_Layer;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.Maledictus_PrimeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
public class Maledictus_PrimeRenderer extends MobRenderer<Maledictus_PrimeEntity, Maledictus_PrimeModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            Primed_Soul.MODID,
            "textures/entity/maledictus_prime/maledictus_prime_armor.png"
    );

    public Maledictus_PrimeRenderer(EntityRendererProvider.Context context) {
        super(context, new Maledictus_PrimeModel(context.bakeLayer(Maledictus_PrimeModel.LAYER_LOCATION)), 1.0F);
        this.addLayer(new Maledictus_PrimeGhost_Layer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Maledictus_PrimeEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(Maledictus_PrimeEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(0.96F, 0.96F, 0.96F);
    }

    @Override
    protected int getBlockLightLevel(Maledictus_PrimeEntity entity, BlockPos pos) {
        return Math.min(super.getBlockLightLevel(entity, pos), 2);
    }

    private net.minecraft.world.phys.Vec3 getRightHandPosition(Maledictus_PrimeEntity entity, float partialTicks, double entityX, double entityY, double entityZ) {
        PoseStack poseStack = createModelPose(entity, partialTicks);
        this.getModel().getBody().translateAndRotate(poseStack);
        this.getModel().getUpperBody().translateAndRotate(poseStack);
        this.getModel().getRight_Shoulder().translateAndRotate(poseStack);
        this.getModel().getRightArm().translateAndRotate(poseStack);
        this.getModel().getRight_ArmUnder().translateAndRotate(poseStack);
        this.getModel().getRightHand().translateAndRotate(poseStack);
        return toWorldPosition(poseStack, entityX, entityY, entityZ);
    }

    private PoseStack createModelPose(Maledictus_PrimeEntity entity, float partialTicks) {
        PoseStack poseStack = new PoseStack();
        float yaw = net.minecraft.util.Mth.lerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float s = 1.3F;
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.scale(-s, -s, s);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        this.getModel().root().translateAndRotate(poseStack);
        return poseStack;
    }

    private net.minecraft.world.phys.Vec3 toWorldPosition(PoseStack poseStack, double entityX, double entityY, double entityZ) {
        org.joml.Vector4f localPos = new org.joml.Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
        localPos.mul(poseStack.last().pose());
        return new net.minecraft.world.phys.Vec3(entityX + localPos.x(), entityY + localPos.y(), entityZ + localPos.z());
    }

    @Override
    public void render(Maledictus_PrimeEntity pEntity, float pEntityYaw, float partialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, partialTicks, pPoseStack, pBuffer, pPackedLight);
        double renderPosX = net.minecraft.util.Mth.lerp(partialTicks, pEntity.xo, pEntity.getX());
        double renderPosY = net.minecraft.util.Mth.lerp(partialTicks, pEntity.yo, pEntity.getY());
        double renderPosZ = net.minecraft.util.Mth.lerp(partialTicks, pEntity.zo, pEntity.getZ());
        net.minecraft.world.entity.Entity grabbedEntity = pEntity.getControllingPassenger();
        if (grabbedEntity != null && pEntity.getAttackState() == Maledictus_PrimeEntity.ATTACK_GRAB_SUCCESS) {
            net.minecraft.world.phys.Vec3 handPos = getRightHandPosition(pEntity, partialTicks, renderPosX, renderPosY, renderPosZ);
            grabbedEntity.setPos(handPos.x, handPos.y, handPos.z);
            grabbedEntity.setOldPosAndRot();
        }
    }
}
