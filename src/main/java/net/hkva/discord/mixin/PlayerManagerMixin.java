package net.hkva.discord.mixin;

import net.hkva.discord.callback.ChatMessageCallback;
import net.hkva.discord.callback.ServerMessageCallback;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    // Called when sending server messages (player join/leave, death messages, advancements(
    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/util/registry/RegistryKey;)V")
    private void broadcast(Text message, RegistryKey<MessageType> typeKey, CallbackInfo ci) {
        MinecraftServer server = ((PlayerManager) (Object) this).getServer();
        ServerMessageCallback.EVENT.invoker().dispatch(server, message);
    }

    // Called when a player sends a chat message
    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/network/message/SignedMessage;Ljava/util/function/Function;Lnet/minecraft/network/message/MessageSender;Lnet/minecraft/util/registry/RegistryKey;)V")
    private void broadcast(SignedMessage message, Function<ServerPlayerEntity, SignedMessage> playerMessageFactory, MessageSender sender, RegistryKey<MessageType> typeKey, CallbackInfo ci) {
        MinecraftServer server = ((PlayerManager) (Object) this).getServer();
        ChatMessageCallback.EVENT.invoker().dispatch(server, message.getContent(), sender);
    }
}
