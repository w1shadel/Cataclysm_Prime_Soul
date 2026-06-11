package com.maxwell.cataclysm_primed_soul.mixin.client;

import com.github.L_Ender.cataclysm.blockentities.Cursed_tombstone_Entity;
import com.github.L_Ender.cataclysm.client.render.blockentity.Cursed_Tombstone_Renderer;
import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.client.model.entity.Maledictus_PrimeSwordEntityModel;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.MaledictusSummonEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@SuppressWarnings("removal")
@Mixin(Cursed_Tombstone_Renderer.class)
public class CursedTombstoneRendererMixin {
    @Unique
    private static final ResourceLocation SWORD_ARMOR = new ResourceLocation(Primed_Soul.MODID, "textures/entity/maledictus_prime/sword_armor.png");
    @Unique
    private Maledictus_PrimeSwordEntityModel swordModel;

    @Inject(method = "render", at = @At("TAIL"), remap = false)
    private void hookRender(Cursed_tombstone_Entity entity, float delta, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int overlay, CallbackInfo ci) {
        boolean isPrime = MaledictusSummonEvent.PRIME_TOMBSTONES.contains(entity.getBlockPos()) || entity.getPersistentData().getBoolean("isPrimeSummon");
        if (isPrime) {
            if (this.swordModel == null) {
                this.swordModel = new Maledictus_PrimeSwordEntityModel(net.minecraft.client.Minecraft.getInstance().getEntityModels().bakeLayer(Maledictus_PrimeSwordEntityModel.LAYER_LOCATION));
            }

            poseStack.pushPose();

            poseStack.translate(0.5F, 1.25F, 0.5F);

            poseStack.scale(1.2F, 1.2F, 1.2F);

            float age = (float)entity.tickCount + delta;
            this.swordModel.setupAnim(null, 0.0F, 0.0F, age, 0.0F, 0.0F);

            VertexConsumer armorConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(SWORD_ARMOR));
            this.swordModel.renderToBuffer(poseStack, armorConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);


            poseStack.popPose();
        }
    }
}