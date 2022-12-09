package me.dyzjct.kura.module.modules.misc;

import me.dyzjct.kura.event.events.entity.MotionUpdateEvent;
import me.dyzjct.kura.manager.SpeedManager;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.block.BlockUtil;
import me.dyzjct.kura.utils.entity.EntityUtil;
import me.dyzjct.kura.utils.inventory.InventoryUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Comparator;
import java.util.stream.Collectors;

@Module.Info(name = "AntiFeetBreak",category = Category.MISC)
public class AntiFeetBreak
        extends Module {
    public EntityPlayer target;
    private final Setting<Float> range = fsetting("Range", 8.0f, 1.0f, 12.0f);
    private final Setting<Boolean> rotate = bsetting("Rotate", true);

    public Setting<Boolean> packet = bsetting("Packet", true);
    private final Setting<Boolean> toggle = bsetting("AutoToggle", false);
	private int obsidian = -1;
    private BlockPos startPos;
    private int isEn;
    int ONE;
    int TWO;
    int THREE;
    int FOUR;



    @Override
    public void onEnable() {
        this.startPos = EntityUtil.getRoundedBlockPos(mc.player);
        this.startPos = EntityUtil.getRoundedBlockPos(AntiFeetBreak.mc.player);
        this.isEn = 1;
    }
    @Override
    public void onDisable() {
        this.isEn = 0;
        this.ONE = 0;
        this.TWO = 0;
        this.THREE = 0;
        this.FOUR = 0;
    }
    @SubscribeEvent
    public void onTick(MotionUpdateEvent event) {
        if (this.toggle.getValue())
            this.disable();
        if (mc.player == null || mc.world == null) {
            return;
        }
        if (this.isEn != 1) {
            this.toggle();
        }
        if (!this.startPos.equals(EntityUtil.getRoundedBlockPos(AntiFeetBreak.mc.player))) {
            this.toggle();
        }
    }

    @Override
    public void onUpdate() {
        this.breakcrystal();
        Vec3d a = AntiFeetBreak.mc.player.getPositionVector();
        this.obsidian = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        this.target = this.getTarget(this.range.getValue());
        if (this.target == null) {
            return;
        }
        BlockPos feet = new BlockPos(this.target.posX, this.target.posY, this.target.posZ);
        BlockPos pos = new BlockPos(AntiFeetBreak.mc.player.posX, AntiFeetBreak.mc.player.posY, AntiFeetBreak.mc.player.posZ);
        if (this.obsidian == -1) {
            return;
        }
        if (this.getBlock(pos.add(0, 0, 1)).getBlock() == Blocks.AIR && this.getBlock(pos.add(0, 1, 1)).getBlock() == Blocks.AIR) {
            if (!new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, 1))) && !new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, 1)))) {
                if (!new BlockPos(feet).equals(new BlockPos(pos.add(0, 1, 1))) && !new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, 1)))) {
                    this.FeetPlace(pos.add(0, 0, 1));
                    this.FeetPlace(pos.add(0, 1, 1));
                    this.FeetPlace(pos.add(1, 1, 1));
                    this.FeetPlace(pos.add(0, 1, 2));
                }
            } else if (this.getBlock(pos.add(0, 0, 2)).getBlock() == Blocks.AIR && this.getBlock(pos.add(0, 1, 2)).getBlock() == Blocks.AIR) {
                this.FeetPlace(pos.add(0, 0, 2));
                this.FeetPlace(pos.add(0, 1, 2));
                this.FeetPlace(pos.add(1, 0, 2));
                this.FeetPlace(pos.add(1, 1, 2));
            }
        }
        if (this.getBlock(pos.add(0, 0, -1)).getBlock() == Blocks.AIR && this.getBlock(pos.add(0, 1, -1)).getBlock() == Blocks.AIR) {
            if (!new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, -1))) && !new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, -1)))) {
                if (!new BlockPos(feet).equals(new BlockPos(pos.add(0, 1, -1))) && !new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, -1)))) {
                    this.FeetPlace(pos.add(0, 0, -1));
                    this.FeetPlace(pos.add(0, 1, -1));
                    this.FeetPlace(pos.add(-1, 1, -1));
                    this.FeetPlace(pos.add(0, 1, -2));
                }
            } else if (this.getBlock(pos.add(0, 0, -2)).getBlock() == Blocks.AIR && this.getBlock(pos.add(0, 1, -2)).getBlock() == Blocks.AIR) {
                this.FeetPlace(pos.add(0, 0, -2));
                this.FeetPlace(pos.add(0, 1, -2));
                this.FeetPlace(pos.add(1, 0, -2));
                this.FeetPlace(pos.add(1, 1, -2));
            }
        }
        if (this.getBlock(pos.add(1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(1, 1, 0)).getBlock() == Blocks.AIR) {
            if (!new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 0))) && !new BlockPos(feet).equals(new BlockPos(pos.add(1, -1, 0)))) {
                if (!new BlockPos(feet).equals(new BlockPos(pos.add(1, 1, 0))) && !new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 0)))) {
                    this.FeetPlace(pos.add(1, 0, 0));
                    this.FeetPlace(pos.add(1, 1, 0));
                    this.FeetPlace(pos.add(1, 1, 1));
                    this.FeetPlace(pos.add(2, 1, 0));
                }
            } else if (this.getBlock(pos.add(2, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(2, 1, 0)).getBlock() == Blocks.AIR) {
                this.FeetPlace(pos.add(2, 0, 0));
                this.FeetPlace(pos.add(2, 1, 0));
                this.FeetPlace(pos.add(2, 0, 1));
                this.FeetPlace(pos.add(2, 1, 1));
            }
        }
        if (this.getBlock(pos.add(-1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(-1, 1, 0)).getBlock() == Blocks.AIR) {
            if (!new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 0))) && !new BlockPos(feet).equals(new BlockPos(pos.add(-1, -1, 0)))) {
                if (!new BlockPos(feet).equals(new BlockPos(pos.add(-1, 1, 0))) && !new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 0)))) {
                    this.FeetPlace(pos.add(-1, 0, 0));
                    this.FeetPlace(pos.add(-1, 1, 0));
                    this.FeetPlace(pos.add(-1, 1, -1));
                    this.FeetPlace(pos.add(-2, 1, 0));
                }
            } else if (this.getBlock(pos.add(-2, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(-2, 1, 0)).getBlock() == Blocks.AIR) {
                this.FeetPlace(pos.add(-2, 0, 0));
                this.FeetPlace(pos.add(-2, 1, 0));
                this.FeetPlace(pos.add(-2, 0, 1));
                this.FeetPlace(pos.add(-2, 1, 1));
            }
        }
        if (this.getBlock(pos.add(1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(1, 0, 1)).getBlock() == Blocks.AIR) {

            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, -1, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, -1, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 1))))) {
                this.FeetPlace(pos.add(1, 0, 0));
                this.FeetPlace(pos.add(1, 0, 1));
            }
        }
        if (this.getBlock(pos.add(0, 0, 1)).getBlock() == Blocks.AIR && this.getBlock(pos.add(1, 0, 1)).getBlock() == Blocks.AIR) {
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, -1, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 1))))) {
                this.FeetPlace(pos.add(0, 0, 1));
                this.FeetPlace(pos.add(1, 0, 1));
            }
        }
        if (this.getBlock(pos.add(-1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(-1, 0, -1)).getBlock() == Blocks.AIR) {
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, -1, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, -1, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, -1))))) {
                this.FeetPlace(pos.add(-1, 0, 0));
                this.FeetPlace(pos.add(-1, 0, -1));
            }
        }
        if (this.getBlock(pos.add(0, 0, -1)).getBlock() == Blocks.AIR && this.getBlock(pos.add(-1, 0, -1)).getBlock() == Blocks.AIR) {
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, -1, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, -1))))) {
                this.FeetPlace(pos.add(0, 0, -1));
                this.FeetPlace(pos.add(-1, 0, -1));
            }
        }
        if (this.getBlock(pos.add(-1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(-1, 0, 1)).getBlock() == Blocks.AIR) {
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, -1, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, -1, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 1))))) {
                this.FeetPlace(pos.add(-1, 0, 0));
                this.FeetPlace(pos.add(-1, 0, 1));
            }
        }
        if (this.getBlock(pos.add(0, 0, 1)).getBlock() == Blocks.AIR && this.getBlock(pos.add(-1, 0, 1)).getBlock() == Blocks.AIR) {
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, -1, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 1))))) {
                this.FeetPlace(pos.add(0, 0, 1));
                this.FeetPlace(pos.add(-1, 0, 1));
            }
        }
        if (this.getBlock(pos.add(1, 0, 0)).getBlock() == Blocks.AIR && this.getBlock(pos.add(1, 0, -1)).getBlock() == Blocks.AIR) {
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, -1, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, -1, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, -1))))) {
                this.FeetPlace(pos.add(1, 0, 0));
                this.FeetPlace(pos.add(1, 0, -1));
            }
        }
        if (this.getBlock(pos.add(0, 0, -1)).getBlock() == Blocks.AIR && this.getBlock(pos.add(1, 0, -1)).getBlock() == Blocks.AIR) {
            if (!(new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, -1, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, -1))))) {
                this.FeetPlace(pos.add(0, 0, -1));
                this.FeetPlace(pos.add(1, 0, -1));
            }
        }
        if (!(this.getBlock(pos.add(1, 0, 0)).getBlock() != Blocks.AIR || this.getBlock(pos.add(1, 0, 1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(1, 1, 1)).getBlock() != Blocks.AIR || new BlockPos(feet).equals(new BlockPos(pos.add(1, 1, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, -1, 0))))) {
            this.FeetPlace(pos.add(1, 0, 0));
            this.FeetPlace(pos.add(1, 0, 1));
            this.FeetPlace(pos.add(1, 1, 1));
        }
        if (!(this.getBlock(pos.add(0, 0, 1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(1, 0, 1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(1, 1, 1)).getBlock() != Blocks.AIR || new BlockPos(feet).equals(new BlockPos(pos.add(1, 1, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, 1))))) {
            this.FeetPlace(pos.add(0, 0, 1));
            this.FeetPlace(pos.add(1, 0, 1));
            this.FeetPlace(pos.add(1, 1, 1));
        }
        if (!(this.getBlock(pos.add(-1, 0, 0)).getBlock() != Blocks.AIR || this.getBlock(pos.add(-1, 0, -1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(-1, 1, -1)).getBlock() != Blocks.AIR || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 1, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, -1, 0))))) {
            this.FeetPlace(pos.add(-1, 0, 0));
            this.FeetPlace(pos.add(-1, 0, -1));
            this.FeetPlace(pos.add(-1, 1, -1));
        }
        if (!(this.getBlock(pos.add(0, 0, -1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(-1, 0, -1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(-1, 1, -1)).getBlock() != Blocks.AIR || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 1, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, -1))))) {
            this.FeetPlace(pos.add(0, 0, -1));
            this.FeetPlace(pos.add(-1, 0, -1));
            this.FeetPlace(pos.add(-1, 1, -1));
        }
        if (!(this.getBlock(pos.add(-1, 0, 0)).getBlock() != Blocks.AIR || this.getBlock(pos.add(-1, 0, 1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(-1, 1, 1)).getBlock() != Blocks.AIR || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 1, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, -1, 0))))) {
            this.FeetPlace(pos.add(-1, 0, 0));
            this.FeetPlace(pos.add(-1, 0, 1));
            this.FeetPlace(pos.add(-1, 1, 1));
        }
        if (!(this.getBlock(pos.add(0, 0, 1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(-1, 0, 1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(-1, 1, 1)).getBlock() != Blocks.AIR || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 1, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(-1, 0, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, 1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, 1))))) {
            this.FeetPlace(pos.add(0, 0, 1));
            this.FeetPlace(pos.add(-1, 0, 1));
            this.FeetPlace(pos.add(-1, 1, 1));
        }
        if (!(this.getBlock(pos.add(1, 0, 0)).getBlock() != Blocks.AIR || this.getBlock(pos.add(1, 0, -1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(1, 1, -1)).getBlock() != Blocks.AIR || new BlockPos(feet).equals(new BlockPos(pos.add(1, 1, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, 0))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, -1, 0))))) {
            this.FeetPlace(pos.add(1, 0, 0));
            this.FeetPlace(pos.add(1, 0, -1));
            this.FeetPlace(pos.add(1, 1, -1));
        }
        if (!(this.getBlock(pos.add(0, 0, -1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(1, 0, -1)).getBlock() != Blocks.AIR || this.getBlock(pos.add(1, 1, -1)).getBlock() != Blocks.AIR || new BlockPos(feet).equals(new BlockPos(pos.add(1, 1, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(1, 0, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, 0, -1))) || new BlockPos(feet).equals(new BlockPos(pos.add(0, -1, -1))))) {
            this.FeetPlace(pos.add(0, 0, -1));
            this.FeetPlace(pos.add(1, 0, -1));
            this.FeetPlace(pos.add(1, 1, -1));
     }
 }

    private void switchToSlot(int slot) {
        AntiFeetBreak.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        AntiFeetBreak.mc.player.inventory.currentItem = slot;
        AntiFeetBreak.mc.playerController.updateController();
    }
    private IBlockState getBlock(BlockPos block) {
        return AntiFeetBreak.mc.world.getBlockState(block);
    }
    private EntityPlayer getTarget(double range) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0) + 1.0;
        for (EntityPlayer player : AntiFeetBreak.mc.world.playerEntities) {
            if (EntityUtil.isntValid(player, range) || SpeedManager.getPlayerSpeed(player) > 10.0) continue;
            if (target == null) {
                target = player;
                distance = AntiFeetBreak.mc.player.getDistanceSq(player);
                continue;
            }
            if (AntiFeetBreak.mc.player.getDistanceSq(player) >= distance) continue;
            target = player;
            distance = AntiFeetBreak.mc.player.getDistanceSq(player);
        }
        return target;
    }
    private void FeetPlace(BlockPos pos) {
        int old = AntiFeetBreak.mc.player.inventory.currentItem;
        this.switchToSlot(this.obsidian);
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), true, false);
        this.switchToSlot(old);
    }
    public  void breakcrystal() {
        for (Entity crystal : AntiFeetBreak.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal && !e.isDead).sorted(Comparator.comparing(e -> Float.valueOf(AntiFeetBreak.mc.player.getDistance(e)))).collect(Collectors.toList())) {
            if (!(crystal instanceof EntityEnderCrystal) || !(AntiFeetBreak.mc.player.getDistance(crystal) <= 4.0f))
                continue;
            AntiFeetBreak.mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
            AntiFeetBreak.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
       }
    }
 }
 