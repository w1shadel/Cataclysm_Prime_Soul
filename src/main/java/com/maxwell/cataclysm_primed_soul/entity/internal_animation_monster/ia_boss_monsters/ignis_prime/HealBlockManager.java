package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = "cataclysm_primed_soul")
public class HealBlockManager {
    private static final Map<UUID, Integer> blockedEntities = new ConcurrentHashMap<>();

    public static void applyHealBlock(LivingEntity entity, int durationTicks) {
        if (entity == null || entity.level().isClientSide()) return;
        blockedEntities.put(entity.getUUID(), durationTicks);
        entity.addEffect(new MobEffectInstance(MobEffects.WITHER, durationTicks, 1, false, true, true));
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity != null && !entity.level().isClientSide()) {
            if (blockedEntities.containsKey(entity.getUUID())) {
                event.setAmount(0.0F);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity != null && !entity.level().isClientSide()) {
            UUID uuid = entity.getUUID();
            if (blockedEntities.containsKey(uuid)) {
                int ticks = blockedEntities.get(uuid);
                if (ticks <= 1) {
                    blockedEntities.remove(uuid);
                } else {
                    blockedEntities.put(uuid, ticks - 1);
                }
            }
        }
    }
}
