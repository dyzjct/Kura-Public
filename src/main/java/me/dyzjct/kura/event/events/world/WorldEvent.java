package me.dyzjct.kura.event.events.world;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class WorldEvent extends Event {
    public WorldClient world;

    public WorldEvent(WorldClient world){
        this.world = world;
    }

    public void getWorld(WorldClient world){
        this.world = world;
    }
}
