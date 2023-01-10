package me.windyteam.kura.event.events.client;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class ConnectEvent extends Event {
    public String name;

    public ConnectEvent(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
