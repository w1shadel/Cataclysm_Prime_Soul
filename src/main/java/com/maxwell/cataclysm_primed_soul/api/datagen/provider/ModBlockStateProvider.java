package com.maxwell.cataclysm_primed_soul.api.datagen.provider;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Primed_Soul.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
    }
}