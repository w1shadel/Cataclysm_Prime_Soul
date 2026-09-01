package com.maxwell.cataclysm_primed_soul.network;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect;
import com.maxwell.cataclysm_primed_soul.network.packet.MessageSyncIgnisDebuff;
import com.maxwell.cataclysm_primed_soul.network.packet.MessageUltrakillTitle;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

@SuppressWarnings("removal")
public class ModMessages {
    public static final String VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
            .named(Primed_Soul.getResourceLocation("main"))
            .clientAcceptedVersions(VERSION::equals)
            .serverAcceptedVersions(VERSION::equals)
            .networkProtocolVersion(() -> VERSION)
            .simpleChannel();
    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        INSTANCE.messageBuilder(MessageIgnisVisualEffect.class, id())
                .encoder(MessageIgnisVisualEffect::encode)
                .decoder(MessageIgnisVisualEffect::decode)
                .consumerMainThread(MessageIgnisVisualEffect::handle)
                .add();
        INSTANCE.messageBuilder(MessageSyncIgnisDebuff.class, id())
                .encoder(MessageSyncIgnisDebuff::encode)
                .decoder(MessageSyncIgnisDebuff::decode)
                .consumerMainThread(MessageSyncIgnisDebuff::handle)
                .add();
        INSTANCE.messageBuilder(MessageUltrakillTitle.class, id())
                .encoder(MessageUltrakillTitle::encode)
                .decoder(MessageUltrakillTitle::decode)
                .consumerMainThread(MessageUltrakillTitle::handle)
                .add();
    }

    public static <MSG> void sendToClients(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }

    public static <MSG> void sendToPlayer(MSG message, net.minecraft.server.level.ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
