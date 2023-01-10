package me.windyteam.kura.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.windyteam.kura.event.events.client.PacketEvents;
import me.windyteam.kura.event.events.entity.MotionUpdateEvent;
import me.windyteam.kura.event.events.render.RenderEvent;
import me.windyteam.kura.friend.FriendManager;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.module.Module.Info;
import me.windyteam.kura.module.modules.crystalaura.cystalHelper.CrystalDamageCalculator;
import me.windyteam.kura.module.modules.crystalaura.cystalHelper.CrystalHelper;
import me.windyteam.kura.setting.BooleanSetting;
import me.windyteam.kura.setting.ColorSetting;
import me.windyteam.kura.setting.IntegerSetting;
import me.windyteam.kura.setting.ModeSetting;
import me.windyteam.kura.utils.TimerUtils;
import me.windyteam.kura.utils.animations.BlockEasingRender;
import me.windyteam.kura.utils.block.BlockUtil;
import me.windyteam.kura.utils.gl.MelonTessellator;
import me.windyteam.kura.utils.inventory.InventoryUtil;
import me.windyteam.kura.utils.mc.ChatUtil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;

@Info(name = "CevBreaker", category = Category.COMBAT)
public class CevBreaker extends Module {
    public int oldSlot;

    public ModeSetting<CheckMode> mode = msetting("CheckMode", CheckMode.Air);

    public BooleanSetting packetExplode = bsetting("PacketExplode", false);

    public BooleanSetting crystalDead = bsetting("CheckCrystal", false);

    public BooleanSetting rotate = bsetting("Rotate", false);

    public BooleanSetting packet = bsetting("Packet", false);

    public BooleanSetting swing = bsetting("Swing", false);

    public BooleanSetting pswing = bsetting("PacketSwing", false).b(this.swing);

    public IntegerSetting range = isetting("Range", 5, 1, 6);

    public ColorSetting color = csetting("Color", new Color(207, 19, 220));

    public IntegerSetting alpha = isetting("Alpha", 65, 1, 255);

    public BlockEasingRender blockRenderSmooth = new BlockEasingRender(new BlockPos(0, 0, 0), 0.0F, 550.0F);

    public TimerUtils breakDelay = new TimerUtils();

    public TimerUtils updatePos = new TimerUtils();

    public EntityEnderCrystal lastCrystal;

    public EntityPlayer target;

    public boolean boost = false;

    boolean flag;

    int progress;

    int firsttime;

    int pickaxeitem;

    int crystalitem;

    int ObiItem;

    private BlockPos cobi;

    private int stage;

    private int currentX;

    private int currentY;

    private int currentZ;

    private int lastX;

    private int lastY;

    private int lastZ;

    public static EntityPlayer findClosestTarget() {
        if (mc.world.playerEntities.isEmpty())
            return null;
        EntityPlayer closestTarget = null;
        for (EntityPlayer target : mc.world.playerEntities) {
            if (target == mc.player)
                continue;
            if (FriendManager.isFriend(target.getName()))
                continue;
            if (closestTarget != null && mc.player.getDistance((Entity)target) > mc.player.getDistance((Entity)closestTarget))
                continue;
            closestTarget = target;
        }
        return closestTarget;
    }

    public void onWorldRender(RenderEvent event) {
        if (fullNullCheck())
            return;
        if (this.cobi != null) {
            this.blockRenderSmooth.begin();
            MelonTessellator.INSTANCE.drawBBBox(this.blockRenderSmooth.getFullUpdate(), (Color)this.color.getValue(), ((Integer)this.alpha.getValue()).intValue(), 2.0F, true);
        } else {
            this.blockRenderSmooth.resetFade();
            this.blockRenderSmooth.end();
        }
    }

    public void onEnable() {
        if (fullNullCheck())
            return;
        this.blockRenderSmooth.resetFade();
        this.progress = 0;
        this.firsttime = 0;
        this.flag = false;
        this.cobi = null;
        this.lastCrystal = null;
        this.target = null;
        this.boost = false;
        this.updatePos.reset();
        this.breakDelay.reset();
        this.stage = 0;
    }

    public void onLogout() {
        disable();
    }

    public void onLogin() {
        disable();
    }

    private boolean validPosition(BlockPos Pos, EntityPlayer player) {
        return (mc.world.getBlockState(Pos).getBlock() != Blocks.BEDROCK && mc.world
                .getBlockState(Pos.add(0, 1, 0)).getBlock() != Blocks.BEDROCK && mc.world
                .getBlockState(Pos.add(0, 2, 0)).getBlock() != Blocks.BEDROCK && mc.world
                .getBlockState(Pos.add(0, 1, 0)).getBlock() == Blocks.AIR && mc.world
                .getBlockState(Pos.add(0, 2, 0)).getBlock() == Blocks.AIR &&
                !player.getPosition().equals(Pos) &&
                !player.getPosition().up().equals(Pos) &&
                BlockUtil.isIntersected(Pos) &&
                !player.getEntityBoundingBox().intersects(new AxisAlignedBB(Pos)) &&
                validPlace(Pos));
    }

