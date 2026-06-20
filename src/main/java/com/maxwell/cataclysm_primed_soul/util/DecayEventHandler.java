package com.maxwell.cataclysm_primed_soul.util;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.item.ErosionSwordItem;
import com.maxwell.cataclysm_primed_soul.network.ModMessages;
import com.maxwell.cataclysm_primed_soul.network.packet.ClientboundDecaySyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = Primed_Soul.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DecayEventHandler {
    private static long EVENT_CANCELED_OFFSET = -1;
    private static java.lang.reflect.Field EVENT_CANCELED_FIELD = null;

    static {
        try {
            EVENT_CANCELED_FIELD = net.minecraftforge.eventbus.api.Event.class.getDeclaredField("isCanceled");
            DecayUnsafeHelper.forceSetAccessible(EVENT_CANCELED_FIELD);
            EVENT_CANCELED_OFFSET = DecayUnsafeHelper.getFieldOffset(EVENT_CANCELED_FIELD);
        } catch (Throwable ignored) {
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onPlayerAttack(AttackEntityEvent event) {
        if (event.getTarget() instanceof IDecayEntity decayTarget && event.getTarget() instanceof LivingEntity livingTarget) {
            Player attacker = event.getEntity();
            ItemStack heldItem = attacker.getMainHandItem();
            if (heldItem.getItem() instanceof ErosionSwordItem) {
                float decayPerHit = livingTarget.getMaxHealth() * 0.20F;
                decayTarget.addDecayAmount(decayPerHit);
                livingTarget.level().broadcastEntityEvent(livingTarget, (byte) 2);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void enforceDecayDeath(net.minecraftforge.event.entity.living.LivingDeathEvent event) {
        if (event.getEntity() instanceof IDecayEntity decayEntity) {
            if (decayEntity.getDecayAmount() >= event.getEntity().getMaxHealth()) {
                if (event.isCanceled()) {
                    try {
                        event.setCanceled(false);
                    } catch (Throwable ignored) {
                    }
                    if (event.isCanceled() && EVENT_CANCELED_OFFSET != -1) {
                        DecayUnsafeHelper.putBoolean(event, EVENT_CANCELED_OFFSET, false);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        net.minecraft.world.entity.player.Player newPlayer = event.getEntity();
        if (newPlayer instanceof IDecayEntity decayEntity) {
            decayEntity.setDecayAmount(0.0f);
            decayEntity.setSuperInvincible(false);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void preventInvincibleHurt(net.minecraftforge.event.entity.living.LivingHurtEvent event) {
        if (event.getEntity() instanceof IDecayEntity decay && decay.isSuperInvincible()) {
            event.setCanceled(true);
            event.setAmount(0.0F);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void preventInvincibleAttack(net.minecraftforge.event.entity.living.LivingAttackEvent event) {
        if (event.getEntity() instanceof IDecayEntity decay && decay.isSuperInvincible()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void preventInvincibleDeath(net.minecraftforge.event.entity.living.LivingDeathEvent event) {
        if (event.getEntity() instanceof IDecayEntity decay && decay.isSuperInvincible()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void preventInvincibleDamage(net.minecraftforge.event.entity.living.LivingDamageEvent event) {
        if (event.getEntity() instanceof IDecayEntity decay && decay.isSuperInvincible()) {
            event.setCanceled(true);
            event.setAmount(0.0F);
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof IDecayEntity decay && event.getEntity() instanceof ServerPlayer serverPlayer) {
            ModMessages.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new ClientboundDecaySyncPacket(event.getTarget().getId(), decay.getDecayAmount(), decay.isSuperInvincible())
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer && serverPlayer instanceof IDecayEntity decay) {
            ModMessages.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new ClientboundDecaySyncPacket(serverPlayer.getId(), decay.getDecayAmount(), decay.isSuperInvincible())
            );
        }
    }
}