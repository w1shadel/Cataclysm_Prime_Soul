package com.maxwell.cataclysm_primed_soul.mixin.compat;

import com.gametechbc.gtbcs_cataclysmic_uis.boss_screen.BossScreenOpener;
import com.github.L_Ender.cataclysm.blockentities.AltarOfFire_Block_Entity;
import com.github.L_Ender.cataclysm.blocks.Altar_Of_Fire_Block;
import com.maxwell.cataclysm_primed_soul.api.entity.IPrimeAltar;
import com.maxwell.cataclysm_primed_soul.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Altar_Of_Fire_Block.class, priority = 900)
public class AltarOfFireBlockPrimeMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void openPrimeBossScreen(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (!player.isShiftKeyDown()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof AltarOfFire_Block_Entity altar && altar.getItem(0).isEmpty()) {
                if (altar instanceof IPrimeAltar primeAltar) {
                    if (player.getItemInHand(hand).is(ModItems.ABYSSAL_ASHES.get())) {
                        primeAltar.cataclysm_primed_soul$setPendingPrime(true);
                        if (player instanceof ServerPlayer serverPlayer) {
                            BossScreenOpener.openForBlock(serverPlayer, "ignis_prime", pos);
                        }
                        cir.setReturnValue(InteractionResult.SUCCESS);
                    } else {
                        primeAltar.cataclysm_primed_soul$setPendingPrime(false);
                    }
                }
            }
        }
    }
}