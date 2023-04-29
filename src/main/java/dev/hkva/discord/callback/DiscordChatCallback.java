package dev.hkva.discord.callback;

import net.dv8tion.jda.api.entities.Message;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface DiscordChatCallback {

    Event<DiscordChatCallback> EVENT = EventFactory.createArrayBacked(DiscordChatCallback.class,
            (listeners) -> (message) -> {
                for (DiscordChatCallback listener : listeners) {
                    listener.dispatch(message);
                }
            });

    void dispatch(Message message);

}
