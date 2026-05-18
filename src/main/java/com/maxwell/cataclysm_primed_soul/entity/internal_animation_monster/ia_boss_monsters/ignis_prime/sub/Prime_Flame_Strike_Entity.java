package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.sub;

import com.github.L_Ender.cataclysm.init.ModEffect;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.util.CMDamageTypes;
import com.github.L_Ender.cataclysm.util.CustomExplosion.IgnisExplosion;
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
    private static final float MAX_RADIUS = 32.0F;

    static {
        DATA_RADIUS = SynchedEntityData.defineId(Prime_Flame_Strike_Entity.class, EntityDataSerializers.FLOAT);
        DATA_WAITING = SynchedEntityData.defineId(Prime_Flame_Strike_Entity.class, EntityDataSerializers.BOOLEAN);
        DATA_SEE = SynchedEntityData.defineId(Prime_Flame_Strike_Entity.class, EntityDataSerializers.BOOLEAN);
        SOUL = SynchedEntityData.defineId(Prime_Flame_Strike_Entity.class, EntityDataSerializers.BOOLEAN);
        DAMAGE = SynchedEntityData.defineId(Prime_Flame_Strike_Entity.class, EntityDataSerializers.FLOAT);
        HPDAMAGE = SynchedEntityData.defineId(Prime_Flame_Strike_Entity.class, EntityDataSerializers.FLOAT);
    }

    private int duration;
    private int waitTime;
    private int warmupDelayTicks;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    public Prime_Flame_Strike_Entity(EntityType<? extends Entity> p_19704_, Level p_19705_) {
        super(p_19704_, p_19705_);
        this.duration = 600;
        this.noPhysics = true;
        this.setRadius(3.0F);
    }

    public Prime_Flame_Strike_Entity(Level level, double x, double y, double z, float p_i47276_8_, int duration, int wait, int delay, float radius, float damage, float Hpdamage, boolean soul, LivingEntity casterIn) {
        this(ModEntities.PRIME_FLAME_STRIKE.get(), level);
        this.setOwner(casterIn);
        this.setDuration(duration);
        this.waitTime = wait;
        this.warmupDelayTicks = delay;
        this.setRadius(radius);
        this.setDamage(damage);
        this.setHpDamage(Hpdamage);
        this.setSoul(soul);
        this.setYRot(p_i47276_8_ * (180F / (float) Math.PI));
        this.setPos(x, y, z);
    }

    protected void defineSynchedData() {
        this.getEntityData().define(DATA_RADIUS, 0.5F);
        this.getEntityData().define(DAMAGE, 0.0F);
        this.getEntityData().define(HPDAMAGE, 0.0F);
        this.getEntityData().define(DATA_WAITING, true);
        this.getEntityData().define(DATA_SEE, false);
        this.getEntityData().define(SOUL, false);
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
        return this.duration;
    }

    public void setDuration(int p_19735_) {
        this.duration = p_19735_;
    }

    public void tick() {
        super.tick();
        boolean flag = this.isWaiting();
        float f = this.getRadius();
        if (this.level().isClientSide) {
            if (flag && this.random.nextBoolean()) {
                return;
            }
            ParticleOptions particleoptions = this.isSoul() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME;
            float f1 = flag ? 0.2F : f;
            double spread = (Math.PI * 2D);
            int arcLen = Mth.ceil((double) this.getRadius() * spread);
            if (!flag) {
                if (this.tickCount % 2 == 0) {
                    for (int j = 0; j < arcLen; ++j) {
                        float f2 = this.random.nextFloat() * ((float) Math.PI * 2F);
                        double d0 = this.getX() + (double) (Mth.cos(f2) * f1) * 0.9;
                        double d2 = this.getY();
                        double d4 = this.getZ() + (double) (Mth.sin(f2) * f1) * 0.9;
                        this.level().addParticle(particleoptions, d0, d2, d4, this.random.nextGaussian() * 0.07, (double) 0.125F * (double) this.getRadius() + 0.4, this.random.nextGaussian() * 0.07);
                    }
                }
                if (this.random.nextInt(24) == 0) {
                    this.level().playLocalSound(this.getX() + (double) 0.5F, this.getY() + (double) 0.5F, this.getZ() + (double) 0.5F, SoundEvents.BLAZE_BURN, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
                }
            }
        } else {
            if (this.tickCount >= this.waitTime + this.duration + this.warmupDelayTicks) {
                if (this.getRadius() > 0.0F) {
                    this.setRadius(this.getRadius() - 0.1F);
                } else {
                    if (!this.isSoul()) {
                        int explosionradius = this.owner instanceof Player ? 1 : 2;
                        IgnisExplosion explosion = new IgnisExplosion(this.level(), this.owner, (DamageSource) null, (ExplosionDamageCalculator) null, this.getX(), this.getY(), this.getZ(), (float) explosionradius, false, BlockInteraction.KEEP);
                        explosion.explode();
                        explosion.finalizeExplosion(0, (double) 0.0F);
                    }
                    this.level().broadcastEntityEvent(this, (byte) 4);
                    this.discard();
                }
            }
            if (this.tickCount >= this.warmupDelayTicks) {
                this.setSee(true);
            }
            boolean flag1 = this.tickCount < this.waitTime + this.warmupDelayTicks;
            if (flag != flag1) {
                this.setWaiting(flag1);
            }
            if (flag1) {
                return;
            }
        }
        if (!flag && this.tickCount % 5 == 0) {
            for (LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
                this.damage(livingentity);
            }
        }

    }

    protected void damage(LivingEntity Hitentity) {
        LivingEntity caster = this.getOwner();
        if (Hitentity.isAlive() && !Hitentity.isInvulnerable() && Hitentity != caster && this.tickCount % 2 == 0) {
            if (caster == null) {
                boolean flag = Hitentity.hurt(this.damageSources().magic(), this.getDamage() + Hitentity.getMaxHealth() * 0.01F * this.getHpDamage());
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
                }
            } else if (!caster.isAlliedTo(Hitentity) && !Hitentity.isAlliedTo(caster)) {
                boolean flag = Hitentity.hurt(CMDamageTypes.causeFlameStrikeDamage(this, caster), this.getDamage() + Hitentity.getMaxHealth() * 0.01F * this.getHpDamage());
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
                }
            }
        }

    }

    public int getWaitTime() {
        return this.waitTime;
    }

    public void setWaitTime(int p_19741_) {
        this.waitTime = p_19741_;
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
        this.duration = p_19727_.getInt("Duration");
        this.waitTime = p_19727_.getInt("WaitTime");
        this.warmupDelayTicks = p_19727_.getInt("Delay");
        this.setRadius(p_19727_.getFloat("Radius"));
        if (p_19727_.hasUUID("Owner")) {
            this.ownerUUID = p_19727_.getUUID("Owner");
        }
        this.setSoul(p_19727_.getBoolean("is_soul"));
        this.setDamage(p_19727_.getFloat("damage"));
        this.setHpDamage(p_19727_.getFloat("Hpdamage"));
    }

    protected void addAdditionalSaveData(CompoundTag p_19737_) {
        p_19737_.putInt("Age", this.tickCount);
        p_19737_.putInt("Duration", this.duration);
        p_19737_.putInt("WaitTime", this.waitTime);
        p_19737_.putInt("Delay", this.warmupDelayTicks);
        p_19737_.putFloat("Radius", this.getRadius());
        if (this.ownerUUID != null) {
            p_19737_.putUUID("Owner", this.ownerUUID);
        }
        p_19737_.putBoolean("is_soul", this.isSoul());
        p_19737_.putFloat("damage", this.getDamage());
        p_19737_.putFloat("Hpdamage", this.getHpDamage());
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
            this.level().addParticle((ParticleOptions) ModParticle.FLARE_EXPLODE.get(), this.getX(), this.getY() + (double) 0.05F, this.getZ(), 0.1, (double) 0.0F, (double) 0.0F);
            for (int i = 0; i < 5; ++i) {
                double particleX = this.getX() + (double) ((this.random.nextFloat() - 0.5F) * 4.0F);
                double particleY = this.getY() + (double) this.random.nextFloat();
                double particleZ = this.getZ() + (double) ((this.random.nextFloat() - 0.5F) * 4.0F);
                this.level().addParticle((ParticleOptions) ModParticle.IGNIS_EXPLODE.get(), particleX, particleY, particleZ, 1.4, (double) 0.0F, (double) 0.0F);
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
