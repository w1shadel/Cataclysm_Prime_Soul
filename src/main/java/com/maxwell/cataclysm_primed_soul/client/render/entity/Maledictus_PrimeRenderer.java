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
    private static final int MAX_TRAIL_SAMPLES = 12;
    private final java.util.Map<Maledictus_PrimeEntity, Deque<SwordSegment>> swordTrails = new java.util.WeakHashMap<>();

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
        int attackState = pEntity.getAttackState();
        boolean isAttacking = attackState != 0
                && attackState != Maledictus_PrimeEntity.ATTACK_COUNTER_START
                && attackState != Maledictus_PrimeEntity.ATTACK_COUNTER_FAIL
                && attackState != Maledictus_PrimeEntity.BACKSTEP
                && attackState != Maledictus_PrimeEntity.BACKSTEP_BEFORE_CHARGE;
        if (isAttacking && !pEntity.isInvisible()) {
            SwordSegment currentSegment = getSwordPositions(pEntity, partialTicks, renderPosX, renderPosY, renderPosZ);
            updateSwordTrail(pEntity, currentSegment);
            drawSwordTrail(pEntity, renderPosX, renderPosY, renderPosZ, pPoseStack, pBuffer, pPackedLight);
        } else {
            swordTrails.remove(pEntity);
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

    private void drawSwordTrail(Maledictus_PrimeEntity entity, double entityX, double entityY, double entityZ, PoseStack poseStack, MultiBufferSource buffer, int light) {
        Deque<SwordSegment> trail = swordTrails.get(entity);
        if (trail == null || trail.size() < 2) return;
        poseStack.pushPose();
        poseStack.translate(-entityX, -entityY, -entityZ);
        PoseStack.Pose lastPose = poseStack.last();
        Matrix4f matrix = lastPose.pose();
        Matrix3f normal = lastPose.normal();
        ResourceLocation trailTexture = new ResourceLocation("cataclysm", "textures/particle/storm.png");
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(trailTexture));
        SwordSegment[] segments = trail.toArray(new SwordSegment[0]);
        float r = 0.337F;
        float g = 0.925F;
        float b = 0.800F;
        for (int i = 1; i < segments.length; i++) {
            float ratio = (float) i / (float) (segments.length - 1);
            float prevRatio = (float) (i - 1) / (float) (segments.length - 1);
            double prevTipFactor = Math.pow(prevRatio, 2.5D);
            double currTipFactor = Math.pow(ratio, 2.5D);
            SwordSegment prev = segments[i - 1];
            SwordSegment curr = segments[i];
            Vec3 prevDecayedTip = prev.base.add(prev.tip.subtract(prev.base).scale(prevTipFactor));
            Vec3 currDecayedTip = curr.base.add(curr.tip.subtract(curr.base).scale(currTipFactor));
            float alpha = ratio * 0.75F;
            addTrailVertex(consumer, matrix, normal, prev.base, r, g, b, alpha, prevRatio, 0.0F, light);
            addTrailVertex(consumer, matrix, normal, prevDecayedTip, r, g, b, alpha, prevRatio, 1.0F, light);
            addTrailVertex(consumer, matrix, normal, currDecayedTip, r, g, b, alpha, ratio, 1.0F, light);
            addTrailVertex(consumer, matrix, normal, curr.base, r, g, b, alpha, ratio, 0.0F, light);
        }
        poseStack.popPose();
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