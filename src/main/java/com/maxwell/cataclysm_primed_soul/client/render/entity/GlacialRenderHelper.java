package com.maxwell.cataclysm_primed_soul.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/** Procedural, texture-free glacial geometry used by Maledictus Prime effects. */
public final class GlacialRenderHelper {
    private GlacialRenderHelper() {
    }

    public static void renderGlacialSpike(PoseStack poseStack, MultiBufferSource buffer,
                                          float radius, float height, int segments, float alpha) {
        int safeSegments = Math.max(3, segments);
        float safeAlpha = Mth.clamp(alpha, 0.0F, 1.0F);

        poseStack.pushPose();
        VertexConsumer core = buffer.getBuffer(RenderType.lightning());
        drawPyramid(poseStack, core, radius * 0.45F, height * 0.95F, safeSegments,
                0.85F, 1.0F, 1.0F, Mth.clamp(safeAlpha * 1.5F, 0.0F, 1.0F), 15728880);

        VertexConsumer shell = buffer.getBuffer(RenderType.entityTranslucentCull(TextureAtlas.LOCATION_BLOCKS));
        drawPyramid(poseStack, shell, radius, height, safeSegments,
                0.35F, 0.82F, 0.95F, safeAlpha * 0.72F, 15728880);
        poseStack.popPose();
    }

    public static void renderBlizzardVortex(PoseStack poseStack, MultiBufferSource buffer,
                                            float radius, float length, int segments,
                                            float rotationAngle, float alpha) {
        int safeSegments = Math.max(3, segments);
        poseStack.pushPose();
        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        float angleStep = (float) (Math.PI * 2.0D / safeSegments);

        for (int i = 0; i < safeSegments; i++) {
            float a0 = i * angleStep + rotationAngle;
            float a1 = (i + 1) * angleStep + rotationAngle;
            float x0 = Mth.cos(a0) * radius;
            float z0 = Mth.sin(a0) * radius;
            float x1 = Mth.cos(a1) * radius;
            float z1 = Mth.sin(a1) * radius;
            addQuad(consumer, pose, normal,
                    x0, 0.0F, z0,
                    x1, 0.0F, z1,
                    x1 * 1.3F, length, z1 * 1.3F,
                    x0 * 1.3F, length, z0 * 1.3F,
                    0.45F, 0.9F, 1.0F, alpha * 0.4F, 15728880);
        }
        poseStack.popPose();
    }

    public static void renderCryoCasket(PoseStack poseStack, MultiBufferSource buffer,
                                        float width, float height, float alpha) {
        poseStack.pushPose();
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucentCull(TextureAtlas.LOCATION_BLOCKS));
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        float halfWidth = width * 0.5F;

        float[][] sides = {
                {-halfWidth, 0.0F, -halfWidth, halfWidth, 0.0F, -halfWidth, halfWidth, height, -halfWidth, -halfWidth, height, -halfWidth},
                {halfWidth, 0.0F, -halfWidth, halfWidth, 0.0F, halfWidth, halfWidth, height, halfWidth, halfWidth, height, -halfWidth},
                {halfWidth, 0.0F, halfWidth, -halfWidth, 0.0F, halfWidth, -halfWidth, height, halfWidth, halfWidth, height, halfWidth},
                {-halfWidth, 0.0F, halfWidth, -halfWidth, 0.0F, -halfWidth, -halfWidth, height, -halfWidth, -halfWidth, height, halfWidth}
        };

