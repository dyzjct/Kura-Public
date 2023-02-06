package me.windyteam.kura.module.modules.combat;

import me.windyteam.kura.event.events.entity.MotionUpdateEvent;
import me.windyteam.kura.event.events.render.RenderEvent;
import me.windyteam.kura.manager.RotationManager;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.utils.block.BlockInteractionHelper;
import me.windyteam.kura.utils.gl.XG42Tessellator;
import me.windyteam.kura.utils.math.GeometryMasks;
import me.windyteam.kura.utils.mc.ChatUtil;
import me.windyteam.kura.manager.RotationManager;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.utils.block.BlockInteractionHelper;
import me.windyteam.kura.utils.mc.ChatUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by hub on 10 December 2019
 * Updated by hub on 25 December 2020
 */
@Module.Info(name = "DispenserMeta", category = Category.COMBAT, description = "Do not use with any AntiGhostBlock Mod!")
public class DispenserMeta extends Module {

    private static final DecimalFormat df = new DecimalFormat("#.#");

    private final Setting<Boolean> rotate = bsetting("Rotate", false);
    private final Setting<Boolean> grabItem = bsetting("GrabItem", false);
    private final Setting<Boolean> autoEnableHitAura = bsetting("AutoEnableHitAura", false);
    private final Setting<Boolean> debugMessages = bsetting("DebugSetting", false);

    private int stage;
    private Vec3d hitVec2;
    private BlockPos placeTarget;

    private int dispenserSlot;
    private int shulkerSlot;
    private int redstoneSlot;
    private int hopperSlot;
    private int obiSlot;
    private boolean isSneaking;

