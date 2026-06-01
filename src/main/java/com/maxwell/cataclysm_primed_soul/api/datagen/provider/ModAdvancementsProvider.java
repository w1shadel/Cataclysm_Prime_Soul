package com.maxwell.cataclysm_primed_soul.api.datagen.provider;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.init.ModEntities;
import com.maxwell.cataclysm_primed_soul.init.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.function.Consumer;
@SuppressWarnings("removal")
public class ModAdvancementsProvider implements ForgeAdvancementProvider.AdvancementGenerator {

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> writer, ExistingFileHelper existingFileHelper) {
        Advancement rootAdvancement = Advancement.Builder.advancement()
                .display(
                        new ItemStack(ModItems.ABYSSAL_ASHES.get()),
                        Component.translatable("advancement.cataclysm_primed_soul.root.title"),
                        Component.translatable("advancement.cataclysm_primed_soul.root.desc"),
                        new ResourceLocation("minecraft:textures/block/netherrack.png"),
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_abyssal_ashes", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.ABYSSAL_ASHES.get()))
                .save(writer, new ResourceLocation(Primed_Soul.MODID, "root"), existingFileHelper);

        Advancement defeatPrime = Advancement.Builder.advancement()
                .parent(rootAdvancement)
                .display(
                        new ItemStack(ModItems.ABYSSAL_ASHES.get()),
                        Component.translatable("advancement.cataclysm_primed_soul.defeat_prime.title"),
                        Component.translatable("advancement.cataclysm_primed_soul.defeat_prime.desc"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion("killed_prime", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(ModEntities.IGNIS_PRIME.get()))))
                .save(writer, new ResourceLocation(Primed_Soul.MODID, "defeat_prime"), existingFileHelper);
    }
}