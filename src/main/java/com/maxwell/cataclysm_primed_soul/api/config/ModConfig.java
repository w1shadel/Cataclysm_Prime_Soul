package com.maxwell.cataclysm_primed_soul.api.config;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


public class ModConfig {
    public static void register(FMLJavaModLoadingContext context) {
        context.registerConfig(
                net.minecraftforge.fml.config.ModConfig.Type.SERVER,
                IgnisPrimeConfig.SPEC,
                "cataclysm_primed_soul-server.toml"
        );
    }
}
