package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.sub;

import com.github.L_Ender.cataclysm.config.CMCommonConfig;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.Ignis_Entity;
import com.github.L_Ender.cataclysm.entity.effect.Cm_Falling_Block_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Ignis_Fireball_Entity;
import com.github.L_Ender.cataclysm.init.ModEffect;
import com.github.L_Ender.cataclysm.util.CustomExplosion.IgnisExplosion;
import com.maxwell.cataclysm_primed_soul.entity.EntityDamageHelper;
import com.maxwell.cataclysm_primed_soul.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;

public class Prime_Fireball_Entity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Integer> BOUNCES;
    private static final EntityDataAccessor<Boolean> FIRED;

    static {
        BOUNCES = SynchedEntityData.defineId(Prime_Fireball_Entity.class, EntityDataSerializers.INT);
        FIRED = SynchedEntityData.defineId(Prime_Fireball_Entity.class, EntityDataSerializers.BOOLEAN);
    }

    private int timer;
    private Vec3[] trailPositions;
    private int trailPointer;

    public Prime_Fireball_Entity(EntityType<Prime_Fireball_Entity> type, Level level) {
        super(type, level);
        this.trailPositions = new Vec3[64];
        this.trailPointer = -1;
    }

    public Prime_Fireball_Entity(Level level, LivingEntity owner, double xPower, double yPower, double zPower) {
        super(ModEntities.PRIME_FIREBALL.get(), owner, xPower, yPower, zPower, level);
        this.trailPositions = new Vec3[64];
        this.trailPointer = -1;
    }

    public Prime_Fireball_Entity(Level level, LivingEntity owner) {
        this(ModEntities.PRIME_FIREBALL.get(), level);
        this.setOwner(owner);
    }

    public boolean isOnFire() {
        return false;
    }

    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            --this.timer;
            if (this.timer <= 0 && !this.getFired()) {
                this.setFired(true);
            }
            if (this.getFired() && this.xPower == 0 && this.yPower == 0 && this.zPower == 0) {
                Entity owner = this.getOwner();
                LivingEntity target = null;
                if (owner instanceof Mob mob) {
                    target = mob.getTarget();
                }
                double dx, dy, dz;
                if (target != null) {
                    dx = target.getX() - this.getX();
                    dy = target.getY() + (double) (target.getBbHeight() * 0.5F) - this.getY();
                    dz = target.getZ() - this.getZ();
                } else {
                    Vec3 look = this.getLookAngle();
                    dx = look.x;
                    dy = look.y;
                    dz = look.z;
                }
                float speed = 0.15F;
                double d = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (d > 0) {
                    this.xPower = (dx / d) * speed;
                    this.yPower = (dy / d) * speed;
                    this.zPower = (dz / d) * speed;
                }
            }
        }
        if (this.timer < -160) {
            this.discard();
        }
        Vec3 trailAt = this.position().add(0.0D, this.getBbHeight() / 2.0D, 0.0D);
        if (this.trailPointer == -1) {
            for (int i = 0; i < this.trailPositions.length; ++i) {
                this.trailPositions[i] = trailAt;
            }
            this.trailPointer = 0;
        }
        if (++this.trailPointer >= this.trailPositions.length) {
            this.trailPointer = 0;
        }
        this.trailPositions[this.trailPointer] = trailAt;
    }

    public void setUp(int delay) {
        this.setFired(false);
        this.timer = delay;
    }

    protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        Entity entity = hit.getEntity();
        Entity shooter = this.getOwner();
        if (entity == shooter || entity instanceof com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.Ignis_PrimeEntity) {
            return;
        }
        if (!this.level().isClientSide && !(entity instanceof Ignis_Fireball_Entity) && !(entity instanceof com.github.L_Ender.cataclysm.entity.projectile.Ignis_Abyss_Fireball_Entity) && !(entity instanceof Cm_Falling_Block_Entity) && (!(entity instanceof Ignis_Entity) || !(shooter instanceof Ignis_Entity)) && this.getFired()) {
            boolean flag;
            if (shooter instanceof LivingEntity) {
                LivingEntity owner = (LivingEntity) shooter;
                if (entity instanceof LivingEntity) {
                    flag = EntityDamageHelper.hurtIgnoringInvulnerability((LivingEntity) entity, this.damageSources().mobProjectile(this, owner), 7.0F + ((LivingEntity) entity).getMaxHealth() * 0.10F);
                } else {
                    flag = EntityDamageHelper.hurtIgnoringInvulnerability((LivingEntity) entity, this.damageSources().mobProjectile(this, owner), 7.0F);
                }
                if (flag) {
                    this.doEnchantDamageEffects(owner, entity);
                    if (owner instanceof Ignis_Entity) {
                        owner.heal(5.0F * (float) CMCommonConfig.Ignis.healthMultiplier);
                    } else {
                        owner.heal(5.0F);
                    }
                }
            } else {
                flag = EntityDamageHelper.hurtIgnoringInvulnerability((LivingEntity) entity, this.damageSources().magic(), 5.0F);
            }
            IgnisExplosion explosion = new IgnisExplosion(this.level(), this, (DamageSource) null, (ExplosionDamageCalculator) null, this.getX(), this.getY(), this.getZ(), 2.0F, true, BlockInteraction.KEEP);
            explosion.explode();
            explosion.finalizeExplosion(2, (double) 0.5F);
            this.discard();
            if (flag && entity instanceof LivingEntity) {
                MobEffectInstance effectinstance1 = ((LivingEntity) entity).getEffect((MobEffect) ModEffect.EFFECTBLAZING_BRAND.get());
                int i = 2;
                if (effectinstance1 != null) {
                    i += effectinstance1.getAmplifier();
                    ((LivingEntity) entity).removeEffectNoUpdate((MobEffect) ModEffect.EFFECTBLAZING_BRAND.get());
                } else {
                    --i;
                }
                i = Mth.clamp(i, 0, 4);
                MobEffectInstance effectinstance = new MobEffectInstance((MobEffect) ModEffect.EFFECTBLAZING_BRAND.get(), 200, i, false, false, true);
                ((LivingEntity) entity).addEffect(effectinstance);
            }
        }

    }

    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        BlockState blockstate = this.level().getBlockState(result.getBlockPos());
        if (!blockstate.getCollisionShape(this.level(), result.getBlockPos()).isEmpty() && this.getFired()) {
            blockstate.onProjectileHit(this.level(), blockstate, result, this);
            if (!this.level().isClientSide) {
                IgnisExplosion explosion = new IgnisExplosion(this.level(), this, (DamageSource) null, (ExplosionDamageCalculator) null, this.getX(), this.getY(), this.getZ(), 2.0F, true, BlockInteraction.KEEP);
                explosion.explode();
                explosion.finalizeExplosion(2, (double) 0.35F);
                this.discard();
            }
        }

    }

    protected void onHit(HitResult ray) {
        HitResult.Type hitresult$type = ray.getType();
        if (hitresult$type == Type.ENTITY) {
            this.onHitEntity((EntityHitResult) ray);
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, ray.getLocation(), Context.of(this, (BlockState) null));
        } else if (hitresult$type == Type.BLOCK) {
            BlockHitResult blockhitresult = (BlockHitResult) ray;
            this.onHitBlock(blockhitresult);
            BlockPos blockpos = blockhitresult.getBlockPos();
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockpos, Context.of(this, this.level().getBlockState(blockpos)));
        }

    }

    public Vec3 getTrailPosition(int pointer, float partialTick) {
        if (this.isRemoved()) {
            partialTick = 1.0F;
        }
        int i = this.trailPointer - pointer & 63;
        int j = this.trailPointer - pointer - 1 & 63;
        Vec3 d0 = this.trailPositions[j];
        Vec3 d1 = this.trailPositions[i].subtract(d0);
        return d0.add(d1.scale((double) partialTick));
    }

    public boolean hasTrail() {
        return this.trailPointer != -1;
    }

    protected void defineSynchedData() {
        this.entityData.define(BOUNCES, 0);
        this.entityData.define(FIRED, false);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("totalBounces", this.getTotalBounces());
        compound.putInt("timer", this.timer);
        compound.putBoolean("fired", this.getFired());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setTotalBounces(compound.getInt("totalBounces"));
        this.timer = compound.getInt("timer");
        this.setFired(compound.getBoolean("fired"));
    }

    public int getTotalBounces() {
        return (Integer) this.entityData.get(BOUNCES);
    }

    public void setTotalBounces(int bounces) {
        this.entityData.set(BOUNCES, bounces);
    }

    public boolean getFired() {
        return (Boolean) this.entityData.get(FIRED);
    }

    public void setFired(boolean fired) {
        this.entityData.set(FIRED, fired);
    }

    public boolean hurt(DamageSource p_36839_, float p_36840_) {
        return false;
    }
}
