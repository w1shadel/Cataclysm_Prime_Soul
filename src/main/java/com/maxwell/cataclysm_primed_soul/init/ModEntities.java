package com.maxwell.cataclysm_primed_soul.init;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.Ignis_PrimeEntity;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.sub.Prime_Fireball_Entity;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.sub.Prime_Flame_Strike_Entity;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.Maledictus_PrimeEntity;
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
    public static final RegistryObject<EntityType<Maledictus_PrimeEntity>> MALEDICTUS_PRIME = ENTITY_TYPES.register("maledictus_prime",
            () -> EntityType.Builder.of(Maledictus_PrimeEntity::new, MobCategory.MONSTER)
                    .sized(2.25F, 3.5F)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true)
                    .build("maledictus_prime"));
    public static final RegistryObject<EntityType<Prime_Flame_Strike_Entity>> PRIME_FLAME_STRIKE = ENTITY_TYPES.register("prime_flame_strike",
            () -> EntityType.Builder.<Prime_Flame_Strike_Entity>of(Prime_Flame_Strike_Entity::new, MobCategory.MISC)
                    .sized(6.0F, 0.5F)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE)
                    .build("prime_flame_strike"));
    public static final RegistryObject<EntityType<Prime_Fireball_Entity>> PRIME_FIREBALL = ENTITY_TYPES.register("prime_fireball",
            () -> EntityType.Builder.<Prime_Fireball_Entity>of(Prime_Fireball_Entity::new, MobCategory.MISC)
                    .sized(0.6F, 0.6F)
                    .setUpdateInterval(1)
                    .setTrackingRange(20)
                    .setShouldReceiveVelocityUpdates(true)
                    .build("prime_fireball"));

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(IGNIS_PRIME.get(), Ignis_PrimeEntity.createAttributes().build());
        event.put(MALEDICTUS_PRIME.get(), Maledictus_PrimeEntity.createAttributes().build());
    }
}
