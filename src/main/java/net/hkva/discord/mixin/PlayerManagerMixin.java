package net.hkva.discord.mixin;

import net.hkva.discord.callback.ServerChatCallback;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;
import java.util.function.Function;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(at = @At("HEAD"), method = "broadcast (Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V")
    private void broadcast(Text text, MessageType type, UUID senderUUID, CallbackInfo ci) {
        MinecraftServer server = ((PlayerManager) (Object) this).getServer();
        ServerChatCallback.EVENT.invoker().dispatch(server, text, type, senderUUID);
    }

    @Inject(at = @At("HEAD"), method = "broadcast (Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V")
    private void broadcast(Text text, Function<ServerPlayerEntity,Text> playerMessageFactory, MessageType type, UUID senderUUID, CallbackInfo ci) {
        MinecraftServer server = ((PlayerManager) (Object) this).getServer();
        ServerChatCallback.EVENT.invoker().dispatch(server, text, type, senderUUID);
    }
}
