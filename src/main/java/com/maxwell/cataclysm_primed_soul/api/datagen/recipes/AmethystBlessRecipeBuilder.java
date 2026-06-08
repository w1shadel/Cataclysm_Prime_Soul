package com.maxwell.cataclysm_primed_soul.api.datagen.recipes;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
@SuppressWarnings("removal")
public class AmethystBlessRecipeBuilder {
    private final Item ingredient;
    private final Item result;
    private final int time;

    public AmethystBlessRecipeBuilder(Item ingredient, Item result, int time) {
        this.ingredient = ingredient;
        this.result = result;
        this.time = time;
    }

    public static AmethystBlessRecipeBuilder bless(Item ingredient, Item result, int time) {
        return new AmethystBlessRecipeBuilder(ingredient, result, time);
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        consumer.accept(new FinishedRecipe() {
            @Override
            public void serializeRecipeData(JsonObject json) {
                JsonObject ingredientsJson = new JsonObject();
                ingredientsJson.addProperty("item", ForgeRegistries.ITEMS.getKey(ingredient).toString());
                json.add("ingredients", ingredientsJson);

                JsonObject resultJson = new JsonObject();
                resultJson.addProperty("item", ForgeRegistries.ITEMS.getKey(result).toString());
                json.add("result", resultJson);

                json.addProperty("time", time);
            }

            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public RecipeSerializer<?> getType() {
                return Objects.requireNonNull(ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation("cataclysm", "amethyst_bless")));
            }

            @Nullable
            @Override
            public JsonObject serializeAdvancement() {
                return null;
            }

            @Nullable
            @Override
            public ResourceLocation getAdvancementId() {
                return null;
            }
        });
    }
}