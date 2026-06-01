package com.maxwell.cataclysm_primed_soul.api.datagen.provider;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.api.datagen.recipes.AmethystBlessRecipeBuilder;
import com.maxwell.cataclysm_primed_soul.init.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
            AmethystBlessRecipeBuilder.bless(com.github.L_Ender.cataclysm.init.ModItems.BURNING_ASHES.get(), ModItems.ABYSSAL_ASHES.get(), 120)
                    .save(consumer, new ResourceLocation(Primed_Soul.MODID, "abyssal_ashes_from_bless"));

//        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ABYSSAL_ASHES.get())
//                .pattern("AAA")
//                .pattern("ABA")
//                .pattern("AAA")
//                .define('A', Items.SOUL_SOIL)
//                .define('B', Items.BLAZE_POWDER)
//                .unlockedBy("has_soul_soil", has(Items.SOUL_SOIL))
//                .save(consumer, new ResourceLocation(Primed_Soul.MODID, "abyssal_ashes_shaped"));
//
//        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ABYSSAL_ASHES.get())
//                .requires(Items.SOUL_SAND)
//                .requires(Items.BLAZE_POWDER)
//                .unlockedBy("has_soul_sand", has(Items.SOUL_SAND))
//                .save(consumer, new ResourceLocation(Primed_Soul.MODID, "abyssal_ashes_shapeless"));
    }
}