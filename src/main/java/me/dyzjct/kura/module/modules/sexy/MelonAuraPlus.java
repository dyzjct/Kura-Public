package me.dyzjct.kura.module.modules.sexy;

import me.dyzjct.kura.event.events.client.PacketEvents;
import me.dyzjct.kura.event.events.entity.MotionUpdateEvent;
import me.dyzjct.kura.event.events.render.RenderEvent;
import me.dyzjct.kura.gui.clickgui.font.FontRenderer;
import me.dyzjct.kura.manager.FriendManager;
import me.dyzjct.kura.manager.GuiManager;
import me.dyzjct.kura.manager.HotbarManager;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.module.ModuleManager;
import me.dyzjct.kura.module.modules.chat.AutoGG;
import me.dyzjct.kura.module.modules.combat.AutoEXP;
import me.dyzjct.kura.module.modules.xddd.CrystalDamageCalculator;
import me.dyzjct.kura.module.modules.xddd.CrystalHelper;
import me.dyzjct.kura.module.modules.xddd.FastRayTrace;
import me.dyzjct.kura.setting.*;
import me.dyzjct.kura.utils.NTMiku.TimerUtils;
import me.dyzjct.kura.utils.animations.BlockEasingRender;
import me.dyzjct.kura.utils.block.BlockInteractionHelper;
import me.dyzjct.kura.utils.entity.CrystalUtil;
import me.dyzjct.kura.utils.entity.EntityUtil;
import me.dyzjct.kura.utils.font.CFontRenderer;
import me.dyzjct.kura.utils.gl.MelonTessellator;
import me.dyzjct.kura.utils.gl.XG42Tessellator;
import me.dyzjct.kura.utils.inventory.InventoryUtil;
import me.dyzjct.kura.utils.mc.ChatUtil;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by zenhao on 16/02/2022.
 * Updated by dyzjct on 07/12/2022.
 */
