package me.windyteam.kura.module.modules.player;

import me.windyteam.kura.event.events.block.BlockEvent;
import me.windyteam.kura.event.events.player.UpdateWalkingPlayerEvent;
import me.windyteam.kura.event.events.render.RenderEvent;
import me.windyteam.kura.manager.RotationManager;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.module.modules.combat.CevBreaker;
import me.windyteam.kura.setting.BooleanSetting;
import me.windyteam.kura.setting.IntegerSetting;
import me.windyteam.kura.utils.TimerUtils;
import me.windyteam.kura.utils.animations.BlockEasingRender;
import me.windyteam.kura.utils.block.BlockInteractionHelper;
import me.windyteam.kura.utils.block.BlockUtil;
import me.windyteam.kura.utils.gl.MelonTessellator;
import me.windyteam.kura.utils.inventory.InventoryUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.awt.*;

@Module.Info(name="StrictPacketMine", category= Category.PLAYER, description="Better Mining")
public class StrictPacketMine
        extends Module {
    public static StrictPacketMine INSTANCE = new StrictPacketMine();
    public BlockEasingRender blockRenderSmooth = new BlockEasingRender(new BlockPos(0, 0, 0), 0.0f, 2000.0f);
    public TimerUtils timerUtils = new TimerUtils();
    public TimerUtils renderTimerUtils = new TimerUtils();
    public BooleanSetting packet = this.bsetting("PacketOnly", false);
    public BooleanSetting spoofSwing = this.bsetting("SpoofSwing", false);
    public IntegerSetting startSwingtime = this.isetting("StartSwingTime", 1350, 0, 2000);
    public BooleanSetting swap = this.bsetting("SwapMine", true);
    public BooleanSetting rotate = this.bsetting("Rotate", false);
    public BooleanSetting render = this.bsetting("Render", true);
    public IntegerSetting alpha = this.isetting("Alpha", 30, 0, 255).b(this.render);
    public static BlockPos currentPos = null;
    public IBlockState currentBlockState;
    public EnumFacing facing;
    public int oldSlot;
    public int picSlot;

    private void equipBestTool(IBlockState blockState) {
        int bestSlot = -1;
        double max = 0.0;
        for (int i = 0; i < 9; ++i) {
            int eff;
            float f = 0;
            ItemStack stack = StrictPacketMine.mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty) continue;
            float speed = stack.getDestroySpeed(blockState);
            if (!(f > 1.0f) || !((double)(speed = (float)((double)speed + ((eff = EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.EFFICIENCY, (ItemStack)stack)) > 0 ? (double)eff + 1.0 : 0.0))) > max)) continue;
            max = speed;
            bestSlot = i;
        }
        if (bestSlot != -1) {
            InventoryUtil.switchToHotbarSlot(bestSlot, false);
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        currentPos = null;
        this.facing = null;
        this.timerUtils.reset();
        this.renderTimerUtils.reset();
    }

    @SubscribeEvent
    public void onBlockEvent(BlockEvent event) {
        if (StrictPacketMine.fullNullCheck()) {
            return;
        }
        this.oldSlot = StrictPacketMine.mc.player.inventory.currentItem;
        this.picSlot = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE);
        try {
            if (currentPos != null) {
                if (!BlockUtil.canBreak(currentPos, false)) {
                    currentPos = null;
                    return;
                }
                if (currentPos.getX() == event.getPos().getX() && currentPos.getY() == event.getPos().getY() && currentPos.getZ() == event.getPos().getZ()) {
                    return;
                }
            }
            currentPos = event.getPos();
            this.facing = event.getFacing();
            this.currentBlockState = StrictPacketMine.mc.world.getBlockState(currentPos);
            this.timerUtils.reset();
            this.renderTimerUtils.reset();
            if (mc.getConnection() != null && BlockUtil.canBreak(currentPos, false)) {
                this.blockRenderSmooth.updatePos(currentPos);
                this.blockRenderSmooth.reset();
                StrictPacketMine.mc.player.swingArm(EnumHand.MAIN_HAND);
                StrictPacketMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, currentPos, this.facing));
                StrictPacketMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, currentPos, this.facing));
            }
            event.setCanceled(true);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateWalkingPlayerEvent event) {
        if (StrictPacketMine.fullNullCheck()) {
            return;
        }
        this.oldSlot = StrictPacketMine.mc.player.inventory.currentItem;
        this.picSlot = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE);
        try {
            if (ModuleManager.getModuleByClass(CevBreaker.class).isEnabled()) {
                currentPos = null;
                return;
            }
            if (BlockUtil.canBreak(currentPos, false)) {
                if (((Boolean)this.swap.getValue()).booleanValue() && !this.currentBlockState.getBlock().equals(Blocks.SNOW_LAYER)) {
                    this.equipBestTool(this.currentBlockState);
                } else if (((Boolean)this.swap.getValue()).booleanValue() && this.currentBlockState.getBlock().equals(Blocks.SNOW_LAYER)) {
                    InventoryUtil.switchToHotbarSlot(this.picSlot, false);
                }
                if (!((Boolean)this.packet.getValue()).booleanValue()) {
                    StrictPacketMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, currentPos, this.facing));
                }
                if (((Boolean)this.swap.getValue()).booleanValue()) {
                    InventoryUtil.switchToHotbarSlot(this.oldSlot, false);
                }
                if (((Boolean)this.spoofSwing.getValue()).booleanValue() && this.timerUtils.passed((Integer)this.startSwingtime.getValue())) {
                    StrictPacketMine.mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
                }
                if (((Boolean)this.rotate.getValue()).booleanValue() && this.timerUtils.passed(1800.0f)) {
                    event.setRotation(BlockInteractionHelper.getLegitRotations(new Vec3d((Vec3i)currentPos).add(0.5, 0.0, 0.5))[0], BlockInteractionHelper.getLegitRotations(new Vec3d((Vec3i)currentPos))[1]);
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (StrictPacketMine.fullNullCheck()) {
            return;
        }
        Color color = new Color(0, 255, 0);
        Color color2 = new Color(255, 0, 0);
        if (((Boolean)this.render.getValue()).booleanValue() && currentPos != null && BlockUtil.canBreak(currentPos, true)) {
            this.blockRenderSmooth.begin();
            MelonTessellator.INSTANCE.drawBBBox(this.blockRenderSmooth.getFullUpdate(), this.renderTimerUtils.passed(1800) ? color : color2, (Integer)this.alpha.getValue(), 3.0f, true);
        } else if (currentPos == null) {
            this.blockRenderSmooth.end();
        }
    }

    @Override
    public void onDisable() {
        if (StrictPacketMine.fullNullCheck()) {
            return;
        }
        currentPos = null;
        this.facing = null;
        RotationManager.resetRotation();
    }

    @Override
    public void onEnable() {
        if (StrictPacketMine.fullNullCheck()) {
            return;
        }
        currentPos = null;
        this.facing = null;
        this.blockRenderSmooth.reset();
    }

    @Override
    public String getHudInfo() {
        return (Boolean)this.packet.getValue() != false ? "Packet" : "Instant";
    }
}

