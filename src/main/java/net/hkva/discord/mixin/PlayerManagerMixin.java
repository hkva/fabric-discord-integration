package net.hkva.discord.mixin;

import net.hkva.discord.callback.ServerChatCallback;
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

    // Need to hook PlayerManager.broadcast
    // This func has a lot of overloads
    // method_44166 Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/server/filter/FilteredMessage;Lnet/minecraft/server/command/ServerCommandSource;Lnet/minecraft/util/registry/RegistryKey;)V
    //      Called when the server is sending a message via /say
    // method_43514 Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/util/registry/RegistryKey;)V
    //      Called when displaying system messages (player join/leave, advancements)
    // method_43674 Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/network/message/MessageSender;Lnet/minecraft/util/registry/RegistryKey;)V
    //      Can't figure out when this is called
    // method_43513 Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/network/message/SignedMessage;Ljava/util/function/Function;Lnet/minecraft/network/message/MessageSender;Lnet/minecraft/util/registry/RegistryKey;)V
    //      Called on player chat
    // method_43673 (Lnet/minecraft/server/filter/FilteredMessage;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/util/registry/RegistryKey;)V
    //      Haven't tested
    // method_43512 (Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/util/registry/RegistryKey;)V
    //      Haven't tested

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/util/registry/RegistryKey;)V")
    private void broadcast(Text message, RegistryKey<MessageType> typeKey, CallbackInfo ci) {
        MinecraftServer server = ((PlayerManager) (Object) this).getServer();
        // Highlight system messages
        Text t = Text.of(String.format("**%s**", message.getString()));
        ServerChatCallback.EVENT.invoker().dispatch(server, t, typeKey, null);
    }

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/network/message/SignedMessage;Ljava/util/function/Function;Lnet/minecraft/network/message/MessageSender;Lnet/minecraft/util/registry/RegistryKey;)V")
    private void broadcast(SignedMessage message, Function<ServerPlayerEntity, SignedMessage> playerMessageFactory, MessageSender sender, RegistryKey<MessageType> typeKey, CallbackInfo ci) {
        MinecraftServer server = ((PlayerManager) (Object) this).getServer();
        // Format chat messages like the previous version did
        Text t = Text.of(String.format("**<%s>** %s", sender.name().getString(), message.getContent().getString()));
        ServerChatCallback.EVENT.invoker().dispatch(server, t, typeKey, sender.uuid());
    }
}
