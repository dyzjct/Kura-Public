package me.dyzjct.kura.event.events.block;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class BlockBreakEvent extends Event {
    private final int breakerId;
    private final BlockPos position;
    private int progress;

    public BlockBreakEvent(int breakerId, BlockPos position, int progress) {
        this.breakerId = breakerId;
        this.position = position;
        this.progress = progress;
    }

    public int getBreakerId() {
        return breakerId;
    }

    public BlockPos getPosition() {
        return position;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}