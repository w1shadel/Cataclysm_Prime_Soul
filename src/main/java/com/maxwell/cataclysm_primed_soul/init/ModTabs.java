package com.maxwell.cataclysm_primed_soul.init;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Primed_Soul.MODID);

    public static final RegistryObject<CreativeModeTab> PRIME_TAB = CREATIVE_TABS.register("prime_tab", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("creative_tab.cataclysm_primed_soul.prime_tab"))
                    .icon(() -> new ItemStack(ModItems.ABYSSAL_ASHES.isPresent() ? ModItems.ABYSSAL_ASHES.get() : Blocks.BARRIER.asItem()))
                    .build()
    );
}