package dev.ben.commandback;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class CommandBack implements ModInitializer {
    private static final String[] GAME_MODES = {
            "survival", "creative", "adventure", "spectator"
    };
    private static final String[] DIFFICULTIES = {
            "peaceful", "easy", "normal", "hard"
    };

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            // Extend the vanilla commands with numeric arguments.
            registerNumberedCommand(dispatcher, "gamemode", "gamemode", GAME_MODES);
            registerNumberedCommand(dispatcher, "defaultgamemode", "defaultgamemode", GAME_MODES);
            registerNumberedCommand(dispatcher, "difficulty", "difficulty", DIFFICULTIES);

            // Short game-mode command: /gm 0..3 and /gm s|c|a|sp.
            registerNumberedCommand(dispatcher, "gm", "gamemode", GAME_MODES);
            registerNamedGameModeAliases(dispatcher);
            registerFixedGameModeAlias(dispatcher, "gms", "survival");
            registerFixedGameModeAlias(dispatcher, "gmc", "creative");
            registerFixedGameModeAlias(dispatcher, "gma", "adventure");
            registerFixedGameModeAlias(dispatcher, "gmsp", "spectator");

            // Short default-game-mode command: /dgm 0..3.
            registerNumberedCommand(dispatcher, "dgm", "defaultgamemode", GAME_MODES);

            // Legacy weather toggle command.
            dispatcher.register(literal("toggledownfall")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        ServerCommandSource source = context.getSource();
                        String weatherCommand = source.getWorld().isRaining()
                                ? "weather clear"
                                : "weather rain";
                        return runVanilla(source, weatherCommand);
                    }));
        });
    }

    private static void registerNumberedCommand(
            CommandDispatcher<ServerCommandSource> dispatcher,
            String registeredRoot,
            String vanillaRoot,
            String[] names
    ) {
        LiteralArgumentBuilder<ServerCommandSource> root = literal(registeredRoot);
        if (!registeredRoot.equals("gamemode")
                && !registeredRoot.equals("defaultgamemode")
                && !registeredRoot.equals("difficulty")) {
            root.requires(source -> source.hasPermissionLevel(2));
        }

        for (int i = 0; i < names.length; i++) {
            final String translatedName = names[i];
            root.then(literal(Integer.toString(i))
                    .executes(context -> runVanilla(
                            context.getSource(), vanillaRoot + " " + translatedName))
                    .then(argument("targets", StringArgumentType.greedyString())
                            .executes(context -> runVanilla(
                                    context.getSource(),
                                    vanillaRoot + " " + translatedName + " "
                                            + StringArgumentType.getString(context, "targets")))));
        }

        dispatcher.register(root);
    }

    private static void registerNamedGameModeAliases(
            CommandDispatcher<ServerCommandSource> dispatcher
    ) {
        String[][] aliases = {
                {"s", "survival"},
                {"c", "creative"},
                {"a", "adventure"},
                {"sp", "spectator"}
        };

        LiteralArgumentBuilder<ServerCommandSource> gm = literal("gm")
                .requires(source -> source.hasPermissionLevel(2));

        for (String[] alias : aliases) {
            gm.then(literal(alias[0])
                    .executes(context -> runVanilla(
                            context.getSource(), "gamemode " + alias[1]))
                    .then(argument("targets", StringArgumentType.greedyString())
                            .executes(context -> runVanilla(
                                    context.getSource(),
                                    "gamemode " + alias[1] + " "
                                            + StringArgumentType.getString(context, "targets")))));
        }
        dispatcher.register(gm);

        // Also accept the old one-letter forms after /gamemode.
        LiteralArgumentBuilder<ServerCommandSource> gameMode = literal("gamemode");
        for (String[] alias : aliases) {
            gameMode.then(literal(alias[0])
                    .executes(context -> runVanilla(
                            context.getSource(), "gamemode " + alias[1]))
                    .then(argument("targets", StringArgumentType.greedyString())
                            .executes(context -> runVanilla(
                                    context.getSource(),
                                    "gamemode " + alias[1] + " "
                                            + StringArgumentType.getString(context, "targets")))));
        }
        dispatcher.register(gameMode);
    }

    private static void registerFixedGameModeAlias(
            CommandDispatcher<ServerCommandSource> dispatcher,
            String alias,
            String gameMode
    ) {
        dispatcher.register(literal(alias)
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> runVanilla(
                        context.getSource(), "gamemode " + gameMode))
                .then(argument("targets", StringArgumentType.greedyString())
                        .executes(context -> runVanilla(
                                context.getSource(),
                                "gamemode " + gameMode + " "
                                        + StringArgumentType.getString(context, "targets")))));
    }

    private static int runVanilla(ServerCommandSource source, String command)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        return source.getServer().getCommandManager().executeWithPrefix(source, command);
    }
}
