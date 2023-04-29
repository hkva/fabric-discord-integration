package dev.hkva.discord.discordcommand;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.hkva.discord.DiscordCommandManager;
import dev.hkva.discord.DiscordIntegrationMod;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;

import java.awt.*;
import java.util.ArrayList;
import java.util.ListIterator;

public class ScoreboardCommand {

    public static void register(CommandDispatcher<Message> dispatcher) {
        dispatcher.register(DiscordCommandManager.literal("scoreboard")
                .then(DiscordCommandManager.literal("list").executes(ScoreboardCommand::listScoreboards))
                .then(DiscordCommandManager.argument("name", StringArgumentType.string())
                        .executes(ScoreboardCommand::displayScoreboard)));
    }

    public static int listScoreboards(CommandContext<Message> context) {
        DiscordIntegrationMod.withServer(s -> {
            final EmbedBuilder e = new EmbedBuilder();
            e.setColor(Color.GREEN);
            e.setTitle("Available scoreboards");

            for (ScoreboardObjective o : s.getScoreboard().getObjectives()) {
                e.appendDescription(String.format("%s\n", o.getName()));
            }

            context.getSource().getChannel().sendMessageEmbeds(e.build()).queue();
        });
        return 0;
    }

    public static int displayScoreboard(CommandContext<Message> context) {
        final String name = StringArgumentType.getString(context, "name");
        DiscordIntegrationMod.withServer(s -> {

            final ScoreboardObjective o = s.getScoreboard().getObjective(name);

            if (o == null) {
                final EmbedBuilder e = new EmbedBuilder();
                e.setColor(Color.RED);
                e.setTitle("Error");
                e.setDescription(String.format("No scoreboard named \"%s\" exists", name));
                context.getSource().getChannel().sendMessageEmbeds(e.build()).queue();
                return;
            }

            final EmbedBuilder e = new EmbedBuilder();

            e.setColor(Color.GREEN);
            e.setTitle(o.getDisplayName().getString());

            ArrayList<ScoreboardPlayerScore> scores = new ArrayList<>(s.getScoreboard().getAllPlayerScores(o));
            ListIterator<ScoreboardPlayerScore> itr = scores.listIterator(scores.size());

            while (itr.hasPrevious()) {
                final ScoreboardPlayerScore score = itr.previous();
                e.appendDescription(String.format("%s: %s\n", score.getPlayerName(), score.getScore()));
            }

            context.getSource().getChannel().sendMessageEmbeds(e.build()).queue();
        });
        return 0;
    }
}
