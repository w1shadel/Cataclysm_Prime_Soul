package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime;

import com.github.L_Ender.cataclysm.blocks.Cursed_Tombstone_Block;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.init.ModTag;
import com.maxwell.cataclysm_primed_soul.api.entity.IPrimeTombstone;
import com.maxwell.cataclysm_primed_soul.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = com.maxwell.cataclysm_primed_soul.Primed_Soul.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MaledictusSummonEvent {
    @SubscribeEvent
    public static void onBlockClick(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof Cursed_Tombstone_Block)) {
            return;
        }
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        BlockEntity be = level.getBlockEntity(pos);
        if (player.isShiftKeyDown() && event.getHand() == InteractionHand.MAIN_HAND) {
            if (stack.is(ModItems.RUSTED_KNIGHT_SWORD.get())) {
                if (!(Boolean) state.getValue(Cursed_Tombstone_Block.LIT)) {
                    if (!level.isClientSide()) {
                        if (be instanceof IPrimeTombstone primeTombstone) {
                            primeTombstone.cataclysm_primed_soul$setPrimeSummon(true);
                        }
                    }
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                }
            }
        } else if (!player.isShiftKeyDown() && event.getHand() == InteractionHand.MAIN_HAND) {
            if (be instanceof IPrimeTombstone primeTombstone && primeTombstone.cataclysm_primed_soul$isPrimeSummon()) {
                if (!(Boolean) state.getValue(Cursed_Tombstone_Block.LIT) && (Boolean) state.getValue(Cursed_Tombstone_Block.POWERED)) {
                    if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                        Maledictus_PrimeEntity prime = com.maxwell.cataclysm_primed_soul.init.ModEntities.MALEDICTUS_PRIME.get().create(level);
                        if (prime != null) {
                            ScreenShake_Entity.ScreenShake(level, Vec3.atCenterOf(pos), 25.0F, 0.25F, 0, 40);
                            prime.setPos((double) pos.getX() + 0.5D, (double) (pos.getY() + 2), (double) pos.getZ() + 0.5D);
                            prime.setHomePos(GlobalPos.of(level.dimension(), pos));
                            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.getX() + 0.5D, pos.getY() + 2.0D, pos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                            serverLevel.sendParticles((ParticleOptions) com.github.L_Ender.cataclysm.init.ModParticle.PHANTOM_WING_FLAME.get(), pos.getX() + 0.5D, pos.getY() + 2.0D, pos.getZ() + 0.5D, 45, 1.5D, 1.5D, 1.5D, 0.1D);
                            serverLevel.sendParticles(ParticleTypes.SOUL, pos.getX() + 0.5D, pos.getY() + 2.0D, pos.getZ() + 0.5D, 30, 1.5D, 1.5D, 1.5D, 0.1D);
                            int MthX = Mth.floor((float) pos.getX());
                            int MthY = Mth.floor((float) pos.getY());
                            int MthZ = Mth.floor((float) pos.getZ());
                            for (int k2 = -1; k2 <= 1; ++k2) {
                                for (int l2 = -1; l2 <= 1; ++l2) {
                                    for (int j = 0; j <= 5; ++j) {
                                        int i3 = MthX + k2;
                                        int k = MthY + j;
                                        int l = MthZ + l2;
                                        BlockPos blockpos = new BlockPos(i3, k, l);
                                        BlockState block = level.getBlockState(blockpos);
                                        if (block != Blocks.AIR.defaultBlockState() && !block.is(ModTag.ALTAR_DESTROY_IMMUNE)) {
                                            level.destroyBlock(blockpos, false);
                                        }
                                    }
                                }
                            }
                            if (level.addFreshEntity(prime)) {
                                level.destroyBlock(pos, false);
                            }
                        }
                    }
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                }
            }
        }
    }
}