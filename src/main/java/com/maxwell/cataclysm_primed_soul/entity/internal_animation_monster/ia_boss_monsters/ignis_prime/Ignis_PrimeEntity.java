package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.AI.InternalMoveGoal;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.IABoss_monster;
import com.github.L_Ender.cataclysm.entity.effect.Cm_Falling_Block_Entity;
import com.github.L_Ender.cataclysm.entity.effect.Flame_Strike_Entity;
import com.github.L_Ender.cataclysm.entity.etc.CMBossInfoServer;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.goal.*;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.sub.Prime_Flame_Strike_Entity;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.sub.FlameStrikeSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.UUID;

public class Ignis_PrimeEntity extends IABoss_monster {
    public static final int STATE_CHARGE_START = 1;
    public static final int STATE_UPPERCUT = 2;
    public static final int STATE_JAB_1 = 3;
    public static final int STATE_JAB_2 = 4;
    public static final int STATE_JAB_3 = 5;
    public static final int STATE_DEATH = 6;
    public static final int STATE_CHARGE_LOOP = 10;
    public static final int STATE_CHARGE_END = 11;
    public static final int STATE_UPPERCUT_HORIZONTAL = 12;
    public static final int STATE_UPPERCUT_VERTICAL = 13;
    public static final int STATE_CHARGE_SHOCKWAVE = 14;
    public static final int STATE_ROCK_START = 15;
    public static final int STATE_ROCK_LOOP = 16;
    public static final int STATE_ROCK_END = 17;
    public static final int STATE_POWER_SLAM = 20;
    public static final int STATE_GUARD_START = 21;
    public static final int STATE_GUARD_LOOP = 22;
    public static final int STATE_GUARD_BREAK = 23;
    public static final int STATE_GUARD_END = 24;
    public static final int STATE_CATCH_START = 25;
    public static final int STATE_CATCH_SUCCESS = 26;
    public static final int STATE_CATCH_FAIL = 27;
    public static final int STATE_JAB_EX_ONE = 28;
    public static final int STATE_OVERHEAD_GUARDBREAKER = 29;
    public static final int STATE_COMBO_RUSH_1 = 30;
    public static final int STATE_COMBO_RUSH_2 = 31;
    public static final int STATE_COMBO_RUSH_3 = 32;
    public static final int STATE_PHASE_CHANGE = 99;
    private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(Ignis_PrimeEntity.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> SPEED_MULTIPLIER = SynchedEntityData
            .defineId(Ignis_PrimeEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> CAUGHT_ENTITY_ID = SynchedEntityData
            .defineId(Ignis_PrimeEntity.class, EntityDataSerializers.INT);
    private static final UUID DEFENSE_REDUCTION_ID = UUID.fromString("f4d7b7e0-1234-4a5b-6c7d-8e9f01234567");
    private final CMBossInfoServer bossEvent;
    private int lastAttackState = 0;
    private Vec3 uppercutStartPos = null;
    private int uppercutReappearTicks = 0;
    private boolean hasSentAppearMessage = false;
    private boolean hasSentHalfHpMessage = false;
    private boolean hasSentPhase2Message = false;
    private int targetShieldingTicks = 0;
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
    public AnimationState power_slamAnimationState = new AnimationState();
    public AnimationState guard_startAnimationState = new AnimationState();
    public AnimationState guard_loopAnimationState = new AnimationState();
    public AnimationState guard_breakAnimationState = new AnimationState();
    public AnimationState guard_endAnimationState = new AnimationState();
    public AnimationState catch_startAnimationState = new AnimationState();
    public AnimationState catch_successAnimationState = new AnimationState();
    public AnimationState catch_failAnimationState = new AnimationState();
    public AnimationState jab_attack_ex_oneAnimationState = new AnimationState();
    public AnimationState overhead_guardbreakerAnimationState = new AnimationState();
    public AnimationState deadAnimationState = new AnimationState();
    public AnimationState mode_changeAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public boolean rockProjectileHit = false;
    public boolean isGuarding = false;
    public int totalAttacksMade = 0;
    public int jabComboCount = 0;
    public int ticksSinceLastHurt = 0;
    public float recentDamageTaken = 0.0F;
    public int catchCooldown = 0;
    public int overheadCooldown = 0;
    public int guardCooldown = 0;
    private boolean uppercutHit = false;
    private boolean uppercutAmbushHidden = false;
    private int lastPhaseTick = 0;
    private int jabCooldown = 0;
    private int uppercutCooldown = 0;
    private int chargeCooldown = 0;
    private int rockCooldown = 0;
    public int phaseChangeTicks = 0;
    private int guardAxeHits = 0;
    private float guardDamageTaken = 0.0F;
    private Entity caughtEntity = null;

    public Ignis_PrimeEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.bossEvent = new CMBossInfoServer(this.getDisplayName(), BossEvent.BossBarColor.YELLOW, true, 99);
        this.xpReward = 1000;
        this.setMaxUpStep(2.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 600.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 20.0D)
                .add(Attributes.ARMOR, 20.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        this.showAfterUppercutAmbush();
        this.setDeltaMovement(Vec3.ZERO);
        this.setAttackState(STATE_DEATH);
        if (!this.level().isClientSide()) {
            IgnisDebuffManager.unregisterBoss(this);
        }
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        super.remove(reason);
        if (!this.level().isClientSide()) {
            IgnisDebuffManager.unregisterBoss(this);
        }
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        this.setDeltaMovement(this.getDeltaMovement().multiply(0.4D, 1.0D, 0.4D));
        if (this.deathTime >= 145 && !this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    public void setJabCooldown(int ticks) {
        this.jabCooldown = ticks;
    }

    public void setUppercutCooldown(int ticks) {
        this.uppercutCooldown = ticks;
    }

    public void setChargeCooldown(int ticks) {
        this.chargeCooldown = ticks;
    }

    public void setRockCooldown(int ticks) {
        this.rockCooldown = ticks;
    }

    public boolean isJabReady() {
        return this.jabCooldown <= 0;
    }

    public boolean isUppercutReady() {
        return this.uppercutCooldown <= 0;
    }

    public boolean isChargeReady() {
        return this.chargeCooldown <= 0;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHASE, 0);
        this.entityData.define(SPEED_MULTIPLIER, 1.2F);
        this.entityData.define(CAUGHT_ENTITY_ID, -1);
    }

    public int getCaughtEntityId() {
        return this.entityData.get(CAUGHT_ENTITY_ID);
    }

    public void setCaughtEntityId(int id) {
        this.entityData.set(CAUGHT_ENTITY_ID, id);
    }

    public Entity getCaughtEntity() {
        if (!this.level().isClientSide) {
            return this.caughtEntity;
        } else {
            int id = this.getCaughtEntityId();
            return id == -1 ? null : this.level().getEntity(id);
        }
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
        } else if (input == "power_slam") {
            return this.power_slamAnimationState;
        } else if (input == "guard_start") {
            return this.guard_startAnimationState;
        } else if (input == "guard_loop") {
            return this.guard_loopAnimationState;
        } else if (input == "guard_break") {
            return this.guard_breakAnimationState;
        } else if (input == "guard_end") {
            return this.guard_endAnimationState;
        } else if (input == "catch_start") {
            return this.catch_startAnimationState;
        } else if (input == "catch_success") {
            return this.catch_successAnimationState;
        } else if (input == "catch_fail") {
            return this.catch_failAnimationState;
        } else if (input == "jab_attack_ex_one") {
            return this.jab_attack_ex_oneAnimationState;
        } else if (input == "overhead_guardbreaker") {
            return this.overhead_guardbreakerAnimationState;
        } else if (input == "dead") {
            return this.deadAnimationState;
        } else if (input == "mode_change") {
            return this.mode_changeAnimationState;
        } else if (input == "walk") {
            return this.walkAnimationState;
        }
        return new AnimationState();
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
        this.power_slamAnimationState.stop();
        this.guard_startAnimationState.stop();
        this.guard_breakAnimationState.stop();
        this.guard_endAnimationState.stop();
        this.guard_loopAnimationState.stop();
        this.catch_startAnimationState.stop();
        this.catch_successAnimationState.stop();
        this.catch_failAnimationState.stop();
        this.overhead_guardbreakerAnimationState.stop();
        this.jab_attack_ex_oneAnimationState.stop();
        this.mode_changeAnimationState.stop();
    }

    private void updateBossPhase() {
        float healthPct = this.getHealth() / this.getMaxHealth();
        if (healthPct <= 0.01F && getBossPhase() < 2) {
            setBossPhase(2);
            this.setHealth(this.getMaxHealth());
            this.phaseChangeTicks = 0;
            this.showAfterUppercutAmbush();
            this.setAttackState(STATE_PHASE_CHANGE);
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
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }
    @Override
    public void push(Entity entityIn) {
        if (entityIn == this.getCaughtEntity()) return;
        super.push(entityIn);
    }

    @Override
    public boolean canBePushedByEntity(Entity entity) {
        if (entity == this.getCaughtEntity()) return false;
        return super.canBePushedByEntity(entity);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.getAttackState() == STATE_PHASE_CHANGE) {
            return false;
        }
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_FALL)) {
            return false;
        }
        if (getBossPhase() == 2 && source.is(net.minecraft.tags.DamageTypeTags.IS_PROJECTILE)) {
            return false;
        }
        boolean isGenericKill = source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY);
        this.recentDamageTaken += amount;
        if (source.getEntity() != null && source.getEntity() == this.getTarget()) {
            this.ticksSinceLastHurt = 0;
        }
        if (this.isGuarding) {
            boolean isMagic = source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_ARMOR) && !isGenericKill;
            if (isGenericKill) {
            } else if (isMagic) {
                amount *= 0.5F;
            } else {
                Entity attacker = source.getEntity();
                if (attacker instanceof LivingEntity livingAttacker) {
                    if (livingAttacker.getMainHandItem().getItem() instanceof net.minecraft.world.item.AxeItem) {
                        this.guardAxeHits++;
                    }
                }
                this.guardDamageTaken += amount;
                float maxDurability = (this.getTarget() instanceof net.minecraft.world.entity.player.Player) ? 60.0F
                        : 400.0F;
                if (this.guardAxeHits >= 3 || this.guardDamageTaken >= maxDurability) {
                    this.setAttackState(STATE_GUARD_BREAK);
                    this.isGuarding = false;
                    this.playSound(SoundEvents.SHIELD_BREAK, 1.0F, 0.8F);
                } else {
                    this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 0.8F + this.random.nextFloat() * 0.2F);
                    if (!this.level().isClientSide) {
                        com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToClients(new com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect(this.getId(), 4));
                        System.out.println("[Ignis Visual Effect] Sent GuardSuccess packet");
                    }
                }
                return false;
            }
        }
        if (!isGenericKill && amount > 30.0F) {
            amount = 30.0F;
        }
        return super.hurt(source, amount);
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

    @Override
    public boolean startRiding(Entity vehicle, boolean force) {
        if (super.startRiding(vehicle, force)) {
            this.setAttackState(0);
            this.setDeltaMovement(Vec3.ZERO);
            return true;
        }
        return false;
    }

    public int getBossPhase() {
        return this.entityData.get(PHASE);
    }

    public void setBossPhase(int phase) {
        this.entityData.set(PHASE, phase);
    }

    public float getAttackSpeedMultiplier() {
        return this.entityData.get(SPEED_MULTIPLIER);
    }

    public void setAttackSpeedMultiplier(float speed) {
        this.entityData.set(SPEED_MULTIPLIER, speed);
    }

    public int getScaledTick(int baseTick) {
        return Math.round(baseTick / this.getAttackSpeedMultiplier());
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (!this.hasSentAppearMessage) {
                this.hasSentAppearMessage = true;
                IgnisDebuffManager.registerBoss(this);
                this.sendBossMessage("chat.cataclysm_primed_soul.ignis_prime.appear");
            }
            if (!this.hasSentHalfHpMessage && (this.getHealth() / this.getMaxHealth() <= 0.5F)) {
                this.hasSentHalfHpMessage = true;
                this.sendBossMessage("chat.cataclysm_primed_soul.ignis_prime.half_hp");
            }
            if (!this.hasSentPhase2Message && this.getAttackState() == STATE_PHASE_CHANGE && this.phaseChangeTicks >= 70) {
                this.hasSentPhase2Message = true;
                this.sendBossMessage("chat.cataclysm_primed_soul.ignis_prime.phase_2");
                if (!this.level().isClientSide) {
                    com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToClients(new com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect(this.getId(), 3));
                    com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity.ScreenShake(this.level(), this.position(), 30.0F, 0.3F, 0, 45);
                    System.out.println("[Ignis Visual Effect] Sent PhaseChange packet & spawned ScreenShake");
                }
            }
            IgnisDebuffManager.tickBossDebuffs(this);

            LivingEntity target = this.getTarget();
            if (target != null && target.isUsingItem() && (target.getUseItem().getItem() instanceof net.minecraft.world.item.ShieldItem || target.getUseItem().canPerformAction(net.minecraftforge.common.ToolActions.SHIELD_BLOCK))) {
                this.targetShieldingTicks++;
                if (this.targetShieldingTicks >= 60 && this.getAttackState() == 0 && this.overheadCooldown <= 0) {
                    this.setAttackState(STATE_OVERHEAD_GUARDBREAKER);
                    this.attackTicks = 0;
                    this.targetShieldingTicks = 0;
                    this.overheadCooldown = 200;
                }
            } else {
                this.targetShieldingTicks = 0;
            }

            int currentAttackState = this.getAttackState();
            if ((this.lastAttackState == STATE_UPPERCUT_HORIZONTAL || this.lastAttackState == STATE_UPPERCUT_VERTICAL)
                    && currentAttackState == 0) {
                if (this.uppercutStartPos != null) {
                    this.teleportTo(this.uppercutStartPos.x, this.uppercutStartPos.y, this.uppercutStartPos.z);
                    this.setDeltaMovement(0.0D, 0.0D, 0.0D);
                }
                this.setInvisible(true);
                this.setNoGravity(false);
                this.uppercutReappearTicks = 6;
            }
            if (this.uppercutReappearTicks > 0) {
                this.uppercutReappearTicks--;
                if (this.uppercutReappearTicks == 0) {
                    this.setInvisible(false);
                }
            }
            this.lastAttackState = currentAttackState;
        }
        this.ticksSinceLastHurt++;
        if (this.catchCooldown > 0)
            this.catchCooldown--;
        if (this.overheadCooldown > 0)
            this.overheadCooldown--;
        if (this.guardCooldown > 0)
            this.guardCooldown--;
        if (this.recentDamageTaken > 0) {
            this.recentDamageTaken -= 0.5F;
            if (this.recentDamageTaken < 0)
                this.recentDamageTaken = 0.0F;
        }
        if (this.level().isClientSide()) {
            boolean canPlayIdleWalk = this.getAttackState() == 0 && this.isAlive();
            boolean isMoving = this.walkAnimation.isMoving();
            this.idleAnimationState.animateWhen(!isMoving && canPlayIdleWalk, this.tickCount);
            this.walkAnimationState.animateWhen(isMoving && canPlayIdleWalk, this.tickCount);
        } else {
            if (this.jabCooldown > 0)
                this.jabCooldown--;
            if (this.rockCooldown > 0)
                this.rockCooldown--;
            if (this.uppercutCooldown > 0)
                this.uppercutCooldown--;
            if (this.chargeCooldown > 0)
                this.chargeCooldown--;
            if (this.getTarget() == null && this.getHealth() < this.getMaxHealth() && this.tickCount % 20 == 0) {
                this.heal(50.0F);
            }
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
            updateBossPhase();
            applyBattlefieldEffects();
            if (getBossPhase() == 2) {
                lastPhaseTick++;
                if (lastPhaseTick >= 20) {
                    float drain = this.getMaxHealth() * 0.01F;
                    if (this.getHealth() <= drain) {
                        this.hurt(this.damageSources().generic(), drain);
                    } else {
                        this.setHealth(this.getHealth() - drain);
                    }
                    lastPhaseTick = 0;
                }
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (ATTACK_STATE.equals(key)) {
            int state = this.getAttackState();
            this.stopAllAnimationStates();
            switch (state) {
                case STATE_CHARGE_START -> this.charge_attackAnimationState.start(this.tickCount);
                case STATE_CHARGE_LOOP -> this.charge_attack_loopAnimationState.start(this.tickCount);
                case STATE_CHARGE_END -> this.charge_attack_endAnimationState.start(this.tickCount);
                case STATE_UPPERCUT -> this.uppercutAnimationState.start(this.tickCount);
                case STATE_JAB_1 -> this.jab_attack_oneAnimationState.start(this.tickCount);
                case STATE_JAB_2 -> this.jab_attack_twoAnimationState.start(this.tickCount);
                case STATE_JAB_3 -> this.jab_attack_threeAnimationState.start(this.tickCount);
                case STATE_UPPERCUT_HORIZONTAL -> this.uppercut_horizontal_comboAnimationState.start(this.tickCount);
                case STATE_UPPERCUT_VERTICAL -> this.uppercut_vertical_comboAnimationState.start(this.tickCount);
                case STATE_DEATH -> this.deadAnimationState.start(this.tickCount);
                case STATE_ROCK_START -> this.rock_excavation_attackAnimationState.start(this.tickCount);
                case STATE_ROCK_LOOP -> this.rock_excavation_attack_loopAnimationState.start(this.tickCount);
                case STATE_ROCK_END -> this.rock_excavation_attack_endAnimationState.start(this.tickCount);
                case STATE_CHARGE_SHOCKWAVE -> this.charge_shockwave_attackAnimationState.start(this.tickCount);
                case STATE_POWER_SLAM -> this.power_slamAnimationState.start(this.tickCount);
                case STATE_GUARD_START -> this.guard_startAnimationState.start(this.tickCount);
                case STATE_GUARD_LOOP -> this.guard_loopAnimationState.start(this.tickCount);
                case STATE_GUARD_BREAK -> this.guard_breakAnimationState.start(this.tickCount);
                case STATE_GUARD_END -> this.guard_endAnimationState.start(this.tickCount);
                case STATE_CATCH_START -> this.catch_startAnimationState.start(this.tickCount);
                case STATE_CATCH_SUCCESS -> this.catch_successAnimationState.start(this.tickCount);
                case STATE_CATCH_FAIL -> this.catch_failAnimationState.start(this.tickCount);
                case STATE_JAB_EX_ONE -> this.jab_attack_ex_oneAnimationState.start(this.tickCount);
                case STATE_OVERHEAD_GUARDBREAKER -> this.overhead_guardbreakerAnimationState.start(this.tickCount);
                case STATE_COMBO_RUSH_1 -> this.jab_attack_ex_oneAnimationState.start(this.tickCount);
                case STATE_COMBO_RUSH_2 -> this.uppercut_horizontal_comboAnimationState.start(this.tickCount);
                case STATE_COMBO_RUSH_3 -> this.power_slamAnimationState.start(this.tickCount);
                case STATE_PHASE_CHANGE -> this.mode_changeAnimationState.start(this.tickCount);
            }
            if (state == STATE_UPPERCUT_HORIZONTAL || state == STATE_UPPERCUT_VERTICAL || state == 0) {
                this.showAfterUppercutAmbush();
            }
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new IgnisStateGoal(this, 0, STATE_ROCK_START, STATE_ROCK_LOOP, 18, 18, 15.0F) {
            @Override
            public boolean canUse() {
                LivingEntity t = ignis.getTarget();
                if (t == null || !ignis.isRockReady())
                    return false;
                double distance = ignis.distanceTo(t);
                float chance = distance > 18.0D ? 0.45F : 0.25F;
                return super.canUse() && distance >= 10.0D && distance <= 30.0D
                        && ignis.getRandom().nextFloat() < chance;
            }
        });
        this.goalSelector.addGoal(0, new IgnisRockLoopGoal(this));
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_ROCK_END, STATE_ROCK_END, 0, 21, 0, 20.0F) {
            @Override
            public void tick() {
                super.tick();
                if (ignis.attackTicks == 10)
                    ignis.performRockThrow();
            }

            @Override
            public void stop() {
                super.stop();
                if (ignis.rockProjectileHit) {
                    ignis.setAttackState(STATE_CHARGE_START);
                } else {
                    ignis.setAttackState(0);
                    ignis.setRockCooldown(140);
                }
            }
        });
        this.goalSelector.addGoal(1, new IgnisChargeGoal(this, 15.0F));
        this.goalSelector.addGoal(0, new IgnisChargeLoopGoal(this));
        this.goalSelector.addGoal(0,
                new IgnisStateGoal(this, STATE_CHARGE_SHOCKWAVE, STATE_CHARGE_SHOCKWAVE, 0, 36, 0, 20.0F));
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_CHARGE_END, STATE_CHARGE_END, 0, 8, 0, 20.0F));
        this.goalSelector.addGoal(0,
                new IgnisStateGoal(this, STATE_UPPERCUT_HORIZONTAL, STATE_UPPERCUT_HORIZONTAL, 0, 33, 0, 20.0F));
        this.goalSelector.addGoal(0,
                new IgnisStateGoal(this, STATE_UPPERCUT_VERTICAL, STATE_UPPERCUT_VERTICAL, 0, 24, 0, 20.0F));
        this.goalSelector.addGoal(2, new IgnisUppercutGoal(this, 4.0F));
        this.goalSelector.addGoal(3, new IgnisJabGoal(this, 0, 3, 4, 22, 15, 4.5F) {
            @Override
            public boolean canUse() {
                return super.canUse() && ignis.getRandom().nextFloat() < 0.5F;
            }
        });
        this.goalSelector.addGoal(0, new IgnisJabGoal(this, 4, 4, 5, 20, 12, 4.5F));
        this.goalSelector.addGoal(0, new IgnisJabGoal(this, 5, 5, 0, 29, 15, 5.0F) {
            @Override
            public void stop() {
                super.stop();
                ignis.jabComboCount++;
            }
        });
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_POWER_SLAM, STATE_POWER_SLAM, 0, 61, 0, 20.0F));
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_JAB_EX_ONE, STATE_JAB_EX_ONE, 0, 58, 0, 20.0F));
        this.goalSelector.addGoal(0,
                new IgnisStateGoal(this, STATE_OVERHEAD_GUARDBREAKER, STATE_OVERHEAD_GUARDBREAKER, 0, 61, 0, 20.0F));
        this.goalSelector.addGoal(0,
                new IgnisStateGoal(this, STATE_GUARD_START, STATE_GUARD_START, STATE_GUARD_LOOP, 21, 0, 20.0F));
        this.goalSelector.addGoal(0,
                new IgnisStateGoal(this, STATE_GUARD_LOOP, STATE_GUARD_LOOP, STATE_GUARD_END, 80, 0, 20.0F));
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_GUARD_END, STATE_GUARD_END, 0, 6, 0, 20.0F));
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_GUARD_BREAK, STATE_GUARD_BREAK, 0, 35, 0, 20.0F));
        this.goalSelector.addGoal(0,
                new IgnisStateGoal(this, STATE_CATCH_START, STATE_CATCH_START, STATE_CATCH_FAIL, 47, 0, 20.0F));
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_CATCH_FAIL, STATE_CATCH_FAIL, 0, 17, 0, 20.0F));
        this.goalSelector.addGoal(0,
                new IgnisStateGoal(this, STATE_CATCH_SUCCESS, STATE_CATCH_SUCCESS, 0, 68, 0, 20.0F));
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, 0, STATE_GUARD_START, STATE_GUARD_LOOP, 21, 0, 20.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && ignis.getTarget() != null && ignis.recentDamageTaken >= 200.0F
                        && ignis.guardCooldown <= 0;
            }

            @Override
            public void start() {
                super.start();
                ignis.guardCooldown = 1200;
                ignis.recentDamageTaken = 0.0F;
            }
        });
        this.goalSelector.addGoal(1, new IgnisStateGoal(this, 0, STATE_CATCH_START, STATE_CATCH_FAIL, 47, 0, 4.0F) {
            @Override
            public boolean canUse() {
                LivingEntity t = ignis.getTarget();
                if (t == null)
                    return false;
                boolean near = ignis.distanceTo(t) <= 4.0D && ignis.catchCooldown <= 0;
                boolean after10 = ignis.totalAttacksMade >= 10;
                return super.canUse() && (near || after10);
            }

            @Override
            public void start() {
                super.start();
                ignis.catchCooldown = 400;
                if (ignis.totalAttacksMade >= 10)
                    ignis.totalAttacksMade = 0;
            }
        });
        this.goalSelector.addGoal(1, new IgnisStateGoal(this, 0, STATE_POWER_SLAM, 0, 61, 0, 5.0F) {
            @Override
            public boolean canUse() {
                LivingEntity t = ignis.getTarget();
                if (t == null)
                    return false;
                boolean behind = ignis.distanceTo(t) <= 6.0D && ignis.isTargetBehind();
                boolean after6 = ignis.totalAttacksMade >= 6;
                return super.canUse() && (behind || after6);
            }

            @Override
            public void start() {
                super.start();
                if (ignis.totalAttacksMade >= 6)
                    ignis.totalAttacksMade = 0;
            }
        });
        this.goalSelector.addGoal(1, new IgnisStateGoal(this, 0, STATE_OVERHEAD_GUARDBREAKER, 0, 61, 0, 4.5F) {
            @Override
            public boolean canUse() {
                LivingEntity t = ignis.getTarget();
                if (t == null)
                    return false;
                boolean notAttacking = ignis.ticksSinceLastHurt >= 100 && ignis.distanceTo(t) <= 4.5D;
                boolean after8 = ignis.totalAttacksMade >= 8;
                return super.canUse() && ignis.overheadCooldown <= 0 && (notAttacking || after8);
            }

            @Override
            public void start() {
                super.start();
                ignis.overheadCooldown = 200;
                if (ignis.totalAttacksMade >= 8)
                    ignis.totalAttacksMade = 0;
            }
        });
        this.goalSelector.addGoal(1, new IgnisStateGoal(this, 0, STATE_JAB_EX_ONE, 0, 58, 0, 4.5F) {
            @Override
            public boolean canUse() {
                LivingEntity t = ignis.getTarget();
                return super.canUse() && t != null && ignis.distanceTo(t) <= 5.0D && ignis.jabComboCount >= 3;
            }

            @Override
            public void start() {
                super.start();
                ignis.jabComboCount = 0;
            }
        });
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_COMBO_RUSH_2, STATE_COMBO_RUSH_2, STATE_COMBO_RUSH_3, 16, 0, 20.0F));
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_COMBO_RUSH_3, STATE_COMBO_RUSH_3, 0, 61, 0, 20.0F));
        this.goalSelector.addGoal(1, new IgnisStateGoal(this, 0, STATE_COMBO_RUSH_1, STATE_COMBO_RUSH_2, 48, 0, 10.0F) {
            @Override
            public boolean canUse() {
                LivingEntity t = ignis.getTarget();
                return super.canUse() && t != null && ignis.distanceTo(t) <= 15.0D && ignis.getRandom().nextFloat() < 0.15F;
            }
        });
        this.goalSelector.addGoal(4, new InternalMoveGoal(this, false, 1.2D));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.attackTicks == 1) {
            int state = this.getAttackState();
            if (state == STATE_JAB_1 || state == STATE_ROCK_START || state == STATE_CHARGE_START
                    || state == STATE_UPPERCUT || state == STATE_CATCH_START || state == STATE_POWER_SLAM
                    || state == STATE_OVERHEAD_GUARDBREAKER || state == STATE_JAB_EX_ONE
                    || state == STATE_COMBO_RUSH_1) {
                this.totalAttacksMade++;
            }
        }
        if (this.isPassenger()) {
            if (this.getAttackState() != 0)
                this.setAttackState(0);
            if (this.level().isClientSide())
                this.stopAllAnimationStates();
        }
        LivingEntity target = this.getTarget();
        int state = this.getAttackState();
        switch (state) {
            case STATE_PHASE_CHANGE:
                handlePhaseChangeAction();
                break;
            case STATE_CHARGE_START:
                if (this.rockProjectileHit) {
                    handleBlinkMovement(target);
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 1.0, 0.5));
                }
                break;
            case STATE_CHARGE_LOOP:
                handleChargeMovement();
                if (!this.level().isClientSide && this.tickCount % 2 == 0) {
                    com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToClients(new com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect(this.getId(), 2));
                    System.out.println("[Ignis Visual Effect] Sent Charge packet");
                }
                break;
            case STATE_ROCK_START:
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.3, 1.0, 0.3));
                break;
            case STATE_ROCK_LOOP:
                handleRockLoopMovement();
                break;
            case STATE_CHARGE_SHOCKWAVE:
                handleShockwaveAction();
                break;
            case STATE_UPPERCUT:
                if (this.attackTicks == 1) {
                    this.uppercutHit = false;
                    this.uppercutStartPos = this.position();
                }
                if (this.attackTicks == 23) {
                    this.performUppercutDamage(1.8f);
                    if (!this.level().isClientSide) {
                        com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToClients(new com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect(this.getId(), 0));
                        com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity.ScreenShake(this.level(), this.position(), 20.0F, 0.15F, 0, 15);
                        System.out.println("[Ignis Visual Effect] Sent Uppercut packet & spawned ScreenShake");
                    }
                }
                if (this.uppercutHit && this.attackTicks >= 33) {
                    this.hideForUppercutAmbush();
                }
                break;
            case STATE_UPPERCUT_HORIZONTAL:
            case STATE_UPPERCUT_VERTICAL:
                this.showAfterUppercutAmbush();
                if (this.attackTicks <= 5 && target != null)
                    teleportToTarget(target);
                this.setDeltaMovement(0, 0, 0);
                this.setNoGravity(true);
                if (state == STATE_UPPERCUT_HORIZONTAL) {
                    if (this.attackTicks == 6) {
                        this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 0.5F);
                        this.performComboDamage(1.5F, 4.75F, 130.0F, 1.1F, 2.4D, 0.2D);
                        if (!this.level().isClientSide) {
                            com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToClients(new com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect(this.getId(), 0));
                            com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity.ScreenShake(this.level(), this.position(), 20.0F, 0.15F, 0, 15);
                            System.out.println("[Ignis Visual Effect] Sent Uppercut Horizontal packet & spawned ScreenShake");
                        }
                    }
                } else {
                    if (this.attackTicks == 12) {
                        this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 0.8F);
                        this.performComboDamage(2.0F, 4.75F, 130.0F, 0.15F, 0.0D, -2.4D);
                        if (!this.level().isClientSide) {
                            com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToClients(new com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect(this.getId(), 0));
                            com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity.ScreenShake(this.level(), this.position(), 20.0F, 0.15F, 0, 15);
                            System.out.println("[Ignis Visual Effect] Sent Uppercut Vertical packet & spawned ScreenShake");
                        }
                    }
                }
                break;
            case 3:
                if (this.attackTicks == getScaledTick(10)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.2F);
                    float yaw = this.getYRot() * ((float) Math.PI / 180F);
                    this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(yaw) * 0.3D, 0.0D, Mth.cos(yaw) * 0.3D));
                    this.hasImpulse = true;
                }
                if (this.attackTicks == getScaledTick(18))
                    this.performJabDamage(1.0F, 0.4F);
                break;
            case 4:
                if (this.attackTicks == getScaledTick(8)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.3F);
                    float yaw = this.getYRot() * ((float) Math.PI / 180F);
                    this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(yaw) * 0.3D, 0.0D, Mth.cos(yaw) * 0.3D));
                    this.hasImpulse = true;
                }
                if (this.attackTicks == getScaledTick(17))
                    this.performJabDamage(1.0F, 0.4F);
                break;
            case 5:
                if (this.attackTicks == getScaledTick(12)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.0F, 0.8F);
                    float yaw = this.getYRot() * ((float) Math.PI / 180F);
                    this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(yaw) * 0.45D, 0.0D, Mth.cos(yaw) * 0.45D));
                    this.hasImpulse = true;
                }
                if (this.attackTicks == getScaledTick(18))
                    this.performJabDamage(2.0F, 0.8F);
                break;
            case STATE_POWER_SLAM:
                if (this.attackTicks == getScaledTick(38)) {
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 1.5F, 0.6F);
                    this.performAreaDamage(2.0F, 1.5F, 7.0D, 3.0D, 0.0D, 0.6D);
                    this.spawnFallingBlockShockwave(7, 360.0F);
                    this.spawnFlameStrike(this.getX(), this.getZ(), this.getY() - 1, this.getY() + 1, 0, 40, 10, 0,
                            5.0F, false);
                    if (!this.level().isClientSide) {
                        com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToClients(new com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect(this.getId(), 1));
                        com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity.ScreenShake(this.level(), this.position(), 20.0F, 0.25F, 0, 20);
                        System.out.println("[Ignis Visual Effect] Sent PowerSlam packet & spawned ScreenShake");
                        this.destroyBlocksInAABB(this.getBoundingBox().inflate(7.0D, 2.0D, 7.0D));
                    }
                }
                break;
            case STATE_JAB_EX_ONE:
                if (this.attackTicks == getScaledTick(23)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.2F);
                    this.performJabDamage(1.5F, 0.5F);
                }
                if (this.attackTicks == getScaledTick(41)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.0F, 0.8F);
                    this.performJabDamage(2.5F, 1.0F);
                    float yaw = this.getYRot() * ((float) Math.PI / 180F);
                    double fx = this.getX() - Mth.sin(yaw) * 2.5D;
                    double fz = this.getZ() + Mth.cos(yaw) * 2.5D;
                    this.spawnFlameStrike(fx, fz, this.getY() - 1, this.getY() + 1, 0, 30, 5, 0, 2.0F, false);
                }
                break;
            case STATE_COMBO_RUSH_1:
                if (this.attackTicks == getScaledTick(23)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.2F);
                    this.performJabDamage(1.5F, 0.5F);
                    float yaw = this.getYRot() * ((float) Math.PI / 180F);
                    this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(yaw) * 1.5D, 0.0D, Mth.cos(yaw) * 1.5D));
                }
                if (this.attackTicks == getScaledTick(41)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.0F, 0.8F);
                    this.performJabDamage(2.5F, 1.0F);
                }
                if (this.attackTicks >= getScaledTick(48)) {
                    this.setAttackState(STATE_COMBO_RUSH_2);
                }
                break;
            case STATE_COMBO_RUSH_2:
                this.setDeltaMovement(0, 0, 0);
                this.setNoGravity(true);
                if (this.attackTicks <= 5 && target != null) teleportToTarget(target);
                if (this.attackTicks == 6) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 0.5F);
                    this.performComboDamage(1.5F, 4.75F, 130.0F, 1.1F, 2.4D, 0.2D);
                    if (!this.level().isClientSide) {
                         com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToClients(new com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect(this.getId(), 0));
                         com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity.ScreenShake(this.level(), this.position(), 20.0F, 0.15F, 0, 15);
                    }
                }
                if (this.attackTicks >= 16) {
                    this.setAttackState(STATE_COMBO_RUSH_3);
                }
                break;
            case STATE_COMBO_RUSH_3:
                if (this.attackTicks == getScaledTick(38)) {
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 1.5F, 0.6F);
                    this.performAreaDamage(2.5F, 1.5F, 7.0D, 3.0D, 0.0D, 0.6D);
                    this.spawnFallingBlockShockwave(7, 360.0F);
                    this.spawnFlameStrike(this.getX(), this.getZ(), this.getY() - 1, this.getY() + 1, 0, 40, 10, 0, 5.0F, false);
                    if (!this.level().isClientSide) {
                        com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToClients(new com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect(this.getId(), 1));
                        com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity.ScreenShake(this.level(), this.position(), 20.0F, 0.25F, 0, 20);
                        this.destroyBlocksInAABB(this.getBoundingBox().inflate(7.0D, 2.0D, 7.0D));
                    }
                }
                if (this.attackTicks >= getScaledTick(61)) {
                    this.setAttackState(0);
                }
                break;
            case STATE_OVERHEAD_GUARDBREAKER:
                if (this.attackTicks == 1) {
                    this.targetShieldingTicks = 0;
                }
                if (this.attackTicks == getScaledTick(38)) {
                    this.playSound(SoundEvents.ANVIL_LAND, 1.0F, 0.5F);
                    this.performOverheadDamage();
                    float yaw = this.getYRot() * ((float) Math.PI / 180F);
                    double fx = this.getX() - Mth.sin(yaw) * 4.0D;
                    double fz = this.getZ() + Mth.cos(yaw) * 4.0D;
                    this.spawnFlameStrike(fx, fz, this.getY() - 1, this.getY() + 1, 0, 40, 10, 0, 3.0F, false);
                }
                break;
            case STATE_CATCH_START:
                if (target != null) {
                    this.lookAt(target, 30.0F, 30.0F);
                    this.yBodyRot = this.getYRot();
                    this.yHeadRot = this.getYRot();
                }
                if (this.attackTicks >= 37 && this.attackTicks <= 47) {
                    float yaw = this.getYRot() * ((float) Math.PI / 180F);
                    this.setDeltaMovement(-Mth.sin(yaw) * 0.4, this.getDeltaMovement().y, Mth.cos(yaw) * 0.4);
                    if (this.caughtEntity == null) {
                        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                                this.getBoundingBox().inflate(1.5D));
                        for (LivingEntity t : targets) {
                            if (this.canDamageTarget(t) && this.isInFrontArc(t, 90)) {
                                this.caughtEntity = t;
                                this.setCaughtEntityId(t.getId());
                                this.playSound(SoundEvents.PLAYER_ATTACK_CRIT, 1.0F, 1.0F);
                                break;
                            }
                        }
                        if (this.caughtEntity == null && this.attackTicks == 47) {
                            this.setAttackState(STATE_CATCH_FAIL);
                            this.setCaughtEntityId(-1);
                        }
                    }
                }
                if (this.caughtEntity != null && this.caughtEntity.isAlive()) {
                    if (this.attackTicks == 47) {
                        this.setAttackState(STATE_CATCH_SUCCESS);
                    }
                } else if (this.attackTicks == 47) {
                    this.setAttackState(STATE_CATCH_FAIL);
                    this.setCaughtEntityId(-1);
                }
                break;
            case STATE_CATCH_FAIL:
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.2, 1.0, 0.2));
                break;
            case STATE_CATCH_SUCCESS:
                if (this.caughtEntity != null) {
                    this.caughtEntity.fallDistance = 0;
                    this.caughtEntity.setDeltaMovement(Vec3.ZERO);
                    float yaw = this.yBodyRot * ((float) Math.PI / 180F);
                    double offsetX = -Mth.sin(yaw) * 1.5;
                    double offsetZ = Mth.cos(yaw) * 1.5;
                    double height = 1.2;
                    this.caughtEntity.setPos(this.getX() + offsetX, this.getY() + height, this.getZ() + offsetZ);
                    this.caughtEntity.setDeltaMovement(Vec3.ZERO);
                    if (this.attackTicks == 32) {
                        this.caughtEntity.hurt(this.damageSources().mobAttack(this), 10.0F);
                        this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 1.0F);
                    }
                    if (this.attackTicks == 58) {
                        this.caughtEntity.hurt(this.damageSources().mobAttack(this), 25.0F);
                        this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 0.5F);
                        float pushYaw = this.getYRot() * ((float) Math.PI / 180F);
                        this.caughtEntity.setDeltaMovement(-Mth.sin(pushYaw) * 2.5, 0.5, Mth.cos(pushYaw) * 2.5);
                        this.caughtEntity.hasImpulse = true;
                        this.caughtEntity = null;
                        this.setCaughtEntityId(-1);
                    }
                }
                break;
            case STATE_GUARD_START:
                if (this.attackTicks == 20) {
                    this.isGuarding = true;
                    this.guardAxeHits = 0;
                    this.guardDamageTaken = 0;
                }
                break;
            case STATE_GUARD_LOOP:
                this.isGuarding = true;
                if (target != null) {
                    this.lookAt(target, 30.0F, 30.0F);
                    this.yBodyRot = this.getYRot();
                }
                break;
            case STATE_GUARD_BREAK:
                this.isGuarding = false;
                break;
            case STATE_GUARD_END:
                this.isGuarding = false;
                break;
        }
        if (state != STATE_UPPERCUT_HORIZONTAL && state != STATE_UPPERCUT_VERTICAL) {
            if (this.isNoGravity())
                this.setNoGravity(false);
        }
    }

    private void handlePhaseChangeAction() {
        this.getNavigation().stop();
        this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();
        this.hurtTime = 0;
        if (!this.level().isClientSide()) {
            this.phaseChangeTicks++;
            if (this.phaseChangeTicks >= 96) {
                this.phaseChangeTicks = 0;
                this.setAttackState(0);
                this.setJabCooldown(20);
                this.setUppercutCooldown(30);
                this.setChargeCooldown(50);
                this.setRockCooldown(60);
            }
        }
    }

    private void handleChargeMovement() {
        LivingEntity target = this.getTarget();
        if (target != null) {
            float targetYaw = (float) (Mth.atan2(target.getZ() - this.getZ(), target.getX() - this.getX())
                    * (180D / Math.PI)) - 90.0F;
            float rotationStep = this.distanceTo(target) > 5.0f ? 10.0F : 5.0F;
            this.setYRot(Mth.approachDegrees(this.getYRot(), targetYaw, rotationStep));
            this.yBodyRot = this.getYRot();
        }
        float yaw = this.getYRot() * ((float) Math.PI / 180F);
        Vec3 currentMotion = this.getDeltaMovement();
        double chargeSpeed = this.attackTicks > 26 ? 0.55D : 0.9D;
        Vec3 push = new Vec3(-Mth.sin(yaw), 0, Mth.cos(yaw)).scale(chargeSpeed);
        double frictionWeight = this.attackTicks > 26 ? 0.35D : 0.18D;
        double pushWeight = 1.0D - frictionWeight;
        this.setDeltaMovement(
                currentMotion.x * frictionWeight + push.x * pushWeight,
                currentMotion.y,
                currentMotion.z * frictionWeight + push.z * pushWeight);
        this.hasImpulse = true;
        this.destroyBlocksInAABB(this.getBoundingBox().inflate(1.5D, 1.0D, 1.5D));
    }

    private void handleRockLoopMovement() {
        LivingEntity target = this.getTarget();
        if (target != null) {
            float targetYaw = (float) (Mth.atan2(target.getZ() - this.getZ(), target.getX() - this.getX())
                    * (180D / Math.PI)) - 90.0F;
            this.setYRot(Mth.approachDegrees(this.getYRot(), targetYaw, 15.0F));
            this.yBodyRot = this.getYRot();
            double dist = this.distanceTo(target);
            if (dist > 6.0D) {
                float yaw = this.getYRot() * ((float) Math.PI / 180F);
                double speed = dist > 14.0D ? 0.72D : 0.52D;
                this.setDeltaMovement(-Mth.sin(yaw) * speed, this.getDeltaMovement().y, Mth.cos(yaw) * speed);
            } else {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, 1.0, 0.8));
            }
        }
    }

    private void handleBlinkMovement(LivingEntity target) {
        if (target == null) {
            this.setAttackState(0);
            this.rockProjectileHit = false;
            return;
        }
        Vec3 dir = target.position().subtract(this.position()).normalize();
        this.setDeltaMovement(dir.x * 1.6, this.getDeltaMovement().y, dir.z * 1.6);
        this.lookAt(target, 30.0F, 30.0F);
        this.yBodyRot = this.getYRot();
        this.hasImpulse = true;
        if (this.distanceTo(target) < 4.0D || this.attackTicks >= 15) {
            this.rockProjectileHit = false;
            this.setAttackState(this.getRandom().nextBoolean() ? STATE_UPPERCUT : STATE_JAB_1);
            this.setDeltaMovement(this.getDeltaMovement().scale(0.1));
        }
    }

    public void performRockThrow() {
        LivingEntity target = this.getTarget();
        if (this.level().isClientSide)
            return;
        BlockState rockState = net.minecraft.world.level.block.Blocks.MAGMA_BLOCK.defaultBlockState();
        Cm_Falling_Block_Entity rock = new Cm_Falling_Block_Entity(this.level(), this.getX(), this.getY() + 2.5D,
                this.getZ(), rockState, 40);
        Vec3 dir = this.getLookAngle();
        if (target != null) {
            dir = target.position().add(0, target.getBbHeight() * 0.5, 0).subtract(this.position().add(0, 2.5, 0))
                    .normalize();
        }
        rock.setDeltaMovement(dir.scale(1.5D));
        this.level().addFreshEntity(rock);
        this.spawnRockExcavationDebris(dir);
        this.playSound(SoundEvents.GHAST_SHOOT, 1.5F, 0.5F);
        if (target != null && this.distanceTo(target) < 16) {
            this.rockProjectileHit = true;
        }
    }

    public boolean isRockReady() {
        return this.rockCooldown <= 0;
    }

    private void spawnRockExcavationDebris(Vec3 direction) {
        if (this.level().isClientSide)
            return;
        Vec3 forward = new Vec3(direction.x, 0.0D, direction.z);
        if (forward.lengthSqr() < 1.0E-4D) {
            forward = this.getLookAngle().multiply(1.0D, 0.0D, 1.0D);
        }
        forward = forward.normalize();
        Vec3 side = new Vec3(-forward.z, 0.0D, forward.x);
        for (int i = 0; i < 18; i++) {
            double forwardOffset = 0.8D + this.getRandom().nextDouble() * 4.6D;
            double sideOffset = (this.getRandom().nextDouble() - 0.5D) * 4.2D;
            double px = this.getX() + forward.x * forwardOffset + side.x * sideOffset;
            double pz = this.getZ() + forward.z * forwardOffset + side.z * sideOffset;
            BlockPos pos = BlockPos.containing(px, this.getY() - 1, pz);
            while (this.level().isEmptyBlock(pos) && pos.getY() > this.level().getMinBuildHeight()) {
                pos = pos.below();
            }
            BlockState state = this.level().getBlockState(pos);
            if (state.isAir() || state.getRenderShape() != RenderShape.MODEL) {
                state = net.minecraft.world.level.block.Blocks.MAGMA_BLOCK.defaultBlockState();
            }
            Cm_Falling_Block_Entity debris = new Cm_Falling_Block_Entity(this.level(), px, pos.getY() + 1.0D, pz, state,
                    18 + this.getRandom().nextInt(16));
            double outward = 0.18D + this.getRandom().nextDouble() * 0.28D;
            debris.setDeltaMovement(
                    forward.x * outward + side.x * sideOffset * 0.06D,
                    0.25D + this.getRandom().nextDouble() * 0.35D,
                    forward.z * outward + side.z * sideOffset * 0.06D);
            this.level().addFreshEntity(debris);
        }
    }

    private void handleShockwaveAction() {
        if (this.attackTicks < 20 && this.getTarget() != null) {
            float targetYaw = (float) (Mth.atan2(this.getTarget().getZ() - this.getZ(),
                    this.getTarget().getX() - this.getX()) * (180D / Math.PI)) - 90.0F;
            this.setYRot(Mth.approachDegrees(this.getYRot(), targetYaw, 10.0F));
            this.yBodyRot = this.getYRot();
        }
        this.setDeltaMovement(this.getDeltaMovement().multiply(0.45, 1.0, 0.45));
        if (this.attackTicks == 20) {
            this.playSound(SoundEvents.GENERIC_EXPLODE, 1.5F, 0.6F);
            this.performJabDamage(2.5f, 1.5f);
            this.spawnFallingBlockShockwave(18, 58.0F);
            this.setChargeCooldown(100);
        }
    }

    private void spawnFallingBlockShockwave(int length, float arc) {
        if (this.level().isClientSide)
            return;
        float centerYaw = this.getYRot();
        List<LivingEntity> alreadyHit = new java.util.ArrayList<>();
        for (int d = 1; d <= length; d++) {
            float arcRad = (float) Math.toRadians(arc);
            int points = Mth.ceil(d * arcRad * 1.2f) + this.getRandom().nextInt(2);
            for (int i = 0; i <= points; i++) {
                if (d > 3 && this.getRandom().nextFloat() < 0.22F)
                    continue;
                float angleOffset = (points == 0) ? 0 : ((float) i / (float) points - 0.5f) * arc;
                angleOffset += (this.getRandom().nextFloat() - 0.5F) * 7.5F;
                double distance = d + (this.getRandom().nextDouble() - 0.5D) * 0.65D;
                double rad = Math.toRadians(centerYaw + 90.0f + angleOffset);
                double px = this.getX() + Math.cos(rad) * distance;
                double pz = this.getZ() + Math.sin(rad) * distance;
                BlockPos pos = BlockPos.containing(px, this.getY() - 1, pz);
                while (this.level().isEmptyBlock(pos) && pos.getY() > this.level().getMinBuildHeight())
                    pos = pos.below();
                BlockState state = this.level().getBlockState(pos);
                if (!state.isAir() && state.getRenderShape() == RenderShape.MODEL) {
                    Cm_Falling_Block_Entity falling = new Cm_Falling_Block_Entity(this.level(), px, pos.getY() + 1.0D,
                            pz, state, 16 + this.getRandom().nextInt(15));
                    falling.push(0, 0.12D + this.getRandom().nextDouble() * 0.22D + (d * 0.008D), 0);
                    this.level().addFreshEntity(falling);
                }
                AABB hitBox = new AABB(px - 1.25, this.getY() - 1.0, pz - 1.25, px + 1.25, this.getY() + 3.0,
                        pz + 1.25);
                for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, hitBox)) {
                    if (target != this && !this.isAlliedTo(target) && !alreadyHit.contains(target)) {
                        if (target.hurt(this.damageSources().mobAttack(this),
                                (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5f))) {
                            target.setDeltaMovement(target.getDeltaMovement().add(0, 0.6D, 0));
                            target.hasImpulse = true;
                            alreadyHit.add(target);
                        }
                    }
                }
            }
        }
    }

    private void teleportToTarget(LivingEntity target) {
        this.showAfterUppercutAmbush();
        float targetYaw = target.getYRot() * ((float) Math.PI / 180F);
        double offsetX = Math.cos(targetYaw) * 2.0;
        double offsetZ = Math.sin(targetYaw) * 2.0;
        double targetY = target.getY() + (target.getBbHeight() * 0.5) - (this.getBbHeight() * 0.5);
        this.moveTo(target.getX() + offsetX, targetY, target.getZ() + offsetZ);
        this.lookAt(target, 360f, 360f);
        this.yBodyRot = this.getYRot();
        this.yRotO = this.getYRot();
        if (this.attackTicks == 1) {
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.2F);
        }
    }

    public boolean wasUppercutHit() {
        return this.uppercutHit;
    }

    private void hideForUppercutAmbush() {
        if (!this.uppercutAmbushHidden) {
            this.uppercutAmbushHidden = true;
            this.setInvisible(true);
            this.setDeltaMovement(Vec3.ZERO);
        }
    }

    private void showAfterUppercutAmbush() {
        if (this.uppercutAmbushHidden) {
            this.uppercutAmbushHidden = false;
            this.setInvisible(false);
        }
    }

    public void performUppercutDamage(float damageMultiplier) {
        this.uppercutHit = this.performForwardArcDamage(damageMultiplier, 4.75F, 120.0F, 0.55F, 1.8D);
    }

    public void performOverheadDamage() {
        if (this.level().isClientSide)
            return;
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(3.0D, 2.0D, 3.0D));
        for (LivingEntity target : targets) {
            if (this.canDamageTarget(target) && this.isInFrontArc(target, 120) && this.distanceTo(target) <= 3.0D + this.getBbWidth()) {
                float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.5F;
                if (target.isBlocking()) {
                    damage *= 0.8F;
                    if (target instanceof Player player) {
                        player.getCooldowns().addCooldown(player.getUseItem().getItem(), 100);
                        player.stopUsingItem();
                        this.level().broadcastEntityEvent(player, (byte) 30);
                    }
                }
                if (target.hurt(this.damageSources().magic(), damage)) {
                    this.applyAttackKnockback(target, 1.5F, 0.0D, 0.0D);
                    this.heal(damage * 0.15F);
                }
            }
        }
    }

    public void performJabDamage(float damageMultiplier, float knockback) {
        if (this.level().isClientSide)
            return;
        this.performForwardArcDamage(damageMultiplier, 3.0F, 120.0F, knockback, 0.0D);
    }

    public boolean isTargetBehind() {
        LivingEntity target = this.getTarget();
        if (target == null)
            return false;
        Vec3 toTarget = target.position().subtract(this.position()).normalize();
        Vec3 forward = Vec3.directionFromRotation(0.0F, this.getYRot()).normalize();
        double dotProduct = toTarget.dot(forward);
        return dotProduct < -0.2D;
    }

    public void performComboDamage(float damageMultiplier, float range, float arc, float knockback, double forwardPush,
            double verticalImpulse) {
        this.performForwardArcDamage(damageMultiplier, range, arc, knockback, forwardPush, verticalImpulse);
    }

    public boolean performAreaDamage(float damageMultiplier, float knockback, double xzRange, double yRange,
            double forwardPush, double verticalImpulse) {
        if (this.level().isClientSide)
            return false;
        boolean hit = false;
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(xzRange, yRange, xzRange));
        for (LivingEntity target : targets) {
            if (this.canDamageTarget(target) && this.distanceTo(target) <= xzRange + this.getBbWidth()) {
                float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier;
                if (target.hurt(this.damageSources().mobAttack(this), damage)) {
                    this.applyAttackKnockback(target, knockback, forwardPush, verticalImpulse);
                    this.heal(damage * 0.15F);
                    hit = true;
                }
            }
        }
        return hit;
    }

    private boolean performForwardArcDamage(float damageMultiplier, float range, float arcAngle, float knockback,
            double knockbackY) {
        return this.performForwardArcDamage(damageMultiplier, range, arcAngle, knockback, 0.0D, knockbackY);
    }

    private boolean performForwardArcDamage(float damageMultiplier, float range, float arc, float knockback,
            double forwardPush, double verticalImpulse) {
        if (this.level().isClientSide)
            return false;
        boolean hit = false;
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(range, 2.0D, range));
        for (LivingEntity target : targets) {
            if (this.canDamageTarget(target) && this.isInFrontArc(target, arc) && this.distanceTo(target) <= range + this.getBbWidth()) {
                float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier;
                if (target.hurt(this.damageSources().mobAttack(this), damage)) {
                    this.applyAttackKnockback(target, knockback, forwardPush, verticalImpulse);
                    this.heal(damage * 0.15F);
                    hit = true;
                }
            }
        }
        return hit;
    }

    private boolean canDamageTarget(LivingEntity target) {
        return target != this && this.isAlive() && target.isAlive() && this.canAttack(target)
                && !this.isAlliedTo(target);
    }

    private boolean isInFrontArc(LivingEntity target, float arc) {
        float angleToTarget = (float) (Mth.atan2(target.getZ() - this.getZ(), target.getX() - this.getX())
                * (180D / Math.PI)) - 90.0F;
        return Mth.degreesDifferenceAbs(this.yBodyRot, angleToTarget) <= arc / 2.0F;
    }

    private void applyAttackKnockback(LivingEntity target, float knockback, double forwardPush,
            double verticalImpulse) {
        float yaw = this.yBodyRot * ((float) Math.PI / 180.0F);
        if (knockback > 0.0F) {
            target.knockback(knockback, Math.sin(yaw), -Math.cos(yaw));
        }
        if (forwardPush != 0.0D || verticalImpulse != 0.0D) {
            Vec3 push = new Vec3(-Mth.sin(yaw) * forwardPush, verticalImpulse, Mth.cos(yaw) * forwardPush);
            target.setDeltaMovement(target.getDeltaMovement().add(push));
        }
        target.hasImpulse = true;
    }

    private boolean destroyBlocksInAABB(net.minecraft.world.phys.AABB aabb) {
        if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this)) {
            return false;
        }
        boolean flag = false;
        int minX = Mth.floor(aabb.minX);
        int minY = Mth.floor(Math.max(aabb.minY, this.getY() + 0.1D));
        int minZ = Mth.floor(aabb.minZ);
        int maxX = Mth.floor(aabb.maxX);
        int maxY = Mth.floor(aabb.maxY);
        int maxZ = Mth.floor(aabb.maxZ);
        for(int x = minX; x <= maxX; ++x) {
            for(int y = minY; y <= maxY; ++y) {
                for(int z = minZ; z <= maxZ; ++z) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = this.level().getBlockState(pos);
                    if (!state.isAir() && state.canEntityDestroy(this.level(), pos, this) && net.minecraftforge.common.ForgeHooks.canEntityDestroy(this.level(), pos, this)) {
                        flag = this.level().destroyBlock(pos, true, this) || flag;
                    }
                }
            }
        }
        return flag;
    }

    private void spawnFlameStrike(double x, double z, double minY, double maxY, float rotation, int duration, int wait,
            int delay, float radius, boolean soul) {
        FlameStrikeSpawner.builder(this.level(), x, z)
                .yRange(minY, maxY)
                .rotation(rotation)
                .duration(duration)
                .waitTime(wait)
                .warmupDelay(delay)
                .radius(radius)
                .damage(soul ? 8.0F : 6.0F)
                .hpDamage(6.0F)
                .soul(soul)
                .owner(this)
                .spawn();
    }

    private void sendBossMessage(String translationKey) {
        if (this.level().isClientSide()) return;
        Component message = Component.translatable(translationKey);
        for (Player player : this.level().players()) {
            if (this.distanceToSqr(player) <= 50 * 50) {
                player.sendSystemMessage(message);
            }
        }
    }

}
