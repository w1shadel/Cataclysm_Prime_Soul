package com.maxwell.cataclysm_primed_soul.transformer;

import com.maxwell.cataclysm_primed_soul.mixin.accessor.LivingEntityAccessor;
import com.maxwell.cataclysm_primed_soul.util.DecayUnsafeHelper;
import com.maxwell.cataclysm_primed_soul.util.IDecayEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Field;

public class DecaySynchedEntityDataMethods {
    private static volatile Field entityField = null;
    private static volatile boolean initialized = false;

    private static void initField(Class<?> clazz) {
        if (initialized) return;
        synchronized (DecaySynchedEntityDataMethods.class) {
            if (initialized) return;
            try {
                entityField = clazz.getDeclaredField("entity");
                DecayUnsafeHelper.forceSetAccessible(entityField);
            } catch (Throwable t) {
                try {
                    entityField = clazz.getDeclaredField("f_135344_");
                    DecayUnsafeHelper.forceSetAccessible(entityField);
                } catch (Throwable ignored) {}
            }
            initialized = true;
        }
    }

    public static boolean handleForceSet(SynchedEntityData data, EntityDataAccessor<?> accessor, Object value) {
        if (com.maxwell.cataclysm_primed_soul.util.DecayDamageUtil.BYPASS_DECAY.get()) {
            com.maxwell.cataclysm_primed_soul.util.DecayDamageUtil.forceSetHealthVanillaRawDirect(data, accessor, value);
            return true;
        }
        return false;
    }

    public static Object onSet(SynchedEntityData data, EntityDataAccessor<?> accessor, Object value) {
        if (!initialized) {
            initField(data.getClass());
        }
        if (entityField == null) return value;

        try {
            Entity entity = (Entity) entityField.get(data);
            if (entity == null || entity.level().isClientSide()) {
                return value;
            }

            if (entity instanceof IDecayEntity decayEntity && entity instanceof LivingEntity living) {
                if (decayEntity.isSuperInvincible()) {

                    if (accessor.equals(LivingEntityAccessor.getDataHealthId())) {
                        if (value instanceof Float) {
                            return Float.valueOf(living.getMaxHealth()); 
                        }
                    }



                    else if (value instanceof Float) {
                        return Float.valueOf(0.0f);
                    }
                }
            }
        } catch (Throwable ignored) {}

        return value;
    }
}