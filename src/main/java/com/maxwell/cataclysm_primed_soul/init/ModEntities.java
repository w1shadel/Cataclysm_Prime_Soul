package com.maxwell.cataclysm_primed_soul.init;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.entity.InternalAnimationMonster.IABossMonsters.Ignis_PrimeEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Primed_Soul.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Primed_Soul.MODID);
    public static final RegistryObject<EntityType<Ignis_PrimeEntity>> IGNIS_PRIME = ENTITY_TYPES.register("ignis_prime",
            () -> EntityType.Builder.of(Ignis_PrimeEntity::new, MobCategory.MONSTER)
                    .sized(2.25F, 3.5F)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true)
                    .build("ignis_prime"));

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(IGNIS_PRIME.get(), Ignis_PrimeEntity.createAttributes().build());
    }
}