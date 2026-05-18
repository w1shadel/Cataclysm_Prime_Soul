package com.maxwell.cataclysm_primed_soul.client.render.entity;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.sub.Prime_Flame_Strike_Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class Prime_Flame_Strike_Renderer extends EntityRenderer<Prime_Flame_Strike_Entity> {
    public static final ResourceLocation SOUL_FLAME_STRIKE = new ResourceLocation("cataclysm", "textures/entity/soul_flame_strike_sigil.png");
    public static final ResourceLocation PRIME_SIGIL_TEXTURE = new ResourceLocation(Primed_Soul.MODID, "textures/entity/prime_sigil.png");

    public Prime_Flame_Strike_Renderer(EntityRendererProvider.Context mgr) {
        super(mgr);
    }

    @Override
    public ResourceLocation getTextureLocation(Prime_Flame_Strike_Entity entity) {
        return SOUL_FLAME_STRIKE;
    }

    @Override
    public void render(Prime_Flame_Strike_Entity flameStrike, float entityYaw, float delta, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        float age = (float) flameStrike.tickCount + delta;
        float radius = flameStrike.getRadius();
        renderSigil(matrixStackIn, bufferIn, radius, age * 2.0F, 1.0F, 1.0F, 1.0F, 0.8F);
        renderSigil(matrixStackIn, bufferIn, radius * 0.7F, age * -4.0F, 0.9F, 0.95F, 1.0F, 1.0F);
        matrixStackIn.popPose();
        super.render(flameStrike, entityYaw, delta, matrixStackIn, bufferIn, packedLightIn);
    }

    public void drawVertex(Matrix4f matrix, Matrix3f normal, VertexConsumer builder, int x, int y, int z, float u, float v, int nx, int ny, int nz, int light, float r, float g, float b, float a) {
        builder.vertex(matrix, (float) x, (float) y, (float) z)
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
        this.drawVertex(mat, norm, builder, -1, 0, -1, 0.0F, 0.0F, 0, 1, 0, 240, r, g, b, alpha);
        this.drawVertex(mat, norm, builder, -1, 0, 1, 0.0F, 1.0F, 0, 1, 0, 240, r, g, b, alpha);
        this.drawVertex(mat, norm, builder, 1, 0, 1, 1.0F, 1.0F, 0, 1, 0, 240, r, g, b, alpha);
        this.drawVertex(mat, norm, builder, 1, 0, -1, 1.0F, 0.0F, 0, 1, 0, 240, r, g, b, alpha);
        ms.popPose();
    }
}