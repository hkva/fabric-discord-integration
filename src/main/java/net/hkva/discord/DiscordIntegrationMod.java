package net.hkva.discord;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.hkva.discord.callback.DiscordChatCallback;
import net.hkva.discord.callback.ServerMessageCallback;
import net.hkva.discord.callback.ChatMessageCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.message.MessageSourceProfile;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import javax.security.auth.login.LoginException;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.vdurmont.emoji.EmojiParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DiscordIntegrationMod implements DedicatedServerModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LogManager.getLogger("Discord");

    // Config file in memory
    public static ModConfig config = ModConfigFile.DEFAULT_CONFIG;
    // Config file on disk
    public static ModConfigFile configFile = new ModConfigFile("discord.json");

    // Discord bot
    public static DiscordBot bot = new DiscordBot();
    
    // Game server instance
    public static Optional<MinecraftServer> server = Optional.empty();

    // Discord command dispatcher
    public static DiscordCommandManager commands = new DiscordCommandManager();

    // Player count
    public static int playerCount = -1;

    //
    // Mod entry point
    //
    public void onInitializeServer() {
        if (configFile.exists()) {
            if (readConfig()) {
                try {
                    bot.connect(config.token);
                    bot.setStatus("Starting...");
                } catch (LoginException | InterruptedException e) {
                    LOGGER.warn("Failed to connect to Discord");
                }
            } else {
                LOGGER.warn("Config file is malformed. Aborting");
            }
        } else {
            LOGGER.warn("Config file doesn't exist, writing default");
            writeConfig();
        }

        // Core server events
        ServerLifecycleEvents.SERVER_STARTED.register(DiscordIntegrationMod::onServerStart);
        ServerLifecycleEvents.SERVER_STOPPED.register(DiscordIntegrationMod::onServerStop);
        ServerTickEvents.END_WORLD_TICK.register(DiscordIntegrationMod::onServerTick);
        // Command events
        CommandRegistrationCallback.EVENT.register(DiscordIntegrationMod::onRegisterCommands);
        // Game chat events
        ChatMessageCallback.EVENT.register(DiscordIntegrationMod::onGameChat);
        ServerMessageCallback.EVENT.register(DiscordIntegrationMod::onServerMessage);
        // Discord chat events
        DiscordChatCallback.EVENT.register(DiscordIntegrationMod::onDiscordChat);
        
        
        
    }

    //
    // Sync the config file from disk
    //
    public static boolean readConfig() {
        try {
            config = configFile.read();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    //
    // Sync the config file to disk
    //
    public static boolean writeConfig() {
        try {
            configFile.write(config);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    //
    // Do something with the server
    //
    public static void withServer(Consumer<MinecraftServer> action) {
        if (server.isPresent()) {
            action.accept(server.get());
        }
    }
    
    //
    // Relay a message to Discord
    //
    public static void relayToDiscord(String message) {
        bot.withConnection(c -> {
            String outgoing = message;
            for (Long channelID : config.relayChannelIDs) {
                final TextChannel channel = c.getTextChannelById(channelID);
                if (channel == null || !channel.canTalk()) {
                    LOGGER.warn("Relay channel " + channelID + " is invalid");
                    continue;
                }

                // Block mentions
                if (config.disableMentions) {
                    outgoing = outgoing.replaceAll("@", "@ ");
                }

                // Format guild emojis
                for (Emote emote : channel.getGuild().getEmotes()) {
                    final String emojiDisplay = String.format(":%s:", emote.getName());
                    final String emojiFormatted = String.format("<%s%s>", emojiDisplay, emote.getId());

                    outgoing = outgoing.replaceAll(emojiDisplay, emojiFormatted);
                }

                channel.sendMessage(outgoing).queue();
            }
        });
    }

    //
    // Called on server start
    //
    private static void onServerStart(MinecraftServer server) {
        DiscordIntegrationMod.server = Optional.of(server);
    }

    //
    // Called on server stop
    //
    private static void onServerStop(MinecraftServer server) {
        DiscordIntegrationMod.server = Optional.empty();
        bot.disconnect();
    }
    
    //
    // Called on server tick
    //
    private static void onServerTick(ServerWorld world) {
        // Update player count every 5 seconds (100 ticks)
        if (server.get().getTicks() % 100 == 0) {
            if (playerCount != server.get().getCurrentPlayerCount()) {
                playerCount = server.get().getCurrentPlayerCount();
                bot.setStatus(String.format("%d/%d players",
                    server.get().getCurrentPlayerCount(),
                    server.get().getMaxPlayerCount()));
            }
        }
    }
    
    //
    // Called on game server message
    //
    private static void onServerMessage(MinecraftServer server, Text text) {
        // Format system message
        String formatted = config.systemMessageFormat.replaceAll("\\$MESSAGE", text.getString());
        // Relay
        relayToDiscord(formatted);
    }

    //
    // Called on game chat message
    //
    private static void onGameChat(MinecraftServer server, Text text, ServerPlayerEntity sender) {
        // Format chat message
        String formatted = config.chatMessageFormat.replaceAll("\\$NAME", sender.getName().getString());
        formatted = formatted.replaceAll("\\$MESSAGE", text.getString());
        // Relay
        relayToDiscord(formatted);
    }

    //
    // Called on Discord message
    //
    private static void onDiscordChat(Message message) {
        // Wait for server
        if (server.isEmpty()) {
            return;
        }

        // Only relay messages sent in relay channels
        if (!config.relayChannelIDs.contains((Long) message.getChannel().getIdLong())) {
            return;
        }

        User author = message.getAuthor();

        // Ignore messages from bots
        if (author.isBot()) {
            return;
        }

        // Ignore pins, joins, boosts, etc
        if (message.getType() != net.dv8tion.jda.api.entities.MessageType.DEFAULT) {
            return;
        }

        // Handle commands
        if (message.getContentDisplay().startsWith(config.commandPrefix)) {
            final String messageNoPrefix = message.getContentDisplay().substring(config.commandPrefix.length());

            try {
                commands.getDispatcher().execute(messageNoPrefix, message);
            } catch (CommandSyntaxException e) {
            }

            return;
        }
        
        // Build incoming message
        String name = String.format("%s#%s", author.getName(), author.getDiscriminator());
        String formatted = config.discordMessageFormat.replaceAll("\\$NAME", name);
        String content = EmojiParser.parseToAliases(message.getContentDisplay());
        // Pad extra space for attachment names if not already empty
        if (content.length() > 0) {
            content += " ";
        }
        formatted = formatted.replaceAll("\\$MESSAGE", content);
        MutableText text = Text.literal(formatted);

        // Embed attachments as clickable text
        for (Message.Attachment attachment : message.getAttachments()) {
            final MutableText attachmentText = Text.literal(attachment.getFileName());
            attachmentText.setStyle(
                attachmentText.getStyle()
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, attachment.getUrl()))
                    .withFormatting(Formatting.GREEN)
                    .withFormatting(Formatting.UNDERLINE)
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Click to open in your web browser")))
            );
            text.append(attachmentText).append(" ");
        }

        // Forward message to all clients
        // Send to each client explicitly to prevent feedback through server console messages
        for (ServerPlayerEntity player : server.get().getPlayerManager().getPlayerList()) {
            player.sendMessage(text);
        }
    }

    //
    // Called when registering commands
    //
    private static void onRegisterCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess reg, CommandManager.RegistrationEnvironment env) {
        dispatcher.register(CommandManager.literal("discord")
            .requires(source -> source.hasPermissionLevel(4))
            .then(CommandManager.literal("loadConfig").executes(DiscordIntegrationMod::commandLoadConfig))
            .then(CommandManager.literal("status").executes(DiscordIntegrationMod::commandStatus))
            .then(CommandManager.literal("reconnect").executes(DiscordIntegrationMod::commandReconnect))
        );
    }

    //
    // discord loadConfig
    //
    private static int commandLoadConfig(CommandContext<ServerCommandSource> context) {
        String response = "Discord: Loaded config";
        if (configFile.exists()) {
            if (!readConfig()) {
                response = "Discord: Config file is malformed";
            }
        } else {
            response = "Config file doesn't exist, writing default";
            writeConfig();
        }

        context.getSource().sendFeedback(Text.of(response), true);
        return 0;
    }

    //
    // discord status
    //
    private static int commandStatus(CommandContext<ServerCommandSource> context) {
        final ServerCommandSource source = context.getSource();
        if (!bot.isConnected()) {
            source.sendFeedback(Text.of("Discord: Not connected"), false);
        } else {
            bot.withConnection(c -> {
                source.sendFeedback(Text.of("Discord: Connected"), false);
                source.sendFeedback(Text.of("Status: " + c.getStatus()), false);
            });
        }

        return 0;
    }

    //
    // disconect reconnect
    //
    private static int commandReconnect(CommandContext<ServerCommandSource> context) {
        final ServerCommandSource source = context.getSource();
        bot.disconnect();
        source.sendFeedback(Text.of("Discord: Disconnected"), true);
        try {
            bot.connect(config.token);
            source.sendFeedback(Text.of("Discord: Connected"), true);
        } catch (LoginException | InterruptedException e) {
            source.sendFeedback(Text.of("Discord: Failed to connect"), true);
        }

        return 0;
    }
}
