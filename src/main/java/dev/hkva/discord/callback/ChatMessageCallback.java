package dev.hkva.discord.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public interface ChatMessageCallback {

    Event<ChatMessageCallback> EVENT = EventFactory.createArrayBacked(ChatMessageCallback.class,
            (listeners) -> (server, text, sender) -> {
                for (ChatMessageCallback listener : listeners) {
                    listener.dispatch(server, text, sender);
                }
            });

    void dispatch(MinecraftServer server, Text text, ServerPlayerEntity sender);

}