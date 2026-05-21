package com.maxwell.cataclysm_primed_soul.client.render.entity;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.client.model.entity.Ignis_PrimeModel;
import com.maxwell.cataclysm_primed_soul.client.render.layer.Ignis_PrimeInterpolation_Layer;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.Ignis_PrimeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector4f;

import javax.annotation.Nullable;

@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
public class Ignis_PrimeRenderer extends MobRenderer<Ignis_PrimeEntity, Ignis_PrimeModel> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[7];
    private final java.util.Map<java.util.UUID, net.minecraft.world.phys.Vec3> prevLeftHandPositions = new java.util.HashMap<>();
    private final java.util.Map<java.util.UUID, net.minecraft.world.phys.Vec3> prevRightHandPositions = new java.util.HashMap<>();

    public Ignis_PrimeRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new Ignis_PrimeModel(renderManagerIn.bakeLayer(Ignis_PrimeModel.LAYER_LOCATION)), 1.0F);
        for (int i = 0; i < 7; ++i) {
            TEXTURES[i] = new ResourceLocation(Primed_Soul.MODID, "textures/entity/ignis_prime/ignis_prime_textures_" + i + ".png");
        }
        this.addLayer(new Ignis_PrimeInterpolation_Layer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Ignis_PrimeEntity entity) {
        return TEXTURES[0];
    }

    @Nullable
    @Override
    protected RenderType getRenderType(Ignis_PrimeEntity entity, boolean bodyVisible, boolean translucent, boolean outline) {
        if (entity.isInvisible()) return null;
        return RenderType.entityTranslucent(new ResourceLocation(Primed_Soul.MODID, "textures/entity/empty.png"));
    }

    @Override
    public void render(Ignis_PrimeEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, net.minecraft.client.renderer.MultiBufferSource bufferIn, int packedLightIn) {
        double renderPosX = net.minecraft.util.Mth.lerp(partialTicks, entityIn.xo, entityIn.getX());
        double renderPosY = net.minecraft.util.Mth.lerp(partialTicks, entityIn.yo, entityIn.getY());
        double renderPosZ = net.minecraft.util.Mth.lerp(partialTicks, entityIn.zo, entityIn.getZ());

        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

        net.minecraft.world.entity.Entity caughtEntity = entityIn.getCaughtEntity();
        int attackState = entityIn.getAttackState();
        boolean shouldShowTrail = attackState != 0 && attackState != 6 && attackState != 99 && (attackState < 21 || attackState > 24);

        if (caughtEntity != null || shouldShowTrail) {
            net.minecraft.world.phys.Vec3 rightHandPos = getRightHandPosition(entityIn, partialTicks, renderPosX, renderPosY, renderPosZ);
            net.minecraft.world.phys.Vec3 leftHandPos = getLeftHandPosition(entityIn, partialTicks, renderPosX, renderPosY, renderPosZ);

            if (caughtEntity != null) {
                caughtEntity.setPos(rightHandPos.x, rightHandPos.y, rightHandPos.z);
                caughtEntity.setOldPosAndRot();
            }

            if (shouldShowTrail) {
                spawnHandTrail(entityIn, entityIn.getUUID(), rightHandPos, prevRightHandPositions);
                spawnHandTrail(entityIn, entityIn.getUUID(), leftHandPos, prevLeftHandPositions);
            } else {
                prevRightHandPositions.remove(entityIn.getUUID());
                prevLeftHandPositions.remove(entityIn.getUUID());
            }
        }
    }

    @Override
    protected void scale(Ignis_PrimeEntity entity, PoseStack matrixStack, float partialTick) {
        float s = 1.3F;
        matrixStack.scale(s, s, s);
    }

    @Override
    protected int getBlockLightLevel(Ignis_PrimeEntity entityIn, BlockPos pos) {
        return 15;
    }

    @Override
    protected float getFlipDegrees(Ignis_PrimeEntity entity) {
        return 0.0F;
    }

    private void spawnHandTrail(Ignis_PrimeEntity entity, java.util.UUID uuid, net.minecraft.world.phys.Vec3 currentPos, java.util.Map<java.util.UUID, net.minecraft.world.phys.Vec3> prevPositions) {
        net.minecraft.world.level.Level level = entity.level();
        net.minecraft.world.phys.Vec3 prevPos = prevPositions.get(uuid);

        if (prevPos != null && currentPos.distanceToSqr(prevPos) < 100.0D) {
            int steps = Math.max(3, (int) (currentPos.distanceTo(prevPos) * 20));
            for (int i = 0; i < steps; i++) {
                double pct = (double) i / steps;
                double x = net.minecraft.util.Mth.lerp(pct, prevPos.x, currentPos.x);
                double y = net.minecraft.util.Mth.lerp(pct, prevPos.y, currentPos.y);
                double z = net.minecraft.util.Mth.lerp(pct, prevPos.z, currentPos.z);

                level.addParticle(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME,
                        x + (level.random.nextDouble() - 0.5) * 0.1,
                        y + (level.random.nextDouble() - 0.5) * 0.1,
                        z + (level.random.nextDouble() - 0.5) * 0.1,
                        0, 0, 0);
            }
        }
        prevPositions.put(uuid, currentPos);
    }

    private net.minecraft.world.phys.Vec3 getRightHandPosition(Ignis_PrimeEntity entity, float partialTicks, double entityX, double entityY, double entityZ) {
        PoseStack poseStack = createModelPose(entity, partialTicks);
        this.getModel().getArm_Right().translateAndRotate(poseStack);
        this.getModel().getR_Under().translateAndRotate(poseStack);
        this.getModel().getGrip_pos().translateAndRotate(poseStack);
        return toWorldPosition(poseStack, entityX, entityY, entityZ);
    }

    private net.minecraft.world.phys.Vec3 getLeftHandPosition(Ignis_PrimeEntity entity, float partialTicks, double entityX, double entityY, double entityZ) {
        PoseStack poseStack = createModelPose(entity, partialTicks);
        this.getModel().getArm_Left().translateAndRotate(poseStack);
        this.getModel().getL_Under().translateAndRotate(poseStack);
        return toWorldPosition(poseStack, entityX, entityY, entityZ);
    }

    private PoseStack createModelPose(Ignis_PrimeEntity entity, float partialTicks) {
        PoseStack poseStack = new PoseStack();
        float yaw = net.minecraft.util.Mth.lerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float s = 1.3F;

        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.scale(-s, -s, s);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        this.getModel().root().translateAndRotate(poseStack);
        this.getModel().getBoddies().translateAndRotate(poseStack);
        this.getModel().getBody_Upper().translateAndRotate(poseStack);
        return poseStack;
    }

    private net.minecraft.world.phys.Vec3 toWorldPosition(PoseStack poseStack, double entityX, double entityY, double entityZ) {
        Vector4f localPos = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
        localPos.mul(poseStack.last().pose());
        return new net.minecraft.world.phys.Vec3(entityX + localPos.x(), entityY + localPos.y(), entityZ + localPos.z());
    }
}
