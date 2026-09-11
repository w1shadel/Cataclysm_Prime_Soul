package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime;

import com.github.L_Ender.cataclysm.client.particle.Options.RingParticleOptions;
import com.github.L_Ender.cataclysm.client.particle.RingParticle.EnumRingBehavior;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.AI.InternalMoveGoal;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.IABoss_monster;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.entity.effect.Cm_Falling_Block_Entity;
import com.github.L_Ender.cataclysm.entity.etc.CMBossInfoServer;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.init.ModSounds;
import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.api.entity.IShaderBoss;
import com.maxwell.cataclysm_primed_soul.api.entity.IDialogueEntity;
import com.maxwell.cataclysm_primed_soul.entity.EntityDamageHelper;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.BasePrimeBossEntity;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.goal.MaledictusAttackGoal;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.goal.MaledictusBackstepGoal;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.goal.MaledictusStateGoal;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.sub.Maledictus_PrimeSwordSpikeEntity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.advancements.CriteriaTriggers;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Primed_Soul.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@SuppressWarnings("removal")
public class Maledictus_PrimeEntity extends BasePrimeBossEntity implements IShaderBoss, IDialogueEntity {
    public static final int ATTACK_JAB_1 = 1;
    public static final int ATTACK_JAB_2 = 2;
    public static final int ATTACK_JAB_3 = 3;
    public static final int ATTACK_CHARGE = 4;
    public static final int ATTACK_COUNTER_START = 5;
    public static final int ATTACK_COUNTER_SUCCESS = 6;
    public static final int ATTACK_COUNTER_FAIL = 7;
    public static final int ATTACK_SHOCKWAVE_START = 8;
    public static final int ATTACK_SHOCKWAVE_END = 9;
    public static final int ATTACK_HEAD_BREAK = 15;
    public static final int ATTACK_EX_JAB_1 = 16;
    public static final int ATTACK_EX_JAB_2 = 17;
    public static final int ATTACK_EX_JAB_3 = 18;
    public static final int ATTACK_ICESHOCK = 19;
    public static final int ATTACK_EXCALIBUR_START = 20;
    public static final int ATTACK_EXCALIBUR_END = 21;
    public static final int ATTACK_ULTIMATE = 35;
    public static final int ATTACK_LAST2 = 36;
    public static final int ATTACK_DEAD = 37;
    public static final int BACKSTEP = 80;
    public static final int BACKSTEP_BEFORE_CHARGE = 81;
    public static final int STATE_FLASH_STEP = 82;
    private static final float TICKS_PER_SECOND = 20.0F;
    private static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> PHASE_2 =
            net.minecraft.network.syncher.SynchedEntityData.defineId(Maledictus_PrimeEntity.class, net.minecraft.network.syncher.EntityDataSerializers.BOOLEAN);
    private static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> IS_ECHO =
            net.minecraft.network.syncher.SynchedEntityData.defineId(Maledictus_PrimeEntity.class, net.minecraft.network.syncher.EntityDataSerializers.BOOLEAN);
    private static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> ULTIMATE_LANDING =
            net.minecraft.network.syncher.SynchedEntityData.defineId(Maledictus_PrimeEntity.class, net.minecraft.network.syncher.EntityDataSerializers.BOOLEAN);
    public static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> IS_DOWNED =
            net.minecraft.network.syncher.SynchedEntityData.defineId(Maledictus_PrimeEntity.class, net.minecraft.network.syncher.EntityDataSerializers.BOOLEAN);
    public static final net.minecraft.network.syncher.EntityDataAccessor<Integer> DIALOGUE_INDEX =
            net.minecraft.network.syncher.SynchedEntityData.defineId(Maledictus_PrimeEntity.class, net.minecraft.network.syncher.EntityDataSerializers.INT);
    public static final int MAX_DIALOGUE = 3;
    private static final ResourceLocation SHADER = new ResourceLocation(Primed_Soul.MODID, "shaders/post/maledictus_debuff.json");
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
    public final AnimationState headbreakAnimationState = new AnimationState();
    public final AnimationState exJab1AnimationState = new AnimationState();
    public final AnimationState exJab2AnimationState = new AnimationState();
    public final AnimationState exJab3AnimationState = new AnimationState();
    public final AnimationState backstepAnimationState = new AnimationState();
    public final AnimationState excaliburStartAnimationState = new AnimationState();
    public final AnimationState excaliburEndAnimationState = new AnimationState();
    public final AnimationState ultimateAnimationState = new AnimationState();
    public final AnimationState last2AnimationState = new AnimationState();
    public final AnimationState deadAnimationState = new AnimationState();
    public final AnimationState iceshockAnimationState = new AnimationState();
    private final CMBossInfoServer bossEvent;
    private final java.util.List<net.minecraft.world.entity.LivingEntity> chargedHitEntities = new java.util.ArrayList<>();
    @javax.annotation.Nullable
    private Maledictus_PrimeEntity phaseTwoEcho;
    private float jabCooldownSeconds;
    private float chargeCooldownSeconds;
    private float counterCooldownSeconds;
    private float shockwaveCooldownSeconds;
    private float excaliburCooldownSeconds;
    private float exJabCooldownSeconds;
    private float iceshockCooldownSeconds;
    private float phantomCooldownSeconds;
    private float backstepCooldownSeconds;
    private float backstepRecoverySeconds;
    private float ultimateCooldownSeconds;
    private boolean counterGuarding;
    private boolean ultimateDeathStarted;
    private boolean deathSequenceFinished;
    private boolean shockwaveJumped;
    private boolean airborneAttackForcedDescent;
    private net.minecraft.world.phys.Vec3 chargeDirection = null;
    private Vec3 flashStepTarget;
    private int flashStepTicks;
    private int nextComboStateAfterFlash;
    private int failedAttackAttempts;
    @javax.annotation.Nullable
    private Maledictus_PrimeEntity echoSource;

    public Maledictus_PrimeEntity(EntityType<? extends Monster> entity, Level world) {
        super(entity, world);
        this.xpReward = 500;
        this.setMaxUpStep(1.5F);
        this.bossEvent = new CMBossInfoServer(this.getDisplayName(), BossEvent.BossBarColor.GREEN, true, 9);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("IsEcho", this.isEcho());
        tag.putBoolean("Phase2", this.isPhase2());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.getBoolean("IsEcho")) {
            this.setEchoMode();
            if (!this.level().isClientSide()) {
                this.discard();
                return;
            }
        }
        if (tag.getBoolean("Phase2")) {
            this.entityData.set(PHASE_2, true);
        }
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

    private static int ticks(float seconds) {
        return Math.round(seconds * TICKS_PER_SECOND);
    }

    private static float remainingSeconds(float remaining, float elapsed) {
        return Math.max(0.0F, remaining - elapsed);
    }
    private boolean isMovementAttack(int state) {
        return state == ATTACK_CHARGE
                || state == ATTACK_SHOCKWAVE_START
                || state == BACKSTEP
                || state == BACKSTEP_BEFORE_CHARGE
                || state == STATE_FLASH_STEP
                || state == ATTACK_EX_JAB_1
                || state == ATTACK_EX_JAB_2
                || state == ATTACK_EX_JAB_3;
    }

    private boolean isAirborneAttack(int state) {
        return state == ATTACK_SHOCKWAVE_START
                || state == ATTACK_ULTIMATE;
    }

    private void interruptAirborneAttackOnHit() {
        if (this.level().isClientSide() || !this.isAirborneAttack(this.getAttackState())) {
            return;
        }
        this.shockwaveJumped = true;
        this.airborneAttackForcedDescent = true;
        this.chargeDirection = null;
        Vec3 current = this.getDeltaMovement();
        this.setDeltaMovement(current.x * 0.15D, Math.min(current.y, -0.35D), current.z * 0.15D);
        this.setNoGravity(false);
        this.hasImpulse = true;
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
        this.entityData.define(IS_ECHO, false);
        this.entityData.define(ULTIMATE_LANDING, false);
        this.entityData.define(IS_DOWNED, false);
        this.entityData.define(DIALOGUE_INDEX, 0);
    }

    public boolean isPhase2() {
        return this.entityData.get(PHASE_2);
    }

    public boolean isEcho() {
        return this.entityData.get(IS_ECHO);
    }

    public void setEchoMode() {
        this.entityData.set(IS_ECHO, true);
        this.entityData.set(PHASE_2, true);
        this.setHealth(this.getMaxHealth());
    }

    public void setEchoSource(@javax.annotation.Nullable Maledictus_PrimeEntity source) {
        this.echoSource = source;
    }

