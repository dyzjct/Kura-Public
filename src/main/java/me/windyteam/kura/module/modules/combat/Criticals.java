package me.windyteam.kura.module.modules.combat;

import me.windyteam.kura.event.events.client.PacketEvents;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.Setting;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author S-B99
 * Thanks cookie uwu
 */
@Module.Info(name = "Criticals", category = Category.COMBAT, description = "Always do critical attacks")
public class Criticals extends Module {
    public Setting<mode> Mode = msetting("Mode", mode.Bypass);

    @SubscribeEvent
    public void wtf(PacketEvents.Send send) {
        if (fullNullCheck()) {
            return;
        }
        try {
            if (send.getStage() == 0) {
                if (mc.player.onGround && send.getPacket() instanceof CPacketUseEntity) {
                    if (((CPacketUseEntity) send.getPacket()).getEntityFromWorld(mc.world) instanceof EntityLivingBase && mc.player.onGround) {
                        if (((CPacketUseEntity) send.getPacket()).getEntityFromWorld(mc.world) instanceof EntityEnderCrystal) {
                            return;
                        }
                        switch (this.Mode.getValue()) {
                            case Packet: {
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.05, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.03, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                break;
                            }
                            case OldPacket: {
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.10000000149011612, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                break;
                            }
                            case MiniJump: {
                                mc.player.jump();
                                mc.player.motionY -= 0.3;
                                break;
                            }
                            case Jump: {
                                mc.player.jump();
                                break;
                            }
                            case Offset: {
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0625, mc.player.posZ, true));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.1E-5, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                break;
                            }
                            case NCP: {
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.11, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1100013579, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.3579E-6, mc.player.posZ, false));
                                break;
                            }
                            case Bypass: {
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.11, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.11, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1100013579, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1100013579, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1100013579, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1100013579, mc.player.posZ, false));
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public String getHudInfo() {
        return this.Mode.getValue().toString();
    }

    public enum mode {
        Bypass,
        NCP,
        Packet,
        Jump,
        OldPacket,
        MiniJump,
        Offset
    }
}
