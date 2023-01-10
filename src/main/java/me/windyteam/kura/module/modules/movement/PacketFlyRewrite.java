package me.windyteam.kura.module.modules.movement;

import me.windyteam.kura.event.events.client.PacketEvents;
import me.windyteam.kura.event.events.entity.MotionUpdateEvent;
import me.windyteam.kura.event.events.entity.MoveEvent;
import me.windyteam.kura.event.events.entity.PushEvent;
import me.windyteam.kura.event.events.render.InsideBlockRenderEvent;
import me.windyteam.kura.event.events.render.RenderOverlayEvent;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.module.ModuleManager;
import me.windyteam.kura.setting.*;
import me.windyteam.kura.utils.MathUtil;
import me.windyteam.kura.utils.entity.EntityUtil;
import me.windyteam.kura.utils.math.RandomUtil;
import me.windyteam.kura.utils.mc.ChatUtil;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Module.Info(name = "PacketFlyRewrite", category = Category.MOVEMENT)
public class PacketFlyRewrite extends Module {
    public static PacketFlyRewrite INSTANCE = new PacketFlyRewrite();
    protected final Map<Integer, TimeVec> posLooks = new ConcurrentHashMap<>();
    public int teleportID;
    public ArrayList<CPacketPlayer> packets = new ArrayList<>();
    public ModeSetting<?> mode = msetting("Mode", Modes.Factor);
    public ModeSetting<PhaseMode> phase = msetting("PhaseMode", PhaseMode.Full);
    public Setting<Types> type = msetting("Type", Types.LimitJitter);
    public Setting<Boolean> TRStep = bsetting("ToggleRStep", false);
    public BooleanSetting betterResponse = bsetting("BetterResponse", false);
    public BooleanSetting confirmtp = bsetting("ConfirmTeleport", true);
    public Setting<Boolean> AntiKick = bsetting("AntiKick", true);
    public BooleanSetting lessReduction = bsetting("LessReduction", false);
    public Setting<Double> Reduction = dsetting("Reduction", 1, 0.1, 3).m(mode, Modes.Fast);
    public FloatSetting FactorValue = fsetting("Factor", 1, 1f, 5);
    public FloatSetting XZSpeed = fsetting("XZSpeed", 1, 0, 5).m2(mode, Modes.Setback).m2(mode, Modes.Fast);
    public FloatSetting YSpeedValue = fsetting("YSpeed", 1, 0, 3).m2(mode, Modes.Setback).m2(mode, Modes.Fast);
    public IntegerSetting valueBounded = isetting("BoundedVal", 2, 1, 255);
    public boolean firstStart = false;
    public int otherids = 0;
    protected float lastFactor;

