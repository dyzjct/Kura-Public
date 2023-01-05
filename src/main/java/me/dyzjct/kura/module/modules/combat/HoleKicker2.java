package me.dyzjct.kura.module.modules.combat;

import me.dyzjct.kura.event.events.entity.MotionUpdateEvent;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.module.modules.misc.InstantMine;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.NTMiku.BlockUtil;
import me.dyzjct.kura.utils.NTMiku.Timer;
import me.dyzjct.kura.utils.block.BlockHelper;
import me.dyzjct.kura.utils.combat.CombatUtil;
import me.dyzjct.kura.utils.inventory.InventoryUtil;
import me.dyzjct.kura.utils.math.RotationUtil;
import me.dyzjct.kura.utils.mc.ChatUtil;
import me.dyzjct.kura.utils.mc.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Module.Info(name = "HolePush+", category = Category.COMBAT)
public class HoleKicker2 extends Module {
    private final Setting<Boolean> var2;
    private final Setting<Float> range;
    private final Setting<Integer> delay;
    private final Setting<Boolean> attackCry;
    private final Setting<Integer> noSuicide;
    private final Setting<Integer> attackRange;
    private final Setting<Integer> count;
    private final Setting<Double> mineDelay;
    private final Setting<Boolean> debug;
    private final Setting<Boolean> ignoreBBox;
    private final BooleanSetting Sticky = bsetting("StickyPiston", false);
    Timer var3;
    Timer var4;
    BlockPos ppos;
    BlockPos pos;
    int stage;
    int ct;
    int ct1;
    EntityPlayer target;
    Timer dynamicRenderingTimer;
    double renderCount;
    Timer renderTimer;
    boolean canRender;
    int rotate;
    private BlockPos renderPos;

    public HoleKicker2() {
        this.stage = 0;
        this.ct = 0;
        this.ct1 = 0;
        this.canRender = false;
        this.var2 = this.bsetting("DisableNoItem", true);
        this.ignoreBBox = this.bsetting("IgnoreBBox", true);
        this.attackCry = this.bsetting("AttackCrystal", true);
        this.range = this.fsetting("Range", 5.0f, 1.0f, 6.0f);
        this.delay = this.isetting("Delay", 0, 0, 1000);
        Setting<Double> var1 = this.dsetting("MaxTargetMotion", 7.0, 0.0, 15.0);
        this.mineDelay = this.dsetting("MinerRSDeley", 0.0, 0.0, 400.0);
        this.count = this.isetting("AntiStickCount", 20, 0, 200);
        this.noSuicide = this.isetting("NoSuicideHealth", 5, 0, 20).b((BooleanSetting) this.attackCry);
        this.attackRange = this.isetting("AttackRange", 5, 0, 7).b((BooleanSetting) this.attackCry);
        this.debug = this.bsetting("Debug", true);
        this.var3 = new Timer();
        this.var4 = new Timer();
        this.dynamicRenderingTimer = new Timer();
        this.renderTimer = new Timer();
    }


