package com.maxwell.cataclysm_primed_soul.client.render.entity;

import com.maxwell.cataclysm_primed_soul.entity.cutscene.PureWhiteEnergySphereEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@SuppressWarnings("removal")
public class PureWhiteEnergySphereRenderer extends EntityRenderer<PureWhiteEnergySphereEntity> {
    private static final ResourceLocation WHITE = new ResourceLocation("minecraft", "textures/particle/glow.png");

    public PureWhiteEnergySphereRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    private static void quad(VertexConsumer c, Matrix4f m, Matrix3f n, float size, float brightness, float alpha) {
        vertex(c, m, n, -size, -size, 0, 0, 1, brightness, alpha);
        vertex(c, m, n, -size, size, 0, 0, 0, brightness, alpha);
        vertex(c, m, n, size, size, 0, 1, 0, brightness, alpha);
        vertex(c, m, n, size, -size, 0, 1, 1, brightness, alpha);
    }

    private static void vertex(VertexConsumer c, Matrix4f m, Matrix3f n, float x, float y, float z,
                               float u, float v, float brightness, float alpha) {
        c.vertex(m, x, y, z).color(brightness, brightness, brightness, alpha).uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(n, 0, 0, 1).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(PureWhiteEnergySphereEntity entity) {
        return WHITE;
    }

    @Override
    public void render(PureWhiteEnergySphereEntity entity, float yaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        float age = entity.tickCount + partialTick;
        float radius = 1.35F + 0.35F * (float) Math.sin(age * 0.16F);
        float alpha = 0.88F;
        if (entity.isDetonating(partialTick)) {
            float blast = entity.detonationProgress(partialTick);
            radius = 1.5F + blast * 8.0F;
            alpha = 1.0F - blast;
        }
        poseStack.pushPose();
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().camera.rotation());
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(WHITE));
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        quad(consumer, matrix, normal, radius, 1.0F, alpha);
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(45.0F));
        quad(consumer, poseStack.last().pose(), poseStack.last().normal(), radius * 0.72F, 1.0F, alpha * 0.8F);
        poseStack.popPose();
    }
}
