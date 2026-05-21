package com.maxwell.cataclysm_primed_soul.network;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.network.packet.MessageIgnisVisualEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Primed_Soul.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(MessageIgnisVisualEffect.class, id())
                .encoder(MessageIgnisVisualEffect::encode)
                .decoder(MessageIgnisVisualEffect::decode)
                .consumerNetworkThread(MessageIgnisVisualEffect::handle)
                .add();

        net.messageBuilder(com.maxwell.cataclysm_primed_soul.network.packet.MessageSyncIgnisDebuff.class, id())
                .encoder(com.maxwell.cataclysm_primed_soul.network.packet.MessageSyncIgnisDebuff::encode)
                .decoder(com.maxwell.cataclysm_primed_soul.network.packet.MessageSyncIgnisDebuff::decode)
                .consumerNetworkThread(com.maxwell.cataclysm_primed_soul.network.packet.MessageSyncIgnisDebuff::handle)
                .add();
    }

    public static <MSG> void sendToClients(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }

    public static <MSG> void sendToPlayer(MSG message, net.minecraft.server.level.ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
