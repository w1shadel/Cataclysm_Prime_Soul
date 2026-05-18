package com.maxwell.cataclysm_primed_soul.client.render.layer;

import com.github.L_Ender.cataclysm.client.render.CMRenderTypes;
import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.client.model.entity.Ignis_PrimeModel;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.Ignis_PrimeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

@SuppressWarnings("removal")
public class Ignis_PrimeInterpolation_Layer extends RenderLayer<Ignis_PrimeEntity, Ignis_PrimeModel> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[7];

    public Ignis_PrimeInterpolation_Layer(RenderLayerParent<Ignis_PrimeEntity, Ignis_PrimeModel> parent) {
        super(parent);
        for (int i = 0; i < 7; ++i) {
            TEXTURES[i] = new ResourceLocation(Primed_Soul.MODID, "textures/entity/ignis_prime/ignis_prime_textures_" + i + ".png");
        }
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, Ignis_PrimeEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) return;

        float speed = 0.4f;
        int totalTextures = 7;
        int loopSteps = (totalTextures - 1) * 2;
        float preciseTick = (float) entity.tickCount + partialTicks;
        float time = preciseTick * speed;
        float currentStepFloat = time % (float) loopSteps;
        int currentStep = (int) Math.floor(currentStepFloat);
        int nextStep = (currentStep + 1) % loopSteps;
        float alpha = currentStepFloat - (float) currentStep;
        int currentFrame = getPingPongFrame(currentStep, totalTextures, loopSteps);
        int nextFrame = getPingPongFrame(nextStep, totalTextures, loopSteps);
        ResourceLocation currentTexture = TEXTURES[Mth.clamp(currentFrame, 0, 6)];
        ResourceLocation nextTexture = TEXTURES[Mth.clamp(nextFrame, 0, 6)];
        renderSmooth(matrixStack, buffer, packedLight, entity, currentTexture, 1.0F);
        renderSmooth(matrixStack, buffer, packedLight, entity, nextTexture, alpha);
    }

    private void renderSmooth(PoseStack ms, MultiBufferSource buffer, int light, Ignis_PrimeEntity entity, ResourceLocation tex, float alpha) {
        if (alpha <= 0.001f) return;
        VertexConsumer vc = buffer.getBuffer(CMRenderTypes.getGhost(tex));
        boolean hurt = entity.hurtTime > 0 || entity.deathTime > 0;
        float red = 1.0F;
        float green = hurt ? 0.35F : 1.0F;
        float blue = hurt ? 0.35F : 1.0F;
        this.getParentModel().renderToBuffer(ms, vc, light, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), red, green, blue, alpha);
    }

    private int getPingPongFrame(int step, int total, int loop) {
        if (step < total) return step;
        return loop - step;
    }
}
