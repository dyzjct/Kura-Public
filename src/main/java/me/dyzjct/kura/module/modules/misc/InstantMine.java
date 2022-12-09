package me.dyzjct.kura.module.modules.misc;

import me.dyzjct.kura.event.events.render.RenderEvent;
import me.dyzjct.kura.event.events.PlayerDamageBlockEvent;
import me.dyzjct.kura.event.events.player.PacketEvent;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.module.modules.combat.AntiBurrow2;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.inventory.InventoryUtil;
import me.dyzjct.kura.utils.Timer;
import me.dyzjct.kura.utils.NTMiku.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Module.Info(name = "InstantMine",category = Category.MISC)
public class InstantMine
        extends Module {
    private final Timer breakSuccess = new Timer();
    private static InstantMine INSTANCE = new InstantMine();
    private BooleanSetting creatoveMode = bsetting("CreativeMode",true);
    private Setting<Boolean> ghosthand = bsetting("GhostHand",true).b(creatoveMode);
    private BooleanSetting render = bsetting("Fill",true);
    private Setting<Integer> falpha = isetting("FillAlpha",30,0,255).b(render);
    private BooleanSetting render2 = bsetting("Box",true);
    private Setting<Integer> balpha = isetting("BoxAlpha",100,0,255).b(render2);
    private final BooleanSetting crystal = bsetting("Crystal", true);
    private final Setting<Boolean> crystalp = bsetting("Crystal on Break",true).b(crystal);
    public final Setting<Boolean> attackcrystal = bsetting("Attack Crystal", true).b(crystal);
    //    public final Setting<BindSetting> bind = (new Setting<Object>("ObsidianBind", new Bind(-1), v -> this.crystal.getValue()));
    public BooleanSetting db = bsetting("Silent Double", true);
    public final Setting<Float> health = fsetting("Health", 18.0f, 0.0f, 35.9f).b(db);
    private Setting<Integer> red = isetting("Red", 255, 0, 255);
    private Setting<Integer> green = isetting("Green", 255, 0, 255);
    private Setting<Integer> blue = isetting("Blue", 255, 0, 255);
    private Setting<Integer> alpha = isetting("BoxAlpha", 150, 0, 255);
    private Setting<Integer> alpha2 = isetting("FillAlpha", 70, 0, 255);
    private final List<Block> godBlocks = Arrays.asList(Blocks.AIR, Blocks.FLOWING_LAVA, Blocks.LAVA, Blocks.FLOWING_WATER, Blocks.WATER, Blocks.BEDROCK);
    private boolean cancelStart = false;
    private boolean empty = false;
    private EnumFacing facing;
    public static BlockPos breakPos;
    int slotMain2;
    int swithc2;
    public static BlockPos breakPos2;
    double manxi = 0.0;
    double manxi2 = 0.0;
    public final Timer imerS = new Timer();
    public final Timer imerS2 = new Timer();
    long times = 0L;
    static int ticked;


    public static InstantMine getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new InstantMine();
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) return;
        int slotMain;
        if (InstantMine.mc.player.isCreative()) {
            return;
        }
        this.slotMain2 = InstantMine.mc.player.inventory.currentItem;
        if (ticked <= 86 && ticked >= 0) {
            ++ticked;
        }
        if (breakPos2 == null) {
            this.manxi2 = 0.0;
        }
        if (breakPos2 != null && (ticked >= 65 || ticked >= 20 && InstantMine.mc.world.getBlockState(breakPos).getBlock() == Blocks.ENDER_CHEST)) {
            if (InstantMine.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.GOLDEN_APPLE || InstantMine.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.CHORUS_FRUIT) {
                if (!Mouse.isButtonDown((int)1)) {
                    if (InstantMine.mc.player.getHealth() + InstantMine.mc.player.getAbsorptionAmount() >= this.health.getValue().floatValue()) {
                        if (InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE) != -1 && this.db.getValue().booleanValue()) {
                            InstantMine.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(InventoryUtil.getItemHotbars(Items.DIAMOND_PICKAXE)));
                            this.swithc2 = 1;
                            ++ticked;
                        }
                    } else if (this.swithc2 == 1) {
                        InstantMine.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.slotMain2));
                        this.swithc2 = 0;
                    }
                } else if (this.swithc2 == 1) {
                    InstantMine.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.slotMain2));
                    this.swithc2 = 0;
                }
            } else if (InstantMine.mc.player.getHealth() + InstantMine.mc.player.getAbsorptionAmount() >= this.health.getValue().floatValue()) {
                if (InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE) != -1 && this.db.getValue().booleanValue()) {
                    InstantMine.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(InventoryUtil.getItemHotbars(Items.DIAMOND_PICKAXE)));
                    this.swithc2 = 1;
                    ++ticked;
                }
            } else if (this.swithc2 == 1) {
                InstantMine.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.slotMain2));
                this.swithc2 = 0;
            }
        }
        if (breakPos2 != null && InstantMine.mc.world.getBlockState(breakPos2).getBlock() == Blocks.AIR) {
            if (this.swithc2 == 1) {
                InstantMine.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.slotMain2));
                this.swithc2 = 0;
            }
            breakPos2 = null;
            this.manxi2 = 0.0;
            ticked = 0;
        }
        if (ticked == 0) {
            this.manxi2 = 0.0;
            breakPos2 = null;
        }
        if (ticked >= 140) {
            if (this.swithc2 == 1) {
                InstantMine.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.slotMain2));
                this.swithc2 = 0;
            }
            this.manxi2 = 0.0;
            breakPos2 = null;
            ticked = 0;
        }
        if (breakPos != null && InstantMine.mc.world.getBlockState(breakPos).getBlock() == Blocks.AIR && breakPos2 == null) {
            ticked = 0;
        }
        if (InstantMine.fullNullCheck()) {
            return;
        }
        if (!this.creatoveMode.getValue().booleanValue()) {
            return;
        }
        if (!this.cancelStart) {
            return;
        }
        if (this.crystal.getValue().booleanValue() && this.attackcrystal.getValue().booleanValue() && InstantMine.mc.world.getBlockState(breakPos).getBlock() == Blocks.AIR) {
            InstantMine.attackcrystal();
        }
