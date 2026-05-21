package com.maxwell.cataclysm_primed_soul.network.packet;

import com.maxwell.cataclysm_primed_soul.client.ClientVisuals;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageIgnisVisualEffect {
    private final int entityId;
    private final int effectType;

    public MessageIgnisVisualEffect(int entityId, int effectType) {
        this.entityId = entityId;
        this.effectType = effectType;
    }

    public static void encode(MessageIgnisVisualEffect msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeInt(msg.effectType);
    }

    public static MessageIgnisVisualEffect decode(FriendlyByteBuf buf) {
        return new MessageIgnisVisualEffect(buf.readInt(), buf.readInt());
    }

    public static void handle(MessageIgnisVisualEffect msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientVisuals.handleEffect(msg));
        });
        ctx.get().setPacketHandled(true);
    }

    public int getEntityId() {
        return entityId;
    }

    public int getEffectType() {
        return effectType;
    }
}
