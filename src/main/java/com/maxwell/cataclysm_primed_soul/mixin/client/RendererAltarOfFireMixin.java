package com.maxwell.cataclysm_primed_soul.mixin.client;

import com.github.L_Ender.cataclysm.blockentities.AltarOfFire_Block_Entity;
import com.github.L_Ender.cataclysm.client.render.blockentity.RendererAltar_of_Fire;
import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("removal")
@Mixin(value = RendererAltar_of_Fire.class, remap = false)
public abstract class RendererAltarOfFireMixin {
    private static final ResourceLocation PRIME_SIGIL = new ResourceLocation(Primed_Soul.MODID, "textures/entity/prime_sigil.png");

    @Shadow
    public abstract void drawVertex(Matrix4f p_229039_1_, Matrix3f p_229039_2_, VertexConsumer p_229039_3_, int p_229039_4_, int p_229039_5_, int p_229039_6_, float p_229039_7_, float p_229039_8_, int p_229039_9_, int p_229039_10_, int p_229039_11_, int p_229039_12_);

    @Inject(method = "renderSigil", at = @At("HEAD"), cancellable = true)
    private void onRenderSigil(AltarOfFire_Block_Entity tileEntityIn, float delta, PoseStack matrixStackIn, MultiBufferSource bufferIn, CallbackInfo ci) {
        if (tileEntityIn.summoningthis) {
            ItemStack held = tileEntityIn.getItem(0);
            String itemID = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(held.getItem()).toString();
            if (!held.isEmpty() && itemID.equals("cataclysm_primed_soul:abyssal_ashes")) {
                float f2 = (float) tileEntityIn.tickCount + delta;
                float f3 = (float) Mth.clamp(tileEntityIn.summoningticks, 0, 25);
                matrixStackIn.pushPose();
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucentEmissive(PRIME_SIGIL));
                matrixStackIn.translate(0.5D, 0.001D, 0.5D);
                matrixStackIn.scale(f3 * 0.1F, f3 * 0.1F, f3 * 0.1F);
                matrixStackIn.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F + f2));
                PoseStack.Pose lastPose = matrixStackIn.last();
                Matrix4f poseMatrix = lastPose.pose();
                Matrix3f normalMatrix = lastPose.normal();
                this.drawVertex(poseMatrix, normalMatrix, ivertexbuilder, -1, 0, -1, 0.0F, 0.0F, 1, 0, 1, 240);
                this.drawVertex(poseMatrix, normalMatrix, ivertexbuilder, -1, 0, 1, 0.0F, 1.0F, 1, 0, 1, 240);
                this.drawVertex(poseMatrix, normalMatrix, ivertexbuilder, 1, 0, 1, 1.0F, 1.0F, 1, 0, 1, 240);
                this.drawVertex(poseMatrix, normalMatrix, ivertexbuilder, 1, 0, -1, 1.0F, 0.0F, 1, 0, 1, 240);
                matrixStackIn.popPose();
                ci.cancel();
            }
        }
    }
}