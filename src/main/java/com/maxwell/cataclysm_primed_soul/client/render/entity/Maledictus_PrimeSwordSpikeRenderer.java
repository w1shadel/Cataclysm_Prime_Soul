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
        float emergeProgress = Math.min(1.0F, age / 5.0F);
        float popHeight = (float) Math.sin(emergeProgress * (Math.PI / 2.0D)) * 7.0F;
        float baseWidth = Mth.lerp(emergeProgress, 0.6F, 1.4F);
        float alpha = age > 24.0F ? Math.max(0.0F, 1.0F - ((age - 24.0F) / 14.0F)) : 1.0F;

        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(entity.getYRot()));
        GlacialVoxelHelper.renderSteppedGlacialPillar(poseStack, buffer, baseWidth, popHeight, 5, alpha);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
