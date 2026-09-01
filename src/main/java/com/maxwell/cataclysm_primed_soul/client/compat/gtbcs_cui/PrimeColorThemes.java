package com.maxwell.cataclysm_primed_soul.client.compat.gtbcs_cui;

import com.gametechbc.gtbcs_cataclysmic_uis.utils.GUIColorThemeBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PrimeColorThemes {
    public static final GUIColorThemeBuilder IGNIS_PRIME = (new GUIColorThemeBuilder.ColorSchemeBuilder())
            .bgLight(0xFF1E2838)
            .bgBase(0xFF141C28)
            .bgDark(0xFF0C121C)
            .borderDark(0xFF0E3854)
            .borderBase(0xFF1A608C)
            .borderAccent(0xFF38B2E6)
            .borderHighlight(0xFFE0F8FF)
            .titleColor(0xFFE8FCFF)
            .subtitleColor(0xFF75D1FF)
            .textColor(0xFFD6F0FA)
            .firstWordColor(0xFF38B2E6)
            .particleColor(0x38B2E6)
            .build();
    public static final GUIColorThemeBuilder MALEDICTUS_PRIME = (new GUIColorThemeBuilder.ColorSchemeBuilder())
            .bgLight(0xFF1B2E2E)
            .bgBase(0xFF112121)
            .bgDark(0xFF0A1515)
            .borderDark(0xFF0F3B3B)
            .borderBase(0xFF1D6666)
            .borderAccent(0xFF3CE0CC)
            .borderHighlight(0xFFCCFFF9)
            .titleColor(0xFFE0FFF8)
            .subtitleColor(0xFF5CEAD9)
            .textColor(0xFFC8F5EE)
            .firstWordColor(0xFF3CE0CC)
            .particleColor(0x3CE0CC)
            .build();
}