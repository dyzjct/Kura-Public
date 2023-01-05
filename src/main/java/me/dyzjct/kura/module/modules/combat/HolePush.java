package me.dyzjct.kura.module.modules.combat;

import me.dyzjct.kura.friend.FriendManager;
import me.dyzjct.kura.manager.SpeedManager;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.module.modules.misc.InstantMine;
import me.dyzjct.kura.setting.*;
import me.dyzjct.kura.utils.block.BlockUtil;
import me.dyzjct.kura.utils.entity.EntityUtil;
import me.dyzjct.kura.utils.fn.BlockHelper;
import me.dyzjct.kura.utils.fn.CombatUtil;
import me.dyzjct.kura.utils.fn.PlayerSpoofUtil;
import me.dyzjct.kura.utils.fn.TimerUtil;
import me.dyzjct.kura.utils.inventory.InventoryUtil;
import me.dyzjct.kura.utils.math.RotationUtil;
import me.dyzjct.kura.utils.mc.ChatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Module.Info(name = "HolePush", category = Category.COMBAT)
public class HolePush extends Module {
    public static boolean hasCry = false;
    public static boolean oldCry = false;
    public static int var = 0;
    static int rotate;
    private final ModeSetting setting = msetting("Page", page.Push);
    private final BooleanSetting ignoreBBox = bsetting("IgnoreAxisAlignedBB", true).m(setting, page.Push);
    private final FloatSetting targetRange = fsetting("TargetMaxRange", 4.0F, 1.0F, 6.0F).m(setting, page.Target);
    private final DoubleSetting minRange = dsetting("TargetMinRange", 2.6D, 0.0D, 4.0D).m(setting, page.Target);
    private final DoubleSetting maxTargetSpeed = dsetting("TargetMaxMoveSpeed", 4.0D, 0.0D, 15.0D).m(setting, page.Target);
    private final ModeSetting surroundCheck = msetting("HoleCheck", surroundCheckMode.Normal).m(setting, page.Target);
    private final BooleanSetting burCheck = bsetting("SelfFillCheck", true).m(setting, page.Target);
    private final BooleanSetting forcePlaceBind = bsetting("ForceTargetPush", false).m(setting, page.Target);
    private final IntegerSetting minArmorPieces = isetting("MinTargetArmorPieces", 3, 0, 4).m(setting, page.Target);
    private final BooleanSetting onlyPushOnGround = bsetting("TargetOnGroundCheck", true).m(setting, page.Target);
    private final DoubleSetting targetMinHP = dsetting("TargetMinHP", 11.0D, 0.0D, 36.0D).m(setting, page.Target);
    private final IntegerSetting delay = isetting("PushDelay", 200, 0, 1000).m(setting, page.Push);
    private final DoubleSetting circulateDelay = dsetting("RunPushTaskDelay", 0.0D, 0.0D, 200.0D).m(setting, page.Push);
    private final ModeSetting feetPlace = msetting("FootPlace", FeetPlaceMode.Obsidian).m(setting, page.Push);
    private final DoubleSetting placeRange = dsetting("PlaceRange", 5.0D, 0.0D, 6.0D).m(setting, page.Push);
    private final BooleanSetting noOutOfDistancePlace = bsetting("OutOfDistancePlaceCheck", true).m(setting, page.Push);
    private final BooleanSetting checkPlaceable = bsetting("PlaceableCheck", false).m(setting, page.Push);
    private final BooleanSetting farPlace = bsetting("FarPlace", false).m(setting, page.Push);
    private final BooleanSetting noPlacePisOnBreakPos = bsetting("PistonPrePlaceChecker", true).m(setting, page.Push);
    private final BooleanSetting noPlaceRstOnBreakPos = bsetting("ElectronicPrePlaceCheker", true).m(setting, page.Push);
    private final IntegerSetting advanceMine = isetting("AdvanceMineOnStage", 2, 0, 3).m(setting, page.Push);
    private final BooleanSetting attackCry = bsetting("AttackCrystal", true).m(setting, page.CrystalAttack);
    private final BooleanSetting crystalAttackSwing = bsetting("CrystalAttackSwing", true).b(attackCry).m(setting, page.CrystalAttack);
    private final BooleanSetting crystalAttackRotate = bsetting("CrystalAttackRotate", true).b(attackCry).m(setting, page.CrystalAttack);
    private final BooleanSetting crystalPacketAttack = bsetting("CrystalPacketAttack", true).b(attackCry).m(setting, page.CrystalAttack);
    private final IntegerSetting attackRange = isetting("AttackCrystalRange", 5,0, 7).b(attackCry).m(setting, page.CrystalAttack);
    private final IntegerSetting noSuicide = isetting("NoSuicideHealth", 5,0, 36).b(attackCry).m(setting, page.CrystalAttack);
    private final BooleanSetting disableOnNoBlock = bsetting("DisableOnNoBlock", true).m(setting, page.Push);
    private final BooleanSetting onGroundCheck = bsetting("OnGroundCheck", true).m(setting, page.Push);
    private final IntegerSetting count = isetting("MaxTryDoPushCount", 20, 0, 200).m(setting, page.Push);
    private final BooleanSetting speedCheck = bsetting("MoveSpeedCheck", true).m(setting, page.Push);
    private final DoubleSetting maxSpeed = dsetting("SelfMaxMoveSpeed", 4.0D, 0.0D, 20.0D).b(speedCheck).m(setting, page.Push);
    private final BooleanSetting noPushSelf = bsetting("NoSelfPush", true).m(setting, page.Push);
    private final ModeSetting onUpdateMode = msetting("CrystalCheckUpdateMode", caCheckMode.Tick).m(setting, page.Push);
    private final DoubleSetting cryRange = dsetting("CrystalRange", 5.0D, 0.0D, 8.0D).m(setting, page.Push);
    private final IntegerSetting maxCount = isetting("MaxCount", 30, 2, 50).m(setting, page.Push);
    private final IntegerSetting balance = isetting("Balance", 17, 2, this.maxCount.getMax()).m(setting, page.Push);
    private final IntegerSetting cryWeight = isetting("CrystalWeight", 5, 1, 10).m(setting, page.Push);
    private final BooleanSetting raytrace = bsetting("RayTrace", false).m(setting, page.Push);
    private final BooleanSetting strictRotate = bsetting("RotateMode", false).m(setting, page.Push);
    private final BooleanSetting packetMine = bsetting("PacketMine", true).m(setting, page.Mine);
    private final IntegerSetting packSwichCount = isetting("MaxTryDoPacketMineCount", 5,0, 20).b(packetMine).m(setting, page.Mine);
    private final DoubleSetting mineDelay = dsetting("MineDelay", 20.0D, 0.0D, 400.0D).m(setting, page.Mine);
    private final BooleanSetting deBugMode = bsetting("DebugMessage", false);
    public PlayerSpoofUtil playerSpoofUtil = new PlayerSpoofUtil();
    TimerUtil mineTimer = new TimerUtil();
    TimerUtil timer = new TimerUtil();
    int stage = 0;
    int ct = 0;
    int ct1 = 0;
    int mineCount = 0;
    PushInfo info;
    BlockPos piston;
    BlockPos rst;
    boolean pull;
    EntityPlayer target;

