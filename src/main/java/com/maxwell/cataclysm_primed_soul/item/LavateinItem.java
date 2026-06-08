package com.maxwell.cataclysm_primed_soul.item;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.api.item.IShaderItem;
import com.maxwell.cataclysm_primed_soul.api.item.ISpecialModel;
import com.maxwell.cataclysm_primed_soul.client.render.item.LavateinRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.sounds.SoundEvents;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.List;
@SuppressWarnings("removal")
public class LavateinItem extends SwordItem implements ISpecialModel, IShaderItem {
    private static final UUID ATTACK_SPEED_MODIFIER_UUID = UUID.fromString("fa23c4d5-6789-012a-345b-6c7d8e9f012a");
    private static final UUID DAMAGE_MODIFIER_UUID = UUID.fromString("fa23c4d5-6789-012a-345b-6c7d8e9f012b");
    private static final UUID DEBUFF_ARMOR_UUID = UUID.fromString("fa23c4d5-6789-012a-345b-6c7d8e9f012c");
    private static final UUID DEBUFF_TOUGHNESS_UUID = UUID.fromString("fa23c4d5-6789-012a-345b-6c7d8e9f012d");
    private static final ResourceLocation SHADER = new ResourceLocation(Primed_Soul.MODID, "shaders/post/ignis_debuff.json");

    public LavateinItem(Tier tier, int attackDamageIn, float attackSpeedIn, Properties properties) {
        super(tier, attackDamageIn, attackSpeedIn, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        CompoundTag tag = stack.getOrCreateTag();
        int stage = tag.getInt("Stage");
        int integrity = tag.getInt("Integrity");

        tag.putLong("LastHitTime", attacker.level().getGameTime());

        if (stage < 2) {
            integrity++;
            if (integrity >= 15) {
                tag.putInt("Stage", stage + 1);
                tag.putInt("Integrity", 0);
                attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                        SoundEvents.SHIELD_BREAK, net.minecraft.sounds.SoundSource.PLAYERS, 1.2F, 1.4F);
            } else {
                tag.putInt("Integrity", integrity);
            }
        } else {
            target.setSecondsOnFire(8);
            com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.HealBlockManager.applyHealBlock(target, 60);
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
        if (level.isClientSide() || !(entity instanceof LivingEntity living)) {
            return;
        }

        CompoundTag tag = stack.getOrCreateTag();
        int stage = tag.getInt("Stage");
        long lastHit = tag.getLong("LastHitTime");
        long currentTime = level.getGameTime();

        if (stage > 0 && currentTime - lastHit > 100) {
            tag.putInt("Stage", stage - 1);
            tag.putInt("Integrity", 0);
            tag.putLong("LastHitTime", currentTime);
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.ANVIL_LAND, net.minecraft.sounds.SoundSource.PLAYERS, 0.6F, 1.8F);
        }

        if (isSelected) {
            applyDebuffArea(level, living, stage);
            applyStageDebuff(living, stage);
            if (stage >= 2) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 1, false, false, false));
            }
            if (living instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToPlayer(
                        new com.maxwell.cataclysm_primed_soul.network.packet.MessageSyncIgnisDebuff(stage + 1), serverPlayer);
            }
        } else {
            removeAreaDebuff(living);
            if (living instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                com.maxwell.cataclysm_primed_soul.network.ModMessages.sendToPlayer(
                        new com.maxwell.cataclysm_primed_soul.network.packet.MessageSyncIgnisDebuff(0), serverPlayer);
            }
        }
    }

    private void applyDebuffArea(Level level, LivingEntity holder, int stage) {
        double range = stage == 0 ? 10.0D : (stage == 1 ? 17.0D : 25.0D);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, holder.getBoundingBox().inflate(range));
        for (LivingEntity target : targets) {
            applyStageDebuff(target, stage);
            if (target != holder && target.isAlive() && target.tickCount % 5 == 0) {
                if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    double px = target.getRandomX(0.5D);
                    double py = target.getRandomY();
                    double pz = target.getRandomZ(0.5D);
                    serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME, px, py, pz, 1, 0.0D, 0.02D, 0.0D, 0.0D);
                }
            }
        }
    }

    private void removeAreaDebuff(LivingEntity holder) {
        removeDebuffFromEntity(holder);
        List<LivingEntity> targets = holder.level().getEntitiesOfClass(LivingEntity.class, holder.getBoundingBox().inflate(25.0D));
        for (LivingEntity target : targets) {
            removeDebuffFromEntity(target);
        }
    }
    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept(new net.minecraftforge.client.extensions.common.IClientItemExtensions() {
            private LavateinRenderer renderer = null;

            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new LavateinRenderer();
                }
                return renderer;
            }
        });
    }
    private void applyStageDebuff(LivingEntity entity, int stage) {
        removeDebuffFromEntity(entity);
        double armorMod = 0;
        double toughnessMod = 0;
        if (stage == 0) {
            armorMod = -0.15D;
        } else if (stage == 1) {
            armorMod = -0.35D;
            toughnessMod = -0.15D;
        } else if (stage == 2) {
            armorMod = -0.55D;
            toughnessMod = -0.30D;
        }
        var armorAttr = entity.getAttribute(Attributes.ARMOR);
        if (armorAttr != null && armorMod != 0) {
            armorAttr.addTransientModifier(new AttributeModifier(DEBUFF_ARMOR_UUID, "Laevateinn Armor Debuff", armorMod, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        var toughnessAttr = entity.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (toughnessAttr != null && toughnessMod != 0) {
            toughnessAttr.addTransientModifier(new AttributeModifier(DEBUFF_TOUGHNESS_UUID, "Laevateinn Toughness Debuff", toughnessMod, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

    private void removeDebuffFromEntity(LivingEntity entity) {
        var armorAttr = entity.getAttribute(Attributes.ARMOR);
        if (armorAttr != null) {
            armorAttr.removeModifier(DEBUFF_ARMOR_UUID);
        }
        var toughnessAttr = entity.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (toughnessAttr != null) {
            toughnessAttr.removeModifier(DEBUFF_TOUGHNESS_UUID);
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modifiers = super.getAttributeModifiers(slot,stack);
        if (slot == EquipmentSlot.MAINHAND) {
            CompoundTag tag = stack.getTag();
            int stage = tag != null ? tag.getInt("Stage") : 0;
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.putAll(modifiers);

            double speedBonus = stage == 0 ? -1.2D : (stage == 1 ? 0.0D : 1.4D);
            double damageBonus = stage == 0 ? 5.0D : (stage == 1 ? 2.0D : -3.0D);

            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER_UUID, "Laevateinn Speed Mod", speedBonus, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(DAMAGE_MODIFIER_UUID, "Laevateinn Damage Mod", damageBonus, AttributeModifier.Operation.ADDITION));
            return builder.build();
        }
        return modifiers;
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return false;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("item.cataclysm_primed_soul.lavatein.desc").withStyle(ChatFormatting.DARK_GREEN));
        tooltip.add(Component.translatable("item.cataclysm_primed_soul.lavatein.desc2").withStyle(ChatFormatting.DARK_GREEN));
    }

    @Override
    public ResourceLocation getDebuffShader(ItemStack stack) {
        return SHADER;
    }

    @Override
    public int getDebuffLevel(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getInt("Stage") : 0;
    }
}