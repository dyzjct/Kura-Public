package me.dyzjct.kura.event.events.entity;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by 086 on 16/11/2017.
 */
@Cancelable
public class EntityEvent extends Event {

    public Entity entity;
    double x, y, z;

    public Entity getEntity() {
        return entity;
    }


    public EntityEvent(Entity entity, double x, double y, double z) {
        super();
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

}
