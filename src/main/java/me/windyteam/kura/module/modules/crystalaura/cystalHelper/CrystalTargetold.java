package me.windyteam.kura.module.modules.crystalaura.cystalHelper;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class CrystalTargetold {
    public BlockPos blockPos;
    public Entity target;

    public CrystalTargetold(BlockPos block, Entity target) {
        this.blockPos = block;
        this.target = target;
    }
}