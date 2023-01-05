package me.dyzjct.kura.module.modules.crystalaura;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dyzjct.kura.concurrent.TaskManager;
import me.dyzjct.kura.event.events.client.PacketEvents;
import me.dyzjct.kura.event.events.render.RenderEvent;
import me.dyzjct.kura.event.events.render.RenderModelEvent;
import me.dyzjct.kura.manager.FriendManager;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.module.ModuleManager;
import me.dyzjct.kura.module.modules.chat.AutoGG;
import me.dyzjct.kura.module.modules.crystalaura.CrystalHelper.CrystalTargetold;
import me.dyzjct.kura.setting.*;
import me.dyzjct.kura.utils.Timer;
import me.dyzjct.kura.utils.entity.CrystalUtil;
import me.dyzjct.kura.utils.font.FontUtils;
import me.dyzjct.kura.utils.gl.MelonTessellator;
import me.dyzjct.kura.utils.gl.XG42Tessellator;
import me.dyzjct.kura.utils.inventory.InventoryUtil;
import me.dyzjct.kura.utils.math.GeometryMasks;
import me.dyzjct.kura.utils.math.MathUtil;
import me.dyzjct.kura.utils.math.RotationUtil;
import me.dyzjct.kura.utils.mc.ChatUtil;
import me.dyzjct.kura.utils.render.sexy.BlockRenderSmooth;
import me.dyzjct.kura.utils.render.sexy.FadeUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by zenhao on 07/06/2021.
 * Updated by zenhao on 21/08/2021.
 */
@Module.Info(name = "LunarAura", category = Category.XDDD)
public class LunarAura extends Module {
    public static CopyOnWriteArrayList<CPacketUseEntity> packetList = new CopyOnWriteArrayList<>();
    public static HashMap<BlockPos, Double> renderBlockDmg = new HashMap<>();
    public static LunarAura INSTANCE = new LunarAura();
    public static EntityPlayer renderEnt;
    public ModeSetting<?> p = msetting("Page", Page.GENERAL);
    //Page GENERAL
    public BooleanSetting place = bsetting("Place", true).m(p, Page.GENERAL);
    public BooleanSetting multiplace = bsetting("MultiPlace", false).m(p, Page.GENERAL);
    public IntegerSetting PlaceSpeed = isetting("PlaceSpeed", 36, 1, 45).b(place).m(p, Page.GENERAL);
    public BooleanSetting explode = bsetting("Explode", true).m(p, Page.GENERAL);
    public BooleanSetting PacketExplode = bsetting("PacketExplode", true).m(p, Page.GENERAL);
    public IntegerSetting HitDelay = isetting("HitDelay", 30, 0, 500).b(explode).m(p, Page.GENERAL);
    public BooleanSetting antiWeakness = bsetting("AntiWeakness", false).b(explode).m(p, Page.GENERAL);
    public BooleanSetting packetAntiWeak = bsetting("PacketAntiWeakness", false).b(antiWeakness).m(p, Page.GENERAL);
    public BooleanSetting wall = bsetting("Wall", true).m(p, Page.GENERAL);
    public BooleanSetting wallAI = bsetting("WallAI", true).b(wall).m(p, Page.GENERAL);
    public DoubleSetting placeRange = dsetting("PlaceRange", 5.5D, 0D, 6D).b(place).m(p, Page.GENERAL);
    public DoubleSetting breakRange = dsetting("BreakRange", 5.5, 0, 6).b(explode).m(p, Page.GENERAL);
    public DoubleSetting WallRange = dsetting("WallRange", 3, 0.1, 4).b(wall).m(p, Page.GENERAL);
    public IntegerSetting enemyRange = isetting("EnemyRange", 7, 1, 10).m(p, Page.GENERAL);
    public IntegerSetting breakMinDmg = isetting("BreakMinDmg", 2, 0, 36).b(explode).m(p, Page.GENERAL);
    public IntegerSetting minDamage = isetting("MinDmg", 4, 0, 36).m(p, Page.GENERAL);
    public IntegerSetting MaxselfDMG = isetting("MaxSelfDmg", 12, 0, 36).m(p, Page.GENERAL);
    //Page COMBAT
    public ModeSetting<?> switchmode = msetting("SwitchMode", Switch.Off).m(p, Page.COMBAT);
    public BooleanSetting rotate = bsetting("Rotate", true).m(p, Page.COMBAT);
    public BooleanSetting endcrystal = bsetting("1.13Place", false).m(p, Page.COMBAT);
    public BooleanSetting speedDebug = bsetting("SpeedDebug", false).m(p, Page.COMBAT);
    public BooleanSetting FacePlace = bsetting("FacePlace", true).m(p, Page.COMBAT);
    public IntegerSetting BlastHealth = isetting("BlastHealth", 10, 0, 20).m(p, Page.COMBAT);
    public BooleanSetting ArmorCheck = bsetting("ArmorFucker", true).m(p, Page.COMBAT);
    public IntegerSetting ArmorRate = isetting("Armor%", 15, 0, 100).b(ArmorCheck).m(p, Page.COMBAT);
    public BooleanSetting PredictHit = bsetting("PredictHit", false).m(p, Page.COMBAT);
    public IntegerSetting PredictHitFactor = isetting("PredictHitFactor", 2, 1, 20).b(PredictHit).m(p, Page.COMBAT);
    public BooleanSetting MotionPredict = bsetting("MotionPredict", true).m(p, Page.COMBAT);
    //Page DEV
    public BooleanSetting AutoMineHole = bsetting("AutoHoleMining", false).m(p, Page.DEV);
    public BooleanSetting chainPop = bsetting("TryChainPop", true).m(p, Page.DEV);
    public BooleanSetting hurtTimeBypass = bsetting("HurtTimeBypass", true).m(p, Page.DEV);
    public BooleanSetting packetOptimize = bsetting("PacketOptimize", true).m(p, Page.DEV);
    public BooleanSetting autoToggle = bsetting("AutoToggle", true).m(p, Page.DEV);
    public BooleanSetting packetSwing = bsetting("PacketSwing", true).m(p, Page.DEV);
    //Page RENDER
    public ModeSetting<?> mode = msetting("Mode", RenderMode.FULL).m(p, Page.RENDER);
    public BooleanSetting renderDamage = bsetting("RenderDamage", true).m(p, Page.RENDER);
    public IntegerSetting red = isetting("Red", 255, 0, 255).m(p, Page.RENDER);
    public IntegerSetting green = isetting("Green", 255, 0, 255).m(p, Page.RENDER);
    public IntegerSetting blue = isetting("Blue", 255, 0, 255).m(p, Page.RENDER);
    public IntegerSetting alpha = isetting("Alpha", 60, 0, 255).m(p, Page.RENDER);
    public BooleanSetting rainbow = bsetting("Rainbow", true).m(p, Page.RENDER);
    public FloatSetting RGBSpeed = fsetting("RGBSpeed", 1, 0, 255).b(rainbow).m(p, Page.RENDER);
    public FloatSetting Saturation = fsetting("Saturation", 0.3f, 0, 1).b(rainbow).m(p, Page.RENDER);
    public FloatSetting Brightness = fsetting("Brightness", 1f, 0, 1).b(rainbow).m(p, Page.RENDER);
    public BlockRenderSmooth blockRenderSmooth = new BlockRenderSmooth(new BlockPos(0, 0, 0), 550L);
    transient AtomicInteger lastEntityID = new AtomicInteger(-1);
    public FadeUtils fadeBlockSize = new FadeUtils(300L);
    public Timer PacketExplodeTimer = new Timer();
    public Timer ExplodeTimer = new Timer();
    public Timer PlaceTimer = new Timer();
    public Timer CalcTimer = new Timer();
    public EntityEnderCrystal lastCrystal;
    public Vec3d PredictionTarget;
    public BlockPos OffsetPos;
    public BlockPos render;
    public BlockPos webPos;
    public boolean ShouldOffFadeRender = false;
    public boolean ShouldInfoLastBreak = false;
    public boolean ShouldDisableRender = true;
    public boolean ShouldOffFadeReset = false;
    public boolean ShouldShadeRender = false;
    public boolean switchCooldown = false;
    public boolean afterAttacking = false;
    public boolean canPredictHit = false;
    public boolean isAttacking = false;
    public boolean ShouldStop = false;
    public boolean isActive = false;
    public boolean switched = false;
    public boolean rotating = false;
    public long infoBreakTime = 0L;
    public long lastBreakTime = 0L;
    public int placements = 0;
    public int StuckTimes = 0;
    public int crystals = 0;
    public int newSlot = -1;
    public int oldSlot = -1;
    public int picSlot = -1;
    public int CSlot = -1;
    public float Pitch;
    public float Yaw;

