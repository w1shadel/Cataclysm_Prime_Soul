package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.goal;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.AI.InternalAttackGoal;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.Ignis_PrimeEntity;
import net.minecraft.world.entity.LivingEntity;

public class IgnisDashUpperGoal extends InternalAttackGoal {
    public final Ignis_PrimeEntity ignis;

    public IgnisDashUpperGoal(Ignis_PrimeEntity entity) {
        super(entity, Ignis_PrimeEntity.STATE_DASH_UPPER, Ignis_PrimeEntity.STATE_DASH_UPPER, 0, 25, 0, 20.0F);
        this.ignis = entity;
    }

    @Override
    public boolean canUse() {
        if (ignis.isPassenger()) return false;
        return ignis.getAttackState() == this.attackstate;
    }

    @Override
    public boolean canContinueToUse() {
        return ignis.getAttackState() == this.attackstate && !ignis.isPassenger();
    }

    @Override
    public void stop() {
        if (ignis.getAttackState() == this.attackstate) {
            ignis.setAttackState(Ignis_PrimeEntity.STATE_JAB_1);
        }
        ignis.getNavigation().stop();
    }
}
