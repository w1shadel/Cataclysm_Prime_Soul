package com.maxwell.cataclysm_primed_soul.init;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.item.LavateinItem;
import com.maxwell.cataclysm_primed_soul.item.ModGenericItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import java.util.List;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Primed_Soul.MODID);

    public static final RegistryObject<Item> ABYSSAL_ASHES = ITEMS.register("abyssal_ashes", () ->
            new ModGenericItem(
                    new Item.Properties().rarity(Rarity.EPIC).fireResistant(),
                    false,
                    List.of("tooltip.cataclysm_primed_soul.abyssal_ashes.desc")
            )
    );
    public static final RegistryObject<Item> LAVATEIN = ITEMS.register("lavatein", () ->
            new LavateinItem(Tiers.NETHERITE,8,-2,new Item.Properties().rarity(Rarity.EPIC).fireResistant())
    );
}