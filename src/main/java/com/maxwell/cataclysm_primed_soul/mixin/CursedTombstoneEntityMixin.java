package com.maxwell.cataclysm_primed_soul.mixin;

import com.github.L_Ender.cataclysm.blockentities.Cursed_tombstone_Entity;
import com.maxwell.cataclysm_primed_soul.api.entity.IPrimeTombstone;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Cursed_tombstone_Entity.class)
public class CursedTombstoneEntityMixin extends BlockEntity implements IPrimeTombstone {
    @Unique
    private boolean cataclysm_primed_soul$isPrimeSummon;

    public CursedTombstoneEntityMixin(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Override
    @Unique
    public boolean cataclysm_primed_soul$isPrimeSummon() {
        return this.cataclysm_primed_soul$isPrimeSummon;
    }

    @Override
    @Unique
    public void cataclysm_primed_soul$setPrimeSummon(boolean val) {
        this.cataclysm_primed_soul$isPrimeSummon = val;
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void hookLoad(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("isPrimeSummon")) {
            this.cataclysm_primed_soul$isPrimeSummon = tag.getBoolean("isPrimeSummon");
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void hookSave(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("isPrimeSummon", this.cataclysm_primed_soul$isPrimeSummon);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putBoolean("isPrimeSummon", this.cataclysm_primed_soul$isPrimeSummon);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains("isPrimeSummon")) {
            this.cataclysm_primed_soul$isPrimeSummon = tag.getBoolean("isPrimeSummon");
        }
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            this.handleUpdateTag(tag);
        }
    }
}