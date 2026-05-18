package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.goal;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.AI.InternalAttackGoal;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.Ignis_PrimeEntity;

public class IgnisStateGoal extends InternalAttackGoal {
    public final Ignis_PrimeEntity ignis;
    private final int nextState;

    public IgnisStateGoal(Ignis_PrimeEntity entity, int requiredState, int targetState, int nextState, int maxTick, int seeTick, float range) {
        super(entity, requiredState, targetState, 0, maxTick, seeTick, range);
        this.ignis = entity;
        this.nextState = nextState;
    }

    @Override
    public boolean canUse() {
        if (ignis.isPassenger()) return false;
        if (ignis.getAttackState() == this.attackstate) return true;
        return super.canUse();
    }

    @Override
    public void stop() {
        if (this.ignis.getAttackState() == this.attackstate) {
            this.ignis.setAttackState(nextState);
        }
        this.ignis.getNavigation().stop();
    }
}
