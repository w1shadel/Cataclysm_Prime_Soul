package com.maxwell.cataclysm_primed_soul;

import com.maxwell.cataclysm_primed_soul.network.UltrakillTitleAPI;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.Collections;

@Mod.EventBusSubscriber(modid = Primed_Soul.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class UltrakillTitleCommand {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("ultrakilltitle")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("ignis")
                                .executes(ctx -> showPreset(ctx.getSource(), Collections.singleton(ctx.getSource().getPlayerOrException()),
                                        "ACT II CLIMAX", "THIS HEAT, AN EVIL HEAT", "NETHER", 120))
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes(ctx -> showPreset(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"),
                                                "ACT II CLIMAX", "THIS HEAT, AN EVIL HEAT", "NETHER", 120))
                                )
                        )
                        .then(Commands.literal("maledictus")
                                .executes(ctx -> showPreset(ctx.getSource(), Collections.singleton(ctx.getSource().getPlayerOrException()),
                                        "SOUL SURVIVOR", "THE FORGOTTEN HERO", "CURSED TOMB", 120))
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes(ctx -> showPreset(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"),
                                                "SOUL SURVIVOR", "THE FORGOTTEN HERO", "CURSED TOMB", 120))
                                )
                        )
                        .then(Commands.literal("custom")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("duration", IntegerArgumentType.integer(10, 1200))
                                                .then(Commands.argument("header", StringArgumentType.string())
                                                        .then(Commands.argument("title", StringArgumentType.string())
                                                                .then(Commands.argument("footer", StringArgumentType.string())
                                                                        .executes(ctx -> {
                                                                            Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "targets");
                                                                            int duration = IntegerArgumentType.getInteger(ctx, "duration");
                                                                            String header = StringArgumentType.getString(ctx, "header");
                                                                            String title = StringArgumentType.getString(ctx, "title");
                                                                            String footer = StringArgumentType.getString(ctx, "footer");
                                                                            for (ServerPlayer player : targets) {
                                                                                UltrakillTitleAPI.sendToPlayer(player, header, title, footer, duration);
                                                                            }
                                                                            ctx.getSource().sendSuccess(() -> Component.literal("§a[ULTRAKILL Title] Sent to " + targets.size() + " player(s)."), true);
                                                                            return targets.size();
                                                                        })
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }

    private static int showPreset(CommandSourceStack source, Collection<ServerPlayer> targets, String header, String title, String footer, int duration) {
        for (ServerPlayer player : targets) {
            UltrakillTitleAPI.sendToPlayer(player, header, title, footer, duration);
        }
        source.sendSuccess(() -> Component.literal("§a[ULTRAKILL Title] " + title + " displayed to " + targets.size() + " player(s)."), true);
        return targets.size();
    }
}