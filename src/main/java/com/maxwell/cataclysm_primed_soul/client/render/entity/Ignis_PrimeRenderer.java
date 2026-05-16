package com.maxwell.cataclysm_primed_soul.client.render.entity;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.client.model.entity.Ignis_PrimeModel;
import com.maxwell.cataclysm_primed_soul.entity.InternalAnimationMonster.IABossMonsters.Ignis_PrimeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Ignis_PrimeRenderer extends MobRenderer<Ignis_PrimeEntity, Ignis_PrimeModel> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[7];

    public Ignis_PrimeRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new Ignis_PrimeModel(renderManagerIn.bakeLayer(Ignis_PrimeModel.LAYER_LOCATION)), 1.0F);

        for(int i = 0; i < 7; ++i) {
            TEXTURES[i] = new ResourceLocation(Primed_Soul.MODID, "textures/entity/ignis_prime/ignis_prime_textures_" + i + ".png");
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Ignis_PrimeEntity entity) {
        int frame = (int)((float)entity.tickCount * 0.5F % 7.0F);
        return TEXTURES[Mth.clamp(frame, 0, 6)];
    }

    @Override
    public void render(Ignis_PrimeEntity entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    protected int getBlockLightLevel(Ignis_PrimeEntity entityIn, BlockPos pos) {
        return 15;
    }

    @Override
    protected float getFlipDegrees(Ignis_PrimeEntity entity) {
        return 0.0F;
    }
}