package me.dyzjct.kura.module.modules.crystalaura.CrystalHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;

public class CrystalTarget {
    private BlockPos blockPos;
    private EntityLivingBase target;
    private Double selfDamage;
    private Double targetDamage;

    public CrystalTarget(BlockPos block, EntityLivingBase target, Double selfDamage, Double targetDamage) {
        this.blockPos = block;
        this.target = target;
        this.selfDamage = selfDamage;
        this.targetDamage = targetDamage;
    }


    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public EntityLivingBase getTarget(){
        return this.target;
    }

    public Double getSelfDamage() {
        return selfDamage;
    }

    public Double getTargetDamage() {
        return targetDamage;
    }

    public void update(BlockPos block, EntityLivingBase target, Double selfDamage, Double targetDamage) {
        this.blockPos = block;
        this.target = target;
        this.selfDamage = selfDamage;
        this.targetDamage = targetDamage;
    }
}