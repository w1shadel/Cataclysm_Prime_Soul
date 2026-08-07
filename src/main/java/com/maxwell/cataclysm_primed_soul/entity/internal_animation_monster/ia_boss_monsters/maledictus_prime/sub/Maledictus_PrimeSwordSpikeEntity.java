package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.sub;

import com.github.L_Ender.cataclysm.init.ModParticle;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.MaledictusPhantomEntity;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.Maledictus_PrimeEntity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class Maledictus_PrimeSwordSpikeEntity extends Entity {
    private int lifeTicks;
    private int warmupDelay;
    private float damage;
    @Nullable
    private LivingEntity summoner;
    @Nullable
    private UUID summonerUUID;

    public Maledictus_PrimeSwordSpikeEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public Maledictus_PrimeSwordSpikeEntity(Level level, double x, double y, double z, float yaw, int warmup, float damage, LivingEntity summoner) {
        this(com.maxwell.cataclysm_primed_soul.init.ModEntities.MALEDICTUS_PRIME_SWORD_SPIKE.get(), level);
        this.setPos(x, y, z);
        this.setYRot(yaw);
        this.warmupDelay = warmup;
        this.damage = damage;
        this.setSummoner(summoner);
    }

    @Nullable
    public LivingEntity getSummoner() {
        if (this.summoner == null && this.summonerUUID != null && this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(this.summonerUUID);
            if (entity instanceof LivingEntity living) {
                this.summoner = living;
            }
        }
        return this.summoner;
    }

    public void setSummoner(@Nullable LivingEntity entity) {
        this.summoner = entity;
        if (entity != null) {
            this.summonerUUID = entity.getUUID();
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.hasUUID("SummonerUUID")) {
            this.summonerUUID = pCompound.getUUID("SummonerUUID");
        }
        this.lifeTicks = pCompound.getInt("LifeTicks");
        this.warmupDelay = pCompound.getInt("WarmupDelay");
        this.damage = pCompound.getFloat("Damage");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        if (this.summonerUUID != null) {
            pCompound.putUUID("SummonerUUID", this.summonerUUID);
        }
        pCompound.putInt("LifeTicks", this.lifeTicks);
        pCompound.putInt("WarmupDelay", this.warmupDelay);
        pCompound.putFloat("Damage", this.damage);
    }

    public int getWarmupDelay() {
        return this.warmupDelay;
    }

    public int getLifeTicks() {
        return this.lifeTicks;
    }

    @Override
    public void tick() {
        super.tick();
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
        if (this.warmupDelay > 0) {
            this.warmupDelay--;
            if (this.warmupDelay == 0 && this.level().isClientSide()) {
                this.level().addParticle(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
            return;
        }
        this.lifeTicks++;
        if (this.lifeTicks < 20 && this.level().isClientSide()) {
            if (this.lifeTicks % 2 == 0) {
                net.minecraft.core.BlockPos belowPos = net.minecraft.core.BlockPos.containing(this.getX(), this.getY() - 1.0D, this.getZ());
                net.minecraft.world.level.block.state.BlockState state = this.level().getBlockState(belowPos);
                if (state.isAir()) {
                    belowPos = belowPos.below();
                    state = this.level().getBlockState(belowPos);
                }
                if (!state.isAir() && state.getRenderShape() != net.minecraft.world.level.block.RenderShape.INVISIBLE) {
                    this.level().addParticle(
                            new net.minecraft.core.particles.BlockParticleOption(net.minecraft.core.particles.ParticleTypes.BLOCK, state),
                            this.getX() + (this.random.nextDouble() - 0.5D) * 0.6D,
                            this.getY() + 0.1D,
                            this.getZ() + (this.random.nextDouble() - 0.5D) * 0.6D,
                            (this.random.nextDouble() - 0.5D) * 0.15D,
                            0.1D + this.random.nextDouble() * 0.15D,
                            (this.random.nextDouble() - 0.5D) * 0.15D
                    );
                }
            }
        }
        if (this.lifeTicks < 20 && this.level().isClientSide()) {
            if (this.lifeTicks % 3 == 0) {
                this.level().addParticle(ParticleTypes.POOF, this.getX(), this.getY() + 0.1D, this.getZ(),
                        (this.random.nextDouble() - 0.5D) * 0.2D, 0.05D, (this.random.nextDouble() - 0.5D) * 0.2D);
            }
        }
        if (this.lifeTicks == 20) {
            this.playSound(SoundEvents.GENERIC_EXPLODE, 1.2F, 0.7F);
            this.playSound(SoundEvents.ANVIL_LAND, 1.0F, 1.1F);
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles((ParticleOptions) ModParticle.PHANTOM_WING_FLAME.get(), this.getX(), this.getY() + 0.5D, this.getZ(), 20, 0.3D, 0.8D, 0.3D, 0.15D);
                serverLevel.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY() + 0.5D, this.getZ(), 12, 0.3D, 0.8D, 0.3D, 0.1D);
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 0.5D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            if (!this.level().isClientSide()) {
                AABB spikeBox = this.getBoundingBox().inflate(0.75D, 2.5D, 0.75D);
                List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, spikeBox);
                LivingEntity boss = this.getSummoner();
                for (LivingEntity target : targets) {
                    if (this.canHit(target)) {
                        float finalDamage = this.damage > 0 ? this.damage : 16.0F;
                        if (target.hurt(this.damageSources().indirectMagic(this, boss != null ? boss : this), finalDamage)) {
                            target.setDeltaMovement(target.getDeltaMovement().add(0.0D, 0.65D, 0.0D));
                            target.hasImpulse = true;
                        }
                    }
                }
            }
        }
        if (this.lifeTicks >= 30 && this.level().isClientSide()) {
            double rx = this.getX() + (this.random.nextDouble() - 0.5D) * 0.4D;
            double ry = this.getY() + this.random.nextDouble() * 2.0D;
            double rz = this.getZ() + (this.random.nextDouble() - 0.5D) * 0.4D;
            this.level().addParticle(ParticleTypes.ASH, rx, ry, rz, 0.0D, 0.05D, 0.0D);
            this.level().addParticle(ParticleTypes.SMOKE, rx, ry, rz, 0.0D, 0.02D, 0.0D);
            if (this.random.nextBoolean()) {
                this.level().addParticle(ParticleTypes.WHITE_ASH, rx, ry, rz, 0.0D, 0.05D, 0.0D);
            }
        }
        if (this.lifeTicks > 40) {
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.ASH, this.getX(), this.getY() + 1.0D, this.getZ(), 25, 0.3D, 0.8D, 0.3D, 0.1D);
                serverLevel.sendParticles(ParticleTypes.WHITE_ASH, this.getX(), this.getY() + 1.0D, this.getZ(), 20, 0.3D, 0.8D, 0.3D, 0.1D);
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 1.0D, this.getZ(), 12, 0.2D, 0.5D, 0.2D, 0.02D);
            }
            this.discard();
        }
    }

    private boolean canHit(LivingEntity target) {
        return target != this.getSummoner()
                && target.isAlive()
                && !(target instanceof MaledictusPhantomEntity)
                && !(target instanceof Maledictus_PrimeEntity)
                && (!(target instanceof Player player) || (!player.isCreative() && !player.isSpectator()));
    }
}