package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.goal;

import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.Maledictus_PrimeEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class MaledictusBackstepGoal extends Goal {
    private final Maledictus_PrimeEntity maledictus;
    private int cooldown;
    private int backstepTicks;

    public MaledictusBackstepGoal(Maledictus_PrimeEntity entity) {
        this.maledictus = entity;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }

        LivingEntity target = this.maledictus.getTarget();
        if (target == null || !target.isAlive() || this.maledictus.isPassenger() || this.maledictus.getAttackState() != 0) {
            return false;
        }

        double distanceSq = this.maledictus.distanceToSqr(target);
        if (distanceSq <= 20.25D) { // 4.5m以内
            return this.maledictus.getRandom().nextFloat() < 0.25F;
        }

        return false;
    }

    @Override
    public void start() {
        this.backstepTicks = 0;
        this.cooldown = 120 + this.maledictus.getRandom().nextInt(80); // 6〜10秒のクールダウン

        LivingEntity target = this.maledictus.getTarget();
        if (target != null) {
            Vec3 diff = this.maledictus.position().subtract(target.position());
            Vec3 dir = new Vec3(diff.x, 0, diff.z).normalize();
            
            // 後方に大きく飛び退く力
            double speed = 1.35D;
            this.maledictus.setDeltaMovement(dir.x * speed, 0.42D, dir.z * speed);
            this.maledictus.hasImpulse = true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.backstepTicks < 15 && this.maledictus.getTarget() != null;
    }

    @Override
    public void tick() {
        this.backstepTicks++;
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
