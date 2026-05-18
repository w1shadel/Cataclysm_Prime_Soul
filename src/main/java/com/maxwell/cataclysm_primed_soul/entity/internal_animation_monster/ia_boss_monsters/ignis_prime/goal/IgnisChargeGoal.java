package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.goal;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.AI.InternalAttackGoal;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.Ignis_PrimeEntity;
import net.minecraft.world.entity.LivingEntity;

public class IgnisChargeGoal extends InternalAttackGoal {
    public final Ignis_PrimeEntity ignis;

    public IgnisChargeGoal(Ignis_PrimeEntity entity, float range) {
        super(entity, 0, 1, 0, 23, 20, range);
        this.ignis = entity;
    }

    @Override
    public boolean canUse() {
        if (ignis.isPassenger()) return false;
        int current = ignis.getAttackState();
        if (ignis.rockProjectileHit && current == 1) return true;
        if (current == 1) return true;
        if (current != 0 || !ignis.isChargeReady()) return false;
        LivingEntity target = ignis.getTarget();
        if (target == null) return false;
        double distance = ignis.distanceTo(target);
        return distance > 6.0F && ignis.getRandom().nextFloat() < (distance > 10.0F ? 0.9f : 0.4f);
    }

    @Override
    public void stop() {
        if (ignis.getAttackState() == Ignis_PrimeEntity.STATE_CHARGE_START) {
            ignis.setAttackState(Ignis_PrimeEntity.STATE_CHARGE_LOOP);
        }
        ignis.getNavigation().stop();
    }

    @Override
    public void tick() {
        super.tick();
        if (ignis.getAttackState() == Ignis_PrimeEntity.STATE_CHARGE_START) {
            LivingEntity target = ignis.getTarget();
            if (target != null) {
                ignis.getLookControl().setLookAt(target, 60.0F, 60.0F);
                ignis.lookAt(target, 60.0F, 60.0F);
            }
        }
        if (ignis.getAttackState() == Ignis_PrimeEntity.STATE_CHARGE_START && ignis.attackTicks >= 23) {
            ignis.setAttackState(Ignis_PrimeEntity.STATE_CHARGE_LOOP);
        }
    }
}
