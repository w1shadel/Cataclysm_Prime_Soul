package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.api.config.IgnisPrimeConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = Primed_Soul.MODID)
public class IgnisDebuffManager {
    private static final UUID DEBUFF_ARMOR_UUID = UUID.fromString("f4d7b7e0-1234-4a5b-6c7d-8e9f01234567");
    private static final UUID DEBUFF_TOUGHNESS_UUID = UUID.fromString("f4d7b7e0-5678-4a5b-6c7d-8e9f01234567");
    private static final Map<UUID, Integer> activeDebuffs = new ConcurrentHashMap<>();
    private static final Map<UUID, Float> rawDamages = new ConcurrentHashMap<>();
    private static final Set<Ignis_PrimeEntity> activeBosses = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final Set<UUID> phase3Bosses = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void registerBoss(Ignis_PrimeEntity boss) {
        activeBosses.add(boss);
    }

    public static void unregisterBoss(Ignis_PrimeEntity boss) {
        activeBosses.remove(boss);
        phase3Bosses.remove(boss.getUUID());
        if (activeBosses.isEmpty()) {
            clearAllDebuffs();
        }
    }

    private static void clearAllDebuffs() {
        net.minecraft.server.MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            for (UUID uuid : new ArrayList<>(activeDebuffs.keySet())) {
                net.minecraft.world.entity.player.Player player = server.getPlayerList().getPlayer(uuid);
                removeDebuff(player, uuid);
            }
        }
        activeDebuffs.clear();
    }

    public static void tickBossDebuffs(Ignis_PrimeEntity boss) {
        if (boss == null || boss.level().isClientSide() || !boss.isAlive()) return;
        int currentLevel = 1;
        float hpPct = boss.getHealth() / boss.getMaxHealth();
        if (phase3Bosses.contains(boss.getUUID())) {
            currentLevel = 3;
        } else if (boss.getAttackState() == Ignis_PrimeEntity.STATE_PHASE_CHANGE && boss.phaseChangeTicks >= 70) {
            currentLevel = 3;
            phase3Bosses.add(boss.getUUID());
        } else if (hpPct <= 0.5F) {
            currentLevel = 2;
        }
        double range = IgnisPrimeConfig.DEBUFF_RANGE.get();
        AABB area = boss.getBoundingBox().inflate(range);
        List<LivingEntity> targets = boss.level().getEntitiesOfClass(LivingEntity.class, area);
        Set<UUID> targetsInTick = new HashSet<>();
        for (LivingEntity target : targets) {
            if (target == boss || !target.isAlive()) continue;
            UUID uuid = target.getUUID();
            targetsInTick.add(uuid);
            applyOrUpdateDebuff(target, currentLevel);
        }
        for (UUID uuid : activeDebuffs.keySet()) {
            if (!targetsInTick.contains(uuid)) {
                boolean inOtherBossRange = false;
                for (Ignis_PrimeEntity otherBoss : activeBosses) {
                    if (otherBoss != boss && otherBoss.isAlive() && otherBoss.level() == boss.level() && otherBoss.distanceToSqr(boss.getX(), boss.getY(), boss.getZ()) <= 50 * 50) {
                        inOtherBossRange = true;
                        break;
                    }
                }
                if (!inOtherBossRange) {
                    LivingEntity entity = null;
                    Player p = boss.level().getPlayerByUUID(uuid);
                    if (p != null) {
                        entity = p;
                    } else {
                    }
                    removeDebuff(entity, uuid);
                }
            }
        }
    }

    private static void applyOrUpdateDebuff(LivingEntity entity, int level) {
        UUID uuid = entity.getUUID();
        Integer existingLevel = activeDebuffs.get(uuid);
        if (existingLevel == null || existingLevel != level) {
            activeDebuffs.put(uuid, level);
            updateAttributes(entity, level);
            if (entity instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToPlayer(
                        new com.maxwell.cataclysm_primed_soul.network.packet.MessageSyncIgnisDebuff(level), serverPlayer);
            }
        }
    }

    private static void removeDebuff(LivingEntity entity, UUID uuid) {
        activeDebuffs.remove(uuid);
        rawDamages.remove(uuid);
        if (entity != null) {
            removeAttributeModifiers(entity);
            if (entity instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToPlayer(
                        new com.maxwell.cataclysm_primed_soul.network.packet.MessageSyncIgnisDebuff(0), serverPlayer);
            }
        }
    }

    private static void updateAttributes(LivingEntity entity, int level) {
        removeAttributeModifiers(entity);
        double armorMod = 0;
        double toughnessMod = 0;
        if (level == 1) {
            armorMod = IgnisPrimeConfig.DEBUFF_ARMOR_LEVEL_1.get();
        } else if (level == 2) {
            armorMod = IgnisPrimeConfig.DEBUFF_ARMOR_LEVEL_2.get();
            toughnessMod = IgnisPrimeConfig.DEBUFF_TOUGHNESS_LEVEL_2.get();
        } else if (level == 3) {
            armorMod = IgnisPrimeConfig.DEBUFF_ARMOR_LEVEL_3.get();
            toughnessMod = IgnisPrimeConfig.DEBUFF_TOUGHNESS_LEVEL_3.get();
        }
        AttributeInstance armorAttr = entity.getAttribute(Attributes.ARMOR);
        if (armorAttr != null && armorMod != 0) {
            armorAttr.addTransientModifier(new AttributeModifier(DEBUFF_ARMOR_UUID, "Ignis Armor Debuff", armorMod, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        AttributeInstance toughnessAttr = entity.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (toughnessAttr != null && toughnessMod != 0) {
            toughnessAttr.addTransientModifier(new AttributeModifier(DEBUFF_TOUGHNESS_UUID, "Ignis Toughness Debuff", toughnessMod, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

    private static void removeAttributeModifiers(LivingEntity entity) {
        AttributeInstance armorAttr = entity.getAttribute(Attributes.ARMOR);
        if (armorAttr != null) {
            armorAttr.removeModifier(DEBUFF_ARMOR_UUID);
        }
        AttributeInstance toughnessAttr = entity.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (toughnessAttr != null) {
            toughnessAttr.removeModifier(DEBUFF_TOUGHNESS_UUID);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide()) return;
        UUID uuid = entity.getUUID();
        if (activeDebuffs.containsKey(uuid) && activeDebuffs.get(uuid) == 3) {
            rawDamages.put(uuid, event.getAmount());
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide()) return;
        UUID uuid = entity.getUUID();
        if (activeDebuffs.containsKey(uuid) && activeDebuffs.get(uuid) == 3) {
            Float rawDamage = rawDamages.remove(uuid);
            if (rawDamage != null) {
                float armor = entity.getArmorValue();
                float toughness = (float) entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
                float finalDamage = net.minecraft.world.damagesource.CombatRules.getDamageAfterAbsorb(rawDamage, armor, toughness);
                event.setAmount(finalDamage);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(net.minecraftforge.event.entity.living.LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity != null && !entity.level().isClientSide()) {
            UUID uuid = entity.getUUID();
            if (activeDebuffs.containsKey(uuid)) {
                removeDebuff(entity, uuid);
            }
        }
    }
}
