package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.sub;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FlameStrikeSpawner {
    public static SpawnerBuilder builder(Level level, double x, double z) {
        return new SpawnerBuilder(level, x, z);
    }

    public static class SpawnerBuilder {
        private final Level level;
        private final double x;
        private final double z;
        private double minY;
        private double maxY;
        private float rotation = 0.0F;
        private int duration = 600;
        private int waitTime = 0;
        private int warmupDelay = 0;
        private int explosionDelay = 0;
        private float radius = 3.0F;
        private float damage = 6.0F;
        private float hpDamage = 6.0F;
        private boolean soul = false;
        private boolean white = false;
        private float explosionRadius = 2.0F;
        private LivingEntity owner = null;

        public SpawnerBuilder(Level level, double x, double z) {
            this.level = level;
            this.x = x;
            this.z = z;
            this.minY = 0.0D;
            this.maxY = 256.0D;
        }

        public SpawnerBuilder yRange(double minY, double maxY) {
            this.minY = minY;
            this.maxY = maxY;
            return this;
        }

        public SpawnerBuilder rotation(float rotation) {
            this.rotation = rotation;
            return this;
        }

        public SpawnerBuilder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public SpawnerBuilder waitTime(int waitTime) {
            this.waitTime = waitTime;
            return this;
        }

        public SpawnerBuilder warmupDelay(int warmupDelay) {
            this.warmupDelay = warmupDelay;
            return this;
        }

        public SpawnerBuilder explosionDelay(int explosionDelay) {
            this.explosionDelay = explosionDelay;
            return this;
        }

        public SpawnerBuilder radius(float radius) {
            this.radius = radius;
            return this;
        }

        public SpawnerBuilder damage(float damage) {
            this.damage = damage;
            return this;
        }

        public SpawnerBuilder hpDamage(float hpDamage) {
            this.hpDamage = hpDamage;
            return this;
        }

        public SpawnerBuilder soul(boolean soul) {
            this.soul = soul;
            return this;
        }

        public SpawnerBuilder white(boolean white) {
            this.white = white;
            return this;
        }

        public SpawnerBuilder explosionRadius(float explosionRadius) {
            this.explosionRadius = explosionRadius;
            return this;
        }

        public SpawnerBuilder owner(LivingEntity owner) {
            this.owner = owner;
            return this;
        }

        public Prime_Flame_Strike_Entity spawn() {
            BlockPos blockpos = BlockPos.containing(x, maxY, z);
            boolean foundGround = false;
            double d0 = 0.0D;
            do {
                BlockPos blockpos1 = blockpos.below();
                BlockState blockstate = level.getBlockState(blockpos1);
                if (blockstate.isFaceSturdy(level, blockpos1, Direction.UP)) {
                    if (!level.isEmptyBlock(blockpos)) {
                        BlockState blockstate1 = level.getBlockState(blockpos);
                        VoxelShape voxelshape = blockstate1.getCollisionShape(level, blockpos);
                        if (!voxelshape.isEmpty()) {
                            d0 = voxelshape.max(Direction.Axis.Y);
                        }
                    }
                    foundGround = true;
                    break;
                }
                blockpos = blockpos.below();
            } while (blockpos.getY() >= Mth.floor(minY) - 1);
            if (foundGround) {
                double spawnY = (double) blockpos.getY() + d0;
                Prime_Flame_Strike_Entity entity = new Prime_Flame_Strike_Entity(
                        level, x, spawnY, z, rotation, duration, waitTime, warmupDelay, radius, damage, hpDamage, soul, owner
                );
                entity.setExplosionDelay(explosionDelay);
                entity.setExplosionRadius(explosionRadius);
                entity.setWhite(white);
                level.addFreshEntity(entity);
                return entity;
            }
            return null;
        }
    }
}
