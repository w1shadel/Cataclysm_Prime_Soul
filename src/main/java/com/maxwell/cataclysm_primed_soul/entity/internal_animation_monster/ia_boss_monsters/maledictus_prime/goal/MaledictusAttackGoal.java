package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.goal;

import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.Maledictus_PrimeEntity;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.MaledictusPhantomEntity;
import com.maxwell.cataclysm_primed_soul.init.ModEntities;
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
                && this.maledictus.getAttackState() == 0
                && this.maledictus.hasLineOfSight(target)
                && this.hasAnyAttackReady(target);
    }

    
    private boolean hasAnyAttackReady(LivingEntity target) {
        double distance = this.maledictus.distanceTo(target);
        if (distance <= 3.5D) {
            return this.maledictus.isJabReady()
                    || this.maledictus.isCounterReady()
                    || this.maledictus.isGrabReady()
                    || this.maledictus.isExJabReady();
        }
        if (distance <= 7.0D) {
            return this.maledictus.isJabReady()
                    || this.maledictus.isShockwaveReady()
                    || this.maledictus.isChargeReady()
                    || this.maledictus.isPhantomReady();
        }
        if (distance <= 16.0D) {
            return this.maledictus.isChargeReady()
                    || this.maledictus.isFarReady()
                    || this.maledictus.isPhantomReady();
        }
        return this.maledictus.isFarReady()
                || this.maledictus.isChargeReady()
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

        if (distance <= 3.5D) {

            if (roll < 0.18F && this.maledictus.isCounterReady()) {
                return Maledictus_PrimeEntity.ATTACK_COUNTER_START;
            }
            if (roll < 0.36F && this.maledictus.isGrabReady()) {
                return Maledictus_PrimeEntity.ATTACK_GRAB_START;
            }
            if (roll < 0.55F && this.maledictus.isExJabReady()) {
                return Maledictus_PrimeEntity.ATTACK_EX_JAB_1;
            }
            if (this.maledictus.isJabReady()) {
                return Maledictus_PrimeEntity.ATTACK_JAB_1;
            }

            return this.maledictus.isCounterReady() ? Maledictus_PrimeEntity.ATTACK_COUNTER_START
                    : Maledictus_PrimeEntity.ATTACK_JAB_1;
        }

        if (distance <= 7.0D) {
            if (roll < 0.20F && this.maledictus.isShockwaveReady()) {
                return Maledictus_PrimeEntity.ATTACK_SHOCKWAVE_START;
            }
            if (roll < 0.40F && this.maledictus.isPhantomReady()) {
                return -1; 
            }
            if (roll < 0.65F && this.maledictus.isChargeReady()) {
                return Maledictus_PrimeEntity.ATTACK_CHARGE;
            }
            if (this.maledictus.isJabReady()) {
                return Maledictus_PrimeEntity.ATTACK_JAB_1;
            }
            return Maledictus_PrimeEntity.ATTACK_SHOCKWAVE_START;
        }

        if (distance <= 16.0D) {
            if (roll < 0.30F && this.maledictus.isChargeReady()) {
                return Maledictus_PrimeEntity.ATTACK_CHARGE;
            }
            if (roll < 0.52F && this.maledictus.isFarReady()) {
                return Maledictus_PrimeEntity.ATTACK_FAR_START;
            }
            if (this.maledictus.isPhantomReady()) {
                return -1; 
            }
            return this.maledictus.isChargeReady()
                    ? Maledictus_PrimeEntity.ATTACK_CHARGE
                    : Maledictus_PrimeEntity.ATTACK_FAR_START;
        }

        if (roll < 0.45F && this.maledictus.isFarReady()) {
            return Maledictus_PrimeEntity.ATTACK_FAR_START;
        }
        if (this.maledictus.isPhantomReady()) {
            return -1; 
        }
        return this.maledictus.isChargeReady()
                ? Maledictus_PrimeEntity.ATTACK_CHARGE
                : Maledictus_PrimeEntity.ATTACK_FAR_START;
    }

    
    private void spawnPhantom(LivingEntity target) {
        double distance = this.maledictus.distanceTo(target);
        int phantomType;
        if (distance <= 7.0D) {
            phantomType = MaledictusPhantomEntity.TYPE_MACE; 
        } else if (distance <= 16.0D) {

            phantomType = this.maledictus.getRandom().nextBoolean()
                    ? MaledictusPhantomEntity.TYPE_SPEAR
                    : MaledictusPhantomEntity.TYPE_BOW;
        } else {
            phantomType = MaledictusPhantomEntity.TYPE_BOW; 
        }

        MaledictusPhantomEntity phantom = ModEntities.MALEDICTUS_PHANTOM.get().create(this.maledictus.level());
        if (phantom != null) {
            phantom.moveTo(this.maledictus.getX(), this.maledictus.getY(), this.maledictus.getZ(),
                    this.maledictus.getYRot(), 0.0F);
            phantom.setPhantomType(phantomType);
            phantom.setTarget(target);
            phantom.setSummoner(this.maledictus);
            phantom.setSummonerYRot(this.maledictus.getYRot());
            this.maledictus.level().addFreshEntity(phantom);

            this.maledictus.setPhantomCooldown(phantomType == MaledictusPhantomEntity.TYPE_BOW ? 80 : 60);
        }
    }
}
