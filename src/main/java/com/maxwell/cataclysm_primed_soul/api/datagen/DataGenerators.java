package com.maxwell.cataclysm_primed_soul.api.datagen;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.api.datagen.provider.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Primed_Soul.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<net.minecraft.core.HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new net.minecraftforge.common.data.ForgeAdvancementProvider(
                output,
                lookupProvider,
                existingFileHelper,
                java.util.List.of(new ModAdvancementsProvider())
        ));

        ModEntityTypeTagsProvider entityTypeTags = new ModEntityTypeTagsProvider(output, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), entityTypeTags);
        generator.addProvider(event.includeServer(), new ModRecipeProvider(output));

        generator.addProvider(event.includeClient(), new ModItemModelProvider(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModBlockStateProvider(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModLanguageProvider.English(output));
        generator.addProvider(event.includeClient(), new ModLanguageProvider.Japanese(output));
    }
}