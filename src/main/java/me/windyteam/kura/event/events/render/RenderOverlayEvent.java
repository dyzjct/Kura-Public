package me.windyteam.kura.event.events.render;

import me.windyteam.kura.event.EventStage;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public final class RenderOverlayEvent
        extends EventStage {

    private OverlayType type;


    public RenderOverlayEvent(final int stage, OverlayType type) {
        super(stage);
        this.type = type;
    }

    public RenderOverlayEvent(OverlayType type) {
        this.type = type;
    }

    public static /* synthetic */ RenderOverlayEvent copy$default(RenderOverlayEvent renderOverlayEvent, OverlayType overlayType, int n, Object object) {
        if ((n & 1) != 0) {
            overlayType = renderOverlayEvent.type;
        }
        return renderOverlayEvent.copy(overlayType);
    }

    public final OverlayType getType() {
        return this.type;
    }

    public final void setType(OverlayType overlayType) {
        this.type = overlayType;
    }

    public final OverlayType component1() {
        return this.type;
    }

    public final RenderOverlayEvent copy(OverlayType type) {
        return new RenderOverlayEvent(type);
    }

    public String toString() {
        return "RenderOverlayEvent(type=" + this.type + ")";
    }

    public int hashCode() {
        OverlayType overlayType = this.type;
        return overlayType != null ? ((Object) overlayType).hashCode() : 0;
    }

    public boolean equals(Object object) {
        block3:
        {
            block2:
            {
                if (this == object) break block2;
                if (!(object instanceof RenderOverlayEvent)) break block3;
                RenderOverlayEvent renderOverlayEvent = (RenderOverlayEvent) object;
            }
            return true;
        }
        return false;
    }

    public enum OverlayType {
        BLOCK,
        LIQUID,
        FIRE
    }
}

