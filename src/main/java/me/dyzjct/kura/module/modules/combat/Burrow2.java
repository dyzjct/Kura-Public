package me.dyzjct.kura.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dyzjct.kura.event.events.entity.MotionUpdateEvent;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.NTMiku.BlockUtil;
import me.dyzjct.kura.utils.block.SeijaBlockUtil;
import me.dyzjct.kura.utils.inventory.InventoryUtil;
import me.dyzjct.kura.utils.mc.ChatUtil;
import me.dyzjct.kura.utils.mc.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Comparator;
import java.util.stream.Collectors;

@Module.Info(name = "Burrow2", category = Category.COMBAT)
public class Burrow2 extends Module {
    private final Setting<Boolean> tpcenter;
    private final Setting<Boolean> smartOffset;
    private final Setting<Double> offsetX;
    private final Setting<Double> offsetY;
    private final Setting<Double> offsetZ;
    private final Setting<Boolean> breakCrystal;
    private final Setting<BlockMode> mode;
    private boolean isSneaking;
    private final Setting<Boolean> rotate;

    public Burrow2() {
        this.smartOffset = bsetting("smartOffset", true);

        this.tpcenter = bsetting("TPCenter", false);
        this.rotate = bsetting("Rotate", true);
        this.offsetX = dsetting("OffsetX", -7.0D, -10.0D, 10.0D);
        this.offsetY = dsetting("OffsetY", -7.0D, -10.0D, 10.0D);
        this.offsetZ = dsetting("OffsetZ", -7.0D, -10.0D, 10.0D);

        this.breakCrystal = bsetting("BreakCrystal", Boolean.valueOf(true));
        this.mode = msetting("BlockMode", BlockMode.Obsidian);
        this.isSneaking = false;
    }

    public static void breakcrystal() {
        for (Entity crystal : mc.world.loadedEntityList.stream().filter(e -> (e instanceof EntityEnderCrystal && !e.isDead)).sorted(Comparator.comparing(e -> Float.valueOf(mc.player.getDistance(e)))).collect(Collectors.toList())) {
            if (crystal instanceof EntityEnderCrystal && mc.player.getDistance(crystal) <= 4.0F) {
                mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
            }
        }
    }

    public static BlockPos getPlayerPosFixY(EntityPlayer player) {
        return new BlockPos(Math.floor(player.posX), Math.round(player.posY), Math.floor(player.posZ));
    }

    public void onDisable() {
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    }

    @SubscribeEvent
    public void onTick(MotionUpdateEvent event) {
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);

        if (this.breakCrystal.getValue().booleanValue()) {
            Burrow2 burrow2 = this;
            breakcrystal();
        }
        if (!mc.world.isBlockLoaded(mc.player.getPosition())) {
            return;
        }
        if (!mc.player.onGround || mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY + 2.0, mc.player.posZ)).getBlock() != Blocks.AIR /*|| !mc.world.getEntitiesWithinAABB((Class)EntityEnderCrystal.class, new AxisAlignedBB(new BlockPos(mc.player.posX,mc.player.posY,mc.player.posZ))).isEmpty()*/) {
            this.disable();
            return;
        }
        if (mc.world.getBlockState(new BlockPos(mc.player.posX, Math.round(mc.player.posY), mc.player.posZ)).getBlock() != Blocks.AIR /*||mc.world.getBlockState(new BlockPos(mc.player.posX,mc.player.posY,mc.player.posZ)).getBlock()==Blocks.ENDER_CHEST*/) {
            this.disable();
            return;
        }
        if (mode.getValue() == BlockMode.Obsidian)
            if (InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)) == -1) {
                ChatUtil.sendMessage(ChatFormatting.RED + "Obsidian ?");
                this.disable();
                return;
            }
        if (mode.getValue() == BlockMode.Chest)
            if (InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST)) == -1) {
                ChatUtil.sendMessage(ChatFormatting.RED + "Ender Chest ?");
                this.disable();
                return;
            }
        if (mode.getValue() == BlockMode.Smart) {
            if (InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)) == -1) {
                if (InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST)) == -1) {
                    ChatUtil.sendMessage(ChatFormatting.RED + "Obsidian/Ender Chest ?");
                    this.disable();
                    return;
                }
            }
        }
//        if (tpcenter.getValue().booleanValue()) {
//            BlockPos startPos = EntityUtil.getRoundedBlockPos(Surround.mc.player);
//            SeijaGod.positionManager.setPositionPacket((double) startPos.getX() + 0.5, startPos.getY(), (double) startPos.getZ() + 0.5, true, true, true);
//        }
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.419999986886978, mc.player.posZ, false));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805212015, mc.player.posZ, false));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.001335979112147, mc.player.posZ, false));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.166109260938214, mc.player.posZ, false));
        final int a = mc.player.inventory.currentItem;
        if (mode.getValue() == BlockMode.Obsidian) {
            InventoryUtil.switchToHotbarSlot(InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)), false);
        }
        if (mode.getValue() == BlockMode.Chest) {
            InventoryUtil.switchToHotbarSlot(InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST)), false);
        }
        if (mode.getValue() == BlockMode.Smart) {
            if (InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST)) != -1) {
                InventoryUtil.switchToHotbarSlot(InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST)), false);
            } else if (InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)) != -1) {
                InventoryUtil.switchToHotbarSlot(InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)), false);
            }
        }
        this.isSneaking = BlockUtil.placeBlock(new BlockPos(getPlayerPosFixY(Burrow2.mc.player)), EnumHand.MAIN_HAND, this.rotate.getValue(), true, this.isSneaking);
        mc.playerController.updateController();
        mc.player.connection.sendPacket(new CPacketHeldItemChange(a));
        mc.player.inventory.currentItem = a;
        mc.playerController.updateController();

        if (smartOffset.getValue()) {
            boolean defaultOffset = true;
            if (mc.player.posY >= 3) {
                for (int i = -10; i < 10; i++) {
                    if (i == -1) i = 3;
                    if (mc.world.getBlockState(SeijaBlockUtil.getFlooredPosition(mc.player).add(0, i, 0)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(SeijaBlockUtil.getFlooredPosition(mc.player).add(0, i + 1, 0)).getBlock().equals(Blocks.AIR)) {
                        BlockPos pos = SeijaBlockUtil.getFlooredPosition(mc.player).add(0, i, 0);
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(pos.getX() + 0.3, pos.getY(), pos.getZ() + 0.3, true));
                        defaultOffset = false;
                        break;
                    }
                }
            }

            if (defaultOffset)
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + this.offsetX.getValue(), mc.player.posY + this.offsetY.getValue(), mc.player.posZ + offsetZ.getValue(), true));

        } else {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + this.offsetX.getValue(), mc.player.posY + this.offsetY.getValue(), mc.player.posZ + offsetZ.getValue(), true));
        }
        this.disable();
    }

    enum BlockMode {
        Obsidian, Chest, Smart
    }
}

