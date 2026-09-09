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
    private static final int MAX_SHADOWS = 5;
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
        if (pEntity.isAlive() && !pEntity.isInvisible() && pEntity.isPhase2()) {
            pPoseStack.pushPose();
            GlacialRenderHelper.renderGlacialBitOrbit(pPoseStack, pBuffer, 3.6F,
                    pEntity.tickCount + partialTicks, 0.9F);
            pPoseStack.popPose();
        }
        if (pEntity.getAttackState() == Maledictus_PrimeEntity.ATTACK_CHARGE
                && pEntity.getAttackTicks() > 20 && !pEntity.isInvisible()) {
            pPoseStack.pushPose();
            pPoseStack.translate(0.0D, 1.5D, 0.0D);
            pPoseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - pEntityYaw));
            pPoseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0F));
            float vortexRotation = (pEntity.tickCount + partialTicks) * 0.4F;
            GlacialRenderHelper.renderBlizzardVortex(pPoseStack, pBuffer, 2.8F, 16.0F, 8,
                    vortexRotation, 0.75F);
            pPoseStack.popPose();
        }
        if (pEntity.getAttackState() == Maledictus_PrimeEntity.ATTACK_DEAD) {
            renderDeathLight(pEntity, partialTicks, pPoseStack, pBuffer, renderPosX, renderPosY, renderPosZ);
        }
        int attackState = pEntity.getAttackState();
        if ((attackState == Maledictus_PrimeEntity.ATTACK_JAB_1
                || attackState == Maledictus_PrimeEntity.ATTACK_JAB_2
                || attackState == Maledictus_PrimeEntity.ATTACK_JAB_3
                || attackState == Maledictus_PrimeEntity.ATTACK_EX_JAB_1
                || attackState == Maledictus_PrimeEntity.ATTACK_EX_JAB_3)
                && pEntity.getAttackTicks() >= 5 && pEntity.getAttackTicks() <= 18) {
            pPoseStack.pushPose();
            pPoseStack.translate(0.0D, 1.2D, 0.0D);
            pPoseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - pEntityYaw));
            if (attackState == Maledictus_PrimeEntity.ATTACK_JAB_2) {
                pPoseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(25.0F));
            } else if (attackState == Maledictus_PrimeEntity.ATTACK_EX_JAB_3) {
                pPoseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-35.0F));
            }
            float slashProgress = (pEntity.getAttackTicks() - 5.0F) / 13.0F;
            GlacialRenderHelper.renderGlacialSlashArc(pPoseStack, pBuffer, 2.0F, 7.5F,
                    140.0F, slashProgress, 1.0F - slashProgress);
            pPoseStack.popPose();
        }
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
        Entity grabbedEntity = pEntity.getControllingPassenger();
        if (grabbedEntity != null && pEntity.getAttackState() == Maledictus_PrimeEntity.ATTACK_GRAB_SUCCESS) {
            Vec3 handPos = getRightHandPosition(pEntity, partialTicks, renderPosX, renderPosY, renderPosZ);
            grabbedEntity.setPos(handPos.x, handPos.y, handPos.z);
            grabbedEntity.setOldPosAndRot();
            pPoseStack.pushPose();
            pPoseStack.translate(handPos.x - renderPosX, handPos.y - renderPosY - 0.5D, handPos.z - renderPosZ);
            GlacialRenderHelper.renderCryoCasket(pPoseStack, pBuffer, 1.2F, 2.2F, 0.85F);
            pPoseStack.popPose();
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
            float alpha = (0.45F - index * 0.08F) * (entity.isEcho() ? 1.4F : 1.0F);
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
            float red = index % 2 == 0 ? 0.1F : 0.6F;
            float green = index % 2 == 0 ? 0.9F : 0.2F;
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
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(TRAIL_TEXTURE));
        SwordSegment[] segments = trail.toArray(new SwordSegment[0]);
        Vec3 cameraPos = net.minecraft.client.Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition();
        for (int i = 1; i < segments.length; i++) {
            float ratio = (float) i / (float) (segments.length - 1);
            float prevRatio = (float) (i - 1) / (float) (segments.length - 1);
            SwordSegment prev = segments[i - 1];
            SwordSegment curr = segments[i];
            draw5ColorRibbonLayer(consumer, matrix, normal, prev, curr, prevRatio, ratio,
                    0.95F * fade, 1.60D, 0.85F, cameraPos, light);
            draw5ColorRibbonLayer(consumer, matrix, normal, prev, curr, prevRatio, ratio,
                    1.00F * fade, 0.90D, 0.45F, cameraPos, light);
            draw5ColorRibbonLayer(consumer, matrix, normal, prev, curr, prevRatio, ratio,
                    1.00F * fade, 0.35D, 0.05F, cameraPos, light);
        }
        poseStack.popPose();
    }

    private void draw5ColorRibbonLayer(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal,
                                       SwordSegment prev, SwordSegment curr,
                                       float prevAgeRatio, float currAgeRatio,
                                       float alphaFade, double width, float crossOffset,
                                       Vec3 cameraPos, int light) {
        double prevTipFactor = Math.pow(prevAgeRatio, 1.2D);
        double currTipFactor = Math.pow(currAgeRatio, 1.2D);
        Vec3 prevTip = prev.base.add(prev.tip.subtract(prev.base).scale(prevTipFactor));
        Vec3 currTip = curr.base.add(curr.tip.subtract(curr.base).scale(currTipFactor));
        Vec3 prevWidth = getTrailWidth(prev.base, prevTip, cameraPos, width);
        Vec3 currWidth = getTrailWidth(curr.base, currTip, cameraPos, width);

        float[] outer0 = getGlacial5Color(prevAgeRatio, Math.min(1.0F, crossOffset + 0.45F), alphaFade * 0.75F);
        float[] inner0 = getGlacial5Color(prevAgeRatio, crossOffset, alphaFade);
        float[] inner1 = getGlacial5Color(currAgeRatio, crossOffset, alphaFade);
        float[] outer1 = getGlacial5Color(currAgeRatio, Math.min(1.0F, crossOffset + 0.45F), alphaFade * 0.75F);
        addColoredVertex(consumer, matrix, normal, prev.base.subtract(prevWidth), outer0, prevAgeRatio, 0.0F, light);
        addColoredVertex(consumer, matrix, normal, prevTip.add(prevWidth), inner0, prevAgeRatio, 1.0F, light);
        addColoredVertex(consumer, matrix, normal, currTip.add(currWidth), inner1, currAgeRatio, 1.0F, light);
        addColoredVertex(consumer, matrix, normal, curr.base.subtract(currWidth), outer1, currAgeRatio, 0.0F, light);
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

        SwordSegment(Vec3 base, Vec3 tip) {
            this.base = base;
            this.tip = tip;
        }
    }
}
