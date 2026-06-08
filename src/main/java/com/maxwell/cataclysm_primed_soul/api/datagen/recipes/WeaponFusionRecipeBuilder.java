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
public class WeaponFusionRecipeBuilder {
    private final Item base;
    private final Item addition;
    private final Item result;

    public WeaponFusionRecipeBuilder(Item base, Item addition, Item result) {
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    public static WeaponFusionRecipeBuilder fusion(Item base, Item addition, Item result) {
        return new WeaponFusionRecipeBuilder(base, addition, result);
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        consumer.accept(new FinishedRecipe() {
            @Override
            public void serializeRecipeData(JsonObject json) {
                JsonObject baseJson = new JsonObject();
                baseJson.addProperty("item", ForgeRegistries.ITEMS.getKey(base).toString());
                json.add("base", baseJson);

                JsonObject additionJson = new JsonObject();
                additionJson.addProperty("item", ForgeRegistries.ITEMS.getKey(addition).toString());
                json.add("addition", additionJson);

                JsonObject resultJson = new JsonObject();
                resultJson.addProperty("item", ForgeRegistries.ITEMS.getKey(result).toString());
                json.add("result", resultJson);
            }

            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public RecipeSerializer<?> getType() {
                return Objects.requireNonNull(ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation("cataclysm", "weapon_fusion")));
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