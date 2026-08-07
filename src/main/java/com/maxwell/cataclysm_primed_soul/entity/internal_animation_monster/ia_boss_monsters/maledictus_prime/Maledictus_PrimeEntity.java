package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime;

import com.github.L_Ender.cataclysm.client.particle.Options.RingParticleOptions;
import com.github.L_Ender.cataclysm.client.particle.RingParticle.EnumRingBehavior;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.AI.InternalMoveGoal;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.IABoss_monster;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.entity.etc.CMBossInfoServer;
import com.github.L_Ender.cataclysm.entity.etc.IHoldEntity;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.init.ModSounds;
import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.api.entity.IShaderBoss;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.goal.MaledictusAttackGoal;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.goal.MaledictusBackstepGoal;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.goal.MaledictusStateGoal;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.sub.Maledictus_PrimeSwordEntity;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.sub.Maledictus_PrimeSwordSpikeEntity;
import com.maxwell.cataclysm_primed_soul.init.ModEntities;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Primed_Soul.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@SuppressWarnings("removal")
public class Maledictus_PrimeEntity extends IABoss_monster implements IHoldEntity, IShaderBoss {
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
    public static final int ATTACK_HEAD_BREAK = 15;
    public static final int ATTACK_EX_JAB_1 = 16;
    public static final int ATTACK_EX_JAB_2 = 17;
    public static final int ATTACK_EX_JAB_3 = 18;
    public static final int ATTACK_GRAB_SLOOP = 13;
    public static final int ATTACK_GRAB_SEND = 14;
    public static final int ATTACK_ULTIMATE = 35;
    public static final int BACKSTEP = 80;
    public static final int BACKSTEP_BEFORE_CHARGE = 81;
    private static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> PHASE_2 =
            net.minecraft.network.syncher.SynchedEntityData.defineId(Maledictus_PrimeEntity.class, net.minecraft.network.syncher.EntityDataSerializers.BOOLEAN);
    private static final ResourceLocation SHADER = new ResourceLocation(Primed_Soul.MODID, "shaders/post/maledictus_debuff.json");
    private int grabSloopTicks = 0;
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
    public final AnimationState headbreakAnimationState = new AnimationState();
    public final AnimationState exJab1AnimationState = new AnimationState();
    public final AnimationState exJab2AnimationState = new AnimationState();
    public final AnimationState exJab3AnimationState = new AnimationState();
    public final AnimationState backstepAnimationState = new AnimationState();
    public final AnimationState grabSloopAnimationState = new AnimationState();
    public final AnimationState grabSendAnimationState = new AnimationState();
    public final AnimationState ultimateAnimationState = new AnimationState();
    private final CMBossInfoServer bossEvent;
    private final java.util.List<net.minecraft.world.entity.LivingEntity> chargedHitEntities = new java.util.ArrayList<>();
    private int jabCooldown;
    private int chargeCooldown;
    private int counterCooldown;
    private int shockwaveCooldown;
    private int grabCooldown;
    private int exJabCooldown;
    private int phantomCooldown;
    private int backstepCooldown;
    private boolean counterGuarding;
    private boolean shockwaveJumped;
    private Entity grabbedEntity;
    private net.minecraft.world.phys.Vec3 chargeDirection = null;

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

