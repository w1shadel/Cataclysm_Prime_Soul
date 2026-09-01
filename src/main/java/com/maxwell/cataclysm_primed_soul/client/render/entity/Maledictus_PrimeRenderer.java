package com.maxwell.cataclysm_primed_soul.client.render.entity;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.client.model.entity.Maledictus_PrimeModel;
import com.maxwell.cataclysm_primed_soul.client.render.layer.Maledictus_PrimeGhost_Layer;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.Maledictus_PrimeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayDeque;
import java.util.Deque;

@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
public class Maledictus_PrimeRenderer extends MobRenderer<Maledictus_PrimeEntity, Maledictus_PrimeModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            Primed_Soul.MODID,
            "textures/entity/maledictus_prime/maledictus_prime_armor.png"
    );
    private static final int MAX_TRAIL_SAMPLES = 30;
    private static final int TRAIL_HOLD_TICKS = 16;
    private static final ResourceLocation TRAIL_TEXTURE = new ResourceLocation(
            "cataclysm", "textures/particle/storm.png"
    );
    private static final ResourceLocation GLITCH_TEXTURE = new ResourceLocation(
            Primed_Soul.MODID, "textures/entity/maledictus_prime/maledictus_prime_ghost.png"
    );
    private final java.util.Map<Maledictus_PrimeEntity, Deque<SwordSegment>> swordTrails = new java.util.WeakHashMap<>();
    private final java.util.Map<Maledictus_PrimeEntity, Integer> trailHoldTicks = new java.util.WeakHashMap<>();

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

    @Override
    public void render(Maledictus_PrimeEntity pEntity, float pEntityYaw, float partialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, partialTicks, pPoseStack, pBuffer, pPackedLight);
        double renderPosX = net.minecraft.util.Mth.lerp(partialTicks, pEntity.xo, pEntity.getX());
        double renderPosY = net.minecraft.util.Mth.lerp(partialTicks, pEntity.yo, pEntity.getY());
        double renderPosZ = net.minecraft.util.Mth.lerp(partialTicks, pEntity.zo, pEntity.getZ());
        if (pEntity.getAttackState() == Maledictus_PrimeEntity.ATTACK_DEAD) {
            renderDeathLight(pEntity, partialTicks, pPoseStack, pBuffer, renderPosX, renderPosY, renderPosZ);
        }
        int attackState = pEntity.getAttackState();
        boolean isAttacking = attackState != 0
                && attackState != Maledictus_PrimeEntity.ATTACK_COUNTER_START
                && attackState != Maledictus_PrimeEntity.ATTACK_COUNTER_FAIL
                && attackState != Maledictus_PrimeEntity.BACKSTEP
                && attackState != Maledictus_PrimeEntity.BACKSTEP_BEFORE_CHARGE;
        if (isAttacking && !pEntity.isInvisible()) {
            SwordSegment currentSegment = getSwordPositions(pEntity, partialTicks, renderPosX, renderPosY, renderPosZ);
            updateSwordTrail(pEntity, currentSegment);
            trailHoldTicks.put(pEntity, TRAIL_HOLD_TICKS);
            drawSwordTrail(pEntity, renderPosX, renderPosY, renderPosZ, pPoseStack, pBuffer, pPackedLight, 1.0F);
        } else {
            int remaining = trailHoldTicks.getOrDefault(pEntity, 0);
            if (remaining > 0 && !pEntity.isInvisible()) {
                trailHoldTicks.put(pEntity, remaining - 1);
                drawSwordTrail(pEntity, renderPosX, renderPosY, renderPosZ, pPoseStack, pBuffer, pPackedLight,
                        remaining / (float) TRAIL_HOLD_TICKS);
            } else {
                swordTrails.remove(pEntity);
                trailHoldTicks.remove(pEntity);
            }
        }
        if (pEntity.isEcho() && !pEntity.isInvisible()) {
            renderGlitchEcho(pEntity, partialTicks, pPoseStack, pBuffer, pPackedLight);
        }
        if (!pEntity.isInvisible() && pEntity.isAlive()) {
            renderEyeFlash(pEntity, partialTicks, pPoseStack, pBuffer, pPackedLight,
                    renderPosX, renderPosY, renderPosZ);
        }
        Entity grabbedEntity = pEntity.getControllingPassenger();
        if (grabbedEntity != null && pEntity.getAttackState() == Maledictus_PrimeEntity.ATTACK_GRAB_SUCCESS) {
            Vec3 handPos = getRightHandPosition(pEntity, partialTicks, renderPosX, renderPosY, renderPosZ);
            grabbedEntity.setPos(handPos.x, handPos.y, handPos.z);
            grabbedEntity.setOldPosAndRot();
        }
    }

    private SwordSegment getSwordPositions(Maledictus_PrimeEntity entity, float partialTicks, double entityX, double entityY, double entityZ) {
        PoseStack poseStack = createModelPose(entity, partialTicks);
        Maledictus_PrimeModel model = this.getModel();
        model.getBody().translateAndRotate(poseStack);
        model.getUpperBody().translateAndRotate(poseStack);
        model.getRight_Shoulder().translateAndRotate(poseStack);
        model.getRightArm().translateAndRotate(poseStack);
        model.getRight_ArmUnder().translateAndRotate(poseStack);
        model.getSword().translateAndRotate(poseStack);
        poseStack.pushPose();
        model.getSwordTip().translateAndRotate(poseStack);
        Vec3 tipPos = toWorldPosition(poseStack, entityX, entityY, entityZ);
        poseStack.popPose();
        poseStack.pushPose();
        model.getSwordTip2().translateAndRotate(poseStack);
        Vec3 basePos = toWorldPosition(poseStack, entityX, entityY, entityZ);
        poseStack.popPose();
        return new SwordSegment(basePos, tipPos);
    }

    private void updateSwordTrail(Maledictus_PrimeEntity entity, SwordSegment current) {
        Deque<SwordSegment> trail = swordTrails.computeIfAbsent(entity, id -> new ArrayDeque<>());
        SwordSegment last = trail.peekLast();
        if (last != null) {
            if (current.tip.distanceToSqr(last.tip) < 0.0025D) {
                return;
            }
        }
        trail.addLast(current);
        while (trail.size() > MAX_TRAIL_SAMPLES) {
            trail.removeFirst();
        }
    }

    private void drawSwordTrail(Maledictus_PrimeEntity entity, double entityX, double entityY, double entityZ,
                                PoseStack poseStack, MultiBufferSource buffer, int light, float fade) {
        Deque<SwordSegment> trail = swordTrails.get(entity);
        if (trail == null || trail.size() < 2) return;
        poseStack.pushPose();
        poseStack.translate(-entityX, -entityY, -entityZ);
        PoseStack.Pose lastPose = poseStack.last();
        Matrix4f matrix = lastPose.pose();
        Matrix3f normal = lastPose.normal();
        VertexConsumer outer = buffer.getBuffer(RenderType.entityTranslucentEmissive(TRAIL_TEXTURE));
        VertexConsumer core = buffer.getBuffer(RenderType.entityTranslucentEmissive(TRAIL_TEXTURE));
        SwordSegment[] segments = trail.toArray(new SwordSegment[0]);
        Vec3 cameraPos = net.minecraft.client.Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition();
        for (int i = 1; i < segments.length; i++) {
            float ratio = (float) i / (float) (segments.length - 1);
            float prevRatio = (float) (i - 1) / (float) (segments.length - 1);
            SwordSegment prev = segments[i - 1];
            SwordSegment curr = segments[i];
            drawSwordRibbon(outer, matrix, normal, prev, curr, prevRatio, ratio,
                    0.20F, 0.82F, 0.74F, ratio * 0.45F * fade, 2.6D, 0.28D, cameraPos, light);
            drawSwordRibbon(core, matrix, normal, prev, curr, prevRatio, ratio,
                    0.72F, 1.0F, 0.94F, ratio * 0.85F * fade, 1.35D, 0.14D, cameraPos, light);
        }
        poseStack.popPose();
    }

    private void drawSwordRibbon(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal,
                                 SwordSegment prev, SwordSegment curr, float prevRatio, float ratio,
                                 float red, float green, float blue, float alpha,
                                 double tipExponent, double width, Vec3 cameraPos, int light) {
        double prevTipFactor = Math.pow(prevRatio, tipExponent);
        double currTipFactor = Math.pow(ratio, tipExponent);
        Vec3 prevTip = prev.base.add(prev.tip.subtract(prev.base).scale(prevTipFactor));
        Vec3 currTip = curr.base.add(curr.tip.subtract(curr.base).scale(currTipFactor));
        Vec3 prevWidth = getTrailWidth(prev.base, prevTip, cameraPos, width);
        Vec3 currWidth = getTrailWidth(curr.base, currTip, cameraPos, width);
        addTrailVertex(consumer, matrix, normal, prev.base.subtract(prevWidth), red, green, blue, alpha, prevRatio, 0.0F, light);
        addTrailVertex(consumer, matrix, normal, prevTip.add(prevWidth), red, green, blue, alpha, prevRatio, 1.0F, light);
        addTrailVertex(consumer, matrix, normal, currTip.add(currWidth), red, green, blue, alpha, ratio, 1.0F, light);
        addTrailVertex(consumer, matrix, normal, curr.base.subtract(currWidth), red, green, blue, alpha, ratio, 0.0F, light);
    }

    private Vec3 getTrailWidth(Vec3 base, Vec3 tip, Vec3 cameraPos, double width) {
        Vec3 blade = tip.subtract(base);
        Vec3 toCamera = cameraPos.subtract(base);
        Vec3 side = blade.cross(toCamera);
        if (side.lengthSqr() < 0.0001D) {
            side = new Vec3(0.0D, 1.0D, 0.0D);
        }
        return side.normalize().scale(width);
    }

    private void addTrailVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, Vec3 pos,
                                float red, float green, float blue, float alpha, float u, float v, int light) {
        consumer.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
                .color(red, green, blue, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normal, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    private void renderGlitchEcho(Maledictus_PrimeEntity entity, float partialTicks, PoseStack poseStack,
                                  MultiBufferSource buffer, int light) {
        float time = entity.tickCount + partialTicks;
        float jitter = 0.035F + 0.025F * (float) Math.sin(time * 1.7F);
        VertexConsumer glitch = buffer.getBuffer(RenderType.entityTranslucentEmissive(GLITCH_TEXTURE));
        float[][] passes = {
                {jitter, 0.02F, 0.95F, 0.22F, 0.92F, 0.52F},
                {-jitter * 1.4F, -0.01F, 0.25F, 0.90F, 1.0F, 0.42F},
                {0.0F, 0.0F, 0.72F, 0.98F, 0.92F, 0.26F}
        };
        for (float[] pass : passes) {
            poseStack.pushPose();
            float yaw = Mth.lerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - yaw));
            poseStack.scale(-0.96F, -0.96F, 0.96F);
            poseStack.translate(0.0F, -1.501F, 0.0F);
            poseStack.translate(pass[0], pass[1] + (float) Math.sin(time * 2.9F) * 0.018F, 0.0F);
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees((float) Math.sin(time * 2.3F) * 1.4F));
            poseStack.scale(1.0F + pass[0] * 0.35F, 1.0F - pass[0] * 0.2F, 1.0F);
            this.getModel().renderToBuffer(poseStack, glitch, 15728880, OverlayTexture.NO_OVERLAY,
                    pass[2], pass[3], pass[4], pass[5]);
            poseStack.popPose();
        }
    }

    private void renderEyeFlash(Maledictus_PrimeEntity entity, float partialTicks, PoseStack poseStack,
                                MultiBufferSource buffer, int light, double entityX, double entityY, double entityZ) {
        Vec3 rightEye = getEyePosition(entity, partialTicks, entityX, entityY, entityZ, this.getModel().getRightEye());
        Vec3 headOrigin = getHeadPosition(entity, partialTicks, entityX, entityY, entityZ);
        Vec3 headForwardPoint = getHeadForwardPoint(entity, partialTicks, entityX, entityY, entityZ);
        Vec3 forward = headForwardPoint.subtract(headOrigin).normalize();
        float pulse = 0.72F + 0.28F * (float) Math.sin((entity.tickCount + partialTicks) * 0.32F);
        poseStack.pushPose();
        poseStack.translate(-entityX, -entityY, -entityZ);
        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer beam = buffer.getBuffer(RenderType.lightning());
        renderEyeRay(beam, matrix, rightEye, forward, pulse, 0.72F);
        renderEyeBloom(beam, matrix, rightEye, forward, pulse);
        poseStack.popPose();
    }

    private void renderDeathLight(Maledictus_PrimeEntity entity, float partialTicks, PoseStack poseStack,
                                  MultiBufferSource buffer, double entityX, double entityY, double entityZ) {
        float age = entity.getAttackTicks() + partialTicks;
        float start = 9.2F;
        if (age < start || age >= 40.0F) return;
        float progress = (age - start) / (40.0F - start);
        float alpha = Math.min(1.0F, (age - start) / 4.0F) * (1.0F - progress);
        Vec3 origin = getTorsoPosition(entity, partialTicks, entityX, entityY, entityZ);
        poseStack.pushPose();
        poseStack.translate(-entityX, -entityY, -entityZ);
        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer beam = buffer.getBuffer(RenderType.lightning());
        float pulse = 0.9F + 0.1F * (float) Math.sin((entity.tickCount + partialTicks) * 0.7F);
        Vec3 up = new Vec3(0.0D, 1.0D, 0.0D);
        addQuad(beam, matrix, origin.add(-0.16D * pulse, 0, 0), origin.add(0.16D * pulse, 0, 0),
                origin.add(0.05D, 1.35D * pulse, 0), origin.add(-0.05D, 1.35D * pulse, 0),
                150, 235, 255, (int) (230.0F * alpha));
        addQuad(beam, matrix, origin.add(0, 0, -0.16D * pulse), origin.add(0, 0, 0.16D * pulse),
                origin.add(0, 1.05D * pulse, 0.05D), origin.add(0, 1.05D * pulse, -0.05D),
                220, 255, 255, (int) (190.0F * alpha));
        poseStack.popPose();
    }

    private Vec3 getTorsoPosition(Maledictus_PrimeEntity entity, float partialTicks,
                                  double entityX, double entityY, double entityZ) {
        PoseStack poseStack = createModelPose(entity, partialTicks);
        Maledictus_PrimeModel model = this.getModel();
        model.getBody().translateAndRotate(poseStack);
        model.getUpperBody().translateAndRotate(poseStack);
        return toWorldPosition(poseStack, 0.0D, 0.35D, 0.0D, entityX, entityY, entityZ);
    }

    private Vec3 getEyePosition(Maledictus_PrimeEntity entity, float partialTicks,
                                double entityX, double entityY, double entityZ,
                                net.minecraft.client.model.geom.ModelPart eye) {
        PoseStack poseStack = createModelPose(entity, partialTicks);
        Maledictus_PrimeModel model = this.getModel();
        model.getBody().translateAndRotate(poseStack);
        model.getUpperBody().translateAndRotate(poseStack);
        model.getHead().translateAndRotate(poseStack);
        eye.translateAndRotate(poseStack);
        return toWorldPosition(poseStack, entityX, entityY, entityZ);
    }

    private Vec3 getHeadPosition(Maledictus_PrimeEntity entity, float partialTicks,
                                 double entityX, double entityY, double entityZ) {
        PoseStack poseStack = createModelPose(entity, partialTicks);
        Maledictus_PrimeModel model = this.getModel();
        model.getBody().translateAndRotate(poseStack);
        model.getUpperBody().translateAndRotate(poseStack);
        model.getHead().translateAndRotate(poseStack);
        return toWorldPosition(poseStack, entityX, entityY, entityZ);
    }

    private Vec3 getHeadForwardPoint(Maledictus_PrimeEntity entity, float partialTicks,
                                     double entityX, double entityY, double entityZ) {
        PoseStack poseStack = createModelPose(entity, partialTicks);
        Maledictus_PrimeModel model = this.getModel();
        model.getBody().translateAndRotate(poseStack);
        model.getUpperBody().translateAndRotate(poseStack);
        model.getHead().translateAndRotate(poseStack);
        return toWorldPosition(poseStack, 0.0D, 0.0D, -1.0D, entityX, entityY, entityZ);
    }

    private void renderEyeRay(VertexConsumer consumer, Matrix4f matrix, Vec3 origin, Vec3 forward,
                              float pulse, float length) {
        Vec3 side = forward.cross(new Vec3(0.0D, 1.0D, 0.0D));
        if (side.lengthSqr() < 0.001D) side = new Vec3(1.0D, 0.0D, 0.0D);
        side = side.normalize().scale(0.035D * pulse);
        Vec3 end = origin.add(forward.scale(length * (0.82D + pulse * 0.18D)));
        addQuad(consumer, matrix, origin.subtract(side), origin.add(side),
                end.add(side.scale(0.35D)), end.subtract(side.scale(0.35D)), 110, 245, 255, 190);
    }

    private void renderEyeBloom(VertexConsumer consumer, Matrix4f matrix, Vec3 origin, Vec3 forward, float pulse) {
        Vec3 side = forward.cross(new Vec3(0.0D, 1.0D, 0.0D));
        if (side.lengthSqr() < 0.001D) side = new Vec3(1.0D, 0.0D, 0.0D);
        side = side.normalize().scale(0.11D * pulse);
        Vec3 up = new Vec3(0.0D, 1.0D, 0.0D).scale(0.11D * pulse);
        addQuad(consumer, matrix, origin.subtract(side), origin.add(up),
                origin.add(side), origin.subtract(up), 220, 255, 255, 235);
    }

    private void addQuad(VertexConsumer consumer, Matrix4f matrix, Vec3 a, Vec3 b, Vec3 c, Vec3 d,
                         int red, int green, int blue, int alpha) {
        consumer.vertex(matrix, (float) a.x, (float) a.y, (float) a.z).color(red, green, blue, alpha).endVertex();
        consumer.vertex(matrix, (float) b.x, (float) b.y, (float) b.z).color(red, green, blue, alpha).endVertex();
        consumer.vertex(matrix, (float) c.x, (float) c.y, (float) c.z).color(red, green, blue, alpha).endVertex();
        consumer.vertex(matrix, (float) d.x, (float) d.y, (float) d.z).color(red, green, blue, alpha).endVertex();
    }

    private Vec3 getRightHandPosition(Maledictus_PrimeEntity entity, float partialTicks, double entityX, double entityY, double entityZ) {
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
        float yaw = Mth.lerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float s = 0.93F;
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.scale(-s, -s, s);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        this.getModel().root().translateAndRotate(poseStack);
        return poseStack;
    }

    private Vec3 toWorldPosition(PoseStack poseStack, double x, double y, double z, double entityX, double entityY, double entityZ) {
        Vector4f localPos = new Vector4f((float) x, (float) y, (float) z, 1.0F);
        localPos.mul(poseStack.last().pose());
        return new Vec3(entityX + localPos.x(), entityY + localPos.y(), entityZ + localPos.z());
    }

    private Vec3 toWorldPosition(PoseStack poseStack, double entityX, double entityY, double entityZ) {
        return toWorldPosition(poseStack, 0.0, 0.0, 0.0, entityX, entityY, entityZ);
    }

    private static class SwordSegment {
        final Vec3 base;
        final Vec3 tip;

        SwordSegment(Vec3 base, Vec3 tip) {
            this.base = base;
            this.tip = tip;
        }
    }
}
