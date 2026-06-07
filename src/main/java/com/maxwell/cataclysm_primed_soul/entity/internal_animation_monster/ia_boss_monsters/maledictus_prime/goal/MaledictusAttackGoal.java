package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.goal;

import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.Maledictus_PrimeEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class MaledictusAttackGoal extends Goal {
    private final Maledictus_PrimeEntity maledictus;

    public MaledictusAttackGoal(Maledictus_PrimeEntity entity) {
        this.maledictus = entity;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.maledictus.getTarget();
        return target != null
                && target.isAlive()
                && !this.maledictus.isPassenger()
                && this.maledictus.isAttackReady()
                && this.maledictus.getAttackState() == 0
                && this.maledictus.hasLineOfSight(target);
    }

    @Override
    public void start() {
        LivingEntity target = this.maledictus.getTarget();
        if (target == null) {
            return;
        }

        this.maledictus.getNavigation().stop();
        this.maledictus.getLookControl().setLookAt(target, 60.0F, 60.0F);
        this.maledictus.lookAt(target, 60.0F, 60.0F);
        this.maledictus.setAttackState(this.chooseAttack(target));
    }

    private int chooseAttack(LivingEntity target) {
        double distance = this.maledictus.distanceTo(target);
        float roll = this.maledictus.getRandom().nextFloat();

        if (distance <= 3.5D) {
            if (roll < 0.16F) {
                return Maledictus_PrimeEntity.ATTACK_COUNTER_START;
            }
            if (roll < 0.34F) {
                return Maledictus_PrimeEntity.ATTACK_GRAB_START;
            }
            if (roll < 0.52F) {
                return Maledictus_PrimeEntity.ATTACK_EX_JAB_1;
            }
            return Maledictus_PrimeEntity.ATTACK_JAB_1;
        }

        if (distance <= 7.0D) {
            if (roll < 0.22F) {
                return Maledictus_PrimeEntity.ATTACK_SHOCKWAVE_START;
            }
            if (roll < 0.42F) {
                return Maledictus_PrimeEntity.ATTACK_PHANTOM_MACE_CRUSH;
            }
            if (roll < 0.62F) {
                return Maledictus_PrimeEntity.ATTACK_CHARGE;
            }
            return Maledictus_PrimeEntity.ATTACK_JAB_1;
        }

        if (distance <= 16.0D) {
            if (roll < 0.28F) {
                return Maledictus_PrimeEntity.ATTACK_CHARGE;
            }
            if (roll < 0.50F) {
                return Maledictus_PrimeEntity.ATTACK_FAR_START;
            }
            if (roll < 0.72F) {
                return Maledictus_PrimeEntity.ATTACK_PHANTOM_SPEAR_CHARGE;
            }
            return Maledictus_PrimeEntity.ATTACK_PHANTOM_BOW_SNIPE;
        }

        if (roll < 0.45F) {
            return Maledictus_PrimeEntity.ATTACK_FAR_START;
        }
        if (roll < 0.75F) {
            return Maledictus_PrimeEntity.ATTACK_PHANTOM_BOW_SNIPE;
        }
        return Maledictus_PrimeEntity.ATTACK_CHARGE;
    }
}
