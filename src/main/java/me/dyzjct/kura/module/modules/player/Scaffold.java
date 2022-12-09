package me.dyzjct.kura.module.modules.player;

import me.dyzjct.kura.event.events.client.PacketEvents;
import me.dyzjct.kura.event.events.entity.MotionUpdateEvent;
import me.dyzjct.kura.event.events.render.RenderEvent;
import me.dyzjct.kura.manager.RotationManager;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.module.ModuleManager;
import me.dyzjct.kura.setting.BooleanSetting;
import me.dyzjct.kura.setting.IntegerSetting;
import me.dyzjct.kura.setting.ModeSetting;
import me.dyzjct.kura.utils.Wrapper;
import me.dyzjct.kura.utils.block.BlockInteractionHelper;
import me.dyzjct.kura.utils.entity.EntityUtil;
import me.dyzjct.kura.utils.gl.XG42Tessellator;
import me.dyzjct.kura.utils.inventory.InventoryUtil;
import me.dyzjct.kura.utils.mc.ChatUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("all")
@Module.Info(name = "Scaffold", category = Category.PLAYER)
public class Scaffold extends Module {
    public static Scaffold INSTANCE = new Scaffold();
    public List<Block> blackList;
    public ModeSetting<RotateMode> rotate = msetting("RotateMode", RotateMode.Hypixel);
    public ModeSetting<SwitchMode> switchMode = msetting("SwitchMode", SwitchMode.Silent);
    public ModeSetting<?> mode = msetting("TowerMode", Mode.NORMAL);
    public BooleanSetting lagbackCheck = bsetting("LagBackCheck", false);
    public BooleanSetting safeWalk = bsetting("SafeWalk", false);
    public IntegerSetting timer = isetting("Timer", 3, 1, 10).m(mode, Mode.TIMER);
    BlockPos lastPos;
    int newSlot;
    int currentItem;

    public Scaffold() {
        blackList = Arrays.asList(Blocks.CHEST, Blocks.TRAPPED_CHEST);
        newSlot = -1;
        lastPos = null;
    }

    public static IBlockState getState(BlockPos blockPos) {
        return Wrapper.getWorld().getBlockState(blockPos);
    }

