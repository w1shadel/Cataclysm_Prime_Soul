package com.maxwell.cataclysm_primed_soul.mixin.compat;

import com.gametechbc.gtbcs_cataclysmic_uis.boss_screen.IBossBattleTrigger;
import com.github.L_Ender.cataclysm.blockentities.Cursed_tombstone_Entity;
import com.github.L_Ender.cataclysm.blocks.Cursed_Tombstone_Block;
import com.github.L_Ender.cataclysm.init.ModBlocks;
import com.maxwell.cataclysm_primed_soul.api.entity.IPrimeTombstone;
import com.maxwell.cataclysm_primed_soul.entity.cutscene.MaledictusPrimeCutsceneEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = Cursed_tombstone_Entity.class, priority = 1100)
public abstract class CursedTombstoneBattleTriggerMixin implements IBossBattleTrigger {
    @Override
    public void onBattleTrigger(ServerPlayer player, int difficulty) {
        Cursed_tombstone_Entity self = (Cursed_tombstone_Entity) (Object) this;
        Level level = self.getLevel();
        if (level == null || level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos pos = self.getBlockPos();
        BlockState state = self.getBlockState();
        if (!(state.getBlock() instanceof Cursed_Tombstone_Block)) {
            return;
        }
        if (!(Boolean) state.getValue(Cursed_Tombstone_Block.POWERED)) {
            player.displayClientMessage(Component.translatable("block.cataclysm.cursed_tombstone.message"), true);
            return;
        }
        if ((Boolean) state.getValue(Cursed_Tombstone_Block.LIT)) {
            return;
        }
        if (self instanceof IPrimeTombstone primeTombstone && primeTombstone.cataclysm_primed_soul$isPrimeSummon()) {
            Direction direction = state.getValue(Cursed_Tombstone_Block.FACING);
            Vec3 dir = new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ());
            MaledictusPrimeCutsceneEntity.summon(
                    serverLevel,
                    pos.getCenter().add(0, -0.5, 0),
                    dir,
                    GlobalPos.of(serverLevel.dimension(), pos)
            );
            for (int x = -5; x <= 5; x++) {
                for (int y = -5; y <= 5; y++) {
                    for (int z = -5; z <= 5; z++) {
                        BlockPos p = pos.offset(x, 18 + y, z);
                        if (serverLevel.getBlockState(p).is(ModBlocks.POINTED_ICICLE.get())) {
                            serverLevel.destroyBlock(p, false);
                        }
                    }
                }
            }
            serverLevel.destroyBlock(pos, false);
            return;
        }
        level.setBlock(pos, state.setValue(Cursed_Tombstone_Block.LIT, Boolean.TRUE), 10);
    }
}