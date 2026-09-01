package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.AI.InternalMoveGoal;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.IABoss_monster;
import com.github.L_Ender.cataclysm.entity.effect.Cm_Falling_Block_Entity;
import com.github.L_Ender.cataclysm.entity.etc.CMBossInfoServer;
import com.github.L_Ender.cataclysm.entity.etc.IHoldEntity;
import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.api.config.IgnisPrimeConfig;
import com.maxwell.cataclysm_primed_soul.api.entity.IShaderBoss;
import com.maxwell.cataclysm_primed_soul.entity.EntityDamageHelper;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.goal.*;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.sub.FlameStrikeSpawner;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.sub.Prime_Fireball_Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

@SuppressWarnings("removal")
public class Ignis_PrimeEntity extends IABoss_monster implements IHoldEntity, IShaderBoss {
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
    public static final int STATE_ULTRACHARGE = 33;
    public static final int STATE_ULTRACHARGE_LIKEAMMO = 34;
    public static final int STATE_ULTRACHARGE_STRIKING = 35;
    public static final int STATE_ULTRACHARGE_STRIKING_END = 36;
    public static final int STATE_DASH = 40;
    public static final int STATE_DASH_UPPER = 41;
    public static final int STATE_GUARD_COUNTER = 42;
    public static final int STATE_JUMP_START = 43;
    public static final int STATE_JUMP_FALL_LOOP = 44;
    public static final int STATE_JUMP_END = 45;
    public static final int STATE_DASH_ATTACK_COMBO = 46;
    public static final int STATE_PHASE_CHANGE = 99;
    private static final double TARGETING_RANGE = 100.0D;
    private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(Ignis_PrimeEntity.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> SPEED_MULTIPLIER = SynchedEntityData
            .defineId(Ignis_PrimeEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> CAUGHT_ENTITY_ID = SynchedEntityData
            .defineId(Ignis_PrimeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HURT_FLASH_TICKS = SynchedEntityData
            .defineId(Ignis_PrimeEntity.class, EntityDataSerializers.INT);
    private static final ResourceLocation SHADER = new ResourceLocation(Primed_Soul.MODID, "shaders/post/ignis_debuff.json");
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
    public AnimationState power_slamAnimationState = new AnimationState();
    public AnimationState guard_startAnimationState = new AnimationState();
    public AnimationState guard_loopAnimationState = new AnimationState();
    public AnimationState guard_breakAnimationState = new AnimationState();
    public AnimationState guard_counterAnimationState = new AnimationState();
    public AnimationState guard_endAnimationState = new AnimationState();
    public AnimationState catch_startAnimationState = new AnimationState();
    public AnimationState catch_successAnimationState = new AnimationState();
    public AnimationState catch_failAnimationState = new AnimationState();
    public AnimationState dashAnimationState = new AnimationState();
    public AnimationState dash_upperAnimationState = new AnimationState();
    public AnimationState jab_attack_ex_oneAnimationState = new AnimationState();
    public AnimationState overhead_guardbreakerAnimationState = new AnimationState();
    public AnimationState ultracharge_chargeAnimationState = new AnimationState();
    public AnimationState ultracharge_likeammoAnimationState = new AnimationState();
    public AnimationState ultracharge_striking_AnimationState = new AnimationState();
    public AnimationState ultracharge_striking_end_AnimationState = new AnimationState();
    public AnimationState jump_attack_start_AnimationState = new AnimationState();
    public AnimationState jump_attack_fall_loop_AnimationState = new AnimationState();
    public AnimationState jump_attack_end_AnimationState = new AnimationState();
    public AnimationState deadAnimationState = new AnimationState();
    public AnimationState mode_changeAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public boolean rockProjectileHit = false;
    public boolean isGuarding = false;
    public boolean dashUpperHit = false;
    public int totalAttacksMade = 0;
    public int jabComboCount = 0;
    public int ticksSinceLastHurt = 0;
    public float recentDamageTaken = 0.0F;
    public int catchCooldown = 0;
    public int overheadCooldown = 0;
    public int guardCooldown = 0;
    public int phaseChangeTicks = 0;
    private int lastAttackState = 0;
    private Vec3 uppercutStartPos = null;
    private int uppercutReappearTicks = 0;
    private boolean hasSentAppearMessage = false;
    private boolean hasSentHalfHpMessage = false;
    private boolean hasSentPhase2Message = false;
    private int targetShieldingTicks = 0;
    private boolean uppercutHit = false;
    private boolean uppercutAmbushHidden = false;
    private int lastPhaseTick = 0;
    private int jabCooldown = 0;
    private int uppercutCooldown = 0;
    private int chargeCooldown = 0;
    private int dashCooldown = 0;
    private int jumpCooldown = 0;
    private int rockCooldown = 0;
    private int guardAxeHits = 0;
    private float guardDamageTaken = 0.0F;
    private boolean guardCounterPrimed = false;
    private Entity caughtEntity = null;
    private double storedY = 0.0D;
    private Vec3 slamPos = null;
    private Vec3 ultrachargeLaunchVelocity = Vec3.ZERO;
    private boolean harmlessJumpAttack = false;
    private int ultCooldown = 0;

    public Ignis_PrimeEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.bossEvent = new CMBossInfoServer(this.getDisplayName(), BossEvent.BossBarColor.YELLOW, true, 99);
        this.xpReward = IgnisPrimeConfig.XP_REWARD.get();
        this.setMaxUpStep(2.0F);
        if (!pLevel.isClientSide()) {
            java.util.Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH))
                    .setBaseValue(IgnisPrimeConfig.MAX_HEALTH.get());
            java.util.Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE))
                    .setBaseValue(IgnisPrimeConfig.ATTACK_DAMAGE.get());
            java.util.Objects.requireNonNull(this.getAttribute(Attributes.ARMOR))
                    .setBaseValue(IgnisPrimeConfig.ARMOR.get());
            java.util.Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED))
                    .setBaseValue(IgnisPrimeConfig.MOVEMENT_SPEED.get());
            java.util.Objects.requireNonNull(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE))
                    .setBaseValue(IgnisPrimeConfig.KNOCKBACK_RESISTANCE.get());
            this.setHealth((float) IgnisPrimeConfig.MAX_HEALTH.get());
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 600.0D)
                .add(Attributes.FOLLOW_RANGE, TARGETING_RANGE)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 20.0D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    public boolean canBeAffected(net.minecraft.world.effect.MobEffectInstance effect) {
        return false;
    }

    @Override
    public ResourceLocation getDebuffShader() {
        return SHADER;
    }

    @Override
    public int getDebuffLevel() {
        float hpPct = this.getHealth() / this.getMaxHealth();
        if (this.getBossPhase() >= 2) return 3;
        if (hpPct <= 0.5F) return 2;
        return 1;
    }

    @Override
    public double getDebuffRangeSq() {
        return 80.0D * 80.0D;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        this.showAfterUppercutAmbush();
        this.setDeltaMovement(Vec3.ZERO);
        this.setAttackState(STATE_DEATH);
        if (this.caughtEntity != null) {
            this.caughtEntity.stopRiding();
        }
        this.caughtEntity = null;
        this.setCaughtEntityId(-1);
        if (!this.level().isClientSide()) {
            IgnisDebuffManager.unregisterBoss(this);
        }
    }

    @Override
    public void setHealth(float health) {
        if (this.getBossPhase() < 2 && health <= 1.0F) {
            super.setHealth(1.0F);
            return;
        }
        super.setHealth(health);
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        super.remove(reason);
        if (this.caughtEntity != null) {
            this.caughtEntity.stopRiding();
        }
        this.caughtEntity = null;
        this.setCaughtEntityId(-1);
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

    public void setDashCooldown(int ticks) {
        this.dashCooldown = ticks;
    }

    public boolean isDashReady() {
        return this.dashCooldown <= 0;
    }

    public void setJumpCooldown(int ticks) {
        this.jumpCooldown = ticks;
    }

    public boolean isJumpReady() {
        return this.jumpCooldown <= 0;
    }

    public void startSpawnJumpAttack() {
        this.harmlessJumpAttack = true;
        this.setAttackState(STATE_JUMP_START);
        this.attackTicks = 0;
        this.setNoGravity(false);
        this.setDeltaMovement(Vec3.ZERO);
    }

    public boolean isJumpAttackState() {
        return this.isJumpAttackState(this.getAttackState());
    }

    private boolean isJumpAttackState(int state) {
        return state == STATE_JUMP_START || state == STATE_JUMP_FALL_LOOP || state == STATE_JUMP_END;
    }

    @Override
    public void setAttackState(int state) {
        if ((this.isDeadOrDying() || !this.isAlive()) && state != STATE_DEATH) {
            return;
        }
        int current = this.getAttackState();
        if (this.isJumpAttackState(current)
                && state != current
                && state != STATE_DEATH
                && !this.isJumpAttackState(state)
                && !(current == STATE_JUMP_END && state == 0)) {
            return;
        }
        super.setAttackState(state);
        this.attackTicks = 0;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHASE, 0);
        this.entityData.define(SPEED_MULTIPLIER, 1.2F);
        this.entityData.define(CAUGHT_ENTITY_ID, -1);
        this.entityData.define(HURT_FLASH_TICKS, 0);
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
        } else if (input == "guard_counter") {
            return this.guard_counterAnimationState;
        } else if (input == "guard_end") {
            return this.guard_endAnimationState;
        } else if (input == "catch_start") {
            return this.catch_startAnimationState;
        } else if (input == "catch_success") {
            return this.catch_successAnimationState;
        } else if (input == "catch_fail") {
            return this.catch_failAnimationState;
        } else if (input == "dash") {
            return this.dashAnimationState;
        } else if (input == "dash_upper") {
            return this.dash_upperAnimationState;
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
        } else if (input == "ultracharge_charge") {
            return this.ultracharge_chargeAnimationState;
        } else if (input == "ultracharge_likeammo") {
            return this.ultracharge_likeammoAnimationState;
        } else if (input == "ultracharge_striking") {
            return this.ultracharge_striking_AnimationState;
        } else if (input == "ultracharge_striking_end") {
            return this.ultracharge_striking_end_AnimationState;
        } else if (input == "jump_attack_start") {
            return this.jump_attack_start_AnimationState;
        } else if (input == "jump_attack_fall_loop") {
            return this.jump_attack_fall_loop_AnimationState;
        } else if (input == "jump_attack_end") {
            return this.jump_attack_end_AnimationState;
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
        this.guard_counterAnimationState.stop();
        this.guard_endAnimationState.stop();
        this.guard_loopAnimationState.stop();
        this.catch_startAnimationState.stop();
        this.catch_successAnimationState.stop();
        this.catch_failAnimationState.stop();
        this.dash_upperAnimationState.stop();
        this.dashAnimationState.stop();
        this.overhead_guardbreakerAnimationState.stop();
        this.jab_attack_ex_oneAnimationState.stop();
        this.mode_changeAnimationState.stop();
        this.ultracharge_striking_AnimationState.stop();
        this.ultracharge_striking_end_AnimationState.stop();
        this.ultracharge_chargeAnimationState.stop();
        this.ultracharge_likeammoAnimationState.stop();
        this.jump_attack_start_AnimationState.stop();
        this.jump_attack_fall_loop_AnimationState.stop();
        this.jump_attack_end_AnimationState.stop();
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
        applyPhaseBalance();
    }

    private void applyPhaseBalance() {
        int phase = this.getBossPhase();
        float speed = phase >= 2 ? 1.05F : (phase == 1 ? 1.30F : 1.18F);
        if (Math.abs(this.getAttackSpeedMultiplier() - speed) > 0.001F) {
            this.setAttackSpeedMultiplier(speed);
        }
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
    public void positionRider(Entity passenger, Entity.MoveFunction moveFunc) {
        if (this.hasPassenger(passenger)) {
            float yaw = this.yBodyRot * ((float) Math.PI / 180F);
            double offsetX = -Mth.sin(yaw) * 1.5;
            double offsetZ = Mth.cos(yaw) * 1.5;
            double height = 1.2;
            int ticks = this.attackTicks;
            if ((ticks >= 30 && ticks <= 34) || (ticks >= 49 && ticks <= 53) || (ticks >= 59 && ticks <= 63)) {
                height = 0.2;
            }
            moveFunc.accept(passenger, this.getX() + offsetX, this.getY() + height, this.getZ() + offsetZ);
        }
    }

    @Override
    public float DamageCap() {
        return 30.0F;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        int state = this.getAttackState();
        if (state == STATE_ULTRACHARGE || state == STATE_ULTRACHARGE_LIKEAMMO || state == STATE_ULTRACHARGE_STRIKING) {
            return false;
        }
        if (this.getAttackState() == STATE_CATCH_SUCCESS) {
            if (source.getEntity() != null && source.getEntity() == this.getCaughtEntity()) {
                return false;
            }
            amount *= 0.2F;
        }
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
        if (!isGenericKill) {
            amount = Math.min(amount, this.DamageCap());
        }
        if (!isGenericKill && this.getAttackState() == 0 && source.getEntity() instanceof LivingEntity attacker) {
            if (this.random.nextFloat() < 0.15F && this.dashCooldown <= 0) {
                double angle = this.getYRot() * (Math.PI / 180.0D) + (this.random.nextBoolean() ? Math.PI / 2.0D : -Math.PI / 2.0D);
                double dx = -Math.sin(angle) * 3.5D;
                double dz = Math.cos(angle) * 3.5D;
                this.teleportTo(this.getX() + dx, this.getY(), this.getZ() + dz);
                this.lookAt(attacker, 360.0F, 360.0F);
                this.yBodyRot = this.getYRot();
                this.yRotO = this.getYRot();
                this.setDeltaMovement(Vec3.ZERO);
                this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.5F);
                if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.PORTAL, this.getX(), this.getY() + 1.0D, this.getZ(), 15, 0.3D, 0.3D, 0.3D, 0.1D);
                    serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY() + 0.5D, this.getZ(), 10, 0.2D, 0.2D, 0.2D, 0.05D);
                }
                this.setAttackState(STATE_DASH_UPPER);
                this.setDashCooldown(60);
                this.clearHurtFlash();
                return false;
            }
        }
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
                float maxDurability = (this.getTarget() instanceof net.minecraft.world.entity.player.Player)
                        ? IgnisPrimeConfig.GUARD_MAX_DAMAGE_PLAYER.get()
                        : IgnisPrimeConfig.GUARD_MAX_DAMAGE_NON_PLAYER.get();
                if (this.guardAxeHits >= IgnisPrimeConfig.GUARD_MAX_AXE_HITS.get() || this.guardDamageTaken >= maxDurability) {
                    this.setAttackState(STATE_GUARD_BREAK);
                    this.isGuarding = false;
                    this.playSound(SoundEvents.SHIELD_BREAK, 1.0F, 0.8F);
                } else {
                    this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 0.8F + this.random.nextFloat() * 0.2F);
                    if (!this.level().isClientSide) {
                        com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToClients(new com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect(this.getId(), 4));
                        if (this.guardCounterPrimed) {
                            this.setAttackState(STATE_GUARD_COUNTER);
                            this.isGuarding = false;
                            this.guardCounterPrimed = false;
                        }
                    }
                }
                return false;
            }
        }
        if (!isGenericKill && amount > 45.0F) {
            amount = 45.0F + (amount - 45.0F) * 0.20F;
        }
        boolean hurt = super.hurt(source, amount);
        if (hurt) {
            if (source.getEntity() != null) {
                this.setHurtFlashTicks(10);
            }
        }
        return hurt;
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

    private float scaleDirectDamage(float damage) {
        return damage * (this.isPrimeSecondForm() ? 0.45F : 1.05F);
    }

    private float scaleEnvironmentalDamage(float damage) {
        return damage * (this.isPrimeSecondForm() ? 0.40F : 0.95F);
    }

    public boolean shouldRenderHurtFlash() {
        int state = this.getAttackState();
        if (state == STATE_PHASE_CHANGE || state == STATE_DEATH) {
            return false;
        }
        return this.entityData.get(HURT_FLASH_TICKS) > 0 || this.deathTime > 0;
    }

    public int getHurtFlashTicks() {
        return this.entityData.get(HURT_FLASH_TICKS);
    }

    private void setHurtFlashTicks(int ticks) {
        this.entityData.set(HURT_FLASH_TICKS, ticks);
    }

    private void clearHurtFlash() {
        this.setHurtFlashTicks(0);
        this.hurtTime = 0;
    }

    private boolean isValidTarget(LivingEntity target) {
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (target instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return false;
        }
        return this.distanceToSqr(target) <= TARGETING_RANGE * TARGETING_RANGE;
    }

    private Player findNearestValidPlayer(double range) {
        Player nearestPlayer = null;
        double nearestDistanceSqr = range * range;
        List<Player> players = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(range));
        for (Player player : players) {
            if (player.isAlive() && !player.isCreative() && !player.isSpectator()) {
                double distanceSqr = this.distanceToSqr(player);
                if (distanceSqr <= nearestDistanceSqr) {
                    nearestDistanceSqr = distanceSqr;
                    nearestPlayer = player;
                }
            }
        }
        return nearestPlayer;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            LivingEntity target = this.getTarget();
            if (!this.isValidTarget(target)) {
                Player nearestPlayer = this.findNearestValidPlayer(TARGETING_RANGE);
                if (nearestPlayer != null) {
                    this.setTarget(nearestPlayer);
                    target = nearestPlayer;
                } else if (target != null) {
                    this.setTarget(null);
                    target = null;
                }
            }
            int currentAttackState = this.getAttackState();
            if (currentAttackState != STATE_CATCH_START && currentAttackState != STATE_CATCH_SUCCESS && this.caughtEntity != null) {
                this.caughtEntity.stopRiding();
                this.caughtEntity = null;
                this.setCaughtEntityId(-1);
            }
            if (!this.getPassengers().isEmpty() && this.getAttackState() == STATE_CATCH_SUCCESS) {
                Entity passenger = this.getPassengers().get(0);
                if (passenger.isShiftKeyDown()) {
                    passenger.setShiftKeyDown(false);
                }
            }
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
                }
            }
            IgnisDebuffManager.tickBossDebuffs(this);
            if (target != null && target.isUsingItem() && (target.getUseItem().getItem() instanceof net.minecraft.world.item.ShieldItem || target.getUseItem().canPerformAction(net.minecraftforge.common.ToolActions.SHIELD_BLOCK))) {
                this.targetShieldingTicks++;
                if (this.targetShieldingTicks >= IgnisPrimeConfig.TARGET_SHIELDING_THRESHOLD.get() && this.getAttackState() == 0 && this.overheadCooldown <= 0) {
                    this.setAttackState(STATE_OVERHEAD_GUARDBREAKER);
                    this.attackTicks = 0;
                    this.targetShieldingTicks = 0;
                    this.overheadCooldown = IgnisPrimeConfig.OVERHEAD_COOLDOWN.get();
                }
            } else {
                this.targetShieldingTicks = 0;
            }
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
            if (this.uppercutAmbushHidden &&
                    currentAttackState != STATE_UPPERCUT &&
                    currentAttackState != STATE_UPPERCUT_HORIZONTAL &&
                    currentAttackState != STATE_UPPERCUT_VERTICAL &&
                    currentAttackState != STATE_ULTRACHARGE &&
                    currentAttackState != STATE_ULTRACHARGE_LIKEAMMO &&
                    currentAttackState != STATE_ULTRACHARGE_STRIKING &&
                    currentAttackState != STATE_ULTRACHARGE_STRIKING_END) {
                this.showAfterUppercutAmbush();
            }
        }
        this.ticksSinceLastHurt++;
        if (this.catchCooldown > 0)
            this.catchCooldown--;
        if (this.ultCooldown > 0) this.ultCooldown--;
        if (this.overheadCooldown > 0)
            this.overheadCooldown--;
        if (this.guardCooldown > 0)
            this.guardCooldown--;
        if (this.recentDamageTaken > 0) {
            this.recentDamageTaken -= 0.5F;
            if (this.recentDamageTaken < 0)
                this.recentDamageTaken = 0.0F;
        }
        int hurtFlashTicks = this.entityData.get(HURT_FLASH_TICKS);
        if (hurtFlashTicks > 0) {
            this.setHurtFlashTicks(hurtFlashTicks - 1);
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
            if (this.dashCooldown > 0)
                this.dashCooldown--;
            if (this.jumpCooldown > 0)
                this.jumpCooldown--;
            if (this.getTarget() == null && this.getHealth() < this.getMaxHealth() && this.tickCount % 20 == 0) {
                this.heal((float) IgnisPrimeConfig.NATURAL_HEAL_AMOUNT.get());
            }
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
            updateBossPhase();
            if (getBossPhase() == 2 && this.getAttackState() != STATE_PHASE_CHANGE) {
                lastPhaseTick++;
                if (lastPhaseTick >= IgnisPrimeConfig.PHASE2_DRAIN_INTERVAL.get()) {
                    float drain = this.getMaxHealth() * (float) IgnisPrimeConfig.PHASE2_DRAIN_PERCENT.get();
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
            this.attackTicks = 0;
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
                case STATE_ULTRACHARGE -> this.ultracharge_chargeAnimationState.start(this.tickCount);
                case STATE_ULTRACHARGE_LIKEAMMO -> this.ultracharge_likeammoAnimationState.start(this.tickCount);
                case STATE_ULTRACHARGE_STRIKING -> this.ultracharge_striking_AnimationState.start(this.tickCount);
                case STATE_DASH -> this.dashAnimationState.start(this.tickCount);
                case STATE_DASH_UPPER -> this.dash_upperAnimationState.start(this.tickCount);
                case STATE_GUARD_COUNTER -> this.guard_counterAnimationState.start(this.tickCount);
                case STATE_ULTRACHARGE_STRIKING_END ->
                        this.ultracharge_striking_end_AnimationState.start(this.tickCount);
                case STATE_JUMP_START -> this.jump_attack_start_AnimationState.start(this.tickCount);
                case STATE_JUMP_FALL_LOOP -> this.jump_attack_fall_loop_AnimationState.start(this.tickCount);
                case STATE_JUMP_END -> this.jump_attack_end_AnimationState.start(this.tickCount);
                case STATE_DASH_ATTACK_COMBO -> this.jab_attack_ex_oneAnimationState.start(this.tickCount);
            }
            if (state == 0 || (state != STATE_ULTRACHARGE && state != STATE_ULTRACHARGE_LIKEAMMO)) {
                this.setInvisible(false);
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
        this.goalSelector.addGoal(1, new IgnisStateGoal(this, 0, STATE_DASH, 0, 30, 0, 35.0F) {
            @Override
            public boolean canUse() {
                LivingEntity target = ignis.getTarget();
                if (target == null) return false;
                double distance = ignis.distanceTo(target);
                return super.canUse() && ignis.isDashReady() && distance >= IgnisPrimeConfig.DASH_MIN_DISTANCE.get() && distance <= IgnisPrimeConfig.DASH_MAX_DISTANCE.get()
                        && ignis.getRandom().nextFloat() < (distance > 16.0D ? (float) IgnisPrimeConfig.DASH_CHANCE_FAR.get() : (float) IgnisPrimeConfig.DASH_CHANCE_NEAR.get());
            }

            @Override
            public void start() {
                super.start();
                ignis.setDashCooldown(IgnisPrimeConfig.DASH_COOLDOWN.get());
            }
        });
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_DASH, STATE_DASH, 0, 30, 0, 35.0F));
        this.goalSelector.addGoal(0, new IgnisDashUpperGoal(this));
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_DASH_ATTACK_COMBO, STATE_DASH_ATTACK_COMBO, 0, 50, 0, 20.0F));
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_GUARD_COUNTER, STATE_GUARD_COUNTER, 0, 22, 0, 20.0F));
        this.goalSelector.addGoal(1, new IgnisStateGoal(this, 0, STATE_JUMP_START, 0, 70, 0, 30.0F) {
            @Override
            public boolean canUse() {
                LivingEntity target = ignis.getTarget();
                if (target == null || !ignis.isJumpReady()) return false;
                double distance = ignis.distanceTo(target);
                return super.canUse() && distance >= IgnisPrimeConfig.JUMP_MIN_DISTANCE.get() && distance <= IgnisPrimeConfig.JUMP_MAX_DISTANCE.get()
                        && ignis.getRandom().nextFloat() < (distance > 12.0D ? (float) IgnisPrimeConfig.JUMP_CHANCE_FAR.get() : (float) IgnisPrimeConfig.JUMP_CHANCE_NEAR.get());
            }

            @Override
            public void start() {
                super.start();
                ignis.setJumpCooldown(IgnisPrimeConfig.JUMP_COOLDOWN.get());
            }
        });
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_JUMP_FALL_LOOP, STATE_JUMP_FALL_LOOP, 0, 100, 0, 40.0F));
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_JUMP_END, STATE_JUMP_END, 0, 24, 0, 40.0F));
        this.goalSelector.addGoal(1, new IgnisStateGoal(this, 0, STATE_ULTRACHARGE, STATE_ULTRACHARGE_LIKEAMMO, 30, 0, 100.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && ignis.getTarget() != null && ignis.getBossPhase() >= 2 && ignis.ultCooldown <= 0 && ignis.getRandom().nextFloat() < (float) IgnisPrimeConfig.ULTRACHARGE_CHANCE.get();
            }

            @Override
            public void start() {
                super.start();
                ignis.sendBossMessage("chat.cataclysm_primed_soul.ignis_prime.ultracharge");
                ignis.ultCooldown = IgnisPrimeConfig.ULT_COOLDOWN.get();
            }
        });
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_ULTRACHARGE_LIKEAMMO, STATE_ULTRACHARGE_LIKEAMMO, STATE_ULTRACHARGE_STRIKING, 80, 0, 100.0F));
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_ULTRACHARGE_STRIKING, STATE_ULTRACHARGE_STRIKING, STATE_ULTRACHARGE_STRIKING_END, 80, 0, 100.0F));
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_ULTRACHARGE_STRIKING_END, STATE_ULTRACHARGE_STRIKING_END, 0, 60, 0, 100.0F));
        this.goalSelector.addGoal(2, new IgnisStateGoal(this, 0, STATE_ROCK_START, STATE_ROCK_LOOP, 18, 18, 35.0F) {
            @Override
            public boolean canUse() {
                LivingEntity t = ignis.getTarget();
                if (t == null || !ignis.isRockReady()) return false;
                double distance = ignis.distanceTo(t);
                return super.canUse() && distance >= 10.0D && distance <= 35.0D && ignis.getRandom().nextFloat() < 0.20F;
            }
        });
        this.goalSelector.addGoal(0, new IgnisRockLoopGoal(this));
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_ROCK_END, STATE_ROCK_END, 0, 21, 0, 30.0F) {
            @Override
            public void stop() {
                super.stop();
                ignis.setRockCooldown(200);
            }
        });
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_ROCK_END, STATE_ROCK_END, 0, 21, 0, 30.0F));
        this.goalSelector.addGoal(1, new IgnisChargeGoal(this, 15.0F) {
            @Override
            public boolean canUse() {
                LivingEntity target = ignis.getTarget();
                if (target == null) return false;
                if (ignis.lastAttackState == STATE_UPPERCUT && ignis.distanceTo(target) < 8.0D) return false;
                return super.canUse();
            }
        });
        this.goalSelector.addGoal(0, new IgnisChargeLoopGoal(this));
        this.goalSelector.addGoal(0,
                new IgnisStateGoal(this, STATE_CHARGE_SHOCKWAVE, STATE_CHARGE_SHOCKWAVE, 0, 36, 0, 20.0F));
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_CHARGE_END, STATE_CHARGE_END, 0, 8, 0, 20.0F));
        this.goalSelector.addGoal(0,
                new IgnisStateGoal(this, STATE_UPPERCUT_HORIZONTAL, STATE_UPPERCUT_HORIZONTAL, 0, 33, 0, 20.0F));
        this.goalSelector.addGoal(0,
                new IgnisStateGoal(this, STATE_UPPERCUT_VERTICAL, STATE_UPPERCUT_VERTICAL, 0, 24, 0, 20.0F));
        this.goalSelector.addGoal(2, new IgnisUppercutGoal(this, 4.0F) {
            @Override
            public boolean canUse() {
                if (ignis.lastAttackState == STATE_CHARGE_END && ignis.getRandom().nextFloat() > 0.3F) return false;
                return super.canUse();
            }
        });
        this.goalSelector.addGoal(3, new IgnisJabGoal(this, 0, 3, 4, 22, 15, 4.5F) {
            @Override
            public boolean canUse() {
                return super.canUse() && ignis.getRandom().nextFloat() < 0.5F;
            }

            @Override
            public void stop() {
                LivingEntity target = ignis.getTarget();
                if (this.ignis.getAttackState() == this.attackstate) {
                    if (target != null && ignis.distanceTo(target) > 5.0D) {
                        ignis.setAttackState(STATE_CHARGE_START);
                    } else {
                        ignis.setAttackState(4);
                    }
                }
                this.ignis.getNavigation().stop();
            }
        });
        this.goalSelector.addGoal(0, new IgnisJabGoal(this, STATE_JAB_1, STATE_JAB_1, STATE_JAB_2, 22, 15, 4.5F) {
            @Override
            public void stop() {
                LivingEntity target = ignis.getTarget();
                if (this.ignis.getAttackState() == this.attackstate) {
                    if (target != null && ignis.distanceTo(target) > 5.0D) {
                        ignis.setAttackState(STATE_CHARGE_START);
                    } else {
                        ignis.setAttackState(STATE_JAB_2);
                    }
                }
                this.ignis.getNavigation().stop();
            }
        });
        this.goalSelector.addGoal(0, new IgnisJabGoal(this, 4, 4, 5, 20, 12, 4.5F) {
            @Override
            public void stop() {
                LivingEntity target = ignis.getTarget();
                if (this.ignis.getAttackState() == this.attackstate) {
                    if (target != null && ignis.distanceTo(target) > 5.0D) {
                        ignis.setAttackState(STATE_CHARGE_START);
                    } else {
                        ignis.setAttackState(5);
                    }
                }
                this.ignis.getNavigation().stop();
            }
        });
        this.goalSelector.addGoal(0, new IgnisJabGoal(this, 5, 5, 0, 29, 15, 5.0F) {
            @Override
            public void stop() {
                LivingEntity target = ignis.getTarget();
                if (this.ignis.getAttackState() == this.attackstate) {
                    if (target != null && ignis.distanceTo(target) > 12.0D && ignis.isRockReady()) {
                        ignis.setAttackState(STATE_ROCK_START);
                    } else {
                        ignis.setAttackState(0);
                    }
                    ignis.jabComboCount++;
                }
                this.ignis.getNavigation().stop();
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
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_GUARD_BREAK, STATE_GUARD_BREAK, 0, 70, 0, 20.0F));
        this.goalSelector.addGoal(0,
                new IgnisStateGoal(this, STATE_CATCH_START, STATE_CATCH_START, STATE_CATCH_FAIL, 47, 0, 20.0F));
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, STATE_CATCH_FAIL, STATE_CATCH_FAIL, 0, 17, 0, 20.0F));
        this.goalSelector.addGoal(0,
                new IgnisStateGoal(this, STATE_CATCH_SUCCESS, STATE_CATCH_SUCCESS, 0, 95, 0, 20.0F));
        this.goalSelector.addGoal(0, new IgnisStateGoal(this, 0, STATE_GUARD_START, STATE_GUARD_LOOP, 21, 0, 20.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && ignis.getTarget() != null && ignis.recentDamageTaken >= (float) IgnisPrimeConfig.RECENT_DAMAGE_GUARD_THRESHOLD.get()
                        && ignis.guardCooldown <= 0;
            }

            @Override
            public void start() {
                super.start();
                ignis.guardCooldown = IgnisPrimeConfig.GUARD_COOLDOWN.get();
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
                boolean after10 = ignis.totalAttacksMade >= IgnisPrimeConfig.CATCH_MIN_ATTACKS.get();
                return super.canUse() && (near || after10);
            }

            @Override
            public void start() {
                super.start();
                ignis.catchCooldown = IgnisPrimeConfig.CATCH_COOLDOWN.get();
                if (ignis.totalAttacksMade >= IgnisPrimeConfig.CATCH_MIN_ATTACKS.get())
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
                boolean after6 = ignis.totalAttacksMade >= IgnisPrimeConfig.POWER_SLAM_FORCE_ATTACKS.get();
                return super.canUse() && (behind || after6);
            }

            @Override
            public void start() {
                super.start();
                if (ignis.totalAttacksMade >= IgnisPrimeConfig.POWER_SLAM_FORCE_ATTACKS.get())
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
                boolean after8 = ignis.totalAttacksMade >= IgnisPrimeConfig.OVERHEAD_FORCE_ATTACKS.get();
                return super.canUse() && ignis.overheadCooldown <= 0 && (notAttacking || after8);
            }

            @Override
            public void start() {
                super.start();
                ignis.overheadCooldown = IgnisPrimeConfig.OVERHEAD_COOLDOWN.get();
                if (ignis.totalAttacksMade >= IgnisPrimeConfig.OVERHEAD_FORCE_ATTACKS.get())
                    ignis.totalAttacksMade = 0;
            }
        });
        this.goalSelector.addGoal(1, new IgnisStateGoal(this, 0, STATE_JAB_EX_ONE, 0, 58, 0, 4.5F) {
            @Override
            public boolean canUse() {
                LivingEntity t = ignis.getTarget();
                return super.canUse() && t != null && ignis.distanceTo(t) <= 5.0D && ignis.jabComboCount >= IgnisPrimeConfig.JAB_EX_COMBO_COUNT.get();
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

            @Override
            public void stop() {
                LivingEntity target = ignis.getTarget();
                if (this.ignis.getAttackState() == this.attackstate) {
                    if (target != null && ignis.distanceTo(target) > 10.0D) {
                        ignis.setAttackState(STATE_CHARGE_START);
                    } else {
                        ignis.setAttackState(STATE_COMBO_RUSH_2);
                    }
                }
                this.ignis.getNavigation().stop();
            }
        });
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(4, new InternalMoveGoal(this, false, 1.2D));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isDeadOrDying() || !this.isAlive()) {
            if (this.getAttackState() != STATE_DEATH) {
                this.setAttackState(STATE_DEATH);
            }
            return;
        }
        if (this.getAttackState() == STATE_PHASE_CHANGE) {
            handlePhaseChangeAction();
            return;
        }
        if (this.attackTicks == 1) {
            int state = this.getAttackState();
            if (state == STATE_JAB_1 || state == STATE_ROCK_START || state == STATE_CHARGE_START
                    || state == STATE_UPPERCUT || state == STATE_CATCH_START || state == STATE_POWER_SLAM
                    || state == STATE_OVERHEAD_GUARDBREAKER || state == STATE_JAB_EX_ONE
                    || state == STATE_COMBO_RUSH_1 || state == STATE_DASH) {
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
        if (!this.level().isClientSide() && state != 0) {
            int maxAllowedTicks = 150;
            switch (state) {
                case STATE_CHARGE_START -> maxAllowedTicks = 35;
                case STATE_UPPERCUT -> maxAllowedTicks = 55;
                case STATE_JAB_1 -> maxAllowedTicks = 35;
                case STATE_JAB_2 -> maxAllowedTicks = 35;
                case STATE_JAB_3 -> maxAllowedTicks = 45;
                case STATE_CHARGE_LOOP -> maxAllowedTicks = 90;
                case STATE_CHARGE_END -> maxAllowedTicks = 20;
                case STATE_UPPERCUT_HORIZONTAL -> maxAllowedTicks = 55;
                case STATE_UPPERCUT_VERTICAL -> maxAllowedTicks = 45;
                case STATE_CHARGE_SHOCKWAVE -> maxAllowedTicks = 60;
                case STATE_ROCK_START -> maxAllowedTicks = 35;
                case STATE_ROCK_LOOP -> maxAllowedTicks = 100;
                case STATE_ROCK_END -> maxAllowedTicks = 40;
                case STATE_POWER_SLAM -> maxAllowedTicks = 90;
                case STATE_GUARD_START -> maxAllowedTicks = 40;
                case STATE_GUARD_LOOP -> maxAllowedTicks = 120;
                case STATE_GUARD_BREAK -> maxAllowedTicks = 70;
                case STATE_GUARD_COUNTER -> maxAllowedTicks = 40;
                case STATE_GUARD_END -> maxAllowedTicks = 20;
                case STATE_CATCH_START -> maxAllowedTicks = 80;
                case STATE_CATCH_SUCCESS -> maxAllowedTicks = 120;
                case STATE_CATCH_FAIL -> maxAllowedTicks = 40;
                case STATE_DASH -> maxAllowedTicks = 40;
                case STATE_DASH_UPPER -> maxAllowedTicks = 45;
                case STATE_DASH_ATTACK_COMBO -> maxAllowedTicks = 80;
                case STATE_OVERHEAD_GUARDBREAKER -> maxAllowedTicks = 90;
                case STATE_JUMP_START -> maxAllowedTicks = 90;
                case STATE_JUMP_FALL_LOOP -> maxAllowedTicks = 120;
                case STATE_JUMP_END -> maxAllowedTicks = 45;
            }
            if (this.attackTicks >= getScaledTick(maxAllowedTicks)) {
                this.setAttackState(0);
                state = 0;
            }
        }
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
                }
                break;
            case STATE_ROCK_START:
                this.getNavigation().stop();
                this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
                break;
            case STATE_ROCK_LOOP:
                handleRockLoopMovement();
                break;
            case STATE_ROCK_END:
                this.getNavigation().stop();
                this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
                break;
            case STATE_CHARGE_SHOCKWAVE:
                if (this.attackTicks == 20) {
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 1.5F, 0.6F);
                    LivingEntity t = this.getTarget();
                    if (t != null) {
                        this.breakPlayerShield(t, 60);
                    }
                    this.performJabDamage((float) IgnisPrimeConfig.CHARGE_SW_DAMAGE_MULT.get(), (float) IgnisPrimeConfig.CHARGE_SW_KNOCKBACK.get());
                    this.spawnFallingBlockShockwave(18, 58.0F);
                    this.setChargeCooldown(100);
                }
                handleShockwaveAction();
                break;
            case STATE_UPPERCUT:
                if (this.attackTicks == 1) {
                    this.uppercutHit = false;
                    this.uppercutStartPos = this.position();
                }
                if (this.attackTicks == getScaledTick(23)) {
                    this.performUppercutDamage((float) IgnisPrimeConfig.UPPERCUT_DAMAGE_MULT.get());
                    if (!this.level().isClientSide) {
                        com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToClients(new com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect(this.getId(), 0));
                        com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity.ScreenShake(this.level(), this.position(), 20.0F, 0.15F, 0, 15);
                    }
                }
                if (this.uppercutHit && this.attackTicks >= getScaledTick(33)) {
                    this.hideForUppercutAmbush();
                }
                if (!this.level().isClientSide() && this.attackTicks >= getScaledTick(40)) {
                    if (this.wasUppercutHit() && target != null && target.isAlive()) {
                        int nextCombo = this.random.nextBoolean() ? STATE_UPPERCUT_HORIZONTAL : STATE_UPPERCUT_VERTICAL;
                        this.setAttackState(nextCombo);
                    } else {
                        this.setAttackState(0);
                        this.setUppercutCooldown(100);
                    }
                }
                break;
            case STATE_UPPERCUT_HORIZONTAL:
            case STATE_UPPERCUT_VERTICAL:
                this.showAfterUppercutAmbush();
                if (this.attackTicks <= 5 && target != null)
                    teleportToTarget(target);
                this.setDeltaMovement(0, 0, 0);
                this.setNoGravity(true);
                if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL, this.getX(), this.getY() + 1.0D, this.getZ(), 3, 0.3D, 0.5D, 0.3D, 0.05D);
                    serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 2, 0.3D, 0.3D, 0.3D, 0.02D);
                }
                if (state == STATE_UPPERCUT_HORIZONTAL) {
                    if (this.attackTicks == 6) {
                        this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 0.5F);
                        this.performComboDamage((float) IgnisPrimeConfig.UPPERCUT_HORIZ_DAMAGE_MULT.get(), 4.75F, 130.0F, (float) IgnisPrimeConfig.UPPERCUT_HORIZ_KNOCKBACK.get(), 2.4D, 0.2D);
                        if (!this.level().isClientSide) {
                            com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToClients(new com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect(this.getId(), 0));
                            com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity.ScreenShake(this.level(), this.position(), 20.0F, 0.15F, 0, 15);
                        }
                    }
                } else {
                    if (this.attackTicks == 12) {
                        this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 0.8F);
                        this.performComboDamage((float) IgnisPrimeConfig.UPPERCUT_VERT_DAMAGE_MULT.get(), 4.75F, 130.0F, (float) IgnisPrimeConfig.UPPERCUT_VERT_KNOCKBACK.get(), 0.0D, -2.4D);
                        if (!this.level().isClientSide) {
                            com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToClients(new com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect(this.getId(), 0));
                            com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity.ScreenShake(this.level(), this.position(), 20.0F, 0.15F, 0, 15);
                        }
                    }
                }
                break;
            case STATE_JAB_1:
                if (this.attackTicks == getScaledTick(10)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.2F);
                    float yaw = this.getYRot() * ((float) Math.PI / 180F);
                    this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(yaw) * 0.3D, 0.0D, Mth.cos(yaw) * 0.3D));
                    this.hasImpulse = true;
                }
                if (this.attackTicks == getScaledTick(18))
                    this.performJabDamage((float) IgnisPrimeConfig.JAB_DAMAGE_MULT_1.get(), (float) IgnisPrimeConfig.JAB_KNOCKBACK_1.get());
                break;
            case STATE_JAB_2:
                if (this.attackTicks == getScaledTick(8)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.3F);
                    float yaw = this.getYRot() * ((float) Math.PI / 180F);
                    this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(yaw) * 0.3D, 0.0D, Mth.cos(yaw) * 0.3D));
                    this.hasImpulse = true;
                }
                if (this.attackTicks == getScaledTick(17))
                    this.performJabDamage((float) IgnisPrimeConfig.JAB_DAMAGE_MULT_2.get(), (float) IgnisPrimeConfig.JAB_KNOCKBACK_2.get());
                break;
            case STATE_JAB_3:
                if (this.attackTicks == getScaledTick(12)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.0F, 0.8F);
                    float yaw = this.getYRot() * ((float) Math.PI / 180F);
                    this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(yaw) * 0.45D, 0.0D, Mth.cos(yaw) * 0.45D));
                    this.hasImpulse = true;
                }
                if (this.attackTicks == getScaledTick(18))
                    this.performJabDamage((float) IgnisPrimeConfig.JAB_DAMAGE_MULT_3.get(), (float) IgnisPrimeConfig.JAB_KNOCKBACK_3.get());
                break;
            case STATE_POWER_SLAM:
                if (this.attackTicks == getScaledTick(38)) {
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 1.5F, 0.6F);
                    List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(7.0D, 3.0D, 7.0D));
                    for (LivingEntity t : targets) {
                        if (t != this && !this.isAlliedTo(t)) {
                            this.breakPlayerShield(t, 60);
                        }
                    }
                    this.performAreaDamage((float) IgnisPrimeConfig.POWER_SLAM_DAMAGE_MULT.get(), (float) IgnisPrimeConfig.POWER_SLAM_KNOCKBACK.get(), 7.0D, 3.0D, 0.0D, 0.5D);
                    this.spawnFallingBlockShockwave(7, 360.0F);
                    this.spawnFlameStrike(this.getX(), this.getZ(), this.getY() - 1, this.getY() + 1, 0, 40, 10, 0,
                            5.0F, false);
                    if (!this.level().isClientSide) {
                        com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToClients(new com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect(this.getId(), 1));
                        com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity.ScreenShake(this.level(), this.position(), 20.0F, 0.25F, 0, 20);
                        this.destroyBlocksInAABB(this.getBoundingBox().inflate(7.0D, 2.0D, 7.0D));
                    }
                }
                if (this.attackTicks == getScaledTick(41) || this.attackTicks == getScaledTick(44)) {
                    this.performAreaDamage(0.8F, 0.8F, 7.0D, 3.0D, 0.0D, 0.2D);
                }
                break;
            case STATE_JAB_EX_ONE:
                if (this.attackTicks == getScaledTick(23)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.2F);
                    this.performJabDamage((float) IgnisPrimeConfig.JAB_EX_DAMAGE_1.get(), (float) IgnisPrimeConfig.JAB_EX_KNOCKBACK_1.get());
                }
                if (this.attackTicks == getScaledTick(41)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.0F, 0.8F);
                    this.performJabDamage((float) IgnisPrimeConfig.JAB_EX_DAMAGE_2.get(), (float) IgnisPrimeConfig.JAB_EX_KNOCKBACK_2.get());
                    float yaw = this.getYRot() * ((float) Math.PI / 180F);
                    double fx = this.getX() - Mth.sin(yaw) * 2.5D;
                    double fz = this.getZ() + Mth.cos(yaw) * 2.5D;
                    this.spawnFlameStrike(fx, fz, this.getY() - 1, this.getY() + 1, 0, 30, 5, 0, 2.0F, false);
                }
                break;
            case STATE_COMBO_RUSH_1:
                if (this.attackTicks == getScaledTick(23)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.2F);
                    this.performJabDamage((float) IgnisPrimeConfig.COMBO_RUSH_1_JAB1_DAMAGE.get(), (float) IgnisPrimeConfig.COMBO_RUSH_1_JAB1_KB.get());
                    float yaw = this.getYRot() * ((float) Math.PI / 180F);
                    this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(yaw) * 1.5D, 0.0D, Mth.cos(yaw) * 1.5D));
                }
                if (this.attackTicks == getScaledTick(41)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.0F, 0.8F);
                    this.performJabDamage((float) IgnisPrimeConfig.COMBO_RUSH_1_JAB2_DAMAGE.get(), (float) IgnisPrimeConfig.COMBO_RUSH_1_JAB2_KB.get());
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
                    this.performComboDamage((float) IgnisPrimeConfig.COMBO_RUSH_2_DAMAGE.get(), 4.75F, 130.0F, (float) IgnisPrimeConfig.COMBO_RUSH_2_KB.get(), 2.4D, 0.2D);
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
                    this.performAreaDamage((float) IgnisPrimeConfig.COMBO_RUSH_3_DAMAGE.get(), (float) IgnisPrimeConfig.COMBO_RUSH_3_KB.get(), 7.0D, 3.0D, 0.0D, 0.6D);
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
                        this.caughtEntity.startRiding(this, true);
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
                    this.caughtEntity.invulnerableTime = 20;
                    if (this.isPrimeSecondForm() && (this.attackTicks == 32 || this.attackTicks == 51 || this.attackTicks == 61)) {
                        spawnForwardPrimeFlameStrike(1.5D, 2.2F, 0);
                    }
                    if (this.attackTicks == 30) {
                        this.performCatchDamage((float) IgnisPrimeConfig.CATCH_DAMAGE_1.get(), 0.2F, SoundEvents.GENERIC_EXPLODE, 0.8F);
                    }
                    if (this.attackTicks == 49) {
                        this.performCatchDamage((float) IgnisPrimeConfig.CATCH_DAMAGE_2.get(), 0.25F, SoundEvents.GENERIC_EXPLODE, 0.7F);
                    }
                    if (this.attackTicks == 59) {
                        this.performCatchDamage((float) IgnisPrimeConfig.CATCH_DAMAGE_3.get(), 0.35F, SoundEvents.ANVIL_LAND, 0.5F);
                    }
                    if (this.attackTicks == 85) {
                        this.caughtEntity.stopRiding();
                        this.caughtEntity.invulnerableTime = 0;
                        EntityDamageHelper.hurtIgnoringInvulnerability((LivingEntity) this.caughtEntity, this.damageSources().mobAttack(this), this.scaleDirectDamage((float) IgnisPrimeConfig.CATCH_DAMAGE_FINAL.get()));
                        this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.5F, 0.5F);
                        float pushYaw = this.getYRot() * ((float) Math.PI / 180F);
                        double speed = 3.2D;
                        this.caughtEntity.setDeltaMovement(-Mth.sin(pushYaw) * speed, 0.8D, Mth.cos(pushYaw) * speed);
                        this.caughtEntity.hasImpulse = true;
                        this.caughtEntity = null;
                        this.setCaughtEntityId(-1);
                    }
                }
                break;
            case STATE_GUARD_START:
                this.guardCounterPrimed = false;
                if (this.attackTicks == 20) {
                    this.isGuarding = true;
                    this.guardAxeHits = 0;
                    this.guardDamageTaken = 0;
                }
                break;
            case STATE_GUARD_LOOP:
                this.isGuarding = true;
                if (!this.guardCounterPrimed && this.attackTicks >= 12) {
                    this.guardCounterPrimed = true;
                    this.playSound(SoundEvents.BEACON_POWER_SELECT, 1.2F, 1.7F);
                }
                if (this.guardCounterPrimed && !this.level().isClientSide && this.attackTicks % 10 == 0) {
                    com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToClients(new com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect(this.getId(), 5));
                }
                if (target != null) {
                    this.lookAt(target, 30.0F, 30.0F);
                    this.yBodyRot = this.getYRot();
                }
                break;
            case STATE_GUARD_BREAK:
                this.isGuarding = false;
                this.guardCounterPrimed = false;
                break;
            case STATE_GUARD_END:
                this.isGuarding = false;
                this.guardCounterPrimed = false;
                break;
            case STATE_ULTRACHARGE:
                this.getNavigation().stop();
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, 1.0, 0.8));
                this.ultrachargeLaunchVelocity = Vec3.ZERO;
                if (this.attackTicks == 20) {
                    if (!this.level().isClientSide) {
                        this.dropAggroFromEnemies();
                        this.playSound(SoundEvents.FIRECHARGE_USE, 2.0F, 0.5F);
                    }
                }
                break;
            case STATE_ULTRACHARGE_LIKEAMMO:
                this.setInvisible(false);
                this.setNoGravity(true);
                if (this.attackTicks <= 20) {
                    if (this.attackTicks == 1) {
                        this.storedY = this.getY();
                        float yaw = this.getYRot() * ((float) Math.PI / 180F);
                        Vec3 shootDir = new Vec3(-Mth.sin(yaw), 0, Mth.cos(yaw)).scale(5.5D);
                        this.ultrachargeLaunchVelocity = shootDir;
                        this.setDeltaMovement(shootDir);
                        this.hasImpulse = true;
                        this.playSound(SoundEvents.GENERIC_EXPLODE, 2.5F, 1.2F);
                    }
                    this.performAreaDamage(1.5F, 1.2F, 4.0D, 3.0D, 0.0D, 0.3D);
                    this.destroyBlocksInAABB(this.getBoundingBox().inflate(3.0D));
                    if (this.tickCount % 2 == 0) {
                        if (!this.level().isClientSide) {
                            com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity.ScreenShake(this.level(), this.position(), 15.0F, 0.15F, 0, 8);
                            this.playSound(SoundEvents.WARDEN_SONIC_BOOM, 1.5F, 1.2F);
                        }
                        this.level().addParticle(net.minecraft.core.particles.ParticleTypes.SONIC_BOOM, this.getX(), this.getY() + 1.2D, this.getZ(), 0, 0, 0);
                    }
                    this.setXRot(0.0F);
                    this.xRotO = 0.0F;
                } else {
                    if (target != null) {
                        this.slamPos = target.position();
                    } else if (this.slamPos == null) {
                        this.slamPos = this.position();
                    }
                    double time = (double) this.tickCount;
                    double idSeed = (double) this.getId();
                    double radiusOffset = Math.sin(time * 0.06D + idSeed) * 4.0D;
                    double radius = 18.0D + radiusOffset;
                    double angleOffset = Math.cos(time * 0.04D + idSeed * 0.5D) * 0.25D;
                    double angle = (time * 0.11D) + angleOffset;
                    double climbProgress = (double) (this.attackTicks - 20) / 60.0D;
                    double heightProgress = Math.sin(climbProgress * Math.PI / 2.0D);
                    double altitudeSway = Math.sin(time * 0.08D + idSeed * 2.0D) * 3.5D;
                    double currentTargetY = this.storedY + (heightProgress * 70.0D) + altitudeSway;
                    double targetX = this.slamPos.x + Math.cos(angle) * radius;
                    double targetZ = this.slamPos.z + Math.sin(angle) * radius;
                    Vec3 targetOrbitalPos = new Vec3(targetX, currentTargetY, targetZ);
                    Vec3 steerVec = targetOrbitalPos.subtract(this.position());
                    Vec3 currentVelocity = this.getDeltaMovement();
                    double ascentTicks = (double) (this.attackTicks - 20);
                    double steerWeight = Mth.clamp(ascentTicks / 35.0D, 0.0D, 1.0D) * 0.14D + 0.04D;
                    Vec3 carriedLaunch = this.ultrachargeLaunchVelocity.scale(Math.max(0.0D, 1.0D - ascentTicks / 28.0D));
                    Vec3 blendedVelocity = currentVelocity.scale(1.0D - steerWeight)
                            .add(steerVec.scale(steerWeight))
                            .add(carriedLaunch.scale(0.10D))
                            .add(0.0D, Math.max(0.0D, 0.55D - ascentTicks * 0.012D), 0.0D);
                    double speedCap = 6.2D;
                    if (blendedVelocity.length() > speedCap) {
                        blendedVelocity = blendedVelocity.normalize().scale(speedCap);
                    }
                    this.setDeltaMovement(blendedVelocity);
                    if (blendedVelocity.lengthSqr() > 1.0E-4D) {
                        float travelYaw = (float) (Mth.atan2(blendedVelocity.z, blendedVelocity.x) * (180D / Math.PI)) - 90.0F;
                        this.setYRot(travelYaw);
                        this.yBodyRot = travelYaw;
                        this.yHeadRot = travelYaw;
                        this.yRotO = travelYaw;
                        double horizontalDist = Math.sqrt(blendedVelocity.x * blendedVelocity.x + blendedVelocity.z * blendedVelocity.z);
                        float travelPitch = (float) -(Mth.atan2(blendedVelocity.y, horizontalDist) * (180D / Math.PI));
                        this.setXRot(travelPitch);
                        this.xRotO = travelPitch;
                    }
                    if (this.tickCount % 2 == 0) {
                        this.level().addParticle(net.minecraft.core.particles.ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
                        this.playSound(SoundEvents.FIREWORK_ROCKET_SHOOT, 1.2F, 0.85F + this.random.nextFloat() * 0.2F);
                    }
                    this.destroyBlocksInAABB(this.getBoundingBox().inflate(3.0D));
                }
                if (this.attackTicks >= 80) {
                    if (!this.level().isClientSide) {
                        this.setAttackState(STATE_ULTRACHARGE_STRIKING);
                    }
                }
                break;
            case STATE_ULTRACHARGE_STRIKING:
                this.setInvisible(false);
                int warningTime = 25;
                if (this.attackTicks < warningTime) {
                    this.setNoGravity(true);
                    this.setDeltaMovement(Vec3.ZERO);
                    if (this.attackTicks == 1 && target != null) {
                        this.slamPos = target.position();
                    }
                    if (this.slamPos != null) {
                        for (int i = 0; i < 6; i++) {
                            double angle = this.random.nextDouble() * Math.PI * 2.0D;
                            double r = this.random.nextDouble() * 3.0D;
                            this.level().addParticle(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME,
                                    this.slamPos.x + Math.cos(angle) * r,
                                    this.storedY + 0.2D,
                                    this.slamPos.z + Math.sin(angle) * r,
                                    0, 0.15D, 0);
                        }
                    }
                } else {
                    this.setNoGravity(false);
                    this.setDeltaMovement(0, -8.0D, 0);
                    this.hasImpulse = true;
                    if (this.attackTicks == warningTime) {
                        if (!this.level().isClientSide && this.slamPos != null) {
                            this.moveTo(this.slamPos.x, this.storedY + 70.0D, this.slamPos.z);
                            this.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 4.0F, 0.5F);
                        }
                    }
                    this.destroyBlocksInAABB(this.getBoundingBox().inflate(3.5D));
                }
                if (this.attackTicks > warningTime && (this.getY() <= this.storedY + 1.5D || this.onGround())) {
                    if (!this.level().isClientSide) {
                        this.setPos(this.getX(), this.storedY, this.getZ());
                        this.setAttackState(STATE_ULTRACHARGE_STRIKING_END);
                        this.playSound(SoundEvents.GENERIC_EXPLODE, 5.0F, 0.5F);
                        this.performAreaDamage((float) IgnisPrimeConfig.ULTRACHARGE_AREA_DAMAGE_MULT.get(), (float) IgnisPrimeConfig.ULTRACHARGE_AREA_KNOCKBACK.get(), 16.0D, 6.0D, 0, 1.5D);
                        this.spawnPrimeFlameArc(16, 12.0D, 360.0F, 4.8F, 0);
                        com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity.ScreenShake(this.level(), this.position(), 100.0F, 1.0F, 0, 50);
                        this.destroyBlocksInAABB(this.getBoundingBox().inflate(12.0D, 4.0D, 10.0D));
                    }
                }
                break;
            case STATE_ULTRACHARGE_STRIKING_END:
                this.setInvisible(false);
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.2, 1.0, 0.2));
                break;
            case STATE_JUMP_START:
                this.getNavigation().stop();
                this.setNoGravity(false);
                if (target != null) {
                    this.lookAt(target, 25.0F, 25.0F);
                    this.yBodyRot = this.getYRot();
                }
                if (this.attackTicks < getScaledTick(22)) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.25D, 1.0D, 0.25D));
                }
                if (this.attackTicks == getScaledTick(22)) {
                    Vec3 horizontal = Vec3.ZERO;
                    if (target != null && !this.harmlessJumpAttack) {
                        Vec3 toTarget = target.position().subtract(this.position());
                        if (toTarget.horizontalDistanceSqr() > 1.0E-4D) {
                            horizontal = new Vec3(toTarget.x, 0.0D, toTarget.z).normalize().scale(0.85D);
                        }
                    }
                    this.setDeltaMovement(horizontal.x, 1.25D, horizontal.z);
                    this.hasImpulse = true;
                }
                if (this.attackTicks >= getScaledTick(35) || (this.attackTicks > getScaledTick(22) && this.getDeltaMovement().y <= 0.0D)) {
                    this.setAttackState(STATE_JUMP_FALL_LOOP);
                }
                break;
            case STATE_JUMP_FALL_LOOP:
                this.getNavigation().stop();
                this.setNoGravity(false);
                if (this.onGround() && this.attackTicks > 2) {
                    this.setAttackState(STATE_JUMP_END);
                    this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
                }
                break;
            case STATE_JUMP_END:
                this.getNavigation().stop();
                this.setNoGravity(false);
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.2D, 1.0D, 0.2D));
                if (this.attackTicks == getScaledTick(4)) {
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 1.8F, 0.55F);
                    if (!this.harmlessJumpAttack) {
                        this.performAreaDamage((float) IgnisPrimeConfig.JUMP_LAND_DAMAGE_MULT.get(), (float) IgnisPrimeConfig.JUMP_LAND_KNOCKBACK.get(), 7.0D, 3.5D, 0.0D, 0.75D);
                        this.spawnFallingBlockShockwave(7, 360.0F);
                        if (!this.level().isClientSide) {
                            com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity.ScreenShake(this.level(), this.position(), 25.0F, 0.35F, 0, 20);
                        }
                    }
                }
                if (this.attackTicks >= getScaledTick(21)) {
                    this.harmlessJumpAttack = false;
                    this.setAttackState(0);
                }
                break;
            case STATE_DASH:
                this.getNavigation().stop();
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.2D, 1.0D, 0.2D));
                if (target != null) {
                    this.lookAt(target, 45.0F, 45.0F);
                    this.yBodyRot = this.getYRot();
                }
                if (target != null && this.attackTicks >= 10 && this.attackTicks < 16) {
                    double targetYawRad = Math.toRadians(target.getYRot());
                    double ox = Math.sin(targetYawRad) * 1.5D;
                    double oz = -Math.cos(targetYawRad) * 1.5D;
                    if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME, target.getX() + ox, target.getY() + 0.1D, target.getZ() + oz, 3, 0.2D, 0.2D, 0.2D, 0.05D);
                        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.PORTAL, target.getX() + ox, target.getY() + 1.0D, target.getZ() + oz, 5, 0.1D, 0.3D, 0.1D, 0.1D);
                    }
                }
                if (this.attackTicks == 16) {
                    if (target != null) {
                        double yaw = Math.toRadians(target.getYRot());
                        double offsetX = Math.sin(yaw) * 1.5D;
                        double offsetZ = -Math.cos(yaw) * 1.5D;
                        double targetY = target.getY();
                        double destX = target.getX() + offsetX;
                        double destY = targetY;
                        double destZ = target.getZ() + offsetZ;
                        if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.PORTAL, this.getX(), this.getY() + 1.5D, this.getZ(), 30, 0.5D, 0.5D, 0.5D, 0.2D);
                            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY() + 1.0D, this.getZ(), 20, 0.4D, 0.4D, 0.4D, 0.1D);
                            Vec3 from = this.position().add(0, 1.0, 0);
                            Vec3 to = new Vec3(destX, destY + 1.0, destZ);
                            Vec3 trajectory = to.subtract(from);
                            double dist = trajectory.length();
                            int pCount = (int) (dist * 3);
                            for (int i = 0; i <= pCount; i++) {
                                double ratio = (double) i / pCount;
                                double px = from.x + trajectory.x * ratio;
                                double py = from.y + trajectory.y * ratio;
                                double pz = from.z + trajectory.z * ratio;
                                serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME, px, py, pz, 1, 0, 0, 0, 0);
                            }
                            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.PORTAL, destX, destY + 1.5D, destZ, 30, 0.5D, 0.5D, 0.5D, 0.2D);
                            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME, destX, destY + 1.0D, destZ, 20, 0.4D, 0.4D, 0.4D, 0.1D);
                        }
                        this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.5F, 1.2F);
                        this.teleportTo(destX, destY, destZ);
                        this.lookAt(target, 360.0F, 360.0F);
                        this.yBodyRot = this.getYRot();
                        this.yRotO = this.getYRot();
                        this.setDeltaMovement(Vec3.ZERO);
                    }
                    this.setAttackState(STATE_DASH_UPPER);
                }
                break;
            case STATE_DASH_UPPER:
                this.getNavigation().stop();
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.5D, 1.0D, 0.5D));
                if (target != null) {
                    this.lookAt(target, 45.0F, 45.0F);
                    this.yBodyRot = this.getYRot();
                }
                if (this.attackTicks == 1) {
                    this.dashUpperHit = false;
                }
                if (this.attackTicks == 6) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 2.0F, 0.8F);
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 1.5F, 1.1F);
                    if (target != null) {
                        this.breakPlayerShield(target, 40);
                    }
                    boolean hit = this.performForwardArcDamage((float) IgnisPrimeConfig.DASH_UPPER_DAMAGE_MULT.get(), 4.5F, 120.0F, (float) IgnisPrimeConfig.DASH_UPPER_KNOCKBACK.get(), 0.05D);
                    float yaw = this.getYRot() * ((float) Math.PI / 180F);
                    this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(yaw) * 0.3D, 0.0D, Mth.cos(yaw) * 0.3D));
                    this.hasImpulse = true;
                    if (target != null && this.distanceTo(target) <= 4.5F + this.getBbWidth() && this.isInFrontArc(target, 120.0F)) {
                        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, IgnisPrimeConfig.DASH_UPPER_SLOWDOWN_TICKS.get(), IgnisPrimeConfig.DASH_UPPER_SLOWDOWN_LEVEL.get(), false, false));
                        this.dashUpperHit = true;
                    } else {
                        this.dashUpperHit = hit;
                    }
                    if (!this.level().isClientSide) {
                        com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToClients(new com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect(this.getId(), 0));
                        com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity.ScreenShake(this.level(), this.position(), 25.0F, 0.25F, 0, 15);
                    }
                }
                if (!this.level().isClientSide() && this.attackTicks >= getScaledTick(25)) {
                    this.setAttackState(STATE_JAB_1);
                }
                break;
            case STATE_DASH_ATTACK_COMBO:
                this.getNavigation().stop();
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.4D, 1.0D, 0.4D));
                if (target != null) {
                    this.lookAt(target, 30.0F, 30.0F);
                    this.yBodyRot = this.getYRot();
                }
                if (this.attackTicks == 12) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5F, 1.0F);
                    this.performComboDamage((float) IgnisPrimeConfig.DASH_COMBO_HIT1_DAMAGE.get(), 4.5F, 120.0F, (float) IgnisPrimeConfig.DASH_COMBO_HIT1_KB.get(), -1.2D, 0.1D);
                }
                if (this.attackTicks == 24) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5F, 0.9F);
                    this.performComboDamage((float) IgnisPrimeConfig.DASH_COMBO_HIT2_DAMAGE.get(), 4.5F, 120.0F, (float) IgnisPrimeConfig.DASH_COMBO_HIT2_KB.get(), -1.2D, 0.1D);
                }
                if (this.attackTicks == 38) {
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 1.8F, 0.7F);
                    this.performComboDamage((float) IgnisPrimeConfig.DASH_COMBO_HIT3_DAMAGE.get(), 5.0F, 130.0F, (float) IgnisPrimeConfig.DASH_COMBO_HIT3_KB.get(), 2.0D, 0.4D);
                    float cyaw = this.getYRot() * ((float) Math.PI / 180F);
                    double fx = this.getX() - Mth.sin(cyaw) * 2.5D;
                    double fz = this.getZ() + Mth.cos(cyaw) * 2.5D;
                    this.spawnFlameStrike(fx, fz, this.getY() - 1, this.getY() + 1, this.getYRot(), 25, 0, 0, 2.5F, true);
                    if (!this.level().isClientSide) {
                        com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToClients(new com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect(this.getId(), 0));
                        com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity.ScreenShake(this.level(), this.position(), 20.0F, 0.2F, 0, 15);
                    }
                }
                break;
            case STATE_GUARD_COUNTER:
                this.getNavigation().stop();
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.5D, 1.0D, 0.5D));
                if (target != null) {
                    this.lookAt(target, 45.0F, 45.0F);
                    this.yBodyRot = this.getYRot();
                }
                if (this.attackTicks == 12) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 2.0F, 0.8F);
                    this.playSound(SoundEvents.SHIELD_BLOCK, 1.5F, 0.5F);
                    this.performForwardArcDamage((float) IgnisPrimeConfig.GUARD_COUNTER_DAMAGE_MULT.get(), 4.0F, 110.0F, (float) IgnisPrimeConfig.GUARD_COUNTER_KNOCKBACK.get(), 0.2D);
                    this.guardCounterPrimed = false;
                    if (!this.level().isClientSide) {
                        com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToClients(new com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect(this.getId(), 1));
                        com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity.ScreenShake(this.level(), this.position(), 20.0F, 0.2F, 0, 15);
                    }
                }
                break;
        }
        handlePrimePhaseAttackAugments(state, target);
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
        this.clearHurtFlash();
        if (!this.level().isClientSide()) {
            this.phaseChangeTicks++;
            if (this.phaseChangeTicks == 1) {
                this.playSound(SoundEvents.EVOKER_PREPARE_ATTACK, 2.0F, 0.5F);
                this.playSound(SoundEvents.BEACON_ACTIVATE, 2.0F, 0.5F);
            }
            if (this.phaseChangeTicks < 70 && this.phaseChangeTicks % 5 == 0) {
                this.playSound(SoundEvents.FIRE_AMBIENT, 1.5F, 0.8F + this.getRandom().nextFloat() * 0.4F);
            }
            if (this.phaseChangeTicks == 70) {
                this.playSound(SoundEvents.GENERIC_EXPLODE, 3.0F, 0.5F);
                this.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 2.5F, 0.75F);
                com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity.ScreenShake(this.level(), this.position(), 40.0F, 0.5F, 0, 30);
                com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToClients(new com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect(this.getId(), 3));
            }
            if (this.phaseChangeTicks >= 70 && this.phaseChangeTicks <= 86) {
                if (this.phaseChangeTicks % 2 == 0) {
                    double distance = (this.phaseChangeTicks - 70) * 1.1D + 2.0D;
                    List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(distance, 3.0D, distance));
                    for (LivingEntity target : targets) {
                        if (target != this && !this.isAlliedTo(target)) {
                            double distToTarget = this.distanceTo(target);
                            if (Math.abs(distToTarget - distance) <= 2.0D) {
                                float dmg = this.scaleEnvironmentalDamage((float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.25F));
                                if (EntityDamageHelper.hurtIgnoringInvulnerability(target, this.damageSources().indirectMagic(this, this), dmg)) {
                                    target.setSecondsOnFire(8);
                                    double dx = target.getX() - this.getX();
                                    double dz = target.getZ() - this.getZ();
                                    double d2 = Math.max(dx * dx + dz * dz, 0.001D);
                                    target.push(dx / d2 * 3.5D, 0.5D, dz / d2 * 3.5D);
                                    target.hasImpulse = true;
                                }
                            }
                        }
                    }
                }
            }
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
        this.getNavigation().stop();
        this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
        LivingEntity target = this.getTarget();
        if (target != null) {
            float targetYaw = (float) (Mth.atan2(target.getZ() - this.getZ(), target.getX() - this.getX())
                    * (180D / Math.PI)) - 90.0F;
            this.setYRot(Mth.approachDegrees(this.getYRot(), targetYaw, 15.0F));
            this.yBodyRot = this.getYRot();
        }
    }

    private void performCatchDamage(float amount, float shakeIntensity, net.minecraft.sounds.SoundEvent sound, float pitch) {
        if (this.caughtEntity != null) {
            this.caughtEntity.invulnerableTime = 0;
            EntityDamageHelper.hurtIgnoringInvulnerability((LivingEntity) this.caughtEntity, this.damageSources().mobAttack(this), this.scaleDirectDamage(amount));
            this.caughtEntity.invulnerableTime = 10;
            this.playSound(sound, 1.2F, pitch);
            if (!this.level().isClientSide) {
                com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity.ScreenShake(this.level(), this.position(), 25.0F, shakeIntensity, 0, 10);
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

    public void performTectonicWave(int phase, LivingEntity target) {
        if (this.level().isClientSide || target == null) return;
        float pitch = 0.6F + (phase * 0.2F);
        this.playSound(SoundEvents.GENERIC_EXPLODE, 2.0F, pitch);
        this.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 1.5F, pitch + 0.4F);
        com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity.ScreenShake(this.level(), this.position(), 20.0F + (phase * 10.0F), 0.25F, 0, 15);
        Vec3 dir = target.position().subtract(this.position()).normalize();
        float yaw = (float) (Mth.atan2(dir.z, dir.x) * (180D / Math.PI)) - 90.0F;
        double dist = this.distanceTo(target);
        switch (phase) {
            case 0 -> {
                for (int i = 1; i <= 5; i++) {
                    double px = this.getX() + dir.x * (i * 4.0D);
                    double pz = this.getZ() + dir.z * (i * 4.0D);
                    spawnFlameStrike(px, pz, this.getY() - 1, this.getY() + 1, yaw, 25, 0, i * 2, 1.5F, true);
                }
            }
            case 1 -> {
                spawnPrimeFlameArc(5, dist + 1.5D, 90.0F, 1.5F, 5);
            }
            case 2 -> {
                double bX = target.getX() + dir.x * 5.0D;
                double bZ = target.getZ() + dir.z * 5.0D;
                spawnFlameStrike(bX, bZ, target.getY() - 1, target.getY() + 1, yaw, 50, 5, 0, 2.5F, true);
                spawnPrimeFlameArc(4, dist, 120.0F, 1.5F, 0);
                if (isPrimeSecondForm()) {
                    spawnPrimeFireballVolley(target, 2, 4, 1.5D, 20.0F);
                }
            }
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
        for (int i = 0; i < 10; i++) {
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
            double outward = 0.12D + this.getRandom().nextDouble() * 0.18D;
            debris.setDeltaMovement(
                    forward.x * outward + side.x * sideOffset * 0.06D,
                    0.20D + this.getRandom().nextDouble() * 0.20D,
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
            this.performJabDamage((float) IgnisPrimeConfig.CHARGE_SW_DAMAGE_MULT.get(), (float) IgnisPrimeConfig.CHARGE_SW_KNOCKBACK.get());
            this.spawnFallingBlockShockwave(18, 58.0F);
            this.setChargeCooldown(100);
        }
    }

    private void spawnFallingBlockShockwave(int length, float arc) {
        if (this.level().isClientSide)
            return;
        float centerYaw = this.yBodyRot;
        double maxDistance = length * 1.0D;
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(maxDistance));
        for (LivingEntity target : targets) {
            if (target != this && !this.isAlliedTo(target) && target.isAlive()) {
                double dist = this.distanceTo(target);
                if (dist >= 1.0D && dist <= maxDistance) {
                    float angleToTarget = (float) (Mth.atan2(target.getZ() - this.getZ(), target.getX() - this.getX()) * (180D / Math.PI)) - 90.0F;
                    float diff = Mth.degreesDifferenceAbs(centerYaw, angleToTarget);
                    if (diff <= arc * 0.5F) {
                        float damage = this.scaleEnvironmentalDamage((float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.2F));
                        if (EntityDamageHelper.hurtIgnoringInvulnerability(target, this.damageSources().mobAttack(this), damage)) {
                            target.setDeltaMovement(target.getDeltaMovement().add(0, 0.6D, 0));
                            target.hasImpulse = true;
                        }
                    }
                }
            }
        }
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
            }
        }
    }

    private void teleportToTarget(LivingEntity target) {
        this.showAfterUppercutAmbush();
        float targetYaw = target.getYRot() * ((float) Math.PI / 180F);
        double offsetX = Math.cos(targetYaw) * 2.0;
        double offsetZ = Math.sin(targetYaw) * 2.0;
        double targetY = target.getY() + (target.getBbHeight() * 0.5) - (this.getBbHeight() * 0.5);
        double destX = target.getX() + offsetX;
        double destY = targetY;
        double destZ = target.getZ() + offsetZ;
        if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            Vec3 from = this.position().add(0, 1.0, 0);
            Vec3 to = new Vec3(destX, destY + 1.0, destZ);
            Vec3 trajectory = to.subtract(from);
            double dist = trajectory.length();
            if (dist > 0.5D) {
                int pCount = (int) (dist * 3);
                if (pCount > 0) {
                    for (int i = 0; i <= pCount; i++) {
                        double ratio = (double) i / pCount;
                        double px = from.x + trajectory.x * ratio;
                        double py = from.y + trajectory.y * ratio;
                        double pz = from.z + trajectory.z * ratio;
                        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME, px, py, pz, 1, 0, 0, 0, 0);
                    }
                }
            }
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.PORTAL, destX, destY + 1.5D, destZ, 20, 0.3D, 0.5D, 0.3D, 0.1D);
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME, destX, destY + 1.0D, destZ, 15, 0.3D, 0.3D, 0.3D, 0.05D);
        }
        this.moveTo(destX, destY, destZ);
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

    public boolean wasDashUpperHit() {
        return this.dashUpperHit;
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

    private void dropAggroFromEnemies() {
        if (this.level().isClientSide) return;
        List<net.minecraft.world.entity.Mob> mobs = this.level().getEntitiesOfClass(
                net.minecraft.world.entity.Mob.class,
                this.getBoundingBox().inflate(64.0D)
        );
        for (net.minecraft.world.entity.Mob mob : mobs) {
            if (mob.getTarget() == this) {
                mob.setTarget(null);
                mob.setLastHurtByMob(null);
            }
        }
    }

    public void performUppercutDamage(float damageMultiplier) {
        LivingEntity target = this.getTarget();
        if (target != null) {
            this.breakPlayerShield(target, 40);
        }
        boolean damageSuccess = this.performForwardArcDamage(damageMultiplier, 4.75F, 120.0F, 0.55F, 1.8D);
        if (target != null && this.distanceTo(target) <= 4.75F + this.getBbWidth() && this.isInFrontArc(target, 120.0F)) {
            this.uppercutHit = true;
        } else {
            this.uppercutHit = damageSuccess;
        }
    }

    public void performOverheadDamage() {
        if (this.level().isClientSide)
            return;
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(3.0D, 2.0D, 3.0D));
        for (LivingEntity target : targets) {
            if (this.canDamageTarget(target) && this.isInFrontArc(target, 120) && this.distanceTo(target) <= 3.0D + this.getBbWidth()) {
                float damage = this.scaleDirectDamage((float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * (float) IgnisPrimeConfig.OVERHEAD_DAMAGE_MULT.get());
                if (target.isBlocking()) {
                    if (target instanceof Player player) {
                        player.getCooldowns().addCooldown(player.getUseItem().getItem(), 100);
                        player.stopUsingItem();
                        this.level().broadcastEntityEvent(player, (byte) 30);
                    }
                }
                if (EntityDamageHelper.hurtIgnoringInvulnerability(target, this.damageSources().magic(), damage)) {
                    this.applyAttackKnockback(target, (float) IgnisPrimeConfig.OVERHEAD_KNOCKBACK.get(), 0.0D, 0.0D);
                    this.heal(damage * 0.10F);
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
        double effectiveXzRange = EntityDamageHelper.expandRange(xzRange);
        double effectiveYRange = EntityDamageHelper.expandRange(yRange);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(effectiveXzRange, effectiveYRange, effectiveXzRange));
        for (LivingEntity target : targets) {
            if (this.canDamageTarget(target) && this.distanceTo(target) <= effectiveXzRange + this.getBbWidth()) {
                float damage = this.scaleDirectDamage((float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier);
                if (EntityDamageHelper.hurtIgnoringInvulnerability(target, this.damageSources().mobAttack(this), damage)) {
                    this.applyAttackKnockback(target, knockback, forwardPush, verticalImpulse);
                    this.heal(damage * 0.10F);
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
        double effectiveRange = EntityDamageHelper.expandRange(range);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(effectiveRange, EntityDamageHelper.expandRange(2.0D), effectiveRange));
        for (LivingEntity target : targets) {
            if (this.canDamageTarget(target) && this.isInFrontArc(target, arc) && this.distanceTo(target) <= effectiveRange + this.getBbWidth()) {
                float damage = this.scaleDirectDamage((float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier);
                if (EntityDamageHelper.hurtIgnoringInvulnerability(target, this.damageSources().mobAttack(this), damage)) {
                    this.applyAttackKnockback(target, knockback, forwardPush, verticalImpulse);
                    this.heal(damage * 0.10F);
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
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
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
                .damage(soul ? (float) IgnisPrimeConfig.SOUL_FLAME_STRIKE_DAMAGE.get() : (float) IgnisPrimeConfig.FLAME_STRIKE_DAMAGE.get())
                .hpDamage(soul ? (float) IgnisPrimeConfig.SOUL_FLAME_STRIKE_HP_DAMAGE.get() : (float) IgnisPrimeConfig.FLAME_STRIKE_HP_DAMAGE.get())
                .soul(soul)
                .owner(this)
                .spawn();
    }

    private void handlePrimePhaseAttackAugments(int state, LivingEntity target) {
        if (this.level().isClientSide) {
            return;
        }
        if (isPrimeSecondForm()) {
            switch (state) {
                case STATE_CHARGE_START -> {
                    if (this.attackTicks == getScaledTick(18)) {
                        spawnForwardPrimeFlameStrike(2.5D, 2.2F, 0);
                    }
                }
                case STATE_CHARGE_LOOP -> {
                    if (this.attackTicks % 10 == 0) {
                        spawnForwardPrimeFlameStrike(2.0D, 1.8F, 0);
                    }
                }
                case STATE_CHARGE_SHOCKWAVE -> {
                    if (this.attackTicks == 20) {
                        spawnPrimeFlameArc(5, 7.0D, 96.0F, 2.0F, 0);
                        spawnPrimeFireballVolley(target, 2, 5, 0.9D, 14.0F);
                    }
                }
                case STATE_ROCK_END -> {
                    if (this.attackTicks == 10) {
                        spawnTargetPrimeFlameStrike(target, 2.4F, 0);
                        spawnPrimeFireballVolley(target, 1, 6, 0.85D, 12.0F);
                    }
                }
                case STATE_UPPERCUT -> {
                    if (this.attackTicks == 23) {
                        spawnForwardPrimeFlameStrike(3.0D, 2.2F, 0);
                    }
                }
                case STATE_UPPERCUT_HORIZONTAL -> {
                    if (this.attackTicks == 6) {
                        spawnPrimeFlameArc(4, 4.0D, 90.0F, 2.0F, 0);
                    }
                }
                case STATE_UPPERCUT_VERTICAL -> {
                    if (this.attackTicks == 12) {
                        spawnForwardPrimeFlameStrike(3.0D, 2.4F, 0);
                    }
                }
                case STATE_JAB_1 -> {
                    if (this.attackTicks == getScaledTick(18)) {
                        spawnForwardPrimeFlameStrike(2.2D, 1.7F, 0);
                    }
                }
                case STATE_JAB_2 -> {
                    if (this.attackTicks == getScaledTick(17)) {
                        spawnForwardPrimeFlameStrike(2.4D, 1.8F, 0);
                    }
                }
                case STATE_JAB_3 -> {
                    if (this.attackTicks == getScaledTick(18)) {
                        spawnForwardPrimeFlameStrike(2.8D, 2.1F, 0);
                    }
                }
                case STATE_POWER_SLAM -> {
                    if (this.attackTicks == getScaledTick(38)) {
                        spawnPrimeFlameArc(6, 6.0D, 360.0F, 2.3F, 0);
                        spawnPrimeFireballVolley(target, 3, 4, 1.05D, 18.0F);
                    }
                }
                case STATE_JAB_EX_ONE -> {
                    if (this.attackTicks == getScaledTick(23) || this.attackTicks == getScaledTick(41)) {
                        spawnForwardPrimeFlameStrike(3.2D, 2.2F, 0);
                    }
                    if (this.attackTicks == getScaledTick(41)) {
                        spawnPrimeFireballVolley(target, 2, 5, 0.9D, 10.0F);
                    }
                }
                case STATE_COMBO_RUSH_1 -> {
                    if (this.attackTicks == getScaledTick(23) || this.attackTicks == getScaledTick(41)) {
                        spawnForwardPrimeFlameStrike(3.0D, 2.0F, 0);
                    }
                }
                case STATE_COMBO_RUSH_2 -> {
                    if (this.attackTicks == 6) {
                        spawnPrimeFlameArc(4, 4.0D, 90.0F, 2.0F, 0);
                    }
                }
                case STATE_COMBO_RUSH_3 -> {
                    if (this.attackTicks == getScaledTick(38)) {
                        spawnPrimeFlameArc(6, 6.5D, 360.0F, 2.4F, 0);
                        spawnPrimeFireballVolley(target, 2, 4, 1.0D, 16.0F);
                    }
                }
                case STATE_OVERHEAD_GUARDBREAKER -> {
                    if (this.attackTicks == getScaledTick(38)) {
                        spawnForwardPrimeFlameStrike(4.0D, 2.7F, 0);
                    }
                }
                case STATE_CATCH_START -> {
                    if (this.attackTicks == 47) {
                        spawnForwardPrimeFlameStrike(1.8D, 1.8F, 0);
                    }
                }
                case STATE_CATCH_SUCCESS -> {
                    if (this.attackTicks == 32 || this.attackTicks == 58) {
                        spawnForwardPrimeFlameStrike(1.5D, 2.2F, 0);
                    }
                }
                default -> {
                }
            }
        } else {
            if (state == STATE_ROCK_END && this.attackTicks == 10) {
                spawnPrimeFireballVolley(target, 1, 8, 0.75D, 8.0F);
            } else if (state == STATE_OVERHEAD_GUARDBREAKER && this.attackTicks == getScaledTick(18)) {
                spawnPrimeFireballVolley(target, 1, 6, 0.8D, 6.0F);
            }
        }
    }

    private boolean isPrimeSecondForm() {
        return this.getBossPhase() >= 2;
    }

    private void spawnForwardPrimeFlameStrike(double forwardDistance, float radius, int delay) {
        float yaw = this.getYRot() * ((float) Math.PI / 180F);
        double x = this.getX() - Mth.sin(yaw) * forwardDistance;
        double z = this.getZ() + Mth.cos(yaw) * forwardDistance;
        this.spawnFlameStrike(x, z, this.getY() - 2.0D, this.getY() + 3.0D, this.getYRot(), 26, 3, delay, radius, true);
    }

    private void spawnTargetPrimeFlameStrike(LivingEntity target, float radius, int delay) {
        if (target == null) {
            spawnForwardPrimeFlameStrike(4.0D, radius, delay);
            return;
        }
        this.spawnFlameStrike(target.getX(), target.getZ(), target.getY() - 2.0D, target.getY() + 3.0D, this.getYRot(), 28, 4, delay, radius, true);
    }

    private void spawnPrimeFlameArc(int count, double distance, float arc, float radius, int delay) {
        if (count <= 0) {
            return;
        }
        float start = this.yBodyRot - arc * 0.5F;
        float step = count == 1 ? 0.0F : arc / (float) (count - 1);
        for (int i = 0; i < count; i++) {
            float yaw = (start + step * i) * ((float) Math.PI / 180F);
            double x = this.getX() - Mth.sin(yaw) * distance;
            double z = this.getZ() + Mth.cos(yaw) * distance;
            this.spawnFlameStrike(x, z, this.getY() - 2.0D, this.getY() + 3.0D, start + step * i, 28, 4, delay + i, radius, true);
        }
    }

    private void spawnPrimeFireballVolley(LivingEntity target, int count, int delayStep, double lateralSpread, float pitchLift) {
        if (target == null || count <= 0) {
            return;
        }
        Vec3 forward = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D)
                .subtract(this.position().add(0.0D, 2.0D, 0.0D));
        if (forward.lengthSqr() < 1.0E-4D) {
            forward = this.getLookAngle();
        }
        Vec3 side = new Vec3(-forward.z, 0.0D, forward.x);
        if (side.lengthSqr() < 1.0E-4D) {
            side = new Vec3(1.0D, 0.0D, 0.0D);
        }
        side = side.normalize();
        for (int i = 0; i < count; i++) {
            double offsetIndex = i - (count - 1) * 0.5D;
            Vec3 spawnPos = this.position()
                    .add(0.0D, 2.2D + Math.sin(i * 0.7D) * 0.25D, 0.0D)
                    .add(side.scale(offsetIndex * lateralSpread));
            Prime_Fireball_Entity fireball = new Prime_Fireball_Entity(this.level(), this);
            fireball.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            fireball.setUp(i * delayStep);
            fireball.setXRot(-pitchLift);
            fireball.setYRot(this.getYRot());
            Vec3 initialMotion = Vec3.directionFromRotation(-pitchLift, this.getYRot()).scale(0.1D);
            fireball.setDeltaMovement(initialMotion);
            this.level().addFreshEntity(fireball);
        }
        this.playSound(SoundEvents.GHAST_SHOOT, 1.3F, 0.7F + this.getRandom().nextFloat() * 0.2F);
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

    private void breakPlayerShield(LivingEntity target, int ticks) {
        if (target instanceof Player player) {
            if (player.isBlocking()) {
                player.getCooldowns().addCooldown(player.getUseItem().getItem(), ticks);
                player.stopUsingItem();
                this.level().broadcastEntityEvent(player, (byte) 30);
            }
        }
    }
}