    @Override
    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }
        mc.player.setSneaking(false);
        RotationManager.resetRotation();
    }

    @Override
    public void onEnable() {

        if (fullNullCheck()) {
            this.disable();
            return;
        }

        df.setRoundingMode(RoundingMode.CEILING);

        stage = 0;

        placeTarget = null;

        obiSlot = -1;
        dispenserSlot = -1;
        shulkerSlot = -1;
        redstoneSlot = -1;
        hopperSlot = -1;

        isSneaking = false;

        for (int i = 0; i < 9; i++) {

            if (obiSlot != -1 && dispenserSlot != -1 && shulkerSlot != -1 && redstoneSlot != -1 && hopperSlot != -1) {
                break;
            }

            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                continue;
            }

            Block block = ((ItemBlock) stack.getItem()).getBlock();

            if (block == Blocks.HOPPER) {
                hopperSlot = i;
            } else if (BlockInteractionHelper.shulkerList.contains(block)) {
                shulkerSlot = i;
            } else if (block == Blocks.OBSIDIAN) {
                obiSlot = i;
            } else if (block == Blocks.DISPENSER) {
                dispenserSlot = i;
            } else if (block == Blocks.REDSTONE_BLOCK) {
                redstoneSlot = i;
            }

        }

        if (obiSlot == -1 || dispenserSlot == -1 || shulkerSlot == -1 || redstoneSlot == -1 || hopperSlot == -1) {
            if (debugMessages.getValue()) {
                ChatUtil.NoSpam.sendMessage("[Dispenser32k] Items missing, disabling.");
            }
            this.disable();
            return;
        }

        try {
            if (mc.objectMouseOver == null) {
                if (debugMessages.getValue()) {
                    ChatUtil.NoSpam.sendMessage("[Dispenser32k] Not a valid place target, disabling.");
                }
                this.disable();
                return;
            } else {
                mc.objectMouseOver.getBlockPos().up();
            }
        } catch (Exception e){
//
        }

        placeTarget = mc.objectMouseOver.getBlockPos().up();

        if (debugMessages.getValue()) {
            ChatUtil.NoSpam.sendMessage("[Dispenser32k] Place Target: " + placeTarget.x + " " + placeTarget.y + " " + placeTarget.z + " Distance: " + df.format(mc.player.getPositionVector().distanceTo(new Vec3d(placeTarget))));
        }

    }

    @SubscribeEvent
    public void onUpdate(MotionUpdateEvent.Tick event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        // stage 0: place obi and dispenser
        if (stage == 0) {

            mc.player.inventory.currentItem = obiSlot;
            placeBlock(event , new BlockPos(placeTarget), EnumFacing.DOWN);

            mc.player.inventory.currentItem = dispenserSlot;
            placeBlock(event , new BlockPos(placeTarget.add(0, 1, 0)), EnumFacing.DOWN);

            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;

            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(placeTarget.add(0, 1, 0), EnumFacing.DOWN, EnumHand.MAIN_HAND, 0, 0, 0));

            stage = 1;
            return;

        }

        // stage 1: put shulker, place redstone
        if (stage == 1) {

            if (!(mc.currentScreen instanceof GuiContainer)) {
                return;
            }

            mc.playerController.windowClick(mc.player.openContainer.windowId, 1, shulkerSlot, ClickType.SWAP, mc.player);
            mc.player.closeScreen();

            mc.player.inventory.currentItem = redstoneSlot;
            placeBlock(event , new BlockPos(placeTarget.add(0, 2, 0)), EnumFacing.DOWN);

            stage = 2;
            return;

        }

        // stage 2: place hopper
        if (stage == 2) {

            // TODO: fix instahopper, why boken? ;(
            Block block = mc.world.getBlockState(placeTarget.offset(mc.player.getHorizontalFacing().getOpposite()).up()).getBlock();
            if ((block instanceof BlockAir) || (block instanceof BlockLiquid)) {
                return;
            }

            mc.player.inventory.currentItem = hopperSlot;
            placeBlock(event , new BlockPos(placeTarget.offset(mc.player.getHorizontalFacing().getOpposite())), mc.player.getHorizontalFacing());

            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;

            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(placeTarget.offset(mc.player.getHorizontalFacing().getOpposite()), EnumFacing.DOWN, EnumHand.MAIN_HAND, 0, 0, 0));

            mc.player.inventory.currentItem = shulkerSlot;

            if (!grabItem.getValue()) {
                this.disable();
                return;
            }

            stage = 3;
            return;

        }

        // stage 3: hopper gui
        if (stage == 3) {

            if (!(mc.currentScreen instanceof GuiContainer)) {
                return;
            }

            if (((GuiContainer) mc.currentScreen).inventorySlots.getSlot(0).getStack().isEmpty) {
                return;
            }
            mc.playerController.windowClick(mc.player.openContainer.windowId, 0, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);

            if (autoEnableHitAura.getValue()) {
                ModuleManager.getModuleByName("32kAuraNew").enable();
            }

            this.disable();
        }

    }

    private void placeBlock(MotionUpdateEvent.Tick event , BlockPos pos, EnumFacing side) {
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();

        if (!isSneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }

        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        hitVec2 = new Vec3d(neighbour).add(0, 0.5, 0).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        if (rotate.getValue()) {
            mc.player.rotationYawHead = BlockInteractionHelper.getLegitRotations(hitVec)[0];
            mc.player.renderYawOffset = BlockInteractionHelper.getLegitRotations(hitVec)[0];
            event.setYaw(BlockInteractionHelper.getLegitRotations(hitVec)[0]);
            event.setPitch(BlockInteractionHelper.getLegitRotations(hitVec)[1]);
        }
        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        mc.player.swingArm(EnumHand.MAIN_HAND);
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (hitVec2 != null) {
            XG42Tessellator.prepare(GL11.GL_QUADS);
            XG42Tessellator.drawBox(hitVec2, 0x30F56674, GeometryMasks.Quad.ALL);
            XG42Tessellator.release();
            XG42Tessellator.prepare(GL11.GL_QUADS);
            XG42Tessellator.drawBoundingBoxBlockPos(hitVec2, 1f, 255, 192, 203, 170);
            XG42Tessellator.release();
        }
    }

}
