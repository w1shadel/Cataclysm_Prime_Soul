package com.maxwell.cataclysm_primed_soul.client.render.entity;

import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.sub.Maledictus_PrimeSwordSpikeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
public class Maledictus_PrimeSwordSpikeRenderer extends EntityRenderer<Maledictus_PrimeSwordSpikeEntity> {
    public Maledictus_PrimeSwordSpikeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Maledictus_PrimeSwordSpikeEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public void render(Maledictus_PrimeSwordSpikeEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.getWarmupDelay() > 0) {
            return;
        }

        poseStack.pushPose();
        float age = entity.getLifeTicks() + partialTicks;
        float emergeProgress = Math.min(1.0F, age / 6.0F);
        float popHeight = (1.0F - (float) Math.pow(1.0F - emergeProgress, 3.0D)) * 10.5F;
        float radius = Mth.lerp(emergeProgress, 0.4F, 1.35F);
        float alpha = age > 25.0F ? 1.0F - ((age - 25.0F) / 15.0F) : 1.0F;
        alpha = Mth.clamp(alpha, 0.0F, 1.0F);

        float yaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - yaw + age * 2.0F));
        GlacialRenderHelper.renderMassiveGlacialPillar(poseStack, buffer, radius, popHeight, alpha);
        poseStack.popPose();
    }
}
