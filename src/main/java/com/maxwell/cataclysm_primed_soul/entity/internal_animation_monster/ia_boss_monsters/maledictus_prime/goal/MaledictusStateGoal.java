package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.goal;

import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.Maledictus_PrimeEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class MaledictusStateGoal extends Goal {
    protected final Maledictus_PrimeEntity maledictus;
    protected final int state;

    public MaledictusStateGoal(Maledictus_PrimeEntity entity, int attackState) {
        this.maledictus = entity;
        this.state = attackState;
        // 攻撃実行中は通常の歩行AI（InternalMoveGoal）や索敵AIを完全にブロックする
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return !this.maledictus.isPassenger() && this.maledictus.getAttackState() == this.state;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.maledictus.isPassenger() && this.maledictus.getAttackState() == this.state;
    }

    @Override
    public boolean isInterruptable() {
        return false; // 他の低優先度Goalによる割り込みを拒否
    }

    @Override
    public void start() {
        // ナビゲーションを停止するのみ（ステートをいじらない）
        this.maledictus.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.maledictus.getTarget();
        // チャージ突進・急降下中以外は、ターゲットへ滑らかに視線を向ける
        if (target != null && this.maledictus.getAttackState() != Maledictus_PrimeEntity.ATTACK_CHARGE) {
            double dx = target.getX() - this.maledictus.getX();
            double dz = target.getZ() - this.maledictus.getZ();
            float targetYaw = (float) (Mth.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
            float rotationSpeed = this.maledictus.isPhase2() ? 12.0F : 6.0F;
            float yawDifference = Mth.wrapDegrees(targetYaw - this.maledictus.getYRot());

            yawDifference = Mth.clamp(yawDifference, -rotationSpeed, rotationSpeed);

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