    public static EnumActionResult processRightClickBlock(BlockPos blockPos, EnumFacing enumFacing, Vec3d vec3d) {
        return mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, enumFacing, vec3d, EnumHand.MAIN_HAND);
    }

    public static Block getBlock(BlockPos blockPos) {
        return getState(blockPos).getBlock();
    }

    public static boolean canBeClicked(BlockPos blockPos) {
        return getBlock(blockPos).canCollideCheck(getState(blockPos), false);
    }

    public static boolean isOnGround(double height) {
        return !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
    }

    @SubscribeEvent
    public void PacketEvent(PacketEvents.Receive event) {
        if (fullNullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = event.getPacket();
            if (lagbackCheck.getValue()) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(packet.getX(), packet.getY(), packet.getZ(), true));
                RotationManager.resetRotation();
                ChatUtil.sendErrorMessage("Rubberband Detected! Position Adjusted!");
            }
        }
    }

    @SubscribeEvent
    public void a(TickEvent.ClientTickEvent event) {
        if (fullNullCheck() || ModuleManager.getModuleByClass(Freecam.class).isEnabled()) {
            return;
        }
        lastPos = null;
        BlockPos down = new BlockPos(EntityUtil.getPlayerPos()).down();
        if (!Wrapper.getWorld().getBlockState(down).getMaterial().isReplaceable()) {
            return;
        }
        lastPos = down;
        if (!hasNeighbour(down)) {
            EnumFacing[] values = EnumFacing.values();
            for (int length = values.length, j = 0; j < length; ++j) {
                BlockPos offset = down.offset(values[j]);
                if (hasNeighbour(offset)) {
                    down = offset;
                    lastPos = down;
                }
            }
            return;
        }
        if (mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(down)).isEmpty() && mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(down)).isEmpty()) {
            for (EnumFacing enumFacing : EnumFacing.values()) {
                BlockPos offset2 = down.offset(enumFacing);
                EnumFacing getOpposite = enumFacing.getOpposite();
                if (canBeClicked(offset2)) {
                    lastPos = down;
                    return;
                }
            }
        }
        lastPos = null;
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        int hsBtoRGB = Color.HSBtoRGB((new float[]{
                System.currentTimeMillis() % 11520L / 11520.0f
        })[0], 0.5f, 1);
        int r = (hsBtoRGB >> 16 & 0xFF);
        int g = (hsBtoRGB >> 8 & 0xFF);
        int b = (hsBtoRGB & 0xFF);
        if (lastPos != null) {
            XG42Tessellator.prepare(GL11.GL_QUADS);
            XG42Tessellator.drawFullBox(lastPos, 1f, r, g, b, 80);
            XG42Tessellator.release();
        }
    }

    @SubscribeEvent
    public void Tick(MotionUpdateEvent.Tick event) {
        if (fullNullCheck()) {
            return;
        }
        currentItem = mc.player.inventory.currentItem;
        newSlot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack getStackInSlot = mc.player.inventory.getStackInSlot(i);
            if (getStackInSlot != ItemStack.EMPTY && getStackInSlot.getItem() instanceof ItemBlock) {
                Block getBlock = ((ItemBlock) getStackInSlot.getItem()).getBlock();
                if (!blackList.contains(getBlock) && !(((ItemBlock) getStackInSlot.getItem()).getBlock() instanceof BlockFalling)) {
                    newSlot = i;
                    break;
                }
            }
        }
        placeBlockScaffold(event, lastPos);
    }

    public boolean hasNeighbour(BlockPos blockPos) {
        EnumFacing[] values = EnumFacing.values();
        for (int length = values.length, i = 0; i < length; ++i) {
            if (!Wrapper.getWorld().getBlockState(blockPos.offset(values[i])).getMaterial().isReplaceable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }
        RotationManager.resetRotation();
        mc.timer.tickLength = 50f;
    }

    public void placeBlockScaffold(MotionUpdateEvent.Tick event, BlockPos blockPos) {
        try {
            if (mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(blockPos)).isEmpty() && mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(blockPos)).isEmpty()) {
                Vec3d vec3d = new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ);
                for (EnumFacing enumFacing : EnumFacing.values()) {
                    BlockPos offset = blockPos.offset(enumFacing);
                    EnumFacing getOpposite = enumFacing.getOpposite();
                    if (canBeClicked(offset) && offset != null) {
                        switch (switchMode.getValue()) {
                            case Silent: {
                                if (newSlot != -1) {
                                    InventoryUtil.switchToHotbarSlot(newSlot, false);
                                }
                                break;
                            }
                            case Auto: {
                                if (newSlot != -1) {
                                    mc.player.inventory.currentItem = newSlot;
                                }
                                break;
                            }
                            case Off: {
                                break;
                            }
                        }
                        Vec3d add = new Vec3d(offset).add(0.5, 1, 0.5).add(new Vec3d(getOpposite.getDirectionVec()).scale(0.5));
                        float[] legitRotations = BlockInteractionHelper.getLegitRotations(new Vec3d(add.x, add.y, add.z));
                        float[] testRotation = BlockInteractionHelper.getLegitRotations(new Vec3d(offset));
                        switch (rotate.getValue()) {
                            case Normal: {
                                event.setRotation(legitRotations[0], legitRotations[1]);
                                //((Aimbot) ModuleManager.getModuleByName("Aimbot")).setRotation(legitRotations[0], legitRotations[1]);
                                //event.setYaw(legitRotations[0]);
                                //event.setPitch(legitRotations[1]);
                                break;
                            }
                            case Hypixel: {
                                event.setRotation(BlockInteractionHelper.getLegitRotations(new Vec3d(offset).add(0.5 , 0f , 0.5))[0] , BlockInteractionHelper.getLegitRotations(new Vec3d(offset))[1]);
                                break;
                            }
                            case None: {
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                        mc.playerController.processRightClickBlock(mc.player, mc.world, offset, getOpposite, vec3d, EnumHand.MAIN_HAND);
                        mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                        mc.rightClickDelayTimer = 4;
                        switch (switchMode.getValue()) {
                            case Silent: {
                                if (currentItem != -1) {
                                    InventoryUtil.switchToHotbarSlot(currentItem, false);
                                }
                                break;
                            }
                            case Auto: {
                                break;
                            }
                            case Off: {
                                break;
                            }
                        }
                        if (mode.getValue() == Mode.NORMAL && mc.player.moveStrafing == 0.0f && mc.player.moveForward == 0.0f && mc.gameSettings.keyBindJump.isKeyDown()) {
                            mc.player.jump();
                            mc.player.motionX *= 0.3;
                            mc.player.motionZ *= 0.3;
                        }
                        if (mode.getValue() == Mode.Test && mc.gameSettings.keyBindJump.isKeyDown() && mc.player.moveStrafing == 0.0f && mc.player.moveForward == 0.0f) {
                            double posY = mc.player.posY + 1;
                            mc.player.motionX = 0.0;
                            mc.player.motionY -= 0.002300000051036477D;
                            mc.player.motionZ = 0.0;
                            mc.player.jump();
                            mc.player.setPosition(mc.player.posX, posY, mc.player.posZ);
                            mc.player.movementInput.moveForward = 0.0f;
                            mc.player.movementInput.moveStrafe = 0.0f;
                        }
                        if (mode.getValue() == Mode.NCP) {
                            double blockBelow = -2.0D;
                            if (mc.gameSettings.keyBindJump.pressed) {
                                mc.player.jump();
                                mc.player.motionY = 0.41999998688697815D;
                            }
                            if (mc.player.motionY < 0.1D && !(mc.world.getBlockState((new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).add(0.0D, blockBelow, 0.0D)).getBlock() instanceof net.minecraft.block.BlockAir)) {
                                mc.player.motionY = -10.0D;
                            }
                        }
                        if (mode.getValue() == Mode.TP && mc.gameSettings.keyBindJump.isKeyDown()) {
                            mc.player.jump();
                            mc.player.motionY -= 0.2300000051036477D;
                            mc.player.setPosition(mc.player.posX, mc.player.posY + 1.1D, mc.player.posZ);
                        }
                        if (mode.getValue() == Mode.SPARTAN) {
                            double blockBelow = -2.0D;
                            if (mc.gameSettings.keyBindJump.pressed) {
                                mc.player.jump();
                                mc.player.motionY = 0.41999998688697815D;
                            }
                            if (mc.player.motionY < 0.0D && !(mc.world.getBlockState((new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).add(0.0D, blockBelow, 0.0D)).getBlock() instanceof net.minecraft.block.BlockAir)) {
                                mc.player.motionY = -10.0D;
                            }
                        }
                        if (mode.getValue() == Mode.AAC) {
                            if (mc.gameSettings.keyBindJump.pressed) {
                                mc.player.jump();
                                mc.player.motionY = 0.395D;
                                mc.player.motionY -= 0.002300000051036477D;
                            }
                        }
                        if (mode.getValue() == Mode.LONG && mc.gameSettings.keyBindJump.isKeyDown()) {
                            mc.player.jump();
                            if (EntityUtil.isMoving()) {
                                if (isOnGround(0.76D) && !isOnGround(0.75D) && mc.player.motionY > 0.23D && mc.player.motionY < 0.25D) {
                                    double round = Math.round(mc.player.posY);
                                    mc.player.motionY = round - mc.player.posY;
                                }
                                if (isOnGround(1.0E-4D)) {
                                    mc.player.motionY = 0.42D;
                                    mc.player.motionX *= 0.9D;
                                    mc.player.motionZ *= 0.9D;
                                } else if (mc.player.posY >= Math.round(mc.player.posY) - 1.0E-4D &&
                                        mc.player.posY <= Math.round(mc.player.posY) + 1.0E-4D) {
                                    mc.player.motionY = 0.0D;
                                }
                            } else {
                                mc.player.motionX = 0.0D;
                                mc.player.motionZ = 0.0D;
                                mc.player.jumpMovementFactor = 0.0F;
                                double x = mc.player.posX;
                                double y = mc.player.posY - 1.0D;
                                double z = mc.player.posZ;
                                BlockPos blockBelow = new BlockPos(x, y, z);
                                if (mc.world.getBlockState(blockBelow).getBlock() == Blocks.AIR) {
                                    mc.player.motionY = 0.4196D;
                                    mc.player.motionX *= 0.75D;
                                    mc.player.motionZ *= 0.75D;
                                }
                            }
                        }
                        if (mode.getValue() == Mode.TIMER) {
                            if (!mc.player.onGround) {
                                mc.timer.tickLength = (float) (50.0D / this.timer.getValue());
                            }
                            mc.rightClickDelayTimer = 0;
                            if (mc.player.onGround) {
                                mc.player.motionY = 0.3932D;
                                mc.timer.tickLength = 50.0F;
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    public enum SwitchMode {
        Off,
        Auto,
        Silent
    }

    public enum RotateMode {
        None,
        Hypixel,
        Normal
    }

    public enum Mode {
        AAC,
        NCP,
        TP,
        SPARTAN,
        LONG,
        TIMER,
        NORMAL,
        Test
    }
}
