package com.maxwell.cataclysm_primed_soul.client.render.entity;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.client.model.entity.Ignis_PrimeModel;
import com.maxwell.cataclysm_primed_soul.client.render.layer.Ignis_PrimeInterpolation_Layer;
import com.maxwell.cataclysm_primed_soul.entity.InternalAnimationMonster.IABossMonsters.Ignis_PrimeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class Ignis_PrimeRenderer extends MobRenderer<Ignis_PrimeEntity, Ignis_PrimeModel> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[7];

    public Ignis_PrimeRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new Ignis_PrimeModel(renderManagerIn.bakeLayer(Ignis_PrimeModel.LAYER_LOCATION)), 1.0F);
        for (int i = 0; i < 7; ++i) {
            TEXTURES[i] = new ResourceLocation(Primed_Soul.MODID, "textures/entity/ignis_prime/ignis_prime_textures_" + i + ".png");
        }
        this.addLayer(new Ignis_PrimeInterpolation_Layer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Ignis_PrimeEntity entity) {
        return TEXTURES[0];
    }

    @Nullable
    @Override
    protected RenderType getRenderType(Ignis_PrimeEntity entity, boolean bodyVisible, boolean translucent, boolean outline) {
        return RenderType.entityTranslucent(new ResourceLocation(Primed_Soul.MODID, "textures/entity/empty.png"));
    }

    @Override
    protected void scale(Ignis_PrimeEntity entity, PoseStack matrixStack, float partialTick) {
        float s = 1.6F;
        matrixStack.scale(s, s, s);
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