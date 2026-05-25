package com.maxwell.cataclysm_primed_soul.client.render.entity;

import com.github.L_Ender.cataclysm.client.render.CMRenderTypes;
import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.client.model.entity.Ignis_PrimeModel;
import com.maxwell.cataclysm_primed_soul.client.render.layer.Ignis_PrimeInterpolation_Layer;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.Ignis_PrimeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;

@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
public class Ignis_PrimeRenderer extends MobRenderer<Ignis_PrimeEntity, Ignis_PrimeModel> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[7];
    private static final ResourceLocation HAND_TRAIL_TEXTURE = new ResourceLocation("cataclysm", "textures/particle/storm.png");
    private static final int HAND_TRAIL_SAMPLES = 20;
    private static final double MIN_SAMPLE_DISTANCE = 0.05;
    private final java.util.Map<java.util.UUID, Deque<net.minecraft.world.phys.Vec3>> leftHandTrails = new java.util.HashMap<>();
    private final java.util.Map<java.util.UUID, Deque<net.minecraft.world.phys.Vec3>> rightHandTrails = new java.util.HashMap<>();
    private static final int MAX_SHADOWS = 5; 
    private final java.util.Map<java.util.UUID, Deque<ShadowPose>> shadowHistory = new java.util.HashMap<>();
    private final java.util.Map<java.util.UUID, Deque<net.minecraft.world.phys.Vec3>> coreTrails = new java.util.HashMap<>();

    private static class ShadowPose {
        final net.minecraft.world.phys.Vec3 pos;
        final float yaw;
        final int tick;
        ShadowPose(net.minecraft.world.phys.Vec3 p, float y, int t) { this.pos = p; this.yaw = y; this.tick = t; }
    }
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
        net.minecraft.world.entity.Entity caughtEntity = entityIn.getCaughtEntity();
        int attackState = entityIn.getAttackState();
        boolean shouldShowTrail = attackState != 0 && attackState != 6 && attackState != 99 && (attackState < 21 || attackState > 24);
        boolean isHighSpeed = attackState == 34 || attackState == 35 || entityIn.getDeltaMovement().lengthSqr() > 0.1D;

        if (isHighSpeed && !entityIn.isInvisible()) {
            Deque<ShadowPose> shadows = shadowHistory.computeIfAbsent(entityIn.getUUID(), id -> new ArrayDeque<>());
            if (entityIn.tickCount % 2 == 0) { 
                shadows.addFirst(new ShadowPose(new net.minecraft.world.phys.Vec3(renderPosX, renderPosY, renderPosZ), entityYaw, entityIn.tickCount));
                if (shadows.size() > MAX_SHADOWS) shadows.removeLast();
            }

            int i = 0;
            for (ShadowPose shadow : shadows) {
                float shadowAlpha = 0.3F - (i * 0.05F);
                if (shadowAlpha > 0) {
                    matrixStackIn.pushPose();
                    matrixStackIn.translate(shadow.pos.x - renderPosX, shadow.pos.y - renderPosY, shadow.pos.z - renderPosZ);
                    matrixStackIn.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - shadow.yaw));
                    float s = 1.3F;
                    matrixStackIn.scale(-s, -s, s);
                    matrixStackIn.translate(0.0F, -1.501F, 0.0F);

                    VertexConsumer shadowVc = bufferIn.getBuffer(RenderType.entityTranslucent(TEXTURES[0]));
                    this.model.renderToBuffer(matrixStackIn, shadowVc, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 0.4F, 0.8F, shadowAlpha);
                    matrixStackIn.popPose();
                }
                i++;
            }
        } else {
            shadowHistory.remove(entityIn.getUUID());
        }

        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

        boolean isUltimate = attackState >= 33 && attackState <= 36;
        if (isUltimate || attackState != 0) {
            net.minecraft.world.phys.Vec3 corePos = getCorePosition(entityIn, partialTicks, renderPosX, renderPosY, renderPosZ);
            net.minecraft.world.phys.Vec3 rightHandPos = getRightHandPosition(entityIn, partialTicks, renderPosX, renderPosY, renderPosZ);
            net.minecraft.world.phys.Vec3 leftHandPos = getLeftHandPosition(entityIn, partialTicks, renderPosX, renderPosY, renderPosZ);

            if (isUltimate) {

                updateTrailPoints(entityIn.getUUID(), corePos, coreTrails);
                renderHandTrail(entityIn, entityIn.getUUID(), coreTrails, renderPosX, renderPosY, renderPosZ, matrixStackIn, bufferIn, packedLightIn, 1.0F, 0.2F, 0.5F);
            }

            updateTrailPoints(entityIn.getUUID(), rightHandPos, rightHandTrails);
            updateTrailPoints(entityIn.getUUID(), leftHandPos, leftHandTrails);

            float pulse = isUltimate ? (float)Math.sin(entityIn.tickCount * 0.5F) * 0.2F + 1.0F : 1.0F;
            renderHandTrail(entityIn, entityIn.getUUID(), rightHandTrails, renderPosX, renderPosY, renderPosZ, matrixStackIn, bufferIn, packedLightIn, 1.0F, 0.4F * pulse, 1.0F);
            renderHandTrail(entityIn, entityIn.getUUID(), leftHandTrails, renderPosX, renderPosY, renderPosZ, matrixStackIn, bufferIn, packedLightIn, 0.4F * pulse, 0.8F, 1.0F);
        }
        if (caughtEntity != null || shouldShowTrail) {
            net.minecraft.world.phys.Vec3 rightHandPos = getRightHandPosition(entityIn, partialTicks, renderPosX, renderPosY, renderPosZ);
            net.minecraft.world.phys.Vec3 leftHandPos = getLeftHandPosition(entityIn, partialTicks, renderPosX, renderPosY, renderPosZ);
            if (caughtEntity != null) {
                caughtEntity.setPos(rightHandPos.x, rightHandPos.y, rightHandPos.z);
                caughtEntity.setOldPosAndRot();
            }
            if (shouldShowTrail) {
                updateTrailPoints(entityIn.getUUID(), rightHandPos, rightHandTrails);
                updateTrailPoints(entityIn.getUUID(), leftHandPos, leftHandTrails);
                renderHandTrail(entityIn, entityIn.getUUID(), rightHandTrails, renderPosX, renderPosY, renderPosZ, matrixStackIn, bufferIn, packedLightIn, 0.98F, 0.36F, 1.0F);
                renderHandTrail(entityIn, entityIn.getUUID(), leftHandTrails, renderPosX, renderPosY, renderPosZ, matrixStackIn, bufferIn, packedLightIn, 0.35F, 0.78F, 1.0F);
            } else {
                rightHandTrails.remove(entityIn.getUUID());
                leftHandTrails.remove(entityIn.getUUID());
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

    private void updateTrailPoints(java.util.UUID uuid, net.minecraft.world.phys.Vec3 currentPos, java.util.Map<java.util.UUID, Deque<net.minecraft.world.phys.Vec3>> trails) {
        Deque<net.minecraft.world.phys.Vec3> trail = trails.computeIfAbsent(uuid, id -> new ArrayDeque<>());
        net.minecraft.world.phys.Vec3 last = trail.peekLast();
        if (last != null) {
            double distSq = currentPos.distanceToSqr(last);
            if (distSq > 100.0D) {
                trail.clear();
            } else if (distSq < MIN_SAMPLE_DISTANCE * MIN_SAMPLE_DISTANCE) {
                return;
            }
        }
        trail.addLast(currentPos);
        while (trail.size() > HAND_TRAIL_SAMPLES) {
            trail.removeFirst();
        }
    }

    private void renderHandTrail(Ignis_PrimeEntity entity, java.util.UUID uuid,
                                 java.util.Map<java.util.UUID, Deque<net.minecraft.world.phys.Vec3>> trails,
                                 double entityX, double entityY, double entityZ, PoseStack poseStack,
                                 MultiBufferSource buffer, int packedLight, float red, float green, float blue) {
        Deque<net.minecraft.world.phys.Vec3> trail = trails.get(uuid);
        if (trail == null || trail.size() < 2 || entity.isInvisible()) return;
        poseStack.pushPose();
        poseStack.translate(-entityX, -entityY, -entityZ);
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();
        VertexConsumer consumer = buffer.getBuffer(CMRenderTypes.getLightTrailEffect(HAND_TRAIL_TEXTURE));
        net.minecraft.world.phys.Vec3[] points = trail.toArray(new net.minecraft.world.phys.Vec3[0]);
        net.minecraft.world.phys.Vec3 cameraPos = net.minecraft.client.Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition();
        int attackState = entity.getAttackState();
        boolean isUltimate = attackState >= 33 && attackState <= 36;
        for (int i = 1; i < points.length; i++) {
            float ratio = (float) i / (float) (points.length - 1);
            float prevRatio = (float) (i - 1) / (float) (points.length - 1);
            float width = isUltimate ? (1.0F + ratio * 2.4F) : (0.2F + ratio * 0.8F);
            float alpha = isUltimate ? (0.4F + ratio * 0.6F) : (0.2F + ratio * 0.6F);
            addTrailSegment(consumer, matrix, normal, points[i - 1], points[i], width, red, green, blue, alpha, prevRatio, ratio, packedLight, cameraPos);
        }
        poseStack.popPose();
    }

    private void addTrailSegment(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal,
                                 net.minecraft.world.phys.Vec3 from, net.minecraft.world.phys.Vec3 to, float width,
                                 float red, float green, float blue, float alpha, float u0, float u1, int light,
                                 net.minecraft.world.phys.Vec3 cameraPos) {
        net.minecraft.world.phys.Vec3 direction = to.subtract(from);
        if (direction.lengthSqr() < 1.0E-5D) return;
        net.minecraft.world.phys.Vec3 toCamera = from.subtract(cameraPos).normalize();
        net.minecraft.world.phys.Vec3 side = direction.cross(toCamera).normalize().scale(width);
        addTrailVertex(consumer, matrix, normal, from.add(side), red, green, blue, alpha, u0, 0.0F, light);
        addTrailVertex(consumer, matrix, normal, to.add(side), red, green, blue, alpha, u1, 0.0F, light);
        addTrailVertex(consumer, matrix, normal, to.subtract(side), red, green, blue, alpha, u1, 1.0F, light);
        addTrailVertex(consumer, matrix, normal, from.subtract(side), red, green, blue, alpha, u0, 1.0F, light);
    }

    private void addTrailVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, net.minecraft.world.phys.Vec3 pos,
                                float red, float green, float blue, float alpha, float u, float v, int light) {
        consumer.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
                .color(red, green, blue, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normal, 0.0F, 1.0F, 0.0F)
                .endVertex();
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
        this.getModel().getHand_pos().translateAndRotate(poseStack);
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
    private net.minecraft.world.phys.Vec3 getCorePosition(Ignis_PrimeEntity entity, float partialTicks, double entityX, double entityY, double entityZ) {
        PoseStack poseStack = createModelPose(entity, partialTicks);
        this.getModel().getBoddies().translateAndRotate(poseStack);
        this.getModel().getBody_Upper().translateAndRotate(poseStack);
        return toWorldPosition(poseStack, entityX, entityY, entityZ);
    }
}
