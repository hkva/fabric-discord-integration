package net.hkva.discord.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

import java.util.UUID;

public interface ServerChatCallback {

    Event<ServerChatCallback> EVENT = EventFactory.createArrayBacked(ServerChatCallback.class,
            (listeners) -> (server, text, type, senderUUID) -> {
                for (ServerChatCallback listener : listeners) {
                    listener.dispatch(server, text, type, senderUUID);
                }
            });

    void dispatch(MinecraftServer server, Text text, MessageType type, UUID senderUUID);

}