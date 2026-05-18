package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.goal;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.AI.InternalAttackGoal;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.Ignis_PrimeEntity;
import net.minecraft.world.entity.LivingEntity;

public class IgnisRockLoopGoal extends InternalAttackGoal {
    public IgnisRockLoopGoal(Ignis_PrimeEntity entity) {
        super(entity, 16, 16, 0, 52, 0, 26.0F);
    }

    @Override
    public void tick() {
        super.tick();
        Ignis_PrimeEntity ignis = (Ignis_PrimeEntity) this.entity;
        LivingEntity target = ignis.getTarget();
        if (target != null) {
            double distance = ignis.distanceTo(target);
            if (distance <= 9.5D || ignis.attackTicks >= 48) {
                ignis.setAttackState(Ignis_PrimeEntity.STATE_ROCK_END);
            }
        } else if (ignis.attackTicks >= 10) {
            ignis.setAttackState(Ignis_PrimeEntity.STATE_ROCK_END);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.entity.getAttackState() == 16 && !this.entity.isPassenger();
    }

    @Override
    public void stop() {
        if (this.entity.getAttackState() == Ignis_PrimeEntity.STATE_ROCK_LOOP) {
            this.entity.setAttackState(Ignis_PrimeEntity.STATE_ROCK_END);
        }
        this.entity.getNavigation().stop();
    }
}
