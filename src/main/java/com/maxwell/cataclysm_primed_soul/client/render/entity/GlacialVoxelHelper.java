package com.maxwell.cataclysm_primed_soul.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.Blocks;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public final class GlacialVoxelHelper {
    private GlacialVoxelHelper() {}

    /**
     * 【突進用：前方に突き進む段差氷板】
     * 地面に埋まらない厚みを持ち、進行方向（-Z方向）に向かって段差状に展開する
     */
    public static void renderVoxelIcePath(PoseStack poseStack, MultiBufferSource buffer,
                                          float width, float length, float alpha) {
        poseStack.pushPose();
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        TextureAtlasSprite iceSprite = Minecraft.getInstance().getBlockRenderer()
                .getBlockModelShaper().getParticleIcon(Blocks.BLUE_ICE.defaultBlockState());
        float u0 = iceSprite.getU0();
        float u1 = iceSprite.getU1();
        float v0 = iceSprite.getV0();
        float v1 = iceSprite.getV1();

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS));
        float halfWidth = width * 0.5F;

        renderTexturedBox(pose, normal, consumer,
                -halfWidth, 0.05F, -length,
                halfWidth, 0.35F, 0.0F,
                u0, u1, v0, v1,
                0.8F, 0.95F, 1.0F, alpha * 0.85F, 15728880);

        float edgeH = 0.65F;
        renderTexturedBox(pose, normal, consumer,
                -halfWidth - 0.25F, 0.05F, -length * 0.8F,
                -halfWidth,        edgeH, -length * 0.2F,
                u0, u1, v0, v1,
                0.7F, 0.9F, 1.0F, alpha * 0.7F, 15728880);

        renderTexturedBox(pose, normal, consumer,
                halfWidth,        0.05F, -length * 0.8F,
                halfWidth + 0.25F, edgeH, -length * 0.2F,
                u0, u1, v0, v1,
                0.7F, 0.9F, 1.0F, alpha * 0.7F, 15728880);

        poseStack.popPose();
    }

    /**
     * 【段差型ボクセル氷柱】
     */
    public static void renderSteppedGlacialPillar(PoseStack poseStack, MultiBufferSource buffer,
                                                  float baseSize, float totalHeight, int steps, float alpha) {
        int safeSteps = Math.max(1, steps);
        poseStack.pushPose();
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        TextureAtlasSprite iceSprite = Minecraft.getInstance().getBlockRenderer()
                .getBlockModelShaper().getParticleIcon(Blocks.BLUE_ICE.defaultBlockState());
        float u0 = iceSprite.getU0();
        float u1 = iceSprite.getU1();
        float v0 = iceSprite.getV0();
        float v1 = iceSprite.getV1();

        VertexConsumer coreConsumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(TextureAtlas.LOCATION_BLOCKS));
        VertexConsumer shellConsumer = buffer.getBuffer(RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS));

        float stepHeight = totalHeight / safeSteps;

        for (int i = 0; i < safeSteps; i++) {
            float progress = (float) i / safeSteps;
            float width = baseSize * (1.0F - progress * 0.72F);
            float yBottom = i * stepHeight;
            float yTop = (i + 1) * stepHeight;

            renderTexturedBox(pose, normal, coreConsumer,
                    -width * 0.35F, yBottom, -width * 0.35F,
                    width * 0.35F, yTop,    width * 0.35F,
                    u0, u1, v0, v1,
                    0.8F, 1.0F, 1.0F, Math.min(1.0F, alpha * 1.5F), 15728880);

            renderTexturedBox(pose, normal, shellConsumer,
                    -width * 0.5F, yBottom, -width * 0.5F,
                    width * 0.5F, yTop,    width * 0.5F,
                    u0, u1, v0, v1,
                    0.9F, 0.95F, 1.0F, alpha * 0.85F, 15728880);
        }
        poseStack.popPose();
    }

    public static void renderTexturedBox(Matrix4f pose, Matrix3f normal, VertexConsumer consumer,
                                         float minX, float minY, float minZ,
                                         float maxX, float maxY, float maxZ,
                                         float u0, float u1, float v0, float v1,
                                         float r, float g, float b, float a, int light) {

        addQuad(consumer, pose, normal,
                minX, maxY, maxZ, u0, v1, maxX, maxY, maxZ, u1, v1,
                maxX, maxY, minZ, u1, v0, minX, maxY, minZ, u0, v0,
                r, g, b, a, light, 0, 1, 0);

        addQuad(consumer, pose, normal,
                minX, minY, minZ, u0, v0, maxX, minY, minZ, u1, v0,
                maxX, minY, maxZ, u1, v1, minX, minY, maxZ, u0, v1,
                r * 0.5F, g * 0.5F, b * 0.5F, a, light, 0, -1, 0);

        addQuad(consumer, pose, normal,
                minX, maxY, minZ, u1, v0, maxX, maxY, minZ, u0, v0,
                maxX, minY, minZ, u0, v1, minX, minY, minZ, u1, v1,
                r * 0.8F, g * 0.8F, b * 0.8F, a, light, 0, 0, -1);

        addQuad(consumer, pose, normal,
                maxX, maxY, maxZ, u1, v0, minX, maxY, maxZ, u0, v0,
                minX, minY, maxZ, u0, v1, maxX, minY, maxZ, u1, v1,
                r * 0.8F, g * 0.8F, b * 0.8F, a, light, 0, 0, 1);

        addQuad(consumer, pose, normal,
                minX, maxY, maxZ, u1, v0, minX, maxY, minZ, u0, v0,
                minX, minY, minZ, u0, v1, minX, minY, maxZ, u1, v1,
                r * 0.6F, g * 0.6F, b * 0.6F, a, light, -1, 0, 0);

        addQuad(consumer, pose, normal,
                maxX, maxY, minZ, u1, v0, maxX, maxY, maxZ, u0, v0,
                maxX, minY, maxZ, u0, v1, maxX, minY, minZ, u1, v1,
                r * 0.6F, g * 0.6F, b * 0.6F, a, light, 1, 0, 0);
    }

    private static void addQuad(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                float x0, float y0, float z0, float uA, float vA,
                                float x1, float y1, float z1, float uB, float vB,
                                float x2, float y2, float z2, float uC, float vC,
                                float x3, float y3, float z3, float uD, float vD,
                                float red, float green, float blue, float alpha, int light,
                                float nx, float ny, float nz) {
        vertex(consumer, pose, normal, x0, y0, z0, red, green, blue, alpha, uA, vA, light, nx, ny, nz);
        vertex(consumer, pose, normal, x1, y1, z1, red, green, blue, alpha, uB, vB, light, nx, ny, nz);
        vertex(consumer, pose, normal, x2, y2, z2, red, green, blue, alpha, uC, vC, light, nx, ny, nz);
        vertex(consumer, pose, normal, x3, y3, z3, red, green, blue, alpha, uD, vD, light, nx, ny, nz);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                               float x, float y, float z, float red, float green, float blue, float alpha,
                               float u, float v, int light, float nx, float ny, float nz) {
        consumer.vertex(pose, x, y, z)
                .color(red, green, blue, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normal, nx, ny, nz)
                .endVertex();
    }
}