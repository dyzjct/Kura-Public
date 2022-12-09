package me.dyzjct.kura.module.modules.combat;

import me.dyzjct.kura.manager.SpeedManager;
import me.dyzjct.kura.module.*;
import me.dyzjct.kura.utils.NTMiku.BlockUtil;
import me.dyzjct.kura.utils.NTMiku.Timer;
import me.dyzjct.kura.utils.combat.CombatUtil;
import net.minecraft.entity.player.*;
import me.dyzjct.kura.setting.*;
import me.dyzjct.kura.event.events.render.*;
import net.minecraft.util.math.*;
import java.awt.*;
import net.minecraftforge.fml.common.eventhandler.*;
import me.dyzjct.kura.event.events.entity.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import me.dyzjct.kura.utils.inventory.*;
import net.minecraft.block.*;
import me.dyzjct.kura.utils.math.*;
import net.minecraft.util.*;
import me.dyzjct.kura.utils.NTMiku.*;
import me.dyzjct.kura.utils.block.*;
import net.minecraft.entity.*;
import me.dyzjct.kura.module.modules.misc.*;
import me.dyzjct.kura.utils.mc.*;
import net.minecraft.entity.item.*;
import java.util.*;
import java.util.List;

@Module.Info(name = "HolePush+", category = Category.COMBAT)
public class HolePushPlus extends Module
{
    private final Setting<Double> var1;
    private final Setting<Boolean> var2;
    private final Setting<Float> range;
    private final Setting<Integer> delay;
    private final Setting<Boolean> attackCry;
    private final Setting<Integer> noSuicide;
    private final Setting<Integer> attackRange;
    private final Setting<Boolean> surCheck;
    private final Setting<Boolean> onGroundCheck;
    private final Setting<Integer> count;
    private final Setting<Integer> renderTime;
    private final Setting<Boolean> render;
    private final Setting<Double> renderSpeed;
    private final Setting<Boolean> renderText;
//    private final Setting<Boolean> noPushSelf;
    private final Setting<Double> mineDelay;
    private final Setting<Boolean> debug;
    private final Setting<Boolean> ignoreBBox;
    private final Setting<Integer> red;
    private final Setting<Integer> green;
    private final Setting<Integer> blue;
    private final Setting<Integer> alpha;
    Timer var3;
    Timer var4;
    BlockPos ppos;
    BlockPos pos;
    int stage;
    int ct;
    int ct1;
    EntityPlayer target;
    private BlockPos renderPos;
    Timer dynamicRenderingTimer;
    double renderCount;
    Timer renderTimer;
    boolean canRender;
    int rotate;
    private BooleanSetting Sticky = bsetting("StickyPiston",false);
    
    public HolePushPlus() {
        this.stage = 0;
        this.ct = 0;
        this.ct1 = 0;
        this.canRender = false;
        this.var2 = this.bsetting("DisableNoItem", true);
        this.ignoreBBox = this.bsetting("IgnoreBBox", true);
        this.attackCry = this.bsetting("AttackCrystal", true);
        this.surCheck = this.bsetting("HoleCheck", true);
        this.onGroundCheck = this.bsetting("OnGroundCheck", true);
//        this.noPushSelf = this.bsetting("NoSelfPush", true);
        this.range = this.fsetting("Range", 5.0f, 1.0f, 6.0f);
        this.delay = this.isetting("Delay", 0, 0, 1000);
        this.var1 = this.dsetting("MaxTargetMotion", 7.0, 0.0, 15.0);
        this.mineDelay = this.dsetting("MinerRSDeley", 0.0, 0.0, 400.0);
        this.count = this.isetting("AntiStickCount", 20, 0, 200);
        this.noSuicide = this.isetting("NoSuicideHealth", 5, (int)0, (int)20).b((BooleanSetting)this.attackCry);
        this.attackRange = this.isetting("AttackRange", 5, (int)0, (int)7).b((BooleanSetting)this.attackCry);
        this.render = this.bsetting("Render", true);
        this.red = this.isetting("Red", 255, 0, 255).b((BooleanSetting)this.render);
        this.green = this.isetting("Green", 0, 0, 255).b((BooleanSetting)this.render);
        this.blue = this.isetting("Blue", 0, 0, 255).b((BooleanSetting)this.render);
        this.alpha = this.isetting("Alpha", 255, 0, 255).b((BooleanSetting)this.render);
        this.renderSpeed = this.dsetting("RenderSpeed", 5.0, 0.0, 10.0).b((BooleanSetting)this.render);
        this.renderTime = this.isetting("RenderTime", 200, 0, 1000).b((BooleanSetting)this.render);
        this.renderText = this.bsetting("RenderText", true).b((BooleanSetting)this.render);
        this.debug = this.bsetting("Debug", true);
        this.var3 = new Timer();
        this.var4 = new Timer();
        this.dynamicRenderingTimer = new Timer();
        this.renderTimer = new Timer();
    }
    
