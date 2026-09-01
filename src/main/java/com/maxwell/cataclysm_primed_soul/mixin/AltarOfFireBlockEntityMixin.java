package com.maxwell.cataclysm_primed_soul.mixin;

import com.github.L_Ender.cataclysm.blockentities.AltarOfFire_Block_Entity;
import com.maxwell.cataclysm_primed_soul.api.entity.IPrimeAltar;
import com.maxwell.cataclysm_primed_soul.entity.cutscene.IgnisPrimeCutsceneEntity;
import com.maxwell.cataclysm_primed_soul.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AltarOfFire_Block_Entity.class, remap = false)
public abstract class AltarOfFireBlockEntityMixin implements IPrimeAltar {
    @Shadow
    public boolean summoningthis;
    @Shadow
    public int summoningticks;
    @Unique
    private boolean cataclysm_primed_soul$pendingPrime = false;

    @Override
    @Unique
    public boolean cataclysm_primed_soul$isPendingPrime() {
        return this.cataclysm_primed_soul$pendingPrime;
    }

    @Override
    @Unique
    public void cataclysm_primed_soul$setPendingPrime(boolean pending) {
        this.cataclysm_primed_soul$pendingPrime = pending;
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(Level level, BlockState state, BlockPos pos, CallbackInfo ci) {
        AltarOfFire_Block_Entity altar = (AltarOfFire_Block_Entity) (Object) this;
        ItemStack held = altar.getItem(0);
        if (!held.isEmpty() && held.getItem() == ModItems.ABYSSAL_ASHES.get()) {
            if (level instanceof ServerLevel serverLevel) {
                altar.setItem(0, ItemStack.EMPTY);
                altar.setChanged();
                serverLevel.sendBlockUpdated(pos, state, state, 3);
                IgnisPrimeCutsceneEntity.summon(serverLevel, pos.getCenter(), GlobalPos.of(serverLevel.dimension(), pos));
            }
            ci.cancel();
        }
    }

    @Unique
    private void spawnPrimeSphereParticle(Level level, BlockPos pos, float height, float size) {
        double d0 = (double) ((float) pos.getX() + 0.5F);
        double d1 = (double) ((float) pos.getY() + height);
        double d2 = (double) ((float) pos.getZ() + 0.5F);
        RandomSource rnd = level.getRandom();
        for (float i = -size; i <= size; ++i) {
            for (float j = -size; j <= size; ++j) {
                for (float k = -size; k <= size; ++k) {
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

    @Unique
    private void blockBreaking(Level level, BlockPos pos, int x, int y, int z) {
        int mthX = pos.getX();
        int mthY = pos.getY();
        int mthZ = pos.getZ();
        for (int k2 = -x; k2 <= x; ++k2) {
            for (int l2 = -z; l2 <= z; ++l2) {
                for (int j = 0; j <= y; ++j) {
                    BlockPos blockpos = new BlockPos(mthX + k2, mthY + j, mthZ + l2);
                    BlockState block = level.getBlockState(blockpos);
                    if (block != Blocks.AIR.defaultBlockState() && !block.is(com.github.L_Ender.cataclysm.init.ModTag.ALTAR_DESTROY_IMMUNE)) {
                        level.destroyBlock(blockpos, false);
                    }
                }
            }
        }
    }

    @Unique
    private void basaltBreaking(Level level, BlockPos pos, int x, int y, int z) {
        int mthX = pos.getX();
        int mthY = pos.getY();
        int mthZ = pos.getZ();
        for (int k2 = -x; k2 <= x; ++k2) {
            for (int l2 = -z; l2 <= z; ++l2) {
                for (int j = -1; j <= y; ++j) {
                    BlockPos blockpos = new BlockPos(mthX + k2, mthY + j, mthZ + l2);
                    BlockState blockstate = level.getBlockState(blockpos);
                    if (blockstate.getBlock() != Blocks.AIR && blockstate.getBlock() == Blocks.BASALT) {
                        level.destroyBlock(blockpos, false);
                    }
                }
            }
        }
    }
}