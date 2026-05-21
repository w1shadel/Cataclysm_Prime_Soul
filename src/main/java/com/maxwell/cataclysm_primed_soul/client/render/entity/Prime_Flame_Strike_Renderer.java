package com.maxwell.cataclysm_primed_soul.client.render.entity;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.sub.Prime_Flame_Strike_Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class Prime_Flame_Strike_Renderer extends EntityRenderer<Prime_Flame_Strike_Entity> {
    public static final ResourceLocation SOUL_FLAME_STRIKE = new ResourceLocation("cataclysm", "textures/entity/soul_flame_strike_sigil.png");
    public static final ResourceLocation PRIME_SIGIL_TEXTURE = new ResourceLocation(Primed_Soul.MODID, "textures/entity/prime_sigil.png");

    private static ResourceLocation WHITE_FLAME_TEXTURE = null;
    private static ResourceLocation WHITE_RING_TEXTURE = null;

    public Prime_Flame_Strike_Renderer(EntityRendererProvider.Context mgr) {
        super(mgr);
    }

    @Override
    public ResourceLocation getTextureLocation(Prime_Flame_Strike_Entity entity) {
        return SOUL_FLAME_STRIKE;
    }

    private static void registerWhiteFlameTexture(TextureManager textureManager) {
        if (WHITE_FLAME_TEXTURE == null) {
            WHITE_FLAME_TEXTURE = new ResourceLocation(Primed_Soul.MODID, "textures/dynamic/white_flame.png");
            net.minecraft.client.renderer.texture.DynamicTexture dynamicTexture = new net.minecraft.client.renderer.texture.DynamicTexture(32, 32, true);
            com.mojang.blaze3d.platform.NativeImage nativeImage = dynamicTexture.getPixels();
            if (nativeImage != null) {
                for (int y = 0; y < 32; y++) {
                    float factor = 1.0F - ((float) y / 31.0F);
                    factor = (float) Math.sqrt(factor);
                    int alpha = (int) (factor * 255.0F);
                    alpha = Math.max(0, Math.min(255, alpha));
                    for (int x = 0; x < 32; x++) {
                        float dx = Math.abs(x - 15.5F) / 15.5F;
                        float edgeFactor = 1.0F - dx * dx;
                        edgeFactor = edgeFactor * edgeFactor;
                        int finalAlpha = (int) (alpha * edgeFactor);
                        finalAlpha = Math.max(0, Math.min(255, finalAlpha));
                        int color = (finalAlpha << 24) | (255 << 16) | (255 << 8) | 255;
                        nativeImage.setPixelRGBA(x, y, color);
                    }
                }
                dynamicTexture.upload();
                textureManager.register(WHITE_FLAME_TEXTURE, dynamicTexture);
            }
        }
    }

    private static void registerWhiteRingTexture(TextureManager textureManager) {
        if (WHITE_RING_TEXTURE == null) {
            WHITE_RING_TEXTURE = new ResourceLocation(Primed_Soul.MODID, "textures/dynamic/white_ring.png");
            net.minecraft.client.renderer.texture.DynamicTexture dynamicTexture = new net.minecraft.client.renderer.texture.DynamicTexture(32, 32, true);
            com.mojang.blaze3d.platform.NativeImage nativeImage = dynamicTexture.getPixels();
            if (nativeImage != null) {
                for (int y = 0; y < 32; y++) {
                    float dy = (y - 15.5F) / 15.5F;
                    for (int x = 0; x < 32; x++) {
                        float dx = (x - 15.5F) / 15.5F;
                        float dist = (float) Math.sqrt(dx * dx + dy * dy);
                        float ringFactor = 1.0F - Math.abs(dist - 0.8F) / 0.15F;
                        ringFactor = Math.max(0.0F, Math.min(1.0F, ringFactor));
                        ringFactor = ringFactor * ringFactor;
                        int alpha = (int) (ringFactor * 255.0F);
                        int color = (alpha << 24) | (255 << 16) | (255 << 8) | 255;
                        nativeImage.setPixelRGBA(x, y, color);
                    }
                }
                dynamicTexture.upload();
                textureManager.register(WHITE_RING_TEXTURE, dynamicTexture);
            }
        }
    }

    @Override
    public void render(Prime_Flame_Strike_Entity flameStrike, float entityYaw, float delta, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        registerWhiteFlameTexture(textureManager);
        registerWhiteRingTexture(textureManager);

        matrixStackIn.pushPose();
        float age = (float) flameStrike.tickCount + delta;
        float radius = flameStrike.getRadius();

        boolean isWhite = flameStrike.isWhite();
        boolean isSoul = flameStrike.isSoul();

        float r = 1.0F, g = 1.0F, b = 1.0F;
        if (isSoul) {
            r = 0.3F; g = 0.6F; b = 1.0F;
        }

        if (flameStrike.isSee()) {
            renderSigil(matrixStackIn, bufferIn, radius, age * 2.0F, r, g, b, 0.8F);
            renderSigil(matrixStackIn, bufferIn, radius * 0.7F, age * -4.0F, r * 0.9F, g * 0.95F, b, 1.0F);
        }

        int warmup = flameStrike.getWarmupDelay();
        int wait = flameStrike.getWaitTime();
        int dur = flameStrike.getDuration();
        int expDelay = flameStrike.getExplosionDelay();
        float currentAge = (float) flameStrike.tickCount + delta;

        if (!flameStrike.isWaiting() && currentAge >= (float)(warmup + wait) && currentAge < (float)(warmup + wait + dur)) {
            float flameAge = currentAge - (float)(warmup + wait);
            renderFlameColumn(matrixStackIn, bufferIn, radius, age, (float)dur, flameAge, r, g, b);
        }

        float endFlameTime = (float)(warmup + wait + dur);
        if (currentAge >= endFlameTime) {
            float progress;
            if (expDelay <= 0) {
                progress = 1.0F - (radius / Math.max(0.01F, flameStrike.getRadius()));
            } else {
                progress = (currentAge - endFlameTime) / (float)expDelay;
            }
            progress = Math.max(0.0F, Math.min(1.0F, progress));
            renderExplosionRing(matrixStackIn, bufferIn, radius, progress, r, g, b);
        }

        matrixStackIn.popPose();
        super.render(flameStrike, entityYaw, delta, matrixStackIn, bufferIn, packedLightIn);
    }

    public void drawVertex(Matrix4f matrix, Matrix3f normal, VertexConsumer builder, float x, float y, float z, float u, float v, int nx, int ny, int nz, int light, float r, float g, float b, float a) {
        builder.vertex(matrix, x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normal, (float) nx, (float) nz, (float) ny)
                .endVertex();
    }

    private void renderSigil(PoseStack ms, MultiBufferSource buffer, float rad, float rotation, float r, float g, float b, float alpha) {
        ms.pushPose();
        ms.scale(rad, rad, rad);
        ms.mulPose(Axis.YP.rotationDegrees(rotation));
        ms.translate(0.0D, 0.001D, 0.0D);
        Matrix4f mat = ms.last().pose();
        Matrix3f norm = ms.last().normal();
        VertexConsumer builder = buffer.getBuffer(RenderType.entityTranslucentEmissive(PRIME_SIGIL_TEXTURE));
        this.drawVertex(mat, norm, builder, -1.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0, 1, 0, 240, r, g, b, alpha);
        this.drawVertex(mat, norm, builder, -1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0, 1, 0, 240, r, g, b, alpha);
        this.drawVertex(mat, norm, builder, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0, 1, 0, 240, r, g, b, alpha);
        this.drawVertex(mat, norm, builder, 1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 0, 1, 0, 240, r, g, b, alpha);
        ms.popPose();
    }

    private void renderFlameColumn(PoseStack ms, MultiBufferSource buffer, float radius, float age, float maxDuration, float currentAge, float r, float g, float b) {
        float heightFactor = 1.0F;
        if (currentAge < 10.0F) {
            heightFactor = currentAge / 10.0F;
        } else if (maxDuration - currentAge < 10.0F) {
            heightFactor = (maxDuration - currentAge) / 10.0F;
        }
        heightFactor = Math.max(0.0F, Math.min(1.0F, heightFactor));

        float flameHeight = radius * 4.0F * heightFactor;
        float flameWidth = radius * 0.9F;

        ms.pushPose();
        ms.mulPose(Axis.YP.rotationDegrees(age * 10.0F));
        VertexConsumer builder = buffer.getBuffer(RenderType.entityTranslucentEmissive(WHITE_FLAME_TEXTURE));

        for (int i = 0; i < 4; i++) {
            ms.pushPose();
            ms.mulPose(Axis.YP.rotationDegrees(i * 45.0F));
            Matrix4f m = ms.last().pose();
            Matrix3f n = ms.last().normal();

            float vOffset = -(age * 0.15F);

            // 表側
            drawVertex(m, n, builder, -flameWidth, 0.0F, 0.0F, 0.0F, 1.0F + vOffset, 0, 1, 0, 240, r, g, b, 0.7F);
            drawVertex(m, n, builder, flameWidth, 0.0F, 0.0F, 1.0F, 1.0F + vOffset, 0, 1, 0, 240, r, g, b, 0.7F);
            drawVertex(m, n, builder, flameWidth, flameHeight, 0.0F, 1.0F, 0.0F + vOffset, 0, 1, 0, 240, r, g, b, 0.7F);
            drawVertex(m, n, builder, -flameWidth, flameHeight, 0.0F, 0.0F, 0.0F + vOffset, 0, 1, 0, 240, r, g, b, 0.7F);

            // 裏側
            drawVertex(m, n, builder, flameWidth, 0.0F, 0.0F, 1.0F, 1.0F + vOffset, 0, -1, 0, 240, r, g, b, 0.7F);
            drawVertex(m, n, builder, -flameWidth, 0.0F, 0.0F, 0.0F, 1.0F + vOffset, 0, -1, 0, 240, r, g, b, 0.7F);
            drawVertex(m, n, builder, -flameWidth, flameHeight, 0.0F, 0.0F, 0.0F + vOffset, 0, -1, 0, 240, r, g, b, 0.7F);
            drawVertex(m, n, builder, flameWidth, flameHeight, 0.0F, 1.0F, 0.0F + vOffset, 0, -1, 0, 240, r, g, b, 0.7F);

            ms.popPose();
        }
        ms.popPose();
    }

    private void renderExplosionRing(PoseStack ms, MultiBufferSource buffer, float radius, float progress, float r, float g, float b) {
        float currentRad = radius * (1.0F + progress * 2.0F);
        float alpha = 1.0F - progress;

        ms.pushPose();
        ms.scale(currentRad, currentRad, currentRad);
        ms.translate(0.0D, 0.01D, 0.0D);
        Matrix4f mat = ms.last().pose();
        Matrix3f norm = ms.last().normal();
        VertexConsumer builder = buffer.getBuffer(RenderType.entityTranslucentEmissive(WHITE_RING_TEXTURE));
        this.drawVertex(mat, norm, builder, -1.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0, 1, 0, 240, r, g, b, alpha);
        this.drawVertex(mat, norm, builder, -1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0, 1, 0, 240, r, g, b, alpha);
        this.drawVertex(mat, norm, builder, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0, 1, 0, 240, r, g, b, alpha);
        this.drawVertex(mat, norm, builder, 1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 0, 1, 0, 240, r, g, b, alpha);
        ms.popPose();
    }
}