    @SubscribeEvent
    public void onRender3D(final Render3DEvent event) {
        if (this.ppos != null) {
            this.renderPos = this.ppos;
        }
        if (this.canRender) {
            if (this.renderCount / 100.0 < 0.5 && this.dynamicRenderingTimer.passedDms(0.5)) {
                this.renderCount += (double)this.renderSpeed.getValue();
                this.dynamicRenderingTimer.reset();
            }
            final AxisAlignedBB axisAlignedBB = new AxisAlignedBB(this.renderPos.getX() + 0.5 - this.renderCount / 100.0, this.renderPos.getY() + 0.5 - this.renderCount / 100.0, this.renderPos.getZ() + 0.5 - this.renderCount / 100.0, this.renderPos.getX() + 0.5 + this.renderCount / 100.0, this.renderPos.getY() + 0.5 + this.renderCount / 100.0, this.renderPos.getZ() + 0.5 + this.renderCount / 100.0);
            if (this.render.getValue()) {
                RenderUtil.drawBBFill(axisAlignedBB, new Color((int)this.red.getValue(), (int)this.green.getValue(), (int)this.blue.getValue()), (int)this.alpha.getValue());
            }
            if (this.renderText.getValue()) {
                RenderUtil.drawText(this.renderPos, "TRY PUSH...");
            }
            if (this.debug.getValue()) {
                ChatUtil.sendMessage("Trying Push...");
            }
        }
        else {
            this.renderCount = 0.0;
        }
    }
    