@Module.Info(name = "AutoCrystal++", category = Category.XDDD)
public class MelonAuraPlus extends Module {
    public static ConcurrentHashMap<BlockPos, Double> renderBlockDmg = new ConcurrentHashMap<>();
    public static MelonAuraPlus INSTANCE = new MelonAuraPlus();
    public static EntityLivingBase renderEnt;
    public ModeSetting<?> p = msetting("Page", Page.GENERAL);
    //Page GENERAL
    public ModeSetting<Switch> switchmode = msetting("SwitchMode", Switch.GhostHand).m(p, Page.GENERAL);
    public ModeSetting<AntiWeaknessMode> antiWeakness = msetting("AntiWeakness", AntiWeaknessMode.Spoof).m(p, Page.GENERAL);
    public ModeSetting<SwingMode> swingMode = msetting("Swing", SwingMode.Auto).m(p, Page.GENERAL);
    public BooleanSetting rotate = bsetting("Rotate", false).m(p, Page.GENERAL);
    public BooleanSetting yawStep = bsetting("YawStep", false).b(rotate).m(p, Page.GENERAL);
    public FloatSetting yawAngle = fsetting("YawAngle", 0.1f, 0.1f, 0.5f).b(rotate).b(yawStep).m(p, Page.GENERAL);
    public IntegerSetting yawTicks = isetting("YawTicks", 1, 1, 5).b(rotate).b(yawStep).m(p, Page.GENERAL);
    //Page Place
    public ModeSetting<PacketPlaceMode> packetPlaceMode = msetting("PacketMode", PacketPlaceMode.Off).m(p, Page.PLACE);
    public BooleanSetting endcrystal = bsetting("1.13Place", false).m(p, Page.PLACE);
    public BooleanSetting placeSwing = bsetting("PlaceSwing", false).m(p, Page.PLACE);
    public IntegerSetting PlaceSpeed = isetting("PlaceSpeed", 34, 1, 40).m(p, Page.PLACE);
    public IntegerSetting placeRange = isetting("PlaceRange", 6, 0, 6).m(p, Page.PLACE);
    public IntegerSetting minDamage = isetting("PlaceMinDmg", 4, 0, 36).m(p, Page.PLACE);
    public IntegerSetting PlaceMaxSelf = isetting("PlaceMaxSelfDmg", 10, 0, 36).m(p, Page.PLACE);
    //Page Break
    public BooleanSetting packetExplode = bsetting("PacketExplode", true).m(p, Page.BREAK);
    public IntegerSetting packetDelay = isetting("PacketDelay", 45, 0, 500).b(packetExplode).m(p, Page.BREAK);
    public IntegerSetting HitDelay = isetting("HitDelay", 50, 0, 500).m(p, Page.BREAK);
    public IntegerSetting PredictHitFactor = isetting("PredictHitFactor", 0, 0, 20).m(p, Page.BREAK);
    public IntegerSetting breakRange = isetting("BreakRange", 6, 0, 6).m(p, Page.BREAK);
    public IntegerSetting breakMinDmg = isetting("BreakMinDmg", 2, 0, 36).m(p, Page.BREAK);
    public IntegerSetting breakMaxSelf = isetting("BreakMaxSelf", 12, 0, 36).m(p, Page.BREAK);
    //Page Calculation
    public BooleanSetting MotionPredict = bsetting("MotionPredict", true).m(p, Page.CALCULATION);
    public IntegerSetting predictTicks = isetting("PredictTicks", 8, 1, 20).b(MotionPredict).m(p, Page.CALCULATION);
    public BooleanSetting debug = bsetting("Debug", false).m(p, Page.CALCULATION);
    public IntegerSetting enemyRange = isetting("EnemyRange", 8, 1, 10).m(p, Page.CALCULATION);
    public FloatSetting noSuicide = fsetting("NoSuicide", 2, 0, 20).m(p, Page.CALCULATION);
    public FloatSetting wallRange = fsetting("WallRange", 3f, 0f, 8f).m(p, Page.CALCULATION);
    //Page Force
    public BooleanSetting slowFP = bsetting("SlowFacePlace", false).m(p, Page.FORCE);
    public IntegerSetting fpDelay = isetting("FacePlaceDelay", 275, 1, 750).b(slowFP).m(p, Page.FORCE);
    public IntegerSetting forceHealth = isetting("ForceHealth", 2, 0, 20).m(p, Page.FORCE);
    public FloatSetting forcePlaceMotion = fsetting("ForcePlaceMotion", 4f, 0.25f, 10f).m(p, Page.FORCE);
    public IntegerSetting armorRate = isetting("ForceArmor%", 25, 0, 100).m(p, Page.FORCE);
    public DoubleSetting forcePlaceDmg = dsetting("ForcePlaceDamage", 0.5f, 0.1, 10).m(p, Page.FORCE);
    public BooleanSetting forcePop = bsetting("ForcePop", false).m(p, Page.FORCE);
    //Page Lethal
    public BooleanSetting lethalOverride = bsetting("LethalOverride", true).m(p, Page.LETHAL);
    public FloatSetting lethalBalance = fsetting("LethalBalance", 0.5f, -5f, 5f).b(lethalOverride).m(p, Page.LETHAL);
    public FloatSetting lethalMaxDamage = fsetting("LethalMaxDamage", 16f, 0, 20f).b(lethalOverride).m(p, Page.LETHAL);
    //Page Render
    public BooleanSetting targetHUD = bsetting("TargetHUD", false).m(p, Page.RENDER);
    public ModeSetting hudinfomod = msetting("HudInfo",Mode.Target).m(p,Page.RENDER);
    public BooleanSetting outline = bsetting("Outline", true).m(p, Page.RENDER);
    public BooleanSetting renderDamage = bsetting("RenderDamage", true).m(p, Page.RENDER);
    public ColorSetting textcolor = csetting("TextColor", new Color(255, 225, 255)).m(p, Page.RENDER).b(renderDamage);
    public IntegerSetting textscalex = isetting("TextScaleX",1,0,5).b(renderDamage);
    public IntegerSetting textscaley = isetting("TextScaleY",1,0,5).b(renderDamage);
    public IntegerSetting textscalez = isetting("TextScaleZ",1,0,5).b(renderDamage);
    public BooleanSetting renderBreak = bsetting("RenderBreak", true).m(p, Page.RENDER);
    public BooleanSetting XG42OutLineMod = bsetting("XG42OutLineMod", true).m(p, Page.RENDER);
    public ColorSetting color = csetting("Color", new Color(20, 225, 219)).m(p, Page.RENDER);
    public IntegerSetting alpha = isetting("Alpha", 70, 0, 255).m(p, Page.RENDER);
    public IntegerSetting breakalpha = isetting("BreakAlpha", 70, 0, 255).m(p, Page.RENDER);
    public BooleanSetting rainbow = bsetting("Rainbow", false).m(p, Page.RENDER);
    public FloatSetting RGBSpeed = fsetting("RGBSpeed", 8, 0, 255).b(rainbow).m(p, Page.RENDER);
    public FloatSetting Saturation = fsetting("Saturation", 0.5f, 0, 1).b(rainbow).m(p, Page.RENDER);
    public FloatSetting Brightness = fsetting("Brightness", 1f, 0, 1).b(rainbow).m(p, Page.RENDER);
    public FloatSetting OutLineWidth = fsetting("OutLineWidth", 1f, 0, 5f).m(p,Page.RENDER);
    public FloatSetting movingLength = fsetting("MovingLength", 350, 1, 1000).m(p, Page.RENDER);
    public ModeSetting<?> renderMode = msetting("RenderMode", RenderModes.Glide).m(p, Page.RENDER);
    public BlockEasingRender blockRenderSmooth = new BlockEasingRender(new BlockPos(0, 0, 0), 650L, 400L);
    public transient AtomicInteger lastEntityID = new AtomicInteger(-1);
    public TimerUtils packetExplodeTimerUtils = new TimerUtils();
    public TimerUtils explodeTimerUtils = new TimerUtils();
    public TimerUtils placeTimerUtils = new TimerUtils();
    public TimerUtils calcTimerUtils = new TimerUtils();
    public TimerUtils FPDelay = new TimerUtils();
    public EntityEnderCrystal lastCrystal;
    public CrystalTarget crystalTarget;
    public Vec3d PredictionTarget;
    public BlockPos tempSpawnPos;
    public BlockPos render;
    public boolean ShouldShadeRender = false;
    public boolean switchCooldown = false;
    public boolean canPredictHit = false;
    public boolean isFacePlacing = false;
    public float[] Rotations;
    public int pitchTicksPassed;
    public int yawTicksPassed;
    public int newSlot = -1;
    public int CSlot = -1;
    public int DamageCA;
    public int PopTicks;
    private String breaked="NULL";
    private int breaktime=0;

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (PredictHitFactor.getValue() != 0) {
            toggle();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacketReceive(PacketEvents.Receive event) {
        if (fullNullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketSpawnObject) {
            SPacketSpawnObject packet = event.getPacket();
            if (PredictHitFactor.getValue() != 0) {
                new ArrayList<>(mc.world.loadedEntityList).forEach(e -> {
                    if (e instanceof EntityItem || e instanceof EntityArrow || e instanceof EntityEnderPearl || e instanceof EntitySnowball || e instanceof EntityEgg) {
                        if (e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6) {
                            lastEntityID.set(-1);
                            canPredictHit = false;
                            event.setCanceled(true);
                        }
                    }
                });
            }
            if (packet.getType() == 51 && !event.isCanceled()) {
                lastEntityID.getAndUpdate(it -> Math.max(it, packet.getEntityID()));
                if (packetExplode.getValue() && packetExplodeTimerUtils.passed(packetDelay.getValue())) {
                    PacketExplode(packet.getEntityID());
                    packetExplodeTimerUtils.reset();
                }
            } else {
                lastEntityID.set(-1);
            }
        } else if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet5 = event.getPacket();
            if (packet5.getSound().equals(SoundEvents.ENTITY_EXPERIENCE_BOTTLE_THROW) || packet5.getSound().equals(SoundEvents.ENTITY_ARROW_SHOOT) || packet5.getSound().equals(SoundEvents.ENTITY_ITEM_BREAK)) {
                canPredictHit = false;
            }
            if (packet5.getCategory() == SoundCategory.BLOCKS && packet5.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE && render != null) {
                new ArrayList<>(mc.world.loadedEntityList).forEach(e -> {
                    if (e instanceof EntityEnderCrystal) {
                        if (e.getDistance(packet5.getX(), packet5.getY(), packet5.getZ()) <= 6.0f) {
                            tempSpawnPos = new BlockPos(e);
                            e.setDead();
                            if (CrystalHelper.placeBoxIntersectsCrystalBox(tempSpawnPos, tempSpawnPos)) {
                                if (packetPlaceMode.getValue().equals(PacketPlaceMode.Weak) || packetPlaceMode.getValue().equals(PacketPlaceMode.Strong) && render != null && tempSpawnPos.down().equals(render)) {
                                    Place(MotionUpdateEvent.Tick.INSTANCETick, tempSpawnPos.down());
                                    if (debug.getValue()) {
                                        ChatUtil.sendMessage("ForcePlacing!");
                                    }
                                }
                            }
                        }
                    }
                });
            }
        } else if (event.getPacket() instanceof SPacketSpawnExperienceOrb || event.getPacket() instanceof SPacketSpawnPainting) {
            lastEntityID.set(-1);
            canPredictHit = false;
        }
    }

