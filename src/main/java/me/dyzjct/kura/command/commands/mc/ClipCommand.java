package me.dyzjct.kura.command.commands.mc;

import me.dyzjct.kura.command.Command;
import me.dyzjct.kura.command.syntax.SyntaxChunk;
import me.dyzjct.kura.event.events.client.PacketEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClipCommand extends Command {
    public int teleportID;

    public ClipCommand() {
        super("clip", SyntaxChunk.EMPTY);
    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvents.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            this.teleportID = ((SPacketPlayerPosLook) event.getPacket()).teleportId;
        }
    }

    @Override
    public void call(String[] var1) {
        double x = new Double(var1[0]);
        double y = new Double(var1[1]);
        double z = new Double(var1[2]);
        for (String arg : var1) {
            if (arg == null) continue;
            Minecraft.getMinecraft().player.connection.sendPacket(new CPacketConfirmTeleport(++teleportID));
            Minecraft.getMinecraft().player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z, mc.player.onGround));
            Minecraft.getMinecraft().player.setPosition(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
        }
    }
}
