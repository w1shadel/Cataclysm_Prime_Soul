package com.maxwell.cataclysm_primed_soul.api.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface IShaderItem {

    ResourceLocation getDebuffShader(ItemStack stack);

    int getDebuffLevel(ItemStack stack);
}