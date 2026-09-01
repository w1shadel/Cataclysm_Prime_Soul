package com.maxwell.cataclysm_primed_soul.mixin.compat;

import com.gametechbc.gtbcs_cataclysmic_uis.boss_screen.BossScreenOpener;
import com.github.L_Ender.cataclysm.blockentities.Cursed_tombstone_Entity;
import com.github.L_Ender.cataclysm.blocks.Cursed_Tombstone_Block;
import com.maxwell.cataclysm_primed_soul.api.entity.IPrimeTombstone;
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

@Mixin(value = Cursed_Tombstone_Block.class, priority = 900)
public class CursedTombstoneBlockPrimeMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void openPrimeTombstoneScreen(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (!player.isShiftKeyDown() && !(Boolean) state.getValue(Cursed_Tombstone_Block.LIT)) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof Cursed_tombstone_Entity && be instanceof IPrimeTombstone primeTombstone) {
                if (primeTombstone.cataclysm_primed_soul$isPrimeSummon()) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        BossScreenOpener.openForBlock(serverPlayer, "maledictus_prime", pos);
                    }
                    cir.setReturnValue(InteractionResult.SUCCESS);
                }
            }
        }
    }
}