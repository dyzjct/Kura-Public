package me.dyzjct.kura.module.modules.crystalaura.CrystalHelper;

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