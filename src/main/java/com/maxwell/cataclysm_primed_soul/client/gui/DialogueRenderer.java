package com.maxwell.cataclysm_primed_soul.client.gui;

import com.maxwell.cataclysm_primed_soul.Primed_Soul;
import com.maxwell.cataclysm_primed_soul.api.entity.IDialogueEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Primed_Soul.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DialogueRenderer {
    private static IDialogueEntity active;
    private static int activeIndex = -1;
    private static int lineTicks;

    private DialogueRenderer() { }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (active != null) lineTicks++;
    }

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.options.hideGui) return;
        IDialogueEntity dialogue = findDialogueTarget(mc);
        if (dialogue == null || dialogue.getMaxLines() <= 0) {
            active = null;
            activeIndex = -1;
            return;
        }

        int index = dialogue.getDialogueIndex();
        index = Math.max(0, Math.min(index, dialogue.getMaxLines() - 1));
        if (active != dialogue || activeIndex != index) {
            active = dialogue;
            activeIndex = index;
            lineTicks = 0;
        }

        Font font = mc.font;
        Component name = Component.translatable(dialogue.getNameKey());
        Component fullLineComponent = Component.translatable(dialogue.getLineKey(index));
        String fullLine = fullLineComponent.getString();
        int maxWidth = Math.max(1, event.getGuiGraphics().guiWidth() - 60);
        List<net.minecraft.util.FormattedCharSequence> lines = font.split(fullLineComponent, maxWidth);
        int lineHeight = font.lineHeight + 2;
        int boxHeight = lines.size() * lineHeight + 25;
        int boxWidth = maxWidth + 32;
        int left = (event.getGuiGraphics().guiWidth() - boxWidth) / 2;
        int top = event.getGuiGraphics().guiHeight() - 40 - boxHeight;
        GuiGraphics graphics = event.getGuiGraphics();
        graphics.fill(left, top, left + boxWidth, top + boxHeight, 0xCC08090D);
        graphics.fill(left, top, left + boxWidth, top + 2, 0xFFFFAA33);
        graphics.drawString(font, name, left + 16, top + 8, 0xFFFFAA33, true);

        int visible = Math.min(fullLine.length(), lineTicks / 2 + 1);
        if (visible > 0) {
            Component visibleComponent = Component.literal(fullLine.substring(0, visible));
            List<net.minecraft.util.FormattedCharSequence> visibleLines = font.split(visibleComponent, maxWidth);
            for (int lineIndex = 0; lineIndex < visibleLines.size(); lineIndex++) {
                graphics.drawString(font, visibleLines.get(lineIndex), left + 16,
                        top + 20 + lineIndex * lineHeight, 0xFFFFFFFF, true);
            }
        }
    }

    private static IDialogueEntity findDialogueTarget(Minecraft mc) {
        Entity crosshair = mc.crosshairPickEntity;
        if (crosshair instanceof IDialogueEntity dialogue && dialogue.isDowned()
                && mc.player.distanceToSqr(crosshair) <= 64.0D) return dialogue;
        IDialogueEntity closest = null;
        double distance = Double.MAX_VALUE;
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof IDialogueEntity dialogue && dialogue.isDowned()) {
                double candidate = mc.player.distanceToSqr(entity);
                if (candidate <= 64.0D && candidate < distance) {
                    closest = dialogue;
                    distance = candidate;
                }
            }
        }
        return closest;
    }

}
