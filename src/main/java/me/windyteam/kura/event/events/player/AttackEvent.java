package me.windyteam.kura.event.events.player;

import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.entity.*;

@Cancelable
public class AttackEvent extends Event
{
    Entity entity;
    
    public AttackEvent(final Entity attack, final int stage) {
        super();
        this.entity = attack;
    }
    
    public Entity getEntity() {
        return this.entity;
    }
}
