package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.goal;

import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.Maledictus_PrimeEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class MaledictusBackstepGoal extends Goal {
    private final Maledictus_PrimeEntity maledictus;

    public MaledictusBackstepGoal(Maledictus_PrimeEntity entity) {
        this.maledictus = entity;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.maledictus.isPassenger() || this.maledictus.getAttackState() != 0) {
            return false;
        }

        if (!this.maledictus.isBackstepReady()) {
            return false;
        }

        LivingEntity target = this.maledictus.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        double distanceSq = this.maledictus.distanceToSqr(target);
        if (distanceSq <= 20.25D) {
            return this.maledictus.getRandom().nextFloat() < 0.25F;
        }

        return false;
    }

    @Override
    public void start() {
        this.maledictus.setAttackState(Maledictus_PrimeEntity.BACKSTEP);
    }

    @Override
    public boolean canContinueToUse() {
        return this.maledictus.getAttackState() == Maledictus_PrimeEntity.BACKSTEP && this.maledictus.getTarget() != null;
    }

    @Override
    public void tick() {
        LivingEntity target = this.maledictus.getTarget();
        if (target != null) {
            this.maledictus.getLookControl().setLookAt(target, 60.0F, 60.0F);
            this.maledictus.lookAt(target, 60.0F, 60.0F);
        }
    }

    @Override
    public void stop() {
        this.maledictus.setDeltaMovement(this.maledictus.getDeltaMovement().multiply(0.5D, 1.0D, 0.5D));
    }
}