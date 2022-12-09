package me.dyzjct.kura.event.events.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class RenderEvent
        extends Event {
    public Tessellator tessellator;
    public Vec3d renderPos;
    public BlockPos renderBlockPos;

    public RenderEvent(Tessellator tessellator, Vec3d renderPos) {
        this.tessellator = tessellator;
        this.renderPos = renderPos;
    }

    public RenderEvent(Tessellator tessellator, BlockPos renderPos) {
        this.tessellator = tessellator;
        this.renderBlockPos = renderPos;
    }

    public Tessellator getTessellator() {
        return this.tessellator;
    }

    public BufferBuilder getBuffer() {
        return this.tessellator.getBuffer();
    }

    public Vec3d getRenderPos() {
        return this.renderPos;
    }

    public void setTranslation(Vec3d translation) {
        this.getBuffer().setTranslation(-translation.x, -translation.y, -translation.z);
    }

    public void resetTranslation() {
        this.setTranslation(this.renderPos);
    }
}

