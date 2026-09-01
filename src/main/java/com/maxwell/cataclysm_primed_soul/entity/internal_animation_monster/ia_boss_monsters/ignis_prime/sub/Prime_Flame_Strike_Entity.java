package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.sub;

import com.github.L_Ender.cataclysm.init.ModEffect;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.util.CMDamageTypes;
import com.github.L_Ender.cataclysm.util.CustomExplosion.IgnisExplosion;
import com.maxwell.cataclysm_primed_soul.entity.EntityDamageHelper;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.HealBlockManager;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.Ignis_PrimeEntity;
import com.maxwell.cataclysm_primed_soul.init.ModEntities;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.UUID;

public class Prime_Flame_Strike_Entity extends Entity {
    private static final EntityDataAccessor<Float> DATA_RADIUS;
    private static final EntityDataAccessor<Boolean> DATA_WAITING;
    private static final EntityDataAccessor<Boolean> DATA_SEE;
    private static final EntityDataAccessor<Boolean> SOUL;
    private static final EntityDataAccessor<Float> DAMAGE;
    private static final EntityDataAccessor<Float> HPDAMAGE;
    private static final EntityDataAccessor<Integer> DURATION;
    private static final EntityDataAccessor<Integer> WAIT_TIME;
    private static final EntityDataAccessor<Integer> WARMUP_DELAY;
    private static final EntityDataAccessor<Integer> EXPLOSION_DELAY;
    private static final EntityDataAccessor<Float> EXPLOSION_RADIUS;
    private static final EntityDataAccessor<Boolean> WHITE;
    private static final float MAX_RADIUS = 32.0F;

    static {
        DATA_RADIUS = SynchedEntityData.defineId(Prime_Flame_Strike_Entity.class, EntityDataSerializers.FLOAT);
        DATA_WAITING = SynchedEntityData.defineId(Prime_Flame_Strike_Entity.class, EntityDataSerializers.BOOLEAN);
        DATA_SEE = SynchedEntityData.defineId(Prime_Flame_Strike_Entity.class, EntityDataSerializers.BOOLEAN);
        SOUL = SynchedEntityData.defineId(Prime_Flame_Strike_Entity.class, EntityDataSerializers.BOOLEAN);
        DAMAGE = SynchedEntityData.defineId(Prime_Flame_Strike_Entity.class, EntityDataSerializers.FLOAT);
        HPDAMAGE = SynchedEntityData.defineId(Prime_Flame_Strike_Entity.class, EntityDataSerializers.FLOAT);
        DURATION = SynchedEntityData.defineId(Prime_Flame_Strike_Entity.class, EntityDataSerializers.INT);
        WAIT_TIME = SynchedEntityData.defineId(Prime_Flame_Strike_Entity.class, EntityDataSerializers.INT);
        WARMUP_DELAY = SynchedEntityData.defineId(Prime_Flame_Strike_Entity.class, EntityDataSerializers.INT);
        EXPLOSION_DELAY = SynchedEntityData.defineId(Prime_Flame_Strike_Entity.class, EntityDataSerializers.INT);
        EXPLOSION_RADIUS = SynchedEntityData.defineId(Prime_Flame_Strike_Entity.class, EntityDataSerializers.FLOAT);
        WHITE = SynchedEntityData.defineId(Prime_Flame_Strike_Entity.class, EntityDataSerializers.BOOLEAN);
    }

    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    public Prime_Flame_Strike_Entity(EntityType<? extends Entity> p_19704_, Level p_19705_) {
        super(p_19704_, p_19705_);
        this.noPhysics = true;
        this.setRadius(3.0F);
        this.setDuration(600);
    }

    public Prime_Flame_Strike_Entity(Level level, double x, double y, double z, float rotation, int duration, int wait, int delay, float radius, float damage, float Hpdamage, boolean soul, LivingEntity casterIn) {
        this(ModEntities.PRIME_FLAME_STRIKE.get(), level);
        this.setOwner(casterIn);
        this.setDuration(duration);
        this.setWaitTime(wait);
        this.setWarmupDelay(delay);
        this.setRadius(radius);
        this.setDamage(damage);
        this.setHpDamage(Hpdamage);
        this.setSoul(soul);
        this.setYRot(rotation * (180F / (float) Math.PI));
        this.setPos(x, y, z);
        this.setExplosionRadius(casterIn instanceof Player ? 1.0F : 2.0F);
    }

