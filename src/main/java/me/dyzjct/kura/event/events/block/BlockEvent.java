package me.dyzjct.kura.event.events.block;

import me.dyzjct.kura.event.events.player.UpdateWalkingPlayerEvent;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import me.dyzjct.kura.event.EventStage;

@Cancelable
public class BlockEvent extends EventStage
{
    public BlockPos pos;
    public EnumFacing facing;
    
    public BlockEvent(final int stage, final BlockPos pos, final EnumFacing facing) {
        super(stage);
        this.pos = pos;
        this.facing = facing;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public EnumFacing getFacing() {
        return this.facing;
    }
}
