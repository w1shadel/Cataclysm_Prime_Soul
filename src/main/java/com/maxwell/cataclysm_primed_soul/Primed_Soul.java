package com.maxwell.cataclysm_primed_soul;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Primed_Soul.MODID)
public class Primed_Soul
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "cataclysm_primed_soul";
    public Primed_Soul(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();
    }
}