//        if (this.bind.getValue().isDown() && this.crystal.getValue().booleanValue() && InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1 && InstantMine.mc.world.getBlockState(breakPos).getBlock() == Blocks.AIR) {
        if (false) {
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int old = InstantMine.mc.player.inventory.currentItem;
            this.switchToSlot(obbySlot);
            BlockUtil.placeBlock(breakPos, EnumHand.MAIN_HAND, false, true, false);
            this.switchToSlot(old);
        }
        if (InventoryUtil.getItemHotbar(Items.END_CRYSTAL) != -1 && this.crystal.getValue().booleanValue() && InstantMine.mc.world.getBlockState(breakPos).getBlock() == Blocks.OBSIDIAN && !breakPos.equals((Object)AntiBurrow2.pos)) {
            if (this.empty) {
                BlockUtil.placeCrystalOnBlock(breakPos, EnumHand.MAIN_HAND, true, false, true);
            } else if (!this.crystalp.getValue().booleanValue()) {
                BlockUtil.placeCrystalOnBlock(breakPos, EnumHand.MAIN_HAND, true, false, true);
            }
        }
        if (this.godBlocks.contains(InstantMine.mc.world.getBlockState(breakPos).getBlock())) {
            return;
        }
        if (InstantMine.mc.world.getBlockState(breakPos).getBlock() != Blocks.WEB) {
            if (this.ghosthand.getValue().booleanValue() && InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE) != -1 && InventoryUtil.getItemHotbars(Items.DIAMOND_PICKAXE) != -1) {
                slotMain = InstantMine.mc.player.inventory.currentItem;
                if (InstantMine.mc.world.getBlockState(breakPos).getBlock() == Blocks.OBSIDIAN) {
                    if (!this.breakSuccess.passedMs(1234L)) {
                        return;
                    }
                    InstantMine.mc.player.inventory.currentItem = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
                    InstantMine.mc.playerController.updateController();
                    InstantMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, this.facing));
                    InstantMine.mc.player.inventory.currentItem = slotMain;
                    InstantMine.mc.playerController.updateController();
                    return;
                }
                InstantMine.mc.player.inventory.currentItem = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
                InstantMine.mc.playerController.updateController();
                InstantMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, this.facing));
                InstantMine.mc.player.inventory.currentItem = slotMain;
                InstantMine.mc.playerController.updateController();
                return;
            }
        } else if (this.ghosthand.getValue().booleanValue() && InventoryUtil.getItemHotbar(Items.DIAMOND_SWORD) != -1 && InventoryUtil.getItemHotbars(Items.DIAMOND_SWORD) != -1) {
            slotMain = InstantMine.mc.player.inventory.currentItem;
            InstantMine.mc.player.inventory.currentItem = InventoryUtil.getItemHotbar(Items.DIAMOND_SWORD);
            InstantMine.mc.playerController.updateController();
            InstantMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, this.facing));
            InstantMine.mc.player.inventory.currentItem = slotMain;
            InstantMine.mc.playerController.updateController();
            return;
        }
        InstantMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, this.facing));
    }

    private void switchToSlot(int slot) {
        InstantMine.mc.player.inventory.currentItem = slot;
        InstantMine.mc.playerController.updateController();
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (!InstantMine.mc.player.isCreative()) {
            AxisAlignedBB axisAlignedBB1;
            double progressValZ;
            double progressValY;
            double progressValX;
            double centerZ;
            double centerY;
            double centerX;
            AxisAlignedBB axisAlignedBB;
            if (breakPos2 != null) {
                axisAlignedBB = InstantMine.mc.world.getBlockState(breakPos2).getSelectedBoundingBox((World)InstantMine.mc.world, breakPos2);
                centerX = axisAlignedBB.minX + (axisAlignedBB.maxX - axisAlignedBB.minX) / 2.0;
                centerY = axisAlignedBB.minY + (axisAlignedBB.maxY - axisAlignedBB.minY) / 2.0;
                centerZ = axisAlignedBB.minZ + (axisAlignedBB.maxZ - axisAlignedBB.minZ) / 2.0;
                progressValX = InstantMine.getInstance().manxi2 * ((axisAlignedBB.maxX - centerX) / 10.0);
                progressValY = InstantMine.getInstance().manxi2 * ((axisAlignedBB.maxY - centerY) / 10.0);
                progressValZ = InstantMine.getInstance().manxi2 * ((axisAlignedBB.maxZ - centerZ) / 10.0);
                axisAlignedBB1 = new AxisAlignedBB(centerX - progressValX, centerY - progressValY, centerZ - progressValZ, centerX + progressValX, centerY + progressValY, centerZ + progressValZ);
                if (breakPos != null) {
                    if (!breakPos2.equals((Object)breakPos)) {
//                        ChatUtil.sendMessage("SB");
                        RenderUtil.drawBBBox(axisAlignedBB1, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.alpha.getValue());
                        RenderUtil.drawBBFill(axisAlignedBB1, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha2.getValue()), this.alpha2.getValue());
                    }
                } else {
//                    ChatUtil.sendMessage("SB2");
                    RenderUtil.drawBBBox(axisAlignedBB1, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.alpha.getValue());
                    RenderUtil.drawBBFill(axisAlignedBB1, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha2.getValue()), this.alpha2.getValue());
                }
            }
            if (this.creatoveMode.getValue().booleanValue() && this.cancelStart) {
                if (this.godBlocks.contains(InstantMine.mc.world.getBlockState(breakPos).getBlock())) {
                    this.empty = true;
                }
                if (this.imerS.passedMs(15L)) {
                    if (this.manxi <= 10.0) {
                        this.manxi += 0.11;
                    }
                    this.imerS.reset();
                }
                if (this.imerS2.passedMs(22L)) {
                    if (this.manxi2 <= 10.0 && this.manxi2 >= 0.0) {
                        this.manxi2 += 0.11;
                    }
                    this.imerS2.reset();
                }
                axisAlignedBB = InstantMine.mc.world.getBlockState(breakPos).getSelectedBoundingBox((World)InstantMine.mc.world, breakPos);
                centerX = axisAlignedBB.minX + (axisAlignedBB.maxX - axisAlignedBB.minX) / 2.0;
                centerY = axisAlignedBB.minY + (axisAlignedBB.maxY - axisAlignedBB.minY) / 2.0;
                centerZ = axisAlignedBB.minZ + (axisAlignedBB.maxZ - axisAlignedBB.minZ) / 2.0;
                progressValX = this.manxi * ((axisAlignedBB.maxX - centerX) / 10.0);
                progressValY = this.manxi * ((axisAlignedBB.maxY - centerY) / 10.0);
                progressValZ = this.manxi * ((axisAlignedBB.maxZ - centerZ) / 10.0);
                axisAlignedBB1 = new AxisAlignedBB(centerX - progressValX, centerY - progressValY, centerZ - progressValZ, centerX + progressValX, centerY + progressValY, centerZ + progressValZ);
                if (this.render.getValue().booleanValue()) {
                    RenderUtil.drawBBFill(axisAlignedBB1, new Color(this.empty ? 0 : 255, this.empty ? 255 : 0, 0, 255), this.falpha.getValue());
                }
                if (this.render2.getValue().booleanValue()) {
                    RenderUtil.drawBBBox(axisAlignedBB1, new Color(this.empty ? 0 : 255, this.empty ? 255 : 0, 0, 255), this.balpha.getValue());
                }
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (InstantMine.fullNullCheck()) {
            return;
        }
        if (InstantMine.mc.player.isCreative()) {
            return;
        }
        if (!(event.getPacket() instanceof CPacketPlayerDigging)) {
            return;
        }
        CPacketPlayerDigging packet = (CPacketPlayerDigging)event.getPacket();
        if (packet.getAction() != CPacketPlayerDigging.Action.START_DESTROY_BLOCK) {
            return;
        }
        event.setCanceled(this.cancelStart);
    }

    public static void attackcrystal() {
        for (Entity crystal : InstantMine.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal && !e.isDead).sorted(Comparator.comparing(e -> Float.valueOf(InstantMine.mc.player.getDistance(e)))).collect(Collectors.toList())) {
            if (!(crystal instanceof EntityEnderCrystal) || !(crystal.getDistanceSq(breakPos) <= 2.0)) continue;
            InstantMine.mc.player.connection.sendPacket((Packet)new CPacketUseEntity(crystal));
            InstantMine.mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
        }
    }

    @SubscribeEvent
    public void onBlockEvent(PlayerDamageBlockEvent event) {
        if (InstantMine.fullNullCheck()) {
            return;
        }
        if (InstantMine.mc.player.isCreative()) {
            return;
        }
        if (!BlockUtil.canBreak(event.pos)) {
            return;
        }
        if (breakPos != null && breakPos.getX() == event.pos.getX() && breakPos.getY() == event.pos.getY() && breakPos.getZ() == event.pos.getZ()) {
            return;
        }
        if (ticked == 0) {
            ticked = 1;
        }
        if (this.manxi2 == 0.0) {
            this.manxi2 = 0.11;
        }
        if (breakPos != null && breakPos2 == null && InstantMine.mc.world.getBlockState(breakPos).getBlock() != Blocks.AIR) {
            breakPos2 = breakPos;
        }
        if (breakPos == null && breakPos2 == null) {
            breakPos2 = event.pos;
        }
        this.manxi = 0.0;
        this.empty = false;
        this.cancelStart = false;
        breakPos = event.pos;
        this.breakSuccess.reset();
        this.facing = event.facing;
        if (breakPos == null) {
            return;
        }
        InstantMine.mc.player.swingArm(EnumHand.MAIN_HAND);
        InstantMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, breakPos, this.facing));
        this.cancelStart = true;
        InstantMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, this.facing));
        event.setCanceled(true);
    }

    static {
        ticked = 0;
    }
}

