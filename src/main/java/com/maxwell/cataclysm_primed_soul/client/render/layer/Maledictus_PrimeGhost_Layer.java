package com.maxwell.cataclysm_primed_soul.client.render.layer;

import com.github.L_Ender.cataclysm.client.render.CMRenderTypes;
import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.client.model.entity.Maledictus_PrimeModel;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.Maledictus_PrimeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public class Maledictus_PrimeGhost_Layer extends RenderLayer<Maledictus_PrimeEntity, Maledictus_PrimeModel> {
    private static final ResourceLocation GHOST_TEXTURE = new ResourceLocation(
            Primed_Soul.MODID,
            "textures/entity/maledictus_prime/maledictus_prime_ghost.png"
    );

    public Maledictus_PrimeGhost_Layer(RenderLayerParent<Maledictus_PrimeEntity, Maledictus_PrimeModel> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Maledictus_PrimeEntity entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) {
            return;
        }

        VertexConsumer consumer = buffer.getBuffer(CMRenderTypes.getGhost(GHOST_TEXTURE));
        float pulse = 0.72F + 0.12F * (float) Math.sin((entity.tickCount + partialTicks) * 0.12F);
        this.getParentModel().renderToBuffer(
                poseStack,
                consumer,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1.0F,
                1.0F,
                1.0F,
                pulse
        );
    }
}
