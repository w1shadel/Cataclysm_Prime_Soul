package com.maxwell.cataclysm_primed_soul;

import com.maxwell.cataclysm_primed_soul.config.ModConfig;
import com.maxwell.cataclysm_primed_soul.init.ModEntities;
import com.maxwell.cataclysm_primed_soul.init.ModItems;
import com.maxwell.cataclysm_primed_soul.init.ModTabs;
import com.maxwell.cataclysm_primed_soul.network.ModMessages;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Primed_Soul.MODID)
public class Primed_Soul {
    public static final String MODID = "cataclysm_primed_soul";

    public Primed_Soul(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModTabs.CREATIVE_TABS.register(modEventBus);

        ModConfig.register(context);

        modEventBus.addListener(this::addCreativeContents);
        modEventBus.addListener(this::commonSetup);
    }

    private void addCreativeContents(net.minecraftforge.event.BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModTabs.PRIME_TAB.get()) {
            ModItems.ITEMS.getEntries().forEach(item -> event.accept(item.get()));
        }
    }
    private void commonSetup(final net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) {
        event.enqueueWork(ModMessages::register);
    }
}