package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.goal;

import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.MaledictusPhantomEntity;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.Maledictus_PrimeEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class MaledictusAttackGoal extends Goal {
    private static final double MELEE_DECISION_RANGE = 3.5D;
    private static final double FLASH_STEP_RANGE = 16.0D;
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
                && this.maledictus.getAttackState() == 0
                && this.maledictus.hasLineOfSight(target)
                && this.hasAnyAttackReady(target);
    }

    private boolean hasAnyAttackReady(LivingEntity target) {
        double distance = this.maledictus.distanceTo(target);
        double heightDiff = Math.abs(this.maledictus.getY() - target.getY());
        if (this.maledictus.isBackstepRecoveryActive()) {
            return this.maledictus.isPhantomReady();
        }
        if (heightDiff > 2.0D && !target.onGround()) {
            return this.maledictus.isPhantomReady()
                    || this.maledictus.isShockwaveReady()
                    || this.maledictus.isBackstepReady();
        }
        if (distance <= MELEE_DECISION_RANGE) {
            return this.maledictus.isExJabReady() // ★ EX JAB のチェックを追加
                    || this.maledictus.isJabReady()
                    || this.maledictus.isCounterReady()
                    || this.maledictus.isGrabReady();
        }
        if (distance <= FLASH_STEP_RANGE) {
            return this.maledictus.isExJabReady() // ★ EX JAB のチェックを追加
                    || this.maledictus.isJabReady()
                    || this.maledictus.isChargeReady()
                    || this.maledictus.isShockwaveReady()
                    || this.maledictus.isPhantomReady();
        }
        return this.maledictus.isChargeReady()
                || this.maledictus.isShockwaveReady()
                || this.maledictus.isPhantomReady();
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
        double distance = this.maledictus.distanceTo(target);

        if (distance > MELEE_DECISION_RANGE && distance <= FLASH_STEP_RANGE) {
            if (this.maledictus.isPhase2() && this.maledictus.isExJabReady()) {
                this.maledictus.startFlashStep(target, Maledictus_PrimeEntity.ATTACK_EX_JAB_1);
                return;
            } else if (this.maledictus.isJabReady()) {
                this.maledictus.startFlashStep(target, Maledictus_PrimeEntity.ATTACK_JAB_1);
                return;
            }
        }

        int chosen = this.chooseAttack(target);
        if (chosen == -1) {
            this.spawnPhantom(target);
        } else {
            this.maledictus.setAttackState(chosen);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    private int chooseAttack(LivingEntity target) {
        double distance = this.maledictus.distanceTo(target);
        float roll = this.maledictus.getRandom().nextFloat();
        boolean isPhase2 = this.maledictus.isPhase2();
        double heightDiff = Math.abs(this.maledictus.getY() - target.getY());
        boolean targetIsAirborne = heightDiff > 2.0D && !target.onGround();

        if (targetIsAirborne && this.maledictus.isPhantomReady()) {
            return -1;
        }
        if (targetIsAirborne) {
            if (this.maledictus.isShockwaveReady()) {
                return Maledictus_PrimeEntity.ATTACK_SHOCKWAVE_START;
            }
            if (this.maledictus.isBackstepReady()) {
                return Maledictus_PrimeEntity.BACKSTEP;
            }
            return 0;
        }

        if (distance <= MELEE_DECISION_RANGE) {
            if (target.isUsingItem() && target.getUseItem().getItem() instanceof net.minecraft.world.item.ShieldItem
                    && this.maledictus.isGrabReady()) {
                return Maledictus_PrimeEntity.ATTACK_GRAB_START;
            }
            // ★ 至近距離：Phase 2なら EX JAB 1 を最優先で選択
            if (isPhase2 && this.maledictus.isExJabReady()) {
                return Maledictus_PrimeEntity.ATTACK_EX_JAB_1;
            }
            if (this.maledictus.isJabReady()) {
                return Maledictus_PrimeEntity.ATTACK_JAB_1;
            }
            if (this.maledictus.isGrabReady() && (roll < 0.35F || !this.maledictus.isChargeReady())) {
                return Maledictus_PrimeEntity.ATTACK_GRAB_START;
            }
            if (this.maledictus.isChargeReady()) {
                return Maledictus_PrimeEntity.ATTACK_CHARGE;
            }
            if (this.maledictus.isShockwaveReady()) {
                return Maledictus_PrimeEntity.ATTACK_SHOCKWAVE_START;
            }
            return this.maledictus.isPhantomReady() ? -1 : 0;
        }

        if (this.maledictus.isBackstepRecoveryActive()) {
            return this.maledictus.isPhantomReady() ? -1 : 0;
        }
        if (this.maledictus.shouldChangeStrategyAfterMiss() && this.maledictus.isPhantomReady()) {
            return -1;
        }
        if (this.maledictus.shouldChangeStrategyAfterMiss()) {
            if (this.maledictus.isBackstepReady()) {
                return Maledictus_PrimeEntity.BACKSTEP;
            }
            if (this.maledictus.isChargeReady()) {
                return Maledictus_PrimeEntity.ATTACK_CHARGE;
            }
        }
        if (this.maledictus.isPhantomReady()) {
            float phantomThreshold = isPhase2 ? 0.75F : 0.45F;
            if (roll < phantomThreshold) {
                return -1;
            }
        }
        if (this.maledictus.isChargeReady()) {
            return Maledictus_PrimeEntity.ATTACK_CHARGE;
        }
        if (this.maledictus.isShockwaveReady()) {
            return Maledictus_PrimeEntity.ATTACK_SHOCKWAVE_START;
        }
        return 0;
    }

    private void spawnPhantom(LivingEntity target) {
        double distance = this.maledictus.distanceTo(target);
        boolean isPhase2 = this.maledictus.isPhase2();
        int phantomType = choosePhantomType(target, distance);
        MaledictusPhantomEntity phantom = com.maxwell.cataclysm_primed_soul.init.ModEntities.MALEDICTUS_PHANTOM.get().create(this.maledictus.level());
        if (phantom != null) {
            double px = this.maledictus.getX();
            double py = this.maledictus.getY();
            double pz = this.maledictus.getZ();
            float pYaw = this.maledictus.getYRot();
            if (distance > 7.0D) {
                double ox = (this.maledictus.getRandom().nextDouble() - 0.5D) * 4.0D;
                double oz = (this.maledictus.getRandom().nextDouble() - 0.5D) * 4.0D;
                px += ox;
                pz += oz;
            }
            phantom.moveTo(px, py, pz, pYaw, 0.0F);
            phantom.setPhantomType(phantomType);
            phantom.setTarget(target);
            phantom.setSummoner(this.maledictus);
            phantom.setSummonerYRot(pYaw);
            this.maledictus.level().addFreshEntity(phantom);
        }
        if (this.maledictus.isBackstepRecoveryActive()) {
            this.maledictus.setAttackState(0);
        } else if (phantomType == MaledictusPhantomEntity.TYPE_SPEAR) {
            this.maledictus.setAttackState(Maledictus_PrimeEntity.ATTACK_CHARGE);
        } else if (phantomType == MaledictusPhantomEntity.TYPE_MACE) {
            this.maledictus.setAttackState(Maledictus_PrimeEntity.BACKSTEP);
        } else if (phantomType == MaledictusPhantomEntity.TYPE_BOW) {
            double heightDiff = Math.abs(this.maledictus.getY() - target.getY());
            if (heightDiff > 2.0D) {
                this.maledictus.setAttackState(0);
            } else if (heightDiff <= 2.0D && this.maledictus.isChargeReady()) {
                this.maledictus.setAttackState(Maledictus_PrimeEntity.ATTACK_CHARGE);
            } else {
                this.maledictus.setAttackState(0);
            }
        }
        this.maledictus.resetAttackFailureStreak();
        float baseCooldownSeconds = 3.0F;
        this.maledictus.setPhantomCooldown(isPhase2 ? baseCooldownSeconds / 3.0F : baseCooldownSeconds);
    }

    private int choosePhantomType(LivingEntity target, double distance) {
        boolean shielded = target.isUsingItem()
                && target.getUseItem().getItem() instanceof net.minecraft.world.item.ShieldItem;
        if (shielded) {
            return MaledictusPhantomEntity.TYPE_MACE;
        }
        if (Math.abs(this.maledictus.getY() - target.getY()) > 2.0D) {
            return MaledictusPhantomEntity.TYPE_BOW;
        }
        float roll = this.maledictus.getRandom().nextFloat();
        if (distance <= 7.0D) {
            if (roll < 0.55F) return MaledictusPhantomEntity.TYPE_MACE;
            if (roll < 0.85F) return MaledictusPhantomEntity.TYPE_BOW;
            return MaledictusPhantomEntity.TYPE_SPEAR;
        }
        if (distance <= 16.0D) {
            if (roll < 0.45F) return MaledictusPhantomEntity.TYPE_BOW;
            if (roll < 0.78F) return MaledictusPhantomEntity.TYPE_MACE;
            return MaledictusPhantomEntity.TYPE_SPEAR;
        }
        if (roll < 0.60F) return MaledictusPhantomEntity.TYPE_BOW;
        if (roll < 0.88F) return MaledictusPhantomEntity.TYPE_MACE;
        return MaledictusPhantomEntity.TYPE_SPEAR;
    }
}