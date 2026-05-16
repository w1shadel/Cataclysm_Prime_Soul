package com.maxwell.cataclysm_primed_soul.entity.InternalAnimationMonster.IABossMonsters;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.IABoss_monster;
import com.github.L_Ender.cataclysm.entity.etc.CMBossInfoServer;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class Ignis_PrimeEntity extends IABoss_monster {
    private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(Ignis_PrimeEntity.class, EntityDataSerializers.INT);
    private static final UUID DEFENSE_REDUCTION_ID = UUID.fromString("f4d7b7e0-1234-4a5b-6c7d-8e9f01234567");
    private final CMBossInfoServer bossEvent;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState spawnAnimationState = new AnimationState();
    public AnimationState charge_attackAnimationState = new AnimationState();
    public AnimationState charge_attack_loopAnimationState = new AnimationState();
    public AnimationState charge_attack_endAnimationState = new AnimationState();
    public AnimationState charge_shockwave_attackAnimationState = new AnimationState();
    public AnimationState rock_excavation_attackAnimationState = new AnimationState();
    public AnimationState rock_excavation_attack_loopAnimationState = new AnimationState();
    public AnimationState rock_excavation_attack_endAnimationState = new AnimationState();
    public AnimationState uppercutAnimationState = new AnimationState();
    public AnimationState uppercut_horizontal_comboAnimationState = new AnimationState();
    public AnimationState uppercut_vertical_comboAnimationState = new AnimationState();
    public AnimationState jab_attack_oneAnimationState = new AnimationState();
    public AnimationState jab_attack_twoAnimationState = new AnimationState();
    public AnimationState jab_attack_threeAnimationState = new AnimationState();
    public AnimationState deadAnimationState = new AnimationState();
    public AnimationState mode_changeAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    private int lastPhaseTick = 0;
    public Ignis_PrimeEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.bossEvent = new CMBossInfoServer(this.getDisplayName(), BossEvent.BossBarColor.YELLOW, true, 99);
        this.xpReward = 1000;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 600.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 20.0D)
                .add(Attributes.ARMOR, 20.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHASE, 0);
    }

    public AnimationState getAnimationState(String input) {
        if (input == "swing") {
            return this.spawnAnimationState;
        } else if (input == "charge_attack") {
            return this.charge_attackAnimationState;
        } else if (input == "charge_attack_loop") {
            return this.charge_attack_loopAnimationState;
        } else if (input == "charge_attack_end") {
            return this.charge_attack_endAnimationState;
        } else if (input == "idle") {
            return this.idleAnimationState;
        } else if (input == "charge_shockwave_attack") {
            return this.charge_shockwave_attackAnimationState;
        } else if (input == "rock_excavation_attack") {
            return this.rock_excavation_attackAnimationState;
        } else if (input == "rock_excavation_attack_loop") {
            return this.rock_excavation_attack_loopAnimationState;
        } else if (input == "rock_excavation_attack_end") {
            return this.rock_excavation_attack_endAnimationState;
        } else if (input == "uppercut") {
            return this.uppercutAnimationState;
        } else if (input == "uppercut_horizontal_combo") {
            return this.uppercut_horizontal_comboAnimationState;
        } else if (input == "uppercut_vertical_combo") {
            return this.uppercut_vertical_comboAnimationState;
        } else if (input == "jab_attack_one") {
            return this.jab_attack_oneAnimationState;
        } else if (input == "jab_attack_two") {
            return this.jab_attack_twoAnimationState;
        } else if (input == "jab_attack_three") {
            return this.jab_attack_threeAnimationState;
        } else if (input == "dead") {
            return this.deadAnimationState;
        } else if (input == "mode_change") {
            return this.mode_changeAnimationState;
        } else {
            return this.walkAnimationState;
        }
    }

    private void stopAllAnimationStates() {
        this.charge_attackAnimationState.stop();
        this.uppercutAnimationState.stop();
        this.rock_excavation_attackAnimationState.stop();
        this.rock_excavation_attack_loopAnimationState.stop();
        this.rock_excavation_attack_endAnimationState.stop();
        this.deadAnimationState.stop();
        this.walkAnimationState.stop();
        this.uppercutAnimationState.stop();
        this.uppercut_horizontal_comboAnimationState.stop();
        this.uppercut_vertical_comboAnimationState.stop();
        this.charge_attack_endAnimationState.stop();
        this.charge_attackAnimationState.stop();
        this.charge_attack_loopAnimationState.stop();
        this.charge_shockwave_attackAnimationState.stop();
        this.jab_attack_oneAnimationState.stop();
        this.jab_attack_twoAnimationState.stop();
        this.jab_attack_threeAnimationState.stop();
        this.mode_changeAnimationState.stop();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
            updateBossPhase();
            applyBattlefieldEffects();
            if (getBossPhase() == 2) {
                lastPhaseTick++;
                if (lastPhaseTick >= 20) {
                    this.hurt(this.damageSources().generic(), this.getMaxHealth() * 0.01F);
                    lastPhaseTick = 0;
                }
            }
        }
    }

    private void updateBossPhase() {
        float healthPct = this.getHealth() / this.getMaxHealth();
        if (healthPct <= 0.01F && getBossPhase() < 2) {
            setBossPhase(2);
            this.setHealth(this.getMaxHealth());
            this.setAttackState(99);
        } else if (healthPct <= 0.5F && getBossPhase() < 1) {
            setBossPhase(1);
        }
    }

    private void applyBattlefieldEffects() {
        int phase = getBossPhase();
        double range = phase == 0 ? 30.0D : (phase == 1 ? 64.0D : 200.0D);
        for (Player player : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(range))) {
            if (phase == 2) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 1, false, false));

            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (getBossPhase() == 2 && source.is(net.minecraft.tags.DamageTypeTags.IS_PROJECTILE)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    public int getBossPhase() {
        return this.entityData.get(PHASE);
    }

    public void setBossPhase(int phase) {
        this.entityData.set(PHASE, phase);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (ATTACK_STATE.equals(key)) {
            int state = this.getAttackState();
            this.stopAllAnimationStates();
            switch (state) {
                case 1 -> this.charge_attackAnimationState.start(this.tickCount);
                case 2 -> this.uppercutAnimationState.start(this.tickCount);
                case 3 -> this.jab_attack_oneAnimationState.start(this.tickCount);
                case 99 -> this.mode_changeAnimationState.start(this.tickCount);
            }
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }
}
