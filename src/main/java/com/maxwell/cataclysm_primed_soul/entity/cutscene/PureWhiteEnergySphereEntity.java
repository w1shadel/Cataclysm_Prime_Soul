package com.maxwell.cataclysm_primed_soul.entity.cutscene;

import com.maxwell.cataclysm_primed_soul.init.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PureWhiteEnergySphereEntity extends Entity {
    public static final int DETONATION_TICK = 80;
    public static final int LIFETIME = 94;
    private double altarY;

    public PureWhiteEnergySphereEntity(EntityType<? extends PureWhiteEnergySphereEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public static PureWhiteEnergySphereEntity summon(Level level, Vec3 altar) {
        PureWhiteEnergySphereEntity sphere = new PureWhiteEnergySphereEntity(ModEntities.PURE_WHITE_ENERGY_SPHERE.get(), level);
        sphere.altarY = altar.y;
        sphere.setPos(altar.x, altar.y + 8.0D, altar.z);
        level.addFreshEntity(sphere);
        return sphere;
    }

    public boolean isDetonating(float partialTick) {
        return this.tickCount + partialTick >= DETONATION_TICK;
    }

    public float detonationProgress(float partialTick) {
        return Math.min(1.0F, Math.max(0.0F, (this.tickCount + partialTick - DETONATION_TICK) / 14.0F));
    }

    @Override
    public void tick() {
        super.tick();
        this.noPhysics = true;
        this.setNoGravity(true);
        if (!this.level().isClientSide()) {
            if (this.tickCount < DETONATION_TICK) {
                float progress = this.tickCount / (float) DETONATION_TICK;
                this.setPos(this.getX(), altarY + 8.0D - 5.0D * progress * progress, this.getZ());
            } else if (this.tickCount >= LIFETIME) {
                this.discard();
            }
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        altarY = tag.getDouble("AltarY");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putDouble("AltarY", altarY);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
