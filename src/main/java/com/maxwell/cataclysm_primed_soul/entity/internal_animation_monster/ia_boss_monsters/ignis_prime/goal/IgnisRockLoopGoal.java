package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.goal;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.AI.InternalAttackGoal;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.Ignis_PrimeEntity;
import net.minecraft.world.entity.LivingEntity;

public class IgnisRockLoopGoal extends InternalAttackGoal {
    public IgnisRockLoopGoal(Ignis_PrimeEntity entity) {
        // maxTickをアニメーションの長さに合わせる
        super(entity, 16, 16, 0, 70, 0, 35.0F);
    }

    @Override
    public void tick() {
        super.tick();
        Ignis_PrimeEntity ignis = (Ignis_PrimeEntity) this.entity;
        LivingEntity target = ignis.getTarget();

        ignis.getNavigation().stop();
        if (target != null) {
            ignis.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }
        if (ignis.attackTicks == 15) ignis.performTectonicWave(0, target);
        if (ignis.attackTicks == 38) ignis.performTectonicWave(1, target);
        if (ignis.attackTicks == 62) ignis.performTectonicWave(2, target);
        if (ignis.attackTicks >= 66) {
            ignis.setAttackState(Ignis_PrimeEntity.STATE_ROCK_END);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.entity.getAttackState() == 16 && !this.entity.isPassenger();
    }

    @Override
    public void stop() {
        if (this.entity.getAttackState() == 16) {
            this.entity.setAttackState(0);
        }
        this.entity.getNavigation().stop();
    }
}