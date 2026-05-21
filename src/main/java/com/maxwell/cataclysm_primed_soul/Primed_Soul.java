package com.maxwell.cataclysm_primed_soul;

import com.maxwell.cataclysm_primed_soul.init.ModEntities;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Primed_Soul.MODID)
public class Primed_Soul {
    public static final String MODID = "cataclysm_primed_soul";

    public Primed_Soul(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ModEntities.ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            com.maxwell.cataclysm_primed_soul.network.ModMessages.register();
        });
    }
}
