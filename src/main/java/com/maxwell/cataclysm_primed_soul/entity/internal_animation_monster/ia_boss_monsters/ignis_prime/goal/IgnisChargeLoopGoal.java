package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.goal;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.AI.InternalAttackGoal;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.Ignis_PrimeEntity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class IgnisChargeLoopGoal extends InternalAttackGoal {
    public IgnisChargeLoopGoal(Ignis_PrimeEntity entity) {
        super(entity, 10, 10, 0, 45, 0, 20.0F);
    }

    @Override
    public boolean canContinueToUse() {
        if (entity.isPassenger()) return false;
        return this.entity.getAttackState() == 10;
    }

    @Override
    public void tick() {
        super.tick();
        Ignis_PrimeEntity ignis = (Ignis_PrimeEntity) this.entity;
        LivingEntity target = ignis.getTarget();
        if (ignis.attackTicks > 5) {
            List<LivingEntity> list = ignis.level().getEntitiesOfClass(LivingEntity.class, ignis.getBoundingBox().inflate(1.5, 0.5, 1.5),
                    e -> e != ignis && ignis.canAttack(e));
            if (!list.isEmpty()) {
                ignis.performAreaDamage(1.0f, 0.5f, 1.5D, 0.5D, 1.5D, 0.2D);
                ignis.setAttackState(ignis.getRandom().nextBoolean() ? Ignis_PrimeEntity.STATE_UPPERCUT : Ignis_PrimeEntity.STATE_JAB_1);
                return;
            }
            if (ignis.horizontalCollision || ignis.attackTicks >= 20) {
                ignis.setAttackState(Ignis_PrimeEntity.STATE_CHARGE_SHOCKWAVE);
            }
        }
    }

    @Override
    public void stop() {
        if (this.entity.getAttackState() == 10) {
            this.entity.setAttackState(0);
        }
        this.entity.getNavigation().stop();
    }
}
