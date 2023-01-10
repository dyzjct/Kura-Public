package me.windyteam.kura.event.events.client;

import me.windyteam.kura.event.EventStage;
import me.windyteam.kura.event.EventStage;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class PacketEvents
        extends EventStage {
    public Packet<?> packet;

    public PacketEvents(int stage, Packet<?> packet) {
        super(stage);
        this.packet = packet;
    }


    public <T extends Packet<?>> T getPacket() {
        return (T) this.packet;
    }

    @Cancelable
    public static class Send extends PacketEvents {
        public Send(int stage, Packet<?> packet) {
            super(stage, packet);
        }
    }

    @Cancelable
    public static class Receive extends PacketEvents {
        public Receive(int stage, Packet<?> packet) {
            super(stage, packet);
        }
    }
}

