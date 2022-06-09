package net.hkva.discord.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

public interface ServerMessageCallback {

    Event<ServerMessageCallback> EVENT = EventFactory.createArrayBacked(ServerMessageCallback.class,
            (listeners) -> (server, text) -> {
                for (ServerMessageCallback listener : listeners) {
                    listener.dispatch(server, text);
                }
            });

    void dispatch(MinecraftServer server, Text text);

}