    public static void pMine(BlockPos minePos) {
        mc.playerController.onPlayerDamageBlock(minePos, BlockUtil.getRayTraceFacing(minePos));
//        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, minePos, BlockUtil.getRayTraceFacing(minePos)));
//        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, minePos, BlockUtil.getRayTraceFacing(minePos)));
    }

    public static boolean headCheck(BlockPos playerPos) {
        return (mc.world.getBlockState(playerPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(playerPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR));
    }

    public static boolean caCheck(double checkRange, int min, int max, int baseValue, int weight, boolean onlyGet) {
        if (onlyGet)
            return (var <= baseValue);
        if (min >= max || baseValue >= max || baseValue <= min)
            return false;
        ArrayList<Entity> crystalList = new ArrayList<>();
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof net.minecraft.entity.item.EntityEnderCrystal && mc.player.getDistance(entity.posX, entity.posY, entity.posZ) < checkRange)
                crystalList.add(entity);
        }
        hasCry = crystalList.size() != 0;
        if (hasCry != oldCry) {
            oldCry = hasCry;
            var += weight;
        } else {
            var--;
        }
        if (var >= max)
            var = max;
        if (var <= min)
            var = min;
        return (var <= baseValue);
    }

    public static boolean isNoBBoxBlocked(BlockPos pos) {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos);
        List<Entity> l = mc.world.getEntitiesWithinAABBExcludingEntity(null, axisAlignedBB);
        for (Entity entity : l) {
            if (entity instanceof net.minecraft.entity.item.EntityEnderCrystal || entity instanceof net.minecraft.entity.item.EntityItem || entity instanceof net.minecraft.entity.projectile.EntityArrow || entity instanceof net.minecraft.entity.projectile.EntityTippedArrow || entity instanceof net.minecraft.entity.projectile.EntityArrow ||
                    entity instanceof net.minecraft.entity.item.EntityXPOrb)
                continue;
            return false;
        }
        return true;
    }

    public void onEnable() {
        this.piston = null;
        this.rst = null;
        this.target = null;
        this.stage = 0;
        this.ct = 0;
        this.ct1 = 0;
    }

    public void onTick() {
        if (this.onUpdateMode.getValue() == caCheckMode.Tick)
            caCheck(this.cryRange.getValue(), 0, this.maxCount.getValue(), this.balance.getValue(), this.cryWeight.getValue(), false);
    }

    public void onUpdate() {
        if (this.onUpdateMode.getValue() == caCheckMode.onUpdate)
            caCheck(this.cryRange.getValue(), 0, this.maxCount.getValue(), this.balance.getValue(), this.cryWeight.getValue(), false);
        int oldSlot = mc.player.inventory.currentItem;
        int obbySlot = InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        int pisSlot = InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.PISTON));
        if (pisSlot == -1) {
            pisSlot = InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.STICKY_PISTON));
            if (pisSlot == -1) {
                if (this.disableOnNoBlock.getValue()) {
                    ChatUtil.sendMessage("HolePush was not detected as PISTON/STICKY_PISTON in Inventory! Disable!");
                    disable();
                }
                return;
            }
        }
        int rstSlot = InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK));
        if (rstSlot == -1) {
            if (this.disableOnNoBlock.getValue()) {
                ChatUtil.sendMessage("HolePush was not detected as REDSTONE_BLOCK in Inventory");
                disable();
            }
            return;
        }
        if (this.stage == 0) {
            if (!this.timer.passedDms(this.delay.getValue()))
                return;
            EntityPlayer target = getTarget(this.targetRange.getValue());
            this.target = target;
            if (getPistonPos(target, this.raytrace.getValue()) == null)
                return;
            this.info = getPistonPos(target, this.raytrace.getValue());
            this.pull = this.info.pullMode;
            this.piston = this.info.pistonPos;
            this.rst = this.info.rstPos;
            this.ct = 0;
            this.ct1 = 0;
            this.stage++;
        }
        if (this.stage == 1) {
            if (!this.timer.passedDms(this.delay.getValue()))
                return;
            this.timer.reset();
            if (this.attackCry.getValue())
                attackCrystal();
            if (this.feetPlace.getValue().equals(FeetPlaceMode.RedStone) && mc.world.getBlockState(this.piston.add(0, -1, 0)).getBlock().equals(Blocks.AIR) && isNoBBoxBlocked(this.piston.add(0, -1, 0))) {
                if (this.noOutOfDistancePlace.getValue() && Math.sqrt(mc.player.getDistanceSq(this.piston.add(0, -1, 0))) > this.placeRange.getValue()) {
                    this.stage = 0;
                    return;
                }
                InventoryUtil.switchToHotbarSlot(rstSlot, false);
                BlockUtil.placeBlock(this.piston.add(0, -1, 0), EnumHand.MAIN_HAND, false, true, true);
                InventoryUtil.switchToHotbarSlot(oldSlot, false);
            }
            if (this.feetPlace.getValue().equals(FeetPlaceMode.Obsidian) && mc.world.getBlockState(this.piston.add(0, -1, 0)).getBlock().equals(Blocks.AIR) && isNoBBoxBlocked(this.piston.add(0, -1, 0))) {
                if (obbySlot == -1) {
                    this.stage = 2;
                    return;
                }
                if (this.noOutOfDistancePlace.getValue() && Math.sqrt(mc.player.getDistanceSq(this.piston.add(0, -1, 0))) > this.placeRange.getValue()) {
                    this.stage = 0;
                    return;
                }
                InventoryUtil.switchToHotbarSlot(obbySlot, false);
                BlockUtil.placeBlock(this.piston.add(0, -1, 0), EnumHand.MAIN_HAND, false, true, true);
                InventoryUtil.switchToHotbarSlot(oldSlot, false);
            }
            if (this.advanceMine.getValue() == 1)
                mineRst(this.target, this.piston);
            this.stage++;
        }
        if (this.stage == 2) {
            if (!this.timer.passedDms(this.delay.getValue()))
                return;
            this.timer.reset();
            if (mc.world.getBlockState(this.piston).getBlock().equals(Blocks.PISTON) || mc.world.getBlockState(this.piston).getBlock().equals(Blocks.STICKY_PISTON)) {
                this.stage++;
                return;
            }
            if (!mc.world.getBlockState(this.piston).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(this.piston).getBlock().equals(Blocks.PISTON) && !mc.world.getBlockState(this.piston).getBlock().equals(Blocks.STICKY_PISTON)) {
                this.stage = 0;
                return;
            }
            if (this.noOutOfDistancePlace.getValue() && Math.sqrt(mc.player.getDistanceSq(this.piston)) > this.placeRange.getValue()) {
                this.stage = 0;
                return;
            }
            InventoryUtil.switchToHotbarSlot(pisSlot, false);
            if (!this.strictRotate.getValue())
                if (this.info.pisFac == EnumFacing.EAST) {
                    RotationUtil.faceYawAndPitch(90.0F, 0.0F);
                } else if (this.info.pisFac == EnumFacing.WEST) {
                    RotationUtil.faceYawAndPitch(-90.0F, 0.0F);
                } else if (this.info.pisFac == EnumFacing.NORTH) {
                    RotationUtil.faceYawAndPitch(0.0F, 0.0F);
                } else if (this.info.pisFac == EnumFacing.SOUTH) {
                    RotationUtil.faceYawAndPitch(180.0F, 0.0F);
                }
            BlockUtil.placeBlock(this.piston, EnumHand.MAIN_HAND, false, true, true);
            InventoryUtil.switchToHotbarSlot(oldSlot, false);
            if ((mc.world.getBlockState(this.piston).getBlock().equals(Blocks.PISTON) || !this.checkPlaceable.getValue()) && isNoBBoxBlocked(this.piston)) {
                this.stage++;
                if (this.advanceMine.getValue() == 2)
                    mineRst(this.target, this.piston);
            } else {
                this.ct1++;
                if (this.ct1 > this.count.getValue())
                    this.stage = 0;
            }
        }
        if (this.stage == 3) {
            if (BlockHelper.haveNeighborBlock(this.piston, Blocks.REDSTONE_BLOCK).size() > 0) {
                this.mineTimer.reset();
                this.stage = 4;
                return;
            }
            if (isNoBBoxBlocked(this.rst) && (!this.checkPlaceable.getValue() || mc.world.getBlockState(this.piston).getBlock().equals(Blocks.PISTON) || mc.world.getBlockState(this.piston).getBlock().equals(Blocks.STICKY_PISTON))) {
                if (!mc.world.getBlockState(this.rst).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(this.rst).getBlock().equals(Blocks.REDSTONE_BLOCK)) {
                    this.stage = 0;
                    return;
                }
                InventoryUtil.switchToHotbarSlot(rstSlot, false);
                BlockUtil.placeBlock(this.rst, EnumHand.MAIN_HAND, false, true, true);
                InventoryUtil.switchToHotbarSlot(oldSlot, false);
                if (mc.world.getBlockState(this.rst).getBlock().equals(Blocks.REDSTONE_BLOCK)) {
                    this.mineTimer.reset();
                    this.stage = 4;
                    if (this.advanceMine.getValue() == 3)
                        mineRst(this.target, this.piston);
                }
                this.ct++;
                if (this.ct > this.count.getValue())
                    this.stage = 0;
            } else {
                this.stage = 0;
            }
        }
        if (this.stage == 4) {
            if (!this.pull) {
                this.stage = 0;
                return;
            }
            if (!this.mineTimer.passedDms(this.mineDelay.getValue()))
                return;
            mineRst(this.target, this.piston);
            this.stage = 0;
        }
    }

    public void mineRst(EntityPlayer target, BlockPos piston) {
        if (mc.world.getBlockState(BlockHelper.getFlooredPosition(target).add(0, 2, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(piston.add(0, -1, 0)).getBlock().equals(Blocks.AIR) && BlockHelper.haveNeighborBlock(piston, Blocks.REDSTONE_BLOCK).size() == 1) {
            BlockPos minePos = BlockHelper.haveNeighborBlock(piston, Blocks.REDSTONE_BLOCK).get(0);
            if (minePos != null && (InstantMine.breakPos == null || !InstantMine.breakPos.equals(minePos)))
                if (this.packetMine.getValue()) {
                    if (mc.world.getBlockState(minePos).getBlock().equals(Blocks.REDSTONE_BLOCK)) {
                        this.mineCount++;
                    } else {
                        this.mineCount = 0;
                    }
                    if (this.mineCount >= this.packSwichCount.getValue()) {
                        int oldslot = mc.player.inventory.currentItem;
                        int pickaxeitem = InventoryUtil.findHotbarBlock(ItemPickaxe.class);
                        this.playerSpoofUtil.spoofHotBar(pickaxeitem);
                        pMine(minePos);
                        InventoryUtil.switchToHotbarSlot(oldslot, false);
                        return;
                    }
                    pMine(minePos);
                } else {
                    mc.playerController.onPlayerDamageBlock(minePos, BlockUtil.getRayTraceFacing(minePos));
                }
        }
    }

    public boolean isSur(Entity player, surroundCheckMode checkMode) {
        BlockPos playerPos = BlockHelper.getFlooredPosition(player);
        if (checkMode == surroundCheckMode.Normal && !mc.world.getBlockState(playerPos.add(1, 0, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(playerPos.add(-1, 0, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(playerPos.add(0, 0, 1)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(playerPos.add(0, 0, -1)).getBlock().equals(Blocks.AIR))
            return true;
        if (checkMode == surroundCheckMode.Center) {
            double x = Math.abs(player.posX) - Math.floor(Math.abs(player.posX));
            double z = Math.abs(player.posZ) - Math.floor(Math.abs(player.posZ));
            if (x <= 0.7D && x >= 0.3D && z <= 0.7D && z >= 0.3D)
                return true;
        }
        if (checkMode == surroundCheckMode.Smart)
            return ((!mc.world.getBlockState(playerPos.add(1, 0, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(playerPos.add(1, 1, 0)).getBlock().equals(Blocks.AIR)) && (!mc.world.getBlockState(playerPos.add(-1, 0, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(playerPos.add(-1, 1, 0)).getBlock().equals(Blocks.AIR)) && (!mc.world.getBlockState(playerPos.add(0, 0, 1)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(playerPos.add(0, 1, 1)).getBlock().equals(Blocks.AIR)) && (!mc.world.getBlockState(playerPos.add(0, 0, -1)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(playerPos.add(0, 0, -1)).getBlock().equals(Blocks.AIR)));
        return (checkMode == surroundCheckMode.Disable);
    }

    public boolean helpingBlockCheck(BlockPos pos) {
        return (!mc.world.getBlockState(pos.add(1, 0, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.add(0, -1, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.add(0, 0, -1)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.add(0, 0, 1)).getBlock().equals(Blocks.AIR));
    }

    private EntityPlayer getTarget(double range) {
        if (!caCheck(this.cryRange.getValue(), 0, this.maxCount.getValue(), this.balance.getValue(), this.cryWeight.getValue(), true)) {
            if (this.deBugMode.getValue())
                ChatUtil.sendMessage("CIF:" + var);
            return null;
        }
        if (this.onGroundCheck.getValue() && !mc.player.onGround) {
            if (this.deBugMode.getValue())
                ChatUtil.sendMessage("HolePush was not detected as You on Ground!");
            return null;
        }
        EntityPlayer target = null;
        double distance = range;
        if (this.speedCheck.getValue() && SpeedManager.getPlayerSpeed(mc.player) > this.maxSpeed.getValue()) {
            if (this.deBugMode.getValue())
                ChatUtil.sendMessage("HolePush was detected as Move Speed High! Disable!");
            return null;
        }
        for (EntityPlayer player : mc.world.playerEntities) {
            if (getPistonPos(player, this.raytrace.getValue()) == null)
                continue;
            BlockPos pistonPos = (getPistonPos(player, this.raytrace.getValue())).pistonPos;
            BlockPos rstPos = (getPistonPos(player, this.raytrace.getValue())).rstPos;
            if (EntityUtil.isntValid(player, range) || FriendManager.isFriend(player.getName()) || SpeedManager.getPlayerSpeed(player) > this.maxTargetSpeed.getValue() || pistonPos == null)
                continue;
            if (rstPos == null)
                continue;
            if (CombatUtil.getArmorPieces(player) < this.minArmorPieces.getValue()) {
                if (!this.deBugMode.getValue())
                    continue;
                ChatUtil.sendMessage("HolePush was detected as " + player.getName() + "Low Armor!Pieces:" + CombatUtil.getArmorPieces(player));
                continue;
            }
            if (this.onlyPushOnGround.getValue() && !player.onGround) {
                if (!this.deBugMode.getValue())
                    continue;
                ChatUtil.sendMessage("HolePush was detected as " + player.getName() + "NoGround!");
                continue;
            }
            if (this.targetMinHP.getValue() > player.getHealth()) {
                if (!this.deBugMode.getValue())
                    continue;
                ChatUtil.sendMessage("HolePush was detected as " + player.getName() + "Low Hp!Hp:" + player.getHealth());
                continue;
            }
            if (this.surroundCheck.getValue() != surroundCheckMode.Disable && !(this.forcePlaceBind.getValue())) {
                boolean p = true;
                if (!isSur(player, (surroundCheckMode) this.surroundCheck.getValue())) {
                    if (this.burCheck.getValue()) {
                        if (mc.world.getBlockState(BlockHelper.getFlooredPosition(player)).getBlock().equals(Blocks.AIR)) {
                            if (this.deBugMode.getValue())
                                ChatUtil.sendMessage("HolePush was not detected as " + player.getName() + "In Surround and SelfFill!!");
                            continue;
                        }
                        p = false;
                    }
                    if (p) {
                        if (this.deBugMode.getValue())
                            ChatUtil.sendMessage("HolePush was not detected as " + player.getName() + "In Surround!!");
                        continue;
                    }
                }
            }
            if (this.noPushSelf.getValue() && BlockHelper.getFlooredPosition(player).equals(BlockHelper.getFlooredPosition(mc.player))) {
                if (!this.deBugMode.getValue())
                    continue;
                ChatUtil.sendMessage("Unsafe PushTask detected by HolePush (may cause SelfPush)!!!");
                continue;
            }
            if ((mc.player.posY - player.posY <= -1.0D || mc.player.posY - player.posY >= 2.0D) && distanceToXZ(pistonPos.getX() + 0.5D, pistonPos.getZ() + 0.5D) < this.minRange.getValue()) {
                if (!this.deBugMode.getValue())
                    continue;
                ChatUtil.sendMessage("HolePush Can't Place Facing" + player.getName());
                continue;
            }
            if (target == null) {
                target = player;
                distance = mc.player.getDistanceSq(player);
                continue;
            }
            if (mc.player.getDistanceSq(player) >= distance)
                continue;
            target = player;
            distance = mc.player.getDistanceSq(player);
        }
        return target;
    }

    public double distanceToXZ(double x, double z) {
        double dx = mc.player.posX - x;
        double dz = mc.player.posZ - z;
        return Math.sqrt(dx * dx + dz * dz);
    }

    public void attackCrystal() {
        if (mc.player.getHealth() < this.noSuicide.getValue())
            return;
        ArrayList<Entity> crystalList = new ArrayList<>();
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof net.minecraft.entity.item.EntityEnderCrystal)
                crystalList.add(entity);
        }
        if (crystalList.size() == 0)
            return;
        HashMap<Entity, Double> distantMap = new HashMap<>();
        for (Entity crystal : crystalList) {
            if (mc.player.getDistance(crystal.posX, crystal.posY, crystal.posZ) < this.attackRange.getValue())
                distantMap.put(crystal, Double.valueOf(mc.player.getDistance(crystal.posX, crystal.posY, crystal.posZ)));
        }
        List<Map.Entry<Entity, Double>> list = new ArrayList<>(distantMap.entrySet());
        list.sort(Map.Entry.comparingByValue());
        if (list.size() == 0)
            return;
        if (((Double) ((Map.Entry) list.get(0)).getValue()) < 5.0D) {
            EntityUtil.attackEntity((Entity) ((Map.Entry) list.get(list.size() - 1)).getKey(), this.crystalPacketAttack.getValue(), this.crystalAttackSwing.getValue());
            if (this.crystalAttackRotate.getValue())
                RotationUtil.lookAtEntity((Entity) ((Map.Entry) list.get(list.size() - 1)).getKey());
        }
    }

    public BlockPos getRSTPos(BlockPos pistonPos, boolean helpBlockCheck, boolean instaMineCheck) {
        if (pistonPos == null)
            return null;
        if (BlockHelper.haveNeighborBlock(pistonPos, Blocks.REDSTONE_BLOCK).size() > 0 && isNoBBoxBlocked(pistonPos))
            return BlockHelper.haveNeighborBlock(pistonPos, Blocks.REDSTONE_BLOCK).get(0);
        ArrayList<BlockPos> placePosList = new ArrayList<>();
        placePosList.add(pistonPos.add(0, 1, 0));
        placePosList.add(pistonPos.add(0, -1, 0));
        placePosList.add(pistonPos.add(-1, 0, 0));
        placePosList.add(pistonPos.add(1, 0, 0));
        placePosList.add(pistonPos.add(0, 0, -1));
        placePosList.add(pistonPos.add(0, 0, 1));
        HashMap<BlockPos, Double> distantMap = new HashMap<>();
        for (BlockPos rSTPos : placePosList) {
            if (mc.world.getBlockState(rSTPos).getBlock().equals(Blocks.AIR) && isNoBBoxBlocked(rSTPos) && (!helpBlockCheck || helpingBlockCheck(rSTPos)) && (InstantMine.breakPos == null || !instaMineCheck || !InstantMine.breakPos.equals(rSTPos)))
                distantMap.put(rSTPos, Double.valueOf(mc.player.getDistanceSq(rSTPos)));
        }
        List<Map.Entry<BlockPos, Double>> list = new ArrayList<>(distantMap.entrySet());
        list.sort(Map.Entry.comparingByValue());
        if (list.size() == 0)
            return null;
        return (BlockPos) ((Map.Entry<?, ?>) list.get(0)).getKey();
    }

    public BlockPos getRSTPos2(BlockPos pistonPos, double range, boolean rayTrace, boolean instaMineCheck, boolean helpBlockCheck) {
        if (pistonPos == null)
            return null;
        if (BlockHelper.haveNeighborBlock(pistonPos, Blocks.REDSTONE_BLOCK).size() > 0 && isNoBBoxBlocked(pistonPos))
            return BlockHelper.haveNeighborBlock(pistonPos, Blocks.REDSTONE_BLOCK).get(0);
        ArrayList<BlockPos> placePosList = new ArrayList<>();
        placePosList.add(pistonPos.add(0, 1, 0));
        placePosList.add(pistonPos.add(-1, 0, 0));
        placePosList.add(pistonPos.add(1, 0, 0));
        placePosList.add(pistonPos.add(0, 0, -1));
        placePosList.add(pistonPos.add(0, 0, 1));
        HashMap<BlockPos, Double> distantMap = new HashMap<>();
        for (BlockPos rSTPos : placePosList) {
            if (!mc.world.getBlockState(rSTPos).getBlock().equals(Blocks.AIR) || !isNoBBoxBlocked(rSTPos) ||
                    Math.sqrt(mc.player.getDistanceSq(rSTPos)) > range)
                continue;
            if (rayTrace && !CombatUtil.rayTraceRangeCheck(rSTPos, 0.0D, 0.0D))
                continue;
            if (instaMineCheck && InstantMine.breakPos != null && InstantMine.breakPos.equals(rSTPos))
                continue;
            if (helpBlockCheck && !helpingBlockCheck(rSTPos))
                continue;
            distantMap.put(rSTPos, mc.player.getDistanceSq(rSTPos));
        }
        List<Map.Entry<BlockPos, Double>> list = new ArrayList<>(distantMap.entrySet());
        list.sort(Map.Entry.comparingByValue());
        if (list.size() == 0)
            return null;
        return (BlockPos) ((Map.Entry<?, ?>) list.get(0)).getKey();
    }

    public PushInfo getPistonPos(EntityPlayer player, boolean raytrace) {
        if (player == null || player.equals(mc.player))
            return null;
        BlockPos playerPos = BlockHelper.getFlooredPosition(player);
        if (mc.world.getBlockState(playerPos.add(0, 1, 0)).getBlock().equals(Blocks.PISTON_HEAD)) {
            if (!mc.world.getBlockState(playerPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR))
                return null;
            getPistonPos(this.target);
            EnumFacing headFac = BlockHelper.getFacing(playerPos.add(0, 1, 0));
            BlockPos pisPos = null;
            switch (headFac) {
                case EAST:
                    pisPos = playerPos.add(-1, 1, 0);
                    break;
                case WEST:
                    pisPos = playerPos.add(1, 1, 0);
                    break;
                case NORTH:
                    pisPos = playerPos.add(0, 1, 1);
                    break;
                case SOUTH:
                    pisPos = playerPos.add(0, 1, -1);
                    break;
            }
            if (pisPos != null && mc.world.getBlockState(pisPos).getBlock() instanceof net.minecraft.block.BlockPistonBase) {
                ArrayList<BlockPos> l = BlockHelper.haveNeighborBlock(pisPos, Blocks.REDSTONE_BLOCK);
                if (l.size() == 1) {
                    BlockPos rstPos = l.get(0);
                    if (raytrace && !CombatUtil.rayTraceRangeCheck(rstPos, 0.0D, 0.0D))
                        return null;
                    if (Math.sqrt(mc.player.getDistanceSq(rstPos)) > 6.0D)
                        return null;
                    return new PushInfo(pisPos, rstPos, headFac, true);
                }
            }
            return null;
        }
        if (!mc.world.getBlockState(playerPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR))
            return null;
        HashMap<PushInfo, Double> distantMap = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            int xOffSet = 0;
            int zOffSet = 0;
            EnumFacing Pisfac = EnumFacing.UP;
            if (i == 0) {
                xOffSet = 1;
                zOffSet = 0;
                Pisfac = EnumFacing.WEST;
            } else if (i == 1) {
                xOffSet = -1;
                zOffSet = 0;
                Pisfac = EnumFacing.EAST;
            } else if (i == 2) {
                xOffSet = 0;
                zOffSet = 1;
                Pisfac = EnumFacing.NORTH;
            } else if (i == 3) {
                xOffSet = 0;
                zOffSet = -1;
                Pisfac = EnumFacing.SOUTH;
            }
            if (mc.world.getBlockState(playerPos.add(-xOffSet, 1, -zOffSet)).getBlock().equals(Blocks.AIR) && ((mc.world.getBlockState(playerPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(playerPos.add(-xOffSet, 2, -zOffSet)).getBlock().equals(Blocks.AIR)) || mc.world.getBlockState(playerPos.add(-xOffSet, 0, -zOffSet)).getBlock().equals(Blocks.AIR))) {
                PushInfo pushInfo = new PushInfo(playerPos.add(xOffSet, 1, zOffSet), raytrace, this.noPlaceRstOnBreakPos.getValue(), Pisfac, false);
                if (pushInfo.check())
                    distantMap.put(pushInfo, Double.valueOf(Math.sqrt(mc.player.getDistanceSq(pushInfo.pistonPos))));
            } else if (!mc.world.getBlockState(playerPos).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(playerPos).getBlock().equals(Blocks.WEB) && mc.world.getBlockState(playerPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(playerPos.add(xOffSet, 2, zOffSet)).getBlock().equals(Blocks.AIR)) {
                PushInfo pushInfo = new PushInfo(playerPos.add(xOffSet, 1, zOffSet), raytrace, this.noPlaceRstOnBreakPos.getValue(), Pisfac, false);
                if (pushInfo.check())
                    distantMap.put(pushInfo, Double.valueOf(Math.sqrt(mc.player.getDistanceSq(pushInfo.pistonPos))));
            }
        }
        List<Map.Entry<PushInfo, Double>> list = new ArrayList<>(distantMap.entrySet());
        list.sort(Map.Entry.comparingByValue());
        int a = 0;
        if (farPlace.getValue()) {
            for (a = list.size() - 1; a >= 0; a--) {
                if ((!noPlacePisOnBreakPos.getValue() || !((PushInfo) ((Map.Entry<?, ?>) list.get(a)).getKey()).pistonPos.equals(InstantMine.breakPos)) && ((Double) ((Map.Entry) list.get(a)).getValue()) < this.placeRange.getValue()) {
                    if (!raytrace)
                        break;
                    if (CombatUtil.rayTraceRangeCheck(((PushInfo) ((Map.Entry<?, ?>) list.get(a)).getKey()).pistonPos, 0.0D, 0.0D))
                        break;
                }
            }
        } else {
            for (a = 0; a <= list.size() - 1; a++) {
                if ((!this.noPlacePisOnBreakPos.getValue() || !((PushInfo) ((Map.Entry<?, ?>) list.get(a)).getKey()).pistonPos.equals(InstantMine.breakPos)) && ((Double) ((Map.Entry) list.get(a)).getValue()) < this.placeRange.getValue()) {
                    if (!raytrace)
                        break;
                    if (CombatUtil.rayTraceRangeCheck(((PushInfo) ((Map.Entry<?, ?>) list.get(a)).getKey()).pistonPos, 0.0D, 0.0D))
                        break;
                }
            }
        }
        if (a == -1)
            return null;
        return (list.size() >= 1) ? (PushInfo) ((Map.Entry<?, ?>) list.get(0)).getKey() : null;
    }

    public BlockPos getPistonPos(EntityPlayer player) {
        if (player == null)
            return null;
        BlockPos pPos = CombatUtil.getFlooredPosition(player);
        if (!mc.world.getBlockState(pPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR))
            return null;
        HashMap<BlockPos, Double> distantMap = new HashMap<>();
        if (mc.world.getBlockState(pPos.add(1, 1, 0)).getBlock().equals(Blocks.AIR) && isAABBBlocked(pPos.add(1, 1, 0)) && mc.world.getBlockState(pPos.add(-1, 1, 0)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pPos.add(-1, 2, 0)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(1, 1, 0), mc.player.getDistance(pPos.add(1, 1, 0).getX(), pPos.add(1, 1, 0).getY(), pPos.add(1, 1, 0).getZ()));
        } else if (mc.world.getBlockState(pPos.add(1, 1, 0)).getBlock().equals(Blocks.AIR) && isAABBBlocked(pPos.add(1, 1, 0)) && !mc.world.getBlockState(pPos.add(1, 0, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(pPos.add(1, 0, 0)).getBlock().equals(Blocks.REDSTONE_BLOCK) && !mc.world.getBlockState(pPos).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pPos.add(1, 2, 0)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(1, 1, 0), mc.player.getDistance(pPos.add(1, 1, 0).getX(), pPos.add(1, 1, 0).getY(), pPos.add(1, 1, 0).getZ()));
        }
        if (mc.world.getBlockState(pPos.add(-1, 1, 0)).getBlock().equals(Blocks.AIR) && isAABBBlocked(pPos.add(-1, 1, 0)) && mc.world.getBlockState(pPos.add(1, 1, 0)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pPos.add(1, 2, 0)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(-1, 1, 0), mc.player.getDistance(pPos.add(-1, 1, 0).getX(), pPos.add(-1, 1, 0).getY(), pPos.add(-1, 1, 0).getZ()));
        } else if (mc.world.getBlockState(pPos.add(-1, 1, 0)).getBlock().equals(Blocks.AIR) && isAABBBlocked(pPos.add(-1, 1, 0)) && !mc.world.getBlockState(pPos.add(-1, 0, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(pPos.add(-1, 0, 0)).getBlock().equals(Blocks.REDSTONE_BLOCK) && !mc.world.getBlockState(pPos).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pPos.add(-1, 2, 0)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(-1, 1, 0), mc.player.getDistance(pPos.add(-1, 1, 0).getX(), pPos.add(-1, 1, 0).getY(), pPos.add(-1, 1, 0).getZ()));
        }
        if (mc.world.getBlockState(pPos.add(0, 1, 1)).getBlock().equals(Blocks.AIR) && isAABBBlocked(pPos.add(0, 1, 1)) && mc.world.getBlockState(pPos.add(0, 1, -1)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pPos.add(0, 2, -1)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(0, 1, 1), mc.player.getDistance(pPos.add(0, 1, 1).getX(), pPos.add(0, 1, 1).getY(), pPos.add(0, 1, 1).getZ()));
        } else if (mc.world.getBlockState(pPos.add(0, 1, 1)).getBlock().equals(Blocks.AIR) && isAABBBlocked(pPos.add(0, 1, 1)) && !mc.world.getBlockState(pPos.add(0, 0, 1)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(pPos.add(0, 0, 1)).getBlock().equals(Blocks.REDSTONE_BLOCK) && !mc.world.getBlockState(pPos).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pPos.add(0, 2, 1)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(0, 1, 1), mc.player.getDistance(pPos.add(0, 1, 1).getX(), pPos.add(0, 1, 1).getY(), pPos.add(0, 1, 1).getZ()));
        }
        if (mc.world.getBlockState(pPos.add(0, 1, -1)).getBlock().equals(Blocks.AIR) && isAABBBlocked(pPos.add(0, 1, -1)) && mc.world.getBlockState(pPos.add(0, 1, 1)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pPos.add(0, 2, 1)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(0, 1, -1), mc.player.getDistance(pPos.add(0, 1, -1).getX(), pPos.add(0, 1, -1).getY(), pPos.add(0, 1, -1).getZ()));
        } else if (mc.world.getBlockState(pPos.add(0, 1, -1)).getBlock().equals(Blocks.AIR) && isAABBBlocked(pPos.add(0, 1, -1)) && !mc.world.getBlockState(pPos).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pPos.add(0, 2, -1)).getBlock().equals(Blocks.AIR)) {
            distantMap.put(pPos.add(0, 1, -1), mc.player.getDistance(pPos.add(0, 1, -1).getX(), pPos.add(0, 1, -1).getY(), pPos.add(0, 1, -1).getZ()));
        }
        ArrayList<Map.Entry> list = new ArrayList<>(distantMap.entrySet());
        if (list.size() == 0)
            return null;
        if (list.get(0).getKey().equals(pPos.add(1, 1, 0))) {
            rotate = -90;
        }
        if (list.get(0).getKey().equals(pPos.add(-1, 1, 0))) {
            rotate = 90;
        }
        if (list.get(0).getKey().equals(pPos.add(0, 1, 1))) {
            rotate = 0;
        }
        if (list.get(0).getKey().equals(pPos.add(0, 1, -1))) {
            rotate = 180;
        }
        return (BlockPos) list.get(0).getKey();
    }

    public boolean isAABBBlocked(BlockPos pos) {
        if (this.ignoreBBox.getValue())
            return true;
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos);
        List<Entity> l = mc.world.getEntitiesWithinAABBExcludingEntity(null, axisAlignedBB);
        return (l.size() == 0);
    }

    public String getHudInfo() {
        if (this.target != null)
            return this.target.getName();
        return "No Target";
    }

    public enum FeetPlaceMode {
        Obsidian, RedStone
    }

    private enum page {
        Push, Mine, CrystalAttack, Target
    }

    private enum surroundCheckMode {
        Disable, Normal, Center, Smart
    }

    private enum caCheckMode {
        Tick, onUpdate
    }

    public class PushInfo {
        private final BlockPos rstPos;
        public BlockPos pistonPos;
        public EnumFacing pisFac;
        public boolean pullMode;

        public PushInfo(BlockPos pistonPos, BlockPos rstPos, EnumFacing pisFac, boolean pullMode) {
            this.pistonPos = pistonPos;
            this.rstPos = rstPos;
            this.pisFac = pisFac;
            this.pullMode = pullMode;
        }

        public PushInfo(BlockPos pistonPos, boolean rayTrace, boolean instaMineC, EnumFacing pisFac, boolean pullMode) {
            this.pistonPos = pistonPos;
            this.rstPos = HolePush.this.getRSTPos2(pistonPos, 6.0D, rayTrace, instaMineC, false);
            this.pisFac = pisFac;
            this.pullMode = pullMode;
        }

        public boolean check() {
            return (this.rstPos != null && this.pistonPos != null && (mc.world.getBlockState(this.pistonPos).getBlock().equals(Blocks.AIR) || (mc.world.getBlockState(this.pistonPos).getBlock().equals(Blocks.PISTON) && BlockHelper.isFacing(this.pistonPos, this.pisFac)) || (mc.world.getBlockState(this.pistonPos).getBlock().equals(Blocks.STICKY_PISTON) && BlockHelper.isFacing(this.pistonPos, this.pisFac))) && BlockHelper.isNoBBoxBlocked(this.pistonPos) && BlockHelper.isNoBBoxBlocked(this.rstPos) && (mc.world.getBlockState(this.rstPos).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(this.rstPos).getBlock().equals(Blocks.REDSTONE_BLOCK)));
        }
    }
}