    private static int seconds(float seconds) {
        return Math.round(seconds * 20.0F);
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public static void onEntityDismount(net.minecraftforge.event.entity.EntityMountEvent event) {
        if (event.isDismounting() && event.getEntityBeingMounted() instanceof Maledictus_PrimeEntity prime) {
            if (prime.getAttackState() == Maledictus_PrimeEntity.ATTACK_GRAB_SUCCESS) {
                if (prime.getGrabbedEntity() == event.getEntity()) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @Override
    public ResourceLocation getDebuffShader() {
        return SHADER;
    }

    @Override
    public int getDebuffLevel() {
        float hpPct = this.getHealth() / this.getMaxHealth();
        if (hpPct <= 0.5F) return 3;
        if (hpPct <= 0.75F) return 2;
        return 1;
    }

    @Override
    public double getDebuffRangeSq() {
        return 64.0D * 64.0D;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHASE_2, false);
    }

    public boolean isPhase2() {
        return this.entityData.get(PHASE_2);
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
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_GRAB_SLOOP));   // 13
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_GRAB_SEND));    // 14
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_HEAD_BREAK));   // 15
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_EX_JAB_1));     // 16
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_EX_JAB_2));     // 17
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_EX_JAB_3));     // 18
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_ULTIMATE));     // 35
        this.goalSelector.addGoal(1, new MaledictusAttackGoal(this));
        this.goalSelector.addGoal(2, new MaledictusBackstepGoal(this));
        this.goalSelector.addGoal(4, new InternalMoveGoal(this, false, 1.3D));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public boolean isJabReady() {
        return this.jabCooldown <= 0;
    }

    public boolean isChargeReady() {
        return this.chargeCooldown <= 0;
    }

    public boolean isCounterReady() {
        return this.counterCooldown <= 0;
    }

    public boolean isShockwaveReady() {
        return this.shockwaveCooldown <= 0;
    }

    public boolean isGrabReady() {
        return this.grabCooldown <= 0;
    }

    public boolean isExJabReady() {
        return this.exJabCooldown <= 0;
    }

    public boolean isPhantomReady() {
        return this.phantomCooldown <= 0;
    }

    public boolean isBackstepReady() {
        return this.backstepCooldown <= 0;
    }

    public void setPhantomCooldown(int cd) {
        this.phantomCooldown = cd;
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(false);
        if (!this.level().isClientSide()) {
            if (this.getAttackState() == ATTACK_GRAB_SLOOP) {
                this.grabSloopTicks++;
            }
        }
        int currentAttackState = this.getAttackState();
        if (currentAttackState != 0) {
            if (currentAttackState != ATTACK_CHARGE
                    && currentAttackState != ATTACK_SHOCKWAVE_START
                    && currentAttackState != ATTACK_GRAB_START
                    && currentAttackState != BACKSTEP
                    && currentAttackState != BACKSTEP_BEFORE_CHARGE) {
                this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
            }
        }
        if (this.level().isClientSide()) {
            int attackState = this.getAttackState();
            boolean canPlayIdleWalk = this.getAttackState() == 0 && this.isAlive();
            boolean isMoving = this.walkAnimation.isMoving();
            this.idleAnimationState.animateWhen(!isMoving && canPlayIdleWalk, this.tickCount);
            this.walkAnimationState.animateWhen(isMoving && canPlayIdleWalk, this.tickCount);
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
            this.headbreakAnimationState.animateWhen(this.isAlive() && attackState == ATTACK_HEAD_BREAK, this.tickCount);
            this.exJab1AnimationState.animateWhen(this.isAlive() && attackState == ATTACK_EX_JAB_1, this.tickCount);
            this.exJab2AnimationState.animateWhen(this.isAlive() && attackState == ATTACK_EX_JAB_2, this.tickCount);
            this.exJab3AnimationState.animateWhen(this.isAlive() && attackState == ATTACK_EX_JAB_3, this.tickCount);
            this.grabSloopAnimationState.animateWhen(this.isAlive() && attackState == ATTACK_GRAB_SLOOP, this.tickCount);
            this.grabSendAnimationState.animateWhen(this.isAlive() && attackState == ATTACK_GRAB_SEND, this.tickCount);
            this.ultimateAnimationState.animateWhen(this.isAlive() && attackState == ATTACK_ULTIMATE, this.tickCount);
            this.backstepAnimationState.animateWhen(this.isAlive() && (attackState == BACKSTEP || attackState == BACKSTEP_BEFORE_CHARGE), this.tickCount);
            if (this.isAlive()) {
                if (this.isPhase2()) {
                    if (this.tickCount % 2 == 0) {
                        double x = this.getX();
                        double y = this.getY() + 1.2D;
                        double z = this.getZ();
                        this.level().addParticle((ParticleOptions) ModParticle.PHANTOM_WING_FLAME.get(),
                                x + this.getRandom().nextGaussian() * 0.4D, y + this.getRandom().nextGaussian() * 0.4D, z + this.getRandom().nextGaussian() * 0.4D,
                                0.0D, 0.05D, 0.0D);
                    }
                }
                if (attackState == ATTACK_COUNTER_SUCCESS && this.attackTicks == seconds(0.92F)) {
                    this.level().addParticle(new RingParticleOptions(0.0F, ((float) Math.PI / 2F), 40, 20, 110, 255, 1.0F, 65.0F, false, EnumRingBehavior.GROW_THEN_SHRINK.ordinal()),
                            this.getX(), this.getY() + 0.1D, this.getZ(), 0.0D, 0.0D, 0.0D);
                }
                if (attackState == ATTACK_SHOCKWAVE_START && this.attackTicks == seconds(1.0F)) {
                    float rotYaw = (float) Math.toRadians(-this.getYRot());
                    this.level().addParticle(new RingParticleOptions(rotYaw, ((float) Math.PI / 2F), 30, 86, 236, 204, 1.0F, 25.0F, false, EnumRingBehavior.GROW_THEN_SHRINK.ordinal()),
                            this.getX(), this.getY() + 0.1D, this.getZ(), 0.0D, 0.0D, 0.0D);
                }
                if (attackState == ATTACK_SHOCKWAVE_END && this.attackTicks == seconds(0.21F)) {
                    this.level().addParticle(new RingParticleOptions(0.0F, ((float) Math.PI / 2F), 45, 86, 236, 204, 1.0F, 35.0F, false, EnumRingBehavior.GROW_THEN_SHRINK.ordinal()),
                            this.getX(), this.getY() + 0.1D, this.getZ(), 0.0D, 0.0D, 0.0D);
                    this.level().addParticle(new RingParticleOptions(0.0F, ((float) Math.PI / 2F), 55, 86, 236, 204, 0.5F, 45.0F, false, EnumRingBehavior.GROW_THEN_SHRINK.ordinal()),
                            this.getX(), this.getY() + 0.1D, this.getZ(), 0.0D, 0.0D, 0.0D);
                }
                if (attackState == ATTACK_GRAB_SUCCESS && this.attackTicks == seconds(3.0F)) {
                    this.level().addParticle(new RingParticleOptions(0.0F, ((float) Math.PI / 2F), 50, 86, 236, 204, 1.0F, 40.0F, false, EnumRingBehavior.GROW_THEN_SHRINK.ordinal()),
                            this.getX(), this.getY() + 0.1D, this.getZ(), 0.0D, 0.0D, 0.0D);
                }
                if (attackState == ATTACK_EX_JAB_2 && this.attackTicks == seconds(1.25F)) {
                    this.level().addParticle(new RingParticleOptions(0.0F, ((float) Math.PI / 2F), 40, 86, 236, 204, 1.0F, 30.0F, false, EnumRingBehavior.GROW_THEN_SHRINK.ordinal()),
                            this.getX(), this.getY() + 0.1D, this.getZ(), 0.0D, 0.0D, 0.0D);
                }
                if (attackState == ATTACK_GRAB_START) {
                    Vec3 hand = this.getApproxRightHandPosition();
                    for (int i = 0; i < 3; i++) {
                        this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                                hand.x + this.getRandom().nextGaussian() * 0.1D,
                                hand.y + this.getRandom().nextGaussian() * 0.1D,
                                hand.z + this.getRandom().nextGaussian() * 0.1D,
                                0.0D, 0.0D, 0.0D);
                    }
                }
            }
        } else {
            int cdDec = this.isPhase2() ? 2 : 1;
            if (this.jabCooldown > 0) this.jabCooldown = Math.max(0, this.jabCooldown - cdDec);
            if (this.chargeCooldown > 0) this.chargeCooldown = Math.max(0, this.chargeCooldown - cdDec);
            if (this.counterCooldown > 0) this.counterCooldown = Math.max(0, this.counterCooldown - cdDec);
            if (this.shockwaveCooldown > 0) this.shockwaveCooldown = Math.max(0, this.shockwaveCooldown - cdDec);
            if (this.grabCooldown > 0) this.grabCooldown = Math.max(0, this.grabCooldown - cdDec);
            if (this.exJabCooldown > 0) this.exJabCooldown = Math.max(0, this.exJabCooldown - cdDec);
            if (this.phantomCooldown > 0) {
                if (this.isPhase2()) {
                    this.phantomCooldown = Math.max(0, this.phantomCooldown - cdDec);
                } else {
                    this.phantomCooldown--;
                }
            }
            if (this.backstepCooldown > 0) this.backstepCooldown--;
            float hpPct = this.getHealth() / this.getMaxHealth();
            if (hpPct <= 0.5F && !this.isPhase2()) {
                this.entityData.set(PHASE_2, true);
                this.setAttackState(ATTACK_HEAD_BREAK);
            }
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
            this.tickAttackState();
            int attackState = this.getAttackState();
            if (attackState == 0 && this.isCounterReady()) {
                LivingEntity target = this.getTarget();
                if (target instanceof Player player && this.distanceToSqr(player) <= 16.0D) {
                    if (player.swinging && player.swingTime > 0 && player.swingTime <= 4) {
                        this.setAttackState(ATTACK_COUNTER_START);
                        attackState = ATTACK_COUNTER_START;
                    }
                }
            }
            if (attackState != 0) {
                if (this.tickCount % 5 == 0) {
                    List<Player> nearbyPlayers = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(6.0D));
                    for (Player p : nearbyPlayers) {
                        if (!p.isCreative() && !p.isSpectator()) {
                            p.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 40, this.isPhase2() ? 1 : 0));
                        }
                    }
                }
                if (attackState != ATTACK_CHARGE
                        && attackState != ATTACK_SHOCKWAVE_START
                        && attackState != ATTACK_GRAB_START
                        && attackState != BACKSTEP
                        && attackState != BACKSTEP_BEFORE_CHARGE) {
                    this.getNavigation().stop();
                    this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
                }
                LivingEntity target = this.getTarget();
                if (target != null && target.isAlive()) {
                    if (attackState != ATTACK_CHARGE) {
                        double dx = target.getX() - this.getX();
                        double dz = target.getZ() - this.getZ();
                        double distSq = dx * dx + dz * dz;
                        if (distSq > 0.5D * 0.5D) {
                            this.getLookControl().setLookAt(target, 90.0F, 90.0F);
                            this.lookAt(target, 90.0F, 90.0F);
                            this.yBodyRot = this.getYRot();
                            this.yHeadRot = this.getYRot();
                            this.yRotO = this.getYRot();
                            this.yBodyRotO = this.getYRot();
                        }
                    } else {
                        this.getLookControl().setLookAt(target, 10.0F, 90.0F);
                    }
                }
            }
        }
    }

    public AnimationState getAnimationState(String name) {
        if ("idle".equals(name)) {
            return this.idleAnimationState;
        } else if ("walk".equals(name)) {
            return this.walkAnimationState;
        } else if ("jab_1".equals(name)) {
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
        } else if ("head_break".equals(name)) {
            return this.headbreakAnimationState;
        } else if ("ex_jab_1".equals(name)) {
            return this.exJab1AnimationState;
        } else if ("ex_jab_2".equals(name)) {
            return this.exJab2AnimationState;
        } else if ("ex_jab_3".equals(name)) {
            return this.exJab3AnimationState;
        } else if ("backstep".equals(name)) {
            return this.backstepAnimationState;
        } else if ("grab_sloop".equals(name)) {
            return this.grabSloopAnimationState;
        } else if ("grab_send".equals(name)) {
            return this.grabSendAnimationState;
        } else if ("ultimate".equals(name)) {
            return this.ultimateAnimationState;
        }
        return new AnimationState();
    }

    @Override
    public void setAttackState(int state) {
        if (state == ATTACK_GRAB_SLOOP) {
            this.grabSloopTicks = 0; // 【追加】SLOOP移行時にタイマーを0にリセット
        }
        if (state == 0) {
            this.setInvisible(false);
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive()) {
                this.getNavigation().moveTo(target, 1.3D);
            }
        }
        if (this.getAttackState() == ATTACK_GRAB_SUCCESS && state != 0) {
            if (state == ATTACK_HEAD_BREAK || state == ATTACK_GRAB_FAIL) {
                if (this.grabbedEntity != null) {
                    Entity temp = this.grabbedEntity;
                    this.grabbedEntity = null;
                    temp.stopRiding();
                }
            } else {
                return;
            }
        }
        if (!this.level().isClientSide() && state == ATTACK_CHARGE && this.getAttackState() == 0) {
            this.setAttackState(BACKSTEP_BEFORE_CHARGE);
            return;
        }
        boolean isComboTransition = this.getAttackState() != 0 && state != 0;
        super.setAttackState(state);
        this.attackTicks = 0;
        this.counterGuarding = false;
        this.shockwaveJumped = false;
        if (state != ATTACK_GRAB_SUCCESS && this.grabbedEntity != null) {
            Entity temp = this.grabbedEntity;
            this.grabbedEntity = null;
            temp.stopRiding();
        }
        if (!this.level().isClientSide() && isComboTransition) {
            this.goalSelector.tick();
        }
    }

    private void tickAttackState() {
        int state = this.getAttackState();
        if (state == 0) {
            return;
        }
        switch (state) {
            case ATTACK_JAB_1 -> {
                if (this.attackTicks == this.getComboTicks(1.3F)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5F, 0.6F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 15.0F, 0.15F, 0, 8);
                    boolean hit = this.performForwardArcDamage(1.0F, 3.4F, 110.0F, 0.35F, 0.1D, 0.0D);
                    if (hit || this.isPhase2()) {
                        float roll = this.random.nextFloat();
                        if (this.isPhase2() && roll < 0.35F) {
                            this.setAttackState(ATTACK_EX_JAB_1);
                        } else if (roll < 0.75F) {
                            this.setAttackState(ATTACK_JAB_2);
                        } else {
                            this.setAttackState(ATTACK_EX_JAB_2);
                        }
                    } else {
                        if (this.isPhase2() && this.level() instanceof ServerLevel serverLevel) {
                            this.setAttackState(0);
                            LivingEntity target = this.getTarget();
                            if (target != null) {
                                Vec3 targetBack = target.position().add(target.getLookAngle().normalize().scale(2.5D));
                                MaledictusPhantomEntity phantom = com.maxwell.cataclysm_primed_soul.init.ModEntities.MALEDICTUS_PHANTOM.get().create(this.level());
                                if (phantom != null) {
                                    phantom.moveTo(targetBack.x, target.getY(), targetBack.z, target.getYRot() + 180.0F, 0.0F);
                                    phantom.setPhantomType(MaledictusPhantomEntity.TYPE_BOW);
                                    phantom.setTarget(target);
                                    phantom.setSummoner(this);
                                    phantom.setSummonerYRot(target.getYRot() + 180.0F);
                                    this.level().addFreshEntity(phantom);
                                }
                            }
                        } else if (this.random.nextFloat() < 0.15F && this.isBackstepReady()) {
                            this.setAttackState(BACKSTEP);
                        } else {
                            this.jabCooldown = 60;
                            this.setAttackState(0);
                        }
                    }
                } else if (this.attackTicks >= this.getComboTicks(1.58F)) {
                    this.jabCooldown = 80;
                    this.setAttackState(0);
                }
            }
            case ATTACK_JAB_2 -> {
                if (this.attackTicks == this.getComboTicks(0.85F)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5F, 0.7F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 12.0F, 0.1F, 0, 6);
                    this.performForwardArcDamage(0.9F, 3.4F, 110.0F, 0.35F, 0.1D, 0.0D);
                } else if (this.attackTicks >= this.getComboTicks(1.0F)) {
                    float roll = this.random.nextFloat();
                    if (this.isPhase2() && roll < 0.45F) {
                        this.setAttackState(ATTACK_EX_JAB_3);
                    } else if (roll < 0.8F) {
                        this.setAttackState(ATTACK_JAB_3);
                    } else {
                        this.setAttackState(ATTACK_EX_JAB_2);
                    }
                }
            }
            case ATTACK_JAB_3 -> {
                if (this.attackTicks == this.getComboTicks(1.15F)) {
                    this.playSound((SoundEvent) ModSounds.PHANTOM_SPEAR.get(), 1.5F, 1.0F);
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.8F, 0.5F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 20.0F, 0.2F, 0, 12);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX(), this.getY() + 1.0D, this.getZ(), 3, 0.5D, 0.5D, 0.5D, 0.0D);
                        float yaw = this.yBodyRot * ((float) Math.PI / 180F);
                        double fx = -Mth.sin(yaw);
                        double fz = Mth.cos(yaw);
                        serverLevel.sendParticles((ParticleOptions) ModParticle.PHANTOM_WING_FLAME.get(),
                                this.getX() + fx * 1.5D, this.getY() + 1.0D, this.getZ() + fz * 1.5D,
                                10, 0.8D, 0.4D, 0.8D, 0.05D);
                    }
                    this.performForwardArcDamage(1.25F, 3.8F, 120.0F, 0.65F, 0.2D, 0.05D);
                    float rad = this.yBodyRot * ((float) Math.PI / 180F);
                    double rightX = Math.cos(rad);
                    double rightZ = Math.sin(rad);
                    this.spawnAssociatedPhantom(this.getX() + rightX * 1.8D, this.getY(), this.getZ() + rightZ * 1.8D, this.getYRot(), MaledictusPhantomEntity.TYPE_SPEAR);
                    this.spawnAssociatedPhantom(this.getX() - rightX * 1.8D, this.getY(), this.getZ() - rightZ * 1.8D, this.getYRot(), MaledictusPhantomEntity.TYPE_SPEAR);
                } else if (this.attackTicks >= this.getComboTicks(2.5F)) {
                    if (this.isPhase2() && this.random.nextFloat() < 0.65F) {
                        this.setAttackState(ATTACK_EX_JAB_1);
                    } else {
                        this.jabCooldown = 15;
                        this.setAttackState(0);
                    }
                }
            }
            case ATTACK_HEAD_BREAK -> {
                this.getNavigation().stop();
                this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
                if (this.attackTicks == this.getHeavyTicks(2.0F)) {
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 2.0F, 0.5F);
                    this.playSound(SoundEvents.ANVIL_LAND, 2.0F, 0.5F);
                    this.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 1.2F, 0.8F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 45.0F, 0.4F, 0, 20);
                    this.performAreaDamage(2.5F, 1.2F, 6.0D, 3.0D, 0.3D, 0.45D);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 0.1D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                        serverLevel.sendParticles((ParticleOptions) ModParticle.PHANTOM_WING_FLAME.get(), this.getX(), this.getY() + 0.1D, this.getZ(), 45, 2.5D, 0.2D, 2.5D, 0.15D);
                        serverLevel.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY() + 0.1D, this.getZ(), 30, 2.0D, 0.2D, 2.0D, 0.1D);
                    }
                    if (this.isPhase2()) {
                        this.performSwordSpikeWave();
                    }
                }
                if (this.attackTicks >= this.getHeavyTicks(3.25F)) {
                    this.setAttackState(0);
                }
            }
            case ATTACK_CHARGE -> {
                if (this.attackTicks == 22) {
                    LivingEntity target = this.getTarget();
                    if (target != null && target.isAlive()) {
                        this.chargeDirection = new net.minecraft.world.phys.Vec3(
                                target.getX() - this.getX(),
                                0.0D,
                                target.getZ() - this.getZ()
                        ).normalize();
                        float targetAngle = (float) (Mth.atan2(this.chargeDirection.z, this.chargeDirection.x) * (180D / Math.PI)) - 90.0F;
                        this.setYRot(targetAngle);
                        this.yBodyRot = targetAngle;
                        this.yHeadRot = targetAngle;
                        if (!this.level().isClientSide()) {
                            float rad = this.yBodyRot * ((float) Math.PI / 180F);
                            double rightX = Math.cos(rad);
                            double rightZ = Math.sin(rad);
                            this.spawnAssociatedPhantom(this.getX() + rightX * 2.5D, this.getY(), this.getZ() + rightZ * 2.5D, this.getYRot(), MaledictusPhantomEntity.TYPE_SPEAR);
                            this.spawnAssociatedPhantom(this.getX() - rightX * 2.5D, this.getY(), this.getZ() - rightZ * 2.5D, this.getYRot(), MaledictusPhantomEntity.TYPE_SPEAR);
                        }
                    } else {
                        this.chargeDirection = net.minecraft.world.phys.Vec3.directionFromRotation(0.0F, this.getYRot()).normalize();
                    }
                    this.chargedHitEntities.clear();
                    this.playSound((SoundEvent) ModSounds.MALEDICTUS_JUMP.get(), 1.0F, 1.0F);
                    this.playSound((SoundEvent) ModSounds.MALEDICTUS_SHORT_ROAR.get(), 1.0F, 1.0F);
                    this.playSound((SoundEvent) ModSounds.PHANTOM_SPEAR.get(), 1.2F, 0.9F);
                }
                if (this.attackTicks >= 22 && this.attackTicks <= 40) {
                    double chargeSpeed = this.isPhase2() ? 1.4D : 1.15D;
                    if (this.chargeDirection != null) {
                        this.setDeltaMovement(
                                this.chargeDirection.x * chargeSpeed,
                                this.getDeltaMovement().y,
                                this.chargeDirection.z * chargeSpeed
                        );
                        this.hasImpulse = true;
                    }
                    if (!this.level().isClientSide()) {
                        java.util.List<LivingEntity> targets = this.level().getEntitiesOfClass(
                                LivingEntity.class,
                                this.getBoundingBox().inflate(1.5D, 0.5D, 1.5D)
                        );
                        for (LivingEntity t : targets) {
                            if (t != this && t.isAlive() && !this.chargedHitEntities.contains(t) && this.canDamageTarget(t)) {
                                float damage = this.getAttackDamage(0.7F);
                                if (t.hurt(this.damageSources().mobAttack(this), damage)) {
                                    this.chargedHitEntities.add(t);
                                    float pushDirectionYaw = this.getYRot() + (this.random.nextBoolean() ? 45.0F : -45.0F);
                                    float pushYawRad = pushDirectionYaw * ((float) Math.PI / 180F);
                                    t.setDeltaMovement(
                                            -Math.sin(pushYawRad) * 1.3D,
                                            0.35D,
                                            Math.cos(pushYawRad) * 1.3D
                                    );
                                    t.hasImpulse = true;
                                }
                            }
                        }
                    }
                } else if (this.attackTicks > 40 && this.attackTicks < 43) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.65D, 1.0D, 0.65D));
                } else if (this.attackTicks >= 43) {
                    this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
                }
                if (this.attackTicks == 43) {
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 1.2F, 0.7F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 25.0F, 0.3F, 0, 15);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 0.5D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    }
                    this.chargeDirection = null;
                    this.chargedHitEntities.clear();
                }
                if (this.attackTicks >= 45) {
                    this.chargeCooldown = 110;
                    this.setAttackState(0);
                }
            }
            case ATTACK_GRAB_SUCCESS -> {
                if (this.grabbedEntity == null || !this.grabbedEntity.isAlive() || !this.getPassengers().contains(this.grabbedEntity)) {
                    this.grabbedEntity = null;
                    this.setAttackState(ATTACK_GRAB_FAIL);
                    return;
                }
                if (this.attackTicks == this.getHeavyTicks(0.5F)) {
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 1.2F, 0.8F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 25.0F, 0.25F, 0, 15);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 0.2D, this.getZ(), 15, 1.0D, 0.2D, 1.0D, 0.05D);
                    }
                    if (this.grabbedEntity instanceof LivingEntity living) {
                        living.hurt(this.damageSources().mobAttack(this), this.getAttackDamage(1.2F));
                    }
                }
                if (this.attackTicks >= this.getHeavyTicks(1.1F)) {
                    this.playSound(SoundEvents.ENDER_DRAGON_FLAP, 1.5F, 0.8F);
                    this.setDeltaMovement(0.0D, 0.5D, 0.0D);
                    this.hasImpulse = true;
                    this.setAttackState(ATTACK_GRAB_SLOOP);
                }
            }
            case ATTACK_COUNTER_START -> {
                if (this.attackTicks == this.getHeavyTicks(0.1F)) {
                    this.counterGuarding = true;
                } else if (this.attackTicks >= this.getHeavyTicks(4.0F)) {
                    this.setAttackState(ATTACK_COUNTER_FAIL);
                }
            }
            case ATTACK_COUNTER_SUCCESS -> {
                if (this.attackTicks == this.getGodTicks(0.6F)) {
                    this.playSound(SoundEvents.ANVIL_LAND, 1.5F, 0.5F);
                    this.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 1.0F, 0.8F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 40.0F, 0.4F, 0, 20);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.CRIT, this.getX(), this.getY() + 1.2D, this.getZ(), 25, 0.8D, 0.8D, 0.8D, 0.2D);
                        serverLevel.sendParticles(ParticleTypes.FLASH, this.getX(), this.getY() + 1.2D, this.getZ(), 2, 0.2D, 0.2D, 0.2D, 0.0D);
                        serverLevel.sendParticles((ParticleOptions) ModParticle.PHANTOM_WING_FLAME.get(), this.getX(), this.getY() + 1.0D, this.getZ(), 25, 1.2D, 0.4D, 1.2D, 0.1D);
                    }
                    this.performForwardArcDamage(1.8F, 4.0F, 130.0F, 1.0F, 0.45D, 0.15D);
                } else if (this.attackTicks >= this.getGodTicks(1.54F)) {
                    this.counterCooldown = 120;
                    this.setAttackState(0);
                }
            }
            case ATTACK_COUNTER_FAIL -> {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.15D, 1.0D, 0.15D));
                if (this.attackTicks >= this.getHeavyTicks(1.0F)) {
                    this.counterCooldown = 140;
                    this.setAttackState(0);
                }
            }
            case ATTACK_SHOCKWAVE_START -> {
                int jumpStart = this.getHeavyTicks(0.5F);
                if (!this.shockwaveJumped && this.attackTicks >= jumpStart) {
                    this.shockwaveJumped = true;
                }
                if (this.shockwaveJumped) {
                    int elapsed = this.attackTicks - jumpStart;
                    if (elapsed == 0) {
                        this.setDeltaMovement(0.0D, 1.4D, 0.0D);
                        this.hasImpulse = true;
                    } else if (elapsed > 0 && elapsed < 12) {
                        this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
                    } else if (elapsed >= 12 && elapsed < 25) {
                        this.setDeltaMovement(0.0D, 0.015D, 0.0D);
                        this.hasImpulse = true;
                        if (this.level() instanceof ServerLevel serverLevel && this.tickCount % 2 == 0) {
                            serverLevel.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY() + 1.2D, this.getZ(), 2, 0.3D, 0.3D, 0.3D, 0.02D);
                            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY() + 1.2D, this.getZ(), 3, 0.4D, 0.4D, 0.4D, 0.05D);
                        }
                    } else if (elapsed == 25) {
                        LivingEntity target = this.getTarget();
                        if (target != null) {
                            Vec3 toTarget = target.position().subtract(this.position());
                            double hDist = toTarget.horizontalDistance();
                            if (hDist > 0.1D) {
                                this.setDeltaMovement(toTarget.x / hDist * 2.4D, -2.8D, toTarget.z / hDist * 2.4D);
                            } else {
                                this.setDeltaMovement(0.0D, -3.0D, 0.0D);
                            }
                        } else {
                            this.setDeltaMovement(0.0D, -3.0D, 0.0D);
                        }
                        this.hasImpulse = true;
                    }
                }
                if (this.shockwaveJumped && this.onGround() && this.attackTicks > jumpStart + 5) {
                    this.setAttackState(ATTACK_SHOCKWAVE_END);
                }
            }
            case ATTACK_GRAB_SLOOP -> {
                if (this.grabbedEntity == null || !this.getPassengers().contains(this.grabbedEntity)) {
                    this.grabbedEntity = null;
                    this.setAttackState(ATTACK_GRAB_FAIL);
                    return;
                }
                this.setDeltaMovement(0.0D, 0.05D, 0.0D);
                this.hasImpulse = true;

                // 【修正】AIの不具合でフリーズする attackTicks の代わりに、確実に増える grabSloopTicks を使用
                if (this.grabSloopTicks >= 20) {
                    this.setAttackState(ATTACK_GRAB_SEND);
                }
            }
            case ATTACK_GRAB_SEND -> {
                // 【修正】attackTicks == 1 の判定を廃止し、このステートに入った最初の1フレームで即時に叩き落としダメージを執行
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
                Entity temp = this.grabbedEntity;
                this.grabbedEntity = null;
                if (temp != null) {
                    temp.stopRiding();
                }
                this.grabCooldown = 160;
                this.setAttackState(0); // 執行後、即座に通常状態へ移行して落下
            }
            case ATTACK_ULTIMATE -> {
                if (this.attackTicks == 23) {
                    this.playSound(SoundEvents.ENDER_DRAGON_GROWL, 2.0F, 0.5F);
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 1.5F, 0.8F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 40.0F, 0.3F, 0, 20);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.CLOUD, this.getX(), this.getY() + 1.2D, this.getZ(), 40, 2.0D, 0.5D, 2.0D, 0.2D);
                        serverLevel.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 1.2D, this.getZ(), 20, 2.0D, 0.5D, 2.0D, 0.2D);
                    }
                    List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(12.0D, 4.0D, 12.0D));
                    for (LivingEntity t : targets) {
                        if (t != this && t.isAlive() && this.canDamageTarget(t)) {
                            t.hurt(this.damageSources().mobAttack(this), 1.0F);
                            double dx = t.getX() - this.getX();
                            double dz = t.getZ() - this.getZ();
                            double distSq = dx * dx + dz * dz;
                            if (distSq > 0.001D) {
                                double dist = Math.sqrt(distSq);
                                t.setDeltaMovement(dx / dist * 2.5D, 0.6D, dz / dist * 2.5D);
                                t.hasImpulse = true;
                            }
                        }
                    }
                }
                if (this.attackTicks >= 50) {
                    this.setInvisible(true);
                    this.setDeltaMovement(0.0D, 0.0D, 0.0D);
                    this.setAttackState(0);
                }
            }
            case ATTACK_SHOCKWAVE_END -> {
                if (this.attackTicks == this.getHeavyTicks(0.21F)) {
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 2.0F, 0.6F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 50.0F, 0.5F, 0, 30);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 0.1D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                        serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY() + 0.2D, this.getZ(), 30, 2.0D, 0.2D, 2.0D, 0.1D);
                        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.2D, this.getZ(), 20, 1.5D, 0.5D, 1.5D, 0.05D);
                    }
                    this.performPointBlankShockwave();
                    this.performSwordSpikeWave();
                } else if (this.attackTicks >= this.getHeavyTicks(1.37F)) {
                    this.shockwaveCooldown = 150;
                    this.setAttackState(0);
                }
            }
            case ATTACK_GRAB_START -> {
                if (this.attackTicks < this.getComboTicks(0.6F)) {
                    this.chargeForward(0.18D);
                } else if (this.attackTicks >= this.getComboTicks(0.6F) && this.attackTicks <= this.getComboTicks(1.45F)) {
                    this.chargeForward(0.85D);
                    LivingEntity grabbed = this.findGrabTarget();
                    if (grabbed != null) {
                        this.grabbedEntity = grabbed;
                        grabbed.stopRiding();
                        if (grabbed.startRiding(this, true)) {
                            this.setAttackState(ATTACK_GRAB_SUCCESS);
                        }
                    }
                }
                if (this.attackTicks > this.getComboTicks(1.45F)) {
                    this.setAttackState(ATTACK_GRAB_FAIL);
                }
            }
            case ATTACK_GRAB_FAIL -> {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.1D, 1.0D, 0.1D));
                if (this.attackTicks >= this.getHeavyTicks(1.0F)) {
                    this.grabCooldown = 160;
                    this.setAttackState(0);
                }
            }
            case ATTACK_EX_JAB_1 -> {
                if (this.attackTicks == this.getComboTicks(1.6F)) {
                    this.playSound((SoundEvent) ModSounds.PHANTOM_SPEAR.get(), 1.5F, 1.0F);
                    this.playSound(SoundEvents.TRIDENT_HIT, 1.2F, 0.8F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 15.0F, 0.15F, 0, 10);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles((ParticleOptions) com.github.L_Ender.cataclysm.init.ModParticle.PHANTOM_WING_FLAME.get(), this.getX(), this.getY() + 0.1D, this.getZ(), 15, 0.5D, 0.2D, 0.5D, 0.05D);
                        serverLevel.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY() + 0.1D, this.getZ(), 8, 0.5D, 0.2D, 0.5D, 0.05D);
                    }
                    this.performForwardArcDamage(1.25F, 3.6F, 120.0F, 0.55F, 0.15D, 0.0D);
                } else if (this.attackTicks >= this.getComboTicks(1.83F)) {
                    if (this.random.nextFloat() < 0.55F) {
                        this.setAttackState(ATTACK_EX_JAB_2);
                    } else {
                        this.setAttackState(ATTACK_JAB_2);
                    }
                }
            }
            case ATTACK_EX_JAB_2 -> {
                if (this.attackTicks == this.getComboTicks(1.25F)) {
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 1.5F, 0.7F);
                    this.playSound(SoundEvents.ANVIL_LAND, 1.5F, 0.6F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 30.0F, 0.3F, 0, 18);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.CRIT, this.getX(), this.getY() + 1.2D, this.getZ(), 15, 0.6D, 0.6D, 0.6D, 0.1D);
                        serverLevel.sendParticles((ParticleOptions) com.github.L_Ender.cataclysm.init.ModParticle.PHANTOM_WING_FLAME.get(), this.getX(), this.getY() + 0.1D, this.getZ(), 30, 2.0D, 0.2D, 2.0D, 0.1D);
                    }
                    this.performForwardArcDamage(2.2F, 4.2F, 130.0F, 1.8F, 0.65D, 0.3D);
                    if (this.isPhase2()) {
                        LivingEntity target = this.getTarget();
                        if (target != null) {
                            this.spawnAssociatedPhantom(target.getX(), target.getY(), target.getZ(), this.getYRot(), MaledictusPhantomEntity.TYPE_MACE);
                        }
                    }
                } else if (this.attackTicks >= this.getComboTicks(2.0F)) {
                    if (this.random.nextFloat() < 0.55F) {
                        this.setAttackState(ATTACK_EX_JAB_3);
                    } else {
                        this.setAttackState(ATTACK_JAB_3);
                    }
                }
            }
            case ATTACK_EX_JAB_3 -> {
                if (this.attackTicks == 1) {
                    LivingEntity target = this.getTarget();
                    if (target != null) {
                        Vec3 inFront = target.position().subtract(Vec3.directionFromRotation(0.0F, target.getYRot()).normalize().scale(1.2D));
                        this.teleportTo(inFront.x, target.getY(), inFront.z);
                        this.lookAt(target, 360.0F, 360.0F);
                        this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.2F, 0.55F);
                        if (this.level() instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles((ParticleOptions) ModParticle.PHANTOM_WING_FLAME.get(), inFront.x, target.getY() + 1.0D, inFront.z, 12, 0.5D, 0.5D, 0.5D, 0.05D);
                        }
                    }
                }
                if (this.attackTicks == this.getGodTicks(0.66F)) {
                    this.playSound((SoundEvent) ModSounds.PHANTOM_SPEAR.get(), 1.5F, 1.0F);
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.8F, 0.5F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 25.0F, 0.35F, 0, 15);
                    this.performForwardArcDamage(2.8F, 4.0F, 120.0F, 0.8F, 0.3D, 0.1D);
                    if (this.isPhase2()) {
                        this.spawnSpikeRing(2.2D, 10, 0, this.getAttackDamage(1.2F));
                    }
                } else if (this.attackTicks >= this.getGodTicks(2.7F)) {
                    this.jabCooldown = 100;
                    this.exJabCooldown = 130;
                    this.setAttackState(0);
                }
            }
            case BACKSTEP -> {
                if (this.attackTicks < this.getComboTicks(0.5F)) {
                    this.chargeForward(-1.1D);
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.75D, 1.0D, 0.75D));
                }
                if (this.attackTicks >= this.getComboTicks(0.75F)) {
                    this.backstepCooldown = 240;
                    this.setAttackState(0);
                }
            }
            case BACKSTEP_BEFORE_CHARGE -> {
                if (this.attackTicks < this.getComboTicks(0.5F)) {
                    this.chargeForward(-1.1D);
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.75D, 1.0D, 0.75D));
                }
                if (this.attackTicks >= this.getComboTicks(0.75F)) {
                    this.backstepCooldown = 240;
                    this.setAttackState(ATTACK_CHARGE);
                }
            }
            default -> this.finishAttackByState(state);
        }
    }

    private void finishAttackByState(int state) {
        this.finishAttack(30);
    }

    @Override
    public boolean canBeAffected(net.minecraft.world.effect.MobEffectInstance effect) {
        return false;
    }

    private void finishAttack(int allCd) {
        this.jabCooldown = Math.max(this.jabCooldown, allCd);
        this.chargeCooldown = Math.max(this.chargeCooldown, allCd);
        this.counterCooldown = Math.max(this.counterCooldown, allCd);
        this.shockwaveCooldown = Math.max(this.shockwaveCooldown, allCd);
        this.grabCooldown = Math.max(this.grabCooldown, allCd);
        this.exJabCooldown = Math.max(this.exJabCooldown, allCd);
        this.setAttackState(0);
    }

    private void chargeForward(double speed) {
        float yaw = this.getYRot() * ((float) Math.PI / 180F);
        this.setDeltaMovement(-Mth.sin(yaw) * speed, this.getDeltaMovement().y, Mth.cos(yaw) * speed);
        this.hasImpulse = true;
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
                this.getBoundingBox().inflate(1.5D, 1.4D, 1.5D));
        for (LivingEntity target : targets) {
            if (this.canDamageTarget(target) && this.isInFrontArc(target, 70.0F)) {
                return target;
            }
        }
        return null;
    }

    private void throwSword() {
        if (!this.level().isClientSide()) {
            Maledictus_PrimeSwordEntity sword = new Maledictus_PrimeSwordEntity(ModEntities.MALEDICTUS_PRIME_SWORD.get(), this.level());
            sword.setPos(this.getX(), this.getY() + 1.5D, this.getZ());
            sword.setYRot(this.getYRot());
            sword.setXRot(this.getXRot());
            sword.setSummoner(this);
            Vec3 direction = Vec3.directionFromRotation(0.0F, this.getYRot()).normalize();
            sword.setDeltaMovement(direction.scale(1.4D));
            this.level().addFreshEntity(sword);
        }
    }

    private boolean canDamageTarget(LivingEntity target) {
        return target != this && target.isAlive() && this.canAttack(target) && !this.isAlliedTo(target)
                && !(target instanceof MaledictusPhantomEntity)
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

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return super.hurt(source, amount);
        }
        int state = this.getAttackState();
        if (state == ATTACK_GRAB_SUCCESS
                || state == ATTACK_GRAB_SLOOP
                || state == ATTACK_GRAB_SEND
                || (state == ATTACK_ULTIMATE && this.attackTicks >= 50)) {
            return false;
        }
        if (!this.level().isClientSide()
                && this.counterGuarding
                && state == ATTACK_COUNTER_START
                && source.getEntity() != null
                && source.getEntity() != this) {
            Entity attacker = source.getEntity();
            if (attacker instanceof LivingEntity living) {
                Vec3 behind = living.position().subtract(living.getLookAngle().normalize().scale(1.5D));
                this.teleportTo(behind.x, living.getY(), behind.z);
                this.lookAt(living, 360.0F, 360.0F);
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.getYRot();
                this.yRotO = this.getYRot();
                this.yBodyRotO = this.getYRot();
                this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.5F, 0.55F);
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles((ParticleOptions) com.github.L_Ender.cataclysm.init.ModParticle.PHANTOM_WING_FLAME.get(), behind.x, living.getY() + 1.0D, behind.z, 20, 0.5D, 0.5D, 0.5D, 0.05D);
                    serverLevel.sendParticles(ParticleTypes.FLASH, behind.x, living.getY() + 1.0D, behind.z, 3, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
            this.setAttackState(ATTACK_COUNTER_SUCCESS);
            return false;
        }
        float cappedAmount = Math.min(amount, this.DamageCap());
        if (state == ATTACK_CHARGE || state == ATTACK_SHOCKWAVE_END || state == ATTACK_GRAB_SUCCESS) {
            cappedAmount *= 0.5F;
        }
        if (this.isPhase2() && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            cappedAmount *= 1.2F;
        }
        return super.hurt(source, cappedAmount);
    }

    private float getAttackDamage(float multiplier) {
        float base = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier;
        if (this.isPhase2()) {
            base *= 1.35F;
        }
        return base;
    }

    @Override
    public float DamageCap() {
        return 20.0F;
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction moveFunction) {
        if (this.hasPassenger(passenger)) {
            int state = this.getAttackState();
            if (state == ATTACK_GRAB_SUCCESS || state == ATTACK_GRAB_SLOOP || state == ATTACK_GRAB_SEND) {
                float yaw = this.yBodyRot * ((float) Math.PI / 180F);
                double grabX = this.getX() - Math.sin(yaw) * 1.5D;
                double grabY = this.getY() + 1.5D;
                double grabZ = this.getZ() + Math.cos(yaw) * 1.5D;
                moveFunction.accept(passenger, grabX, grabY, grabZ);
                return;
            }
            super.positionRider(passenger, moveFunction);
        }
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (this.getAttackState() == ATTACK_GRAB_SUCCESS) {
            this.grabbedEntity = null;
            this.setAttackState(0);
        }
    }

    public Entity getGrabbedEntity() {
        return this.grabbedEntity;
    }

    private void performSwordSpikeWave() {
        if (this.level().isClientSide()) {
            return;
        }
        boolean isPhase2 = this.isPhase2();
        float damage = this.getAttackDamage(1.5F);
        this.spawnSpikeRing(1.8D, 8, 0, damage);
        this.spawnSpikeRing(3.6D, 12, 6, damage);
        if (isPhase2) {
            this.spawnSpikeRing(5.4D, 16, 12, damage);
            this.spawnSpikeRing(7.2D, 20, 18, damage);
            LivingEntity target = this.getTarget();
            if (target != null) {
                Vec3 toTarget = target.position().subtract(this.position()).normalize();
                for (int i = 1; i <= 6; i++) {
                    double sx = this.getX() + toTarget.x * (i * 1.6D);
                    double sz = this.getZ() + toTarget.z * (i * 1.6D);
                    double sy = this.getY();
                    this.spawnSpikeAt(sx, sy, sz, this.getYRot(), i * 2, damage);
                }
            }
        }
    }

    private void spawnSpikeAt(double sx, double sy, double sz, float yaw, int warmup, float damage) {
        double sy_orig = sy;
        net.minecraft.core.BlockPos pos = net.minecraft.core.BlockPos.containing(sx, sy_orig, sz);
        while (sy > this.level().getMinBuildHeight() && this.level().isEmptyBlock(pos)) {
            sy--;
            pos = pos.below();
        }
        while (sy < this.level().getMaxBuildHeight() && !this.level().isEmptyBlock(pos.above())) {
            sy++;
            pos = pos.above();
        }
        Maledictus_PrimeSwordSpikeEntity spike = new Maledictus_PrimeSwordSpikeEntity(
                this.level(), sx, sy, sz, yaw, warmup, damage, this
        );
        this.level().addFreshEntity(spike);
    }

    private void spawnSpikeRing(double radius, int count, int warmup, float damage) {
        double angleStep = (Math.PI * 2.0D) / count;
        for (int i = 0; i < count; i++) {
            double angle = i * angleStep;
            double sx = this.getX() + Math.cos(angle) * radius;
            double sz = this.getZ() + Math.sin(angle) * radius;
            double sy = this.getY();
            net.minecraft.core.BlockPos pos = net.minecraft.core.BlockPos.containing(sx, sy, sz);
            while (sy > this.level().getMinBuildHeight() && this.level().isEmptyBlock(pos)) {
                sy--;
                pos = pos.below();
            }
            while (sy < this.level().getMaxBuildHeight() && !this.level().isEmptyBlock(pos.above())) {
                sy++;
                pos = pos.above();
            }
            float yaw = (float) (angle * (180D / Math.PI)) - 90.0F;
            Maledictus_PrimeSwordSpikeEntity spike = new Maledictus_PrimeSwordSpikeEntity(
                    this.level(), sx, sy, sz, yaw, warmup, damage, this
            );
            this.level().addFreshEntity(spike);
        }
    }

    private void spawnAssociatedPhantom(double x, double y, double z, float yaw, int type) {
        if (this.level().isClientSide()) {
            return;
        }
        MaledictusPhantomEntity phantom = com.maxwell.cataclysm_primed_soul.init.ModEntities.MALEDICTUS_PHANTOM.get().create(this.level());
        if (phantom != null) {
            phantom.moveTo(x, y, z, yaw, 0.0F);
            phantom.setPhantomType(type);
            phantom.setSummoner(this);
            phantom.setSummonerYRot(yaw);
            LivingEntity target = this.getTarget();
            if (target != null) {
                phantom.setTarget(target);
            }
            this.level().addFreshEntity(phantom);
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
                double progress = ticks / 12.0D;
                heightOffset = 1.7D - (1.375D * progress);
            } else if (ticks <= 29) {
                heightOffset = 0.325D;
            } else if (ticks <= 32) {
                double progress = (ticks - 29) / 3.0D;
                heightOffset = 0.325D + (0.5625D * progress);
                forwardOffset = 1.1D + (2.25D * progress);
            } else if (ticks <= 59) {
                heightOffset = 0.8875D;
                forwardOffset = 3.35D;
            } else if (ticks <= 62) {
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

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        int state = this.getAttackState();
        return (state == ATTACK_GRAB_START || state == ATTACK_GRAB_SUCCESS) && this.getPassengers().size() < 1;
    }

    private float getComboSpeed() {
        return this.isPhase2() ? 1.4F : 1.15F;
    }

    private float getHeavySpeed() {
        return this.isPhase2() ? 1.25F : 1.05F;
    }

    private float getGodSpeed() {
        return this.isPhase2() ? 1.6F : 1.35F;
    }

    private int getComboTicks(float seconds) {
        return Math.round(seconds * 20.0F / this.getComboSpeed());
    }

    private int getHeavyTicks(float seconds) {
        return Math.round(seconds * 20.0F / this.getHeavySpeed());
    }

    private int getGodTicks(float seconds) {
        return Math.round(seconds * 20.0F / this.getGodSpeed());
    }
}