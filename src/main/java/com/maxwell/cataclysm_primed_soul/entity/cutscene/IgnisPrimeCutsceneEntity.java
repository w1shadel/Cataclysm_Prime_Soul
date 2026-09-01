package com.maxwell.cataclysm_primed_soul.entity.cutscene;

import com.finderfeed.cataclysm_custscenes.CatCutUtil;
import com.finderfeed.fdlib.init.FDScreenEffects;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.network.FDPacketHandler;
import com.finderfeed.fdlib.systems.cutscenes.CameraPos;
import com.finderfeed.fdlib.systems.cutscenes.CutsceneData;
import com.finderfeed.fdlib.systems.cutscenes.EasingType;
import com.finderfeed.fdlib.systems.impact_frames.ImpactFrame;
import com.finderfeed.fdlib.systems.impact_frames.ImpactFramesPacket;
import com.finderfeed.fdlib.systems.screen.screen_effect.instances.datas.ScreenColorData;
import com.finderfeed.fdlib.systems.shake.DefaultShakePacket;
import com.finderfeed.fdlib.systems.shake.FDShakeData;
import com.finderfeed.fdlib.util.FDTargetFinder;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.github.L_Ender.cataclysm.client.particle.Options.RingParticleOptions;
import com.github.L_Ender.cataclysm.client.particle.RingParticle.EnumRingBehavior;
import com.github.L_Ender.cataclysm.entity.effect.Cm_Falling_Block_Entity;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.ignis_prime.Ignis_PrimeEntity;
import com.maxwell.cataclysm_primed_soul.init.ModEntities;
import com.maxwell.cataclysm_primed_soul.network.UltrakillTitleAPI;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;

import java.util.List;

public class IgnisPrimeCutsceneEntity extends Ignis_PrimeEntity implements AutoSerializable {
    @SerializableField
    private Vec3 altarCenterPos;
    private GlobalPos homepos;

