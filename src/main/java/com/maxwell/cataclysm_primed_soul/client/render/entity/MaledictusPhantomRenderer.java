package com.maxwell.cataclysm_primed_soul.client.render.entity;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.client.model.entity.MaledictusPhantomModel;
import com.maxwell.cataclysm_primed_soul.client.render.layer.MaledictusPhantomGhostLayer;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.MaledictusPhantomEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
public class MaledictusPhantomRenderer extends MobRenderer<MaledictusPhantomEntity, MaledictusPhantomModel> {
    private static final ResourceLocation ARMOR_TEXTURE = new ResourceLocation(
            Primed_Soul.MODID,
            "textures/entity/maledictus_prime/maledictus_prime_armor.png"
    );

    public MaledictusPhantomRenderer(EntityRendererProvider.Context context) {
        super(context, new MaledictusPhantomModel(
                context.bakeLayer(MaledictusPhantomModel.LAYER_LOCATION)), 1.0F);
        this.addLayer(new MaledictusPhantomGhostLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(MaledictusPhantomEntity entity) {
        return ARMOR_TEXTURE;
    }

    @Override
    protected void scale(MaledictusPhantomEntity entity, PoseStack poseStack, float partialTick) {
        float s = 0.96F;
        poseStack.scale(s, s, s);
    }

    @Override
    protected int getBlockLightLevel(MaledictusPhantomEntity entity, BlockPos pos) {
        return Math.min(super.getBlockLightLevel(entity, pos), 2);
    }
}