    @SubscribeEvent
    public void onTick(final MotionUpdateEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (!mc.player.onGround) {
            return;
        }
        final int oldSlot = HoleKicker2.mc.player.inventory.currentItem;
        int pisSlot = InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.PISTON));
        int pisSlot2 = InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.STICKY_PISTON));
        if (!Sticky.getValue() && pisSlot == -1 && (pisSlot = InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK))) == -1) {
            if (this.var2.getValue()) {
                ChatUtil.sendMessage("Undetected PISTON in Inventory");
                this.disable();
            }
            return;
        }
        if (Sticky.getValue() && pisSlot2 == -1 && (pisSlot2 = InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK))) == -1) {
            if (this.var2.getValue()) {
                ChatUtil.sendMessage("Undetected STICKY_PISTON in Inventory");
                this.disable();
            }
            return;
        }
        final int rstSlot = InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK));
        if (rstSlot == -1) {
            if (this.var2.getValue()) {
                ChatUtil.sendMessage("Undetected REDSTONE_BLOCK in Inventory");
                this.disable();
            }
            return;
        }
        if (this.stage == 0) {
            if (!this.var4.passedDms(this.delay.getValue())) {
                return;
            }
            final EntityPlayer target = this.target = this.getTarget(this.range.getValue());
            this.ppos = this.getPistonPos(target);
            if (this.ppos == null) {
                return;
            }
            this.pos = this.getRSTPos(this.ppos);
            if (this.pos == null) {
                return;
            }
            this.ct = 0;
            this.ct1 = 0;
            ++this.stage;
        }
        if (this.stage == 1) {
            ++this.stage;
        } else if (this.stage == 2) {
            if (!this.var4.passedDms(this.delay.getValue())) {
                return;
            }
            this.var4.reset();
            this.canRender = true;
            this.renderTimer.reset();
            if (this.attackCry.getValue()) {
                this.attackCrystal();
            }
            if (Sticky.getValue()) {
                InventoryUtil.switchToHotbarSlot(pisSlot2, false);
            } else {
                InventoryUtil.switchToHotbarSlot(pisSlot, false);
            }
            if (ppos != null) {
                RotationUtil.faceYawAndPitch((float) this.rotate, 0.0f);
                BlockUtil.placeBlock(this.ppos, EnumHand.MAIN_HAND, false, true, true);
            }
            InventoryUtil.switchToHotbarSlot(oldSlot, false);
            if (HoleKicker2.mc.world.getBlockState(this.ppos).getBlock().equals(Blocks.PISTON) | HoleKicker2.mc.world.getBlockState(this.ppos).getBlock().equals(Blocks.STICKY_PISTON)) {
                ++this.stage;
            } else {
                ++this.ct1;
                if (this.ct1 > this.count.getValue()) {
                    this.stage = 0;
                }
            }
        }
        if (this.stage == 3) {
            if (BlockHelper.haveNeighborBlock(this.ppos, Blocks.REDSTONE_BLOCK).size() > 0) {
                this.var3.reset();
                this.stage = 4;
                return;
            }
            if (HoleKicker2.mc.world.getBlockState(this.ppos).getBlock().equals(Blocks.PISTON) | HoleKicker2.mc.world.getBlockState(this.ppos).getBlock().equals(Blocks.STICKY_PISTON) || !HoleKicker2.mc.world.getBlockState(this.ppos).getBlock().equals(Blocks.REDSTONE_BLOCK)) {
            }
            InventoryUtil.switchToHotbarSlot(rstSlot, false);
            BlockUtil.placeBlock(this.pos, EnumHand.MAIN_HAND, false, true, true);
            InventoryUtil.switchToHotbarSlot(oldSlot, false);
            if (HoleKicker2.mc.world.getBlockState(this.pos).getBlock().equals(Blocks.REDSTONE_BLOCK)) {
                this.var3.reset();
                this.stage = 4;
            }
            ++this.ct;
            if (this.ct > this.count.getValue()) {
                this.stage = 0;
            }
        } else if (this.stage == 4) {
            if (!this.var3.passedDms(this.mineDelay.getValue())) {
                if (this.debug.getValue()) {
                    ChatUtil.sendMessage("minerDealy not passedDms");
                }
                return;
            }
            if (!HoleKicker2.mc.world.getBlockState(CombatUtil.getFlooredPosition(this.target)).getBlock().equals(Blocks.AIR) && HoleKicker2.mc.world.getBlockState(CombatUtil.getFlooredPosition(this.target).add(0, 2, 0)).getBlock().equals(Blocks.AIR) && !HoleKicker2.mc.world.getBlockState(this.ppos.add(0, -1, 0)).getBlock().equals(Blocks.AIR)) {
                if (HoleKicker2.mc.world.getBlockState(this.ppos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                    if (this.debug.getValue()) {
                        ChatUtil.sendMessage("minerDealy passedDms ok!");
                    }
                    final BlockPos minePos;
                    if (BlockHelper.haveNeighborBlock(this.ppos, Blocks.REDSTONE_BLOCK).size() == 1 && (minePos = BlockHelper.haveNeighborBlock(this.ppos, Blocks.REDSTONE_BLOCK).get(0)) != null && (InstantMine.breakPos == null || !InstantMine.breakPos.equals(minePos))) {
                        HoleKicker2.mc.playerController.onPlayerDamageBlock(minePos, BlockUtil.getRayTraceFacing(minePos));
                        if (this.debug.getValue()) {
                            ChatUtil.sendMessage("Miner REDSTONE_BLOCK!");
                        }
                    }
                } else if (HoleKicker2.mc.world.getBlockState(this.ppos.add(0, 1, 0)).getBlock().equals(Blocks.REDSTONE_BLOCK) && BlockHelper.haveNeighborBlock(this.ppos, Blocks.REDSTONE_BLOCK).size() == 1) {
                    if (this.debug.getValue()) {
                        ChatUtil.sendMessage("Miner REDSTONE_BLOCK_1! NOTNULL!");
                    }
                    HoleKicker2.mc.playerController.onPlayerDamageBlock(this.ppos.add(0, 1, 0), BlockUtil.getRayTraceFacing(this.ppos.add(0, 1, 0)));
                }
            }
            this.stage = 0;
            this.disable();
        }
    }

    public boolean isSur(final Entity player) {
        final BlockPos playerPos = CombatUtil.getFlooredPosition(player);
        return (!HoleKicker2.mc.world.getBlockState(playerPos.add(1, 0, 0)).getBlock().equals(Blocks.AIR) && !HoleKicker2.mc.world.getBlockState(playerPos.add(-1, 0, 0)).getBlock().equals(Blocks.AIR) && !HoleKicker2.mc.world.getBlockState(playerPos.add(0, 0, 1)).getBlock().equals(Blocks.AIR) && !HoleKicker2.mc.world.getBlockState(playerPos.add(0, 0, -1)).getBlock().equals(Blocks.AIR)) || EntityUtil.isBothHole(playerPos);
    }

    private EntityPlayer getTarget(double range) {
        EntityPlayer target = null;
        for (EntityPlayer player : new ArrayList<>(mc.world.playerEntities)) {
            if (me.dyzjct.kura.utils.entity.EntityUtil.isntValid(player, range)) continue;
            if (mc.player.getDistance(player) > range) continue;
            target = player;
            if (player != null) {
                break;
            }
            return player;
        }
        return target;
    }

    public void attackCrystal() {
        if (HoleKicker2.mc.player.getHealth() < (float) this.noSuicide.getValue().intValue()) {
            return;
        }
        ArrayList<Entity> crystalList = new ArrayList<Entity>();
        for (Object entity : HoleKicker2.mc.world.loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal)) continue;
            crystalList.add((Entity) entity);
        }
        if (crystalList.size() == 0) {
            return;
        }
        HashMap<Entity, Double> distantMap = new HashMap<Entity, Double>();
        for (Entity crystal : crystalList) {
            if (!(HoleKicker2.mc.player.getDistance(crystal.posX, crystal.posY, crystal.posZ) < (double) this.attackRange.getValue().intValue()))
                continue;
            distantMap.put(crystal, HoleKicker2.mc.player.getDistance(crystal.posX, crystal.posY, crystal.posZ));
        }
        ArrayList list = new ArrayList(distantMap.entrySet());
        list.sort(Map.Entry.comparingByValue());
        if (list.size() == 0) {
            return;
        }
        if ((Double) ((Map.Entry) list.get(0)).getValue() < 5.0) {
            EntityUtil.attackEntity((Entity) ((Map.Entry) list.get(list.size() - 1)).getKey(), true);
        }
    }

    public BlockPos getRSTPos(BlockPos pistonPos) {
        if (pistonPos == null) {
            return null;
        }
        if (BlockHelper.haveNeighborBlock(pistonPos, Blocks.REDSTONE_BLOCK).size() > 0 && this.isAABBBlocked(pistonPos)) {
            return BlockHelper.haveNeighborBlock(pistonPos, Blocks.REDSTONE_BLOCK).get(0);
        }
        ArrayList<BlockPos> placePosList = new ArrayList<BlockPos>();
        placePosList.add(pistonPos.add(0, 1, 0));
        placePosList.add(pistonPos.add(0, -1, 0));
        if (this.rotate != -90) {
            placePosList.add(pistonPos.add(-1, 0, 0));
        }
        if (this.rotate != 90) {
            placePosList.add(pistonPos.add(1, 0, 0));
        }
        if (this.rotate != 0) {
            placePosList.add(pistonPos.add(0, 0, -1));
        }
        if (this.rotate != 180) {
            placePosList.add(pistonPos.add(0, 0, 1));
        }
        HashMap<BlockPos, Double> distantMap = new HashMap<BlockPos, Double>();
        for (BlockPos rSTPos : placePosList) {
            if (!HoleKicker2.mc.world.getBlockState(rSTPos).getBlock().equals(Blocks.AIR) || !this.isAABBBlocked(rSTPos))
                continue;
            distantMap.put(rSTPos, HoleKicker2.mc.player.getDistanceSq(rSTPos));
        }
        ArrayList list = new ArrayList(distantMap.entrySet());
        list.sort(Map.Entry.comparingByValue());
        if (list.size() == 0) {
            return null;
        }
        return (BlockPos) ((Map.Entry) list.get(0)).getKey();
    }

    public BlockPos getPistonPos(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        BlockPos pPos = CombatUtil.getFlooredPosition(player);
        if (!HoleKicker2.mc.world.getBlockState(pPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) || !HoleKicker2.mc.world.getBlockState(pPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
            return null;
        }
        HashMap<BlockPos, Double> distantMap = new HashMap<BlockPos, Double>();
        if (HoleKicker2.mc.world.getBlockState(pPos.add(1, 1, 0)).getBlock().equals(Blocks.AIR) && this.isAABBBlocked(pPos.add(1, 1, 0)) && HoleKicker2.mc.world.getBlockState(pPos.add(-1, 1, 0)).getBlock().equals(Blocks.AIR) && HoleKicker2.mc.world.getBlockState(pPos.add(-1, 2, 0)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(1, 1, 0), HoleKicker2.mc.player.getDistance(pPos.add(1, 1, 0).getX(), pPos.add(1, 1, 0).getY(), pPos.add(1, 1, 0).getZ()));
        } else if (HoleKicker2.mc.world.getBlockState(pPos.add(1, 1, 0)).getBlock().equals(Blocks.AIR) && this.isAABBBlocked(pPos.add(1, 1, 0)) && !HoleKicker2.mc.world.getBlockState(pPos.add(1, 0, 0)).getBlock().equals(Blocks.AIR) && !HoleKicker2.mc.world.getBlockState(pPos.add(1, 0, 0)).getBlock().equals(Blocks.REDSTONE_BLOCK) && !HoleKicker2.mc.world.getBlockState(pPos).getBlock().equals(Blocks.AIR) && HoleKicker2.mc.world.getBlockState(pPos.add(1, 2, 0)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(1, 1, 0), HoleKicker2.mc.player.getDistance(pPos.add(1, 1, 0).getX(), pPos.add(1, 1, 0).getY(), pPos.add(1, 1, 0).getZ()));
        }
        if (HoleKicker2.mc.world.getBlockState(pPos.add(-1, 1, 0)).getBlock().equals(Blocks.AIR) && this.isAABBBlocked(pPos.add(-1, 1, 0)) && HoleKicker2.mc.world.getBlockState(pPos.add(1, 1, 0)).getBlock().equals(Blocks.AIR) && HoleKicker2.mc.world.getBlockState(pPos.add(1, 2, 0)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(-1, 1, 0), HoleKicker2.mc.player.getDistance(pPos.add(-1, 1, 0).getX(), pPos.add(-1, 1, 0).getY(), pPos.add(-1, 1, 0).getZ()));
        } else if (HoleKicker2.mc.world.getBlockState(pPos.add(-1, 1, 0)).getBlock().equals(Blocks.AIR) && this.isAABBBlocked(pPos.add(-1, 1, 0)) && !HoleKicker2.mc.world.getBlockState(pPos.add(-1, 0, 0)).getBlock().equals(Blocks.AIR) && !HoleKicker2.mc.world.getBlockState(pPos.add(-1, 0, 0)).getBlock().equals(Blocks.REDSTONE_BLOCK) && !HoleKicker2.mc.world.getBlockState(pPos).getBlock().equals(Blocks.AIR) && HoleKicker2.mc.world.getBlockState(pPos.add(-1, 2, 0)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(-1, 1, 0), HoleKicker2.mc.player.getDistance(pPos.add(-1, 1, 0).getX(), pPos.add(-1, 1, 0).getY(), pPos.add(-1, 1, 0).getZ()));
        }
        if (HoleKicker2.mc.world.getBlockState(pPos.add(0, 1, 1)).getBlock().equals(Blocks.AIR) && this.isAABBBlocked(pPos.add(0, 1, 1)) && HoleKicker2.mc.world.getBlockState(pPos.add(0, 1, -1)).getBlock().equals(Blocks.AIR) && HoleKicker2.mc.world.getBlockState(pPos.add(0, 2, -1)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(0, 1, 1), HoleKicker2.mc.player.getDistance(pPos.add(0, 1, 1).getX(), pPos.add(0, 1, 1).getY(), pPos.add(0, 1, 1).getZ()));
        } else if (HoleKicker2.mc.world.getBlockState(pPos.add(0, 1, 1)).getBlock().equals(Blocks.AIR) && this.isAABBBlocked(pPos.add(0, 1, 1)) && !HoleKicker2.mc.world.getBlockState(pPos.add(0, 0, 1)).getBlock().equals(Blocks.AIR) && !HoleKicker2.mc.world.getBlockState(pPos.add(0, 0, 1)).getBlock().equals(Blocks.REDSTONE_BLOCK) && !HoleKicker2.mc.world.getBlockState(pPos).getBlock().equals(Blocks.AIR) && HoleKicker2.mc.world.getBlockState(pPos.add(0, 2, 1)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(0, 1, 1), HoleKicker2.mc.player.getDistance(pPos.add(0, 1, 1).getX(), pPos.add(0, 1, 1).getY(), pPos.add(0, 1, 1).getZ()));
        }
        if (HoleKicker2.mc.world.getBlockState(pPos.add(0, 1, -1)).getBlock().equals(Blocks.AIR) && this.isAABBBlocked(pPos.add(0, 1, -1)) && HoleKicker2.mc.world.getBlockState(pPos.add(0, 1, 1)).getBlock().equals(Blocks.AIR) && HoleKicker2.mc.world.getBlockState(pPos.add(0, 2, 1)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(0, 1, -1), HoleKicker2.mc.player.getDistance(pPos.add(0, 1, -1).getX(), pPos.add(0, 1, -1).getY(), pPos.add(0, 1, -1).getZ()));
        } else if (HoleKicker2.mc.world.getBlockState(pPos.add(0, 1, -1)).getBlock().equals(Blocks.AIR) && this.isAABBBlocked(pPos.add(0, 1, -1)) && !HoleKicker2.mc.world.getBlockState(pPos).getBlock().equals(Blocks.AIR) && HoleKicker2.mc.world.getBlockState(pPos.add(0, 2, -1)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(0, 1, -1), HoleKicker2.mc.player.getDistance(pPos.add(0, 1, -1).getX(), pPos.add(0, 1, -1).getY(), pPos.add(0, 1, -1).getZ()));
        }
        ArrayList list = new ArrayList(distantMap.entrySet());
        list.sort(Map.Entry.comparingByValue());
        if (list.size() == 0) {
            return null;
        }
        if (((Map.Entry) list.get(0)).getKey().equals(pPos.add(1, 1, 0))) {
            this.rotate = -90;
        }
        if (((Map.Entry) list.get(0)).getKey().equals(pPos.add(-1, 1, 0))) {
            this.rotate = 90;
        }
        if (((Map.Entry) list.get(0)).getKey().equals(pPos.add(0, 1, 1))) {
            this.rotate = 0;
        }
        if (((Map.Entry) list.get(0)).getKey().equals(pPos.add(0, 1, -1))) {
            this.rotate = 180;
        }
        return (BlockPos) ((Map.Entry) list.get(0)).getKey();
    }

    public boolean isAABBBlocked(BlockPos pos) {
        if (this.ignoreBBox.getValue().booleanValue()) {
            return true;
        }
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos);
        boolean i = false;
        List l = HoleKicker2.mc.world.getEntitiesWithinAABBExcludingEntity(null, axisAlignedBB);
        return l.size() == 0;
    }

    public String getStageString(String retur1) {
        if (stage == 1)
            retur1 = "1";
        return retur1;
    }

    public String getDisplayInfo() {
        if (target == null)
            return "No Target";
        return target.getName() + "|" + stage;
    }
}
