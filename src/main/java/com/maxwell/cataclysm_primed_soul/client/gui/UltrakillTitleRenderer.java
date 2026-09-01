package com.maxwell.cataclysm_primed_soul.client.gui;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.network.packet.MessageUltrakillTitle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Primed_Soul.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class UltrakillTitleRenderer {
    private static String footer = "";
    private static String header = "";
    private static String mainTitle = "";
    private static int ticksRemaining;
    private static int totalDuration;

    private UltrakillTitleRenderer() {
    }

    public static void show(MessageUltrakillTitle message) {
        footer = message.subTitle() != null ? message.subTitle() : "";
        header = message.header() != null ? message.header() : "";
        mainTitle = message.mainTitle() != null ? message.mainTitle() : "";
        totalDuration = ticksRemaining = message.durationTicks();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && ticksRemaining > 0) {
            ticksRemaining--;
        }
    }

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {
        if (ticksRemaining <= 0) return;
        GuiGraphics graphics = event.getGuiGraphics();
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();
        float elapsed = totalDuration - ticksRemaining;
        float fadeOutAlpha = Mth.clamp(ticksRemaining / 15.0F, 0.0F, 1.0F);
        String topFullText;
        if (!footer.isEmpty() && !header.isEmpty()) {
            topFullText = footer + "///" + header;
        } else if (!footer.isEmpty()) {
            topFullText = footer;
        } else {
            topFullText = header;
        }
        String bottomFullText = mainTitle;
        int topLen = topFullText.length();
        int bottomLen = bottomFullText.length();
        int visibleTopChars = Mth.clamp((int) elapsed, 0, topLen);
        String visibleTopText = topFullText.substring(0, visibleTopChars);
        int bottomStartTick = topLen + 3;
        int visibleBottomChars = 0;
        if (elapsed >= bottomStartTick) {
            visibleBottomChars = Mth.clamp((int) (elapsed - bottomStartTick), 0, bottomLen);
        }
        String visibleBottomText = bottomFullText.substring(0, visibleBottomChars);
        float maxMainWidth = screenWidth * 0.88F;
        float defaultMainScale = 3.0F;
        float textRawWidth = font.width(bottomFullText);
        float mainScale = (textRawWidth * defaultMainScale > maxMainWidth && textRawWidth > 0)
                ? (maxMainWidth / textRawWidth)
                : defaultMainScale;
        float headerScale = mainScale * 0.44F;
        int startY = (int) (screenHeight * 0.16F);
        PoseStack pose = graphics.pose();
        int whiteColor = withAlpha(0xFFFFFFFF, fadeOutAlpha);
        if (!visibleTopText.isEmpty()) {
            pose.pushPose();
            pose.translate(screenWidth / 2.0F, startY, 0);
            pose.scale(headerScale, headerScale, 1.0F);
            float topFullWidth = font.width(topFullText);
            graphics.drawString(font, visibleTopText, -topFullWidth / 2.0F, 0, whiteColor, true);
            pose.popPose();
        }
        if (!visibleBottomText.isEmpty()) {
            pose.pushPose();
            int mainY = startY + (int) (font.lineHeight * headerScale) + 3;
            pose.translate(screenWidth / 2.0F, mainY, 0);
            pose.scale(mainScale, mainScale, 1.0F);
            float bottomFullWidth = font.width(bottomFullText);
            graphics.drawString(font, visibleBottomText, -bottomFullWidth / 2.0F, 0, whiteColor, true);
            pose.popPose();
        }
    }

    private static int withAlpha(int argb, float alpha) {
        int a = (int) (255 * alpha);
        return (argb & 0x00FFFFFF) | (a << 24);
    }
}