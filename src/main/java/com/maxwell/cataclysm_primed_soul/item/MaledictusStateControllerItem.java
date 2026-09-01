package com.maxwell.cataclysm_primed_soul.item;

import com.maxwell.cataclysm_primed_soul.api.item.ISpecialModel;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.Maledictus_PrimeEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class MaledictusStateControllerItem extends Item implements ISpecialModel {
    private static final String STATE_INDEX_TAG = "MaledictusStateIndex";
    private static final int[] STATES = {
            0,
            Maledictus_PrimeEntity.ATTACK_JAB_1,
            Maledictus_PrimeEntity.ATTACK_JAB_2,
            Maledictus_PrimeEntity.ATTACK_JAB_3,
            Maledictus_PrimeEntity.ATTACK_CHARGE,
            Maledictus_PrimeEntity.ATTACK_COUNTER_START,
            Maledictus_PrimeEntity.ATTACK_COUNTER_SUCCESS,
            Maledictus_PrimeEntity.ATTACK_COUNTER_FAIL,
            Maledictus_PrimeEntity.ATTACK_SHOCKWAVE_START,
            Maledictus_PrimeEntity.ATTACK_SHOCKWAVE_END,
            Maledictus_PrimeEntity.ATTACK_GRAB_START,
            Maledictus_PrimeEntity.ATTACK_GRAB_SUCCESS,
            Maledictus_PrimeEntity.ATTACK_GRAB_FAIL,
            Maledictus_PrimeEntity.ATTACK_GRAB_SLOOP,
            Maledictus_PrimeEntity.ATTACK_GRAB_SEND,
            Maledictus_PrimeEntity.ATTACK_HEAD_BREAK,
            Maledictus_PrimeEntity.ATTACK_EX_JAB_1,
            Maledictus_PrimeEntity.ATTACK_EX_JAB_2,
            Maledictus_PrimeEntity.ATTACK_EX_JAB_3,
            Maledictus_PrimeEntity.ATTACK_ULTIMATE,
            Maledictus_PrimeEntity.BACKSTEP,
            Maledictus_PrimeEntity.BACKSTEP_BEFORE_CHARGE
    };

    public MaledictusStateControllerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                int nextIndex = (getStateIndex(stack) + 1) % STATES.length;
                setStateIndex(stack, nextIndex);
                player.displayClientMessage(Component.literal("Maledictus state: " + getStateName(STATES[nextIndex])), true);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        if (!level.isClientSide()) {
            Maledictus_PrimeEntity boss = findBoss(player);
            if (boss == null) {
                player.displayClientMessage(Component.literal("Maledictus Prime が視線上にいません"), true);
            } else {
                int state = getSelectedState(stack);
                boss.setAttackState(state);
                player.displayClientMessage(Component.literal("Maledictus state -> " + getStateName(state)), true);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof Maledictus_PrimeEntity boss) || player.level().isClientSide()) {
            return InteractionResult.PASS;
        }
        int state = getSelectedState(stack);
        boss.setAttackState(state);
        player.displayClientMessage(Component.literal("Maledictus state -> " + getStateName(state)), true);
        return InteractionResult.sidedSuccess(player.level().isClientSide());
    }

    private Maledictus_PrimeEntity findBoss(Player player) {
        Vec3 look = player.getLookAngle().normalize();
        AABB search = player.getBoundingBox().expandTowards(look.scale(32.0D)).inflate(2.0D);
        Maledictus_PrimeEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (Maledictus_PrimeEntity boss : player.level().getEntitiesOfClass(Maledictus_PrimeEntity.class, search)) {
            if (!boss.isAlive() || boss.isEcho()) {
                continue;
            }
            Vec3 toBoss = boss.getEyePosition().subtract(player.getEyePosition());
            if (toBoss.lengthSqr() < 0.01D || look.dot(toBoss.normalize()) < 0.75D || !player.hasLineOfSight(boss)) {
                continue;
            }
            double distance = player.distanceToSqr(boss);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = boss;
            }
        }
        return nearest;
    }

    private int getStateIndex(ItemStack stack) {
        return Math.floorMod(stack.getOrCreateTag().getInt(STATE_INDEX_TAG), STATES.length);
    }

    private void setStateIndex(ItemStack stack, int index) {
        stack.getOrCreateTag().putInt(STATE_INDEX_TAG, index);
    }

    private int getSelectedState(ItemStack stack) {
        return STATES[getStateIndex(stack)];
    }

    private String getStateName(int state) {
        return switch (state) {
            case 0 -> "IDLE (0)";
            case Maledictus_PrimeEntity.ATTACK_JAB_1 -> "JAB_1 (1)";
            case Maledictus_PrimeEntity.ATTACK_JAB_2 -> "JAB_2 (2)";
            case Maledictus_PrimeEntity.ATTACK_JAB_3 -> "JAB_3 (3)";
            case Maledictus_PrimeEntity.ATTACK_CHARGE -> "CHARGE (4)";
            case Maledictus_PrimeEntity.ATTACK_COUNTER_START -> "COUNTER_START (5)";
            case Maledictus_PrimeEntity.ATTACK_COUNTER_SUCCESS -> "COUNTER_SUCCESS (6)";
            case Maledictus_PrimeEntity.ATTACK_COUNTER_FAIL -> "COUNTER_FAIL (7)";
            case Maledictus_PrimeEntity.ATTACK_SHOCKWAVE_START -> "SHOCKWAVE_START (8)";
            case Maledictus_PrimeEntity.ATTACK_SHOCKWAVE_END -> "SHOCKWAVE_END (9)";
            case Maledictus_PrimeEntity.ATTACK_GRAB_START -> "GRAB_START (10)";
            case Maledictus_PrimeEntity.ATTACK_GRAB_SUCCESS -> "GRAB_SUCCESS (11)";
            case Maledictus_PrimeEntity.ATTACK_GRAB_FAIL -> "GRAB_FAIL (12)";
            case Maledictus_PrimeEntity.ATTACK_GRAB_SLOOP -> "GRAB_SLOOP (13)";
            case Maledictus_PrimeEntity.ATTACK_GRAB_SEND -> "GRAB_SEND (14)";
            case Maledictus_PrimeEntity.ATTACK_HEAD_BREAK -> "HEAD_BREAK (15)";
            case Maledictus_PrimeEntity.ATTACK_EX_JAB_1 -> "EX_JAB_1 (16)";
            case Maledictus_PrimeEntity.ATTACK_EX_JAB_2 -> "EX_JAB_2 (17)";
            case Maledictus_PrimeEntity.ATTACK_EX_JAB_3 -> "EX_JAB_3 (18)";
            case Maledictus_PrimeEntity.ATTACK_ULTIMATE -> "ULTIMATE (35)";
            case Maledictus_PrimeEntity.BACKSTEP -> "BACKSTEP (80)";
            case Maledictus_PrimeEntity.BACKSTEP_BEFORE_CHARGE -> "BACKSTEP_BEFORE_CHARGE (81)";
            default -> "UNKNOWN (" + state + ")";
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal("選択中: " + getStateName(getSelectedState(stack))));
        tooltip.add(Component.literal("右クリック: 視線上の本体で再生"));
        tooltip.add(Component.literal("スニーク右クリック: 次のステート"));
    }
}
