package me.dyzjct.kura.module.modules.combat;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.module.modules.misc.InstantMine;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.NTMiku.BlockUtil;
import me.dyzjct.kura.utils.entity.EntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

@Module.Info(name = "FeetMiner",category = Category.COMBAT)
public class AutoCity2
        extends Module {
    public static EntityPlayer target;
    private final Setting<Float> range = fsetting("Range", 5.0f, 1.0f, 8.0f);





    @Override
    public void onUpdate() {
        if (AutoCity2.fullNullCheck()) {
            return;
        }
        target = this.getTarget(this.range.getValue().floatValue());
        if (target == null) {
            return;
        }
        BlockPos feet = new BlockPos(AutoCity2.target.posX, AutoCity2.target.posY, AutoCity2.target.posZ);
        if (!this.detection(target)) {
            if (InstantMine.getInstance().db.getValue().booleanValue()) {
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

    public String getDisplayInfo() {
//        if (!HUD.getInstance().moduleInfo.getValue().booleanValue()) {
//            return null;
//        }
        if (target != null) {
            return target.getName();
        }
        return null;
    }

    private void surroundMine(BlockPos position) {
        if (InstantMine.breakPos2 != null && InstantMine.breakPos2.equals((Object)position)) {
            return;
        }
        if (InstantMine.breakPos != null) {
            if (InstantMine.breakPos.equals((Object)position)) {
                return;
            }
            if (InstantMine.breakPos.equals((Object)new BlockPos(AutoCity2.target.posX, AutoCity2.target.posY, AutoCity2.target.posZ)) && AutoCity2.mc.world.getBlockState(new BlockPos(AutoCity2.target.posX, AutoCity2.target.posY, AutoCity2.target.posZ)).getBlock() != Blocks.AIR) {
                return;
            }
            if (InstantMine.breakPos.equals((Object)new BlockPos(AutoCity2.mc.player.posX, AutoCity2.mc.player.posY + 2.0, AutoCity2.mc.player.posZ))) {
                return;
            }
            if (InstantMine.breakPos.equals((Object)new BlockPos(AutoCity2.mc.player.posX, AutoCity2.mc.player.posY - 1.0, AutoCity2.mc.player.posZ))) {
                return;
            }
            if (AutoCity2.mc.player.rotationPitch <= 90.0f && AutoCity2.mc.player.rotationPitch >= 80.0f) {
                return;
            }
            if (AutoCity2.mc.world.getBlockState(InstantMine.breakPos).getBlock() == Blocks.WEB) {
                return;
            }
        }
        AutoCity2.mc.playerController.onPlayerDamageBlock(position, BlockUtil.getRayTraceFacing(position));
    }

    private boolean detection(EntityPlayer player) {
        return AutoCity2.mc.world.getBlockState(new BlockPos(player.posX + 1.2, player.posY, player.posZ)).getBlock() == Blocks.AIR & AutoCity2.mc.world.getBlockState(new BlockPos(player.posX + 1.2, player.posY + 1.0, player.posZ)).getBlock() == Blocks.AIR || AutoCity2.mc.world.getBlockState(new BlockPos(player.posX - 1.2, player.posY, player.posZ)).getBlock() == Blocks.AIR & AutoCity2.mc.world.getBlockState(new BlockPos(player.posX - 1.2, player.posY + 1.0, player.posZ)).getBlock() == Blocks.AIR || AutoCity2.mc.world.getBlockState(new BlockPos(player.posX, player.posY, player.posZ + 1.2)).getBlock() == Blocks.AIR & AutoCity2.mc.world.getBlockState(new BlockPos(player.posX, player.posY + 1.0, player.posZ + 1.2)).getBlock() == Blocks.AIR || AutoCity2.mc.world.getBlockState(new BlockPos(player.posX, player.posY, player.posZ - 1.2)).getBlock() == Blocks.AIR & AutoCity2.mc.world.getBlockState(new BlockPos(player.posX, player.posY + 1.0, player.posZ - 1.2)).getBlock() == Blocks.AIR || AutoCity2.mc.world.getBlockState(new BlockPos(player.posX + 2.2, player.posY + 1.0, player.posZ)).getBlock() == Blocks.AIR & AutoCity2.mc.world.getBlockState(new BlockPos(player.posX + 2.2, player.posY, player.posZ)).getBlock() == Blocks.AIR & AutoCity2.mc.world.getBlockState(new BlockPos(player.posX + 1.2, player.posY, player.posZ)).getBlock() == Blocks.AIR || AutoCity2.mc.world.getBlockState(new BlockPos(player.posX - 2.2, player.posY + 1.0, player.posZ)).getBlock() == Blocks.AIR & AutoCity2.mc.world.getBlockState(new BlockPos(player.posX - 2.2, player.posY, player.posZ)).getBlock() == Blocks.AIR & AutoCity2.mc.world.getBlockState(new BlockPos(player.posX - 1.2, player.posY, player.posZ)).getBlock() == Blocks.AIR || AutoCity2.mc.world.getBlockState(new BlockPos(player.posX, player.posY + 1.0, player.posZ + 2.2)).getBlock() == Blocks.AIR & AutoCity2.mc.world.getBlockState(new BlockPos(player.posX, player.posY, player.posZ + 2.2)).getBlock() == Blocks.AIR & AutoCity2.mc.world.getBlockState(new BlockPos(player.posX, player.posY, player.posZ + 1.2)).getBlock() == Blocks.AIR || AutoCity2.mc.world.getBlockState(new BlockPos(player.posX, player.posY + 1.0, player.posZ - 2.2)).getBlock() == Blocks.AIR & AutoCity2.mc.world.getBlockState(new BlockPos(player.posX, player.posY, player.posZ - 2.2)).getBlock() == Blocks.AIR & AutoCity2.mc.world.getBlockState(new BlockPos(player.posX, player.posY, player.posZ - 1.2)).getBlock() == Blocks.AIR;
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
        return AutoCity2.mc.world.getBlockState(block);
    }
}

