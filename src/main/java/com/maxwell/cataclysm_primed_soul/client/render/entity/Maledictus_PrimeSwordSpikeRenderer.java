package com.maxwell.cataclysm_primed_soul.client.render.entity;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.client.model.entity.Maledictus_PrimeSwordEntityModel;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.sub.Maledictus_PrimeSwordSpikeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
public class Maledictus_PrimeSwordSpikeRenderer extends EntityRenderer<Maledictus_PrimeSwordSpikeEntity> {
    private static final ResourceLocation TEXTURE_ARMOR = new ResourceLocation(Primed_Soul.MODID, "textures/entity/maledictus_prime/sword_armor.png");
    private static final ResourceLocation TEXTURE_GHOST = new ResourceLocation(Primed_Soul.MODID, "textures/entity/maledictus_prime/sword_ghost.png");
    private static final ResourceLocation TEXTURE_DISSOLVE_1 = new ResourceLocation(Primed_Soul.MODID, "textures/entity/maledictus_prime/sword_armor_dissolve_1.png");
    private static final ResourceLocation TEXTURE_DISSOLVE_2 = new ResourceLocation(Primed_Soul.MODID, "textures/entity/maledictus_prime/sword_armor_dissolve_2.png");
    private static final ResourceLocation TEXTURE_DISSOLVE_3 = new ResourceLocation(Primed_Soul.MODID, "textures/entity/maledictus_prime/sword_armor_dissolve_3.png");
    private static final ResourceLocation TEXTURE_DISSOLVE_4 = new ResourceLocation(Primed_Soul.MODID, "textures/entity/maledictus_prime/sword_armor_dissolve_4.png");
    private final Maledictus_PrimeSwordEntityModel model;

    public Maledictus_PrimeSwordSpikeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new Maledictus_PrimeSwordEntityModel(context.bakeLayer(Maledictus_PrimeSwordEntityModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(Maledictus_PrimeSwordSpikeEntity entity) {
        int life = entity.getLifeTicks();
        if (life >= 30) {
            if (life < 32) return TEXTURE_DISSOLVE_1;
            if (life < 35) return TEXTURE_DISSOLVE_2;
            if (life < 38) return TEXTURE_DISSOLVE_3;
            return TEXTURE_DISSOLVE_4;
        }
        return TEXTURE_ARMOR;
    }

    @Override
    public void render(Maledictus_PrimeSwordSpikeEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.getWarmupDelay() > 0) {
            return;
        }
        poseStack.pushPose();
        float yaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - yaw));
        float age = (float) entity.getLifeTicks() + partialTicks;
        float progress = Math.min(20.0F, age) / 20.0F;
        float scale = 0.75F;
        float baseHeight = 0.6F;
        float spawnDepth = -4.0F;
        float heightOffset = baseHeight + (spawnDepth * (1.0F - progress));
        poseStack.translate(0.0F, heightOffset, 0.0F);
        poseStack.scale(-scale, -scale, scale);
        poseStack.translate(0.0F, -1.5F, 0.0F);
        float alpha = 1.0F;
        if (age > 30.0F) {
            alpha = 1.0F - ((age - 30.0F) / 10.0F);
        }
        alpha = Math.max(0.0F, Math.min(1.0F, alpha));
        this.model.setupAnim(null, 0.0F, 0.0F, age, 0.0F, 0.0F);
        ResourceLocation currentTex = this.getTextureLocation(entity);
        VertexConsumer armorConsumer = buffer.getBuffer(RenderType.entityTranslucent(currentTex));
        this.model.renderToBuffer(poseStack, armorConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, alpha);
        VertexConsumer ghostConsumer = buffer.getBuffer(RenderType.eyes(TEXTURE_GHOST));
        this.model.renderToBuffer(poseStack, ghostConsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, alpha);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}