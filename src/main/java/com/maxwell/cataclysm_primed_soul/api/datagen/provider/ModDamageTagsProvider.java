package com.maxwell.cataclysm_primed_soul.api.datagen.provider;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.init.ModDamageTypes;
import com.maxwell.cataclysm_primed_soul.transformer.DecayEntityMethods;
import com.maxwell.cataclysm_primed_soul.util.DecayDamageUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModDamageTagsProvider extends DamageTypeTagsProvider {
    public ModDamageTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Primed_Soul.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(DecayDamageUtil.ULTRA_BYPASS_DAMAGE)
                .add(ModDamageTypes.EROSION)
                .add(ModDamageTypes.TRUE_VOID);
    }
}