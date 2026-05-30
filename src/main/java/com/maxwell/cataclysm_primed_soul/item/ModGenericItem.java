package com.maxwell.cataclysm_primed_soul.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import java.util.List;

public class ModGenericItem extends Item {
    private final boolean foil;
    private final List<String> tooltipKeys;

    public ModGenericItem(Properties properties, boolean foil, List<String> tooltipKeys) {
        super(properties);
        this.foil = foil;
        this.tooltipKeys = tooltipKeys;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return this.foil || super.isFoil(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);
        if (this.tooltipKeys != null) {
            for (String key : this.tooltipKeys) {
                components.add(Component.translatable(key));
            }
        }
    }
}