package com.maxwell.cataclysm_primed_soul.mixin;

import com.github.L_Ender.cataclysm.blockentities.AltarOfFire_Block_Entity;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.Ignis_PrimeEntity;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.sub.Prime_Flame_Strike_Entity;
import com.maxwell.cataclysm_primed_soul.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AltarOfFire_Block_Entity.class, remap = false)
public abstract class AltarOfFireBlockEntityMixin {

    @Shadow public boolean summoningthis;
    @Shadow public int summoningticks;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(Level level, BlockState state, BlockPos pos, CallbackInfo ci) {
        AltarOfFire_Block_Entity altar = (AltarOfFire_Block_Entity) (Object) this;
        ItemStack held = altar.getItem(0);

        if (!held.isEmpty() && held.getItem() == ModItems.ABYSSAL_ASHES.get()) {
            this.summoningthis = true;

            if (this.summoningticks == 1) {
                ScreenShake_Entity.ScreenShake(level, Vec3.atCenterOf(pos), 20.0F, 0.05F, 0, 150);
            }

            if (this.summoningticks > 118 && this.summoningticks < 121) {
                this.spawnPrimeSphereParticle(level, pos, 3.0F, 3.0F);
            }

            if (this.summoningticks > 121) {
                this.blockBreaking(level, pos, 3, 3, 3);
                this.basaltBreaking(level, pos, 16, 8, 16);

                if (level instanceof ServerLevel serverLevel) {
                    Ignis_PrimeEntity prime = new Ignis_PrimeEntity(com.maxwell.cataclysm_primed_soul.init.ModEntities.IGNIS_PRIME.get(), serverLevel);
                    if (prime != null) {
                        prime.setPos(pos.getX() + 0.5D, pos.getY() + 1.2D, pos.getZ() + 0.5D);

                        double jumpHeight = 1.6D;
                        prime.setDeltaMovement(0.0D, jumpHeight, 0.0D);
                        prime.hasImpulse = true;

                        boolean flag = serverLevel.addFreshEntity(prime);
                        if (flag) {
                            Prime_Flame_Strike_Entity flameStrike = new Prime_Flame_Strike_Entity(
                                    serverLevel,
                                    pos.getX() + 0.5D,
                                    pos.getY() + 1.0D,
                                    pos.getZ() + 0.5D,
                                    0.0F, 60, 0, 0, 5.5F, 10.0F, 10.0F, true, prime
                            );
                            flameStrike.setWhite(true);
                            serverLevel.addFreshEntity(flameStrike);

                            altar.setItem(0, ItemStack.EMPTY);
                            altar.setChanged();
                            serverLevel.sendBlockUpdated(pos, state, state, 3);
                        }
                    }
                }
            }

            if (!this.summoningthis) {
                this.summoningticks = 0;
            } else {
                this.summoningticks++;
            }
            ci.cancel();
        }
    }

    private void spawnPrimeSphereParticle(Level level, BlockPos pos, float height, float size) {
        double d0 = (double)((float)pos.getX() + 0.5F);
        double d1 = (double)((float)pos.getY() + height);
        double d2 = (double)((float)pos.getZ() + 0.5F);
        RandomSource rnd = level.getRandom();

        for(float i = -size; i <= size; ++i) {
            for(float j = -size; j <= size; ++j) {
                for(float k = -size; k <= size; ++k) {
                    double d3 = (double) j + (rnd.nextDouble() - rnd.nextDouble()) * 0.5D;
                    double d2d = (double) i + (rnd.nextDouble() - rnd.nextDouble()) * 0.5D;
                    double d5 = (double) k + (rnd.nextDouble() - rnd.nextDouble()) * 0.5D;
                    double d6 = (double) Mth.sqrt((float) (d3 * d3 + d2d * d2d + d5 * d5)) / 0.5F + rnd.nextGaussian() * 0.05;
                    level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, d0, d1, d2, d3 / d6, d2d / d6, d5 / d6);
                    if (i != -size && i != size && j != -size && j != size) {
                        k += size * 2.0F - 1.0F;
                    }
                }
            }
        }
    }

    private void blockBreaking(Level level, BlockPos pos, int x, int y, int z) {
        int MthX = pos.getX();
        int MthY = pos.getY();
        int MthZ = pos.getZ();

        for(int k2 = -x; k2 <= x; ++k2) {
            for(int l2 = -z; l2 <= z; ++l2) {
                for(int j = 0; j <= y; ++j) {
                    BlockPos blockpos = new BlockPos(MthX + k2, MthY + j, MthZ + l2);
                    BlockState block = level.getBlockState(blockpos);
                    if (block != Blocks.AIR.defaultBlockState() && !block.is(com.github.L_Ender.cataclysm.init.ModTag.ALTAR_DESTROY_IMMUNE)) {
                        level.destroyBlock(blockpos, false);
                    }
                }
            }
        }
    }

    private void basaltBreaking(Level level, BlockPos pos, int x, int y, int z) {
        int MthX = pos.getX();
        int MthY = pos.getY();
        int MthZ = pos.getZ();

        for(int k2 = -x; k2 <= x; ++k2) {
            for(int l2 = -z; l2 <= z; ++l2) {
                for(int j = -1; j <= y; ++j) {
                    BlockPos blockpos = new BlockPos(MthX + k2, MthY + j, MthZ + l2);
                    BlockState blockstate = level.getBlockState(blockpos);
                    if (blockstate.getBlock() != Blocks.AIR && blockstate.getBlock() == Blocks.BASALT) {
                        level.destroyBlock(blockpos, false);
                    }
                }
            }
        }
    }
}