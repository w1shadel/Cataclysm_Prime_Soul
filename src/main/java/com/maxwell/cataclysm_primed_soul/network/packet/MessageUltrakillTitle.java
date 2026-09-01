package com.maxwell.cataclysm_primed_soul.network.packet;

import com.maxwell.cataclysm_primed_soul.client.gui.UltrakillTitleRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageUltrakillTitle {
    private final String header;
    private final String mainTitle;
    private final String subTitle;
    private final int durationTicks;
    private final int headerColor;
    private final int mainColor;
    private final int subColor;

    public MessageUltrakillTitle(String header, String mainTitle, String subTitle, int durationTicks,
                                 int headerColor, int mainColor, int subColor) {
        this.header = header;
        this.mainTitle = mainTitle;
        this.subTitle = subTitle;
        this.durationTicks = Math.max(1, durationTicks);
        this.headerColor = headerColor;
        this.mainColor = mainColor;
        this.subColor = subColor;
    }

    public static void encode(MessageUltrakillTitle message, FriendlyByteBuf buffer) {
        buffer.writeUtf(message.header, 256);
        buffer.writeUtf(message.mainTitle, 256);
        buffer.writeUtf(message.subTitle, 256);
        buffer.writeVarInt(message.durationTicks);
        buffer.writeInt(message.headerColor);
        buffer.writeInt(message.mainColor);
        buffer.writeInt(message.subColor);
    }

    public static MessageUltrakillTitle decode(FriendlyByteBuf buffer) {
        return new MessageUltrakillTitle(buffer.readUtf(256), buffer.readUtf(256), buffer.readUtf(256),
                buffer.readVarInt(), buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    public static void handle(MessageUltrakillTitle message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> UltrakillTitleRenderer.show(message)));
        context.get().setPacketHandled(true);
    }

    public String header() {
        return header;
    }

    public String mainTitle() {
        return mainTitle;
    }

    public String subTitle() {
        return subTitle;
    }

    public int durationTicks() {
        return durationTicks;
    }

    public int headerColor() {
        return headerColor;
    }

    public int mainColor() {
        return mainColor;
    }

    public int subColor() {
        return subColor;
    }
}
