package com.maxwell.cataclysm_primed_soul.client.render.entity;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.client.model.entity.Maledictus_PrimeSwordEntityModel;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.sub.Maledictus_PrimeSwordEntity;
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
public class Maledictus_PrimeSwordRenderer extends EntityRenderer<Maledictus_PrimeSwordEntity> {
    private static final ResourceLocation TEXTURE_ARMOR = new ResourceLocation(Primed_Soul.MODID, "textures/entity/maledictus_prime/sword_armor.png");
    private static final ResourceLocation TEXTURE_GHOST = new ResourceLocation(Primed_Soul.MODID, "textures/entity/maledictus_prime/sword_ghost.png");
    private final Maledictus_PrimeSwordEntityModel model;

    public Maledictus_PrimeSwordRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new Maledictus_PrimeSwordEntityModel(context.bakeLayer(Maledictus_PrimeSwordEntityModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(Maledictus_PrimeSwordEntity entity) {
        return TEXTURE_ARMOR;
    }

    @Override
    public void render(Maledictus_PrimeSwordEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float yaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - yaw));
        float age = (float) entity.tickCount + partialTicks;
        float spinAngle = age * 25.0F;
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(spinAngle));
        poseStack.scale(-1.3F, -1.3F, 1.3F);
        poseStack.translate(0.0F, -1.5F, 0.0F);
        this.model.setupAnim(entity, 0.0F, 0.0F, age, 0.0F, 0.0F);
        VertexConsumer armorConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_ARMOR));
        this.model.renderToBuffer(poseStack, armorConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        VertexConsumer ghostConsumer = buffer.getBuffer(RenderType.eyes(TEXTURE_GHOST));
        this.model.renderToBuffer(poseStack, ghostConsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}