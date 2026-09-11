package com.maxwell.cataclysm_primed_soul.client.render.entity;

import com.github.L_Ender.cataclysm.client.particle.Options.RingParticleOptions;
import com.github.L_Ender.cataclysm.client.particle.RingParticle.EnumRingBehavior;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.WeakHashMap;

@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
public class Maledictus_PrimeRenderer extends MobRenderer<Maledictus_PrimeEntity, Maledictus_PrimeModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            Primed_Soul.MODID,
            "textures/entity/maledictus_prime/maledictus_prime_armor.png"
    );
    private static final int MAX_TRAIL_SAMPLES = 70;
    private static final int TRAIL_HOLD_TICKS = 36;
    private static final ResourceLocation TRAIL_TEXTURE = new ResourceLocation(
            "cataclysm", "textures/particle/storm.png"
    );
    private static final ResourceLocation GLITCH_TEXTURE = new ResourceLocation(
            Primed_Soul.MODID, "textures/entity/maledictus_prime/maledictus_prime_ghost.png"
    );
    private static final int MAX_SHADOWS = 4;
    private final Map<Maledictus_PrimeEntity, Deque<ShadowPose>> shadowHistory = new WeakHashMap<>();
    private final Map<Maledictus_PrimeEntity, Deque<SwordSegment>> swordTrails = new WeakHashMap<>();
    private final Map<Maledictus_PrimeEntity, Integer> trailHoldTicks = new WeakHashMap<>();

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
        return entity.isEcho() ? 12 : Math.min(super.getBlockLightLevel(entity, pos), 2);
    }

    @Override
    public void render(Maledictus_PrimeEntity pEntity, float pEntityYaw, float partialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        double renderPosX = net.minecraft.util.Mth.lerp(partialTicks, pEntity.xo, pEntity.getX());
        double renderPosY = net.minecraft.util.Mth.lerp(partialTicks, pEntity.yo, pEntity.getY());
        double renderPosZ = net.minecraft.util.Mth.lerp(partialTicks, pEntity.zo, pEntity.getZ());
        renderMovementAfterimages(pEntity, pEntityYaw, partialTicks, pPoseStack, pBuffer,
                renderPosX, renderPosY, renderPosZ);
        if (pEntity.isEcho()) {
            renderOverhauledEcho(pEntity, partialTicks, pPoseStack, pBuffer, pPackedLight);
        } else {
            super.render(pEntity, pEntityYaw, partialTicks, pPoseStack, pBuffer, pPackedLight);
        }
        if (pEntity.getAttackState() == Maledictus_PrimeEntity.ATTACK_EXCALIBUR_END
                && pEntity.getAttackTicks() == 19) {
            pEntity.level().addParticle(
                    new RingParticleOptions(0.0F, ((float) Math.PI / 2F),
                            45, 86, 236, 204, 1.0F, 35.0F, false,
                            EnumRingBehavior.GROW_THEN_SHRINK.ordinal()),
                    pEntity.getX(), pEntity.getY() + 0.1D, pEntity.getZ(),
                    0.0D, 0.0D, 0.0D);
            pEntity.level().addParticle(
                    new RingParticleOptions(0.0F, ((float) Math.PI / 2F),
                            55, 200, 245, 255, 0.6F, 45.0F, false,
                            EnumRingBehavior.GROW_THEN_SHRINK.ordinal()),
                    pEntity.getX(), pEntity.getY() + 0.1D, pEntity.getZ(),
                    0.0D, 0.0D, 0.0D);
        }
        if (pEntity.getAttackState() == Maledictus_PrimeEntity.ATTACK_CHARGE
                && pEntity.getAttackTicks() >= 20 && !pEntity.isInvisible()) {
            pPoseStack.pushPose();

            pPoseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - pEntityYaw));
            int chargeTicks = pEntity.getAttackTicks();

            float pathLength = Math.min(18.0F, (chargeTicks - 20) * 0.9F + 3.0F);
            GlacialVoxelHelper.renderVoxelIcePath(pPoseStack, pBuffer, 2.2F, pathLength, 0.85F);
            pPoseStack.popPose();
        }
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
        if (!pEntity.isInvisible() && pEntity.isAlive()) {
            renderEyeFlash(pEntity, partialTicks, pPoseStack, pBuffer, pPackedLight,
                    renderPosX, renderPosY, renderPosZ);
        }
    }

    private void renderMovementAfterimages(Maledictus_PrimeEntity entity, float entityYaw, float partialTicks,
                                           PoseStack poseStack, MultiBufferSource buffer,
                                           double renderPosX, double renderPosY, double renderPosZ) {
        int attackState = entity.getAttackState();
        boolean moving = entity.getDeltaMovement().horizontalDistanceSqr() > 0.003D
                || attackState == Maledictus_PrimeEntity.ATTACK_CHARGE
                || attackState == Maledictus_PrimeEntity.BACKSTEP
                || attackState == Maledictus_PrimeEntity.BACKSTEP_BEFORE_CHARGE
                || attackState == Maledictus_PrimeEntity.STATE_FLASH_STEP;
        if (!moving || entity.isInvisible() || entity.isDowned()) {
            shadowHistory.remove(entity);
            return;
        }

        Deque<ShadowPose> shadows = shadowHistory.computeIfAbsent(entity, ignored -> new ArrayDeque<>());
        if (entity.tickCount % 2 == 0) {
            shadows.addFirst(new ShadowPose(new Vec3(renderPosX, renderPosY, renderPosZ), entityYaw));
            while (shadows.size() > MAX_SHADOWS) {
                shadows.removeLast();
            }
        }

        int index = 0;
        for (ShadowPose shadow : shadows) {
            float alpha = (0.42F - index * 0.09F) * (entity.isEcho() ? 1.3F : 1.0F);
            if (alpha <= 0.02F) {
                index++;
                continue;
            }
            poseStack.pushPose();
            poseStack.translate(shadow.position.x - renderPosX,
                    shadow.position.y - renderPosY,
                    shadow.position.z - renderPosZ);
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - shadow.yaw));
            poseStack.scale(-0.96F, -0.96F, 0.96F);
            poseStack.translate(0.0F, -1.501F, 0.0F);
            VertexConsumer shadowConsumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(GLITCH_TEXTURE));
            float red = index % 2 == 0 ? 0.05F : 0.60F;
            float green = index % 2 == 0 ? 0.95F : 0.20F;
            this.getModel().renderToBuffer(poseStack, shadowConsumer, 15728880,
                    OverlayTexture.NO_OVERLAY, red, green, 1.0F, alpha);
            poseStack.popPose();
            index++;
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
        return new SwordSegment(basePos, tipPos, 0.0F);
    }

    private void updateSwordTrail(Maledictus_PrimeEntity entity, SwordSegment current) {
        Deque<SwordSegment> trail = swordTrails.computeIfAbsent(entity, id -> new ArrayDeque<>());
        SwordSegment last = trail.peekLast();
        float intensity = 0.0F;
        if (last != null) {
            double distanceSquared = current.tip.distanceToSqr(last.tip);

            if (distanceSquared < 0.0025D) {
                return;
            }

            double speed = Math.sqrt(distanceSquared);
            float minSpeed = 0.20F;
            float maxSpeed = 0.85F;
            intensity = Mth.clamp(((float) speed - minSpeed) / (maxSpeed - minSpeed), 0.0F, 1.0F);

            intensity *= intensity;
        }
        trail.addLast(new SwordSegment(current.base, current.tip, intensity));
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

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(TRAIL_TEXTURE));
        SwordSegment[] segments = trail.toArray(new SwordSegment[0]);

        for (int i = 1; i < segments.length; i++) {
            float currAgeRatio = (float) i / (float) (segments.length - 1);
            float prevAgeRatio = (float) (i - 1) / (float) (segments.length - 1);

            SwordSegment prev = segments[i - 1];
            SwordSegment curr = segments[i];

            float segmentIntensity = Math.max(prev.speedIntensity, curr.speedIntensity);
            if (segmentIntensity <= 0.02F) {
                continue;
            }

            Vec3 prevBlade = prev.tip.subtract(prev.base);
            Vec3 currBlade = curr.tip.subtract(curr.base);

            Vec3 swingDir = curr.tip.subtract(prev.tip);
            Vec3 bladeNormal = prevBlade.cross(swingDir);
            if (bladeNormal.lengthSqr() < 1.0E-4D) {
                bladeNormal = new Vec3(0, 1, 0);
            } else {
                bladeNormal = bladeNormal.normalize();
            }


            float trailThickness = 0.04F * segmentIntensity;
            Vec3 thicknessOffset = bladeNormal.scale(trailThickness);
            float dynamicFade = fade * segmentIntensity;

            drawVoxelBladeSegment(consumer, matrix, normal, prev, curr,
                    prevBlade.scale(0.8D * segmentIntensity), currBlade.scale(0.8D * segmentIntensity),
                    thicknessOffset, prevAgeRatio, currAgeRatio, 0.85F * dynamicFade, 0.85F, light);

            drawVoxelBladeSegment(consumer, matrix, normal, prev, curr,
                    prevBlade.scale(0.35D * segmentIntensity), currBlade.scale(0.35D * segmentIntensity),
                    thicknessOffset.scale(1.5D), prevAgeRatio, currAgeRatio, 1.0F * dynamicFade, 0.45F, light);

            drawVoxelBladeSegment(consumer, matrix, normal, prev, curr, Vec3.ZERO, Vec3.ZERO,
                    thicknessOffset.scale(2.0D), prevAgeRatio, currAgeRatio, 1.0F * dynamicFade, 0.05F, light);
        }
        poseStack.popPose();
    }

    /**
     * 刀身に完全追従し、直角・階段状の角張りを持つボクセルスラブを描画
     */
    private void drawVoxelBladeSegment(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal,
                                       SwordSegment prev, SwordSegment curr,
                                       Vec3 prevExpand, Vec3 currExpand, Vec3 thicknessOffset,
                                       float prevAgeRatio, float currAgeRatio,
                                       float alphaFade, float crossOffset, int light) {

        Vec3 pBase0 = prev.base.subtract(thicknessOffset);
        Vec3 pTip0  = prev.tip.add(prevExpand).add(thicknessOffset);
        Vec3 pBase1 = curr.base.subtract(thicknessOffset);
        Vec3 pTip1  = curr.tip.add(currExpand).add(thicknessOffset);

        float[] colInner0 = getGlacial5Color(prevAgeRatio, crossOffset, alphaFade);
        float[] colOuter0 = getGlacial5Color(prevAgeRatio, Math.min(1.0F, crossOffset + 0.45F), alphaFade * 0.75F);
        float[] colInner1 = getGlacial5Color(currAgeRatio, crossOffset, alphaFade);
        float[] colOuter1 = getGlacial5Color(currAgeRatio, Math.min(1.0F, crossOffset + 0.45F), alphaFade * 0.75F);

        addColoredVertex(consumer, matrix, normal, pBase0, colOuter0, prevAgeRatio, 0.0F, light);
        addColoredVertex(consumer, matrix, normal, pTip0,  colInner0, prevAgeRatio, 1.0F, light);
        addColoredVertex(consumer, matrix, normal, pTip1,  colInner1, currAgeRatio, 1.0F, light);
        addColoredVertex(consumer, matrix, normal, pBase1, colOuter1, currAgeRatio, 0.0F, light);

        addColoredVertex(consumer, matrix, normal, pBase1, colOuter1, currAgeRatio, 0.0F, light);
        addColoredVertex(consumer, matrix, normal, pTip1,  colInner1, currAgeRatio, 1.0F, light);
        addColoredVertex(consumer, matrix, normal, pTip0,  colInner0, prevAgeRatio, 1.0F, light);
        addColoredVertex(consumer, matrix, normal, pBase0, colOuter0, prevAgeRatio, 0.0F, light);
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

    private void addColoredVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, Vec3 pos,
                                  float[] rgba, float u, float v, int light) {
        consumer.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
                .color(rgba[0], rgba[1], rgba[2], rgba[3])
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normal, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    private static float[] getGlacial5Color(float ageRatio, float crossRatio, float alphaMultiplier) {
        float[] core = {1.00F, 1.00F, 1.00F};
        float[] cyan = {0.00F, 0.92F, 1.00F};
        float[] jade = {0.05F, 1.00F, 0.58F};
        float[] violet = {0.62F, 0.20F, 1.00F};
        float[] navy = {0.04F, 0.08F, 0.45F};
        float red;
        float green;
        float blue;
        if (crossRatio < 0.25F) {
            float f = crossRatio / 0.25F;
            red = Mth.lerp(f, core[0], cyan[0]);
            green = Mth.lerp(f, core[1], cyan[1]);
            blue = Mth.lerp(f, core[2], cyan[2]);
        } else if (crossRatio < 0.60F) {
            float f = (crossRatio - 0.25F) / 0.35F;
            red = Mth.lerp(f, cyan[0], jade[0]);
            green = Mth.lerp(f, cyan[1], jade[1]);
            blue = Mth.lerp(f, cyan[2], jade[2]);
        } else if (crossRatio < 0.85F) {
            float f = (crossRatio - 0.60F) / 0.25F;
            red = Mth.lerp(f, jade[0], violet[0]);
            green = Mth.lerp(f, jade[1], violet[1]);
            blue = Mth.lerp(f, jade[2], violet[2]);
        } else {
            float f = (crossRatio - 0.85F) / 0.15F;
            red = Mth.lerp(f, violet[0], navy[0]);
            green = Mth.lerp(f, violet[1], navy[1]);
            blue = Mth.lerp(f, violet[2], navy[2]);
        }
        if (ageRatio < 0.5F) {
            float f = (1.0F - ageRatio / 0.5F) * 0.4F;
            red = Mth.lerp(f, red, violet[0]);
            green = Mth.lerp(f, green, navy[1]);
            blue = Mth.lerp(f, blue, navy[2]);
        }
        return new float[]{red, green, blue, ageRatio * alphaMultiplier};
    }

    private void renderOverhauledEcho(Maledictus_PrimeEntity entity, float partialTicks, PoseStack poseStack,
                                      MultiBufferSource buffer, int light) {
        float time = entity.tickCount + partialTicks;
        float yaw = Mth.lerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);

        poseStack.pushPose();
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.scale(-0.96F, -0.96F, 0.96F);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        VertexConsumer armorGhost = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
        this.getModel().setupAnim(entity, 0.0F, 0.0F, time, 0.0F, 0.0F);
        this.getModel().renderToBuffer(poseStack, armorGhost, light, OverlayTexture.NO_OVERLAY,
                0.2F, 0.6F, 0.8F, 0.25F);
        poseStack.popPose();

        float jitter = 0.035F + 0.025F * (float) Math.sin(time * 1.7F);
        VertexConsumer glitch = buffer.getBuffer(RenderType.entityTranslucentEmissive(GLITCH_TEXTURE));
        float[][] passes = {
                {jitter, 0.02F, 0.95F, 0.22F, 0.92F, 0.52F},
                {-jitter * 1.4F, -0.01F, 0.25F, 0.90F, 1.0F, 0.42F},
                {0.0F, 0.0F, 0.72F, 0.98F, 0.92F, 0.26F}
        };
        for (float[] pass : passes) {
            poseStack.pushPose();
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

    private static class ShadowPose {
        final Vec3 position;
        final float yaw;

        ShadowPose(Vec3 position, float yaw) {
            this.position = position;
            this.yaw = yaw;
        }
    }

    private static class SwordSegment {
        final Vec3 base;
        final Vec3 tip;
        final float speedIntensity;

        SwordSegment(Vec3 base, Vec3 tip, float speedIntensity) {
            this.base = base;
            this.tip = tip;
            this.speedIntensity = speedIntensity;
        }
    }
}
