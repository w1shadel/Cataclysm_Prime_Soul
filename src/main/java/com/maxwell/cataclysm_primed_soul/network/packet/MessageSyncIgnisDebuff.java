package com.maxwell.cataclysm_primed_soul.network.packet;

import com.maxwell.cataclysm_primed_soul.client.ClientVisuals;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageSyncIgnisDebuff {
    private final int level;

    public MessageSyncIgnisDebuff(int level) {
        this.level = level;
    }

    public static MessageSyncIgnisDebuff decode(FriendlyByteBuf buf) {
        return new MessageSyncIgnisDebuff(buf.readInt());
    }

    public static void handle(MessageSyncIgnisDebuff msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientVisuals.setDebuffLevel(msg.level);
        });
        ctx.get().setPacketHandled(true);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.level);
    }
}
