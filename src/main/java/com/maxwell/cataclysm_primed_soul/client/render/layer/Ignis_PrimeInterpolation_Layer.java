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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

@SuppressWarnings("removal")
public class Ignis_PrimeInterpolation_Layer extends RenderLayer<Ignis_PrimeEntity, Ignis_PrimeModel> {
    private static final ResourceLocation[] BLUE_TEXTURES = new ResourceLocation[7];
    private static final ResourceLocation[] WHITE_TEXTURES = new ResourceLocation[7];

    public Ignis_PrimeInterpolation_Layer(RenderLayerParent<Ignis_PrimeEntity, Ignis_PrimeModel> parent) {
        super(parent);
        for (int i = 0; i < 7; ++i) {
            BLUE_TEXTURES[i] = new ResourceLocation(Primed_Soul.MODID, "textures/entity/ignis_prime/one_ignis_prime_textures_" + i + ".png");
            WHITE_TEXTURES[i] = new ResourceLocation(Primed_Soul.MODID, "textures/entity/ignis_prime/ignis_prime_textures_" + i + ".png");
        }
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, Ignis_PrimeEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) return;

        ResourceLocation[] currentTextures = BLUE_TEXTURES;
        if (entity.getBossPhase() >= 2) {
            if (entity.getAttackState() == Ignis_PrimeEntity.STATE_PHASE_CHANGE) {
                if (entity.attackTicks >= 70) {
                    currentTextures = WHITE_TEXTURES;
                }
            } else {
                currentTextures = WHITE_TEXTURES;
            }
        }

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

        ResourceLocation currentTexture = currentTextures[Mth.clamp(currentFrame, 0, 6)];
        ResourceLocation nextTexture = currentTextures[Mth.clamp(nextFrame, 0, 6)];

        renderSmooth(matrixStack, buffer, packedLight, entity, currentTexture, 1.0F);
        renderSmooth(matrixStack, buffer, packedLight, entity, nextTexture, alpha);
    }

    private void renderSmooth(PoseStack ms, MultiBufferSource buffer, int light, Ignis_PrimeEntity entity, ResourceLocation tex, float alpha) {
        if (alpha <= 0.001f) return;
        VertexConsumer vc = buffer.getBuffer(CMRenderTypes.getGhost(tex));
        boolean hurt = entity.shouldRenderHurtFlash();
        float red = 1.0F;
        float green = hurt ? 0.35F : 1.0F;
        float blue = hurt ? 0.35F : 1.0F;
        int overlay = hurt ? LivingEntityRenderer.getOverlayCoords(entity, 0.0F) : OverlayTexture.NO_OVERLAY;
        this.getParentModel().renderToBuffer(ms, vc, light, overlay, red, green, blue, alpha);
    }

    private int getPingPongFrame(int step, int total, int loop) {
        if (step < total) return step;
        return loop - step;
    }
}