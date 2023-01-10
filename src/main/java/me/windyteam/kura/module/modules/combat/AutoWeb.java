package me.windyteam.kura.module.modules.combat;

import me.windyteam.kura.event.events.entity.MotionUpdateEvent;
import me.windyteam.kura.friend.FriendManager;
import me.windyteam.kura.manager.RotationManager;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.utils.Wrapper;
import me.windyteam.kura.utils.block.BlockInteractionHelper;
import me.windyteam.kura.utils.entity.EntityUtil;
import me.windyteam.kura.utils.mc.ChatUtil;
import me.windyteam.kura.manager.RotationManager;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.utils.block.BlockInteractionHelper;
import me.windyteam.kura.utils.entity.EntityUtil;
import me.windyteam.kura.utils.mc.ChatUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

@Module.Info(name = "AutoWeb", category = Category.COMBAT)
public class AutoWeb extends Module {
    private final Setting<Double> range = dsetting("Range", 4, 0.5, 6);
    private final Setting<Double> blockPerTick = dsetting("BlocksPerTick", 4, 0, 6);
    private final Setting<Boolean> spoofRotations = bsetting("Rotate", true);
    private final Setting<Boolean> spoofHotbar = bsetting("SpoofHotbar", true);
    private final Setting<Boolean> debugMessages = bsetting("DebugMessage", true);
    private final Vec3d[] offsetList = new Vec3d[]{new Vec3d(0.0, 2.0, 0.0), new Vec3d(0.0, 1.0, 0.0), new Vec3d(0.0, 0.0, 0.0)};
    private boolean slowModeSwitch = false;
    private int playerHotbarSlot = -1;
    private EntityPlayer closestTarget;
    private int lastHotbarSlot = -1;
    private int offsetStep = 0;

    @SubscribeEvent
    public void onUpdate(MotionUpdateEvent.Tick event) {
        if (isDisabled() || mc.player == null || ModuleManager.getModuleByName("Freecam").isEnabled()) {
            return;
        }
        if (closestTarget == null) {
            return;
        }
        if (slowModeSwitch) {
            slowModeSwitch = false;
            return;
        }
        for (int i = 0; i < (int) Math.floor(blockPerTick.getValue()); ++i) {
            if (debugMessages.getValue()) {
                ChatUtil.NoSpam.sendMessage("[AutoWeb] Loop iteration: " + offsetStep);
            }
            if (offsetStep >= offsetList.length) {
                endLoop();
                return;
            }
            Vec3d offset = offsetList[offsetStep];
            placeBlock(event, new BlockPos(closestTarget.getPositionVector()).down().add(offset.x, offset.y, offset.z));
            ++offsetStep;
        }
        slowModeSwitch = true;
    }

    private void placeBlock(MotionUpdateEvent.Tick event, BlockPos blockPos) {
        if (!Wrapper.getWorld().getBlockState(blockPos).getMaterial().isReplaceable()) {
            if (debugMessages.getValue()) {
                ChatUtil.NoSpam.sendMessage("[AutoWeb] Block is already placed, skipping");
            }
            return;
        }
        if (!BlockInteractionHelper.checkForNeighbours(blockPos)) {
            return;
        }
        placeBlockExecute(event, blockPos);
    }

