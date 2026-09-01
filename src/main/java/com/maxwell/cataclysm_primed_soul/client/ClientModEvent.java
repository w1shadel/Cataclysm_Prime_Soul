package com.maxwell.cataclysm_primed_soul.client;

import com.github.L_Ender.cataclysm.client.gui.CustomBossBar;
import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.client.compat.gtbcs_cui.PrimeBossScreens;
import com.maxwell.cataclysm_primed_soul.client.model.entity.Ignis_PrimeModel;
import com.maxwell.cataclysm_primed_soul.client.model.entity.MaledictusPhantomModel;
import com.maxwell.cataclysm_primed_soul.client.model.entity.Maledictus_PrimeModel;
import com.maxwell.cataclysm_primed_soul.client.model.entity.Maledictus_PrimeSwordEntityModel;
import com.maxwell.cataclysm_primed_soul.client.render.entity.*;
import com.maxwell.cataclysm_primed_soul.init.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("removal")
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
                    5, 16, 1, 1, -2, -2, 256, 16, 25, 182, ChatFormatting.AQUA
            ));
            PrimeBossScreens.registerAll();
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.IGNIS_PRIME.get(), Ignis_PrimeRenderer::new);
        event.registerEntityRenderer(ModEntities.MALEDICTUS_PRIME.get(), Maledictus_PrimeRenderer::new);
        event.registerEntityRenderer(ModEntities.MALEDICTUS_PHANTOM.get(), MaledictusPhantomRenderer::new);
        event.registerEntityRenderer(ModEntities.PRIME_FLAME_STRIKE.get(), Prime_Flame_Strike_Renderer::new);
        event.registerEntityRenderer(ModEntities.PRIME_FIREBALL.get(), Prime_Fireball_Renderer::new);
        event.registerEntityRenderer(ModEntities.MALEDICTUS_PRIME_SWORD.get(), Maledictus_PrimeSwordRenderer::new);
        event.registerEntityRenderer(ModEntities.MALEDICTUS_PRIME_SWORD_SPIKE.get(), Maledictus_PrimeSwordSpikeRenderer::new);
        event.registerEntityRenderer(ModEntities.IGNIS_PRIME_CUTSCENE.get(), Ignis_PrimeRenderer::new);
        event.registerEntityRenderer(ModEntities.PURE_WHITE_ENERGY_SPHERE.get(), PureWhiteEnergySphereRenderer::new);
        event.registerEntityRenderer(ModEntities.MALEDICTUS_PRIME_CUTSCENE.get(), Maledictus_PrimeRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(Ignis_PrimeModel.LAYER_LOCATION, Ignis_PrimeModel::createBodyLayer);
        event.registerLayerDefinition(Maledictus_PrimeModel.LAYER_LOCATION, Maledictus_PrimeModel::createBodyLayer);
        event.registerLayerDefinition(MaledictusPhantomModel.LAYER_LOCATION, Maledictus_PrimeModel::createBodyLayer);
        event.registerLayerDefinition(Maledictus_PrimeSwordEntityModel.LAYER_LOCATION, Maledictus_PrimeSwordEntityModel::createBodyLayer);
    }
}
