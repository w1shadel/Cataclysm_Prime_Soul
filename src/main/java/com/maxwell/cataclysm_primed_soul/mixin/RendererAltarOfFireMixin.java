package com.maxwell.cataclysm_primed_soul.mixin;

import com.github.L_Ender.cataclysm.blockentities.AltarOfFire_Block_Entity;
import com.github.L_Ender.cataclysm.client.render.blockentity.RendererAltar_of_Fire;
import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
@SuppressWarnings("removal")
@Mixin(value = RendererAltar_of_Fire.class, remap = false)
public class RendererAltarOfFireMixin {

    private static final ResourceLocation PRIME_SIGIL = new ResourceLocation(Primed_Soul.MODID, "textures/entity/prime_sigil.png");

    @Redirect(method = "renderSigil", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;entityTranslucentEmissive(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"))
    private RenderType redirectSigilTexture(ResourceLocation location, AltarOfFire_Block_Entity tileEntityIn) {
        if (!tileEntityIn.getItem(0).isEmpty() && net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(tileEntityIn.getItem(0).getItem()).toString().equals("cataclysm_primed_soul:abyssal_ashes")) {
            return RenderType.entityTranslucentEmissive(PRIME_SIGIL);
        }
        return RenderType.entityTranslucentEmissive(location);
    }
}