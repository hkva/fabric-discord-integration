package dev.hkva.discord.mixin;

import dev.hkva.discord.callback.ChatMessageCallback;
import dev.hkva.discord.callback.ServerMessageCallback;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    // Called when the server sends a message (player join/leave, death messages, advancements)
    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V")
    private void broadcast(Text message, boolean overlay, CallbackInfo ci) {
        MinecraftServer server = ((PlayerManager) (Object) this).getServer();
        ServerMessageCallback.EVENT.invoker().dispatch(server, message);
    }

    // Called when a player sends a chat message
    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)V")
    private void broadcast(SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters params, CallbackInfo ci) {
        MinecraftServer server = ((PlayerManager) (Object) this).getServer();
        ChatMessageCallback.EVENT.invoker().dispatch(server, message.getContent(), sender);
    }
}