    @SubscribeEvent
    public void onCrystal(MotionUpdateEvent.Tick event) {
        if (fullNullCheck()) {
            return;
        }
        newSlot = mc.player.inventory.currentItem;
        CSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL);
        --yawTicksPassed;
        --pitchTicksPassed;
        crystalTarget = Calc();
        if (yawStep.getValue()) {
            Place(event, null);
            Explode(event);
        } else {
            Explode(event);
            Place(event, null);
        }
    }

    public void Explode(MotionUpdateEvent.Tick event) {
        EntityEnderCrystal crystal = new ArrayList<>(mc.world.loadedEntityList).stream().filter(e -> e instanceof EntityEnderCrystal &&
                canHitCrystal(e.getPositionVector()) && CrystalHelper.checkBreakRange((EntityEnderCrystal) e, breakRange.getValue(), wallRange.getValue(), 20, new BlockPos.MutableBlockPos())).map(e -> (EntityEnderCrystal) e).min(Comparator.comparing(e -> mc.player.getDistance(e))).orElse(null);
        if (mc.player != null && crystal != null && renderEnt != null) {
            if (mc.player.getDistance(crystal) <= breakRange.getValue()) {
                lastCrystal = crystal;
                if (!antiWeakness.getValue().equals(AntiWeaknessMode.Off) && mc.player.isPotionActive(MobEffects.WEAKNESS) && (!mc.player.isPotionActive(MobEffects.STRENGTH) || Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() < 1)) {
                    HotbarManager.spoofHotbar(InventoryUtil.findHotbarItem(Items.DIAMOND_SWORD) != -1 ? InventoryUtil.findHotbarItem(Items.DIAMOND_SWORD) : InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE), true);
                }
                ExplodeCrystal(event);
                if (antiWeakness.getValue().equals(AntiWeaknessMode.Spoof) && mc.player.isPotionActive(MobEffects.WEAKNESS) && (!mc.player.isPotionActive(MobEffects.STRENGTH) || Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() < 1)) {
                    HotbarManager.spoofHotbar(newSlot, true);
                }
            }
        }
    }

    public void Place(MotionUpdateEvent.Tick event, BlockPos pos) {
        try {
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
            if (pos != null && pos != render) {
                render = pos;
            }
            if (crystalTarget != null) {
                renderEnt = crystalTarget.getTarget();
                render = crystalTarget.getBlockPos();
            }
            if (renderEnt == null || render == null) {
                ShouldShadeRender = true;
                blockRenderSmooth.end();
                renderEnt = null;
                render = null;
                renderBlockDmg.clear();
                return;
            }
            if (ShouldShadeRender) {
                blockRenderSmooth.resetFade();
                ShouldShadeRender = false;
            }
            if (render != null) {
                if (switchmode.getValue() == Switch.GhostHand && mc.getConnection() != null && CSlot != -1) {
                    HotbarManager.spoofHotbar(CSlot, true);
                }
                if (rotate.getValue()) {
                    Rotations = BlockInteractionHelper.getLegitRotations(new Vec3d(render).add(0.5, 1, 0.5));
                    //Rotations = AimUtil.getNeededFacing(new Vec3d(render).add(0.5, 0, 0.5), render.getY() < mc.player.posY);
                    if (yawStep.getValue()) {
                        if (this.yawTicksPassed > 0) {
                            this.Rotations[0] = mc.player.lastReportedYaw;
                        } else {
                            float f = MathHelper.wrapDegrees(this.Rotations[0] - mc.player.lastReportedYaw);
                            if (Math.abs(f) > 180.0f * yawAngle.getValue()) {
                                this.Rotations[0] = mc.player.lastReportedYaw + f * (180.0f * yawAngle.getValue() / Math.abs(f));
                                this.yawTicksPassed = yawTicks.getValue();
                            }
                        }
                        if (this.pitchTicksPassed > 0) {
                            this.Rotations[1] = mc.player.lastReportedPitch;
                        } else {
                            float f2 = MathHelper.wrapDegrees(this.Rotations[1] - mc.player.lastReportedPitch);
                            if (Math.abs(f2) > 90.0f * yawAngle.getValue()) {
                                this.Rotations[1] = mc.player.lastReportedPitch + f2 * (90.0f * yawAngle.getValue() / Math.abs(f2));
                                this.pitchTicksPassed = yawTicks.getValue();
                            }
                        }
                    }
                    if (event != null) {
                        event.setRotation(Rotations[0], Rotations[1]);
                    }
                }
                if (!offhand && mc.player.inventory.currentItem != crystalSlot) {
                    if (switchmode.getValue() == Switch.AutoSwitch) {
                        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemAppleGold && mc.player.isHandActive()) {
                            return;
                        }
                        mc.player.inventory.currentItem = crystalSlot;
                        switchCooldown = true;
                    }
                    return;
                }
                if (switchCooldown) {
                    switchCooldown = false;
                    return;
                }
                if (slowFP.getValue() && isFacePlacing && renderEnt != null) {
                    if (!FPDelay.passed(fpDelay.getValue())) {
                        if (switchmode.getValue() == Switch.GhostHand) {
                            HotbarManager.spoofHotbar(newSlot, true);
                        }
                        return;
                    }
                    FPDelay.reset();
                }
                if (mc.getConnection() != null) {
                    if (hasDelayRunPlace(PlaceSpeed.getValue())) {
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(render, EnumFacing.UP, mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 1f, 0.5f));
                        if (placeSwing.getValue()) {
                            swing();
                        }
                        blockRenderSmooth.updatePos(render);
                        if (PredictHitFactor.getValue() != 0 && renderEnt != null) {
                            try {
                                if (renderEnt.isDead || !canPredictHit || !canHitCrystal(lastCrystal.getPositionVector())) {
                                    if (switchmode.getValue() == Switch.GhostHand) {
                                        HotbarManager.spoofHotbar(newSlot, true);
                                    }
                                    placeTimerUtils.reset();
                                    return;
                                }
                                if (mc.player.getHealth() + mc.player.getAbsorptionAmount() > PlaceMaxSelf.getValue() && lastEntityID.get() != -1 && lastCrystal != null && canPredictHit) {
                                    int syncedId = lastEntityID.get();
                                    for (int spam = 0; spam < PredictHitFactor.getValue(); spam++) {
                                        if (syncedId != -1) {
                                            PacketExplode(syncedId + spam + 1);
                                        }
                                    }
                                }
                            } catch (Exception ignored) {
                            }
                        }
                        placeTimerUtils.reset();
                    }
                }
                if (switchmode.getValue() == Switch.GhostHand) {
                    HotbarManager.spoofHotbar(newSlot, true);
                }
            }
        } catch (Exception ignored) {
        }
    }

    public CrystalTarget Calc() {
        double damage = 0.5;
        double selfDamage = 0;
        EntityLivingBase target = null;
        BlockPos tempBlock = null;
        int totemCount = mc.player.inventory.mainInventory.stream().filter(t -> t.getItem().equals(Items.TOTEM_OF_UNDYING)).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            totemCount += mc.player.getHeldItemOffhand().stackSize;
        }
        for (EntityLivingBase entity2 : new ArrayList<>(getEntities())) {
            if (entity2 != mc.player) {
                PredictionTarget = entity2 instanceof EntityPlayer && MotionPredict.getValue() ? CrystalHelper.PredictionHandlerNew(entity2, predictTicks.getValue()) : new Vec3d(0, 0, 0);
                if (entity2.getHealth() <= 0.0f || entity2.isDead) continue;
                canPredictHit = (!entity2.getHeldItemMainhand().getItem().equals(Items.EXPERIENCE_BOTTLE)) && !entity2.getHeldItemOffhand().getItem().equals(Items.EXPERIENCE_BOTTLE) || ModuleManager.getModuleByClass(AutoEXP.class).isDisabled();
                for (BlockPos blockPos : new ArrayList<>(rendertions(placeRange.getValue()))) {
                    if (entity2.getDistanceSq(blockPos) >= enemyRange.getValue() * enemyRange.getValue()) continue;
                    if (mc.player.getDistance(blockPos.x, blockPos.y, blockPos.z) > placeRange.getValue()) continue;
                    double d = CrystalDamageCalculator.Companion.calcDamage(entity2, entity2.getPositionVector().add(PredictionTarget), entity2.getEntityBoundingBox(), blockPos.x + 0.5, blockPos.y + 1, blockPos.z + 0.5, new BlockPos.MutableBlockPos());
                    DamageCA = (int) d;
                    if (d < damage) continue;
                    isFacePlacing = EntityUtil.isInHole(entity2); //HoleUtil.is2HoleB(entity2.getPosition()) || HoleUtil.isHole(entity2.getPosition());
                    if (d < (CrystalHelper.shouldForcePlace(entity2, forceHealth.getValue(), armorRate.getValue(), forcePlaceMotion.getValue()) ? forcePlaceDmg.getValue() : minDamage.getValue()))
                        continue;
                    float healthTarget = entity2.getHealth() + entity2.getAbsorptionAmount();
                    double self = CrystalDamageCalculator.Companion.calcDamage(mc.player, mc.player.getPositionVector(), mc.player.getEntityBoundingBox(), blockPos.x + 0.5, blockPos.y + 1, blockPos.z + 0.5, new BlockPos.MutableBlockPos());
                    selfDamage = self;
                    if (self > d && d < healthTarget) continue;
                    //if (self - noSuicide.getValue() > healthSelf) continue;
                    if (forcePop.getValue() && totemCount > 1) {
                        if (entity2.getHealth() <= d && entity2.getDistance(mc.player) <= 1.75 && (mc.player.getHeldItemOffhand().getItem().equals(Items.TOTEM_OF_UNDYING) || mc.player.getHeldItemMainhand().getItem().equals(Items.TOTEM_OF_UNDYING))) {
                            if (mc.player.getPosition().getDistance(blockPos.up().getX(), blockPos.up().getY(), blockPos.up().getZ()) < 3) {
                                ChatUtil.NoSpam.sendMessage("ForcePopping");
                                if (renderDamage.getValue()) renderBlockDmg.put(blockPos, d);
                                return new CrystalTarget(blockPos, entity2, self, d);
                            }
                        }
                    }
                    if (CrystalHelper.getScaledHealth(mc.player) - self <= noSuicide.getValue()) continue;
                    //if (!lethalOverride.getValue() && self > PlaceMaxSelf.getValue()) continue;
                    if (self > PlaceMaxSelf.getValue()) continue;
                    if (mc.player.getPositionVector().squareDistanceTo(new Vec3d(blockPos.add(0.5, 1, 0.5))) > wallRange.getValue() && !FastRayTrace.Companion.rayTraceVisible(mc.player.getPositionVector().add(0, mc.player.getEyeHeight(), 0), blockPos.getX() + 0.5, (blockPos.getY() + 1f) + 1.7f, blockPos.getZ() + 0.5, 20, new BlockPos.MutableBlockPos()))
                        continue;
                    if (entity2.getEntityBoundingBox().intersects(CrystalHelper.getCrystalPlacingBB(blockPos)))
                        continue;
                    if (CrystalHelper.getCrystalPlacingBB(blockPos).intersects(entity2.getPositionVector().add(PredictionTarget), new Vec3d(blockPos)))
                        continue;
                    if (lethalOverride.getValue() && d - CrystalHelper.getTotalHealth(mc.player) > lethalBalance.getValue() && self <= lethalMaxDamage.getValue()) {
                        if (crystalTarget != null) {
                            if (self < crystalTarget.getSelfDamage()) {
                                //crystalTarget.update(blockPos, entity2, selfDamage, d);
                                if (ModuleManager.getModuleByClass(AutoGG.class).isEnabled()) {
                                    ((AutoGG) ModuleManager.getModuleByClass(AutoGG.class)).addTargetedPlayer(entity2.getName());
                                }
                                if (renderDamage.getValue()) renderBlockDmg.put(blockPos, d);
                                return new CrystalTarget(blockPos, entity2, self, d);
                            }
                        }
                    }
                    damage = d;
                    tempBlock = blockPos;
                    target = entity2;
                    if (ModuleManager.getModuleByClass(AutoGG.class).isEnabled()) {
                        ((AutoGG) ModuleManager.getModuleByClass(AutoGG.class)).addTargetedPlayer(target.getName());
                    }
                    if (renderDamage.getValue()) renderBlockDmg.put(tempBlock, damage);
                }
                if (target != null) {
                    break;
                }
            }
        }
        return new CrystalTarget(tempBlock, target, selfDamage, damage);
    }

    public void swing() {
        if (fullNullCheck()) {
            return;
        }
        switch (swingMode.getValue()) {
            case Off: {
                break;
            }
            case Offhand: {
                mc.player.swingArm(EnumHand.OFF_HAND);
                break;
            }
            case Mainhand: {
                mc.player.swingArm(EnumHand.MAIN_HAND);
                break;
            }
            case Auto: {
                mc.player.swingArm(mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                break;
            }
        }
    }

    public boolean hasDelayRunPlace(double placeSpeed) {
        return placeTimerUtils.passed(1000 / placeSpeed);
    }

    public List<EntityLivingBase> getEntities() {
        List<EntityLivingBase> entities = new ArrayList<>(mc.world.playerEntities).stream()
                .filter(entityPlayer -> !FriendManager.isFriend(entityPlayer.getName()))
                .filter(entity -> mc.player.getDistance(entity) < enemyRange.getValue())
                .collect(Collectors.toList());
        for (EntityLivingBase ite2 : new ArrayList<>(entities)) {
            if (mc.player.getDistance(ite2) > enemyRange.getValue()) entities.remove(ite2);
            if (ite2 == mc.player) entities.remove(ite2);
        }
        entities.sort(Comparator.comparing(e -> mc.player.getDistance(e)));
        return entities;
    }

    public void ExplodeCrystal(MotionUpdateEvent.Tick event) {
        EntityEnderCrystal crystal = new ArrayList<>(mc.world.loadedEntityList).stream().filter(e -> e instanceof EntityEnderCrystal &&
                canHitCrystal(e.getPositionVector()) && CrystalHelper.checkBreakRange((EntityEnderCrystal) e, breakRange.getValue(), wallRange.getValue(), 20, new BlockPos.MutableBlockPos())).map(e -> (EntityEnderCrystal) e).min(Comparator.comparing(e -> mc.player.getDistance(e))).orElse(null);
        if (crystal != null) {
            if (explodeTimerUtils.passed(HitDelay.getValue()) && mc.getConnection() != null) {
                PacketExplode(crystal.getEntityId());
                swing();
                if (packetPlaceMode.getValue().equals(PacketPlaceMode.Strong)) {
                    if (CrystalHelper.placeBoxIntersectsCrystalBox(new BlockPos(crystal), new BlockPos(crystal))) {
                        Place(event, new BlockPos(crystal).down());
                    }
                }
                explodeTimerUtils.reset();
            }
        }
    }

    public void PacketExplode(int i) {
        try {
            if (mc.player.getDistance(lastCrystal) <= breakRange.getValue() && canHitCrystal(lastCrystal.getPositionVector())) {
                CPacketUseEntity wdnmd = new CPacketUseEntity(lastCrystal);
                wdnmd.entityId = i;
                wdnmd.action = CPacketUseEntity.Action.ATTACK;
                mc.player.connection.sendPacket(wdnmd);
            }
        } catch (Exception ignored) {
        }
    }

    public boolean canHitCrystal(Vec3d crystal) {
        this.breaktime++;
        if (breaktime==3){
            this.breaked = "BREAK";
            breaktime=0;
        }
//
        float selfDamage = CrystalDamageCalculator.Companion.calcDamage(mc.player, mc.player.getPositionVector(), mc.player.getEntityBoundingBox(), crystal.x, crystal.y, crystal.z, new BlockPos.MutableBlockPos());
        float healthSelf = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        if (selfDamage >= healthSelf) return false;
        for (EntityLivingBase player : new ArrayList<>(getEntities())) {
            if (player instanceof EntityPlayer) {
                if (mc.player.isDead || healthSelf <= 0.0f) continue;
                double minDamage = breakMinDmg.getValue();
                double maxSelf = breakMaxSelf.getValue();
                if (CrystalHelper.shouldForcePlace(player, forceHealth.getValue(), armorRate.getValue(), forcePlaceMotion.getValue())) {
                    minDamage = 1;
                }
                double target = CrystalDamageCalculator.Companion.calcDamage(player, player.getPositionVector(), player.getEntityBoundingBox(), crystal.x, crystal.y, crystal.z, new BlockPos.MutableBlockPos());
                if (target > player.getHealth() + player.getAbsorptionAmount() && selfDamage < healthSelf) {
                    return true;
                }
                if (selfDamage > maxSelf) continue;
                if (target < minDamage) continue;
                if (selfDamage > target) continue;
                return true;
            }
        }
        return false;

    }

    public List<BlockPos> rendertions(double range) {
        NonNullList<BlockPos> Positions = NonNullList.create();
        Positions.addAll(CrystalUtil.getSphere(EntityUtil.getPlayerPos(), range, range, false, true, 0)
                .stream()
                .filter(v -> canPlaceCrystal(v, endcrystal.getValue())).collect(Collectors.toList()));
        return Positions;
    }

    public boolean canPlaceCrystal(BlockPos blockPos, boolean newPlace) {
        this.breaked="PLACE";
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);

        Block base = mc.world.getBlockState(blockPos).getBlock();
        Block b1 = mc.world.getBlockState(boost).getBlock();
        Block b2 = mc.world.getBlockState(boost2).getBlock();

        if (base != Blocks.BEDROCK && base != Blocks.OBSIDIAN) return false;

        if (b1 != Blocks.AIR && !CrystalHelper.isReplaceable(b1)) return false;
        if (!newPlace && b2 != Blocks.AIR) return false;

        AxisAlignedBB box = new AxisAlignedBB(blockPos.getX(), blockPos.getY() + 1.0, blockPos.getZ(), blockPos.getX() + 1.0, blockPos.getY() + 3.0, blockPos.getZ() + 1.0);

        for (Entity entity : new ArrayList<>(mc.world.loadedEntityList)) {
            if (entity instanceof EntityEnderCrystal) continue;
            if (entity.getEntityBoundingBox().intersects(box)) return false;
        }
        return true;
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (fullNullCheck()) {
            return;
        }
        int hsBtoRGB = Color.HSBtoRGB((new float[]{
                System.currentTimeMillis() % 11520L / 11520.0f * RGBSpeed.getValue()
        })[0], Saturation.getValue(), Brightness.getValue());
        int r = (hsBtoRGB >> 16 & 0xFF);
        int g = (hsBtoRGB >> 8 & 0xFF);
        int b = (hsBtoRGB & 0xFF);
        Color c = new Color((rainbow.getValue()) ? r : (color.getValue().getRed()), (rainbow.getValue()) ? g : (color.getValue().getGreen()), (rainbow.getValue()) ? b : (color.getValue().getBlue()));
        CFontRenderer fonts = GuiManager.getINSTANCE().getFont();
        try {
            if (render != null) {
                blockRenderSmooth.begin();
                if (renderMode.getValue().equals(RenderModes.Glide)){
                    if (!XG42OutLineMod.getValue()){
                        MelonTessellator.drawBBBox(blockRenderSmooth.getFullUpdate(), c, alpha.getValue(), OutLineWidth.getValue(), outline.getValue());
                    }else {
                        XG42Tessellator.drawBBBox(blockRenderSmooth.getFullUpdate(), c, alpha.getValue(), OutLineWidth.getValue(), outline.getValue());
                    }
                }
                if (renderMode.getValue().equals(RenderModes.Normal)&&!renderBreak.getValue()){
                    XG42Tessellator.prepare(GL11.GL_QUADS);
                    XG42Tessellator.drawFullBox(render, OutLineWidth.getValue(), (rainbow.getValue()) ? r : (this.color.getValue().getRed()), (rainbow.getValue()) ? g : (this.color.getValue().getGreen()), (rainbow.getValue()) ? b : (this.color.getValue().getBlue()), alpha.getValue());
                    XG42Tessellator.release();
                }
//                if (renderMode.getValue().equals(RenderModes.Normal)&&renderBreak.getValue()){
//                    if (breaktime==1){
//                        XG42Tessellator.prepare(GL11.GL_QUADS);
//                        XG42Tessellator.drawFullBox(render, OutLineWidth.getValue(), (rainbow.getValue()) ? r : (this.color.getValue().getRed()), (rainbow.getValue()) ? g : (this.color.getValue().getGreen()), (rainbow.getValue()) ? b : (this.color.getValue().getBlue()), breakalpha.getValue());
//                        XG42Tessellator.release();
//                        this.breaktime++;
//                        if (this.breaktime == 3) {
//                            this.breaktime = 0;
//                        }
//                    }else{
//                        XG42Tessellator.prepare(GL11.GL_QUADS);
//                        XG42Tessellator.drawFullBox(render, OutLineWidth.getValue(), (rainbow.getValue()) ? r : (this.color.getValue().getRed()), (rainbow.getValue()) ? g : (this.color.getValue().getGreen()), (rainbow.getValue()) ? b : (this.color.getValue().getBlue()), alpha.getValue());
//                        XG42Tessellator.release();
//                    }
//                }
                if (renderMode.getValue().equals(RenderModes.Normal)&&renderBreak.getValue()){
                    if (fullNullCheck()){
                        return;
                    }
                    if (breaked == "PLACE"||breaked=="NULL"){
                        XG42Tessellator.prepare(GL11.GL_QUADS);
                        XG42Tessellator.drawFullBox(render, OutLineWidth.getValue(), (rainbow.getValue()) ? r : (this.color.getValue().getRed()), (rainbow.getValue()) ? g : (this.color.getValue().getGreen()), (rainbow.getValue()) ? b : (this.color.getValue().getBlue()), alpha.getValue());
                        XG42Tessellator.release();
                    }else if (breaked == "BREAK"){
                        XG42Tessellator.prepare(GL11.GL_QUADS);
                        XG42Tessellator.drawFullBox(render, OutLineWidth.getValue(), (rainbow.getValue()) ? r : (this.color.getValue().getRed()), (rainbow.getValue()) ? g : (this.color.getValue().getGreen()), (rainbow.getValue()) ? b : (this.color.getValue().getBlue()), breakalpha.getValue());
                        XG42Tessellator.release();
                        this.breaked="NULL";
                    }
                }
                if (renderDamage.getValue()) {
                    if (renderBlockDmg.containsKey(render) && renderMode.getValue().equals(RenderModes.Glide)){
                        if (renderBlockDmg.containsKey(render)) {
                            GlStateManager.pushMatrix();
                            AxisAlignedBB blockPos = blockRenderSmooth.getFullUpdate();
                            if (blockPos != null) {
                                MelonTessellator.glBillboardDistanceScaled((float) blockPos.getCenter().x, (float) blockPos.getCenter().y, (float) blockPos.getCenter().z, mc.player, 0.5f);
                            }
                            final double damage = renderBlockDmg.get(render);
                            final String damageText = (Math.floor(damage) == damage ? damage : String.format("%.1f", damage)) + "";
                            GlStateManager.disableDepth();
                            GlStateManager.translate(-(fonts.getStringWidth(damageText) / 2.0d), 0, 0);
                            GlStateManager.scale(textscalex.getValue(), textscaley.getValue(), textscalez.getValue());
                            fontRenderer.drawString(damageText, 0, 0, new Color(textcolor.getValue().getRed(), textcolor.getValue().getGreen(), textcolor.getValue().getBlue()).getRGB());
                            GlStateManager.popMatrix();
                        }
                    }
                    else {
                        GlStateManager.pushMatrix();
                        XG42Tessellator.glBillboardDistanceScaled((float) render.getX() + 0.5f, (float) render.getY() + 0.5f, (float) render.getZ() + 0.5f, mc.player, 1);
                        final float damage = CrystalUtil.calculateDamage(render.getX() + 0.5, render.getY() + 1, render.getZ() + 0.5, renderEnt);
                        final String damageText = (Math.floor(damage) == damage ? damage : String.format("%.1f", damage)) + "";
                        GlStateManager.disableDepth();
//                        GlStateManager.translate(-(mc.fontRenderer.getStringWidth(damageText) / 2.0d), 0, 0);
                        GlStateManager.translate(-(fonts.getStringWidth(damageText) / 2.0d), 0, 0);
                        //GlStateManager.scale(1, 1.5, 1);
//                        fonts.drawString(damageText, 0, 0, new Color(255, 215, 0).getRGB());
                        fontRenderer.drawString(damageText, 0, 0, new Color(textcolor.getValue().getRed(), textcolor.getValue().getGreen(), textcolor.getValue().getBlue()).getRGB());
                        GlStateManager.popMatrix();
                    }

                }
            } else {
                blockRenderSmooth.resetFade();
                blockRenderSmooth.end();
            }
            if (renderEnt != null && targetHUD.getValue()) {
                MelonTessellator.drawBBBox(new AxisAlignedBB(renderEnt.getEntityBoundingBox().minX, renderEnt.getEntityBoundingBox().minY, renderEnt.getEntityBoundingBox().minZ, renderEnt.getEntityBoundingBox().minX + (renderEnt.width / 1.5f), renderEnt.getEntityBoundingBox().minY + (renderEnt.height / 2f), renderEnt.getEntityBoundingBox().minZ + (renderEnt.width / 1.5f)), GuiManager.getINSTANCE().getColor(), alpha.getValue(), 1.0f, outline.getValue());
                //CrystalUtil.targetHUD(renderEnt, GuiManager.getINSTANCE().getRGB(), 1);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        blockRenderSmooth = new BlockEasingRender(new BlockPos(0, 0, 0), movingLength.getValue(), 350L);
        newSlot = mc.player.inventory.currentItem;
        CSlot = -1;
        blockRenderSmooth.resetFade();
        renderBlockDmg.clear();
        lastEntityID.set(-1);
        ShouldShadeRender = false;
        switchCooldown = false;
        isFacePlacing = false;
        canPredictHit = true;
        packetExplodeTimerUtils.reset();
        explodeTimerUtils.reset();
        placeTimerUtils.reset();
        calcTimerUtils.reset();
        FPDelay.reset();
        PopTicks = 0;
    }

    @Override
    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }
        blockRenderSmooth.resetFade();
        blockRenderSmooth.end();
        renderEnt = null;
        render = null;
    }

    @Override
    public String getHudInfo() {
        if (renderEnt != null && hudinfomod.getValue().equals(Mode.Target)) {
            return TextFormatting.AQUA + "" + renderEnt.getName() + "";
        }
        if (hudinfomod.getValue().equals(Mode.BreakPlace) && breaked != null){
            return TextFormatting.AQUA + "" + breaked + "";
        }
        return null;
    }

    public enum Page {
        GENERAL, CALCULATION, PLACE, BREAK, FORCE, LETHAL, RENDER
    }

    public enum PacketPlaceMode {
        Off,
        Weak,
        Strong
    }


    public enum Switch {
        AutoSwitch,
        GhostHand,
        Off
    }

    public enum AntiWeaknessMode {
        Swap,
        Spoof,
        Off
    }

    public enum SwingMode {
        Offhand,
        Mainhand,
        Auto,
        Off
    }

    public enum RenderModes {
        Glide , Normal
    }

    public enum Mode{
        Target , BreakPlace
    }
}