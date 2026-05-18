package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.goal;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.AI.InternalAttackGoal;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.Ignis_PrimeEntity;
import net.minecraft.world.entity.LivingEntity;

public class IgnisUppercutGoal extends InternalAttackGoal {
    public final Ignis_PrimeEntity ignis;

    public IgnisUppercutGoal(Ignis_PrimeEntity entity, float range) {
        super(entity, 0, 2, 0, 40, 20, range);
        this.ignis = entity;
    }

    @Override
    public boolean canUse() {
        int current = ignis.getAttackState();
        if (ignis.isPassenger()) return false;
        if (current == this.attackstate) return true;
        if (current != this.getattackstate) return false;
        if (ignis.getTarget() == null) return false;
        return super.canUse() && ignis.isUppercutReady() && ignis.getRandom().nextFloat() < 0.5f;
    }

    @Override
    public void stop() {
        if (ignis.getAttackState() == this.attackstate) {
            LivingEntity target = ignis.getTarget();
            if (ignis.wasUppercutHit() && target != null && target.isAlive()) {
                int nextCombo = ignis.getRandom().nextBoolean() ? Ignis_PrimeEntity.STATE_UPPERCUT_HORIZONTAL : Ignis_PrimeEntity.STATE_UPPERCUT_VERTICAL;
                ignis.setAttackState(nextCombo);
            } else {
                ignis.setAttackState(0);
                ignis.setUppercutCooldown(100);
            }
        }
        ignis.getNavigation().stop();
    }

    @Override
    public void tick() {
        super.tick();
        if (ignis.attackTicks == 15) {
            ignis.push(ignis.getLookAngle().x * 0.5, 0, ignis.getLookAngle().z * 0.5);
        }
    }
}
