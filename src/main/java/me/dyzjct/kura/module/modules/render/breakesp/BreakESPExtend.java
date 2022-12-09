package me.dyzjct.kura.module.modules.render.breakesp;

import net.minecraft.util.math.BlockPos;

public class BreakESPExtend {
    public final int miningPlayerEntId;
    public final BlockPos position;
    public float calcMineTime;
    public long currentTime;
    public float finalProgress;

    public BreakESPExtend(int p_i45925_1_, BlockPos p_i45925_2_, float calcMineTime, long currentTime, float finalProgress) {
        this.miningPlayerEntId = p_i45925_1_;
        this.position = p_i45925_2_;
        this.calcMineTime = calcMineTime;
        this.currentTime = currentTime;
        this.finalProgress = finalProgress;
    }

    public BlockPos getPosition() {
        return this.position;
    }
}