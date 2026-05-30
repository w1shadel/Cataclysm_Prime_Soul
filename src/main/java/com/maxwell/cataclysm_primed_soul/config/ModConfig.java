package com.maxwell.cataclysm_primed_soul.config;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Mod全体のコンフィグ登録ハブ。
 * Primed_Soul のコンストラクタから register() を呼び出す。
 */
public class ModConfig {
    public static void register(FMLJavaModLoadingContext context) {
        context.registerConfig(
                net.minecraftforge.fml.config.ModConfig.Type.SERVER,
                IgnisPrimeConfig.SPEC,
                "cataclysm_primed_soul-server.toml"
        );
    }
}
