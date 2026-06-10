package com.maxwell.cataclysm_primed_soul.mixin;

import com.github.L_Ender.cataclysm.blockentities.Cursed_tombstone_Entity;
import com.maxwell.cataclysm_primed_soul.entity.internal_animation_monster.ia_boss_monsters.maledictus_prime.MaledictusSummonEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Cursed_tombstone_Entity.class)
public class CursedTombstoneEntityMixin extends BlockEntity {
    public CursedTombstoneEntityMixin(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void hookLoad(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("isPrimeSummon") && tag.getBoolean("isPrimeSummon")) {
            MaledictusSummonEvent.PRIME_TOMBSTONES.add(this.worldPosition);
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void hookSave(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("isPrimeSummon", MaledictusSummonEvent.PRIME_TOMBSTONES.contains(this.worldPosition));
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putBoolean("isPrimeSummon", MaledictusSummonEvent.PRIME_TOMBSTONES.contains(this.worldPosition));
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains("isPrimeSummon") && tag.getBoolean("isPrimeSummon")) {
            MaledictusSummonEvent.PRIME_TOMBSTONES.add(this.worldPosition);
            this.getPersistentData().putBoolean("isPrimeSummon", true);
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