    private void maintainEchoTarget() {
        if (!this.isEcho() || this.level().isClientSide()) {
            return;
        }
        LivingEntity mainTarget = this.echoSource != null && this.echoSource.isAlive()
                ? this.echoSource.getTarget() : null;
        LivingEntity currentTarget = this.getTarget();
        if (this.isValidEchoTarget(currentTarget) && currentTarget != mainTarget) {
            return;
        }
        Player alternate = null;
        double nearestDistance = Double.MAX_VALUE;
        List<Player> candidates = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(64.0D));
        for (Player candidate : candidates) {
            if (candidate == mainTarget || !this.isValidEchoTarget(candidate)) {
                continue;
            }
            double distance = this.distanceToSqr(candidate);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                alternate = candidate;
            }
        }
        if (alternate != null) {
            this.setTarget(alternate);
        } else if (this.isValidEchoTarget(mainTarget)) {
            this.setTarget(mainTarget);
        } else {
            this.setTarget(null);
        }
    }

    private boolean isValidEchoTarget(@javax.annotation.Nullable LivingEntity target) {
        return target != null && target.isAlive()
                && (!(target instanceof Player player) || (!player.isCreative() && !player.isSpectator()));
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
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_EX_JAB_1));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_EX_JAB_2));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_EX_JAB_3));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_EXCALIBUR_START));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_EXCALIBUR_END));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_HEAD_BREAK));
        this.goalSelector.addGoal(0, new MaledictusStateGoal(this, ATTACK_ULTIMATE));
        this.goalSelector.addGoal(1, new MaledictusAttackGoal(this));
        this.goalSelector.addGoal(2, new MaledictusBackstepGoal(this));
        this.goalSelector.addGoal(4, new InternalMoveGoal(this, false, 1.3D));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public boolean isJabReady() {
        return this.jabCooldownSeconds <= 0.0F;
    }

    public boolean isChargeReady() {
        return this.chargeCooldownSeconds <= 0.0F;
    }

    public boolean isCounterReady() {
        return this.counterCooldownSeconds <= 0.0F;
    }

    public boolean isShockwaveReady() {
        return this.shockwaveCooldownSeconds <= 0.0F;
    }

    public boolean isExcaliburReady() {
        return this.excaliburCooldownSeconds <= 0.0F;
    }

    public boolean isExJabReady() {
        return this.exJabCooldownSeconds <= 0.0F;
    }

    public boolean isIceShockReady() {
        return this.iceshockCooldownSeconds <= 0.0F;
    }

    public boolean isPhantomReady() {
        return this.phantomCooldownSeconds <= 0.0F;
    }

    public boolean isBackstepReady() {
        return this.backstepCooldownSeconds <= 0.0F;
    }

    public boolean isUltimateReady() {
        return this.ultimateCooldownSeconds <= 0.0F;
    }

    public boolean isUltimateDeathStarted() {
        return this.ultimateDeathStarted;
    }

    public int getAttackTicks() {
        return this.attackTicks;
    }

    public boolean isBackstepRecoveryActive() {
        return this.backstepRecoverySeconds > 0.0F;
    }

    public boolean isUltimateLanding() {
        return this.entityData.get(ULTIMATE_LANDING);
    }

    public boolean isDowned() { return this.entityData.get(IS_DOWNED); }
    public int getDialogueIndex() { return this.entityData.get(DIALOGUE_INDEX); }

    @Override public String getNameKey() {
        return "dialogue." + Primed_Soul.MODID + ".maledictus_prime.name";
    }

    @Override public String getLineKey(int index) {
        return "dialogue." + Primed_Soul.MODID + ".maledictus_prime.line." + index;
    }

    @Override public int getMaxLines() { return MAX_DIALOGUE; }

    private void setUltimateLanding(boolean landing) {
        this.entityData.set(ULTIMATE_LANDING, landing);
    }

    public boolean shouldChangeStrategyAfterMiss() {
        return this.failedAttackAttempts >= 2;
    }

    public void resetAttackFailureStreak() {
        this.failedAttackAttempts = 0;
    }

    private void recordAttackResult(boolean hit) {
        if (hit) {
            this.failedAttackAttempts = 0;
        } else {
            this.failedAttackAttempts = Math.min(3, this.failedAttackAttempts + 1);
        }
    }

    public boolean isIncomingAttackThreat(LivingEntity target) {
        if (target == null || !target.isAlive() || !this.hasLineOfSight(target)) {
            return false;
        }
        Vec3 toBoss = this.position().subtract(target.position());
        double distance = toBoss.horizontalDistance();
        if (distance < 1.2D || distance > 5.0D) {
            return false;
        }
        if (target.swinging && target.swingTime > 0 && target.swingTime <= 4) {
            return true;
        }
        Vec3 direction = toBoss.normalize();
        double closingSpeed = target.getDeltaMovement().dot(direction);
        return closingSpeed > 0.12D && distance <= 4.2D;
    }

    private boolean tryEmergencyBackstep() {
        int state = this.getAttackState();
        if (!this.isBackstepReady()
                || state == BACKSTEP
                || state == BACKSTEP_BEFORE_CHARGE
                || state == ATTACK_ULTIMATE
                || state == ATTACK_HEAD_BREAK
                || state != 0) {
            return false;
        }
        LivingEntity target = this.getTarget();
        if (!this.isIncomingAttackThreat(target)) {
            return false;
        }
        this.setAttackState(BACKSTEP);
        return true;
    }

    public void setPhantomCooldown(float seconds) {
        this.phantomCooldownSeconds = Math.max(0.0F, seconds);
    }

    public void startFlashStep(LivingEntity target, int nextState) {
        if (target == null || !target.isAlive() || this.getAttackState() != 0) {
            return;
        }
        Vec3 targetPos = target.position();
        Vec3 toBoss = this.position().subtract(targetPos);
        Vec3 offsetDir = toBoss.horizontalDistanceSqr() > 1.0E-4D
                ? new Vec3(toBoss.x, 0.0D, toBoss.z).normalize()
                : new Vec3(0.0D, 0.0D, 1.0D);
        this.setAttackState(STATE_FLASH_STEP);
        this.flashStepTarget = targetPos.add(offsetDir.scale(2.2D));
        this.flashStepTicks = 0;
        this.nextComboStateAfterFlash = nextState;
        this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.2F, 1.8F);
        this.lookAt(target, 360.0F, 360.0F);
    }

    private void tickFlashStep() {
        this.flashStepTicks++;
        this.getNavigation().stop();
        LivingEntity target = this.getTarget();

        if (this.flashStepTicks <= 3) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.2D, 1.0D, 0.2D));
            if (target != null) {
                this.lookAt(target, 60.0F, 60.0F);
            }
            return;
        }


        if (this.flashStepTicks <= 8) {
            this.noPhysics = true;
            if (this.flashStepTarget != null) {
                Vec3 toDest = this.flashStepTarget.subtract(this.position());
                double dist = toDest.horizontalDistance();
                if (dist > 0.4D) {
                    Vec3 slideVelocity = new Vec3(toDest.x, 0.0D, toDest.z).normalize().scale(1.35D);
                    this.setDeltaMovement(slideVelocity.x, this.getDeltaMovement().y, slideVelocity.z);
                    this.hasImpulse = true;
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles((ParticleOptions) ModParticle.PHANTOM_WING_FLAME.get(),
                                this.getX(), this.getY() + 0.2D, this.getZ(),
                                4, 0.3D, 0.1D, 0.3D, 0.02D);
                    }
                } else {
                    this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
                }
            }
            return;
        }

        this.noPhysics = false;
        this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
        if (target != null) {
            this.lookAt(target, 90.0F, 90.0F);
        }
        if (this.flashStepTicks >= 12) {
            int next = this.nextComboStateAfterFlash != 0
                    ? this.nextComboStateAfterFlash : ATTACK_JAB_1;
            this.setAttackState(next);
        }
    }

    public void spawnNextStatePhantom(int nextState) {
        if (this.isEcho() || this.level().isClientSide() || nextState == 0 || this.getRandom().nextFloat() > (this.isPhase2() ? 0.55F : 0.30F)) {
            return;
        }
        MaledictusPhantomEntity phantom = com.maxwell.cataclysm_primed_soul.init.ModEntities.MALEDICTUS_PHANTOM.get().create(this.level());
        if (phantom == null) return;
        phantom.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
        phantom.setPhantomType(MaledictusPhantomEntity.TYPE_NEXT_STATE);
        phantom.setPlannedAttackState(nextState);
        phantom.setTarget(this.getTarget());
        phantom.setSummoner(this);
        phantom.setSummonerYRot(this.getYRot());
        this.level().addFreshEntity(phantom);
    }

    private int getNextPhantomState(int state) {
        return switch (state) {
            case ATTACK_JAB_1 -> ATTACK_JAB_2;
            case ATTACK_JAB_2 -> ATTACK_JAB_3;
            case ATTACK_CHARGE -> ATTACK_SHOCKWAVE_START;
            case ATTACK_SHOCKWAVE_START -> ATTACK_SHOCKWAVE_END;
            default -> 0;
        };
    }

    private void ensurePhaseTwoEcho() {
        if (this.level().isClientSide() || this.phaseTwoEcho != null && this.phaseTwoEcho.isAlive()) return;
        Maledictus_PrimeEntity echo = com.maxwell.cataclysm_primed_soul.init.ModEntities.MALEDICTUS_PRIME.get().create(this.level());
        if (echo == null) return;
        echo.moveTo(this.getX() + 4.0D, this.getY(), this.getZ() + 4.0D, this.getYRot(), 0.0F);
        echo.setEchoMode();
        echo.setEchoSource(this);
        echo.setTarget(this.getTarget());
        this.level().addFreshEntity(echo);
        this.phaseTwoEcho = echo;
    }

    private void tickCooldowns() {
        float elapsed = this.isPhase2() ? 0.1F : 0.05F;
        this.jabCooldownSeconds = remainingSeconds(this.jabCooldownSeconds, elapsed);
        this.chargeCooldownSeconds = remainingSeconds(this.chargeCooldownSeconds, elapsed);
        this.counterCooldownSeconds = remainingSeconds(this.counterCooldownSeconds, elapsed);
        this.shockwaveCooldownSeconds = remainingSeconds(this.shockwaveCooldownSeconds, elapsed);
        this.exJabCooldownSeconds = remainingSeconds(this.exJabCooldownSeconds, elapsed);
        this.excaliburCooldownSeconds = remainingSeconds(this.excaliburCooldownSeconds, elapsed);
        this.iceshockCooldownSeconds = remainingSeconds(this.iceshockCooldownSeconds, elapsed);
        this.phantomCooldownSeconds = remainingSeconds(this.phantomCooldownSeconds, elapsed);
        this.backstepCooldownSeconds = remainingSeconds(this.backstepCooldownSeconds, elapsed);
        this.backstepRecoverySeconds = remainingSeconds(this.backstepRecoverySeconds, elapsed);
        this.ultimateCooldownSeconds = remainingSeconds(this.ultimateCooldownSeconds, elapsed);
    }

    @Override
    public void tick() {



        if (!this.level().isClientSide() && !this.isEcho() && !this.isDowned()
                && !this.ultimateDeathStarted && !this.deathSequenceFinished
                && this.getHealth() <= 1.0F) {
            this.beginUltimateDeathSequence();
        }
        super.tick();
        if (this.isDowned()) {
            this.setDeltaMovement(Vec3.ZERO);
            this.getNavigation().stop();
            this.setNoGravity(true);
            if (this.level().isClientSide()) {
                this.deadAnimationState.animateWhen(true, this.tickCount);
            } else {
                this.cleanupDownedEntities();
            }
            return;
        }
        if (this.isDeadOrDying() || !this.isAlive()) {
            if (this.getAttackState() != 0) {
                this.setAttackState(0);
            }
            return;
        }
        if (!this.level().isClientSide() && this.isEcho()) {
            this.maintainEchoTarget();
        }
        this.setNoGravity(false);
        if (!this.level().isClientSide()) {
        }
        int currentAttackState = this.getAttackState();
        if (currentAttackState != 0) {
            this.getNavigation().stop();
            if (!this.isMovementAttack(currentAttackState)) {
                this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
            }
        }
        if (this.level().isClientSide()) {
            int attackState = this.getAttackState();
            boolean canPlayIdleWalk = this.getAttackState() == 0 && this.isAlive() && !this.isDowned();
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
            this.shockwaveEndAnimationState.animateWhen(this.isAlive()
                    && (attackState == ATTACK_SHOCKWAVE_END || this.isUltimateLanding()), this.tickCount);
            this.excaliburStartAnimationState.animateWhen(this.isAlive() && attackState == ATTACK_EXCALIBUR_START, this.tickCount);
            this.excaliburEndAnimationState.animateWhen(this.isAlive() && attackState == ATTACK_EXCALIBUR_END, this.tickCount);
            this.headbreakAnimationState.animateWhen(this.isAlive() && attackState == ATTACK_HEAD_BREAK, this.tickCount);
            this.exJab1AnimationState.animateWhen(this.isAlive() && attackState == ATTACK_EX_JAB_1, this.tickCount);
            this.exJab2AnimationState.animateWhen(this.isAlive() && attackState == ATTACK_EX_JAB_2, this.tickCount);
            this.exJab3AnimationState.animateWhen(this.isAlive() && attackState == ATTACK_EX_JAB_3, this.tickCount);
            this.ultimateAnimationState.animateWhen(this.isAlive()
                    && attackState == ATTACK_ULTIMATE && !this.isUltimateLanding(), this.tickCount);
            this.last2AnimationState.animateWhen(this.isAlive() && attackState == ATTACK_LAST2
                    && this.isUltimateLanding(), this.tickCount);
            this.iceshockAnimationState.animateWhen(this.isAlive() && attackState == ATTACK_ICESHOCK, this.tickCount);
            this.deadAnimationState.animateWhen(this.isAlive() && (this.isDowned()
                    || (attackState == ATTACK_DEAD && !this.isUltimateLanding())), this.tickCount);
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
                if (attackState == ATTACK_COUNTER_SUCCESS && this.attackTicks == ticks(0.92F)) {
                    this.level().addParticle(new RingParticleOptions(0.0F, ((float) Math.PI / 2F), 40, 20, 110, 255, 1.0F, 65.0F, false, EnumRingBehavior.GROW_THEN_SHRINK.ordinal()),
                            this.getX(), this.getY() + 0.1D, this.getZ(), 0.0D, 0.0D, 0.0D);
                }
                if (attackState == ATTACK_SHOCKWAVE_START && this.attackTicks == ticks(1.0F)) {
                    float rotYaw = (float) Math.toRadians(-this.getYRot());
                    this.level().addParticle(new RingParticleOptions(rotYaw, ((float) Math.PI / 2F), 30, 86, 236, 204, 1.0F, 25.0F, false, EnumRingBehavior.GROW_THEN_SHRINK.ordinal()),
                            this.getX(), this.getY() + 0.1D, this.getZ(), 0.0D, 0.0D, 0.0D);
                }
                if (attackState == ATTACK_SHOCKWAVE_END && this.attackTicks == ticks(0.21F)) {
                    this.level().addParticle(new RingParticleOptions(0.0F, ((float) Math.PI / 2F), 45, 86, 236, 204, 1.0F, 35.0F, false, EnumRingBehavior.GROW_THEN_SHRINK.ordinal()),
                            this.getX(), this.getY() + 0.1D, this.getZ(), 0.0D, 0.0D, 0.0D);
                    this.level().addParticle(new RingParticleOptions(0.0F, ((float) Math.PI / 2F), 55, 86, 236, 204, 0.5F, 45.0F, false, EnumRingBehavior.GROW_THEN_SHRINK.ordinal()),
                            this.getX(), this.getY() + 0.1D, this.getZ(), 0.0D, 0.0D, 0.0D);
                }
                if (attackState == ATTACK_EX_JAB_2 && this.attackTicks == ticks(1.25F)) {
                    this.level().addParticle(new RingParticleOptions(0.0F, ((float) Math.PI / 2F), 40, 86, 236, 204, 1.0F, 30.0F, false, EnumRingBehavior.GROW_THEN_SHRINK.ordinal()),
                            this.getX(), this.getY() + 0.1D, this.getZ(), 0.0D, 0.0D, 0.0D);
                }
            }
        } else {
            if (this.isDowned()) {
                this.getNavigation().stop();
                this.setTarget(null);
                this.setDeltaMovement(Vec3.ZERO);
                this.cleanupDownedEntities();
                return;
            }
            this.tickCooldowns();
            if (!this.isEcho() && !this.ultimateDeathStarted && this.getHealth() <= 1.0F) {
                this.beginUltimateDeathSequence();
                return;
            }
            float hpPct = this.getHealth() / this.getMaxHealth();
            if (!this.isEcho() && !this.ultimateDeathStarted && hpPct <= 0.5F && !this.isPhase2()) {
                this.entityData.set(PHASE_2, true);
                this.ensurePhaseTwoEcho();
                this.setAttackState(ATTACK_HEAD_BREAK);
            }
            if (!this.isEcho()) {
                this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
            }
            this.tryEmergencyBackstep();
            this.tickAttackState();
            this.tickTargetMovementFallback();
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
                this.getNavigation().stop();
                if (this.tickCount % 5 == 0) {
                    List<Player> nearbyPlayers = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(6.0D));
                    for (Player p : nearbyPlayers) {
                        if (!p.isCreative() && !p.isSpectator()) {
                            p.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 40, this.isPhase2() ? 1 : 0));
                        }
                    }
                }
                if (!this.isMovementAttack(currentAttackState)) {
                    this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
                }
                LivingEntity target = this.getTarget();
                if (target != null && target.isAlive()) {
                    if (attackState != ATTACK_EXCALIBUR_END && !this.isMovementAttack(attackState)) {
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
        } else if ("excalibur_start".equals(name)) {
            return this.excaliburStartAnimationState;
        } else if ("excalibur_end".equals(name)) {
            return this.excaliburEndAnimationState;
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
        } else if ("ultimate".equals(name)) {
            return this.ultimateAnimationState;
        } else if ("last2".equals(name)) {
            return this.last2AnimationState;
        } else if ("dead".equals(name)) {
            return this.deadAnimationState;
        } else if ("iceshock".equals(name)) {
            return this.iceshockAnimationState;
        }
        return new AnimationState();
    }

    @Override
    public void setAttackState(int state) {
        if (this.ultimateDeathStarted && state != ATTACK_ULTIMATE && state != ATTACK_LAST2 && state != ATTACK_DEAD && state != 0) {
            return;
        }

        if ((this.isDeadOrDying() || !this.isAlive()) && state != 0) {
            return;
        }
        if (state != STATE_FLASH_STEP) {
            this.noPhysics = false;
            this.flashStepTarget = null;
            this.flashStepTicks = 0;
            this.nextComboStateAfterFlash = 0;
        }
        int previousState = this.getAttackState();
        int nextPhantomState = this.getNextPhantomState(state);
        if (!this.level().isClientSide() && state != 0 && state != previousState
                && state != BACKSTEP && state != BACKSTEP_BEFORE_CHARGE
                && state != ATTACK_COUNTER_START && state != ATTACK_COUNTER_SUCCESS
                && state != ATTACK_COUNTER_FAIL && state != ATTACK_HEAD_BREAK
                && state != ATTACK_LAST2 && state != ATTACK_DEAD) {
            this.spawnNextStatePhantom(nextPhantomState);
        }
        if (state == 0) {
            this.setUltimateLanding(false);
            this.airborneAttackForcedDescent = false;
            this.setInvisible(false);
            LivingEntity target = this.getTarget();
            if (this.getDeltaMovement().y > 0) {
                this.setDeltaMovement(0.0D, -0.1D, 0.0D);
                this.hasImpulse = true;
            }
            if (target != null && target.isAlive()) {
                this.getNavigation().moveTo(target, 1.3D);
            }
        }
        super.setAttackState(state);
        this.attackTicks = 0;
        if (state == ATTACK_ULTIMATE) {
            this.setUltimateLanding(false);
        } else if (state == ATTACK_LAST2) {
            this.setUltimateLanding(true);
        } else {

            this.setUltimateLanding(false);
        }
        this.counterGuarding = false;
        this.shockwaveJumped = false;
    }

    @Override
    public void setHealth(float health) {
        if (!this.isEcho() && health <= 1.0F && !this.deathSequenceFinished) {
            super.setHealth(1.0F);
            if (!this.level().isClientSide() && !this.ultimateDeathStarted) {
                this.beginUltimateDeathSequence();
            }
            return;
        }
        super.setHealth(health);
    }

    private void beginUltimateDeathSequence() {
        if (this.level().isClientSide() || this.isEcho() || this.ultimateDeathStarted || this.deathSequenceFinished) {
            return;
        }
        this.ultimateDeathStarted = true;
        super.setHealth(1.0F);
        this.setInvulnerable(true);
        this.setDeltaMovement(Vec3.ZERO);
        this.setNoGravity(true);
        this.getNavigation().stop();
        this.setTarget(null);
        this.goalSelector.removeAllGoals(goal -> true);
        this.targetSelector.removeAllGoals(goal -> true);
        this.setAttackState(ATTACK_DEAD);
    }

    private void tickTargetMovementFallback() {
        if (this.getAttackState() != 0 || this.isPassenger() || this.tickCount % 5 != 0) {
            return;
        }
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        this.getLookControl().setLookAt(target, 60.0F, 60.0F);
        if (this.distanceToSqr(target) > 9.0D) {
            if (this.getNavigation().isDone() || this.tickCount % 20 == 0) {
                this.getNavigation().moveTo(target, 1.3D);
            }
        } else {
            this.getNavigation().stop();
        }
    }
    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide() && this.getAttackState() != 0) {

            int maxAllowedTicks = switch (this.getAttackState()) {
                case ATTACK_ICESHOCK -> ticks(3.1F);
                case ATTACK_EX_JAB_1 -> ticks(1.5F);
                case ATTACK_EX_JAB_2 -> ticks(1.8F);
                case ATTACK_EX_JAB_3 -> ticks(3.0F);
                case ATTACK_DEAD -> 1_000_000;
                case ATTACK_JAB_1, ATTACK_JAB_2, ATTACK_JAB_3 -> 60;
                default -> 100;
            };
            if (this.attackTicks >= maxAllowedTicks) {
                this.setAttackState(0);
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(net.minecraft.network.syncher.EntityDataAccessor<?> key) {
        if (ATTACK_STATE.equals(key) && this.level().isClientSide()) {
            int state = this.getAttackState();
            this.attackTicks = 0;
            this.stopAllAnimationStates();

            if (state == ATTACK_DEAD) {
                this.deadAnimationState.start(this.tickCount);
            } else if (state == ATTACK_ICESHOCK) {
                this.iceshockAnimationState.start(this.tickCount);
            } else if (state == ATTACK_EXCALIBUR_START) {
                this.excaliburStartAnimationState.start(this.tickCount);
            } else if (state == ATTACK_EXCALIBUR_END) {
                this.excaliburEndAnimationState.start(this.tickCount);
            }
        }
        super.onSyncedDataUpdated(key);
    }
    private void stopAllAnimationStates() {
        this.idleAnimationState.stop();
        this.walkAnimationState.stop();
        this.jab1AnimationState.stop();
        this.jab2AnimationState.stop();
        this.jab3AnimationState.stop();
        this.chargeAnimationState.stop();
        this.counterStartAnimationState.stop();
        this.counterSuccessAnimationState.stop();
        this.counterFailAnimationState.stop();
        this.shockwaveStartAnimationState.stop();
        this.shockwaveEndAnimationState.stop();
        this.excaliburStartAnimationState.stop();
        this.excaliburEndAnimationState.stop();
        this.headbreakAnimationState.stop();
        this.exJab1AnimationState.stop();
        this.exJab2AnimationState.stop();
        this.exJab3AnimationState.stop();
        this.backstepAnimationState.stop();
        this.ultimateAnimationState.stop();
        this.last2AnimationState.stop();
        this.deadAnimationState.stop();
        this.iceshockAnimationState.stop();
    }
    private void tickAttackState() {
        int state = this.getAttackState();
        if (state == 0) {
            return;
        }
        switch (state) {
            case STATE_FLASH_STEP -> this.tickFlashStep();
            case ATTACK_JAB_1, ATTACK_JAB_2, ATTACK_JAB_3 -> this.tickJabCombo(state);
            case ATTACK_EX_JAB_1, ATTACK_EX_JAB_2, ATTACK_EX_JAB_3 -> this.tickExJabCombo(state);
            case ATTACK_CHARGE -> this.tickChargeAttack();
            case ATTACK_SHOCKWAVE_START, ATTACK_SHOCKWAVE_END -> this.tickShockwaveAttack(state);
            case ATTACK_EXCALIBUR_START -> this.tickExcaliburStart();
            case ATTACK_EXCALIBUR_END -> this.tickExcaliburEnd();
            case ATTACK_HEAD_BREAK -> this.tickHeadBreakAttack();
            case ATTACK_ICESHOCK -> this.tickIceShockAttack();
            case BACKSTEP, BACKSTEP_BEFORE_CHARGE -> this.tickBackstep(state);
            case ATTACK_ULTIMATE -> this.tickUltimateSequence();
            case ATTACK_LAST2 -> this.tickLast2Landing();
            case ATTACK_DEAD -> this.tickDeadState();
            default -> this.finishAttack(1.5F);
        }
    }

    private void tickJabCombo(int state) {
        switch (state) {
            case ATTACK_JAB_1 -> {

                if (this.attackTicks == ticks(1.05F)) {
                    float yaw = this.getYRot() * ((float) Math.PI / 180F);
                    double pushSpeed = 1.2D;
                    this.setDeltaMovement(-Mth.sin(yaw) * pushSpeed, this.getDeltaMovement().y, Mth.cos(yaw) * pushSpeed);
                    this.hasImpulse = true;
                }

                if (this.attackTicks == ticks(1.2083F)) {
                    this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
                    this.hasImpulse = true;
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5F, 0.6F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 15.0F, 0.15F, 0, 8);
                    this.recordAttackResult(this.performForwardArcDamage(1.0F, 3.4F, 110.0F, 0.35F, 0.1D, 0.0D));
                }

                if (this.attackTicks >= ticks(1.2917F)) {
                    this.setAttackState(ATTACK_JAB_2);
                }
            }
            case ATTACK_JAB_2 -> {

                if (this.attackTicks == ticks(0.75F)) {
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5F, 0.7F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 12.0F, 0.1F, 0, 6);
                    this.recordAttackResult(this.performForwardArcDamage(0.9F, 3.4F, 110.0F, 0.35F, 0.1D, 0.0D));
                }

                if (this.attackTicks >= ticks(0.875F)) {
                    this.setAttackState(ATTACK_JAB_3);
                }
            }
            case ATTACK_JAB_3 -> {

                if (this.attackTicks == ticks(0.9583F)) {
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
                    this.recordAttackResult(this.performForwardArcDamage(1.25F, 3.8F, 120.0F, 0.65F, 0.2D, 0.05D));
                    float rad = this.yBodyRot * ((float) Math.PI / 180F);
                    double rightX = Math.cos(rad);
                    double rightZ = Math.sin(rad);
                    this.spawnAssociatedPhantom(this.getX() + rightX * 1.8D, this.getY(), this.getZ() + rightZ * 1.8D, this.getYRot(), MaledictusPhantomEntity.TYPE_SPEAR);
                    this.spawnAssociatedPhantom(this.getX() - rightX * 1.8D, this.getY(), this.getZ() - rightZ * 1.8D, this.getYRot(), MaledictusPhantomEntity.TYPE_SPEAR);
                }

                if (this.attackTicks >= ticks(2.5F)) {
                    this.jabCooldownSeconds = 1.0F;
                    this.setAttackState(0);
                }
            }
        }
    }

    private void tickExJabCombo(int state) {
        LivingEntity target = this.getTarget();

        switch (state) {
            case ATTACK_EX_JAB_1 -> {

                if (this.attackTicks == ticks(0.75F)) {
                    if (target != null) {
                        Vec3 toTarget = target.position().subtract(this.position());
                        double hDist = toTarget.horizontalDistance();
                        if (hDist > 0.1D) {
                            this.setDeltaMovement(toTarget.x / hDist * 1.5D, this.getDeltaMovement().y, toTarget.z / hDist * 1.5D);
                            this.hasImpulse = true;
                        }
                    } else {
                        float yaw = this.getYRot() * ((float) Math.PI / 180F);
                        this.setDeltaMovement(-Mth.sin(yaw) * 1.5D, this.getDeltaMovement().y, Mth.cos(yaw) * 1.5D);
                        this.hasImpulse = true;
                    }
                }

                if (this.attackTicks == ticks(1.125F)) {
                    this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
                    this.hasImpulse = true;
                    this.playSound((SoundEvent) ModSounds.PHANTOM_SPEAR.get(), 1.5F, 1.0F);
                    this.playSound(SoundEvents.TRIDENT_HIT, 1.2F, 0.8F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 15.0F, 0.15F, 0, 10);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles((ParticleOptions) ModParticle.PHANTOM_WING_FLAME.get(), this.getX(), this.getY() + 0.1D, this.getZ(), 15, 0.5D, 0.2D, 0.5D, 0.05D);
                    }
                    this.recordAttackResult(this.performComboLockDamage(1.25F, 4.2F, 120.0F));
                }

                if (this.attackTicks >= ticks(1.1667F)) {
                    this.setAttackState(ATTACK_EX_JAB_2);
                }
            }
            case ATTACK_EX_JAB_2 -> {

                if (this.attackTicks == ticks(1.0F)) {
                    Vec3 direction = target != null
                            ? target.position().subtract(this.position())
                            : Vec3.directionFromRotation(0.0F, this.getYRot());
                    double horizontalDistance = direction.horizontalDistance();
                    if (horizontalDistance > 1.0E-4D) {
                        this.setDeltaMovement(
                                direction.x / horizontalDistance * 0.9D,
                                0.48D,
                                direction.z / horizontalDistance * 0.9D
                        );
                        this.hasImpulse = true;
                    }
                    this.playSound(SoundEvents.ENDER_DRAGON_FLAP, 1.0F, 1.8F);
                }
                if (this.attackTicks > ticks(1.0F) && this.attackTicks < ticks(1.7F) && target != null) {
                    this.lookAt(target, 360.0F, 360.0F);
                }

                if (this.attackTicks == ticks(0.875F)) {
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 1.5F, 0.7F);
                    this.playSound(SoundEvents.ANVIL_LAND, 1.5F, 0.6F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 30.0F, 0.3F, 0, 18);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.CRIT, this.getX(), this.getY() + 1.2D, this.getZ(), 15, 0.6D, 0.6D, 0.6D, 0.1D);
                        serverLevel.sendParticles((ParticleOptions) ModParticle.PHANTOM_WING_FLAME.get(), this.getX(), this.getY() + 0.1D, this.getZ(), 30, 2.0D, 0.2D, 2.0D, 0.1D);
                    }
                    this.recordAttackResult(this.performComboLockDamage(2.2F, 4.5F, 130.0F));
                    if (this.isPhase2() && target != null) {
                        this.spawnAssociatedPhantom(target.getX(), target.getY(), target.getZ(), this.getYRot(), MaledictusPhantomEntity.TYPE_MACE);
                    }
                }
                if (this.attackTicks >= ticks(1.7F)) {
                    this.setAttackState(ATTACK_EX_JAB_3);
                }
            }
            case ATTACK_EX_JAB_3 -> {

                if (!this.level().isClientSide() && this.attackTicks == 1) {
                    double pushX = 0.0D, pushZ = 0.0D;
                    if (target != null) {
                        Vec3 toTarget = target.position().subtract(this.position());
                        double hDist = toTarget.horizontalDistance();
                        if (hDist > 0.1D) {
                            pushX = toTarget.x / hDist * 0.85D;
                            pushZ = toTarget.z / hDist * 0.85D;
                        }
                    }
                    this.setDeltaMovement(pushX, -2.5D, pushZ);
                    this.hasImpulse = true;
                }

                if (this.attackTicks == ticks(0.6667F)) {
                    this.playSound((SoundEvent) ModSounds.PHANTOM_SPEAR.get(), 1.5F, 1.0F);
                    this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.8F, 0.5F);
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 2.0F, 0.5F);
                    ScreenShake_Entity.ScreenShake(this.level(), this.position(), 35.0F, 0.4F, 0, 18);
                    this.recordAttackResult(this.performForwardArcDamage(2.8F, 5.0F, 130.0F, 1.8F, 0.6D, 0.45D));
                    if (this.isPhase2()) {
                        this.spawnSpikeRing(2.5D, 12, 0, this.getAttackDamage(1.2F));
                    }
                }

                if (this.attackTicks >= ticks(2.5F)) {
                    this.jabCooldownSeconds = 3.5F;
                    this.exJabCooldownSeconds = 5.0F;
                    this.setAttackState(0);
                }
            }
        }
    }

    private void tickChargeAttack() {
        if (this.attackTicks == ticks(1.4F)) {
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
                                this.yRotO = targetAngle;
                                this.yBodyRotO = targetAngle;
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
                        if (this.attackTicks >= ticks(1.5F) && this.attackTicks <= ticks(2.42F)) {
                            double chargeSpeed = this.isPhase2() ? 1.4D : 1.15D;
                            if (this.chargeDirection != null) {
                                this.setDeltaMovement(
                                        this.chargeDirection.x * chargeSpeed,
                                        this.getDeltaMovement().y,
                                        this.chargeDirection.z * chargeSpeed
                                );
                                this.hasImpulse = true;
                                float lockAngle = (float) (Mth.atan2(this.chargeDirection.z, this.chargeDirection.x) * (180D / Math.PI)) - 90.0F;
                                this.setYRot(lockAngle);
                                this.yBodyRot = lockAngle;
                                this.yHeadRot = lockAngle;
                                this.yRotO = lockAngle;
                                this.yBodyRotO = lockAngle;
                            }
                            if (!this.level().isClientSide()) {
                                java.util.List<LivingEntity> targets = this.level().getEntitiesOfClass(
                                        LivingEntity.class,
                                        this.getBoundingBox().inflate(1.5D, 0.5D, 1.5D)
                                );
                                for (LivingEntity t : targets) {
                                    if (t != this && t.isAlive() && !this.chargedHitEntities.contains(t) && this.canDamageTarget(t)) {
                                        float damage = this.getAttackDamage(0.7F);
                                        EntityDamageHelper.hurtIgnoringInvulnerability(t, this, damage, "death.maledictus_prime.1");
                                        this.chargedHitEntities.add(t);
                                            float pushDirectionYaw = this.getYRot() + (this.random.nextBoolean() ? 45.0F : -45.0F);
                                            float pushYawRad = pushDirectionYaw * ((float) Math.PI / 180F);
                                            t.setDeltaMovement(
                                                    -Math.sin(pushYawRad) * 1.3D,
                                                    0.35D,
                                                    Math.cos(pushYawRad) * 1.3D
                                            );
                                            t.hasImpulse = true;
                                            this.attackTicks = ticks(2.42F);
                                        break;
                                    }
                                }
                            }
                        } else if (this.attackTicks > ticks(2.42F) && this.attackTicks < ticks(2.6F)) {
                            this.setDeltaMovement(this.getDeltaMovement().multiply(0.65D, 1.0D, 0.65D));
                        } else if (this.attackTicks >= ticks(2.6F)) {
                            this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
                        }
                        if (this.attackTicks == ticks(2.6F)) {
                            this.playSound(SoundEvents.GENERIC_EXPLODE, 1.2F, 0.7F);
                            ScreenShake_Entity.ScreenShake(this.level(), this.position(), 25.0F, 0.3F, 0, 15);
                            if (this.level() instanceof ServerLevel serverLevel) {
                            }
                            this.chargeDirection = null;
                            this.chargedHitEntities.clear();
                        }
                        if (this.attackTicks >= ticks(3.0833F)) {
                            this.chargeCooldownSeconds = 5.5F;
                            this.setAttackState(0);
                        }
    }

    private void tickShockwaveAttack(int state) {
        switch (state) {
            case ATTACK_SHOCKWAVE_START -> {
        int jumpStart = ticks(0.46F);
                        int diveFrame = ticks(1.3333F);
                        if (!this.shockwaveJumped && this.attackTicks >= jumpStart) {
                            this.shockwaveJumped = true;
                        }
                        if (this.shockwaveJumped && !this.airborneAttackForcedDescent) {
                            int elapsed = this.attackTicks - jumpStart;
                            if (!this.level().isClientSide()) {
                                if (elapsed == 0) {
                                    LivingEntity target = this.getTarget();
                                    double pushX = 0.0D, pushZ = 0.0D;
                                    if (target != null) {
                                        Vec3 toTarget = target.position().subtract(this.position());
                                        double hDist = toTarget.horizontalDistance();
                                        if (hDist > 0.1D) {
                                            pushX = toTarget.x / hDist * 0.45D;
                                            pushZ = toTarget.z / hDist * 0.45D;
                                        }
                                    }
                                    this.setDeltaMovement(pushX, 1.4D, pushZ);
                                    this.hasImpulse = true;
                                } else if (this.attackTicks < diveFrame) {
                                    Vec3 current = this.getDeltaMovement();
                                    this.setDeltaMovement(current.x * 0.9D, Math.max(0.015D, current.y * 0.82D), current.z * 0.9D);
                                    this.hasImpulse = true;
                                } else if (this.attackTicks == diveFrame) {
                                    LivingEntity target = this.getTarget();
                                    if (target != null) {
                                        Vec3 toTarget = target.position().subtract(this.position());
                                        double hDist = toTarget.horizontalDistance();
                                        if (hDist > 0.1D) {
                                            double diveSpeed = Math.min(5.5D, Math.max(2.4D, hDist * 0.42D));
                                            this.setDeltaMovement(toTarget.x / hDist * diveSpeed, -3.2D, toTarget.z / hDist * diveSpeed);
                                        } else {
                                            this.setDeltaMovement(0.0D, -3.2D, 0.0D);
                                        }
                                    } else {
                                        this.setDeltaMovement(0.0D, -3.2D, 0.0D);
                                    }
                                    this.hasImpulse = true;
                                }
                            }
                        }
                        if (this.shockwaveJumped && this.onGround() && this.attackTicks > jumpStart + ticks(0.25F)) {
                            this.setAttackState(ATTACK_SHOCKWAVE_END);
                        }
            }
            case ATTACK_SHOCKWAVE_END -> {
        if (this.attackTicks == ticks(0.21F)) {
                            this.playSound(SoundEvents.GENERIC_EXPLODE, 2.0F, 0.6F);
                            ScreenShake_Entity.ScreenShake(this.level(), this.position(), 50.0F, 0.5F, 0, 30);
                            if (this.level() instanceof ServerLevel serverLevel) {
                                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY() + 0.2D, this.getZ(), 30, 2.0D, 0.2D, 2.0D, 0.1D);
                                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.2D, this.getZ(), 20, 1.5D, 0.5D, 1.5D, 0.05D);
                            }
                            this.performPointBlankShockwave();
                            this.performSwordSpikeWave();
                        } else if (this.attackTicks >= ticks(1.375F)) {
                            this.shockwaveCooldownSeconds = 7.5F;
                            this.setAttackState(0);
                        }
            }
        }
    }

    private void tickExcaliburStart() {
        this.getNavigation().stop();
        this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
        LivingEntity target = this.getTarget();
        if (target != null) {
            this.lookAt(target, 40.0F, 40.0F);
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot();
        }
        if (this.attackTicks >= 40) {
            this.setAttackState(ATTACK_EXCALIBUR_END);
        }
    }

    private void tickExcaliburEnd() {
        this.getNavigation().stop();
        this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
        LivingEntity target = this.getTarget();

        if (this.attackTicks == 19) {
            this.playSound(SoundEvents.GENERIC_EXPLODE, 3.5F, 0.45F);
            this.playSound(SoundEvents.ANVIL_LAND, 3.0F, 0.5F);
            this.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 3.0F, 0.55F);
            ScreenShake_Entity.ScreenShake(this.level(), this.position(), 75.0F, 0.7F, 0, 35);
            this.performAreaDamage(3.5F, 1.8F, 7.0D, 3.5D, 0.5D, 1.0D);
            this.spawnFallingBlockShockwave(this.getX(), this.getY(), this.getZ(), 5);

            if (!this.level().isClientSide()) {
                float baseYaw = this.yBodyRot;
                float[] angleOffsets = {-36.0F, -18.0F, 0.0F, 18.0F, 36.0F};
                float spikeDamage = this.getAttackDamage(2.0F);
                for (float angleOffset : angleOffsets) {
                    float rad = (baseYaw + angleOffset) * ((float) Math.PI / 180F);
                    Vec3 direction = new Vec3(-Mth.sin(rad), 0.0D, Mth.cos(rad)).normalize();
                    for (int i = 1; i <= 10; i++) {
                        double distance = i * 2.8D;
                        this.spawnSpikeAt(this.getX() + direction.x * distance,
                                this.getY(), this.getZ() + direction.z * distance,
                                baseYaw + angleOffset, (i - 1) * 2, spikeDamage);
                    }
                }

                if (this.level() instanceof ServerLevel serverLevel) {
                    float forwardRad = baseYaw * ((float) Math.PI / 180F);
                    Vec3 forward = new Vec3(-Mth.sin(forwardRad), 0.0D, Mth.cos(forwardRad)).normalize();
                    double[] laserDistances = {8.0D, 16.0D, 24.0D, 30.0D};
                    float laserDamage = this.getAttackDamage(2.2F);
                    for (double distance : laserDistances) {
                        triggerSkyLaserImpact(serverLevel,
                                this.getX() + forward.x * distance,
                                this.getY(),
                                this.getZ() + forward.z * distance,
                                laserDamage);
                    }
                    if (target != null && target.isAlive()) {
                        triggerSkyLaserImpact(serverLevel, target.getX(), target.getY(), target.getZ(), laserDamage);
                    }
                }
            }
        }

        if (this.attackTicks >= 48) {
            this.excaliburCooldownSeconds = this.isPhase2() ? 14.0F : 18.0F;
            this.setAttackState(0);
        }
    }

    /**
     * Resolves a vertical glacial beam impact at ground level. The damage box
     * is deliberately wider than the beam so the laser remains a zone attack.
     */
    private void triggerSkyLaserImpact(ServerLevel level, double x, double y, double z, float damage) {
        net.minecraft.world.phys.AABB impactBox = new net.minecraft.world.phys.AABB(
                x - 2.5D, y - 1.0D, z - 2.5D,
                x + 2.5D, y + 4.0D, z + 2.5D
        );
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, impactBox);
        for (LivingEntity target : targets) {
            if (this.canDamageTarget(target) && target.distanceToSqr(x, y, z) <= 2.5D * 2.5D) {
                EntityDamageHelper.hurtIgnoringInvulnerability(target, this, damage, "death.maledictus_prime.1");
                target.setDeltaMovement(target.getDeltaMovement().add(0.0D, 0.75D, 0.0D));
                target.hasImpulse = true;
            }
        }

        level.playSound(null, x, y, z, SoundEvents.LIGHTNING_BOLT_IMPACT,
                this.getSoundSource(), 2.0F, 0.6F);
        level.playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE,
                this.getSoundSource(), 2.0F, 0.8F);
        net.minecraft.core.BlockPos groundPos = net.minecraft.core.BlockPos.containing(x, y - 0.5D, z);
        BlockState groundState = level.getBlockState(groundPos);
        if (groundState.isAir()) {
            groundState = Blocks.DEEPSLATE.defaultBlockState();
        }
        level.sendParticles(new net.minecraft.core.particles.BlockParticleOption(
                        ParticleTypes.BLOCK, groundState),
                x, y + 0.2D, z, 30, 1.2D, 0.6D, 1.2D, 0.15D);
        level.sendParticles(new net.minecraft.core.particles.BlockParticleOption(
                        ParticleTypes.BLOCK, Blocks.BLUE_ICE.defaultBlockState()),
                x, y + 0.5D, z, 40, 1.0D, 0.8D, 1.0D, 0.2D);
        for (double beamY = y; beamY <= y + 30.0D; beamY += 1.5D) {
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, beamY, z,
                    2, 0.1D, 0.0D, 0.1D, 0.0D);
            level.sendParticles(ParticleTypes.END_ROD, x, beamY, z,
                    1, 0.05D, 0.0D, 0.05D, 0.01D);
        }
        level.sendParticles(ParticleTypes.EXPLOSION, x, y + 0.5D, z,
                1, 0.0D, 0.0D, 0.0D, 0.0D);
        level.sendParticles(
                new net.minecraft.core.particles.BlockParticleOption(
                        ParticleTypes.BLOCK, Blocks.BLUE_ICE.defaultBlockState()),
                x, y + 0.2D, z, 25, 1.2D, 0.5D, 1.2D, 0.15D);
    }

    /** Lift the surface blocks around an impact as physical debris. */
    private void spawnFallingBlockShockwave(double centerX, double centerY, double centerZ, int radius) {
        if (this.level().isClientSide()) {
            return;
        }
        for (int distance = 1; distance <= radius; distance++) {
            int points = distance * 5;
            for (int i = 0; i < points; i++) {
                double rad = Math.toRadians((360.0D / points) * i);
                double px = centerX + Math.cos(rad) * distance;
                double pz = centerZ + Math.sin(rad) * distance;
                net.minecraft.core.BlockPos pos = net.minecraft.core.BlockPos.containing(px, centerY - 1.0D, pz);
                while (this.level().isEmptyBlock(pos) && pos.getY() > this.level().getMinBuildHeight()) {
                    pos = pos.below();
                }
                BlockState state = this.level().getBlockState(pos);
                if (!state.isAir()
                        && state.getRenderShape() == net.minecraft.world.level.block.RenderShape.MODEL) {
                    Cm_Falling_Block_Entity falling = new Cm_Falling_Block_Entity(
                            this.level(), px, pos.getY() + 1.0D, pz, state,
                            15 + this.getRandom().nextInt(10));
                    falling.push(0.0D,
                            0.14D + distance * 0.01D + this.getRandom().nextDouble() * 0.1D,
                            0.0D);
                    this.level().addFreshEntity(falling);
                }
            }
        }
    }

    private void tickHeadBreakAttack() {
        this.getNavigation().stop();
                        this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
                        if (this.attackTicks == ticks(2.0F)) {
                            this.playSound(SoundEvents.GENERIC_EXPLODE, 2.0F, 0.5F);
                            this.playSound(SoundEvents.ANVIL_LAND, 2.0F, 0.5F);
                            this.playSound(SoundEvents.GLASS_BREAK, 1.8F, 0.7F);
                            this.playSound(SoundEvents.SHIELD_BREAK, 1.5F, 0.6F);
                            ScreenShake_Entity.ScreenShake(this.level(), this.position(), 45.0F, 0.4F, 0, 20);
                            this.performAreaDamage(2.5F, 1.2F, 6.0D, 3.0D, 0.3D, 0.45D);
                            if (this.level() instanceof ServerLevel serverLevel) {
                                serverLevel.sendParticles((ParticleOptions) ModParticle.PHANTOM_WING_FLAME.get(), this.getX(), this.getY() + 0.1D, this.getZ(), 45, 2.5D, 0.2D, 2.5D, 0.15D);
                                serverLevel.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY() + 0.1D, this.getZ(), 30, 2.0D, 0.2D, 2.0D, 0.1D);
                                Vec3 handPos = this.position().add(0.0D, 1.7D, 0.0D);
                                BlockState breakState = Blocks.OBSIDIAN.defaultBlockState();
                                serverLevel.sendParticles(
                                        new net.minecraft.core.particles.BlockParticleOption(ParticleTypes.BLOCK, breakState),
                                        handPos.x, handPos.y, handPos.z,
                                        40, 0.5D, 0.5D, 0.5D, 0.15D
                                );
                            }
                            if (this.isPhase2()) {
                                this.performSwordSpikeWave();
                            }
                        }
                        if (this.attackTicks >= ticks(3.7917F)) {
                            this.setAttackState(0);
                        }
    }

    /** Ice shock: impact at 26 ticks, then the full 60-tick animation recovery. */
    private void tickIceShockAttack() {
        this.getNavigation().stop();
        this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
        LivingEntity target = this.getTarget();

        if (this.attackTicks < 24 && target != null) {
            this.lookAt(target, 40.0F, 40.0F);
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot();
        }

        if (this.attackTicks == 26) {
            this.playSound(SoundEvents.GENERIC_EXPLODE, 2.0F, 0.55F);
            this.playSound(SoundEvents.ANVIL_LAND, 1.8F, 0.5F);
            ScreenShake_Entity.ScreenShake(this.level(), this.position(), 40.0F, 0.45F, 0, 20);
            this.performAreaDamage(1.5F, 1.0F, 4.0D, 2.5D, 0.2D, 0.4D);

            if (!this.level().isClientSide()) {
                float yawRad = this.yBodyRot * ((float) Math.PI / 180F);
                Vec3 forward = new Vec3(-Mth.sin(yawRad), 0.0D, Mth.cos(yawRad)).normalize();
                float spikeDamage = this.getAttackDamage(1.6F);
                for (int i = 1; i <= 8; i++) {
                    double distance = i * 1.85D;
                    this.spawnSpikeAt(
                            this.getX() + forward.x * distance,
                            this.getY(),
                            this.getZ() + forward.z * distance,
                            this.getYRot(),
                            (i - 1) * 2,
                            spikeDamage
                    );
                }
            }
        }

        if (this.attackTicks >= 60) {
            this.iceshockCooldownSeconds = this.isPhase2() ? 7.0F : 9.0F;


            Vec3 retreat = null;
            if (target != null && target.isAlive()) {
                Vec3 away = new Vec3(this.getX() - target.getX(), 0.0D,
                        this.getZ() - target.getZ());
                if (away.horizontalDistanceSqr() > 1.0E-4D) {
                    away = away.normalize();
                    double sideSign = this.getRandom().nextBoolean() ? 1.0D : -1.0D;
                    Vec3 side = new Vec3(-away.z, 0.0D, away.x).scale(sideSign);
                    retreat = away.scale(0.75D).add(side.scale(0.65D)).normalize().scale(0.65D);
                }
            }
            if (retreat != null && this.getRandom().nextFloat() < 0.65F) {
                this.setDeltaMovement(retreat.x, this.getDeltaMovement().y, retreat.z);
                this.hasImpulse = true;
            }
            this.setAttackState(0);
        }
    }

    private void tickBackstep(int state) {
        switch (state) {
            case BACKSTEP -> {
        if (this.attackTicks < ticks(0.5F)) {
                            this.chargeForward(-1.1D);
                        } else {
                            this.setDeltaMovement(this.getDeltaMovement().multiply(0.75D, 1.0D, 0.75D));
                        }
                if (this.attackTicks >= ticks(1.1F)) {
                    this.backstepCooldownSeconds = 8.0F;
                    LivingEntity target = this.getTarget();
                    if (target != null && target.isAlive() && !this.isEcho()) {
                        float roll = this.getRandom().nextFloat();
                        if (roll < 0.25F && this.isExcaliburReady()) {
                            this.setAttackState(ATTACK_EXCALIBUR_START);
                            return;
                        } else if (roll < 0.50F && this.isIceShockReady()) {
                            this.setAttackState(ATTACK_ICESHOCK);
                            return;
                        } else if (roll < 0.75F && this.isChargeReady()) {
                            this.setAttackState(ATTACK_CHARGE);
                            return;
                        } else if (this.isExJabReady() || this.isJabReady()) {
                            this.startFlashStep(target, this.isExJabReady() ? ATTACK_EX_JAB_1 : ATTACK_JAB_1);
                            return;
                        }
                    }
                    this.backstepRecoverySeconds = 1.8F;
                    this.setAttackState(0);
                }
            }
            case BACKSTEP_BEFORE_CHARGE -> {
        if (this.attackTicks < ticks(0.5F)) {
                            this.chargeForward(-1.1D);
                        } else {
                            this.setDeltaMovement(this.getDeltaMovement().multiply(0.75D, 1.0D, 0.75D));
                        }
                        if (this.attackTicks >= ticks(0.9167F)) {
                            this.backstepCooldownSeconds = 12.0F;
                            this.setAttackState(ATTACK_CHARGE);
                        }
            }
        }
    }

    private void tickLast2Landing() {
        if (this.attackTicks >= ticks(0.2083F)) {
                            this.setAttackState(ATTACK_DEAD);
                        }
    }

    private void tickDeadState() {
        if (this.attackTicks >= ticks(2.0F)) {
                            if (this.ultimateDeathStarted) {
                                this.setHealth(1.0F);
                                this.setInvulnerable(true);
                                this.setDeltaMovement(Vec3.ZERO);
                                this.setTarget(null);
                                this.goalSelector.removeAllGoals(goal -> true);
                                this.targetSelector.removeAllGoals(goal -> true);
                                this.discardUltimatePhantoms(-1);
                                this.entityData.set(IS_DOWNED, true);
                                this.entityData.set(DIALOGUE_INDEX, 0);
                            } else {
                                this.deathSequenceFinished = true;
                                this.die(this.damageSources().generic());
                                this.remove(RemovalReason.KILLED);
                            }
                        }
    }

    @Override
    public boolean canBeAffected(net.minecraft.world.effect.MobEffectInstance effect) {
        return false;
    }

    private void tickUltimateSequence() {
        int elapsed = this.attackTicks;
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            target = this;
        }
        if (elapsed < ticks(2.13F)) {
            this.setDeltaMovement(0.0D, 0.0D, 0.0D);
        }
        if (elapsed == ticks(2.13F)) {
            this.playSound(SoundEvents.ENDER_DRAGON_GROWL, 2.0F, 0.55F);
            this.playSound(SoundEvents.GLASS_BREAK, 2.0F, 0.65F);
            this.setDeltaMovement(0.0D, 1.15D, 0.0D);
            this.hasImpulse = true;
            ScreenShake_Entity.ScreenShake(this.level(), this.position(), 35.0F, 0.35F, 0, 18);
        } else if (elapsed > ticks(2.13F) && elapsed < ticks(2.5F)) {
            this.setDeltaMovement(0.0D, 1.15D, 0.0D);
            this.hasImpulse = true;
        }
        if (elapsed == ticks(2.5F)) {
            this.setInvisible(true);
            this.setDeltaMovement(0.0D, 0.0D, 0.0D);
            this.playSound(SoundEvents.GENERIC_EXPLODE, 2.0F, 0.55F);
            ScreenShake_Entity.ScreenShake(this.level(), this.position(), 50.0F, 0.45F, 0, 24);
        }
        if (elapsed >= ticks(2.5F) && elapsed < ticks(6.5F)
                && (elapsed - ticks(2.5F)) % ticks(0.5F) == 0) {
            int index = (elapsed - ticks(2.5F)) / ticks(0.5F);
            this.spawnUltimateSpear(target, index, 8);
        }
        if (elapsed == ticks(6.5F)) {
            this.discardUltimatePhantoms(MaledictusPhantomEntity.TYPE_SPEAR);
            this.playSound(SoundEvents.ANVIL_LAND, 2.0F, 0.55F);
            for (int i = 0; i < 5; i++) {
                this.spawnUltimateMace(target, i, 5);
            }
        }
        int spikeTick = elapsed - ticks(6.5F);
        if (spikeTick >= ticks(0.35F) && spikeTick <= ticks(2.2F)
                && spikeTick % ticks(0.45F) == 0) {
            double radius = 6.5D - (spikeTick / (double) ticks(0.45F)) * 1.25D;
            this.spawnUltimateSpikeRing(target, Math.max(1.5D, radius), 12, ticks(0.08F), this.getAttackDamage(1.1F));
        }
        if (elapsed == ticks(9.5F)) {
            for (int i = 0; i < 4; i++) {
                this.spawnUltimateBow(target, i);
            }
        }
        if (elapsed == ticks(10.5F)) {
            this.spawnUltimateSpear(target, 0, 2);
            this.spawnUltimateMace(target, 1, 2);
            this.spawnUltimateSpear(target, 1, 2);
        }
        if (elapsed == ticks(13.5F)) {
            this.discardUltimatePhantoms(-1);
            ScreenShake_Entity.ScreenShake(this.level(), target.position(), 60.0F, 0.35F, 0, 24);
        }
        if (elapsed == ticks(14.5F)) {
            this.setInvisible(false);
            this.setPos(target.getX(), target.getY() + 14.0D, target.getZ());
            this.setNoGravity(false);
            this.setDeltaMovement(0.0D, -3.0D, 0.0D);
            this.hasImpulse = true;
            this.playSound(SoundEvents.ENDER_DRAGON_FLAP, 2.0F, 0.5F);
        }
        if (elapsed == ticks(15.5F)) {
            this.setPos(target.getX(), target.getY(), target.getZ());
            this.setDeltaMovement(0.0D, 0.0D, 0.0D);
            this.setUltimateLanding(true);
            this.setAttackState(ATTACK_LAST2);
            this.playSound(SoundEvents.GENERIC_EXPLODE, 3.0F, 0.45F);
            ScreenShake_Entity.ScreenShake(this.level(), this.position(), 90.0F, 0.8F, 0, 45);
            this.performUltimateShockwave();
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY() + 0.4D, this.getZ(),
                        100, 6.0D, 0.8D, 6.0D, 0.12D);
                serverLevel.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY() + 1.0D, this.getZ(),
                        60, 5.0D, 2.0D, 5.0D, 0.1D);
            }
        }
        if (elapsed >= ticks(16.5F) && elapsed < ticks(18.0F)) {
            this.setDeltaMovement(0.0D, 0.0D, 0.0D);
            if (this.level() instanceof ServerLevel serverLevel && elapsed % 3 == 0) {
                serverLevel.sendParticles(ParticleTypes.WHITE_ASH, this.getX(), this.getY() + 1.0D, this.getZ(),
                        12, 0.6D, 1.0D, 0.6D, 0.02D);
            }
        }

    }

    private void spawnUltimateSpear(LivingEntity target, int index, int count) {
        double angle = (Math.PI * 2.0D * index / count) - Math.PI / 2.0D;
        double x = target.getX() + Math.cos(angle) * 8.0D;
        double z = target.getZ() + Math.sin(angle) * 8.0D;
        float yaw = (float) (Math.atan2(target.getZ() - z, target.getX() - x) * (180.0D / Math.PI)) - 90.0F;
        this.spawnAssociatedPhantom(x, target.getY(), z, yaw, MaledictusPhantomEntity.TYPE_SPEAR);
    }

    private void spawnUltimateMace(LivingEntity target, int index, int count) {
        double angle = Math.PI * 2.0D * index / count;
        double x = target.getX() + Math.cos(angle) * 4.5D;
        double z = target.getZ() + Math.sin(angle) * 4.5D;
        float yaw = (float) (Math.atan2(target.getZ() - z, target.getX() - x) * (180.0D / Math.PI)) - 90.0F;
        this.spawnAssociatedPhantom(x, target.getY(), z, yaw, MaledictusPhantomEntity.TYPE_MACE);
    }

    private void spawnUltimateBow(LivingEntity target, int index) {
        double xOffset = (index % 2 == 0 ? -1.0D : 1.0D) * 7.5D;
        double zOffset = (index < 2 ? -1.0D : 1.0D) * 7.5D;
        float yaw = (float) (Math.atan2(target.getZ() - (target.getZ() + zOffset),
                target.getX() - (target.getX() + xOffset)) * (180.0D / Math.PI)) - 90.0F;
        this.spawnAssociatedPhantom(target.getX() + xOffset, target.getY() + 1.0D, target.getZ() + zOffset,
                yaw, MaledictusPhantomEntity.TYPE_BOW);
    }

    private void spawnUltimateSpikeRing(LivingEntity target, double radius, int count, int warmup, float damage) {
        double angleStep = (Math.PI * 2.0D) / count;
        for (int i = 0; i < count; i++) {
            double angle = i * angleStep;
            this.spawnSpikeAt(target.getX() + Math.cos(angle) * radius, target.getY(),
                    target.getZ() + Math.sin(angle) * radius, (float) (angle * (180.0D / Math.PI)) - 90.0F,
                    warmup, damage);
        }
    }

    private void performUltimateShockwave() {
        this.performAreaDamage(4.2F, 2.2F, 18.0D, 7.0D, 0.45D, 1.1D);
        this.spawnSpikeRing(15.0D, 32, ticks(0.05F), this.getAttackDamage(2.2F));
        this.spawnSpikeRing(11.0D, 24, ticks(0.15F), this.getAttackDamage(1.9F));
        this.spawnSpikeRing(7.0D, 16, ticks(0.25F), this.getAttackDamage(1.6F));
        this.spawnSpikeRing(3.5D, 10, ticks(0.35F), this.getAttackDamage(1.3F));
    }

    private void discardUltimatePhantoms(int type) {
        if (this.level().isClientSide()) {
            return;
        }
        List<MaledictusPhantomEntity> phantoms = this.level().getEntitiesOfClass(MaledictusPhantomEntity.class,
                this.getBoundingBox().inflate(32.0D));
        for (MaledictusPhantomEntity phantom : phantoms) {
            if (phantom.getSummoner() == this && (type < 0 || phantom.getPhantomType() == type)) {
                phantom.discard();
            }
        }
    }

    private void finishAttack(float allCooldownSeconds) {
        this.jabCooldownSeconds = Math.max(this.jabCooldownSeconds, allCooldownSeconds);
        this.chargeCooldownSeconds = Math.max(this.chargeCooldownSeconds, allCooldownSeconds);
        this.counterCooldownSeconds = Math.max(this.counterCooldownSeconds, allCooldownSeconds);
        this.shockwaveCooldownSeconds = Math.max(this.shockwaveCooldownSeconds, allCooldownSeconds);
        this.exJabCooldownSeconds = Math.max(this.exJabCooldownSeconds, allCooldownSeconds);
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
        double effectiveRange = EntityDamageHelper.expandRange(range);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(effectiveRange, EntityDamageHelper.expandRange(2.0D), effectiveRange));
        for (LivingEntity target : targets) {
            if (this.canDamageTarget(target) && this.isInFrontArc(target, arc) && this.distanceTo(target) <= effectiveRange + this.getBbWidth()) {
                EntityDamageHelper.hurtIgnoringInvulnerability(target, this, this.getAttackDamage(damageMultiplier), "death.maledictus_prime.1");
                this.applyAttackKnockback(target, knockback, forwardPush, verticalImpulse);
                hit = true;
            }
        }
        return hit;
    }

    private boolean performAreaDamage(float damageMultiplier, float knockback, double xzRange, double yRange,
                                      double forwardPush, double verticalImpulse) {
        boolean hit = false;
        double effectiveXzRange = EntityDamageHelper.expandRange(xzRange);
        double effectiveYRange = EntityDamageHelper.expandRange(yRange);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(effectiveXzRange, effectiveYRange, effectiveXzRange));
        for (LivingEntity target : targets) {
            if (this.canDamageTarget(target) && this.distanceTo(target) <= effectiveXzRange + this.getBbWidth()) {
                EntityDamageHelper.hurtIgnoringInvulnerability(target, this, this.getAttackDamage(damageMultiplier), "death.maledictus_prime.1");
                this.applyAttackKnockback(target, knockback, forwardPush, verticalImpulse);
                hit = true;
            }
        }
        return hit;
    }

    private void performPointBlankShockwave() {
        LivingEntity shockwaveTarget = this.getTarget();
        double targetDistance = shockwaveTarget == null ? 0.0D : this.distanceTo(shockwaveTarget);
        double radius = Math.min(7.5D, Math.max(5.0D, targetDistance + 1.5D));
        this.performForwardArcDamage(2.6F, (float) Math.min(4.8D, radius), 110.0F, 1.2F, 0.25D, 0.45D);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(radius, 2.5D, radius));
        for (LivingEntity target : targets) {
            if (this.canDamageTarget(target) && this.distanceTo(target) <= radius + this.getBbWidth()) {
                target.setDeltaMovement(target.getDeltaMovement().add(0.0D, 0.9D, 0.0D));
                target.hasImpulse = true;
            }
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
        if (!this.isEcho()) {
            this.bossEvent.addPlayer(player);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        if (!this.isEcho()) {
            this.bossEvent.removePlayer(player);
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!this.level().isClientSide() && !this.isEcho() && this.phaseTwoEcho != null && this.phaseTwoEcho.isAlive()) {
            this.phaseTwoEcho.discard();
        }
        super.remove(reason);
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (this.isEcho()) {
            return false;
        }
        if (this.ultimateDeathStarted && !this.deathSequenceFinished) {
            return false;
        }



        int state = this.getAttackState();
        if (state == BACKSTEP
                || state == BACKSTEP_BEFORE_CHARGE
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
                }
            }
            this.setAttackState(ATTACK_COUNTER_SUCCESS);
            return false;
        }
        float cappedAmount = Math.min(amount, this.DamageCap());
        if (state == ATTACK_CHARGE || state == ATTACK_SHOCKWAVE_END) {
            cappedAmount *= 0.5F;
        }
        if (this.isPhase2() && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            cappedAmount *= 0.85F;
        }


        if (!this.level().isClientSide() && !this.isEcho()
                && this.getHealth() - cappedAmount <= 1.0F
                && !this.ultimateDeathStarted && !this.deathSequenceFinished) {
            this.beginUltimateDeathSequence();
            return true;
        }
        boolean hurt = super.hurt(source, cappedAmount);


        if (hurt && !this.level().isClientSide() && !this.isEcho()
                && this.getHealth() <= 1.0F && !this.ultimateDeathStarted && !this.deathSequenceFinished) {
            this.beginUltimateDeathSequence();
        }
        if (hurt) {
            this.interruptAirborneAttackOnHit();
        }
        return hurt;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.isDowned()) {
            return super.mobInteract(player, hand);
        }
        if (this.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (player.isSpectator()) {
            return InteractionResult.PASS;
        }
        int nextIndex = this.getDialogueIndex() + 1;
        if (nextIndex >= MAX_DIALOGUE) {
            this.processFinalDeath(player);
        } else {
            this.entityData.set(DIALOGUE_INDEX, nextIndex);
        }
        return InteractionResult.CONSUME;
    }

    private void processFinalDeath(Player player) {
        this.setInvulnerable(false);
        this.deathSequenceFinished = true;
        net.minecraft.world.damagesource.DamageSource source = this.damageSources().generic();
        this.dropAllDeathLoot(source);
        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.PLAYER_KILLED_ENTITY.trigger(serverPlayer, this, source);
        }
        this.die(source);
        this.discard();
    }
    private boolean performComboLockDamage(float damageMultiplier, float range, float arc) {
        boolean hit = false;
        double effectiveRange = EntityDamageHelper.expandRange(range);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(effectiveRange, EntityDamageHelper.expandRange(2.0D), effectiveRange));
        for (LivingEntity target : targets) {
            if (this.canDamageTarget(target) && this.isInFrontArc(target, arc) && this.distanceTo(target) <= effectiveRange + this.getBbWidth()) {
                EntityDamageHelper.hurtIgnoringInvulnerability(target, this, this.getAttackDamage(damageMultiplier), "death.maledictus_prime.1");
                Vec3 toBoss = this.position().subtract(target.position()).normalize().scale(0.15D);
                    target.setDeltaMovement(toBoss.x, Math.max(0.0D, target.getDeltaMovement().y * 0.5D), toBoss.z);
                    target.hasImpulse = true;
                hit = true;
            }
        }
        return hit;
    }
    private void cleanupDownedEntities() {
        if (this.level().isClientSide()) return;
        if (this.phaseTwoEcho != null && this.phaseTwoEcho.isAlive()) {
            this.phaseTwoEcho.discard();
        }
        for (Maledictus_PrimeEntity entity : this.level().getEntitiesOfClass(Maledictus_PrimeEntity.class,
                this.getBoundingBox().inflate(128.0D))) {
            if (entity != this && entity.isEcho()) {
                entity.discard();
            }
        }
        for (MaledictusPhantomEntity phantom : this.level().getEntitiesOfClass(MaledictusPhantomEntity.class,
                this.getBoundingBox().inflate(128.0D))) {
            if (phantom.getSummoner() == this) {
                phantom.discard();
            }
        }
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

    private void performSwordSpikeWave() {
        if (this.level().isClientSide()) {
            return;
        }
        boolean isPhase2 = this.isPhase2();
        float damage = this.getAttackDamage(1.5F);
        this.spawnSpikeRing(1.8D, 8, 0, damage);
        this.spawnSpikeRing(3.6D, 12, ticks(0.3F), damage);
        if (isPhase2) {
            this.spawnSpikeRing(5.4D, 16, ticks(0.6F), damage);
            this.spawnSpikeRing(7.2D, 20, ticks(0.9F), damage);
            LivingEntity target = this.getTarget();
            if (target != null) {
                Vec3 toTarget = target.position().subtract(this.position()).normalize();
                for (int i = 1; i <= 6; i++) {
                    double sx = this.getX() + toTarget.x * (i * 1.6D);
                    double sz = this.getZ() + toTarget.z * (i * 1.6D);
                    double sy = this.getY();
                    this.spawnSpikeAt(sx, sy, sz, this.getYRot(), ticks(i * 0.1F), damage);
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
        if (this.isEcho() || this.level().isClientSide()) {
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

    @Override
    public boolean isNoGravity() {
        if (this.airborneAttackForcedDescent) {
            return false;
        }
        int state = this.getAttackState();
        if (state == ATTACK_SHOCKWAVE_START || state == ATTACK_ULTIMATE) {
            return true;
        }
        return false;
    }
}
