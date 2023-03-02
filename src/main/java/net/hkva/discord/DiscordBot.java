package net.hkva.discord;

import java.util.Optional;
import java.util.function.Consumer;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.hkva.discord.callback.DiscordChatCallback;

//
// Represents a connection to Discord
//
public class DiscordBot extends ListenerAdapter {

    private Optional<JDA> connection = Optional.empty();

    //
    // Connect to Discord
    //
    public void connect(final String token) throws InterruptedException, InvalidTokenException {
        final JDABuilder builder = JDABuilder.createDefault(token)
            .setStatus(OnlineStatus.ONLINE)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
            .enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS)
            .addEventListeners(this);
        connection = Optional.of(builder.build());
        connection.get().awaitReady();

        DiscordIntegrationMod.LOGGER.info("Connected");;
    }

    //
    // Disconnect from Discord
    //
    public void disconnect() {
        withConnection(c -> {
            c.shutdownNow();
            DiscordIntegrationMod.LOGGER.info("Disconnected");
        });
        connection = Optional.empty();
    }

    //
    // Do something with the connection, if available
    //
    public boolean withConnection(Consumer<JDA> action) {
        if (connection.isPresent()) {
            action.accept(connection.get());
            return false;
        }
        return false;
    }

    //
    // Returns true if the bot is connected
    //
    public boolean isConnected() {
        return connection.isPresent();
    }

    //
    // Set the "now playing" status
    //
    public void setStatus(String status) {
        withConnection(c -> c.getPresence().setActivity(Activity.playing(status)));
    }
    
    //
    // Called on Discord message
    //
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        DiscordChatCallback.EVENT.invoker().dispatch(event.getMessage());
    }
}
