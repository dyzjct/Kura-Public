package me.windyteam.kura.event.events.render;
import me.windyteam.kura.event.EventStage;
public class PerspectiveEvent extends EventStage
{
    private float aspect;
    
    public PerspectiveEvent(final float f) {
        this.aspect = f;
    }
    
    public float getAspect() {
        return this.aspect;
    }
    
    public void setAspect(final float f) {
        this.aspect = f;
    }
}
