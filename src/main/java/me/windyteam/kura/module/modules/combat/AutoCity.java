package me.windyteam.kura.module.modules.combat;

import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.module.modules.misc.InstantMine;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.utils.block.BlockUtil;
import me.windyteam.kura.utils.entity.EntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

@Module.Info(name = "FeetMiner",category = Category.COMBAT)
public class AutoCity
        extends Module {
    public static EntityPlayer target;
    private final Setting<Float> range = fsetting("Range", 5.0f, 1.0f, 8.0f);


    @Override
    public void onUpdate() {
        if (AutoCity.fullNullCheck()) {
            return;
        }
        target = this.getTarget(this.range.getValue());
        if (target == null) {
            return;
        }
        BlockPos feet = new BlockPos(target.posX, target.posY, target.posZ);
        if (!this.detection(target)) {
            if (InstantMine.db.getValue()) {
                if (this.getBlock(feet.add(0, 1, 2)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, 2)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(0, 0, 1));
                } else if (this.getBlock(feet.add(0, 1, -2)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, -2)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(0, 0, -1));
                } else if (this.getBlock(feet.add(2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(2, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(1, 0, 0));
                } else if (this.getBlock(feet.add(-2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(-2, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(-1, 0, 0));
                } else if (this.getBlock(feet.add(2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(2, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(2, 0, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(2, 0, 0));
                } else if (this.getBlock(feet.add(-2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(-2, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(-2, 0, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(-2, 0, 0));
                } else if (this.getBlock(feet.add(0, 1, -2)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, -2)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 0, -2)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(0, 0, -2));
                } else if (this.getBlock(feet.add(0, 1, 2)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, 2)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 0, 2)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(0, 0, 2));
                } else if (this.getBlock(feet.add(2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(2, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(2, 0, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(2, 0, 0));
                    if (InstantMine.breakPos2 == null) {
                        this.surroundMine(feet.add(1, 0, 0));
                    }
                } else if (this.getBlock(feet.add(-2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(-2, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(-2, 0, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(-2, 0, 0));
                    if (InstantMine.breakPos2 == null) {
                        this.surroundMine(feet.add(-1, 0, 0));
                    }
                } else if (this.getBlock(feet.add(0, 1, -2)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, -2)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 0, -2)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(0, 0, -2));
                    if (InstantMine.breakPos2 == null) {
                        this.surroundMine(feet.add(0, 0, -1));
                    }
                } else if (this.getBlock(feet.add(0, 1, 2)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, 2)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 0, 2)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(0, 0, 2));
                    if (InstantMine.breakPos2 == null) {
                        this.surroundMine(feet.add(0, 0, 1));
                    }
                } else if (this.getBlock(feet.add(0, 2, 1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 1, 1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 1, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 1, 1)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(0, 1, 1));
                } else if (this.getBlock(feet.add(0, 2, 1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 1, 1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(0, 0, 1));
                } else if (this.getBlock(feet.add(0, 2, -1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 1, -1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(0, 0, -1));
                } else if (this.getBlock(feet.add(1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(1, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(1, 0, 0));
                } else if (this.getBlock(feet.add(-1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(-1, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(-1, 0, 0));
                } else if (this.getBlock(feet.add(1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(1, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(1, 1, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(1, 1, 0));
                } else if (this.getBlock(feet.add(-1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(-1, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(-1, 1, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(-1, 1, 0));
                } else if (this.getBlock(feet.add(0, 2, -1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 1, -1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 1, -1)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(0, 1, -1));
                } else if (this.getBlock(feet.add(1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(1, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(1, 1, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(1, 1, 0));
                    if (InstantMine.breakPos2 == null) {
                        this.surroundMine(feet.add(1, 0, 0));
                    }
                } else if (this.getBlock(feet.add(-1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(-1, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(-1, 1, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(-1, 1, 0));
                    if (InstantMine.breakPos2 == null) {
                        this.surroundMine(feet.add(-1, 0, 0));
                    }
                } else if (this.getBlock(feet.add(0, 2, -1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 1, -1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 1, -1)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(0, 1, -1));
                    if (InstantMine.breakPos2 == null) {
                        this.surroundMine(feet.add(0, 0, -1));
                    }
                } else if (this.getBlock(feet.add(0, 2, 1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 1, 1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 1, 1)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(0, 1, 1));
                    if (InstantMine.breakPos2 == null) {
                        this.surroundMine(feet.add(0, 0, 1));
                    }
                } else if (this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(-2, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(-2, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(-2, 1, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(-2, 1, 0));
                } else if (this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(2, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(2, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(2, 1, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(2, 1, 0));
                } else if (this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 0, 2)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 1, 2)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 1, 2)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(0, 1, 2));
                } else if (this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 0, -2)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 1, -2)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 1, -2)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(0, 1, -2));
                } else if (this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(-1, 1, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(-1, 2, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(-1, 2, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(-1, 2, 0));
                } else if (this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(1, 1, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(1, 2, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(1, 2, 0)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(1, 2, 0));
                } else if (this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 1, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 2, 1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 2, 1)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(0, 2, 1));
                } else if (this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 1, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 2, -1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 2, -1)).getBlock() != Blocks.BEDROCK) {
                    this.surroundMine(feet.add(0, 2, -1));
                }
            } else if (this.getBlock(feet.add(0, 1, 2)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, 2)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(0, 0, 1));
            } else if (this.getBlock(feet.add(0, 1, -2)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, -2)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(0, 0, -1));
            } else if (this.getBlock(feet.add(2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(2, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(1, 0, 0));
            } else if (this.getBlock(feet.add(-2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(-2, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(-1, 0, 0));
            } else if (this.getBlock(feet.add(2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(2, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(2, 0, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(2, 0, 0));
            } else if (this.getBlock(feet.add(-2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(-2, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(-2, 0, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(-2, 0, 0));
            } else if (this.getBlock(feet.add(0, 1, -2)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, -2)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 0, -2)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(0, 0, -2));
            } else if (this.getBlock(feet.add(0, 1, 2)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, 2)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 0, 2)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(0, 0, 2));
            } else if (this.getBlock(feet.add(2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(2, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(2, 0, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(2, 0, 0));
            } else if (this.getBlock(feet.add(-2, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(-2, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(-2, 0, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(-2, 0, 0));
            } else if (this.getBlock(feet.add(0, 1, -2)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, -2)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 0, -2)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(0, 0, -2));
            } else if (this.getBlock(feet.add(0, 1, 2)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, 2)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 0, 2)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(0, 0, 2));
            } else if (this.getBlock(feet.add(0, 2, 1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 1, 1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 1, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 1, 1)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(0, 1, 1));
            } else if (this.getBlock(feet.add(0, 2, 1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 1, 1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(0, 0, 1));
            } else if (this.getBlock(feet.add(0, 2, -1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 1, -1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(0, 0, -1));
            } else if (this.getBlock(feet.add(1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(1, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(1, 0, 0));
            } else if (this.getBlock(feet.add(-1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(-1, 1, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(-1, 0, 0));
            } else if (this.getBlock(feet.add(1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(1, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(1, 1, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(1, 1, 0));
            } else if (this.getBlock(feet.add(-1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(-1, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(-1, 1, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(-1, 1, 0));
            } else if (this.getBlock(feet.add(0, 2, -1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 1, -1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 1, -1)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(0, 1, -1));
            } else if (this.getBlock(feet.add(1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(1, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(1, 1, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(1, 1, 0));
            } else if (this.getBlock(feet.add(-1, 2, 0)).getBlock() == Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(-1, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(-1, 1, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(-1, 1, 0));
            } else if (this.getBlock(feet.add(0, 2, -1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 1, -1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 1, -1)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(0, 1, -1));
            } else if (this.getBlock(feet.add(0, 2, 1)).getBlock() == Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 1, 1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 1, 1)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(0, 1, 1));
            } else if (this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(-2, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(-2, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(-2, 1, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(-2, 1, 0));
            } else if (this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(2, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(2, 1, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(2, 1, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(2, 1, 0));
            } else if (this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 0, 2)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 1, 2)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 1, 2)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(0, 1, 2));
            } else if (this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 0, -2)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 1, -2)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 1, -2)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(0, 1, -2));
            } else if (this.getBlock(feet.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(-1, 1, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(-1, 2, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(-1, 2, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(-1, 2, 0));
            } else if (this.getBlock(feet.add(1, 0, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(1, 1, 0)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(1, 2, 0)).getBlock() != Blocks.AIR && this.getBlock(feet.add(1, 2, 0)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(1, 2, 0));
            } else if (this.getBlock(feet.add(0, 0, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 1, 1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 2, 1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 2, 1)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(0, 2, 1));
            } else if (this.getBlock(feet.add(0, 0, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 1, -1)).getBlock() != Blocks.BEDROCK && this.getBlock(feet.add(0, 2, -1)).getBlock() != Blocks.AIR && this.getBlock(feet.add(0, 2, -1)).getBlock() != Blocks.BEDROCK) {
                this.surroundMine(feet.add(0, 2, -1));
            }
        }
    }
    @Override
    public String getHudInfo() {
        if (target != null) {
            return target.getName();
        } else return "null";
    }

    private void surroundMine(BlockPos position) {
        if (InstantMine.breakPos2 != null && InstantMine.breakPos2.equals((Object)position)) {
            return;
        }
        if (InstantMine.breakPos != null) {
            if (InstantMine.breakPos.equals((Object)position)) {
                return;
            }
            if (InstantMine.breakPos.equals((Object)new BlockPos(AutoCity.target.posX, AutoCity.target.posY, AutoCity.target.posZ)) && AutoCity.mc.world.getBlockState(new BlockPos(AutoCity.target.posX, AutoCity.target.posY, AutoCity.target.posZ)).getBlock() != Blocks.AIR) {
                return;
            }
            if (InstantMine.breakPos.equals((Object)new BlockPos(AutoCity.mc.player.posX, AutoCity.mc.player.posY + 2.0, AutoCity.mc.player.posZ))) {
                return;
            }
            if (InstantMine.breakPos.equals((Object)new BlockPos(AutoCity.mc.player.posX, AutoCity.mc.player.posY - 1.0, AutoCity.mc.player.posZ))) {
                return;
            }
            if (AutoCity.mc.player.rotationPitch <= 90.0f && AutoCity.mc.player.rotationPitch >= 80.0f) {
                return;
            }
            if (AutoCity.mc.world.getBlockState(InstantMine.breakPos).getBlock() == Blocks.WEB) {
                return;
            }
        }
        AutoCity.mc.playerController.onPlayerDamageBlock(position, BlockUtil.getRayTraceFacing(position));
    }

    private boolean detection(EntityPlayer player) {
        return AutoCity.mc.world.getBlockState(new BlockPos(player.posX + 1.2, player.posY, player.posZ)).getBlock() == Blocks.AIR & AutoCity.mc.world.getBlockState(new BlockPos(player.posX + 1.2, player.posY + 1.0, player.posZ)).getBlock() == Blocks.AIR || AutoCity.mc.world.getBlockState(new BlockPos(player.posX - 1.2, player.posY, player.posZ)).getBlock() == Blocks.AIR & AutoCity.mc.world.getBlockState(new BlockPos(player.posX - 1.2, player.posY + 1.0, player.posZ)).getBlock() == Blocks.AIR || AutoCity.mc.world.getBlockState(new BlockPos(player.posX, player.posY, player.posZ + 1.2)).getBlock() == Blocks.AIR & AutoCity.mc.world.getBlockState(new BlockPos(player.posX, player.posY + 1.0, player.posZ + 1.2)).getBlock() == Blocks.AIR || AutoCity.mc.world.getBlockState(new BlockPos(player.posX, player.posY, player.posZ - 1.2)).getBlock() == Blocks.AIR & AutoCity.mc.world.getBlockState(new BlockPos(player.posX, player.posY + 1.0, player.posZ - 1.2)).getBlock() == Blocks.AIR || AutoCity.mc.world.getBlockState(new BlockPos(player.posX + 2.2, player.posY + 1.0, player.posZ)).getBlock() == Blocks.AIR & AutoCity.mc.world.getBlockState(new BlockPos(player.posX + 2.2, player.posY, player.posZ)).getBlock() == Blocks.AIR & AutoCity.mc.world.getBlockState(new BlockPos(player.posX + 1.2, player.posY, player.posZ)).getBlock() == Blocks.AIR || AutoCity.mc.world.getBlockState(new BlockPos(player.posX - 2.2, player.posY + 1.0, player.posZ)).getBlock() == Blocks.AIR & AutoCity.mc.world.getBlockState(new BlockPos(player.posX - 2.2, player.posY, player.posZ)).getBlock() == Blocks.AIR & AutoCity.mc.world.getBlockState(new BlockPos(player.posX - 1.2, player.posY, player.posZ)).getBlock() == Blocks.AIR || AutoCity.mc.world.getBlockState(new BlockPos(player.posX, player.posY + 1.0, player.posZ + 2.2)).getBlock() == Blocks.AIR & AutoCity.mc.world.getBlockState(new BlockPos(player.posX, player.posY, player.posZ + 2.2)).getBlock() == Blocks.AIR & AutoCity.mc.world.getBlockState(new BlockPos(player.posX, player.posY, player.posZ + 1.2)).getBlock() == Blocks.AIR || AutoCity.mc.world.getBlockState(new BlockPos(player.posX, player.posY + 1.0, player.posZ - 2.2)).getBlock() == Blocks.AIR & AutoCity.mc.world.getBlockState(new BlockPos(player.posX, player.posY, player.posZ - 2.2)).getBlock() == Blocks.AIR & AutoCity.mc.world.getBlockState(new BlockPos(player.posX, player.posY, player.posZ - 1.2)).getBlock() == Blocks.AIR;
    }

    private EntityPlayer getTarget(double range) {
        EntityPlayer target = null;
        for (EntityPlayer player : new ArrayList<>(mc.world.playerEntities)) {
            if (EntityUtil.isntValid(player, range)) continue;
            if (mc.player.getDistance(player) > range) continue;
            target = player;
            if (player != null) {
                break;
            }
            return player;
        }
        return target;
    }


    private IBlockState getBlock(BlockPos block) {
        return AutoCity.mc.world.getBlockState(block);
    }
}

