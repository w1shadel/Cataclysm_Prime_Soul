package com.maxwell.cataclysm_primed_soul.client.compat.gtbcs_cui;

import com.gametechbc.gtbcs_cataclysmic_uis.boss_screen.BossScreenData;
import com.gametechbc.gtbcs_cataclysmic_uis.boss_screen.BossScreenData.RewardRarity;
import com.gametechbc.gtbcs_cataclysmic_uis.init.GCBUBossScreens;
import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
public class PrimeBossScreens {
    public static void registerAll() {
        GCBUBossScreens.register(buildIgnisPrimeScreen());
        GCBUBossScreens.register(buildMaledictusPrimeScreen());
    }

    private static BossScreenData buildIgnisPrimeScreen() {
        return BossScreenData.builder("ignis_prime")
                .colorTheme(PrimeColorThemes.IGNIS_PRIME)
                .bossName(Component.translatable("boss_screen.cataclysm_primed_soul.ignis_prime.name"))
                .bossTitle(Component.translatable("boss_screen.cataclysm_primed_soul.ignis_prime.title"))
                .lore(Component.translatable("boss_screen.cataclysm_primed_soul.ignis_prime.lore"))
                .lootTable(new ResourceLocation(Primed_Soul.MODID, "entities/ignis_prime"))
                .bossImage(new ResourceLocation(Primed_Soul.MODID, "textures/gui/bosses/ignis_prime.png"), 709, 543, 0.55F, -10, 16)
                .suggestions(new Component[]{
                        Component.translatable("boss_screen.cataclysm_primed_soul.ignis_prime.suggestion.1"),
                        Component.translatable("boss_screen.cataclysm_primed_soul.ignis_prime.suggestion.2"),
                        Component.translatable("boss_screen.cataclysm_primed_soul.ignis_prime.suggestion.3")
                })
                .addAdvancedInfo(
                        Component.translatable("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.erosion.title"),
                        Component.translatable("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.erosion.desc")
                )
                .addAdvancedInfo(
                        Component.translatable("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.guard.title"),
                        Component.translatable("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.guard.desc")
                )
                .addSpoilerAdvancedInfo(
                        Component.translatable("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.phase2.title"),
                        Component.translatable("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.phase2.desc")
                )
                .addSpoilerAdvancedInfo(
                        Component.translatable("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.ultracharge.title"),
                        Component.translatable("boss_screen.cataclysm_primed_soul.ignis_prime.advanced_info.ultracharge.desc")
                )
                .requiredItem(new ItemStack(ModItems.ABYSSAL_ASHES.get()), 1)
                .reward(new ItemStack(ModItems.LAVATEIN.get()), RewardRarity.LEGENDARY)
                .build();
    }

    private static BossScreenData buildMaledictusPrimeScreen() {
        return BossScreenData.builder("maledictus_prime")
                .colorTheme(PrimeColorThemes.MALEDICTUS_PRIME)
                .bossName(Component.translatable("boss_screen.cataclysm_primed_soul.maledictus_prime.name"))
                .bossTitle(Component.translatable("boss_screen.cataclysm_primed_soul.maledictus_prime.title"))
                .lore(Component.translatable("boss_screen.cataclysm_primed_soul.maledictus_prime.lore"))
                .lootTable(new ResourceLocation(Primed_Soul.MODID, "entities/maledictus_prime"))
                .bossImage(new ResourceLocation(Primed_Soul.MODID, "textures/gui/bosses/maledictus_prime.png"), 709, 543, 0.5F, -20, 8)
                .suggestions(new Component[]{
                        Component.translatable("boss_screen.cataclysm_primed_soul.maledictus_prime.suggestion.1"),
                        Component.translatable("boss_screen.cataclysm_primed_soul.maledictus_prime.suggestion.2"),
                        Component.translatable("boss_screen.cataclysm_primed_soul.maledictus_prime.suggestion.3")
                })
                .addAdvancedInfo(
                        Component.translatable("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.phantoms.title"),
                        Component.translatable("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.phantoms.desc")
                )
                .addAdvancedInfo(
                        Component.translatable("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.counter.title"),
                        Component.translatable("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.counter.desc")
                )
                .addSpoilerAdvancedInfo(
                        Component.translatable("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.grab.title"),
                        Component.translatable("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.grab.desc")
                )
                .addSpoilerAdvancedInfo(
                        Component.translatable("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.phase2.title"),
                        Component.translatable("boss_screen.cataclysm_primed_soul.maledictus_prime.advanced_info.phase2.desc")
                )
                .requiredItem(new ItemStack(ModItems.RUSTED_KNIGHT_SWORD.get()), 1)
                .build();
    }
}