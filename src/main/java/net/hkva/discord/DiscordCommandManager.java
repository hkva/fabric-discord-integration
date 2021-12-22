package net.hkva.discord;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.hkva.discord.discordcommand.PlayersCommand;
import net.hkva.discord.discordcommand.RconCommand;
import net.hkva.discord.discordcommand.ScoreboardCommand;

public class DiscordCommandManager {

    private final CommandDispatcher<Message> dispatcher = new CommandDispatcher<>();

    public DiscordCommandManager() {
        PlayersCommand.register(dispatcher);
        RconCommand.register(dispatcher);
        ScoreboardCommand.register(dispatcher);
    }

    public static LiteralArgumentBuilder<Message> literal(String literal) {
        return LiteralArgumentBuilder.literal(literal);
    }

    public static <T> RequiredArgumentBuilder<Message, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public CommandDispatcher<Message> getDispatcher() {
        return dispatcher;
    }

}