    @SubscribeEvent
    public void onTick(final MotionUpdateEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (!mc.player.onGround){
            return;
        }
        if (this.renderTimer.passedDms((double)(int)this.renderTime.getValue())) {
            this.canRender = false;
        }
        final int oldSlot = HolePushPlus.mc.player.inventory.currentItem;
        final int obbySlot = InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        int pisSlot = InventoryUtil.getItemHotbar(Item.getItemFromBlock((Block)Blocks.PISTON));
        int pisSlot2 = InventoryUtil.getItemHotbar(Item.getItemFromBlock((Block)Blocks.STICKY_PISTON));
        if (!Sticky.getValue()&&pisSlot == -1 && (pisSlot = InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK))) == -1) {
            if (this.var2.getValue()) {
                ChatUtil.sendMessage("Undetected PISTON in Inventory");
                this.disable();
            }
            return;
        }
        if (Sticky.getValue()&&pisSlot2 == -1 && (pisSlot2 = InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK))) == -1) {
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
            if (!this.var4.passedDms((double)(int)this.delay.getValue())) {
                return;
            }
            final EntityPlayer target = this.target = this.getTarget((float)this.range.getValue());
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
        }
        else if (this.stage == 2) {
            if (!this.var4.passedDms((double)(int)this.delay.getValue())) {
                return;
            }
            this.var4.reset();
            this.canRender = true;
            this.renderTimer.reset();
            if (this.attackCry.getValue()) {
                this.attackCrystal();
            }
            if (Sticky.getValue()){
                InventoryUtil.switchToHotbarSlot(pisSlot2, false);
            }
            else {
                InventoryUtil.switchToHotbarSlot(pisSlot, false);
            }
            if (ppos!=null){
                RotationUtil.faceYawAndPitch((float)this.rotate, 0.0f);
                BlockUtil.placeBlock(this.ppos, EnumHand.MAIN_HAND, false, true, true);
            }
            InventoryUtil.switchToHotbarSlot(oldSlot, false);
            if (HolePushPlus.mc.world.getBlockState(this.ppos).getBlock().equals(Blocks.PISTON)|HolePushPlus.mc.world.getBlockState(this.ppos).getBlock().equals(Blocks.STICKY_PISTON)) {
                ++this.stage;
            }
            else {
                ++this.ct1;
                if (this.ct1 > (int)this.count.getValue()) {
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
            if (HolePushPlus.mc.world.getBlockState(this.ppos).getBlock().equals(Blocks.PISTON) | HolePushPlus.mc.world.getBlockState(this.ppos).getBlock().equals(Blocks.STICKY_PISTON) || !HolePushPlus.mc.world.getBlockState(this.ppos).getBlock().equals(Blocks.REDSTONE_BLOCK)) {}
            InventoryUtil.switchToHotbarSlot(rstSlot, false);
            BlockUtil.placeBlock(this.pos, EnumHand.MAIN_HAND, false, true, true);
            InventoryUtil.switchToHotbarSlot(oldSlot, false);
            if (HolePushPlus.mc.world.getBlockState(this.pos).getBlock().equals(Blocks.REDSTONE_BLOCK)) {
                this.var3.reset();
                this.stage = 4;
            }
            ++this.ct;
            if (this.ct > (int)this.count.getValue()) {
                this.stage = 0;
            }
        }
        else if (this.stage == 4) {
            if (!this.var3.passedDms((double)this.mineDelay.getValue())) {
                if (this.debug.getValue()) {
                    ChatUtil.sendMessage("minerDealy not passedDms");
                }
                return;
            }
            if (!HolePushPlus.mc.world.getBlockState(CombatUtil.getFlooredPosition((Entity)this.target)).getBlock().equals(Blocks.AIR) && HolePushPlus.mc.world.getBlockState(CombatUtil.getFlooredPosition((Entity)this.target).add(0, 2, 0)).getBlock().equals(Blocks.AIR) && !HolePushPlus.mc.world.getBlockState(this.ppos.add(0, -1, 0)).getBlock().equals(Blocks.AIR)) {
                if (HolePushPlus.mc.world.getBlockState(this.ppos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                    if (this.debug.getValue()) {
                        ChatUtil.sendMessage("minerDealy passedDms ok!");
                    }
                    final BlockPos minePos;
                    if (BlockHelper.haveNeighborBlock(this.ppos, Blocks.REDSTONE_BLOCK).size() == 1 && (minePos = BlockHelper.haveNeighborBlock(this.ppos, Blocks.REDSTONE_BLOCK).get(0)) != null && (InstantMine.breakPos == null || !InstantMine.breakPos.equals((Object)minePos))) {
                        HolePushPlus.mc.playerController.onPlayerDamageBlock(minePos, BlockUtil.getRayTraceFacing(minePos));
                        if (this.debug.getValue()) {
                            ChatUtil.sendMessage("Miner REDSTONE_BLOCK!");
                        }
                    }
                }
                else if (HolePushPlus.mc.world.getBlockState(this.ppos.add(0, 1, 0)).getBlock().equals(Blocks.REDSTONE_BLOCK) && BlockHelper.haveNeighborBlock(this.ppos, Blocks.REDSTONE_BLOCK).size() == 1) {
                    if (this.debug.getValue()) {
                        ChatUtil.sendMessage("Miner REDSTONE_BLOCK_1! NOTNULL!");
                    }
                    HolePushPlus.mc.playerController.onPlayerDamageBlock(this.ppos.add(0, 1, 0), BlockUtil.getRayTraceFacing(this.ppos.add(0, 1, 0)));
                }
            }
            this.stage = 0;
            this.disable();
        }
    }
    
    public boolean isSur(final Entity player) {
        final BlockPos playerPos = CombatUtil.getFlooredPosition(player);
        return (!HolePushPlus.mc.world.getBlockState(playerPos.add(1, 0, 0)).getBlock().equals(Blocks.AIR) && !HolePushPlus.mc.world.getBlockState(playerPos.add(-1, 0, 0)).getBlock().equals(Blocks.AIR) && !HolePushPlus.mc.world.getBlockState(playerPos.add(0, 0, 1)).getBlock().equals(Blocks.AIR) && !HolePushPlus.mc.world.getBlockState(playerPos.add(0, 0, -1)).getBlock().equals(Blocks.AIR)) || EntityUtil.isBothHole(playerPos);
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
        if (HolePushPlus.mc.player.getHealth() < (float)this.noSuicide.getValue().intValue()) {
            return;
        }
        ArrayList<Entity> crystalList = new ArrayList<Entity>();
        for (Object entity : HolePushPlus.mc.world.loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal)) continue;
            crystalList.add((Entity)entity);
        }
        if (crystalList.size() == 0) {
            return;
        }
        HashMap<Entity, Double> distantMap = new HashMap<Entity, Double>();
        for (Entity crystal : crystalList) {
            if (!(HolePushPlus.mc.player.getDistance(crystal.posX, crystal.posY, crystal.posZ) < (double)this.attackRange.getValue().intValue())) continue;
            distantMap.put(crystal, HolePushPlus.mc.player.getDistance(crystal.posX, crystal.posY, crystal.posZ));
        }
        ArrayList list = new ArrayList(distantMap.entrySet());
        list.sort(Map.Entry.comparingByValue());
        if (list.size() == 0) {
            return;
        }
        if ((Double)((Map.Entry)list.get(0)).getValue() < 5.0) {
            EntityUtil.attackEntity((Entity)((Map.Entry)list.get(list.size() - 1)).getKey(), true);
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
            if (!HolePushPlus.mc.world.getBlockState(rSTPos).getBlock().equals(Blocks.AIR) || !this.isAABBBlocked(rSTPos)) continue;
            distantMap.put(rSTPos, HolePushPlus.mc.player.getDistanceSq(rSTPos));
        }
        ArrayList list = new ArrayList(distantMap.entrySet());
        list.sort(Map.Entry.comparingByValue());
        if (list.size() == 0) {
            return null;
        }
        return (BlockPos)((Map.Entry)list.get(0)).getKey();
    }

    public BlockPos getPistonPos(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        BlockPos pPos = CombatUtil.getFlooredPosition(player);
        if (!HolePushPlus.mc.world.getBlockState(pPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) || !HolePushPlus.mc.world.getBlockState(pPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
            return null;
        }
        HashMap<BlockPos, Double> distantMap = new HashMap<BlockPos, Double>();
        if (HolePushPlus.mc.world.getBlockState(pPos.add(1, 1, 0)).getBlock().equals(Blocks.AIR) && this.isAABBBlocked(pPos.add(1, 1, 0)) && HolePushPlus.mc.world.getBlockState(pPos.add(-1, 1, 0)).getBlock().equals(Blocks.AIR) && HolePushPlus.mc.world.getBlockState(pPos.add(-1, 2, 0)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(1, 1, 0), HolePushPlus.mc.player.getDistance(pPos.add(1, 1, 0).getX(), pPos.add(1, 1, 0).getY(), pPos.add(1, 1, 0).getZ()));
        } else if (HolePushPlus.mc.world.getBlockState(pPos.add(1, 1, 0)).getBlock().equals(Blocks.AIR) && this.isAABBBlocked(pPos.add(1, 1, 0)) && !HolePushPlus.mc.world.getBlockState(pPos.add(1, 0, 0)).getBlock().equals(Blocks.AIR) && !HolePushPlus.mc.world.getBlockState(pPos.add(1, 0, 0)).getBlock().equals(Blocks.REDSTONE_BLOCK) && !HolePushPlus.mc.world.getBlockState(pPos).getBlock().equals(Blocks.AIR) && HolePushPlus.mc.world.getBlockState(pPos.add(1, 2, 0)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(1, 1, 0), HolePushPlus.mc.player.getDistance(pPos.add(1, 1, 0).getX(), pPos.add(1, 1, 0).getY(), pPos.add(1, 1, 0).getZ()));
        }
        if (HolePushPlus.mc.world.getBlockState(pPos.add(-1, 1, 0)).getBlock().equals(Blocks.AIR) && this.isAABBBlocked(pPos.add(-1, 1, 0)) && HolePushPlus.mc.world.getBlockState(pPos.add(1, 1, 0)).getBlock().equals(Blocks.AIR) && HolePushPlus.mc.world.getBlockState(pPos.add(1, 2, 0)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(-1, 1, 0), HolePushPlus.mc.player.getDistance(pPos.add(-1, 1, 0).getX(), pPos.add(-1, 1, 0).getY(), pPos.add(-1, 1, 0).getZ()));
        } else if (HolePushPlus.mc.world.getBlockState(pPos.add(-1, 1, 0)).getBlock().equals(Blocks.AIR) && this.isAABBBlocked(pPos.add(-1, 1, 0)) && !HolePushPlus.mc.world.getBlockState(pPos.add(-1, 0, 0)).getBlock().equals(Blocks.AIR) && !HolePushPlus.mc.world.getBlockState(pPos.add(-1, 0, 0)).getBlock().equals(Blocks.REDSTONE_BLOCK) && !HolePushPlus.mc.world.getBlockState(pPos).getBlock().equals(Blocks.AIR) && HolePushPlus.mc.world.getBlockState(pPos.add(-1, 2, 0)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(-1, 1, 0), HolePushPlus.mc.player.getDistance(pPos.add(-1, 1, 0).getX(), pPos.add(-1, 1, 0).getY(), pPos.add(-1, 1, 0).getZ()));
        }
        if (HolePushPlus.mc.world.getBlockState(pPos.add(0, 1, 1)).getBlock().equals(Blocks.AIR) && this.isAABBBlocked(pPos.add(0, 1, 1)) && HolePushPlus.mc.world.getBlockState(pPos.add(0, 1, -1)).getBlock().equals(Blocks.AIR) && HolePushPlus.mc.world.getBlockState(pPos.add(0, 2, -1)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(0, 1, 1), HolePushPlus.mc.player.getDistance(pPos.add(0, 1, 1).getX(), pPos.add(0, 1, 1).getY(), pPos.add(0, 1, 1).getZ()));
        } else if (HolePushPlus.mc.world.getBlockState(pPos.add(0, 1, 1)).getBlock().equals(Blocks.AIR) && this.isAABBBlocked(pPos.add(0, 1, 1)) && !HolePushPlus.mc.world.getBlockState(pPos.add(0, 0, 1)).getBlock().equals(Blocks.AIR) && !HolePushPlus.mc.world.getBlockState(pPos.add(0, 0, 1)).getBlock().equals(Blocks.REDSTONE_BLOCK) && !HolePushPlus.mc.world.getBlockState(pPos).getBlock().equals(Blocks.AIR) && HolePushPlus.mc.world.getBlockState(pPos.add(0, 2, 1)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(0, 1, 1), HolePushPlus.mc.player.getDistance(pPos.add(0, 1, 1).getX(), pPos.add(0, 1, 1).getY(), pPos.add(0, 1, 1).getZ()));
        }
        if (HolePushPlus.mc.world.getBlockState(pPos.add(0, 1, -1)).getBlock().equals(Blocks.AIR) && this.isAABBBlocked(pPos.add(0, 1, -1)) && HolePushPlus.mc.world.getBlockState(pPos.add(0, 1, 1)).getBlock().equals(Blocks.AIR) && HolePushPlus.mc.world.getBlockState(pPos.add(0, 2, 1)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(0, 1, -1), HolePushPlus.mc.player.getDistance(pPos.add(0, 1, -1).getX(), pPos.add(0, 1, -1).getY(), pPos.add(0, 1, -1).getZ()));
        } else if (HolePushPlus.mc.world.getBlockState(pPos.add(0, 1, -1)).getBlock().equals(Blocks.AIR) && this.isAABBBlocked(pPos.add(0, 1, -1)) && !HolePushPlus.mc.world.getBlockState(pPos).getBlock().equals(Blocks.AIR) && HolePushPlus.mc.world.getBlockState(pPos.add(0, 2, -1)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(0, 1, -1), HolePushPlus.mc.player.getDistance(pPos.add(0, 1, -1).getX(), pPos.add(0, 1, -1).getY(), pPos.add(0, 1, -1).getZ()));
        }
        ArrayList list = new ArrayList(distantMap.entrySet());
        list.sort(Map.Entry.comparingByValue());
        if (list.size() == 0) {
            return null;
        }
        if (((BlockPos)((Map.Entry)list.get(0)).getKey()).equals(pPos.add(1, 1, 0))) {
            this.rotate = -90;
        }
        if (((BlockPos)((Map.Entry)list.get(0)).getKey()).equals(pPos.add(-1, 1, 0))) {
            this.rotate = 90;
        }
        if (((BlockPos)((Map.Entry)list.get(0)).getKey()).equals(pPos.add(0, 1, 1))) {
            this.rotate = 0;
        }
        if (((BlockPos)((Map.Entry)list.get(0)).getKey()).equals(pPos.add(0, 1, -1))) {
            this.rotate = 180;
        }
        return (BlockPos)((Map.Entry)list.get(0)).getKey();
    }

    public boolean isAABBBlocked(BlockPos pos) {
        if (this.ignoreBBox.getValue().booleanValue()) {
            return true;
        }
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos);
        boolean i = false;
        List l = HolePushPlus.mc.world.getEntitiesWithinAABBExcludingEntity(null, axisAlignedBB);
        return l.size() == 0;
    }

    public String getStageString(String retur1){
        if(stage == 1)
            retur1 = "1";
        return retur1;
    }

    public String getDisplayInfo() {
        if (target == null)
            return "No Target";
        return target.getName() + "|" +stage;
    }
}
