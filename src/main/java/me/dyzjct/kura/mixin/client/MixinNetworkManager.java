package me.dyzjct.kura.mixin.client;

import me.dyzjct.kura.event.events.client.PacketEvents;
import me.dyzjct.kura.module.ModuleManager;
import me.dyzjct.kura.module.modules.misc.NoPacketKick;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {NetworkManager.class})
public abstract class MixinNetworkManager {
    @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;)V"}, at = @At("HEAD"), cancellable = true)
    public void onSendPacketPre2(final Packet<?> packet, final CallbackInfo info) {
        PacketEvents.Send event = new PacketEvents.Send(0, packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }

    @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;)V"}, at = @At(value = "RETURN"), cancellable = true)
    public void onSendPacketPost(Packet<?> packet, CallbackInfo info) {
        PacketEvents.Send event = new PacketEvents.Send(1, packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }

    @Inject(method = {"channelRead0"}, at = @At("HEAD"), cancellable = true)
    public void onChannelReadPre2(final ChannelHandlerContext context, final Packet<?> packet, final CallbackInfo info) {
        PacketEvents.Receive event = new PacketEvents.Receive(0, packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }

    @Inject(method = {"exceptionCaught"}, at = @At(value = "HEAD"), cancellable = true)
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable, CallbackInfo ci2) {
        if (ModuleManager.getModuleByClass(NoPacketKick.class).isEnabled()) {
            ci2.cancel();
        }
    }

}