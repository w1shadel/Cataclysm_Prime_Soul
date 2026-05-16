package com.maxwell.cataclysm_primed_soul;

import com.github.L_Ender.cataclysm.client.gui.CustomBossBar;
import com.maxwell.cataclysm_primed_soul.client.model.entity.Ignis_PrimeModel;
import com.maxwell.cataclysm_primed_soul.client.render.entity.Ignis_PrimeRenderer;
import com.maxwell.cataclysm_primed_soul.init.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
        modid = Primed_Soul.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = {Dist.CLIENT}
)
public class ClientModEvent {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

        event.enqueueWork(() -> {
            CustomBossBar.customBossBars.put(99, new CustomBossBar(
                    new ResourceLocation(Primed_Soul.MODID, "textures/gui/boss_bar/ignis_prime_bar_base.png"),
                    new ResourceLocation(Primed_Soul.MODID, "textures/gui/boss_bar/ignis_prime_bar_overlay.png"),
                    5, 16, 1, 1, -2, -2, 256, 16, 25, 182, ChatFormatting.GOLD
            ));
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.IGNIS_PRIME.get(), Ignis_PrimeRenderer::new);
    }
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(Ignis_PrimeModel.LAYER_LOCATION, Ignis_PrimeModel::createBodyLayer);
    }
}