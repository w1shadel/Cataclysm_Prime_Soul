package com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime;

import com.github.L_Ender.cataclysm.blocks.Cursed_Tombstone_Block;
import com.maxwell.cataclysm_primed_soul.api.entity.IPrimeTombstone;
import com.maxwell.cataclysm_primed_soul.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
                            level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0F, 0.8F);
                        }
                    }
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                }
            }
        }
    }
}