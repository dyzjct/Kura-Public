package me.windyteam.kura.event.events.block;

import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.util.math.*;
import net.minecraft.util.*;

@Cancelable
public class BlockEvents extends Event
{
    private final BlockPos pos;
    private final EnumFacing facing;

    public BlockEvents(final BlockPos pos, final EnumFacing facing) {
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
