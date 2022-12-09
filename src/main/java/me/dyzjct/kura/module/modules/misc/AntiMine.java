package me.dyzjct.kura.module.modules.misc;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.module.modules.xddd.Surround;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.NTMiku.BlockUtil;
import me.dyzjct.kura.utils.Timer;
import me.dyzjct.kura.utils.entity.EntityUtil;
import me.dyzjct.kura.utils.inventory.InventoryUtil;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

@Module.Info(name = "AntiMine", category = Category.MISC)
public class AntiMine
        extends Module {
    static Minecraft mc = Minecraft.getMinecraft();
    private final Setting<Integer> range = isetting("Range", 8, 1, 12);
    private final Setting<Float> AntiSameHole = fsetting("AntiSameHole", Float.valueOf(2.0f), Float.valueOf(1.0f), Float.valueOf(5.0f));
    private final Setting<Boolean> rotate = bsetting("Rotate", false);
    private final Setting<Integer> delay = isetting("Delay", 0, 0, 300);
    private final Setting<Boolean> toggle = bsetting("AutoToggle", false);
    private final Timer timer = new Timer();
    private final Timer retryTimer = new Timer();
    public EntityPlayer target;
    public EntityPlayer targets;
    public Setting<Boolean> packet = bsetting("Packet", true);

    int ONE;
    int TWO;
    int THREE;
    int FOUR;
    private int obsidian = -1;
    private boolean isSneaking;
    private BlockPos startPos;
    private boolean offHand;
    private int lastHotbarSlot;
    private int isEn;

    @Override
    public void onEnable() {
        this.startPos = EntityUtil.getRoundedBlockPos(Surround.mc.player);
        new Surround();
        this.startPos = EntityUtil.getRoundedBlockPos(AntiMine.mc.player);
        this.isEn = 1;
    }

    @Override
    public void onDisable() {
        if (fullNullCheck()) return;
        this.isEn = 0;
        this.ONE = 0;
        this.TWO = 0;
        this.THREE = 0;
        this.FOUR = 0;
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) return;
//        if (this.toggle.getValue())
//            this.disable();
        if (mc.player == null || mc.world == null) {
            return;
        }
        if (this.isEn != 1) {
            this.toggle();
        }
        if (!this.startPos.equals(EntityUtil.getRoundedBlockPos(mc.player))) {
            this.toggle();
        }

        if (this.startPos != null && ((Boolean)this.toggle.getValue()).booleanValue() && !this.startPos.equals((Object)EntityUtil.getPlayerPos())) {
            this.toggle();
            return;
        }

        Vec3d a = mc.player.getPositionVector();
        this.obsidian = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        this.target = this.getTarget(this.range.getValue());
        this.targets = this.getTargets(this.AntiSameHole.getValue());
        if (this.target == null) {
            return;
        }
        if (this.targets != null) {
            return;
        }
        BlockPos feet = new BlockPos(this.target.posX, this.target.posY, this.target.posZ);
        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        if (this.obsidian == -1) {
            return;
        }
        if (this.checkCrystal(a, EntityUtil.getVarOffsets(0, 1, 1)) != null && this.checkCrystal(a, EntityUtil.getVarOffsets(0, 1, 2)) != null) {
            EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(0, 1, 1)), true);
            this.place(a, EntityUtil.getVarOffsets(0, 1, 1));
        }
        if (this.checkCrystal(a, EntityUtil.getVarOffsets(0, 1, -1)) != null && this.checkCrystal(a, EntityUtil.getVarOffsets(0, 1, -2)) != null) {
            EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(0, 1, -1)), true);
            this.place(a, EntityUtil.getVarOffsets(0, 1, -1));
        }
        if (this.checkCrystal(a, EntityUtil.getVarOffsets(1, 1, 0)) != null && this.checkCrystal(a, EntityUtil.getVarOffsets(2, 1, 0)) != null) {
            EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(1, 1, 0)), true);
            this.place(a, EntityUtil.getVarOffsets(1, 1, 0));
        }
        if (this.checkCrystal(a, EntityUtil.getVarOffsets(-1, 1, 0)) != null && this.checkCrystal(a, EntityUtil.getVarOffsets(-2, 1, 0)) != null) {
            EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(-1, 1, 0)), true);
            this.place(a, EntityUtil.getVarOffsets(-1, 1, 0));
        }
        if (this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, 1)) != null && this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, 2)) != null) {
            EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, 1)), true);
            this.place(a, EntityUtil.getVarOffsets(0, 0, 1));
        }
        if (this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, -1)) != null && this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, -2)) != null) {
            EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, -1)), true);
            this.place(a, EntityUtil.getVarOffsets(0, 0, -1));
        }
        if (this.checkCrystal(a, EntityUtil.getVarOffsets(1, 0, 0)) != null && this.checkCrystal(a, EntityUtil.getVarOffsets(2, 0, 0)) != null) {
            EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(1, 0, 0)), true);
            this.place(a, EntityUtil.getVarOffsets(1, 0, 0));
        }
        if (this.checkCrystal(a, EntityUtil.getVarOffsets(-1, 0, 0)) != null && this.checkCrystal(a, EntityUtil.getVarOffsets(-2, 0, 0)) != null) {
            EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(-1, 0, 0)), true);
            this.place(a, EntityUtil.getVarOffsets(-1, 0, 0));
        }
        if (this.getBlock(pos.add(1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(2, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(2, 1, 0)).getBlock() == Blocks.AIR) {
            if (this.checkCrystal(a, EntityUtil.getVarOffsets(2, 1, 0)) != null) {
                EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(2, 1, 0)), true);
            }
            if (!new BlockPos(feet).equals(new BlockPos(pos.add(2, 1, 0))) && !new BlockPos(feet).equals(new BlockPos(pos.add(2, 0, 0)))) {
                this.perform(pos.add(2, 1, 0));
            }
        }
        if (this.getBlock(pos.add(-1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(-2, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(-2, 1, 0)).getBlock() == Blocks.AIR) {
            if (this.checkCrystal(a, EntityUtil.getVarOffsets(-2, 1, 0)) != null) {
                EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(-2, 1, 0)), true);
            }
            if (!new BlockPos(feet).equals(new BlockPos(pos.add(-2, 1, 0))) && !new BlockPos(feet).equals(new BlockPos(pos.add(-2, 0, 0)))) {
                this.perform(pos.add(-2, 1, 0));
            }
        }
        if (this.getBlock(pos.add(0, 0, 1)).getBlock() == Blocks.AIR && this.getBlock(pos.add(0, 0, 2)).getBlock() == Blocks.AIR && this.getBlock(pos.add(0, 1, 2)).getBlock() == Blocks.AIR) {
            if (this.checkCrystal(a, EntityUtil.getVarOffsets(0, 1, 2)) != null) {
                EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(0, 1, 2)), true);
            }
            if (!new BlockPos(feet).equals(new BlockPos(pos.add(0, 1, 2))) && !new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, 2)))) {
                this.perform(pos.add(0, 1, 2));
            }
        }
        if (this.getBlock(pos.add(0, 0, -1)).getBlock() == Blocks.AIR && this.getBlock(pos.add(0, 0, -2)).getBlock() == Blocks.AIR && this.getBlock(pos.add(0, 1, -2)).getBlock() == Blocks.AIR) {
            if (this.checkCrystal(a, EntityUtil.getVarOffsets(0, 1, -2)) != null) {
                EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(0, 1, -2)), true);
            }
            if (!new BlockPos(feet).equals(new BlockPos(pos.add(0, 1, -2))) && !new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, -2)))) {
                this.perform(pos.add(0, 1, -2));
            }
        }
        if (this.getBlock(pos.add(0, 0, 1)).getBlock() == Blocks.AIR && this.getBlock(pos.add(0, -1, 1)).getBlock() == Blocks.AIR) {
            if (this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, 1)) != null) {
                EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, 1)), true);
            }
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -2, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, 1))))) {
                this.perform(pos.add(0, -1, 1));
                this.perform(pos.add(0, 0, 1));
            }
        }
        if (this.getBlock(pos.add(0, 0, -1)).getBlock() == Blocks.AIR && this.getBlock(pos.add(0, -1, -1)).getBlock() == Blocks.AIR) {
            if (this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, -1)) != null) {
                EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, -1)), true);
            }
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -2, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, -1))))) {
                this.perform(pos.add(0, -1, -1));
                this.perform(pos.add(0, 0, -1));
            }
        }
        if (this.getBlock(pos.add(1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(1, -1, 0)).getBlock() == Blocks.AIR) {
            if (this.checkCrystal(a, EntityUtil.getVarOffsets(1, 0, 0)) != null) {
                EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(1, 0, 0)), true);
            }
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, -2, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, -1, 0))))) {
                this.perform(pos.add(1, -1, 0));
                this.perform(pos.add(1, 0, 0));
            }
        }
        if (this.getBlock(pos.add(-1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(-1, -1, 0)).getBlock() == Blocks.AIR) {
            if (this.checkCrystal(a, EntityUtil.getVarOffsets(-1, 0, 0)) != null) {
                EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(-1, 0, 0)), true);
            }
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, -2, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, -1, 0))))) {
                this.perform(pos.add(-1, -1, 0));
                this.perform(pos.add(-1, 0, 0));
            }
        }
        if (this.getBlock(pos.add(0, 0, 1)).getBlock() == Blocks.AIR && this.getBlock(pos.add(0, 0, 2)).getBlock() == Blocks.AIR) {
            if (this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, 2)) != null) {
                EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, 2)), true);
            }
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, 2))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, 2))))) {
                if (this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, 2)) == null) {
                    this.perform(pos.add(0, 0, 1));
                    this.perform(pos.add(0, 0, 2));
                    this.perform(pos.add(0, -1, 2));
                    this.perform(pos.add(0, 0, 3));
                } else if (this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, 2)) != null && this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, 1)) != null) {
                    EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, 2)), true);
                    this.perform(pos.add(0, 0, 1));
                    this.perform(pos.add(0, 0, 2));
                    this.perform(pos.add(0, -1, 2));
                    this.perform(pos.add(0, 0, 3));
                }
            }
        }
        if (this.getBlock(pos.add(0, 0, -1)).getBlock() == Blocks.AIR && this.getBlock(pos.add(0, 0, -2)).getBlock() == Blocks.AIR) {
            if (this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, -2)) != null) {
                EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, -2)), true);
            }
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, -2))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, -2))))) {
                if (this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, -2)) == null) {
                    this.perform(pos.add(0, 0, -1));
                    this.perform(pos.add(0, 0, -2));
                    this.perform(pos.add(0, -1, -2));
                    this.perform(pos.add(0, 0, -3));
                } else if (this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, -2)) != null && this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, -1)) != null) {
                    EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(0, 0, -2)), true);
                    this.perform(pos.add(0, 0, -1));
                    this.perform(pos.add(0, 0, -2));
                    this.perform(pos.add(0, -1, -2));
                    this.perform(pos.add(0, 0, -3));
                }
            }
        }
        if (this.getBlock(pos.add(1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(2, 0, 0)).getBlock() == Blocks.AIR) {
            if (this.checkCrystal(a, EntityUtil.getVarOffsets(2, 0, 0)) != null) {
                EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(2, 0, 0)), true);
            }
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, -1, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(2, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(2, -1, 0))))) {
                if (this.checkCrystal(a, EntityUtil.getVarOffsets(2, 0, 0)) == null) {
                    this.perform(pos.add(1, 0, 0));
                    this.perform(pos.add(2, 0, 0));
                    this.perform(pos.add(2, -1, 0));
                    this.perform(pos.add(3, 0, 0));
                } else if (this.checkCrystal(a, EntityUtil.getVarOffsets(2, 0, 0)) != null && this.checkCrystal(a, EntityUtil.getVarOffsets(1, 0, 0)) != null) {
                    EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(2, 0, 0)), true);
                    this.perform(pos.add(1, 0, 0));
                    this.perform(pos.add(2, 0, 0));
                    this.perform(pos.add(2, -1, 0));
                    this.perform(pos.add(3, 0, 0));
                }
            }
        }
        if (this.getBlock(pos.add(-1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(-2, 0, 0)).getBlock() == Blocks.AIR) {
            if (this.checkCrystal(a, EntityUtil.getVarOffsets(-2, 0, 0)) != null) {
                EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(-2, 0, 0)), true);
            }
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, -1, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(-2, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(-2, -1, 0))))) {
                if (this.checkCrystal(a, EntityUtil.getVarOffsets(-2, 0, 0)) == null) {
                    this.perform(pos.add(-1, 0, 0));
                    this.perform(pos.add(-2, 0, 0));
                    this.perform(pos.add(-2, -1, 0));
                    this.perform(pos.add(-3, 0, 0));
                } else if (this.checkCrystal(a, EntityUtil.getVarOffsets(-2, 0, 0)) != null && this.checkCrystal(a, EntityUtil.getVarOffsets(-1, 0, 0)) != null) {
                    EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(-2, 0, 0)), true);
                    this.perform(pos.add(-1, 0, 0));
                    this.perform(pos.add(-2, 0, 0));
                    this.perform(pos.add(-2, -1, 0));
                    this.perform(pos.add(-3, 0, 0));
                }
            }
        }
        if (this.getBlock(pos.add(0, 0, 1)).getBlock() == Blocks.AIR && this.getBlock(pos.add(0, 1, 1)).getBlock() == Blocks.AIR) {
            if (!new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, 1))) && !new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, 1)))) {
                if (!new BlockPos(feet).equals(new BlockPos(pos.add(0, 1, 1))) && !new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, 1)))) {
                    this.perform(pos.add(0, 0, 1));
                    this.perform(pos.add(0, 1, 1));
                    this.perform(pos.add(1, 1, 1));
                    this.perform(pos.add(0, 1, 2));
                }
            } else if (this.getBlock(pos.add(0, 0, 2)).getBlock() == Blocks.AIR && this.getBlock(pos.add(0, 1, 2)).getBlock() == Blocks.AIR) {
                this.perform(pos.add(0, 0, 2));
                this.perform(pos.add(0, 1, 2));
                this.perform(pos.add(1, 0, 2));
                this.perform(pos.add(1, 1, 2));
            }
        }
        if (this.getBlock(pos.add(0, 0, -1)).getBlock() == Blocks.AIR && this.getBlock(pos.add(0, 1, -1)).getBlock() == Blocks.AIR) {
            if (!new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, -1))) && !new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, -1)))) {
                if (!new BlockPos(feet).equals(new BlockPos(pos.add(0, 1, -1))) && !new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, -1)))) {
                    this.perform(pos.add(0, 0, -1));
                    this.perform(pos.add(0, 1, -1));
                    this.perform(pos.add(-1, 1, -1));
                    this.perform(pos.add(0, 1, -2));
                }
            } else if (this.getBlock(pos.add(0, 0, -2)).getBlock() == Blocks.AIR && this.getBlock(pos.add(0, 1, -2)).getBlock() == Blocks.AIR) {
                this.perform(pos.add(0, 0, -2));
                this.perform(pos.add(0, 1, -2));
                this.perform(pos.add(1, 0, -2));
                this.perform(pos.add(1, 1, -2));
            }
        }
        if (this.getBlock(pos.add(1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(1, 1, 0)).getBlock() == Blocks.AIR) {
            if (!new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 0))) && !new BlockPos(feet).equals(new BlockPos(pos.add(1, -1, 0)))) {
                if (!new BlockPos(feet).equals(new BlockPos(pos.add(1, 1, 0))) && !new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 0)))) {
                    this.perform(pos.add(1, 0, 0));
                    this.perform(pos.add(1, 1, 0));
                    this.perform(pos.add(1, 1, 1));
                    this.perform(pos.add(2, 1, 0));
                }
            } else if (this.getBlock(pos.add(2, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(2, 1, 0)).getBlock() == Blocks.AIR) {
                this.perform(pos.add(2, 0, 0));
                this.perform(pos.add(2, 1, 0));
                this.perform(pos.add(2, 0, 1));
                this.perform(pos.add(2, 1, 1));
            }
        }
        if (this.getBlock(pos.add(-1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(-1, 1, 0)).getBlock() == Blocks.AIR) {
            if (!new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 0))) && !new BlockPos(feet).equals(new BlockPos(pos.add(-1, -1, 0)))) {
                if (!new BlockPos(feet).equals(new BlockPos(pos.add(-1, 1, 0))) && !new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 0)))) {
                    this.perform(pos.add(-1, 0, 0));
                    this.perform(pos.add(-1, 1, 0));
                    this.perform(pos.add(-1, 1, -1));
                    this.perform(pos.add(-2, 1, 0));
                }
            } else if (this.getBlock(pos.add(-2, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(-2, 1, 0)).getBlock() == Blocks.AIR) {
                this.perform(pos.add(-2, 0, 0));
                this.perform(pos.add(-2, 1, 0));
                this.perform(pos.add(-2, 0, 1));
                this.perform(pos.add(-2, 1, 1));
            }
        }
        if (this.getBlock(pos.add(1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(1, 0, 1)).getBlock() == Blocks.AIR) {
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, -1, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, -1, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 1))))) {
                if (this.checkCrystal(a, EntityUtil.getVarOffsets(1, 0, 1)) != null) {
                    EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(1, 0, 1)), true);
                }
                this.perform(pos.add(1,0,1));
                this.perform(pos.add(1, 0, 0));
                this.perform(pos.add(1, 0, 1));
            }
        }
        if (this.getBlock(pos.add(0, 0, 1)).getBlock() == Blocks.AIR && this.getBlock(pos.add(1, 0, 1)).getBlock() == Blocks.AIR) {
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, -1, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 1))))) {
                if (this.checkCrystal(a, EntityUtil.getVarOffsets(1, 0, 1)) != null) {
                    EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(1, 0, 1)), true);
                }
                this.perform(pos.add(1,0,1));
                this.perform(pos.add(0, 0, 1));
                this.perform(pos.add(1, 0, 1));
            }
        }
        if (this.getBlock(pos.add(-1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(-1, 0, -1)).getBlock() == Blocks.AIR) {
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, -1, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, -1, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, -1))))) {
                if (this.checkCrystal(a, EntityUtil.getVarOffsets(-1, 0, -1)) != null) {
                    EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(-1, 0, -1)), true);
                }
                this.perform(pos.add(-1,0,1));
                this.perform(pos.add(-1, 0, 0));
                this.perform(pos.add(-1, 0, -1));
            }
        }
        if (this.getBlock(pos.add(0, 0, -1)).getBlock() == Blocks.AIR && this.getBlock(pos.add(-1, 0, -1)).getBlock() == Blocks.AIR) {
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, -1, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, -1))))) {
                if (this.checkCrystal(a, EntityUtil.getVarOffsets(-1, 0, -1)) != null) {
                    EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(-1, 0, -1)), true);
                }
                this.perform(pos.add(-1,0,-1));
                this.perform(pos.add(0, 0, -1));
                this.perform(pos.add(-1, 0, -1));
            }
        }
        if (this.getBlock(pos.add(-1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(-1, 0, 1)).getBlock() == Blocks.AIR) {
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, -1, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, -1, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 1))))) {
                if (this.checkCrystal(a, EntityUtil.getVarOffsets(-1, 0, 1)) != null) {
                    EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(-1, 0, 1)), true);
                }
                this.perform(pos.add(-1,0,1));
                this.perform(pos.add(-1, 0, 0));
                this.perform(pos.add(-1, 0, 1));
            }
        }
        if (this.getBlock(pos.add(0, 0, 1)).getBlock() == Blocks.AIR && this.getBlock(pos.add(-1, 0, 1)).getBlock() == Blocks.AIR) {
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, -1, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 1))))) {
                if (this.checkCrystal(a, EntityUtil.getVarOffsets(-1, 0, 1)) != null) {
                    EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(-1, 0, 1)), true);
                }
                this.perform(pos.add(-1,0,1));
                this.perform(pos.add(0, 0, 1));
                this.perform(pos.add(-1, 0, 1));
            }
        }
        if (this.getBlock(pos.add(1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(1, 0, -1)).getBlock() == Blocks.AIR) {
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, -1, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, -1, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, -1))))) {
                if (this.checkCrystal(a, EntityUtil.getVarOffsets(1, 0, -1)) != null) {
                    EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(1, 0, -1)), true);
                }
                this.perform(pos.add(1, 0, 0));
                this.perform(pos.add(1, 0, -1));
            }
        }
        if (this.getBlock(pos.add(0, 0, -1)).getBlock() == Blocks.AIR && this.getBlock(pos.add(1, 0, -1)).getBlock() == Blocks.AIR) {
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, -1, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, -1))))) {
                if (this.checkCrystal(a, EntityUtil.getVarOffsets(1, 0, -1)) != null) {
                    EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(1, 0, -1)), true);
                }
                this.perform(pos.add(0, 0, -1));
                this.perform(pos.add(1, 0, -1));
            }
        }
        if (!(this.getBlock(pos.add(1, 0, 0)).getBlock() != Blocks.AIR || this.getBlock(pos.add(1, 0, 1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(1, 1, 1)).getBlock() != Blocks.AIR || new BlockPos(feet).equals(new BlockPos(pos.add(1, 1, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, -1, 0))))) {
            this.perform(pos.add(1, 0, 0));
            this.perform(pos.add(1, 0, 1));
            this.perform(pos.add(1, 1, 1));
        }
        if (!(this.getBlock(pos.add(0, 0, 1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(1, 0, 1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(1, 1, 1)).getBlock() != Blocks.AIR || new BlockPos(feet).equals(new BlockPos(pos.add(1, 1, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, 1))))) {
            this.perform(pos.add(0, 0, 1));
            this.perform(pos.add(1, 0, 1));
            this.perform(pos.add(1, 1, 1));
        }
        if (!(this.getBlock(pos.add(-1, 0, 0)).getBlock() != Blocks.AIR || this.getBlock(pos.add(-1, 0, -1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(-1, 1, -1)).getBlock() != Blocks.AIR || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 1, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, -1, 0))))) {
            this.perform(pos.add(-1, 0, 0));
            this.perform(pos.add(-1, 0, -1));
            this.perform(pos.add(-1, 1, -1));
        }
        if (!(this.getBlock(pos.add(0, 0, -1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(-1, 0, -1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(-1, 1, -1)).getBlock() != Blocks.AIR || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 1, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, -1))))) {
            this.perform(pos.add(0, 0, -1));
            this.perform(pos.add(-1, 0, -1));
            this.perform(pos.add(-1, 1, -1));
        }
        if (!(this.getBlock(pos.add(-1, 0, 0)).getBlock() != Blocks.AIR || this.getBlock(pos.add(-1, 0, 1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(-1, 1, 1)).getBlock() != Blocks.AIR || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 1, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, -1, 0))))) {
            this.perform(pos.add(-1, 0, 0));
            this.perform(pos.add(-1, 0, 1));
            this.perform(pos.add(-1, 1, 1));
        }
        if (!(this.getBlock(pos.add(0, 0, 1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(-1, 0, 1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(-1, 1, 1)).getBlock() != Blocks.AIR || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 1, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, 1))))) {
            this.perform(pos.add(0, 0, 1));
            this.perform(pos.add(-1, 0, 1));
            this.perform(pos.add(-1, 1, 1));
        }
        if (!(this.getBlock(pos.add(1, 0, 0)).getBlock() != Blocks.AIR || this.getBlock(pos.add(1, 0, -1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(1, 1, -1)).getBlock() != Blocks.AIR || new BlockPos(feet).equals(new BlockPos(pos.add(1, 1, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, -1, 0))))) {
            this.perform(pos.add(1, 0, 0));
            this.perform(pos.add(1, 0, -1));
            this.perform(pos.add(1, 1, -1));
        }
        if (!(this.getBlock(pos.add(0, 0, -1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(1, 0, -1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(1, 1, -1)).getBlock() != Blocks.AIR || new BlockPos(feet).equals(new BlockPos(pos.add(1, 1, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, -1))))) {
            this.perform(pos.add(0, 0, -1));
            this.perform(pos.add(1, 0, -1));
            this.perform(pos.add(1, 1, -1));
        }
//        }
    }

    private void switchToSlot(int slot) {
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
    }

    private boolean check() {
        if (AntiMine.nullCheck()) {
            return true;
        }
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        if (obbySlot == -1 && eChestSot == -1) {
            this.toggle();
        }
        this.offHand = InventoryUtil.isBlock(mc.player.getHeldItemOffhand().getItem(), BlockObsidian.class);
        int obbySlot1 = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int echestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        if (this.retryTimer.passedMs(2500L)) {
            this.retryTimer.reset();
        }
        if (obbySlot1 == -1 && !this.offHand && echestSlot == -1) {
            this.disable();
            return true;
        }
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        if (mc.player.inventory.currentItem != this.lastHotbarSlot && mc.player.inventory.currentItem != obbySlot1 && mc.player.inventory.currentItem != echestSlot) {
            this.lastHotbarSlot = mc.player.inventory.currentItem;
        }
        return !this.timer.passedMs(this.delay.getValue().intValue());

    }

    private void place(Vec3d pos, Vec3d[] list) {
        if (fullNullCheck()) return;
        Vec3d[] var3 = list;
        int var4 = list.length;
        for (int var5 = 0; var5 < var4; ++var5) {
            Vec3d vec3d = var3[var5];
            BlockPos position = new BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z);
            int a = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            mc.playerController.updateController();
            this.isSneaking = BlockUtil.placeBlock(position, EnumHand.MAIN_HAND, false, this.packet.getValue(), true);
            mc.player.inventory.currentItem = a;
            mc.playerController.updateController();
        }
    }

    Entity checkCrystal(Vec3d pos, Vec3d[] list) {
        Entity crystal = null;
        Vec3d[] var4 = list;
        int var5 = list.length;
        for (int var6 = 0; var6 < var5; ++var6) {
            Vec3d vec3d = var4[var6];
            BlockPos position = new BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z);
            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(position))) {
                if (!(entity instanceof EntityEnderCrystal) || crystal != null) continue;
                crystal = entity;
            }
        }
        return crystal;
    }

    private IBlockState getBlock(BlockPos block) {
        return mc.world.getBlockState(block);
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

    private EntityPlayer getTargets(double range) {
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

    private void perform(BlockPos pos) {
        if (fullNullCheck()) return;
        int old = mc.player.inventory.currentItem;
        this.switchToSlot(this.obsidian);
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), true, false);
        this.switchToSlot(old);
    }
}
 