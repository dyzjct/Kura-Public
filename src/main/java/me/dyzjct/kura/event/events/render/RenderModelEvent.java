package me.dyzjct.kura.event.events.render;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class RenderModelEvent extends Event{
    public boolean rotating  = false;
    public float pitch;
}
