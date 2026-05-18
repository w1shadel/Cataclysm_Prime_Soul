package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.goal;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.AI.InternalAttackGoal;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.Ignis_PrimeEntity;

public class IgnisJabGoal extends InternalAttackGoal {
    public final Ignis_PrimeEntity ignis;
    private final int nextState;

    public IgnisJabGoal(Ignis_PrimeEntity entity, int startState, int currentState, int nextState, int maxTick, int seeTick, float range) {
        super(entity, startState, currentState, 0, maxTick, seeTick, range);
        this.ignis = entity;
        this.nextState = nextState;
    }

    @Override
    public boolean canUse() {
        int current = ignis.getAttackState();
        if (ignis.isPassenger()) return false;
        if (current == this.attackstate) return true;
        if (current != this.getattackstate) return false;
        if (ignis.getTarget() == null) return false;
        return super.canUse() && ignis.isJabReady() && ignis.getRandom().nextFloat() < 0.5f;
    }

    @Override
    public void stop() {
        if (ignis.getAttackState() == this.attackstate) {
            if (nextState != 0) {
                ignis.setAttackState(nextState);
            } else {
                ignis.setAttackState(0);
                ignis.setJabCooldown(60);
            }
        }
        ignis.getNavigation().stop();
    }

    @Override
    public void tick() {
        super.tick();
        if (ignis.attackTicks == 5) {
            float yaw = ignis.getYRot() * ((float) Math.PI / 180F);
            ignis.setDeltaMovement(ignis.getDeltaMovement().add(-Math.sin(yaw) * 0.2, 0, Math.cos(yaw) * 0.2));
        }
    }
}
