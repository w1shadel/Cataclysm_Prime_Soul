package com.maxwell.cataclysm_primed_soul.client.render.layer;

import com.github.L_Ender.cataclysm.client.render.CMRenderTypes;
import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.client.model.entity.MaledictusPhantomModel;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.MaledictusPhantomEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public class MaledictusPhantomGhostLayer extends RenderLayer<MaledictusPhantomEntity, MaledictusPhantomModel> {
    private static final ResourceLocation GHOST_TEXTURE = new ResourceLocation(
            Primed_Soul.MODID,
            "textures/entity/maledictus_prime/maledictus_prime_ghost.png"
    );

    public MaledictusPhantomGhostLayer(RenderLayerParent<MaledictusPhantomEntity, MaledictusPhantomModel> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, MaledictusPhantomEntity entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) {
            return;
        }
        VertexConsumer consumer = buffer.getBuffer(CMRenderTypes.getGhost(GHOST_TEXTURE));
        float pulse = 0.45F + 0.12F * (float) Math.sin((entity.tickCount + partialTicks) * 0.15F);
        float alpha = computeAlpha(entity, partialTicks);
        float finalAlpha = pulse * (alpha / 0.55F);
        this.getParentModel().renderToBuffer(
                poseStack,
                consumer,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1.0F,
                1.0F,
                1.0F,
                finalAlpha
        );
    }

    private float computeAlpha(MaledictusPhantomEntity entity, float partialTick) {
        int maxLife = switch (entity.getPhantomType()) {
            case MaledictusPhantomEntity.TYPE_MACE -> 35;
            case MaledictusPhantomEntity.TYPE_BOW -> 58;
            default -> 50;
        };
        float age = entity.tickCount + partialTick;
        float alpha = 0.55F;
        if (age < 8.0F) {
            alpha = (age / 8.0F) * 0.55F;
        } else {
            float remaining = maxLife - age;
            if (remaining < 8.0F) {
                alpha = Math.max(0.0F, (remaining / 8.0F) * 0.55F);
            }
        }
        return alpha;
    }
}