    public IgnisPrimeCutsceneEntity(EntityType<? extends IgnisPrimeCutsceneEntity> entity, Level world) {
        super(entity, world);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public static IgnisPrimeCutsceneEntity summon(Level level, Vec3 pos, GlobalPos homePos) {
        IgnisPrimeCutsceneEntity cutscene = new IgnisPrimeCutsceneEntity(ModEntities.IGNIS_PRIME_CUTSCENE.get(), level);
        cutscene.altarCenterPos = pos;
        cutscene.homepos = homePos;
        cutscene.setPos(pos.x, pos.y + 8.0D, pos.z);
        cutscene.setInvisible(true);
        cutscene.lookAt(EntityAnchorArgument.Anchor.FEET, pos.add(0, 0, -100));
        cutscene.setYRot(180.0F);
        cutscene.yBodyRot = 180.0F;
        cutscene.yHeadRot = 180.0F;
        cutscene.yRotO = 180.0F;
        cutscene.yBodyRotO = 180.0F;
        cutscene.yHeadRotO = 180.0F;
        PureWhiteEnergySphereEntity.summon(level, pos);
        var affected = CatCutUtil.startCutsceneForPlayers((ServerLevel) level, pos, 60, 200, createCutsceneData(pos));
        var inSurvival = affected.stream().filter(p -> !p.isCreative() && !p.isSpectator()).toList();
        for (int i = 0; i < inSurvival.size(); i++) {
            float p = ((float) i / Math.max(1, inSurvival.size()));
            float angle = FDMathUtil.FPI * 2f * p;
            Vec3 optimalPos = pos.add(new Vec3(-14, 0, 0).yRot(angle));
            ServerPlayer player = inSurvival.get(i);
            player.teleportTo(optimalPos.x, optimalPos.y, optimalPos.z);
            player.lookAt(EntityAnchorArgument.Anchor.FEET, pos.add(0, 3, 0));
        }
        level.addFreshEntity(cutscene);
        return cutscene;
    }

    private static CutsceneData createCutsceneData(Vec3 pos) {
        CutsceneData part1 = CutsceneData.create()
                .addScreenEffect(0, FDScreenEffects.SCREEN_COLOR, new ScreenColorData(0, 0, 0, 1), 0, 0, 20)
                .time(80)
                .timeEasing(EasingType.EASE_IN_OUT);
        CameraPos lastPos = null;
        int c = 16;
        float startAngle = FDMathUtil.FPI * 0.5f;
        float sweepAngle = FDMathUtil.FPI * 1.5f;
        for (int i = 0; i <= c; i++) {
            float p = i / (c - 1f);
            float currentAngle = startAngle + sweepAngle * p;
            Vec3 v = new Vec3(0, 0, -1).yRot(currentAngle);
            float radius = FDMathUtil.lerp(14, 5.0f, p);
            float height = FDMathUtil.lerp(6, 2.5f, p);
            Vec3 camPos = pos.add(v.scale(radius)).add(0, height, 0);
            Vec3 lookTarget = pos.add(0, FDMathUtil.lerp(8, 3, p), 0);
            Vec3 look = lookTarget.subtract(camPos).normalize();
            part1.addCameraPos(lastPos = new CameraPos(camPos, look));
        }
        CutsceneData part2 = CutsceneData.create()
                .time(50)
                .timeEasing(EasingType.EASE_OUT)
                .addScreenEffect(0, FDScreenEffects.SCREEN_COLOR, new ScreenColorData(1f, 1f, 1f, 1f), 0, 8, 25);
        Vec3 landingCamPos = pos.add(0, 1.2, -8.5);
        Vec3 landingLook = new Vec3(0, 0.35, 1).normalize();
        part2.addCameraPos(lastPos);
        part2.addCameraPos(new CameraPos(landingCamPos, landingLook));
        part1.nextCutscene(part2);
        return part1;
    }

    @Override
    public void tick() {
        this.setTarget(null);
        super.tick();
        this.noPhysics = true;
        this.setNoGravity(true);
        Vec3 center = this.altarCenterPos != null ? this.altarCenterPos : this.position();
        if (!this.level().isClientSide() && this.tickCount == 20) {
            UltrakillTitleAPI.sendToNearbyPlayers((ServerLevel) this.level(), center, 60.0D,
                    "IGNIS", "RE-IGNITED", "PRIME", 110);
        }
        this.setYRot(180.0F);
        this.yBodyRot = 180.0F;
        this.yHeadRot = 180.0F;
        if (this.tickCount < 80) {
            this.setInvisible(true);
            float progress = this.tickCount / 80.0F;
            double currentY = center.y + FDMathUtil.lerp(8.0, 3.0, progress * progress);
            this.setPos(center.x, currentY, center.z);

        }
        if (this.tickCount == 80) {
            this.setInvisible(false);
            if (!this.level().isClientSide()) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 5.0F, 0.6F);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 4.0F, 1.8F);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 4.0F, 0.8F);
                ImpactFramesPacket impactPacket = new ImpactFramesPacket(List.of(
                        new ImpactFrame().setDuration(2),
                        new ImpactFrame().setDuration(1).setInverted(true),
                        new ImpactFrame().setDuration(1)
                ));
                for (var player : FDTargetFinder.getEntitiesInCylinder(ServerPlayer.class, level(), center, 50, 40)) {
                    FDPacketHandler.INSTANCE.sendTo(impactPacket, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                    FDPacketHandler.INSTANCE.sendTo(new DefaultShakePacket(FDShakeData.builder().inTime(5).outTime(20).amplitude(2.0f).build()),
                            player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                }
                this.setDeltaMovement(0.0D, 1.4D, 0.0D);
                this.setAttackState(STATE_JUMP_START);
            }
        }
        if (this.tickCount > 80 && this.tickCount < 110) {
            if (this.tickCount == 95) {
                if (!this.level().isClientSide()) {
                    this.setAttackState(STATE_JUMP_FALL_LOOP);
                    this.setDeltaMovement(0.0D, -1.8D, 0.0D);
                }
            } else if (this.tickCount < 95) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, 0.8, 0.8));
            }
        }
        if (this.tickCount == 110) {
            this.setPos(center.x, center.y + 0.5D, center.z);
            this.setDeltaMovement(Vec3.ZERO);
            if (!this.level().isClientSide()) {
                this.setAttackState(STATE_JUMP_END);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 4.0F, 0.5F);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 3.0F, 0.6F);
                for (var player : FDTargetFinder.getEntitiesInCylinder(ServerPlayer.class, level(), center, 50, 40)) {
                    FDPacketHandler.INSTANCE.sendTo(new DefaultShakePacket(FDShakeData.builder().inTime(0).outTime(30).amplitude(1.8f).build()),
                            player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                }
                this.spawnFallingBlockShockwave(center, 8);
            } else {
                this.level().addParticle(new RingParticleOptions(
                        0.0F, ((float) Math.PI / 2F), 40, 255, 255, 255, 1.0F, 40.0F, false, EnumRingBehavior.GROW.ordinal()
                ), center.x, center.y + 0.1D, center.z, 0, 0, 0);
            }
        }
        if (this.tickCount >= 130) {
            if (!this.level().isClientSide()) {
                this.remove(RemovalReason.DISCARDED);
                Ignis_PrimeEntity prime = ModEntities.IGNIS_PRIME.get().create(this.level());
                if (prime != null) {
                    prime.setPos(this.getX(), this.getY(), this.getZ());
                    prime.setYRot(180.0F);
                    prime.yBodyRot = 180.0F;
                    prime.yHeadRot = 180.0F;
                    prime.setAttackState(0);
                    this.level().addFreshEntity(prime);
                }
            }
        }
    }

    private void spawnFallingBlockShockwave(Vec3 center, int length) {
        for (int d = 1; d <= length; d++) {
            int points = d * 6;
            for (int i = 0; i < points; i++) {
                double rad = Math.toRadians((360.0 / points) * i);
                double px = center.x + Math.cos(rad) * d;
                double pz = center.z + Math.sin(rad) * d;
                BlockPos pos = BlockPos.containing(px, center.y - 1, pz);
                BlockState state = this.level().getBlockState(pos);
                if (!state.isAir() && state.getRenderShape() == RenderShape.MODEL) {
                    Cm_Falling_Block_Entity falling = new Cm_Falling_Block_Entity(this.level(), px, pos.getY() + 1.0D, pz, state, 15);
                    falling.push(0, 0.15D + (d * 0.015D), 0);
                    this.level().addFreshEntity(falling);
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        return (source.is(DamageTypes.GENERIC_KILL) || source.is(DamageTypes.FELL_OUT_OF_WORLD)) && super.hurt(source, damage);
    }
}