    private boolean validPlace(BlockPos pos) {
        return (!mc.world.isAirBlock(pos.down()) || !mc.world.isAirBlock(pos.north()) || !mc.world.isAirBlock(pos.east()) || !mc.world.isAirBlock(pos.west()) || !mc.world.isAirBlock(pos.south()));
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public void onPacketReceive(PacketEvents.Receive event) {
        if (fullNullCheck())
            return;
        try {
            if (event.getPacket() instanceof SPacketSoundEffect) {
                SPacketSoundEffect packet5 = (SPacketSoundEffect)event.getPacket();
                if (packet5.getCategory() == SoundCategory.BLOCKS && packet5.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE)
                    (new ArrayList(mc.world.loadedEntityList)).forEach(e -> {
                        if (e instanceof EntityEnderCrystal && this.lastCrystal.equals(e) && this.lastCrystal.getDistance(packet5.getX(), packet5.getY(), packet5.getZ()) <= 6.0D)
                            this.lastCrystal.setDead();
                    });
            }
            if (event.getPacket() instanceof SPacketSpawnObject) {
                SPacketSpawnObject packet = (SPacketSpawnObject)event.getPacket();
                if (packet.getType() == 51 && !event.isCanceled() && this.boost && ((Boolean)this.packetExplode.getValue()).booleanValue())
                    PacketExplode(packet.getEntityID());
            }
        } catch (Exception exception) {}
    }

    public void PacketExplode(int i) {
        try {
            if (canHitCrystal(this.lastCrystal.getPositionVector(), this.cobi, this.target)) {
                CPacketUseEntity wdnmd = new CPacketUseEntity((Entity)this.lastCrystal);
                wdnmd.entityId = i;
                wdnmd.action = CPacketUseEntity.Action.ATTACK;
                mc.player.connection.sendPacket((Packet)wdnmd);
            }
        } catch (Exception exception) {}
    }

    @SubscribeEvent
    public void onUpdate(MotionUpdateEvent.Tick event) {
        if (fullNullCheck())
            return;
        if (this.lastCrystal != null &&
                this.lastCrystal.isDead)
            this.boost = false;
        EntityPlayer entity = findClosestTarget();
        this.oldSlot = mc.player.inventory.currentItem;
        this.pickaxeitem = InventoryUtil.findHotbarBlock(ItemPickaxe.class);
        this.crystalitem = InventoryUtil.findHotbarBlock(ItemEndCrystal.class);
        this.ObiItem = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        if (this.updatePos.passed(250)) {
            this.lastX = this.currentX;
            this.lastY = this.currentY;
            this.lastZ = this.currentZ;
            this.updatePos.reset();
        }
        if (this.pickaxeitem == -1 || this.crystalitem == -1 || this.ObiItem == -1) {
            ChatUtil.sendMessage(ChatFormatting.WHITE + "Not enough Material");
            disable();
            return;
        }
        if (entity != null) {
            int n5;
            this.target = entity;
            if (entity.getDistance((Entity)mc.player) > ((Integer)this.range.getValue()).intValue()) {
                this.cobi = null;
                this.stage = 0;
                return;
            }
            BlockPos obiUpExtend = (new BlockPos((Entity)entity)).add(0.0D, 3.0D, 0.0D);
            BlockPos obi = (new BlockPos((Entity)entity)).add(-1.0D, 1.0D, 0.0D);
            BlockPos obi2 = (new BlockPos((Entity)entity)).add(0.0D, 1.0D, 1.0D);
            BlockPos obi3 = (new BlockPos((Entity)entity)).add(0.0D, 1.0D, -1.0D);
            BlockPos obi4 = (new BlockPos((Entity)entity)).add(1.0D, 1.0D, 0.0D);
            BlockPos obiUp = (new BlockPos((Entity)entity)).add(0.0D, 2.0D, 0.0D);
            if (validPosition(obiUpExtend, entity) && mc.world.isAirBlock(obiUpExtend.down()) && checkAirBlock(obiUpExtend, false)) {
                this.cobi = obiUpExtend;
                this.stage = 1;
            } else if (validPosition(obi, entity) && checkAirBlock(obi, true)) {
                this.cobi = obi;
                this.stage = 0;
            } else if (validPosition(obi2, entity) && checkAirBlock(obi2, true)) {
                this.cobi = obi2;
                this.stage = 0;
            } else if (validPosition(obi3, entity) && checkAirBlock(obi3, true)) {
                this.cobi = obi3;
                this.stage = 0;
            } else if (validPosition(obi4, entity) && checkAirBlock(obi4, true)) {
                this.cobi = obi4;
                this.stage = 0;
            } else if (validPosition(obiUp, entity) && checkAirBlock(obiUp, false)) {
                this.cobi = obiUp;
                this.stage = 1;
            }
            if (this.cobi == null || !mc.world.getBlockState(this.cobi.up(2)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(this.cobi.up()).getBlock().equals(Blocks.AIR)) {
                disable();
                return;
            }
            if (this.lastCrystal != null && !this.lastCrystal.isDead &&
                    !CrystalHelper.checkBreakRange(this.lastCrystal, 5.0F, 8.0F, 20, new BlockPos.MutableBlockPos())) {
                InventoryUtil.switchToHotbarSlot(this.oldSlot, false);
                return;
            }
            if (this.lastCrystal != null && ((Boolean)this.crystalDead.getValue()).booleanValue() &&
                    !this.lastCrystal.isDead) {
                InventoryUtil.switchToHotbarSlot(this.oldSlot, false);
                return;
            }
            this.blockRenderSmooth.updatePos(this.cobi);
            this.currentX = this.cobi.x;
            this.currentY = this.cobi.y;
            this.currentZ = this.cobi.z;
            switch (this.progress) {
                case 0:
                    if (this.firsttime < 1)
                        this.flag = true;
                    InventoryUtil.switchToHotbarSlot(this.ObiItem, false);
                    if (mc.world.isAirBlock(this.cobi.down()) && this.stage == 0)
                        BlockUtil.placeBlock(this.cobi.down(), EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), ((Boolean)this.packet.getValue()).booleanValue());
                    if (mc.world.isAirBlock(this.cobi))
                        BlockUtil.placeBlock(this.cobi, EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), ((Boolean)this.packet.getValue()).booleanValue());
                    InventoryUtil.switchToHotbarSlot(this.oldSlot, false);
                    this.progress++;
                    break;
                case 1:
                    if (this.firsttime < 1 || this.lastX != this.currentX || this.lastZ != this.currentZ || this.lastY != this.currentY)
                        mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.cobi, BlockUtil.getRayTraceFacing(this.cobi)));
                    InventoryUtil.switchToHotbarSlot(this.crystalitem, false);
                    mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(this.cobi, EnumFacing.UP, EnumHand.MAIN_HAND, 0.5F, 1.0F, 0.5F));
                    InventoryUtil.switchToHotbarSlot(this.oldSlot, false);
                    this.progress++;
                    break;
                case 2:
                    InventoryUtil.switchToHotbarSlot(this.pickaxeitem, false);
                    mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.cobi, BlockUtil.getRayTraceFacing(this.cobi)));
                    InventoryUtil.switchToHotbarSlot(this.oldSlot, false);
                    this.progress++;
                    break;
                case 3:
                    n5 = 0;
                    if (mc.world.isAirBlock(this.cobi))
                        for (Entity entity3 : mc.world.loadedEntityList) {
                            if (entity.getDistance(entity3) > ((Integer)this.range.getValue()).intValue() ||
                                    !(entity3 instanceof EntityEnderCrystal))
                                continue;
                            if (this.breakDelay.passed(50)) {
                                mc.player.connection.sendPacket((Packet)new CPacketUseEntity(entity3));
                                this.breakDelay.reset();
                            }
                            this.lastCrystal = (EntityEnderCrystal)entity3;
                            this.boost = true;
                            if (((Boolean)this.swing.getValue()).booleanValue() && !((Boolean)this.pswing.getValue()).booleanValue()) {
                                mc.player.swingArm(EnumHand.MAIN_HAND);
                            } else if (((Boolean)this.swing.getValue()).booleanValue() && ((Boolean)this.pswing.getValue()).booleanValue()) {
                                mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
                            }
                            n5++;
                        }
                    if (n5 != 0 && !this.flag)
                        break;
                    this.progress = 0;
                    this.firsttime++;
                    break;
            }
        } else {
            ChatUtil.sendMessage("[Disabled Due To No Target!");
            disable();
        }
    }

    public boolean checkAirBlock(BlockPos pos, boolean down) {
        boolean horizontal = (!mc.world.isAirBlock(pos.north()) || !mc.world.isAirBlock(pos.east()) || !mc.world.isAirBlock(pos.west()) || !mc.world.isAirBlock(pos.south()));
        if (down)
            return (horizontal || !mc.world.isAirBlock(pos.down()));
        return horizontal;
    }

    public boolean canHitCrystal(Vec3d crystal, BlockPos pos, EntityPlayer player) {
        float healthSelf = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        if (mc.player.isDead || healthSelf <= 0.0F || player.isDead)
            return false;
        double minDamage = 0.20000000298023224D;
        double target = CrystalDamageCalculator.Companion.calcDamage((EntityLivingBase)player, player.getPositionVector(), player.getEntityBoundingBox(), crystal.x, crystal.y, crystal.z, new BlockPos.MutableBlockPos());
        if (((CheckMode)this.mode.getValue()).equals(CheckMode.Damage))
            return (target < minDamage);
        return mc.world.isAirBlock(pos);
    }

    public enum CheckMode {
        Air, Damage;
    }
}
