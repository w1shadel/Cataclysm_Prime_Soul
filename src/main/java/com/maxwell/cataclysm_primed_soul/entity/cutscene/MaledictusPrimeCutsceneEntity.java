package com.maxwell.cataclysm_primed_soul.entity.cutscene;

import com.finderfeed.cataclysm_custscenes.CatCutUtil;
import com.finderfeed.fdlib.init.FDScreenEffects;
import com.finderfeed.fdlib.nbt.AutoSerializable;
import com.finderfeed.fdlib.nbt.SerializableField;
import com.finderfeed.fdlib.systems.cutscenes.CameraPos;
import com.finderfeed.fdlib.systems.cutscenes.CutsceneData;
import com.finderfeed.fdlib.systems.cutscenes.EasingType;
import com.finderfeed.fdlib.systems.screen.screen_effect.instances.datas.ScreenColorData;
import com.finderfeed.fdlib.util.ProjectileMovementPath;
import com.finderfeed.fdlib.util.math.FDMathUtil;
import com.github.L_Ender.cataclysm.init.ModSounds;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.Maledictus_PrimeEntity;
import com.maxwell.cataclysm_primed_soul.init.ModEntities;
import com.maxwell.cataclysm_primed_soul.network.UltrakillTitleAPI;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MaledictusPrimeCutsceneEntity extends Maledictus_PrimeEntity implements AutoSerializable {
    @SerializableField
    private ProjectileMovementPath movementPath;
    private GlobalPos homepos;

    public MaledictusPrimeCutsceneEntity(EntityType<? extends MaledictusPrimeCutsceneEntity> entity, Level world) {
        super(entity, world);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public static MaledictusPrimeCutsceneEntity summon(Level level, Vec3 pos, Vec3 direction, GlobalPos homeAndTombstonePos) {
        MaledictusPrimeCutsceneEntity maledictus = new MaledictusPrimeCutsceneEntity(ModEntities.MALEDICTUS_PRIME_CUTSCENE.get(), level);
        direction = direction.multiply(1, 0, 1).normalize();
        maledictus.homepos = homeAndTombstonePos;
        maledictus.movementPath = createMovementPath(pos, direction);
        maledictus.setPos(maledictus.movementPath.getPositions().get(0));
        maledictus.lookAt(EntityAnchorArgument.Anchor.FEET, pos.add(direction.scale(200)));
        level.addFreshEntity(maledictus);
        var affected = CatCutUtil.startCutsceneForPlayers((ServerLevel) level, pos, 50, 200, createCutsceneData(pos, direction));
        var inSurvival = affected.stream().filter(p -> !p.isCreative() && !p.isSpectator()).toList();
        for (int i = 0; i < inSurvival.size(); i++) {
            Vec3 offs = direction.scale(25).add(direction.yRot(FDMathUtil.FPI / 2).scale((i % 2 == 0 ? 1 : -1) * (i + 2)));
            ServerPlayer serverPlayer = inSurvival.get(i);
            serverPlayer.teleportTo(pos.x + offs.x, pos.y + offs.y, pos.z + offs.z);
            serverPlayer.lookAt(EntityAnchorArgument.Anchor.FEET, pos);
            UltrakillTitleAPI.sendToPlayer(serverPlayer,
                    "MALEDICTUS",
                    "FREEZING FREEDOM",
                    "PRIME",
                    90);
        }
        return maledictus;
    }

    private static CutsceneData createCutsceneData(Vec3 pos, Vec3 direction) {
        Vec3 initPos = pos.add(0, 16, 0).add(direction.scale(3));
        CameraPos lastCamera;
        CutsceneData cutsceneData = CutsceneData.create()
                .addScreenEffect(0, FDScreenEffects.SCREEN_COLOR, new ScreenColorData(0, 0, 0, 1f), 0, 0, 20)
                .time(30)
                .timeEasing(EasingType.EASE_IN_OUT);
        cutsceneData.addCameraPos(new CameraPos(initPos, direction.reverse()));
        cutsceneData.addCameraPos(lastCamera = new CameraPos(initPos.add(direction.scale(5)), direction.reverse()));
        CutsceneData cutsceneData1 = new CutsceneData()
                .time(40)
                .timeEasing(EasingType.EASE_IN_OUT)
                .addCameraPos(lastCamera)
                .addCameraPos(new CameraPos(lastCamera.getPos().add(direction.scale(12)).add(0, -14, 0), direction.reverse().add(0, 0.5, 0)));
        cutsceneData.nextCutscene(cutsceneData1);
        return cutsceneData;
    }

    private static ProjectileMovementPath createMovementPath(Vec3 pos, Vec3 direction) {
        ProjectileMovementPath path = new ProjectileMovementPath(20, false);
        Vec3 initPos = pos.add(0, 14, 0);
        Vec3 endPos = pos.add(direction.scale(16)).add(0, -1, 0);
        path.addPos(initPos);
        path.addPos(initPos.add(direction.scale(8)).add(0, 4, 0));
        path.addPos(endPos);
        return path;
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);
        this.noPhysics = true;
        int duration = 90;
        if (!this.level().isClientSide) {
            if (this.tickCount == 30) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.MALEDICTUS_BATTLE_CRY.get(), SoundSource.HOSTILE, 3F, 1F);
            } else if (this.tickCount >= duration) {
                Maledictus_PrimeEntity prime = ModEntities.MALEDICTUS_PRIME.get().create(this.level());
                if (prime != null) {
                    prime.setPos(this.getX(), this.getY(), this.getZ());
                    prime.setHomePos(this.homepos);
                    this.level().addFreshEntity(prime);
                }
                this.remove(RemovalReason.DISCARDED);
            }
            if (this.movementPath != null && this.tickCount > 25) {
                if (!this.movementPath.isFinished()) {
                    this.movementPath.tick(this);
                } else {
                    this.setDeltaMovement(Vec3.ZERO);
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        return (source.is(DamageTypes.GENERIC_KILL) || source.is(DamageTypes.FELL_OUT_OF_WORLD)) && super.hurt(source, damage);
    }
}
