package com.maxwell.cataclysm_primed_soul.client.render.entity;

import com.github.L_Ender.cataclysm.client.model.entity.Ignis_Fireball_Model;
import com.github.L_Ender.cataclysm.client.render.CMRenderTypes;
import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.Ignis_PrimeEntity;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.sub.Prime_Fireball_Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
public class Prime_Fireball_Renderer extends EntityRenderer<Prime_Fireball_Entity> {
    private static final ResourceLocation TRAIL_TEXTURE = new ResourceLocation("cataclysm", "textures/particle/storm.png");
    private static ResourceLocation DYNAMIC_FIREBALL_TEXTURE = null;
    private final RandomSource random = RandomSource.create();
    public Ignis_Fireball_Model model = new Ignis_Fireball_Model();

    public Prime_Fireball_Renderer(EntityRendererProvider.Context manager) {
        super(manager);
    }

    private static void registerDynamicFireballTexture(net.minecraft.client.renderer.texture.TextureManager textureManager) {
        if (DYNAMIC_FIREBALL_TEXTURE == null) {
            DYNAMIC_FIREBALL_TEXTURE = new ResourceLocation(Primed_Soul.MODID, "textures/dynamic/prime_fireball.png");
            net.minecraft.client.renderer.texture.DynamicTexture dynamicTexture = new net.minecraft.client.renderer.texture.DynamicTexture(32, 32, true);
            com.mojang.blaze3d.platform.NativeImage nativeImage = dynamicTexture.getPixels();
            if (nativeImage != null) {
                for (int y = 0; y < 32; y++) {
                    float dy = (y - 15.5F) / 15.5F;
                    for (int x = 0; x < 32; x++) {
                        float dx = (x - 15.5F) / 15.5F;
                        float dist = (float) Math.sqrt(dx * dx + dy * dy);
                        float intensity = 1.0F - (dist / 1.2F);
                        intensity = Math.max(0.0F, Math.min(1.0F, intensity));
                        intensity = intensity * intensity;
                        int alpha = (int) (intensity * 255.0F);
                        int color = (alpha << 24) | (255 << 16) | (255 << 8) | 255;
                        nativeImage.setPixelRGBA(x, y, color);
                    }
                }
                dynamicTexture.upload();
                textureManager.register(DYNAMIC_FIREBALL_TEXTURE, dynamicTexture);
            }
        }
    }

    @Override
    protected int getBlockLightLevel(Prime_Fireball_Entity entity, BlockPos pos) {
        return 15;
    }

    @Override
    public void render(Prime_Fireball_Entity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        net.minecraft.client.renderer.texture.TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        registerDynamicFireballTexture(textureManager);
        matrixStackIn.pushPose();
        float f = this.rotLerp(entityIn.yRotO, entityIn.getYRot(), partialTicks);
        float f1 = Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot());
        float f2 = (float) entityIn.tickCount + partialTicks;
        matrixStackIn.translate((double) 0.0F, (double) 0.3F, (double) 0.0F);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.sin(f2 * 0.1F) * 180.0F));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(Mth.cos(f2 * 0.1F) * 180.0F));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(Mth.sin(f2 * 0.15F) * 360.0F));
        this.model.setupAnim(entityIn, 0.0F, 0.0F, 0.0F, f, f1);
        float red = 0.25F;
        float green = 0.55F;
        float blue = 1.0F;
        Entity owner = entityIn.getOwner();
        if (owner instanceof Ignis_PrimeEntity boss && boss.getBossPhase() >= 2) {
            red = 1.0F;
            green = 0.92F;
            blue = 1.0F;
        }
        VertexConsumer VertexConsumer = bufferIn.getBuffer(RenderType.entityTranslucentEmissive(this.getTextureLocation(entityIn)));
        this.model.renderToBuffer(matrixStackIn, VertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
        matrixStackIn.popPose();
        if (entityIn.hasTrail()) {
            double x = Mth.lerp((double) partialTicks, entityIn.xOld, entityIn.getX());
            double y = Mth.lerp((double) partialTicks, entityIn.yOld, entityIn.getY());
            double z = Mth.lerp((double) partialTicks, entityIn.zOld, entityIn.getZ());
            float ran = 0.04F;
            float trailR = red + this.random.nextFloat() * ran;
            float trailG = green + this.random.nextFloat() * ran;
            float trailB = blue + this.random.nextFloat() * ran;
            matrixStackIn.pushPose();
            matrixStackIn.translate(-x, -y, -z);
            this.renderTrail(entityIn, partialTicks, matrixStackIn, bufferIn, trailR, trailG, trailB, 1.0F, packedLightIn);
            matrixStackIn.popPose();
        }
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getTextureLocation(Prime_Fireball_Entity entity) {
        return DYNAMIC_FIREBALL_TEXTURE;
    }

    private void renderTrail(Prime_Fireball_Entity entityIn, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, float trailR, float trailG, float trailB, float trailA, int packedLightIn) {
        int sampleSize = 10;
        float trailHeight = 0.2F;
        float trailYRot = 0.0F;
        float trailZRot = 0.0F;
        Vec3 topAngleVec = (new Vec3((double) trailHeight, (double) trailHeight, (double) 0.0F)).yRot(trailYRot).zRot(trailZRot);
        Vec3 bottomAngleVec = (new Vec3((double) (-trailHeight), (double) (-trailHeight), (double) 0.0F)).yRot(trailYRot).zRot(trailZRot);
        Vec3 drawFrom = entityIn.getTrailPosition(0, partialTicks);
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        VertexConsumer vertexconsumer = bufferIn.getBuffer(CMRenderTypes.getLightTrailEffect(TRAIL_TEXTURE));
        for (int samples = 0; samples < sampleSize; ++samples) {
            Vec3 sample = entityIn.getTrailPosition(samples + 2, partialTicks);
            float u1 = (float) samples / (float) sampleSize;
            float u2 = u1 + 1.0F / (float) sampleSize;
            this.addVertex(vertexconsumer, matrix4f, matrix3f, drawFrom, bottomAngleVec, trailR, trailG, trailB, u1, 1.0F, packedLightIn);
            this.addVertex(vertexconsumer, matrix4f, matrix3f, sample, bottomAngleVec, trailR, trailG, trailB, u2, 1.0F, packedLightIn);
            this.addVertex(vertexconsumer, matrix4f, matrix3f, sample, topAngleVec, trailR, trailG, trailB, u2, 0.0F, packedLightIn);
            this.addVertex(vertexconsumer, matrix4f, matrix3f, drawFrom, topAngleVec, trailR, trailG, trailB, u1, 0.0F, packedLightIn);
            drawFrom = sample;
        }
    }

    private void addVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f matrix3, Vec3 pos, Vec3 offset, float r, float g, float b, float u, float v, int light) {
        consumer.vertex(matrix, (float) (pos.x + offset.x), (float) (pos.y + offset.y), (float) (pos.z + offset.z)).color(r, g, b, 1.0F).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private float rotLerp(float prevRotation, float rotation, float partialTicks) {
        float f;
        for (f = rotation - prevRotation; f < -180.0F; f += 360.0F) {
        }
        while (f >= 180.0F) {
            f -= 360.0F;
        }
        return prevRotation + partialTicks * f;
    }
}