    public void placeBlockExecute(MotionUpdateEvent.Tick event, BlockPos pos) {
        Vec3d eyesPos = new Vec3d(Wrapper.getPlayer().posX, Wrapper.getPlayer().posY + Wrapper.getPlayer().getEyeHeight(), Wrapper.getPlayer().posZ);
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();
            if (!BlockInteractionHelper.canBeClicked(neighbor)) {
                if (debugMessages.getValue()) {
                    ChatUtil.NoSpam.sendMessage("[AutoWeb] No neighbor to click at!");
                }
            } else {
                Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
                if (eyesPos.squareDistanceTo(hitVec) > 18.0625) {
                    if (debugMessages.getValue()) {
                        ChatUtil.NoSpam.sendMessage("[AutoWeb] Distance > 4.25 blocks!");
                    }
                } else {
                    if (spoofRotations.getValue()) {
                        mc.player.rotationYawHead = BlockInteractionHelper.getLegitRotations(hitVec)[0];
                        mc.player.renderYawOffset = BlockInteractionHelper.getLegitRotations(hitVec)[0];
                        event.setYaw(BlockInteractionHelper.getLegitRotations(hitVec)[0]);
                        event.setPitch(BlockInteractionHelper.getLegitRotations(hitVec)[1]);
                    }
                    boolean needSneak = false;
                    Block blockBelow = mc.world.getBlockState(neighbor).getBlock();
                    if (BlockInteractionHelper.blackList.contains(blockBelow) || BlockInteractionHelper.shulkerList.contains(blockBelow)) {
                        if (debugMessages.getValue()) {
                            ChatUtil.NoSpam.sendMessage("[AutoWeb] Sneak enabled!");
                        }
                        needSneak = true;
                    }
                    if (needSneak) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    }
                    int obiSlot = findObiInHotbar();
                    if (obiSlot == -1) {
                        if (debugMessages.getValue()) {
                            ChatUtil.NoSpam.sendMessage("[AutoWeb] No Web in Hotbar, disabling!");
                        }
                        disable();
                        return;
                    }
                    if (lastHotbarSlot != obiSlot) {
                        if (debugMessages.getValue()) {
                            ChatUtil.NoSpam.sendMessage("[AutoWeb Setting Slot to Obi at  = " + obiSlot);
                        }
                        if (spoofHotbar.getValue()) {
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(obiSlot));
                        } else {
                            Wrapper.getPlayer().inventory.currentItem = obiSlot;
                        }
                        lastHotbarSlot = obiSlot;
                    }
                    mc.playerController.processRightClickBlock(Wrapper.getPlayer(), mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                    if (needSneak) {
                        if (debugMessages.getValue()) {
                            ChatUtil.NoSpam.sendMessage("[AutoWeb] Sneak disabled!");
                        }
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                    }
                    return;
                }
            }
        }
    }

    private int findObiInHotbar() {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = Wrapper.getPlayer().inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock) stack.getItem()).getBlock();
                if (block instanceof BlockWeb) {
                    slot = i;
                    break;
                }
            }
        }
        return slot;
    }

    private void findTarget() {
        List<EntityPlayer> playerList = Wrapper.getWorld().playerEntities;
        for (EntityPlayer target : playerList) {
            if (target == mc.player) {
                continue;
            }
            if (FriendManager.isFriend(target.getName())) {
                continue;
            }
            if (!EntityUtil.isLiving(target)) {
                continue;
            }
            if (target.getHealth() <= 0.0f) {
                continue;
            }
            double currentDistance = Wrapper.getPlayer().getDistance(target);
            if (currentDistance > range.getValue()) {
                continue;
            }
            if (closestTarget != null) {
                if (currentDistance >= Wrapper.getPlayer().getDistance(closestTarget)) {
                    continue;
                }
            }
            closestTarget = target;
        }
    }

    private void endLoop() {
        offsetStep = 0;
        if (debugMessages.getValue()) {
            ChatUtil.NoSpam.sendMessage("[AutoWeb] Ending Loop");
        }
        if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
            if (debugMessages.getValue()) {
                ChatUtil.NoSpam.sendMessage("[AutoWeb] Setting Slot back to  = " + playerHotbarSlot);
            }
            if (spoofHotbar.getValue()) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(playerHotbarSlot));
            } else {
                Wrapper.getPlayer().inventory.currentItem = playerHotbarSlot;
            }
            lastHotbarSlot = playerHotbarSlot;
        }
        findTarget();
    }

    @Override
    public void onEnable() {
        if (mc.player == null) {
            disable();
            return;
        }
        if (debugMessages.getValue()) {
            ChatUtil.NoSpam.sendMessage("[AutoWeb] Enabling");
        }
        playerHotbarSlot = Wrapper.getPlayer().inventory.currentItem;
        lastHotbarSlot = -1;
        if (debugMessages.getValue()) {
            ChatUtil.NoSpam.sendMessage("[AutoWeb] Saving initial Slot  = " + playerHotbarSlot);
        }
        findTarget();
    }

    @Override
    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }
        RotationManager.resetRotation();
        if (mc.player == null) {
            return;
        }
        if (debugMessages.getValue()) {
            ChatUtil.NoSpam.sendMessage("[AutoWeb] Disabling");
        }
        if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
            if (debugMessages.getValue()) {
                ChatUtil.NoSpam.sendMessage("[AutoWeb] Setting Slot to  = " + playerHotbarSlot);
            }
            if (spoofHotbar.getValue()) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(playerHotbarSlot));
            } else {
                Wrapper.getPlayer().inventory.currentItem = playerHotbarSlot;
            }
        }
        playerHotbarSlot = -1;
        lastHotbarSlot = -1;
    }

}