    public static EnumFacing enumFacing(BlockPos blockPos) {
        try {
            EnumFacing[] values;
            int length = (values = EnumFacing.values()).length;
            int i = 0;
            while (i < length) {
                EnumFacing enumFacing = values[i];
                Vec3d vec3d = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
                Vec3d vec3d2 = new Vec3d(blockPos.getX() + enumFacing.getDirectionVec().getX(), blockPos.getY() + enumFacing.getDirectionVec().getY(), blockPos.getZ() + enumFacing.getDirectionVec().getZ());
                RayTraceResult rayTraceBlocks;
                if ((rayTraceBlocks = mc.world.rayTraceBlocks(vec3d, vec3d2, false, true, false)) != null
                        && rayTraceBlocks.typeOfHit.equals(RayTraceResult.Type.BLOCK) && rayTraceBlocks.getBlockPos().equals(blockPos)) {
                    return enumFacing;
                }
                i++;
            }
            if (blockPos.getY() > mc.player.posY + mc.player.getEyeHeight()) {
                return EnumFacing.DOWN;
            }
        } catch (Exception ignored) {
        }
        return EnumFacing.UP;
    }

    public static boolean CanSeeBlock(BlockPos blockPos) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), false, true, false) != null;
    }

    public void resetRotation() {
        if (fullNullCheck()) {
            return;
        }
        Yaw = mc.player.rotationYaw;
        Pitch = mc.player.rotationPitch;
        rotating = false;
    }

    public static double getVdistance(BlockPos blockPos, double n, double n2, double n3) {
        double n4 = blockPos.x - n;
        double n5 = blockPos.y - n2;
        double n6 = blockPos.z - n3;
        return Math.sqrt(n4 * n4 + n5 * n5 + n6 * n6);
    }

    public static double getRange(Vec3d a, double x, double y, double z) {
        double xl = a.x - x;
        double yl = a.y - y;
        double zl = a.z - z;
        return Math.sqrt(xl * xl + yl * yl + zl * zl);
    }

    public static float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
        float damage = damageI;
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            int k = 0;
            try {
                k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            } catch (Exception ignored) {
            }
            float f = MathHelper.clamp((float) k, 0.0f, 20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage -= damage / 4.0f;
            }
            damage = Math.max(damage, 0.0f);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    public static float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * ((diff == 0) ? 0.0f : ((diff == 2) ? 1.0f : ((diff == 1) ? 0.5f : 1.5f)));
    }

    public static Vec3d PredictionHandler(EntityPlayer target, boolean predict) {
        double partialTick = mc.timer.tickLength / 1000.0f;
        double motionX = target.posX - target.lastTickPosX;
        double motionY = target.posY - target.lastTickPosY;
        double motionZ = target.posZ - target.lastTickPosZ;
        double posX = target.posX + (predict ? (motionX * (motionX * motionX + 0.1) / partialTick) : 0);
        double posY = target.posY + (predict ? (motionY * (motionY * motionY + 0.2) / partialTick) : 0) + target.getEyeHeight() - 0.15;
        double posZ = target.posZ + (predict ? (motionZ * (motionZ * motionZ + 0.1) / partialTick) : 0);
        return new Vec3d(posX, posY, posZ);
    }

    @SubscribeEvent
    public void renderModelRotation(RenderModelEvent event) {
        if (!rotate.getValue() || fullNullCheck()) return;
        if (rotating) {
            event.rotating = true;
            event.pitch = Pitch;
        }
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (PredictHit.getValue() || autoToggle.getValue()) {
            toggle();
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvents.Send event) {
        if (fullNullCheck()) {
            return;
        }
        Packet<?> packet = event.packet;
        if (rotate.getValue()) {
            if (packet instanceof CPacketPlayer) {
                ((CPacketPlayer) packet).yaw = Yaw;
                ((CPacketPlayer) packet).pitch = Pitch;
            }
        }
        if (packetOptimize.getValue()) {
            if (event.getPacket() instanceof CPacketUseEntity) {
                if (packetList.size() > 40) {
                    event.setCanceled(true);
                    packetList.clear();
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onPacketReceive(PacketEvents.Receive event) {
        if (fullNullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketSpawnObject) {
            SPacketSpawnObject packet = event.getPacket();
            if (PredictHit.getValue()) {
                for (Entity e : new ArrayList<>(mc.world.loadedEntityList)) {
                    if (e instanceof EntityItem || e instanceof EntityArrow || e instanceof EntityEnderPearl || e instanceof EntitySnowball || e instanceof EntityEgg) {
                        if (e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6) {
                            lastEntityID.set(-1);
                            canPredictHit = false;
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
            }
        }
        if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet5 = event.getPacket();
            if (packet5.getSound().equals(SoundEvents.ENTITY_EXPERIENCE_BOTTLE_THROW) || packet5.getSound().equals(SoundEvents.ENTITY_ITEM_BREAK)) {
                canPredictHit = false;
            }
            if (packet5.getCategory() == SoundCategory.BLOCKS && packet5.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE && render != null) {
                ShouldInfoLastBreak = true;
                for (Entity e : new ArrayList<>(mc.world.loadedEntityList)) {
                    if (e instanceof EntityEnderCrystal) {
                        if (e.getDistance(packet5.getX(), packet5.getY(), packet5.getZ()) <= 6.0f) {
                            e.setDead();
                            crystals++;
                        }
                    }
                }
            }
        }
        if (event.getPacket() instanceof SPacketSpawnObject) {
            SPacketSpawnObject packet = event.getPacket();
            if (packet.getType() == 51) {
                lastEntityID.getAndUpdate(it -> Math.max(it, ((SPacketSpawnObject) (event.packet)).getEntityID()));
                if (PacketExplode.getValue() && ExplodeTimer.passed(HitDelay.getValue()) && explode.getValue() && lastCrystal != null) {
                    if (wall.getValue() && mc.player.getDistance(lastCrystal) > WallRange.getValue() && CanSeeBlock(new BlockPos(lastCrystal))) {
                        return;
                    }
                    PacketExplode(packet.getEntityID());
                    PacketExplodeTimer.reset();
                }
            } else {
                lastEntityID.set(-1);
            }
        }
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = event.getPacket();
            if (packet.getOpCode() == 35) {
                EntityPlayer entity = (EntityPlayer) packet.getEntity(mc.world);
                if (chainPop.getValue()) {
                    if (mc.player.getDistance(entity) < 6) {
                        entity.maxHurtTime = 0;
                        MinecraftForge.EVENT_BUS.post(new TotemPopEvent(entity));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void ChainPop(TotemPopEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (chainPop.getValue()) {
            if (lastCrystal != null && event.getEntity().equals(renderEnt)) {
                renderEnt.hurtTime = renderEnt.maxHurtTime;
                PacketExplode(lastCrystal.getEntityId());
                ChatUtil.sendMessage(ChatFormatting.AQUA + "Trying To ChainPop " + ChatFormatting.LIGHT_PURPLE + renderEnt.getName() + ChatFormatting.RED + " !");
            }
        }
    }

    @SubscribeEvent
    public void doCrystalawa(TickEvent.ClientTickEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (rotating) {
            mc.player.rotationYawHead = Yaw;
            mc.player.renderYawOffset = Yaw;
        }
        if (CalcTimer.passed(1000)) {
            CalcTimer.reset();
            if (speedDebug.getValue()) {
                ChatUtil.NoSpam.sendMessage("CrystalSpeed: " + crystals + " Crystals/s");
            }
            crystals = 0;
        }
        oldSlot = mc.player.inventory.currentItem;
        newSlot = mc.player.inventory.currentItem;
        picSlot = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
        CSlot = InventoryUtil.getItemHotbar(Items.END_CRYSTAL);
        Update();
    }

    public void Update() {
        if (fullNullCheck()) {
            return;
        }
        EntityEnderCrystal crystal = mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal &&
                canHitCrystal((EntityEnderCrystal) e)).map(e -> (EntityEnderCrystal) e).min(Comparator.comparing(e -> mc.player.getDistance(e))).orElse(null);
        if (mc.player != null && crystal != null && renderEnt != null) {
            int invTime = renderEnt.hurtTime;
            if (hurtTimeBypass.getValue()) {
                renderEnt.hurtTime = 0;
            }
            if (explode.getValue() && mc.player.getDistance(crystal) <= breakRange.getValue()) {
                if (!mc.player.canEntityBeSeen(crystal)) {
                    StuckTimes++;
                    afterAttacking = false;
                }
                if (mc.player.canEntityBeSeen(crystal) || (mc.player.getDistance(crystal) < WallRange.getValue() && wall.getValue())) {
                    lastCrystal = crystal;
                    StuckTimes = 0;
                    ExplodeCrystal();
                    afterAttacking = true;
                }
            }
            if (multiplace.getValue()) {
                if (placements >= 3) {
                    placements = 0;
                    afterAttacking = true;
                    return;
                }
            }
            if (hurtTimeBypass.getValue()) {
                renderEnt.maxHurtResistantTime = invTime;
            }
        }
        try {
            CSlot = InventoryUtil.getItemHotbar(Items.END_CRYSTAL);
            picSlot = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
            int crystalSlot = (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) ? mc.player.inventory.currentItem : -1;
            if (crystalSlot == -1) {
                for (int l = 0; l < 9; ++l) {
                    if (mc.player.inventory.getStackInSlot(l).getItem() == Items.END_CRYSTAL) {
                        crystalSlot = l;
                        break;
                    }
                }
            }
            boolean offhand = false;
            if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
                offhand = true;
            } else if (crystalSlot == -1) {
                return;
            }
            CrystalTargetold crystalTarget = Calc();
            renderEnt = (EntityPlayer) crystalTarget.target;
            render = crystalTarget.blockPos;
            if (renderEnt == null || render == null) {
                ShouldShadeRender = true;
                renderEnt = null;
                render = null;
                resetRotation();
                return;
            } else if (ShouldShadeRender) {
                fadeBlockSize.reset();
                ShouldShadeRender = false;
            }
            if (switchmode.getValue() == Switch.GhostHand && mc.getConnection() != null && CSlot != -1) {
                switchIt(CSlot);
            }
            if (AutoGG.INSTANCE.isEnabled() && renderEnt != null) {
                AutoGG.INSTANCE.addTargetedPlayer(renderEnt.getName());
            }
            if (place.getValue() && render != null) {
                if (rotate.getValue()) {
                    lookAtPos(render, enumFacing(render));
                }
                if (!offhand && mc.player.inventory.currentItem != crystalSlot) {
                    if (switchmode.getValue() == Switch.AutoSwitch) {
                        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemAppleGold && mc.player.isHandActive()) {
                            isActive = false;
                            return;
                        }
                        isActive = true;
                        mc.player.inventory.currentItem = crystalSlot;
                        switchCooldown = true;
                    }
                    return;
                }
                if (switchCooldown) {
                    switchCooldown = false;
                    return;
                }
                if (mc.getConnection() != null) {
                    if (hasDelayRunPlace(PlaceSpeed.getValue())) {
                        TaskManager.repeat(3, () -> {
                            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(render, enumFacing(render), mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
                            placements++;
                        });
                        blockRenderSmooth.setNewPos(render);
                        if (PredictHit.getValue() && renderEnt != null) {
                            int syncedId = lastEntityID.get();
                            AtomicInteger count = new AtomicInteger(0);
                            try {
                                if (renderEnt.isDead || !canPredictHit || !canHitCrystal(lastCrystal)) {
                                    PlaceTimer.reset();
                                    return;
                                }
                                if (mc.player.getHealth() + mc.player.getAbsorptionAmount() > MaxselfDMG.getValue() && lastEntityID.get() != -1 && lastCrystal != null && canPredictHit) {
                                    TaskManager.repeat(PredictHitFactor.getValue(), () -> {
                                        if (syncedId != -1) {
                                            PacketExplode(syncedId + count.getAndIncrement() + 1);
                                        }
                                    });
                                }
                            } catch (Exception ignored) {
                            }
                        }
                        PlaceTimer.reset();
                    }
                }
            }
            if (AutoMineHole.getValue()) {
                List<EntityPlayer> entities = getEntities();
                for (Entity e : new ArrayList<>(entities)) {
                    if (e != mc.player) {
                        BlockPos minePos = null;
                        BlockPos blockPos = new BlockPos(e);
                        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                            IBlockState touchingState = mc.world.getBlockState(blockPos.offset(facing));
                            if (touchingState.getBlock() == Blocks.OBSIDIAN) {
                                minePos = blockPos.offset(facing);
                            }
                        }
                        InventoryUtil.switchToHotbarSlot(picSlot, false);
                        if (mc.getConnection() != null) {
                            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, minePos, enumFacing(minePos)));
                            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, minePos, enumFacing(minePos)));
                            swing();
                        }
                        InventoryUtil.switchToHotbarSlot(newSlot, false);
                    }
                }
            }
            if (switchmode.getValue() == Switch.GhostHand) {
                switchIt(newSlot);
            }
        } catch (Exception ignored) {
        }
    }

    public CrystalTargetold Calc() {
        List<EntityPlayer> entities = getEntities();
        double damage = 0.5;
        newSlot = mc.player.inventory.currentItem;
        EntityPlayer target = null;
        BlockPos tempBlock = null;
        BlockPos setToAir = null;
        IBlockState state = null;
        List<BlockPos> default_blocks;
        if (wall.getValue() && wallAI.getValue()) {
            double TempRange = placeRange.getValue();
            double temp2 = TempRange - (StuckTimes * 0.5);
            if (StuckTimes > 0) {
                TempRange = placeRange.getValue();
                if (temp2 > WallRange.getValue()) {
                    TempRange = temp2;
                } else if (WallRange.getValue() < placeRange.getValue()) {
                    TempRange = 3.0;
                }
            }
            default_blocks = rendertions(TempRange);
        } else {
            default_blocks = rendertions(placeRange.getValue());
        }
        for (EntityPlayer entity2 : new ArrayList<>(entities)) {
            if (entity2 != mc.player) {
                if (entity2 instanceof EntityPlayer) {
                    PredictionTarget = PredictionHandler(entity2, MotionPredict.getValue());
                    BlockPos playerPos = new BlockPos(entity2.getPositionVector());
                    Block web = mc.world.getBlockState(playerPos).getBlock();
                    if (web == Blocks.WEB) {
                        setToAir = playerPos;
                        state = mc.world.getBlockState(playerPos);
                        mc.world.setBlockToAir(playerPos);
                    }
                    if (entity2.getHealth() <= 0.0f) {
                        continue;
                    }
                    canPredictHit = (!PredictHit.getValue() || !entity2.getHeldItemMainhand().getItem().equals(Items.EXPERIENCE_BOTTLE)) && !entity2.getHeldItemOffhand().getItem().equals(Items.EXPERIENCE_BOTTLE) || ModuleManager.getModuleByName("AutoExp").isDisabled();
                    for (BlockPos blockPos : new ArrayList<>(default_blocks)) {
                        double d = calculateDamage(blockPos.x + 0.5, blockPos.y + 1, blockPos.z + 0.5, entity2, PredictionTarget);
                        if (d < damage) continue;
                        if (entity2.getDistanceSq(blockPos) >= enemyRange.getValue() * enemyRange.getValue()) continue;
                        if (mc.player.getDistance(blockPos.x, blockPos.y, blockPos.z) > placeRange.getValue()) continue;
                        if (d < (FacePlace.getValue() ? (canFacePlace(entity2) ? 1 : minDamage.getValue()) : minDamage.getValue()))
                            continue;
                        float healthTarget = entity2.getHealth() + entity2.getAbsorptionAmount();
                        float healthSelf = mc.player.getHealth() + mc.player.getAbsorptionAmount();
                        double self = calculateDamage(blockPos.x + 0.5, blockPos.y + 1, blockPos.z + 0.5, mc.player);
                        if (self > d && d < healthTarget) continue;
                        if (self - 0.5 > healthSelf) continue;
                        if (self > MaxselfDMG.getValue()) continue;
                        if (chainPop.getValue()) {
                            if (isDoublePoppable(entity2, (float) self)) {
                                if (d < healthTarget && self > healthSelf) continue;
                            }
                        }
                        if (!wall.getValue()) {
                            if (WallRange.getValue() > 0)
                                if (CanSeeBlock(blockPos))
                                    if (getVdistance(blockPos, mc.player.posX, mc.player.posY, mc.player.posZ) > WallRange.getValue())
                                        continue;
                        }
                        damage = d;
                        tempBlock = blockPos;
                        target = entity2;
                        if (renderDamage.getValue()) renderBlockDmg.put(tempBlock, d);
                    }
                    if (setToAir != null) {
                        mc.world.setBlockState(setToAir, state);
                        webPos = tempBlock;
                    }
                }
                if (target != null) {
                    break;
                }
            }
        }
        return new CrystalTargetold(tempBlock, target);
    }

    public void swing() {
        if (fullNullCheck()) {
            return;
        }
        if (packetSwing.getValue()) {
            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
        } else {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    public void switchIt(int slot) {
        if (fullNullCheck()) {
            return;
        }
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
    }

    public boolean hasDelayRunPlace(double placeSpeed) {
        return PlaceTimer.passed(1000 / placeSpeed);
    }

    public List<EntityPlayer> getEntities() {
        List<EntityPlayer> entities = mc.world.playerEntities.stream()
                .filter(entityPlayer -> !FriendManager.isFriend(entityPlayer.getName()))
                .filter(entity -> mc.player.getDistance(entity) < enemyRange.getValue())
                .collect(Collectors.toList());
        for (EntityPlayer ite2 : new ArrayList<>(entities)) {
            if (mc.player.getDistance(ite2) > enemyRange.getValue()) entities.remove(ite2);
            if (ite2 == mc.player) entities.remove(ite2);
        }
        entities.sort(Comparator.comparing(EntityLivingBase::getHealth));
        return entities;
    }

    public void ExplodeCrystal() {
        oldSlot = mc.player.inventory.currentItem;
        EntityEnderCrystal crystal = mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal &&
                canHitCrystal((EntityEnderCrystal) e)).map(e -> (EntityEnderCrystal) e).min(Comparator.comparing(e -> mc.player.getDistance(e))).orElse(null);
        if (crystal != null) {
            if (rotate.getValue()) {
                lookAtCrystal(crystal);
            }
            if (ExplodeTimer.passed(HitDelay.getValue()) && mc.getConnection() != null) {
                if (antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS) && (!mc.player.isPotionActive(MobEffects.STRENGTH) || Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() < 1)) {
                    if (!isAttacking) {
                        oldSlot = mc.player.inventory.currentItem;
                        isAttacking = true;
                    }
                    for (int i = 0; i < 45; ++i) {
                        ItemStack stack = mc.player.inventory.getStackInSlot(i);
                        if (stack != ItemStack.EMPTY) {
                            if (stack.getItem() instanceof ItemSword) {
                                oldSlot = i;
                                break;
                            } else if (stack.getItem() instanceof ItemTool) {
                                oldSlot = i;
                                break;
                            }
                        }
                    }
                    if (oldSlot != -1) {
                        if (packetAntiWeak.getValue()) {
                            switchIt(oldSlot);
                            switched = true;
                        } else {
                            mc.player.inventory.currentItem = oldSlot;
                            switchCooldown = true;
                        }
                    }
                }
                PacketExplode(crystal.getEntityId());
                if (packetSwing.getValue()) {
                    mc.player.connection.sendPacket(new CPacketAnimation(mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
                } else {
                    mc.player.swingArm(mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                }
                mc.player.resetCooldown();
                ExplodeTimer.reset();
            }
            if (switched) {
                switchIt(newSlot);
            }
            if (lastBreakTime == 0L) {
                lastBreakTime = System.currentTimeMillis();
                ShouldInfoLastBreak = false;
            }
        }
    }

    public void PacketExplode(int i) {
        if (lastCrystal != null) {
            try {
                if (mc.player.getDistance(lastCrystal) > breakRange.getValue() || !canHitCrystal(lastCrystal)) return;
                CPacketUseEntity wdnmd = new CPacketUseEntity(lastCrystal);
                wdnmd.entityId = i;
                wdnmd.action = CPacketUseEntity.Action.ATTACK;
                mc.player.connection.sendPacket(wdnmd);
                if (packetOptimize.getValue()) {
                    packetList.add(wdnmd);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public boolean isDoublePoppable(EntityPlayer player, float damage) {
        return (player.getHealth() + player.getAbsorptionAmount()) <= damage;
    }

    public boolean canHitCrystal(EntityEnderCrystal crystal) {
        if (mc.player.getDistance(crystal) > breakRange.getValue()) return false;
        float selfDamage = calculateDamage(crystal.posX, crystal.posY, crystal.posZ, mc.player);
        float healthSelf = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        if (selfDamage >= healthSelf) return false;
        List<EntityPlayer> entities = mc.world.playerEntities.stream()
                .filter(e -> mc.player.getDistance(e) <= enemyRange.getValue())
                .filter(e -> mc.player != e)
                .filter(e -> !FriendManager.isFriend(e.getName()))
                .sorted(Comparator.comparing(e -> mc.player.getDistance(e)))
                .collect(Collectors.toList());
        for (EntityPlayer player : new ArrayList<>(entities)) {
            if (mc.player.isDead || healthSelf <= 0.0f) continue;
            double minDamage = breakMinDmg.getValue();
            if (canFacePlace(player)) {
                minDamage = 1;
            }
            double target = calculateDamage(crystal.posX, crystal.posY, crystal.posZ, player);
            if (target > player.getHealth() + player.getAbsorptionAmount() && selfDamage < healthSelf) {
                return true;
            }
            if (target < minDamage) continue;
            if (selfDamage > target) continue;
            return true;
        }
        return false;
    }

    public boolean canFacePlace(EntityLivingBase target) {
        float healthTarget = target.getHealth() + target.getAbsorptionAmount();
        if (healthTarget <= BlastHealth.getValue()) {
            return true;
        } else if (ArmorCheck.getValue()) {
            for (ItemStack itemStack : target.getArmorInventoryList()) {
                if (itemStack.isEmpty) {
                    continue;
                }
                float dmg = ((float) itemStack.getMaxDamage() - (float) itemStack.getItemDamage()) / (float) itemStack.getMaxDamage();
                if (dmg <= ArmorRate.getValue() / 100f) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<BlockPos> rendertions(double range) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(CrystalUtil.getSphere(getPlayerPos(), range, range, false, true, 0)
                .stream()
                .filter(v -> canPlaceCrystal(v, endcrystal.getValue())).collect(Collectors.toList()));
        return positions;
    }

    public Vec3d getPlayerPos() {
        return new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ);
    }

    public boolean canPlaceCrystal(BlockPos blockPos, boolean newPlace) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        if (mc.world.getBlockState(boost).getBlock() == Blocks.WATER
                || mc.world.getBlockState(boost).getBlock() == Blocks.WATERLILY
                || mc.world.getBlockState(boost).getBlock() == Blocks.FLOWING_WATER
                || mc.world.getBlockState(boost).getBlock() == Blocks.MAGMA
                || mc.world.getBlockState(boost).getBlock() == Blocks.LAVA
                || mc.world.getBlockState(boost).getBlock() == Blocks.FLOWING_LAVA
        ) return false;
        if (mc.world.getBlockState(boost2).getBlock() == Blocks.WATER
                || mc.world.getBlockState(boost2).getBlock() == Blocks.WATERLILY
                || mc.world.getBlockState(boost2).getBlock() == Blocks.FLOWING_WATER
                || mc.world.getBlockState(boost2).getBlock() == Blocks.MAGMA
                || mc.world.getBlockState(boost2).getBlock() == Blocks.LAVA
                || mc.world.getBlockState(boost2).getBlock() == Blocks.FLOWING_LAVA
        ) return false;
        if (newPlace) {
            if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
            if (mc.world.getBlockState(boost).getBlock() != Blocks.AIR) {
                return false;
            }
            for (Entity entity : new ArrayList<>(mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)))) {
                if (!(entity instanceof EntityEnderCrystal)) {
                    return false;
                }
            }
            for (Entity entity : new ArrayList<>(mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)))) {
                if (!(entity instanceof EntityEnderCrystal)) {
                    return false;
                }
            }
            webcalc();
        } else {
            if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
            if (mc.world.getBlockState(boost).getBlock() != Blocks.AIR || mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) {
                return false;
            }
            for (Entity entity : new ArrayList<>(mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)))) {
                if (!(entity instanceof EntityEnderCrystal)) {
                    return false;
                }
            }
            for (Entity entity : new ArrayList<>(mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)))) {
                if (!(entity instanceof EntityEnderCrystal)) {
                    return false;
                }
            }
            webcalc();
        }
        if (multiplace.getValue()) {
            return (mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK
                    || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN)
                    && mc.world.getBlockState(boost).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(boost2).getBlock() == Blocks.AIR
                    && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty()
                    && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
        }
        if (afterAttacking) {
            for (Entity entity : new ArrayList<>(mc.world.loadedEntityList)) {
                if (!(entity instanceof EntityEnderCrystal)) continue;
                EntityEnderCrystal entityEnderCrystal = (EntityEnderCrystal) entity;
                double d2 = lastCrystal != null ? lastCrystal.getDistance((double) blockPos.x + 0.5, (blockPos.y + 1), (double) blockPos.z + 0.5) : 10000.0;
                if (!(d2 > 6.0) || !(getRange(entityEnderCrystal.getPositionVector(), (double) blockPos.x + 0.5, 0, (double) blockPos.z + 0.5) < 2.0)
                        || !(Math.abs(entityEnderCrystal.posY - (double) (blockPos.y + 1)) < 2.0)) continue;
                return false;
            }
            return !(!mc.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(boost)).isEmpty()
                    || !mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(boost)).isEmpty()
                    || !mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(boost2)).isEmpty()
                    || !mc.world.getEntitiesWithinAABB(EntityArrow.class, new AxisAlignedBB(boost)).isEmpty());
        } else {
            return true;
        }
    }

    public void webcalc() {
        if (webPos != null) {
            if (mc.player.getDistanceSq(webPos) > MathUtil.square(breakRange.getValue())) {
                webPos = null;
            } else {
                for (Entity entity : new ArrayList<>(mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(webPos)))) {
                    if (entity instanceof EntityEnderCrystal) {
                        webPos = null;
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (render != null) {
            OffsetPos = new BlockPos(render);
            ShouldOffFadeReset = true;
            if (ShouldOffFadeRender) {
                ShouldOffFadeRender = false;
                fadeBlockSize.reset();
            }
            Vec3d interpolateEntity = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
            AxisAlignedBB pos = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D).offset(blockRenderSmooth.getRenderPos());
            pos = pos.offset(-interpolateEntity.x, -interpolateEntity.y, -interpolateEntity.z);
            renderESP(pos, (float) fadeBlockSize.easeOutQuad());
            if (renderDamage.getValue()) {
                if (renderBlockDmg.containsKey(render)) {
                    GlStateManager.pushMatrix();
                    Vec3d blockPos = blockRenderSmooth.getRenderPos();
                    GlStateManager.shadeModel(GL11.GL_SMOOTH);
                    XG42Tessellator.glBillboardDistanceScaled((float) blockPos.x + 0.5f, (float) blockPos.y + 0.5f, (float) blockPos.z + 0.5f, mc.player, 1f);
                    final double damage = renderBlockDmg.get(render);
                    final String damageText = (Math.floor(damage) == damage ? damage : String.format("%.1f", damage)) + "";
                    GlStateManager.disableDepth();
                    GlStateManager.translate(-(FontUtils.Comfortaa.getStringWidth(damageText) / 2.0d), 0, 0);
                    GlStateManager.scale(1, 1, 1);
                    FontUtils.Comfortaa.drawStringWithShadow(damageText, 1, 1, -1);
                    GlStateManager.popMatrix();
                }
            }
        } else if (ShouldOffFadeReset) {
            ShouldOffFadeReset = false;
            ShouldOffFadeRender = true;
            fadeBlockSize.reset();
        } else {
            if (fadeBlockSize.isEnd()) {
                ShouldOffFadeRender = false;
            }
        }
        if (ShouldOffFadeRender) {
            if (OffsetPos != null) {
                Vec3d interpolateEntity = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
                AxisAlignedBB pos = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D).offset(OffsetPos);
                pos = pos.offset(-interpolateEntity.x, -interpolateEntity.y, -interpolateEntity.z);
                renderESP(pos, (float) (1 - fadeBlockSize.easeOutQuad()));
            }
        }
    }

    public void renderESP(AxisAlignedBB axisAlignedBB, float size) {
        int hsBtoRGB = Color.HSBtoRGB(System.currentTimeMillis() % 11520L / 11520.0f * RGBSpeed.getValue(), Saturation.getValue(), Brightness.getValue());
        int r = (hsBtoRGB >> 16 & 0xFF);
        int g = (hsBtoRGB >> 8 & 0xFF);
        int b = (hsBtoRGB & 0xFF);
        double centerX = axisAlignedBB.minX + ((axisAlignedBB.maxX - axisAlignedBB.minX) / 2);
        double centerY = axisAlignedBB.minY + ((axisAlignedBB.maxY - axisAlignedBB.minY) / 2);
        double centerZ = axisAlignedBB.minZ + ((axisAlignedBB.maxZ - axisAlignedBB.minZ) / 2);
        double fullX = (axisAlignedBB.maxX - centerX);
        double fullY = (axisAlignedBB.maxY - centerY);
        double fullZ = (axisAlignedBB.maxZ - centerZ);
        double progressValX = fullX * size;
        double progressValY = fullY * size;
        double progressValZ = fullZ * size;
        int color = new Color((rainbow.getValue()) ? r : (this.red.getValue()), (rainbow.getValue()) ? g : (this.green.getValue()), (rainbow.getValue()) ? b : (this.blue.getValue()), alpha.getValue()).getRGB();
        AxisAlignedBB axisAlignedBB1 = new AxisAlignedBB(centerX - progressValX, centerY - progressValY, centerZ - progressValZ, centerX + progressValX, centerY + progressValY, centerZ + progressValZ);
        if (mode.getValue() == RenderMode.FULL) {
            XG42Tessellator.drawFullBox(axisAlignedBB1, 1, color);
//            XG42Tessellator.drawBoxTest((float) axisAlignedBB1.minX, (float) axisAlignedBB1.minY, (float) axisAlignedBB1.minZ, (float) axisAlignedBB1.maxX - (float) axisAlignedBB1.minX, (float) axisAlignedBB1.maxY - (float) axisAlignedBB1.minY, (float) axisAlignedBB1.maxZ - (float) axisAlignedBB1.minZ, (rainbow.getValue()) ? r : (this.red.getValue()), (rainbow.getValue()) ? g : (this.green.getValue()), (rainbow.getValue()) ? b : (this.blue.getValue()), alpha.getValue(), 63);
            XG42Tessellator.drawBoundingBox(axisAlignedBB1, 1f, (rainbow.getValue()) ? r : (this.red.getValue()), (rainbow.getValue()) ? g : (this.green.getValue()), (rainbow.getValue()) ? b : (this.blue.getValue()), 255);
        }
        if (mode.getValue() == RenderMode.SOLID) {
            XG42Tessellator.drawBoxTest(axisAlignedBB1, (rainbow.getValue()) ? r : (this.red.getValue()), (rainbow.getValue()) ? g : (this.green.getValue()), (rainbow.getValue()) ? b : (this.blue.getValue()), alpha.getValue(), GeometryMasks.Quad.ALL);
        }
    }

    public float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        Vec3d offset = new Vec3d(entity.posX, entity.posY, entity.posZ);
        return calculateDamage(posX, posY, posZ, entity, offset);
    }

    public float calculateDamage(double posX, double posY, double posZ, Entity entity, Vec3d vec) {
        float doubleExplosionSize = 12.0f;
        double distancedsize = getRange(vec, posX, posY, posZ) / doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = 0.0;
        try {
            blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        } catch (Exception ignore) {
        }
        double v = (1.0 - distancedsize) * blockDensity;
        float damage = (float) ((v * v + v) / 2.0 * 7.0 * doubleExplosionSize + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            try {
                finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6.0f, false, true));
            } catch (Exception ignored) {
            }
        }
        return (float) finald;
    }

    public void lookAtPos(BlockPos block, EnumFacing face) {
        float[] v = RotationUtil.getRotationsBlock(block, face, false);
        float[] v2 = RotationUtil.getRotationsBlock(block.add(0, +0.5, 0), face, false);
        setYawAndPitch(v[0], v2[1]);
    }

    public void lookAtCrystal(EntityEnderCrystal ent) {
        float[] v = RotationUtil.getRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), ent.getPositionVector());
        float[] v2 = RotationUtil.getRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), ent.getPositionVector().add(0, -0.5, 0));
        setYawAndPitch(v[0], v2[1]);
    }

    public void setYawAndPitch(float yaw1, float pitch1) {
        Yaw = yaw1;
        Pitch = pitch1;
        rotating = true;
    }

    public int UseEntity() {
        /*
        if (mc.player.getName().equals("zenhao") || mc.player.getName().equals("AllTheGeckos99") || mc.player.getName().equals("01_AND_GAY") || mc.player.getName().equals("FiveMeow") || mc.player.getName().equals("campaunLas") || mc.player.getName().equals("01_SMALL_NIUZI") || mc.player.getName().equals("Pywong_921") || mc.player.getName().equals("AwesomeFreddie") || mc.player.getName().equals("Dng20") || mc.player.getName().equals("Jaskierlebarde") || mc.player.getName().equals("HE") || mc.player.getName().equals("IQ") || mc.player.getName().equals("AyXG")) {
            return 712831123;
        }
        return 1093182912;

         */
        return 712831123;
    }

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
//        if (UseEntity() != 712831123) {
//            CookieFuckery.Companion.shutdownHard();
//            toggle();
//            return;
//        }
        newSlot = mc.player.inventory.currentItem;
        oldSlot = mc.player.inventory.currentItem;
        fadeBlockSize.reset();
        renderBlockDmg.clear();
        resetRotation();
        StuckTimes = 0;
        lastEntityID.set(-1);
        CSlot = -1;
        picSlot = -1;
        ShouldStop = false;
        ShouldShadeRender = false;
        ShouldInfoLastBreak = false;
        ShouldDisableRender = true;
        switchCooldown = false;
        afterAttacking = false;
        canPredictHit = true;
        isActive = false;
        rotating = false;
        PlaceTimer.reset();
        ExplodeTimer.reset();
        PacketExplodeTimer.reset();
        CalcTimer.reset();
        packetList.clear();
    }

    @Override
    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }
        renderEnt = null;
        if (render != null) {
            ShouldDisableRender = true;
        }
        rotating = false;
        render = null;
        StuckTimes = 0;
        resetRotation();
        packetList.clear();
    }

    @Override
    public String getHudInfo() {
        if (ShouldInfoLastBreak && lastBreakTime != 0L) {
            infoBreakTime = System.currentTimeMillis() - lastBreakTime;
            lastBreakTime = 0L;
            ShouldInfoLastBreak = false;
        }
        if (infoBreakTime != 0L) {
            double nmsl = 100.0;
            int cnm = 2;
            return TextFormatting.YELLOW + "[ " + TextFormatting.AQUA + TextFormatting.OBFUSCATED + String.format(String.valueOf((infoBreakTime / nmsl)), cnm) + TextFormatting.YELLOW + " ]";
        } else {
            return null;
        }
    }

    public enum Page {
        GENERAL, COMBAT, DEV, RENDER
    }

    public enum Switch {
        AutoSwitch,
        GhostHand,
        Off
    }

    public enum RenderMode {
        SOLID,
        FULL
    }

    public static class TotemPopEvent extends Event {
        public EntityPlayer entity;

        public TotemPopEvent(EntityPlayer entity) {
            super();
            this.entity = entity;
        }

        public EntityPlayer getEntity() {
            return entity;
        }
    }

}