        for (float[] side : sides) {
            addQuad(consumer, pose, normal,
                    side[0], side[1], side[2], side[3], side[4], side[5],
                    side[6], side[7], side[8], side[9], side[10], side[11],
                    0.5F, 0.88F, 1.0F, alpha * 0.65F, 15728880);
        }
        poseStack.popPose();
    }

    /** Large hexagonal glacier pillar used for the refreshed sword-spike impact. */
    public static void renderMassiveGlacialPillar(PoseStack poseStack, MultiBufferSource buffer,
                                                  float radius, float height, float alpha) {
        poseStack.pushPose();
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        VertexConsumer core = buffer.getBuffer(RenderType.lightning());
        drawCylinder(pose, normal, core, radius * 0.35F, height * 0.95F, 6,
                0.9F, 1.0F, 1.0F, alpha);
        VertexConsumer shell = buffer.getBuffer(RenderType.endGateway());
        drawCylinder(pose, normal, shell, radius, height, 6,
                1.0F, 1.0F, 1.0F, alpha * 0.7F);
        drawCylinderWireframe(pose, normal, core, radius * 1.02F, height, 6,
                0.3F, 0.9F, 1.0F, alpha);
        poseStack.popPose();
    }

    /** Large frozen-reality crescent left behind by JAB and EX_JAB swings. */
    public static void renderGlacialSlashArc(PoseStack poseStack, MultiBufferSource buffer,
                                             float innerRadius, float outerRadius, float sweepAngleDeg,
                                             float progress, float alpha) {
        poseStack.pushPose();
        float expansion = Mth.lerp(Mth.clamp(progress, 0.0F, 1.0F), 0.35F, 1.0F);
        poseStack.scale(expansion, expansion, expansion);
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        VertexConsumer core = buffer.getBuffer(RenderType.lightning());
        VertexConsumer voidConsumer = buffer.getBuffer(RenderType.endGateway());
        int segments = 16;
        float startAngle = (float) Math.toRadians(-sweepAngleDeg / 2.0F);
        float step = (float) Math.toRadians(sweepAngleDeg / segments);

        for (int i = 0; i < segments; i++) {
            float a0 = startAngle + i * step;
            float a1 = startAngle + (i + 1) * step;
            float x0In = Mth.cos(a0) * innerRadius;
            float z0In = Mth.sin(a0) * innerRadius;
            float x1In = Mth.cos(a1) * innerRadius;
            float z1In = Mth.sin(a1) * innerRadius;
            float x0Out = Mth.cos(a0) * outerRadius;
            float z0Out = Mth.sin(a0) * outerRadius;
            float x1Out = Mth.cos(a1) * outerRadius;
            float z1Out = Mth.sin(a1) * outerRadius;

            addQuad(voidConsumer, pose, normal,
                    x0In, 0.0F, z0In, x1In, 0.0F, z1In,
                    x1Out, 0.0F, z1Out, x0Out, 0.0F, z0Out,
                    1.0F, 1.0F, 1.0F, alpha, 15728880);
            addQuad(core, pose, normal,
                    x0Out * 0.98F, 0.0F, z0Out * 0.98F,
                    x1Out * 0.98F, 0.0F, z1Out * 0.98F,
                    x1Out, 0.0F, z1Out, x0Out, 0.0F, z0Out,
                    0.4F, 0.95F, 1.0F, alpha * 1.5F, 15728880);
        }
        poseStack.popPose();
    }

    /** Six orbiting frost bits displayed after Maledictus Prime's armor breaks. */
    public static void renderGlacialBitOrbit(PoseStack poseStack, MultiBufferSource buffer,
                                             float orbitRadius, float time, float alpha) {
        for (int i = 0; i < 6; i++) {
            float angle = time * 0.035F + i * ((float) Math.PI * 2.0F / 6.0F);
            float x = Mth.cos(angle) * orbitRadius;
            float z = Mth.sin(angle) * orbitRadius;
            float y = 1.8F + Mth.sin(time * 0.05F + i) * 0.55F;
            poseStack.pushPose();
            poseStack.translate(x, y, z);
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees((float) Math.toDegrees(-angle)));
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(25.0F + Mth.sin(time * 0.08F + i) * 12.0F));
            renderGlacialSpike(poseStack, buffer, 0.32F, 2.2F, 5, alpha);
            poseStack.popPose();
        }
    }

    private static void drawCylinder(Matrix4f pose, Matrix3f normal, VertexConsumer consumer,
                                     float radius, float height, int segments,
                                     float red, float green, float blue, float alpha) {
        float angleStep = (float) (Math.PI * 2.0D / segments);
        for (int i = 0; i < segments; i++) {
            float a0 = i * angleStep;
            float a1 = (i + 1) * angleStep;
            float x0 = Mth.cos(a0) * radius;
            float z0 = Mth.sin(a0) * radius;
            float x1 = Mth.cos(a1) * radius;
            float z1 = Mth.sin(a1) * radius;
            addQuad(consumer, pose, normal,
                    x0, 0.0F, z0, x1, 0.0F, z1,
                    x1 * 0.7F, height, z1 * 0.7F,
                    x0 * 0.7F, height, z0 * 0.7F,
                    red, green, blue, alpha, 15728880);
        }
    }

    private static void drawCylinderWireframe(Matrix4f pose, Matrix3f normal, VertexConsumer consumer,
                                              float radius, float height, int segments,
                                              float red, float green, float blue, float alpha) {
        float angleStep = (float) (Math.PI * 2.0D / segments);
        for (int i = 0; i < segments; i++) {
            float angle = i * angleStep;
            float x = Mth.cos(angle) * radius;
            float z = Mth.sin(angle) * radius;
            addQuad(consumer, pose, normal,
                    x - 0.04F, 0.0F, z, x + 0.04F, 0.0F, z,
                    x * 0.7F + 0.04F, height, z * 0.7F,
                    x * 0.7F - 0.04F, height, z * 0.7F,
                    red, green, blue, alpha, 15728880);
        }
    }

    private static void drawPyramid(PoseStack poseStack, VertexConsumer consumer,
                                    float radius, float height, int segments,
                                    float red, float green, float blue, float alpha, int light) {
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        float angleStep = (float) (Math.PI * 2.0D / segments);

        for (int i = 0; i < segments; i++) {
            float a0 = i * angleStep;
            float a1 = (i + 1) * angleStep;
            float x0 = Mth.cos(a0) * radius;
            float z0 = Mth.sin(a0) * radius;
            float x1 = Mth.cos(a1) * radius;
            float z1 = Mth.sin(a1) * radius;
            Vector3f edge1 = new Vector3f(x1 - x0, 0.0F, z1 - z0);
            Vector3f edge2 = new Vector3f(-x0, height, -z0);
            Vector3f faceNormal = new Vector3f();
            edge1.cross(edge2, faceNormal).normalize();

            vertex(consumer, pose, normal, 0.0F, height, 0.0F,
                    red, green, blue, alpha, light, faceNormal);
            vertex(consumer, pose, normal, x0, 0.0F, z0,
                    red * 0.8F, green * 0.8F, blue * 0.8F, alpha, light, faceNormal);
            vertex(consumer, pose, normal, x1, 0.0F, z1,
                    red * 0.9F, green * 0.9F, blue * 0.9F, alpha, light, faceNormal);
        }
    }

    private static void addQuad(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                float x0, float y0, float z0, float x1, float y1, float z1,
                                float x2, float y2, float z2, float x3, float y3, float z3,
                                float red, float green, float blue, float alpha, int light) {
        vertex(consumer, pose, normal, x0, y0, z0, red, green, blue, alpha, light, 0.0F, 1.0F, 0.0F);
        vertex(consumer, pose, normal, x1, y1, z1, red, green, blue, alpha, light, 0.0F, 1.0F, 0.0F);
        vertex(consumer, pose, normal, x2, y2, z2, red, green, blue, alpha, light, 0.0F, 1.0F, 0.0F);
        vertex(consumer, pose, normal, x3, y3, z3, red, green, blue, alpha, light, 0.0F, 1.0F, 0.0F);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                               float x, float y, float z, float red, float green, float blue, float alpha,
                               int light, Vector3f faceNormal) {
        vertex(consumer, pose, normal, x, y, z, red, green, blue, alpha, light,
                faceNormal.x(), faceNormal.y(), faceNormal.z());
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                               float x, float y, float z, float red, float green, float blue, float alpha,
                               int light, float nx, float ny, float nz) {
        consumer.vertex(pose, x, y, z)
                .color(red, green, blue, alpha)
                .uv(0.0F, 0.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normal, nx, ny, nz)
                .endVertex();
    }
}
