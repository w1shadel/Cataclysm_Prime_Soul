package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.goal;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.AI.InternalAttackGoal;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.Maledictus_PrimeEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class MaledictusStateGoal extends InternalAttackGoal {
    protected final Maledictus_PrimeEntity maledictus;

    public MaledictusStateGoal(Maledictus_PrimeEntity entity, int attackState) {
        super(entity, attackState, attackState, 0, 200, 0, 64.0F);
        this.maledictus = entity;
    }

    @Override
    public boolean canUse() {
        return !this.maledictus.isPassenger() && this.maledictus.getAttackState() == this.attackstate;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity target = this.maledictus.getTarget();
        if (target != null && this.maledictus.getAttackState() != Maledictus_PrimeEntity.ATTACK_CHARGE) {
            double dx = target.getX() - this.maledictus.getX();
            double dz = target.getZ() - this.maledictus.getZ();
            float targetYaw = (float) (Mth.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
            float rotationSpeed = this.maledictus.isPhase2() ? 12.0F : 6.0F;
            float yawDifference = Mth.wrapDegrees(targetYaw - this.maledictus.getYRot());
            if (yawDifference > rotationSpeed) {
                yawDifference = rotationSpeed;
            } else if (yawDifference < -rotationSpeed) {
                yawDifference = -rotationSpeed;
            }
            float newYaw = this.maledictus.getYRot() + yawDifference;
            this.maledictus.setYRot(newYaw);
            this.maledictus.yBodyRot = newYaw;
            this.maledictus.yHeadRot = newYaw;
            this.maledictus.getLookControl().setLookAt(target, 60.0F, 60.0F);
        }
    }

    @Override
    public void stop() {
        this.maledictus.getNavigation().stop();
    }
}