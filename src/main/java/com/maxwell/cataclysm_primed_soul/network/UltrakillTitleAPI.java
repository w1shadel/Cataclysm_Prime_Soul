package com.maxwell.cataclysm_primed_soul.network;

import com.maxwell.cataclysm_primed_soul.network.packet.MessageUltrakillTitle;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

public final class UltrakillTitleAPI {
    private UltrakillTitleAPI() {
    }

    public static void sendToPlayer(ServerPlayer player, String prog, String mainTitle, String layer, int durationTicks) {
        sendToPlayer(player, prog, mainTitle, layer, durationTicks, 0xFFFF3333, 0xFFFFFFFF, 0xFF55FFFF);
    }

    public static void sendToPlayer(ServerPlayer player, String prog, String mainTitle, String layer,
                                    int durationTicks, int progColor, int mainColor, int layerColor) {
        ModMessages.sendToPlayer(new MessageUltrakillTitle(prog, mainTitle, layer, durationTicks,
                progColor, mainColor, layerColor), player);
    }

    public static void sendToNearbyPlayers(ServerLevel level, Vec3 pos, double range, String header,
                                           String mainTitle, String subTitle, int durationTicks) {
        sendToNearbyPlayers(level, pos, range, header, mainTitle, subTitle, durationTicks,
                0xFFFF3333, 0xFFFFFFFF, 0xFF55FFFF);
    }

    public static void sendToNearbyPlayers(ServerLevel level, Vec3 pos, double range, String header,
                                           String mainTitle, String subTitle, int durationTicks,
                                           int headerColor, int mainColor, int subColor) {
        MessageUltrakillTitle message = new MessageUltrakillTitle(header, mainTitle, subTitle, durationTicks,
                headerColor, mainColor, subColor);
        ModMessages.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                pos.x, pos.y, pos.z, range, level.dimension())), message);
    }
}
