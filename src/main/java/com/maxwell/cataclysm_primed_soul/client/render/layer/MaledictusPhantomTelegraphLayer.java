package com.maxwell.cataclysm_primed_soul.client.render.layer;

import com.maxwell.cataclysm_primed_soul.client.model.entity.MaledictusPhantomModel;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.MaledictusPhantomEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class MaledictusPhantomTelegraphLayer extends RenderLayer<MaledictusPhantomEntity, MaledictusPhantomModel> {
    public MaledictusPhantomTelegraphLayer(RenderLayerParent<MaledictusPhantomEntity, MaledictusPhantomModel> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       MaledictusPhantomEntity entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible() || entity.getPhantomType() != MaledictusPhantomEntity.TYPE_SPEAR
                || entity.tickCount >= entity.getSpearChargeStartTicks()) {
            return;
        }
        float progress = Math.min(1.0F, (entity.tickCount + partialTicks) / entity.getSpearChargeStartTicks());
        float alpha = 0.18F + progress * 0.42F;
        float halfWidth = entity.getSpearTelegraphHalfWidth();
        float length = entity.getSpearTelegraphLength();
        poseStack.pushPose();
        float entityYaw = Mth.lerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float displayYaw = calculateDisplayYaw(entity, partialTicks, entityYaw);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(displayYaw - entityYaw));
        poseStack.translate(0.0D, 0.035D, 0.0D);
        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer vertices = buffer.getBuffer(RenderType.lightning());
        drawLane(vertices, matrix, -halfWidth, halfWidth, -length, -0.15F, 90, 220, 255, alpha);
        float coreHalfWidth = Math.min(0.35F, halfWidth * 0.12F);
        drawLane(vertices, matrix, -coreHalfWidth, coreHalfWidth, -length + 0.2F, -0.2F,
                220, 255, 255, Math.min(0.9F, alpha + 0.25F));
        for (int i = 1; i <= 3; i++) {
            float z = -length * i / 4.0F;
            drawChevron(vertices, matrix, z, Math.min(halfWidth * 0.45F, 1.4F),
                    110, 235, 255, Math.min(0.95F, alpha + 0.2F));
        }
        poseStack.popPose();
    }

    /**
     * The telegraph is intentionally calculated from the target/motion on the client,
     * instead of trusting the phantom's body rotation used by server-side physics.
     */
    private float calculateDisplayYaw(MaledictusPhantomEntity entity, float partialTicks, float fallbackYaw) {
        LivingEntity target = entity.getPhantomTarget();
        if (target == null && entity.level().isClientSide()) {
            double nearestDistance = Double.MAX_VALUE;
            for (Player candidate : entity.level().getEntitiesOfClass(Player.class,
                    entity.getBoundingBox().inflate(32.0D))) {
                if (!candidate.isAlive() || candidate.isCreative() || candidate.isSpectator()) {
                    continue;
                }
                double distance = entity.distanceToSqr(candidate);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    target = candidate;
                }
            }
        }
        if (target != null && target.isAlive()) {
            double entityX = Mth.lerp(partialTicks, entity.xo, entity.getX());
            double entityZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());
            double targetX = Mth.lerp(partialTicks, target.xo, target.getX());
            double targetZ = Mth.lerp(partialTicks, target.zo, target.getZ());
            double dx = targetX - entityX;
            double dz = targetZ - entityZ;
            if (dx * dx + dz * dz > 1.0E-4D) {
                return (float) (Mth.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
            }
        }

        Vec3 movement = entity.getDeltaMovement();
        if (movement.horizontalDistanceSqr() > 1.0E-4D) {
            return (float) (Mth.atan2(movement.z, movement.x) * (180.0D / Math.PI)) - 90.0F;
        }
        double dx = entity.getX() - entity.xo;
        double dz = entity.getZ() - entity.zo;
        if (dx * dx + dz * dz > 1.0E-4D) {
            return (float) (Mth.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
        }
        return fallbackYaw;
    }

    private void drawLane(VertexConsumer vertices, Matrix4f matrix, float left, float right,
                          float front, float back, int red, int green, int blue, float alpha) {
        vertex(vertices, matrix, left, 0.0F, front, red, green, blue, alpha);
        vertex(vertices, matrix, right, 0.0F, front, red, green, blue, alpha);
        vertex(vertices, matrix, right, 0.0F, back, red, green, blue, alpha);
        vertex(vertices, matrix, left, 0.0F, back, red, green, blue, alpha);
    }

    private void drawChevron(VertexConsumer vertices, Matrix4f matrix, float z, float halfWidth,
                             int red, int green, int blue, float alpha) {
        drawLane(vertices, matrix, -halfWidth, -halfWidth + 0.07F, z + 0.22F, z - 0.22F,
                red, green, blue, alpha);
        drawLane(vertices, matrix, halfWidth - 0.07F, halfWidth, z - 0.22F, z + 0.22F,
                red, green, blue, alpha);
    }

    private void vertex(VertexConsumer vertices, Matrix4f matrix, float x, float y, float z,
                        int red, int green, int blue, float alpha) {
        vertices.vertex(matrix, x, y, z)
                .color(red, green, blue, (int) (alpha * 255.0F))
                .endVertex();
    }
}