    protected void clearValues() {
        lastFactor = 1.0f;
        otherids = 0;
        teleportID = 0;
        packets.clear();
        posLooks.clear();
    }

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        if (TRStep.getValue()) {
            ModuleManager.getModuleByClass(ReverseStep.class).disable();
        }
        if (mc.isSingleplayer()) {
            ChatUtil.sendMessage(TextFormatting.RED + "Can't enable PacketFly in SinglePlayer!");
            disable();
        }
        firstStart = true;
        clearValues();
    }

    @Override
    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }
        if (TRStep.getValue()) {
            ModuleManager.getModuleByClass(ReverseStep.class).enable();
        }
        this.otherids = 0;
        this.packets.clear();
    }

    @Override
    public String getHudInfo() {
        return "" + TextFormatting.AQUA + mode.getValue();
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        disable();
    }

    @SubscribeEvent
    public void onPacketReceive(final PacketEvents.Receive event) {
        if (fullNullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = event.getPacket();
            if (mc.player.isEntityAlive() && mode.getValue() != Modes.Setback && mc.world.isBlockLoaded(new BlockPos(mc.player), false) && !(mc.currentScreen instanceof GuiDownloadTerrain)) {
                TimeVec vec = posLooks.remove(packet.getTeleportId());
                if (vec != null && vec.x == packet.getX() && vec.y == packet.getY() && vec.z == packet.getZ()) {
                    event.setCanceled(true);
                    return;
                }
                teleportID = packet.getTeleportId();
                if (betterResponse.getValue()) {
                    doBetterResponse(event.getPacket());
                }
            }
        }
    }

    private void doBetterResponse(SPacketPlayerPosLook event) {
        double x = event.x;
        double z = event.z;
        float yaw = event.yaw;
        float pitch = event.pitch;
        if (event.flags.contains(SPacketPlayerPosLook.EnumFlags.X)) {
            x += mc.player.posX;
        }
        if (event.flags.contains(SPacketPlayerPosLook.EnumFlags.Z)) {
            z += mc.player.posZ;
        }
        if (event.flags.contains(SPacketPlayerPosLook.EnumFlags.X_ROT)) {
            pitch += mc.player.rotationPitch;
        }
        if (event.flags.contains(SPacketPlayerPosLook.EnumFlags.Y_ROT)) {
            yaw += mc.player.rotationYaw;
        }
        mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(x, mc.player.getEntityBoundingBox().minY, z, yaw, pitch, false));
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        if (posLooks != null) {
            posLooks.entrySet().removeIf(entry -> System.currentTimeMillis() - entry.getValue().getTime() > TimeUnit.SECONDS.toMillis(30L));
        }
    }

    @SubscribeEvent
    public void onTick(MotionUpdateEvent.Tick event) {
        if (fullNullCheck() || event.getStage() != 0) {
            return;
        }
        mc.player.setVelocity(0, 0, 0);
        if (mode.getValue() != Modes.Setback && teleportID == 0 && lessReduction.getValue()) {
            sendTP(mc.player.getPositionVector());
            if (resetTicks(6)) {
                sendPackets(0.0, 0.0, 0.0);
            }
            return;
        }
        if (TRStep.getValue()) {
            if (ModuleManager.getModuleByClass(ReverseStep.class).isEnabled()) {
                return;
            }
        }
        boolean isPhasing = checkHitBoxes();
        double ySpeed;
        if (mc.player.movementInput.jump && (isPhasing || !EntityUtil.isMoving())) {
            if (AntiKick.getValue() && !isPhasing) {
                ySpeed = resetTicks(mode.getValue() == Modes.Setback ? 10 : 20) ? -0.032 : 0.062;
            } else {
                ySpeed = 0.062;
            }
        } else if (mc.player.movementInput.sneak) {
            ySpeed = -0.062;
        } else {
            ySpeed = !isPhasing ? (resetTicks(4) ? (AntiKick.getValue() ? -0.04 : 0.0) : 0.0) : 0.0;
        }
        if (phase.getValue() == PhaseMode.Full && isPhasing && EntityUtil.isMoving() && ySpeed != 0.0) {
            ySpeed /= 2.5;
        }
        if (mode.getValue() == Modes.Increment) {
            if (lastFactor >= FactorValue.getValue()) {
                lastFactor = 1.0f;
            } else if (++lastFactor > FactorValue.getValue()) {
                lastFactor = FactorValue.getValue();
            }
        } else {
            lastFactor = FactorValue.getValue();
        }
        double[] dirSpeed = MathUtil.directionSpeed(phase.getValue().equals(PhaseMode.Full) && isPhasing ? 0.031 : 0.26);
        for (int i = 1; i <= ((mode.getValue() == Modes.Factor || mode.getValue() == Modes.Increment) ? lastFactor : 1); i++) {
            mc.player.motionX = dirSpeed[0] * 1f * i * XZSpeed.getValue();
            mc.player.motionY = ySpeed * 1f * i * YSpeedValue.getValue();
            mc.player.motionZ = dirSpeed[1] * 1f * i * XZSpeed.getValue();
            sendPackets(mc.player.motionX, mc.player.motionY, mc.player.motionZ);
        }
        //Fast Mode
        double FastSpeed = mc.player.movementInput.jump && (checkHitBoxes() || !EntityUtil.isMoving()) ? (AntiKick.getValue() && !checkHitBoxes() ? (resetTicks(10) ? -0.032 : 0.062) : (resetTicks(20) ? -0.032 : 0.062)) : (mc.player.movementInput.sneak ? -0.062 : (!checkHitBoxes() ? (resetTicks(4) ? (AntiKick.getValue() ? -0.04 : 0.0) : 0.0) : 0.0));
        if (checkHitBoxes() && EntityUtil.isMoving() && FastSpeed != 0) {
            FastSpeed /= Reduction.getValue();
        }
        if (mode.getValue().equals(Modes.Fast)) {
            sendPackets(dirSpeed[0], FastSpeed, dirSpeed[1]);
        }
    }

    @SubscribeEvent
    public void onMove(final MoveEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (firstStart) {
            event.setCanceled(true);
            firstStart = false;
        } else {
            event.setCanceled(false);
        }
        if (teleportID != 0 || mode.getValue().equals(Modes.Setback)) {
            event.setX(mc.player.motionX);
            event.setY(mc.player.motionY);
            event.setZ(mc.player.motionZ);
            if (checkHitBoxes() || phase.getValue().equals(PhaseMode.Semi)) {
                mc.player.noClip = true;
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvents.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && !this.packets.remove((CPacketPlayer) event.getPacket())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPush(PushEvent event) {
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderOverlayEvent event) {
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onRender(InsideBlockRenderEvent event) {
        event.setCanceled(true);
    }

    public boolean checkHitBoxes() {
        return !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox()).isEmpty();
    }

    public boolean resetTicks(int ticks) {
        if (++otherids >= ticks) {
            otherids = 0;
            return true;
        }
        return false;
    }

    public void sendPackets(double x, double y, double z) {
        Vec3d vec = new Vec3d(x, y, z);
        Vec3d position = mc.player.getPositionVector().add(vec);
        Vec3d outOfBoundsVec = CAONIMA(position);
        packetSender(new CPacketPlayer.Position(position.x, position.y, position.z, true));
        packetSender(new CPacketPlayer.PositionRotation(outOfBoundsVec.x, outOfBoundsVec.y, outOfBoundsVec.z, mc.player.rotationYaw, mc.player.rotationPitch, true));
        mc.player.setPosition(position.x, position.y, position.z);
        sendTP(position);
    }

    public void sendTP(Vec3d position) {
        if (confirmtp.getValue() && teleportID != 0) {
            int id = ++teleportID;
            mc.player.connection.sendPacket(new CPacketConfirmTeleport(id));
            posLooks.put(id, new TimeVec(position));
        }
    }

    public Vec3d CAONIMA(Vec3d position) {
        //左右极限 = 6
        //上下极限 =-150 || =300
        //尝试不非法发包
        double spoofX = position.x;
        double spoofY = position.y;
        double spoofZ = position.z;
        switch (type.getValue()) {
            case Up: {
                spoofY += 1337;
                break;
            }
            case Down: {
                spoofY -= 1337;
                break;
            }
            case DownStrict: {
                spoofY -= 256;
                break;
            }
            case Bounded: {
                spoofY += (spoofY < 127.5 ? 255 : 0) - position.y;
                break;
            }
            case Conceal: {
                spoofX += RandomUtil.nextInt(-100000, 100000);
                spoofY += 2.0;
                spoofZ += RandomUtil.nextInt(-100000, 100000);
                break;
            }
            case Limit: {
                spoofX += RandomUtil.nextDouble(-50.0, 50.0);
                spoofY += RandomUtil.getRandom().nextBoolean() ? RandomUtil.nextDouble(-80.0, -50.0) : RandomUtil.nextDouble(50.0, 80.0);
                spoofZ += RandomUtil.nextDouble(-50.0, 50.0);
                break;
            }
            case LimitJitter: {
                spoofX += RandomUtil.nextDouble(-10.0, 10.0);
                spoofY += (RandomUtil.getRandom().nextBoolean()) ? RandomUtil.nextDouble(-100.0, -80.0) : RandomUtil.nextDouble(80.0, 100.0);
                spoofZ += RandomUtil.nextDouble(-10.0, 10.0);
                break;
            }
            case Preserve: {
                spoofX += RandomUtil.getRandom().nextInt(100000);
                spoofZ += RandomUtil.getRandom().nextInt(100000);
                break;
            }
            case LimitPreserve: {
                spoofX += RandomUtil.nextDouble(45.0, 85.0);
                spoofY += (RandomUtil.getRandom().nextBoolean()) ? RandomUtil.nextDouble(-95.0, -40.0) : RandomUtil.nextDouble(40.0, 95.0);
                spoofZ += RandomUtil.nextDouble(-85.0, -45.0);
                break;
            }
            case Xin: {
                //spoofY += 10;
                spoofX += valueBounded.getValue();
                spoofY += 0;
                spoofZ += valueBounded.getValue();
                break;
            }
            case OrgStrict: {
                spoofX -= RandomUtil.nextInt(-10, 10);
                spoofY -= RandomUtil.nextInt(-2, 2);
                spoofZ += RandomUtil.nextInt(-10, 10);
                break;
            }
        }
        return new Vec3d(spoofX, spoofY, spoofZ);
    }

    public void packetSender(final CPacketPlayer packet) {
        packets.add(packet);
        mc.player.connection.sendPacket(packet);
    }

    public enum Types {
        Up,
        Down,
        DownStrict,
        Bounded,
        Conceal,
        Limit,
        LimitJitter,
        Preserve,
        LimitPreserve,
        Xin,
        OrgStrict
    }

    public enum PhaseMode {
        Off,
        Semi,
        Full
    }

    public enum Modes {
        Factor,
        Setback,
        Fast,
        Increment
    }

    public static class TimeVec extends Vec3d {
        private final long time;

        public TimeVec(Vec3d vec3d) {
            this(vec3d.x, vec3d.y, vec3d.z, System.currentTimeMillis());
        }

        public TimeVec(double xIn, double yIn, double zIn, long time) {
            super(xIn, yIn, zIn);
            this.time = time;
        }

        public long getTime() {
            return time;
        }
    }
}