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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class Maledictus_PrimeSwordEntity extends Entity {
    private int lifeTicks;
    @Nullable
    private LivingEntity summoner;
    @Nullable
    private UUID summonerUUID;

    public Maledictus_PrimeSwordEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noCulling = true;
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
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        if (this.summonerUUID != null) {
            pCompound.putUUID("SummonerUUID", this.summonerUUID);
        }
        pCompound.putInt("LifeTicks", this.lifeTicks);
    }

    @Override
    public void tick() {
        super.tick();
        this.lifeTicks++;
        Vec3 motion = this.getDeltaMovement();
        double nextX = this.getX() + motion.x;
        double nextY = this.getY() + motion.y;
        double nextZ = this.getZ() + motion.z;
        this.setPos(nextX, nextY, nextZ);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY(), this.getZ(), 2, 0.1D, 0.1D, 0.1D, 0.0D);
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY(), this.getZ(), 2, 0.1D, 0.1D, 0.1D, 0.0D);
        }
        if (!this.level().isClientSide()) {
            AABB swordBox = this.getBoundingBox().inflate(0.75D);
            List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, swordBox);
            for (LivingEntity target : targets) {
                if (this.canHit(target)) {
                    float damage = 22.0F;
                    LivingEntity boss = this.getSummoner();
                    if (boss != null) {
                        damage = (float) boss.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.2F;
                    }
                    if (target.hurt(this.damageSources().indirectMagic(this, boss != null ? boss : this), damage)) {
                        Vec3 inFront = target.position().subtract(Vec3.directionFromRotation(0.0F, target.getYRot()).normalize().scale(1.2D));
                        if (this.level() instanceof ServerLevel serverLevel) {
                            if (boss != null) {
                                serverLevel.sendParticles((ParticleOptions) ModParticle.PHANTOM_WING_FLAME.get(), boss.getX(), boss.getY() + 1.0D, boss.getZ(), 12, 0.5D, 0.5D, 0.5D, 0.05D);
                                serverLevel.sendParticles(ParticleTypes.FLASH, boss.getX(), boss.getY() + 1.0D, boss.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                            }
                            serverLevel.sendParticles((ParticleOptions) ModParticle.PHANTOM_WING_FLAME.get(), inFront.x, target.getY() + 1.0D, inFront.z, 12, 0.5D, 0.5D, 0.5D, 0.05D);
                            serverLevel.sendParticles(ParticleTypes.FLASH, inFront.x, target.getY() + 1.0D, inFront.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                        }
                        this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.2F, 0.55F);
                        if (boss instanceof Maledictus_PrimeEntity prime) {
                            prime.teleportTo(inFront.x, target.getY(), inFront.z);
                            prime.lookAt(target, 360.0F, 360.0F);
                            prime.setAttackState(this.random.nextBoolean() ? Maledictus_PrimeEntity.ATTACK_JAB_1 : Maledictus_PrimeEntity.ATTACK_EX_JAB_1);
                        }
                        this.discard();
                        return;
                    }
                }
            }
            if (this.lifeTicks > 30 || this.horizontalCollision || this.verticalCollision) {
                this.discard();
            }
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