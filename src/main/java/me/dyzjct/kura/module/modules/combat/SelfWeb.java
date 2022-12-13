package me.dyzjct.kura.module.modules.combat;

import me.dyzjct.kura.friend.FriendManager;
import me.dyzjct.kura.manager.RotationManager;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.block.BlockInteractionHelper;
import me.dyzjct.kura.utils.entity.PlayerUtil;
import me.dyzjct.kura.utils.mc.ChatUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;


@Module.Info(name = "SelfWeb", category = Category.COMBAT, description = "places fuckin webs at ur feet")
public class SelfWeb extends Module {

    int new_slot = -1;
    boolean sneak = false;
    private final Setting<Boolean> always_on = bsetting("AlwaysOn", false);
    private final Setting<Boolean> rotate = bsetting("Rotate", true);
    private final Setting<Integer> range = isetting("Range", 4, 0, 8);

    @Override
    public void onEnable() {

        if (mc.player != null) {

            new_slot = find_in_hotbar();

            if (new_slot == -1) {
                ChatUtil.sendMessage("cannot find webs in hotbar");
                disable();
            }

        }

    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            if (sneak) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                sneak = false;
            }
        }
        RotationManager.resetRotation();
    }

    @Override
    public void onUpdate() {

        if (mc.player == null) return;

        if (always_on.getValue()) {

            EntityPlayer target = find_closest_target();
            if (target == null) return;


            if (mc.player.getDistance(target) < range.getValue() && is_surround()) {
                int last_slot = mc.player.inventory.currentItem;
                mc.player.inventory.currentItem = new_slot;
                mc.playerController.updateController();
                place_blocks(PlayerUtil.GetLocalPlayerPosFloored());
                mc.player.inventory.currentItem = last_slot;
            }

        } else {
            int last_slot = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = new_slot;
            mc.playerController.updateController();
            place_blocks(PlayerUtil.GetLocalPlayerPosFloored());
            mc.player.inventory.currentItem = last_slot;
            disable();
        }

    }

    public EntityPlayer find_closest_target() {

        if (mc.world.playerEntities.isEmpty())
            return null;

        EntityPlayer closestTarget = null;

        for (final EntityPlayer target : mc.world.playerEntities) {
            if (target == mc.player)
                continue;

            if (FriendManager.isFriend(target.getName()))
                continue;

            if (target.getHealth() <= 0.0f)
                continue;

            if (closestTarget != null)
                if (mc.player.getDistance(target) > mc.player.getDistance(closestTarget))
                    continue;

            closestTarget = target;
        }

        return closestTarget;
    }

    private int find_in_hotbar() {

        for (int i = 0; i < 9; ++i) {

            final ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack.getItem() == Item.getItemById(30)) {
                return i;
            }

        }
        return -1;
    }

    private boolean is_surround() {

        BlockPos player_block = PlayerUtil.GetLocalPlayerPosFloored();
        return mc.world.getBlockState(player_block.east()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(player_block.west()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(player_block.north()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(player_block.south()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(player_block).getBlock() == Blocks.AIR;

    }

    private void place_blocks(BlockPos pos) {

        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            return;
        }

        if (!BlockInteractionHelper.checkForNeighbours(pos)) {
            return;
        }

        for (EnumFacing side : EnumFacing.values()) {

            BlockPos neighbor = pos.offset(side);

            EnumFacing side2 = side.getOpposite();

            if (!BlockInteractionHelper.canBeClicked(neighbor)) continue;

            if (BlockInteractionHelper.blackList.contains(mc.world.getBlockState(neighbor).getBlock())) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                sneak = true;
            }
            Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
            if (rotate.getValue()) {
                BlockInteractionHelper.faceVectorPacketInstant(hitVec);
            }
            mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            return;
        }
    }
}
