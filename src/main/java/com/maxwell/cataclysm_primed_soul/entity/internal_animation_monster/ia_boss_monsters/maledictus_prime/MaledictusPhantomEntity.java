package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime;

import com.github.L_Ender.cataclysm.client.particle.Options.RingParticleOptions;
import com.github.L_Ender.cataclysm.client.particle.RingParticle.EnumRingBehavior;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.init.ModSounds;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class MaledictusPhantomEntity extends Mob {
    private static final float TICKS_PER_SECOND = 20.0F;
    public static final int TYPE_SPEAR = 0;
    public static final int TYPE_MACE = 1;
    public static final int TYPE_BOW = 2;
    public static final int TYPE_TRACE = 3;
    private static final int LIFE_SPEAR = ticks(3.0F);
    private static final int LIFE_MACE = ticks(2.0F);
    private static final int LIFE_BOW = ticks(3.5F);
    private static final int LIFE_TRACE = ticks(3.5F);
    private static final int TRACE_DELAY_TICKS = ticks(0.6F);
    private static final EntityDataAccessor<Integer> PHANTOM_TYPE =
            SynchedEntityData.defineId(MaledictusPhantomEntity.class, EntityDataSerializers.INT);
    private static final int SPEAR_CHARGE_START = ticks(1.1F);
    private static final int MACE_HIT_TICK = ticks(1.25F);
    private static final int BOW_SHOT_TICK = ticks(2.3F);
    public final AnimationState phantomSpearChargeAnimationState = new AnimationState();
    public final AnimationState phantomMaceCrushAnimationState = new AnimationState();
    public final AnimationState phantomBowSnipeAnimationState = new AnimationState();
    private int lifeTicks;
    private boolean damageDealt;
    private float summonerYRot;
    @Nullable
    private LivingEntity cachedTarget;
    @Nullable
    private LivingEntity summoner;
    private int lastTraceAttackState = Integer.MIN_VALUE;

    private static int ticks(float seconds) {
        return Math.round(seconds * TICKS_PER_SECOND);
    }

    public MaledictusPhantomEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.noPhysics = false;
        this.setInvulnerable(true);
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.ATTACK_DAMAGE, 0.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHANTOM_TYPE, TYPE_SPEAR);
    }

    public int getPhantomType() {
        return this.entityData.get(PHANTOM_TYPE);
    }

    public void setPhantomType(int type) {
        this.entityData.set(PHANTOM_TYPE, type);
        this.lifeTicks = switch (type) {
            case TYPE_MACE -> LIFE_MACE;
            case TYPE_BOW -> LIFE_BOW;
            case TYPE_TRACE -> LIFE_TRACE;
            default -> LIFE_SPEAR;
        };
    }

    public void setSummonerYRot(float yRot) {
        this.summonerYRot = yRot;
        this.setYRot(yRot);
        this.yBodyRot = yRot;
        this.yBodyRotO = yRot;
    }

    @Nullable
    public LivingEntity getSummoner() {
        return this.summoner;
    }

    public void setSummoner(@Nullable LivingEntity summoner) {
        this.summoner = summoner;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);
        this.cachedTarget = target;
    }

    @Nullable
    public LivingEntity getPhantomTarget() {
        if (this.cachedTarget != null && this.canPhantomHit(this.cachedTarget)) {
            return this.cachedTarget;
        }
        if (this.summoner instanceof Mob mobSummoner && mobSummoner.getTarget() != null && this.canPhantomHit(mobSummoner.getTarget())) {
            this.cachedTarget = mobSummoner.getTarget();
            return this.cachedTarget;
        }
        if (!this.level().isClientSide()) {
            List<LivingEntity> nearby = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(32.0D));
            double nearestDist = Double.MAX_VALUE;
            LivingEntity nearestTarget = null;
            for (LivingEntity t : nearby) {
                if (this.canPhantomHit(t)) {
                    double dist = this.distanceToSqr(t);
                    if (dist < nearestDist) {
                        nearestDist = dist;
                        nearestTarget = t;
                    }
                }
            }
            if (nearestTarget != null) {
                this.cachedTarget = nearestTarget;
                return this.cachedTarget;
            }
        }
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            int type = this.getPhantomType();
            boolean isTelegraphing = false;
            if (type == TYPE_SPEAR && this.tickCount < SPEAR_CHARGE_START) {
                isTelegraphing = true;
            } else if (type == TYPE_MACE && this.tickCount < MACE_HIT_TICK) {
                isTelegraphing = true;
            } else if (type == TYPE_BOW && this.tickCount < BOW_SHOT_TICK) {
                isTelegraphing = true;
            }
            if (isTelegraphing) {
                this.rotateTowardsTarget();
            }
        }
        if (this.getPhantomType() == TYPE_TRACE) {
            this.tickTraceAttack();
        } else {
            this.tickPhantomAttack();
        }
        if (this.level().isClientSide()) {
            int type = this.getPhantomType();
            this.phantomSpearChargeAnimationState.animateWhen(this.isAlive() && type == TYPE_SPEAR, this.tickCount);
            this.phantomMaceCrushAnimationState.animateWhen(this.isAlive() && type == TYPE_MACE, this.tickCount);
            this.phantomBowSnipeAnimationState.animateWhen(this.isAlive() && type == TYPE_BOW, this.tickCount);
        } else {
            this.lifeTicks--;
            if (this.lifeTicks <= 0) {
                this.discard();
            }
        }
    }

    private void rotateTowardsTarget() {
        LivingEntity target = this.getPhantomTarget();
        if (target != null && target.isAlive()) {
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            float targetYaw = (float) (Mth.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
            float rotationSpeed = 15.0F;
            float yawDifference = Mth.wrapDegrees(targetYaw - this.getYRot());
            if (yawDifference > rotationSpeed) {
                yawDifference = rotationSpeed;
            } else if (yawDifference < -rotationSpeed) {
                yawDifference = -rotationSpeed;
            }
            float newYaw = this.getYRot() + yawDifference;
            this.setYRot(newYaw);
            this.yBodyRot = newYaw;
            this.yHeadRot = newYaw;
            this.summonerYRot = newYaw;
        }
    }

    private void tickPhantomAttack() {
        int type = this.getPhantomType();
        switch (type) {
            case TYPE_SPEAR -> this.tickSpear();
            case TYPE_MACE -> this.tickMace();
            case TYPE_BOW -> this.tickBow();
        }
    }

    private void tickTraceAttack() {
        if (this.level().isClientSide()) {
            return;
        }
        if (!(this.summoner instanceof Maledictus_PrimeEntity boss) || !boss.isAlive()) {
            this.discard();
            return;
        }
        Maledictus_PrimeEntity.TraceFrame frame = boss.getTraceFrame(TRACE_DELAY_TICKS);
        this.setPos(frame.position().x, frame.position().y, frame.position().z);
        this.setYRot(frame.yaw());
        this.yBodyRot = frame.yaw();
        this.yHeadRot = frame.yaw();
        if (frame.attackState() != lastTraceAttackState) {
            lastTraceAttackState = frame.attackState();
            this.performTraceAttack(frame.attackState());
        }
        if (this.level() instanceof ServerLevel serverLevel && this.tickCount % 3 == 0) {
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY() + 1.0D, this.getZ(),
                    3, 0.35D, 0.5D, 0.35D, 0.02D);
        }
    }

    private void performTraceAttack(int attackState) {
        switch (attackState) {
            case Maledictus_PrimeEntity.ATTACK_JAB_1,
                    Maledictus_PrimeEntity.ATTACK_JAB_2,
                    Maledictus_PrimeEntity.ATTACK_JAB_3,
                    Maledictus_PrimeEntity.ATTACK_EX_JAB_1,
                    Maledictus_PrimeEntity.ATTACK_EX_JAB_2,
                    Maledictus_PrimeEntity.ATTACK_EX_JAB_3 ->
                    this.performPhantomForwardArc(0.65F, 3.8F, 110.0F, 0.25F, 0.15D, 0.05D);
            case Maledictus_PrimeEntity.ATTACK_CHARGE ->
                    this.performPhantomForwardArc(0.85F, 4.2F, 100.0F, 0.45F, 0.25D, 0.1D);
            case Maledictus_PrimeEntity.ATTACK_SHOCKWAVE_START,
                    Maledictus_PrimeEntity.ATTACK_SHOCKWAVE_END,
                    Maledictus_PrimeEntity.ATTACK_HEAD_BREAK,
                    Maledictus_PrimeEntity.ATTACK_ULTIMATE ->
                    this.performPhantomArea(0.75F, 0.8F, 4.5D, 2.0D, 0.1D, 0.25D);
            case Maledictus_PrimeEntity.ATTACK_GRAB_START,
                    Maledictus_PrimeEntity.ATTACK_GRAB_SUCCESS,
                    Maledictus_PrimeEntity.ATTACK_GRAB_SEND ->
                    this.performPhantomArea(0.55F, 0.45F, 2.8D, 1.5D, 0.1D, 0.2D);
            default -> {
            }
        }
    }

    private void tickSpear() {
        int elapsed = this.tickCount;
        if (elapsed == SPEAR_CHARGE_START) {
            this.playSound((SoundEvent) ModSounds.PHANTOM_SPEAR.get(), 1.0F, 1.0F);
            LivingEntity target = this.getPhantomTarget();
            if (target != null) {
                float angle = (float) (Mth.atan2(target.getZ() - this.getZ(), target.getX() - this.getX()) * (180D / Math.PI)) - 90.0F;
                this.setSummonerYRot(angle);
            }
            if (this.level().isClientSide()) {
                float rotYaw = (float) Math.toRadians(-this.getYRot());
                float pitch = (float) Math.toRadians(-this.getXRot());
                this.level().addParticle(new RingParticleOptions(rotYaw, pitch, 35, 86, 236, 204, 1.0F, 60.0F, false, EnumRingBehavior.GROW_THEN_SHRINK.ordinal()),
                        this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
        if (elapsed >= SPEAR_CHARGE_START) {
            float yaw = this.summonerYRot * ((float) Math.PI / 180F);
            this.setDeltaMovement(-Mth.sin(yaw) * 1.2D, 0.0D, Mth.cos(yaw) * 1.2D);
            this.hasImpulse = true;
            if (!this.level().isClientSide() && elapsed % 4 == 0) {
                this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.2F, 0.8F);
                this.performPhantomForwardArc(1.2F, 3.8F, 90.0F, 0.5F, 0.2D, 0.0D);
            }
            if (this.level().isClientSide()) {
                double x = this.getX();
                double y = this.getY() + (this.getBbHeight() / 2.0F);
                double z = this.getZ();
                float rotYaw = (float) Math.toRadians(-this.getYRot());
                float rotYaw2 = (float) Math.toRadians(-this.getYRot() + 180.0F);
                float pitch = (float) Math.toRadians(-this.getXRot());
                this.level().addParticle(new RingParticleOptions(rotYaw, pitch, 40, 86, 236, 204, 1.0F, 50.0F, false, EnumRingBehavior.GROW_THEN_SHRINK.ordinal()), x, y, z, 0.0D, 0.0D, 0.0D);
                this.level().addParticle(new RingParticleOptions(rotYaw2, pitch, 40, 86, 236, 204, 1.0F, 50.0F, false, EnumRingBehavior.GROW_THEN_SHRINK.ordinal()), x, y, z, 0.0D, 0.0D, 0.0D);
                if (elapsed % 2 == 0) {
                    this.level().addParticle((ParticleOptions) ModParticle.PHANTOM_WING_FLAME.get(),
                            this.getRandomX(0.8D), this.getY() + 0.5D, this.getRandomZ(0.8D),
                            0.0D, 0.05D, 0.0D);
                }
            }
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.5D, 1.0D, 0.5D));
        }
    }

    private void tickMace() {
        int elapsed = this.tickCount;
        if (elapsed < MACE_HIT_TICK) {
            if (this.level().isClientSide()) {
                double maxRadius = 4.8D;
                double progress = (double) elapsed / (double) MACE_HIT_TICK;
                double currentRadius = progress * maxRadius;
                int particleDensity = 4;
                for (int i = 0; i < particleDensity; i++) {
                    double angle = this.random.nextFloat() * 2.0F * Math.PI;
                    double px = this.getX() + Math.cos(angle) * currentRadius;
                    double py = this.getY() + 0.1D;
                    double pz = this.getZ() + Math.sin(angle) * currentRadius;
                    this.level().addParticle((ParticleOptions) ModParticle.PHANTOM_WING_FLAME.get(),
                            px, py, pz, 0.0D, 0.02D, 0.0D);
                    if (this.random.nextBoolean()) {
                        this.level().addParticle(ParticleTypes.SOUL,
                                px, py, pz, 0.0D, 0.02D, 0.0D);
                    }
                }
            }
        }
        if (elapsed == MACE_HIT_TICK) {
            this.playSound((SoundEvent) ModSounds.MALEDICTUS_MACE_SWING.get(), 1.2F, 0.8F);
            this.playSound(SoundEvents.GENERIC_EXPLODE, 1.5F, 0.6F);
            ScreenShake_Entity.ScreenShake(this.level(), this.position(), 35.0F, 0.3F, 0, 18);
            if (this.level().isClientSide()) {
                this.level().addParticle(new RingParticleOptions(0.0F, ((float) Math.PI / 2F), 40, 86, 236, 204, 1.0F, 50.0F, false, EnumRingBehavior.GROW_THEN_SHRINK.ordinal()),
                        this.getX(), this.getY() + 0.1D, this.getZ(), 0.0D, 0.0D, 0.0D);
                this.level().addParticle(new RingParticleOptions(0.0F, ((float) Math.PI / 2F), 50, 86, 236, 204, 0.5F, 75.0F, false, EnumRingBehavior.GROW_THEN_SHRINK.ordinal()),
                        this.getX(), this.getY() + 0.1D, this.getZ(), 0.0D, 0.0D, 0.0D);
            } else if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 0.2D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX(), this.getY() + 0.8D, this.getZ(), 12, 1.5D, 0.3D, 1.5D, 0.0D);
            }
            if (!this.level().isClientSide()) {
                this.performPhantomArea(1.8F, 1.2F, 4.8D, 3.0D, 0.2D, 0.45D);
                this.damageDealt = true;
            }
        }
    }

    private void tickBow() {
        int elapsed = this.tickCount;
        if (elapsed == BOW_SHOT_TICK - 15) {
            this.playSound((SoundEvent) ModSounds.MALEDICTUS_BOW_PULL.get(), 1.2F, 1.0F);
        }
        if (elapsed < BOW_SHOT_TICK && this.level() instanceof ServerLevel serverLevel) {
            if (elapsed % 3 == 0) {
                serverLevel.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY() + 1.8D, this.getZ(), 1, 0.1D, 0.1D, 0.1D, 0.0D);
            }
        }
        if (elapsed == BOW_SHOT_TICK) {
            this.playSound(SoundEvents.CROSSBOW_SHOOT, 1.2F, 0.8F);
            this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.2F);
            ScreenShake_Entity.ScreenShake(this.level(), this.position(), 10.0F, 0.1F, 0, 5);
            if (this.level().isClientSide()) {
                float rotYaw = (float) Math.toRadians(-this.getYRot());
                float pitch = (float) Math.toRadians(-this.getXRot());
                this.level().addParticle(new RingParticleOptions(rotYaw, pitch, 25, 86, 236, 204, 1.0F, 35.0F, false, EnumRingBehavior.GROW_THEN_SHRINK.ordinal()),
                        this.getX(), this.getY() + 1.8D, this.getZ(), 0.0D, 0.0D, 0.0D);
            }
            if (!this.level().isClientSide()) {
                this.fireBowProjectile();
                this.damageDealt = true;
            }
        }
    }

    private void fireBowProjectile() {
        LivingEntity target = this.getPhantomTarget();
        if (target == null || !target.isAlive()) return;
        Vec3 start = this.getEyePosition();
        Vec3 direction = target.getEyePosition().subtract(start).normalize();
        double speed = 1.8D;
        Vec3 pos = start;
        for (int i = 0; i < 60; i++) {
            if (target.isAlive()) {
                Vec3 toTarget = target.getEyePosition().subtract(pos).normalize();
                direction = direction.scale(0.85D).add(toTarget.scale(0.15D)).normalize();
            }
            Vec3 next = pos.add(direction.scale(speed));
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SOUL, next.x, next.y, next.z, 1, 0.05D, 0.05D, 0.05D, 0.0D);
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, next.x, next.y, next.z, 1, 0.05D, 0.05D, 0.05D, 0.0D);
            }
            AABB box = new AABB(pos, next).inflate(0.65D);
            List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, box);
            for (LivingEntity t : targets) {
                if (this.canPhantomHit(t)) {
                    float dmg = this.getPhantomBaseDamage() * 2.2F;
                    t.hurt(this.damageSources().mobAttack(this.summoner != null ? this.summoner : this), dmg);
                    t.setDeltaMovement(t.getDeltaMovement().add(direction.scale(0.6D)));
                    t.hasImpulse = true;
                    return;
                }
            }
            pos = next;
            if (pos.distanceToSqr(start) > 2500.0D) break;
        }
    }

    private float getPhantomBaseDamage() {
        if (this.summoner != null) {
            return (float) this.summoner.getAttributeValue(Attributes.ATTACK_DAMAGE);
        }
        return 18.0F;
    }

    private boolean canPhantomHit(LivingEntity t) {
        return t != this
                && t.isAlive()
                && !(t instanceof MaledictusPhantomEntity)
                && !(t instanceof Maledictus_PrimeEntity)
                && (!(t instanceof net.minecraft.world.entity.player.Player player)
                || (!player.isCreative() && !player.isSpectator()));
    }

    private void performPhantomForwardArc(float damageMult, float range, float arc, float knockback,
                                          double forwardPush, double verticalImpulse) {
        float yaw = this.yBodyRot * ((float) Math.PI / 180F);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(range, 2.0D, range));
        for (LivingEntity target : targets) {
            if (!this.canPhantomHit(target)) continue;
            float angleToTarget = (float) (Mth.atan2(target.getZ() - this.getZ(), target.getX() - this.getX())
                    * (180D / Math.PI)) - 90.0F;
            if (Mth.degreesDifferenceAbs(this.yBodyRot, angleToTarget) > arc / 2.0F) continue;
            if (this.distanceTo(target) > range + this.getBbWidth()) continue;
            float dmg = this.getPhantomBaseDamage() * damageMult;
            if (target.hurt(this.damageSources().mobAttack(this.summoner != null ? this.summoner : this), dmg)) {
                if (knockback > 0.0F) target.knockback(knockback, Math.sin(yaw), -Math.cos(yaw));
                if (forwardPush != 0.0D || verticalImpulse != 0.0D) {
                    Vec3 push = new Vec3(-Mth.sin(yaw) * forwardPush, verticalImpulse, Mth.cos(yaw) * forwardPush);
                    target.setDeltaMovement(target.getDeltaMovement().add(push));
                }
                target.hasImpulse = true;
            }
        }
    }

    private void performPhantomArea(float damageMult, float knockback, double xzRange, double yRange,
                                    double forwardPush, double verticalImpulse) {
        float yaw = this.yBodyRot * ((float) Math.PI / 180F);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(xzRange, yRange, xzRange));
        for (LivingEntity target : targets) {
            if (!this.canPhantomHit(target)) continue;
            if (this.distanceTo(target) > xzRange + this.getBbWidth()) continue;
            float dmg = this.getPhantomBaseDamage() * damageMult;
            if (target.hurt(this.damageSources().mobAttack(this.summoner != null ? this.summoner : this), dmg)) {
                if (knockback > 0.0F) target.knockback(knockback, Math.sin(yaw), -Math.cos(yaw));
                if (forwardPush != 0.0D || verticalImpulse != 0.0D) {
                    Vec3 push = new Vec3(-Mth.sin(yaw) * forwardPush, verticalImpulse, Mth.cos(yaw) * forwardPush);
                    target.setDeltaMovement(target.getDeltaMovement().add(push));
                }
                target.hasImpulse = true;
            }
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, net.minecraft.world.damagesource.DamageSource source) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected boolean isAffectedByFluids() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }
}
