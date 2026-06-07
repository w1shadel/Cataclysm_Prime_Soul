package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.AI.InternalMoveGoal;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.IABoss_monster;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.entity.etc.CMBossInfoServer;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.goal.MaledictusAttackGoal;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.goal.MaledictusBackstepGoal;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.goal.MaledictusStateGoal;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class Maledictus_PrimeEntity extends IABoss_monster {
    public static final int ATTACK_JAB_1 = 1;
    public static final int ATTACK_JAB_2 = 2;
    public static final int ATTACK_JAB_3 = 3;
    public static final int ATTACK_CHARGE = 4;
    public static final int ATTACK_COUNTER_START = 5;
    public static final int ATTACK_COUNTER_SUCCESS = 6;
    public static final int ATTACK_COUNTER_FAIL = 7;
    public static final int ATTACK_SHOCKWAVE_START = 8;
    public static final int ATTACK_SHOCKWAVE_END = 9;
    public static final int ATTACK_GRAB_START = 10;
    public static final int ATTACK_GRAB_SUCCESS = 11;
    public static final int ATTACK_GRAB_FAIL = 12;
    public static final int ATTACK_PHANTOM_SPEAR_CHARGE = 13;
    public static final int ATTACK_PHANTOM_MACE_CRUSH = 14;
    public static final int ATTACK_PHANTOM_BOW_SNIPE = 15;
    public static final int ATTACK_EX_JAB_1 = 16;
    public static final int ATTACK_EX_JAB_2 = 17;
    public static final int ATTACK_FAR_START = 18;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState jab1AnimationState = new AnimationState();
    public final AnimationState jab2AnimationState = new AnimationState();
    public final AnimationState jab3AnimationState = new AnimationState();
    public final AnimationState chargeAnimationState = new AnimationState();
    public final AnimationState counterStartAnimationState = new AnimationState();
    public final AnimationState counterSuccessAnimationState = new AnimationState();
    public final AnimationState counterFailAnimationState = new AnimationState();
    public final AnimationState shockwaveStartAnimationState = new AnimationState();
    public final AnimationState shockwaveEndAnimationState = new AnimationState();
    public final AnimationState grabStartAnimationState = new AnimationState();
    public final AnimationState grabSuccessAnimationState = new AnimationState();
    public final AnimationState grabFailAnimationState = new AnimationState();
    public final AnimationState phantomSpearChargeAnimationState = new AnimationState();
    public final AnimationState phantomMaceCrushAnimationState = new AnimationState();
    public final AnimationState phantomBowSnipeAnimationState = new AnimationState();
    public final AnimationState exJab1AnimationState = new AnimationState();
    public final AnimationState exJab2AnimationState = new AnimationState();
    public final AnimationState farStartAnimationState = new AnimationState();

    private final CMBossInfoServer bossEvent;
    private int attackCooldown;
    private boolean counterGuarding;
    private boolean shockwaveJumped;
    private Entity grabbedEntity;
    private Vec3 thrownSwordPos;
    private Vec3 thrownSwordMotion;
    private int thrownSwordTicks;

    public Maledictus_PrimeEntity(EntityType<? extends Monster> entity, Level world) {
        super(entity, world);
        this.xpReward = 500;
        this.setMaxUpStep(1.5F);
        this.bossEvent = new CMBossInfoServer(this.getDisplayName(), BossEvent.BossBarColor.GREEN, true, 9);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 600.0D)
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.33D)
                .add(Attributes.ATTACK_DAMAGE, 18.0D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_JAB_1));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_JAB_2));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_JAB_3));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_CHARGE));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_COUNTER_START));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_COUNTER_SUCCESS));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_COUNTER_FAIL));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_SHOCKWAVE_START));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_SHOCKWAVE_END));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_GRAB_START));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_GRAB_SUCCESS));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_GRAB_FAIL));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_PHANTOM_SPEAR_CHARGE));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_PHANTOM_MACE_CRUSH));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_PHANTOM_BOW_SNIPE));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_EX_JAB_1));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_EX_JAB_2));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_FAR_START));
        this.goalSelector.addGoal(1, new MaledictusAttackGoal(this));
        this.goalSelector.addGoal(2, new MaledictusBackstepGoal(this));
        this.goalSelector.addGoal(4, new InternalMoveGoal(this, false, 1.3D));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public boolean isAttackReady() {
        return this.attackCooldown <= 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            int attackState = this.getAttackState();
            this.idleAnimationState.animateWhen(this.isAlive() && attackState == 0, this.tickCount);
            this.walkAnimationState.animateWhen(this.isAlive(), this.tickCount);
            this.jab1AnimationState.animateWhen(this.isAlive() && attackState == ATTACK_JAB_1, this.tickCount);
            this.jab2AnimationState.animateWhen(this.isAlive() && attackState == ATTACK_JAB_2, this.tickCount);
            this.jab3AnimationState.animateWhen(this.isAlive() && attackState == ATTACK_JAB_3, this.tickCount);
            this.chargeAnimationState.animateWhen(this.isAlive() && attackState == ATTACK_CHARGE, this.tickCount);
            this.counterStartAnimationState.animateWhen(this.isAlive() && attackState == ATTACK_COUNTER_START, this.tickCount);
            this.counterSuccessAnimationState.animateWhen(this.isAlive() && attackState == ATTACK_COUNTER_SUCCESS, this.tickCount);
            this.counterFailAnimationState.animateWhen(this.isAlive() && attackState == ATTACK_COUNTER_FAIL, this.tickCount);
            this.shockwaveStartAnimationState.animateWhen(this.isAlive() && attackState == ATTACK_SHOCKWAVE_START, this.tickCount);
            this.shockwaveEndAnimationState.animateWhen(this.isAlive() && attackState == ATTACK_SHOCKWAVE_END, this.tickCount);
            this.grabStartAnimationState.animateWhen(this.isAlive() && attackState == ATTACK_GRAB_START, this.tickCount);
            this.grabSuccessAnimationState.animateWhen(this.isAlive() && attackState == ATTACK_GRAB_SUCCESS, this.tickCount);
            this.grabFailAnimationState.animateWhen(this.isAlive() && attackState == ATTACK_GRAB_FAIL, this.tickCount);
            this.phantomSpearChargeAnimationState.animateWhen(
                    this.isAlive() && attackState == ATTACK_PHANTOM_SPEAR_CHARGE,
                    this.tickCount
            );
            this.phantomMaceCrushAnimationState.animateWhen(
                    this.isAlive() && attackState == ATTACK_PHANTOM_MACE_CRUSH,
                    this.tickCount
            );
            this.phantomBowSnipeAnimationState.animateWhen(
                    this.isAlive() && attackState == ATTACK_PHANTOM_BOW_SNIPE,
                    this.tickCount
            );
            this.exJab1AnimationState.animateWhen(this.isAlive() && attackState == ATTACK_EX_JAB_1, this.tickCount);
            this.exJab2AnimationState.animateWhen(this.isAlive() && attackState == ATTACK_EX_JAB_2, this.tickCount);
            this.farStartAnimationState.animateWhen(this.isAlive() && attackState == ATTACK_FAR_START, this.tickCount);
        } else {
            if (this.attackCooldown > 0) {
                this.attackCooldown--;
            }
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
            this.tickAttackState();
        }
    }

    public AnimationState getAnimationState(String name) {
        if ("idle".equals(name)) {
            return this.idleAnimationState;
        }else if ("walk".equals(name)) {
            return this.walkAnimationState;
        }else if ("jab_1".equals(name)) {
            return this.jab1AnimationState;
        } else if ("jab_2".equals(name)) {
            return this.jab2AnimationState;
        } else if ("jab_3".equals(name)) {
            return this.jab3AnimationState;
        } else if ("charge".equals(name)) {
            return this.chargeAnimationState;
        } else if ("counter_start".equals(name)) {
            return this.counterStartAnimationState;
        } else if ("counter_success".equals(name)) {
            return this.counterSuccessAnimationState;
        } else if ("counter_fail".equals(name)) {
            return this.counterFailAnimationState;
        } else if ("shockwave_start".equals(name)) {
            return this.shockwaveStartAnimationState;
        } else if ("shockwave_end".equals(name)) {
            return this.shockwaveEndAnimationState;
        } else if ("grab_start".equals(name)) {
            return this.grabStartAnimationState;
        } else if ("grab_success".equals(name)) {
            return this.grabSuccessAnimationState;
        } else if ("grab_fail".equals(name)) {
            return this.grabFailAnimationState;
        } else if ("phantom_spear_charge".equals(name)) {
            return this.phantomSpearChargeAnimationState;
        } else if ("phantom_mace_crush".equals(name)) {
            return this.phantomMaceCrushAnimationState;
        } else if ("phantom_bow_snipe".equals(name)) {
            return this.phantomBowSnipeAnimationState;
        } else if ("ex_jab_1".equals(name)) {
            return this.exJab1AnimationState;
        } else if ("ex_jab_2".equals(name)) {
            return this.exJab2AnimationState;
        } else if ("far_start".equals(name)) {
            return this.farStartAnimationState;
        }
        return new AnimationState();
    }

    @Override
    public void setAttackState(int state) {
        super.setAttackState(state);
        this.attackTicks = 0;
        this.counterGuarding = false;
        this.shockwaveJumped = false;
        this.thrownSwordPos = null;
        this.thrownSwordMotion = null;
        this.thrownSwordTicks = 0;
        if (state != ATTACK_GRAB_SUCCESS && this.grabbedEntity != null) {
            this.grabbedEntity.stopRiding();
            this.grabbedEntity = null;
        }
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (!this.level().isClientSide()
                && this.counterGuarding
                && this.getAttackState() == ATTACK_COUNTER_START
                && source.getEntity() != null
                && source.getEntity() != this) {
            this.setAttackState(ATTACK_COUNTER_SUCCESS);
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (passenger == this.grabbedEntity && this.getAttackState() == ATTACK_GRAB_SUCCESS) {
            Vec3 hand = this.getApproxRightHandPosition();
            moveFunction.accept(passenger, hand.x, hand.y, hand.z);
            return;
        }
        super.positionRider(passenger, moveFunction);
    }

    private void tickAttackState() {
        int state = this.getAttackState();
        if (state == 0) {
            return;
        }

        switch (state) {
            case ATTACK_JAB_1 -> {
                if (this.attackTicks == seconds(1.25F)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5F, 0.6F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 15.0F, 0.15F, 0, 8);
                    if (this.performForwardArcDamage(1.0F, 3.4F, 110.0F, 0.35F, 0.1D, 0.0D)) {
                        this.setAttackState(ATTACK_JAB_2);
                    }
                } else if (this.attackTicks >= seconds(1.58F)) {
                    this.finishAttack(25);
                }
            }
            case ATTACK_JAB_2 -> {
                if (this.attackTicks == seconds(0.54F)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5F, 0.7F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 12.0F, 0.1F, 0, 6);
                    this.performForwardArcDamage(0.9F, 3.4F, 110.0F, 0.35F, 0.1D, 0.0D);
                    this.setAttackState(ATTACK_JAB_3);
                }
            }
            case ATTACK_JAB_3 -> {
                if (this.attackTicks == seconds(0.96F)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.8F, 0.5F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 20.0F, 0.2F, 0, 12);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX(), this.getY() + 1.0D, this.getZ(), 3, 0.5D, 0.5D, 0.5D, 0.0D);
                    }
                    this.performForwardArcDamage(1.25F, 3.8F, 120.0F, 0.65F, 0.2D, 0.05D);
                } else if (this.attackTicks >= seconds(1.83F)) {
                    this.finishAttack(45);
                }
            }
            case ATTACK_CHARGE -> {
                if (this.attackTicks >= seconds(1.83F) && this.attackTicks <= seconds(2.75F)) {
                    this.chargeForward(1.15D);
                    this.performForwardArcDamage(0.7F, 3.2F, 100.0F, 0.45F, 0.25D, 0.0D);
                    if (this.attackTicks % 2 == 0 && this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 0.1D, this.getZ(), 4, 0.3D, 0.1D, 0.3D, 0.05D);
                    }
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.65D, 1.0D, 0.65D));
                }
                if (this.attackTicks == seconds(2.80F)) {
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 1.2F, 0.7F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 25.0F, 0.3F, 0, 15);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 0.5D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    }
                }
                if (this.attackTicks >= seconds(3.04F)) {
                    this.finishAttack(55);
                }
            }
            case ATTACK_COUNTER_START -> {
                if (this.attackTicks == seconds(1.0F)) {
                    this.counterGuarding = true;
                } else if (this.attackTicks >= seconds(4.0F)) {
                    this.setAttackState(ATTACK_COUNTER_FAIL);
                }
            }
            case ATTACK_COUNTER_SUCCESS -> {
                if (this.attackTicks == seconds(0.92F)) {
                    this.playSound(SoundEvents.ANVIL_LAND, 1.5F, 0.5F);
                    this.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 1.0F, 0.8F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 40.0F, 0.4F, 0, 20);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.CRIT, this.getX(), this.getY() + 1.2D, this.getZ(), 25, 0.8D, 0.8D, 0.8D, 0.2D);
                        serverLevel.sendParticles(ParticleTypes.FLASH, this.getX(), this.getY() + 1.2D, this.getZ(), 2, 0.2D, 0.2D, 0.2D, 0.0D);
                    }
                    this.performForwardArcDamage(1.8F, 4.0F, 130.0F, 1.0F, 0.45D, 0.15D);
                } else if (this.attackTicks >= seconds(1.54F)) {
                    this.finishAttack(50);
                }
            }
            case ATTACK_COUNTER_FAIL -> {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.15D, 1.0D, 0.15D));
                if (this.attackTicks >= seconds(1.0F)) {
                    this.finishAttack(70);
                }
            }
            case ATTACK_SHOCKWAVE_START -> {
                if (!this.shockwaveJumped && this.attackTicks >= seconds(1.0F)) {
                    this.shockwaveJumped = true;
                    this.setDeltaMovement(this.getDeltaMovement().x, 0.95D, this.getDeltaMovement().z);
                    this.hasImpulse = true;
                }
                if (this.shockwaveJumped && this.onGround() && this.attackTicks > seconds(1.1F)) {
                    this.setAttackState(ATTACK_SHOCKWAVE_END);
                }
            }
            case ATTACK_SHOCKWAVE_END -> {
                if (this.attackTicks == seconds(0.21F)) {
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 2.0F, 0.6F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 50.0F, 0.5F, 0, 30);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 0.1D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                        serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY() + 0.2D, this.getZ(), 30, 2.0D, 0.2D, 2.0D, 0.1D);
                        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.2D, this.getZ(), 20, 1.5D, 0.5D, 1.5D, 0.05D);
                    }
                    this.performPointBlankShockwave();
                } else if (this.attackTicks >= seconds(1.37F)) {
                    this.finishAttack(55);
                }
            }
            case ATTACK_GRAB_START -> {
                if (this.attackTicks >= seconds(1.33F) && this.attackTicks <= seconds(1.45F)) {
                    this.chargeForward(0.95D);
                }
                if (this.attackTicks == seconds(1.33F)) {
                    LivingEntity grabbed = this.findGrabTarget();
                    if (grabbed != null) {
                        this.grabbedEntity = grabbed;
                        grabbed.startRiding(this, true);
                        this.setAttackState(ATTACK_GRAB_SUCCESS);
                    }
                } else if (this.attackTicks >= seconds(1.45F)) {
                    this.setAttackState(ATTACK_GRAB_FAIL);
                }
            }
            case ATTACK_GRAB_SUCCESS -> {
                if (this.grabbedEntity == null || !this.grabbedEntity.isAlive()) {
                    this.setAttackState(ATTACK_GRAB_FAIL);
                    return;
                }
                if (this.attackTicks == seconds(0.63F)) {
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 1.2F, 0.8F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 25.0F, 0.25F, 0, 15);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 0.2D, this.getZ(), 15, 1.0D, 0.2D, 1.0D, 0.05D);
                    }
                    if (this.grabbedEntity instanceof LivingEntity living) {
                        living.hurt(this.damageSources().mobAttack(this), this.getAttackDamage(1.2F));
                    }
                }
                if (this.attackTicks == seconds(1.58F)) {
                    this.playSound(SoundEvents.ENDER_DRAGON_FLAP, 1.5F, 0.8F);
                    this.setDeltaMovement(this.getDeltaMovement().x, 1.05D, this.getDeltaMovement().z);
                    this.hasImpulse = true;
                }
                if (this.attackTicks == seconds(3.0F)) {
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 2.5F, 0.5F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 60.0F, 0.6F, 0, 35);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 0.1D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                        serverLevel.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY() + 0.2D, this.getZ(), 20, 1.5D, 0.5D, 1.5D, 0.1D);
                        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.2D, this.getZ(), 25, 2.0D, 0.5D, 2.0D, 0.05D);
                    }
                    if (this.grabbedEntity instanceof LivingEntity living) {
                        living.hurt(this.damageSources().mobAttack(this), this.getAttackDamage(2.0F));
                    }
                    this.performAreaDamage(1.1F, 0.8F, 4.5D, 2.5D, 0.2D, 0.35D);
                }
                if (this.attackTicks == seconds(3.08F)) {
                    this.grabbedEntity.stopRiding();
                    this.grabbedEntity = null;
                } else if (this.attackTicks >= seconds(4.25F)) {
                    this.finishAttack(70);
                }
            }
            case ATTACK_GRAB_FAIL -> {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.1D, 1.0D, 0.1D));
                if (this.attackTicks >= seconds(1.0F)) {
                    this.finishAttack(80);
                }
            }
            case ATTACK_EX_JAB_1 -> {
                if (this.attackTicks == seconds(1.54F)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5F, 0.5F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 15.0F, 0.15F, 0, 10);
                    if (this.performForwardArcDamage(1.25F, 3.6F, 120.0F, 0.55F, 0.15D, 0.0D)) {
                        this.setAttackState(ATTACK_EX_JAB_2);
                    }
                } else if (this.attackTicks >= seconds(2.04F)) {
                    this.finishAttack(45);
                }
            }
            case ATTACK_EX_JAB_2 -> {
                if (this.attackTicks == seconds(0.13F)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5F, 0.8F);
                    this.performForwardArcDamage(0.8F, 3.5F, 120.0F, 0.45F, 0.1D, 0.0D);
                }
                if (this.attackTicks == seconds(1.42F)) {
                    this.playSound(SoundEvents.ANVIL_LAND, 1.5F, 0.6F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 30.0F, 0.3F, 0, 18);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.CRIT, this.getX(), this.getY() + 1.2D, this.getZ(), 15, 0.6D, 0.6D, 0.6D, 0.1D);
                    }
                    this.performForwardArcDamage(2.2F, 4.2F, 130.0F, 1.15F, 0.35D, 0.2D);
                } else if (this.attackTicks >= seconds(2.45F)) {
                    this.finishAttack(65);
                }
            }
            case ATTACK_FAR_START -> {
                if (this.attackTicks == seconds(1.25F)) {
                    this.playSound(SoundEvents.TRIDENT_THROW, 1.5F, 0.7F);
                    this.throwSword();
                }
                this.tickThrownSword();
                if (this.attackTicks >= seconds(3.16F)) {
                    this.finishAttack(70);
                }
            }
            case ATTACK_PHANTOM_SPEAR_CHARGE, ATTACK_PHANTOM_MACE_CRUSH, ATTACK_PHANTOM_BOW_SNIPE -> {
                if (this.attackTicks >= 70) {
                    this.finishAttack(45);
                }
            }
            default -> this.finishAttack(30);
        }
    }

    private void finishAttack(int cooldown) {
        this.attackCooldown = cooldown;
        this.setAttackState(0);
    }

    private static int seconds(float seconds) {
        return Math.round(seconds * 20.0F);
    }

    private void chargeForward(double speed) {
        float yaw = this.getYRot() * ((float) Math.PI / 180F);
        this.setDeltaMovement(-Mth.sin(yaw) * speed, this.getDeltaMovement().y, Mth.cos(yaw) * speed);
        this.hasImpulse = true;
    }

    private float getAttackDamage(float multiplier) {
        return (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier;
    }

    private boolean performForwardArcDamage(float damageMultiplier, float range, float arc, float knockback,
                                            double forwardPush, double verticalImpulse) {
        boolean hit = false;
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(range, 2.0D, range));
        for (LivingEntity target : targets) {
            if (this.canDamageTarget(target) && this.isInFrontArc(target, arc) && this.distanceTo(target) <= range + this.getBbWidth()) {
                if (target.hurt(this.damageSources().mobAttack(this), this.getAttackDamage(damageMultiplier))) {
                    this.applyAttackKnockback(target, knockback, forwardPush, verticalImpulse);
                    hit = true;
                }
            }
        }
        return hit;
    }

    private boolean performAreaDamage(float damageMultiplier, float knockback, double xzRange, double yRange,
                                      double forwardPush, double verticalImpulse) {
        boolean hit = false;
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(xzRange, yRange, xzRange));
        for (LivingEntity target : targets) {
            if (this.canDamageTarget(target) && this.distanceTo(target) <= xzRange + this.getBbWidth()) {
                if (target.hurt(this.damageSources().mobAttack(this), this.getAttackDamage(damageMultiplier))) {
                    this.applyAttackKnockback(target, knockback, forwardPush, verticalImpulse);
                    hit = true;
                }
            }
        }
        return hit;
    }

    private void performPointBlankShockwave() {
        this.performForwardArcDamage(2.6F, 2.2F, 90.0F, 1.2F, 0.25D, 0.45D);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(5.0D, 2.5D, 5.0D));
        for (LivingEntity target : targets) {
            if (this.canDamageTarget(target) && this.distanceTo(target) <= 5.0D + this.getBbWidth()) {
                target.setDeltaMovement(target.getDeltaMovement().add(0.0D, 0.9D, 0.0D));
                target.hasImpulse = true;
            }
        }
    }

    private LivingEntity findGrabTarget() {
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(3.2D, 2.0D, 3.2D));
        for (LivingEntity target : targets) {
            if (this.canDamageTarget(target) && this.isInFrontArc(target, 80.0F)) {
                return target;
            }
        }
        return null;
    }

    private void throwSword() {
        this.thrownSwordPos = this.getEyePosition();
        this.thrownSwordMotion = Vec3.directionFromRotation(0.0F, this.getYRot()).normalize().scale(1.4D);
        this.thrownSwordTicks = 0;
    }

    private void tickThrownSword() {
        if (this.thrownSwordPos == null || this.thrownSwordMotion == null) {
            return;
        }
        Vec3 next = this.thrownSwordPos.add(this.thrownSwordMotion);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL, next.x, next.y, next.z, 2, 0.1D, 0.1D, 0.1D, 0.0D);
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, next.x, next.y, next.z, 2, 0.1D, 0.1D, 0.1D, 0.0D);
        }
        AABB swordBox = new AABB(this.thrownSwordPos, next).inflate(0.75D);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, swordBox);
        for (LivingEntity target : targets) {
            if (this.canDamageTarget(target) && target.hurt(this.damageSources().mobAttack(this), this.getAttackDamage(1.2F))) {
                Vec3 inFront = target.position().subtract(Vec3.directionFromRotation(0.0F, target.getYRot()).normalize().scale(1.2D));
                this.teleportTo(inFront.x, target.getY(), inFront.z);
                this.lookAt(target, 360.0F, 360.0F);
                this.setAttackState(this.getRandom().nextBoolean() ? ATTACK_JAB_1 : ATTACK_EX_JAB_1);
                return;
            }
        }
        this.thrownSwordPos = next;
        this.thrownSwordTicks++;
        if (this.thrownSwordTicks > 30 || this.thrownSwordPos.distanceToSqr(this.position()) > 900.0D) {
            this.thrownSwordPos = null;
            this.thrownSwordMotion = null;
        }
    }

    private Vec3 getApproxRightHandPosition() {
        float yaw = this.yBodyRot * ((float) Math.PI / 180F);
        double forwardX = -Mth.sin(yaw);
        double forwardZ = Mth.cos(yaw);
        double rightX = Mth.cos(yaw);
        double rightZ = Mth.sin(yaw);

        double forwardOffset = 1.1D;
        double rightOffset = -0.85D;
        double heightOffset = 1.7D;

        if (this.getAttackState() == ATTACK_GRAB_SUCCESS) {
            int ticks = this.attackTicks;
            if (ticks <= 12) {
                // 振り下ろし（徐々に下がる）
                double progress = ticks / 12.0D;
                heightOffset = 1.7D - (1.375D * progress);
            } else if (ticks <= 29) {
                // 地面に叩きつけ中
                heightOffset = 0.325D;
            } else if (ticks <= 32) {
                // 持ち上げ（30〜32 ticks）
                double progress = (ticks - 29) / 3.0D;
                heightOffset = 0.325D + (0.5625D * progress);
                forwardOffset = 1.1D + (2.25D * progress);
            } else if (ticks <= 59) {
                // 持ち上げた状態をキープ
                heightOffset = 0.8875D;
                forwardOffset = 3.35D;
            } else if (ticks <= 62) {
                // 再度叩きつけ（60〜62 ticks）
                double progress = (ticks - 59) / 3.0D;
                heightOffset = 0.8875D - (0.8125D * progress);
                forwardOffset = 3.35D - (1.0625D * progress);
            } else {
                heightOffset = 1.7D;
            }
        }

        return new Vec3(
                this.getX() + forwardX * forwardOffset - rightX * rightOffset,
                this.getY() + heightOffset,
                this.getZ() + forwardZ * forwardOffset - rightZ * rightOffset
        );
    }

    private boolean canDamageTarget(LivingEntity target) {
        return target != this && target.isAlive() && this.canAttack(target) && !this.isAlliedTo(target)
                && (!(target instanceof Player player) || (!player.isCreative() && !player.isSpectator()));
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

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, net.minecraft.world.damagesource.DamageSource source) {
        return false;
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
