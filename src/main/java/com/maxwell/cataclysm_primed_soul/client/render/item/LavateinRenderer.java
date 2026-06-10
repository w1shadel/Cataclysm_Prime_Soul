package com.maxwell.cataclysm_primed_soul.client.render.item;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.client.model.item.LavateinModel;
import com.maxwell.cataclysm_primed_soul.item.LavateinItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("removal")
public class LavateinRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation TEXTURE_STAGE0 = new ResourceLocation(Primed_Soul.MODID, "textures/item/levatein.png");
    private final LavateinModel model;

    public LavateinRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.model = new LavateinModel();
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (stack.getItem() instanceof LavateinItem) {
            CompoundTag tag = stack.getTag();
            int stage = tag != null ? tag.getInt("Stage") : 0;
            ResourceLocation texture = TEXTURE_STAGE0;
            poseStack.pushPose();
            poseStack.translate(0.5D, 1.5D, 0.5D);
            poseStack.scale(1.0F, -1.0F, -1.0F);
            this.model.setupStage(stage);
            VertexConsumer consumer;
            consumer = ItemRenderer.getFoilBufferDirect(buffer, RenderType.entityCutoutNoCull(texture), true, stack.isEnchanted());
            this.model.renderToBuffer(poseStack, consumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }
    }
}