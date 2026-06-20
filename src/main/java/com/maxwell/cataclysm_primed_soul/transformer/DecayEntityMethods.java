package com.maxwell.cataclysm_primed_soul.transformer;

import com.maxwell.cataclysm_primed_soul.util.DecayDamageUtil;
import com.maxwell.cataclysm_primed_soul.util.IDecayEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;

public class DecayEntityMethods {




    public static boolean shouldReplaceHealthMethod(Entity entity) {
        if (entity instanceof LivingEntity living && entity instanceof IDecayEntity decay) {
            return decay.isSuperInvincible() || decay.getDecayAmount() >= living.getMaxHealth();
        }
        return false;
    }

    public static float replaceGetHealth(LivingEntity livingEntity) {
        if (livingEntity instanceof IDecayEntity decay && decay.isSuperInvincible()) {
            return livingEntity.getMaxHealth();
        }
        return -Float.MAX_VALUE;
    }

    public static float getHealth(float health, LivingEntity livingEntity) {
        if (livingEntity instanceof IDecayEntity decay) {
            if (decay.isSuperInvincible()) {
                return livingEntity.getMaxHealth();
            }
            float decayAmount = decay.getDecayAmount();
            if (decayAmount >= livingEntity.getMaxHealth()) {
                return -Float.MAX_VALUE;
            }
        }
        return health;
    }

    public static boolean replaceIsDeadOrDying(Entity entity) {
        if (entity instanceof LivingEntity living && entity instanceof IDecayEntity decay) {
            if (decay.isSuperInvincible()) return false;
            return decay.getDecayAmount() >= living.getMaxHealth();
        }
        return false;
    }

    public static boolean isDeadOrDying(boolean deadOrDying, LivingEntity livingEntity) {
        if (livingEntity instanceof IDecayEntity decay) {
            if (decay.isSuperInvincible()) return false;
            if (decay.getDecayAmount() >= livingEntity.getMaxHealth()) {
                return true;
            }
        }
        return deadOrDying;
    }

    public static boolean replaceIsAlive(Entity entity) {
        return !replaceIsDeadOrDying(entity);
    }

    public static boolean isAlive(boolean alive, Entity entity) {
        if (entity instanceof LivingEntity living && entity instanceof IDecayEntity decay) {
            if (decay.isSuperInvincible()) return true;
            if (decay.getDecayAmount() >= living.getMaxHealth()) {
                return false;
            }
        }
        return alive;
    }

    public static Entity.RemovalReason getRemovalReason(Entity.RemovalReason removalReason, Entity entity) {
        if (entity instanceof IDecayEntity decay && decay.isSuperInvincible() && !decay.isRemoveBypass()) {
            return null;
        }
        return removalReason;
    }

    public static boolean isRemoved(boolean removed, Entity entity) {
        if (entity instanceof IDecayEntity decay && decay.isSuperInvincible() && !decay.isRemoveBypass()) {
            return false;
        }
        return removed;
    }

    public static boolean shouldReplaceIsPickable(Entity entity) {
        return entity instanceof IDecayEntity decay && decay.isSuperInvincible();
    }

    public static boolean replaceIsPickable(Entity entity) {
        return false;
    }

    public static boolean shouldOverrideTick(Entity entity) {
        return entity instanceof IDecayEntity decay && decay.isSuperInvincible();
    }

    public static void tickOverride(java.util.function.Consumer<Entity> consumer, Entity entity) {
        consumer.accept(entity);
    }

    public static void updateLastTicks(net.minecraft.server.level.ServerLevel serverLevel) {
    }

    public static boolean shouldPreventTeleport(Entity entity) {
        if (entity instanceof IDecayEntity decay && decay.isSuperInvincible()) {
            return isForcedByRivalMod();
        }
        return false;
    }

    public static boolean shouldPreventRespawn(Entity entity) {
        if (entity instanceof LivingEntity living && entity instanceof IDecayEntity decay) {
            if (decay.isSuperInvincible()) {
                boolean cspAllowedToDie = decay.getDecayAmount() >= living.getMaxHealth();
                return !cspAllowedToDie;
            }
        }
        return false;
    }

    public static boolean shouldPreventServerPlayerDie(Entity entity) {
        if (entity instanceof ServerPlayer player) {
            if (player.connection == null) {
                return true;
            }
            if (player instanceof IDecayEntity decay && decay.isSuperInvincible()) {
                return true;
            }
        }
        return false;
    }

    public static boolean handleForceDamage(LivingEntity target, net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (target instanceof IDecayEntity decay && decay.isSuperInvincible()) {
            return true;
        }
        if (DecayDamageUtil.shouldApplyBypass(source)) {
            if (target instanceof IDecayEntity decayTarget) {
                decayTarget.subtractTrueHP(amount);
                return true;
            }
        }
        return false;
    }

    public static boolean handleForceActuallyHurt(LivingEntity target, net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (target instanceof IDecayEntity decay && decay.isSuperInvincible()) {
            return true;
        }
        if (DecayDamageUtil.shouldApplyBypass(source)) {
            if (target instanceof IDecayEntity decayTarget) {
                decayTarget.subtractTrueHP(amount);
                return true;
            }
        }
        return false;
    }

    public static float handleSetHealth(LivingEntity entity, float health) {
        if (entity instanceof IDecayEntity decay && decay.isSuperInvincible()) {
            return entity.getMaxHealth();
        }
        return health;
    }

    public static boolean handleForceKill(Entity entity) {
        if (entity instanceof IDecayEntity decay && decay.isSuperInvincible()) {
            return true;
        }
        return false;
    }

    public static boolean handleForceDropLoot(LivingEntity entity) {
        if (entity instanceof IDecayEntity decay && decay.isSuperInvincible()) {
            return true;
        }
        return false;
    }

    private static boolean isForcedByRivalMod() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            String name = element.getClassName();
            if (name.startsWith("java.") || name.startsWith("javax.") ||
                    name.startsWith("sun.") || name.startsWith("com.sun.") ||
                    name.startsWith("jdk.") || name.startsWith("org.lwjgl.") ||
                    name.startsWith("cpw.mods.") || name.startsWith("net.minecraftforge.") ||
                    name.startsWith("net.minecraft.") || name.startsWith("org.spongepowered.") ||
                    name.contains("com.maxwell") || name.startsWith("com.mojang.") ||
                    name.startsWith("com.google.") || name.startsWith("org.apache.") ||
                    name.startsWith("io.netty.")) {
                continue;
            }
            return true;
        }
        return false;
    }
}