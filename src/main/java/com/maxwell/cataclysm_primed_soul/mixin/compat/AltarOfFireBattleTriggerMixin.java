package com.maxwell.cataclysm_primed_soul.mixin.compat;

import com.gametechbc.gtbcs_cataclysmic_uis.boss_screen.IBossBattleTrigger;
import com.github.L_Ender.cataclysm.blockentities.AltarOfFire_Block_Entity;
import com.maxwell.cataclysm_primed_soul.api.entity.IPrimeAltar;
import com.maxwell.cataclysm_primed_soul.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = AltarOfFire_Block_Entity.class, priority = 1100)
public abstract class AltarOfFireBattleTriggerMixin implements IBossBattleTrigger {
    @Override
    public void onBattleTrigger(ServerPlayer player, int difficulty) {
        AltarOfFire_Block_Entity self = (AltarOfFire_Block_Entity) (Object) this;
        Level level = self.getLevel();
        if (level == null || level.isClientSide || !self.getItem(0).isEmpty()) {
            return;
        }
        boolean isPrime = (self instanceof IPrimeAltar primeAltar) && primeAltar.cataclysm_primed_soul$isPendingPrime();
        if (isPrime) {
            ItemStack abyssalAshes = this.prime$findAbyssalAshes(player);
            if (!abyssalAshes.isEmpty()) {
                ItemStack copy = abyssalAshes.copy();
                copy.setCount(1);
                self.setItem(0, copy);
                if (!player.isCreative()) {
                    abyssalAshes.shrink(1);
                }
            } else {
                player.displayClientMessage(Component.translatable("tooltip.cataclysm_primed_soul.abyssal_ashes.desc"), true);
            }
        } else {
            ItemStack burningAshes = this.prime$findBurningAshes(player);
            if (!burningAshes.isEmpty()) {
                ItemStack copy = burningAshes.copy();
                copy.setCount(1);
                self.setItem(0, copy);
                if (!player.isCreative()) {
                    burningAshes.shrink(1);
                }
            } else {
                player.displayClientMessage(Component.translatable("message.gtbcs_cataclysmic_uis.ignis.missing_catalyst"), true);
            }
        }
    }

    @Unique
    private ItemStack prime$findAbyssalAshes(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(ModItems.ABYSSAL_ASHES.get())) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Unique
    private ItemStack prime$findBurningAshes(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(com.github.L_Ender.cataclysm.init.ModItems.BURNING_ASHES.get())) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}