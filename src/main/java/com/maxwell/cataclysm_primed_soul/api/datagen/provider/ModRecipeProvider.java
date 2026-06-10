package com.maxwell.cataclysm_primed_soul.api.datagen.provider;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.api.datagen.recipes.AmethystBlessRecipeBuilder;
import com.maxwell.cataclysm_primed_soul.init.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

@SuppressWarnings("removal")
public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        AmethystBlessRecipeBuilder.bless(com.github.L_Ender.cataclysm.init.ModItems.BURNING_ASHES.get(), ModItems.ABYSSAL_ASHES.get(), 120)
                .save(consumer, new ResourceLocation(Primed_Soul.MODID, "abyssal_ashes_from_bless"));

    }
}