    protected void defineSynchedData() {
        this.getEntityData().define(DATA_RADIUS, 0.5F);
        this.getEntityData().define(DAMAGE, 0.0F);
        this.getEntityData().define(HPDAMAGE, 0.0F);
        this.getEntityData().define(DATA_WAITING, true);
        this.getEntityData().define(DATA_SEE, false);
        this.getEntityData().define(SOUL, false);
        this.getEntityData().define(DURATION, 600);
        this.getEntityData().define(WAIT_TIME, 0);
        this.getEntityData().define(WARMUP_DELAY, 0);
        this.getEntityData().define(EXPLOSION_DELAY, 0);
        this.getEntityData().define(EXPLOSION_RADIUS, 2.0F);
        this.getEntityData().define(WHITE, false);
    }

    public float getDamage() {
        return (Float) this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public float getHpDamage() {
        return (Float) this.entityData.get(HPDAMAGE);
    }

    public void setHpDamage(float damage) {
        this.entityData.set(HPDAMAGE, damage);
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public float getRadius() {
        return (Float) this.getEntityData().get(DATA_RADIUS);
    }

    public void setRadius(float p_19713_) {
        if (!this.level().isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Mth.clamp(p_19713_, 0.0F, 32.0F));
        }
    }

    public boolean isWaiting() {
        return (Boolean) this.getEntityData().get(DATA_WAITING);
    }

    protected void setWaiting(boolean p_19731_) {
        this.getEntityData().set(DATA_WAITING, p_19731_);
    }

    public boolean isSee() {
        return (Boolean) this.getEntityData().get(DATA_SEE);
    }

    protected void setSee(boolean p_19731_) {
        this.getEntityData().set(DATA_SEE, p_19731_);
    }

    public boolean isSoul() {
        return (Boolean) this.getEntityData().get(SOUL);
    }

    public void setSoul(boolean Soul) {
        this.getEntityData().set(SOUL, Soul);
    }

    public int getDuration() {
        return this.entityData.get(DURATION);
    }

    public void setDuration(int duration) {
        this.entityData.set(DURATION, duration);
    }

    public int getWaitTime() {
        return this.entityData.get(WAIT_TIME);
    }

    public void setWaitTime(int waitTime) {
        this.entityData.set(WAIT_TIME, waitTime);
    }

    public int getWarmupDelay() {
        return this.entityData.get(WARMUP_DELAY);
    }

    public void setWarmupDelay(int warmupDelay) {
        this.entityData.set(WARMUP_DELAY, warmupDelay);
    }

    public int getExplosionDelay() {
        return this.entityData.get(EXPLOSION_DELAY);
    }

    public void setExplosionDelay(int explosionDelay) {
        this.entityData.set(EXPLOSION_DELAY, explosionDelay);
    }

    public float getExplosionRadius() {
        return this.entityData.get(EXPLOSION_RADIUS);
    }

    public void setExplosionRadius(float explosionRadius) {
        this.entityData.set(EXPLOSION_RADIUS, explosionRadius);
    }

    public boolean isWhite() {
        return this.entityData.get(WHITE);
    }

    public void setWhite(boolean white) {
        this.entityData.set(WHITE, white);
    }

    public void tick() {
        super.tick();
        boolean flag = this.isWaiting();
        float f = this.getRadius();
        int warmup = this.getWarmupDelay();
        int wait = this.getWaitTime();
        int dur = this.getDuration();
        int expDelay = this.getExplosionDelay();
        if (this.level().isClientSide) {
            if (flag && this.random.nextBoolean()) {
                return;
            }
            ParticleOptions particleoptions;
            if (this.isWhite() || !this.isSoul()) {
                particleoptions = ParticleTypes.END_ROD;
            } else {
                particleoptions = ParticleTypes.SOUL_FIRE_FLAME;
            }
            if (!flag) {
                if (this.tickCount % 2 == 0) {
                    int spawnCount = Mth.ceil(f * 4.0F);
                    if (this.isWhite() || !this.isSoul()) {
                        spawnCount *= 2;
                    }
                    for (int j = 0; j < spawnCount; ++j) {
                        double d0 = this.getX() + (this.random.nextDouble() - 0.5D) * 0.15D * (double) f;
                        double d2 = this.getY() + 0.05D;
                        double d4 = this.getZ() + (this.random.nextDouble() - 0.5D) * 0.15D * (double) f;
                        double vx = (this.random.nextDouble() - 0.5D) * 0.08D * (double) f;
                        double vy = (0.25D + this.random.nextDouble() * 0.35D) * (double) f * 0.6D;
                        double vz = (this.random.nextDouble() - 0.5D) * 0.08D * (double) f;
                        this.level().addParticle(particleoptions, d0, d2, d4, vx, vy, vz);
                        if (this.isWhite() || !this.isSoul()) {
                            double cvx = (this.random.nextDouble() - 0.5D) * 0.12D * (double) f;
                            double cvy = (0.2D + this.random.nextDouble() * 0.25D) * (double) f * 0.6D;
                            double cvz = (this.random.nextDouble() - 0.5D) * 0.12D * (double) f;
                            if (this.random.nextBoolean()) {
                                this.level().addParticle(ParticleTypes.CLOUD, d0, d2, d4, cvx, cvy, cvz);
                            } else {
                                this.level().addParticle(ParticleTypes.POOF, d0, d2, d4, cvx * 0.5, cvy * 0.8, cvz * 0.5);
                            }
                        } else {
                            if (this.random.nextInt(3) == 0) {
                                this.level().addParticle(ParticleTypes.LARGE_SMOKE, d0, d2, d4, vx * 0.5, vy * 0.8, vz * 0.5);
                            }
                        }
                    }
                }
                if (this.random.nextInt(24) == 0) {
                    this.level().playLocalSound(this.getX() + (double) 0.5F, this.getY() + (double) 0.5F, this.getZ() + (double) 0.5F, SoundEvents.BLAZE_BURN, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
                }
            }
        } else {
            if (this.tickCount >= warmup) {
                this.setSee(true);
            }
            boolean flag1 = this.tickCount < wait + warmup;
            if (flag != flag1) {
                this.setWaiting(flag1);
            }
            int endFlameTick = warmup + wait + dur;
            if (this.tickCount >= endFlameTick) {
                if (expDelay <= 0) {
                    if (this.getRadius() > 0.0F) {
                        this.setRadius(this.getRadius() - 0.1F);
                    } else {
                        this.triggerExplosion();
                    }
                } else {
                    int ticksAfterFlame = this.tickCount - endFlameTick;
                    if (ticksAfterFlame >= expDelay) {
                        this.triggerExplosion();
                    } else {
                        this.setRadius(Math.max(0.0F, this.getRadius() - (this.getRadius() / (float) (expDelay - ticksAfterFlame))));
                    }
                }
            }
            if (flag1) {
                return;
            }
        }
        if (!flag && this.tickCount % 5 == 0) {
            for (LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(EntityDamageHelper.expandRange(0.15D)))) {
                this.damage(livingentity);
            }
        }
    }

    protected void triggerExplosion() {
        if (!this.isSoul() && !this.isWhite()) {
            IgnisExplosion explosion = new IgnisExplosion(this.level(), this.getOwner(), (DamageSource) null, (ExplosionDamageCalculator) null, this.getX(), this.getY(), this.getZ(), this.getExplosionRadius(), false, BlockInteraction.KEEP);
            explosion.explode();
            explosion.finalizeExplosion(0, (double) 0.0F);
        }
        this.level().broadcastEntityEvent(this, (byte) 4);
        this.discard();
    }

    protected void damage(LivingEntity Hitentity) {
        LivingEntity caster = this.getOwner();
        if (Hitentity instanceof Ignis_PrimeEntity) {
            return;
        }
        if (Hitentity.isAlive() && !Hitentity.isInvulnerable() && Hitentity != caster && this.tickCount % 2 == 0) {
            if (caster == null) {
                boolean flag = EntityDamageHelper.hurtIgnoringInvulnerability(Hitentity, this.damageSources().magic(), this.getDamage() + Hitentity.getMaxHealth() * 0.01F * this.getHpDamage());
                if (flag) {
                    MobEffectInstance effectinstance1 = Hitentity.getEffect((MobEffect) ModEffect.EFFECTBLAZING_BRAND.get());
                    int i = 1;
                    if (effectinstance1 != null) {
                        i += effectinstance1.getAmplifier();
                        Hitentity.removeEffectNoUpdate((MobEffect) ModEffect.EFFECTBLAZING_BRAND.get());
                    } else {
                        --i;
                    }
                    i = Mth.clamp(i, 0, 4);
                    MobEffectInstance effectinstance = new MobEffectInstance((MobEffect) ModEffect.EFFECTBLAZING_BRAND.get(), 200, i, false, false, true);
                    Hitentity.addEffect(effectinstance);
                    HealBlockManager.applyHealBlock(Hitentity, 60);
                }
            } else if (!caster.isAlliedTo(Hitentity) && !Hitentity.isAlliedTo(caster)) {
                boolean flag = EntityDamageHelper.hurtIgnoringInvulnerability(Hitentity, CMDamageTypes.causeFlameStrikeDamage(this, caster), this.getDamage() + Hitentity.getMaxHealth() * 0.01F * this.getHpDamage());
                if (flag) {
                    MobEffectInstance effectinstance1 = Hitentity.getEffect((MobEffect) ModEffect.EFFECTBLAZING_BRAND.get());
                    int i = 1;
                    if (effectinstance1 != null) {
                        i += effectinstance1.getAmplifier();
                        Hitentity.removeEffectNoUpdate((MobEffect) ModEffect.EFFECTBLAZING_BRAND.get());
                    } else {
                        --i;
                    }
                    i = Mth.clamp(i, 0, 4);
                    MobEffectInstance effectinstance = new MobEffectInstance((MobEffect) ModEffect.EFFECTBLAZING_BRAND.get(), 200, i, false, false, true);
                    Hitentity.addEffect(effectinstance);
                    HealBlockManager.applyHealBlock(Hitentity, 60);
                }
            }
        }
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel) this.level()).getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity) entity;
            }
        }
        return this.owner;
    }

    public void setOwner(@Nullable LivingEntity p_19719_) {
        this.owner = p_19719_;
        this.ownerUUID = p_19719_ == null ? null : p_19719_.getUUID();
    }

    protected void readAdditionalSaveData(CompoundTag p_19727_) {
        this.tickCount = p_19727_.getInt("Age");
        this.setRadius(p_19727_.getFloat("Radius"));
        if (p_19727_.hasUUID("Owner")) {
            this.ownerUUID = p_19727_.getUUID("Owner");
        }
        this.setSoul(p_19727_.getBoolean("is_soul"));
        this.setDamage(p_19727_.getFloat("damage"));
        this.setHpDamage(p_19727_.getFloat("Hpdamage"));
        this.setDuration(p_19727_.getInt("Duration"));
        this.setWaitTime(p_19727_.getInt("WaitTime"));
        this.setWarmupDelay(p_19727_.getInt("Delay"));
        this.setExplosionDelay(p_19727_.getInt("ExplosionDelay"));
        this.setExplosionRadius(p_19727_.getFloat("ExplosionRadius"));
        this.setWhite(p_19727_.getBoolean("IsWhite"));
    }

    protected void addAdditionalSaveData(CompoundTag p_19737_) {
        p_19737_.putInt("Age", this.tickCount);
        p_19737_.putFloat("Radius", this.getRadius());
        if (this.ownerUUID != null) {
            p_19737_.putUUID("Owner", this.ownerUUID);
        }
        p_19737_.putBoolean("is_soul", this.isSoul());
        p_19737_.putFloat("damage", this.getDamage());
        p_19737_.putFloat("Hpdamage", this.getHpDamage());
        p_19737_.putInt("Duration", this.getDuration());
        p_19737_.putInt("WaitTime", this.getWaitTime());
        p_19737_.putInt("Delay", this.getWarmupDelay());
        p_19737_.putInt("ExplosionDelay", this.getExplosionDelay());
        p_19737_.putFloat("ExplosionRadius", this.getExplosionRadius());
        p_19737_.putBoolean("IsWhite", this.isWhite());
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_19729_) {
        if (DATA_RADIUS.equals(p_19729_)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(p_19729_);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == 4) {
            if (this.isWhite() || !this.isSoul()) {
                this.level().addParticle(ParticleTypes.FLASH, this.getX(), this.getY() + 0.05D, this.getZ(), 0.0, 0.0, 0.0);
                this.level().addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 0.05D, this.getZ(), 0.0, 0.0, 0.0);
                for (int i = 0; i < 20; ++i) {
                    double px = this.getX() + (this.random.nextDouble() - 0.5D) * this.getRadius() * 2.0D;
                    double py = this.getY() + this.random.nextDouble() * 2.0D;
                    double pz = this.getZ() + (this.random.nextDouble() - 0.5D) * this.getRadius() * 2.0D;
                    this.level().addParticle(ParticleTypes.CLOUD, px, py, pz, this.random.nextGaussian() * 0.1, 0.1 + this.random.nextDouble() * 0.2, this.random.nextGaussian() * 0.1);
                    this.level().addParticle(ParticleTypes.END_ROD, px, py, pz, this.random.nextGaussian() * 0.05, 0.2, this.random.nextGaussian() * 0.05);
                }
            } else {
                this.level().addParticle((ParticleOptions) ModParticle.FLARE_EXPLODE.get(), this.getX(), this.getY() + (double) 0.05F, this.getZ(), 0.1, (double) 0.0F, (double) 0.0F);
                for (int i = 0; i < 5; ++i) {
                    double particleX = this.getX() + (double) ((this.random.nextFloat() - 0.5F) * 4.0F);
                    double particleY = this.getY() + (double) this.random.nextFloat();
                    double particleZ = this.getZ() + (double) ((this.random.nextFloat() - 0.5F) * 4.0F);
                    this.level().addParticle((ParticleOptions) ModParticle.IGNIS_EXPLODE.get(), particleX, particleY, particleZ, 1.4, (double) 0.0F, (double) 0.0F);
                }
            }
        }
    }

    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    public EntityDimensions getDimensions(Pose p_19721_) {
        return EntityDimensions.scalable(this.getRadius() * 1.8F, this.getRadius() * 3.0F);
    }
}
