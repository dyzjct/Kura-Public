
package me.dyzjct.kura.module.modules.movement;

import io.netty.util.internal.ConcurrentSet;
import me.dyzjct.kura.event.events.client.PacketEvents;
import me.dyzjct.kura.event.events.entity.MotionUpdateEvent;
import me.dyzjct.kura.event.events.entity.MoveEvent;
import me.dyzjct.kura.event.events.entity.PushEvent;
import me.dyzjct.kura.event.events.player.UpdateWalkingPlayerEvent;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.Setting;
import me.dyzjct.kura.utils.NTMiku.Timer;
import me.dyzjct.kura.utils.mc.EntityUtil;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Module.Info(name = "Phase",category = Category.MOVEMENT)
public class TestPhase
        extends Module {
    public Setting<Boolean> flight = bsetting("Flight", true);
    public Setting<Integer> flightMode = isetting("FMode", 0, 0, 1);
    public Setting<Boolean> doAntiFactor = bsetting("Factorize", true);
    public Setting<Double> antiFactor = dsetting("AntiFactor", 2.5, 0.1, 3.0);
    public Setting<Double> extraFactor = dsetting("ExtraFactor", 1.0, 0.1, 3.0);
    public Setting<Boolean> strafeFactor = bsetting("StrafeFactor", true);
    public Setting<Integer> loops = isetting("Loops", 1, 1, 10);
    public Setting<Boolean> clearTeleMap = bsetting("ClearMap", true);
    public Setting<Integer> mapTime = isetting("ClearTime", 30, 1, 500);
    public Setting<Boolean> clearIDs = bsetting("ClearIDs", true);
    public Setting<Boolean> setYaw = bsetting("SetYaw", true);
    public Setting<Boolean> setID = bsetting("SetID", true);
    public Setting<Boolean> setMove = bsetting("SetMove", false);
    public Setting<Boolean> nocliperino = bsetting("NoClip", false);
    public Setting<Boolean> sendTeleport = bsetting("Teleport", true);
    public Setting<Boolean> resetID = bsetting("ResetID", true);
    public Setting<Boolean> setPos = bsetting("SetPos", false);
    public Setting<Boolean> invalidPacket = bsetting("InvalidPacket", true);
    private final Set<CPacketPlayer> packets = new ConcurrentSet();
    private final Map<Integer, IDtime> teleportmap = new ConcurrentHashMap<Integer, IDtime>();
    private int flightCounter = 0;
    private int teleportID = 0;
    private static TestPhase instance;

    public TestPhase() {
        instance = this;
    }

    public static TestPhase getInstance() {
        if (instance == null) {
            instance = new TestPhase();
        }
        return instance;
    }
    
    @SubscribeEvent
    public void onTick(MotionUpdateEvent event) {
        this.teleportmap.entrySet().removeIf(idTime -> this.clearTeleMap.getValue() != false && ((IDtime)idTime.getValue()).getTimer().passedS(this.mapTime.getValue().intValue()));
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 1) {
            return;
        }
        TestPhase.mc.player.setVelocity(0.0, 0.0, 0.0);
        double speed = 0.0;
        boolean checkCollisionBoxes = this.checkHitBoxes();
        speed = TestPhase.mc.player.movementInput.jump && (checkCollisionBoxes || !EntityUtil.isMoving()) ? (this.flight.getValue().booleanValue() && !checkCollisionBoxes ? (this.flightMode.getValue() == 0 ? (this.resetCounter(10) ? -0.032 : 0.062) : (this.resetCounter(20) ? -0.032 : 0.062)) : 0.062) : (TestPhase.mc.player.movementInput.sneak ? -0.062 : (!checkCollisionBoxes ? (this.resetCounter(4) ? (this.flight.getValue().booleanValue() ? -0.04 : 0.0) : 0.0) : 0.0));
        if (this.doAntiFactor.getValue().booleanValue() && checkCollisionBoxes && EntityUtil.isMoving() && speed != 0.0) {
            speed /= this.antiFactor.getValue().doubleValue();
        }
        double[] strafing = this.getMotion(this.strafeFactor.getValue() != false && checkCollisionBoxes ? 0.031 : 0.26);
        for (int i = 1; i < this.loops.getValue() + 1; ++i) {
            TestPhase.mc.player.motionX = strafing[0] * (double)i * this.extraFactor.getValue();
            TestPhase.mc.player.motionY = speed * (double)i;
            TestPhase.mc.player.motionZ = strafing[1] * (double)i * this.extraFactor.getValue();
            this.sendPackets(TestPhase.mc.player.motionX, TestPhase.mc.player.motionY, TestPhase.mc.player.motionZ, this.sendTeleport.getValue());
        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (this.setMove.getValue().booleanValue() && this.flightCounter != 0) {
            event.setX(TestPhase.mc.player.motionX);
            event.setY(TestPhase.mc.player.motionY);
            event.setZ(TestPhase.mc.player.motionZ);
            if (this.nocliperino.getValue().booleanValue() && this.checkHitBoxes()) {
                TestPhase.mc.player.noClip = true;
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvents.Send event) {
        CPacketPlayer packet;
        if (event.getPacket() instanceof CPacketPlayer && !this.packets.remove(packet = (CPacketPlayer)event.getPacket())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPushOutOfBlocks(PushEvent event) {
        if (event.getStage() == 1) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvents.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook && !TestPhase.fullNullCheck()) {
            BlockPos pos;
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
            if (TestPhase.mc.player.isEntityAlive() && TestPhase.mc.world.isBlockLoaded(pos = new BlockPos(TestPhase.mc.player.posX, TestPhase.mc.player.posY, TestPhase.mc.player.posZ), false) && !(TestPhase.mc.currentScreen instanceof GuiDownloadTerrain) && this.clearIDs.getValue().booleanValue()) {
                this.teleportmap.remove(packet.getTeleportId());
            }
            if (this.setYaw.getValue().booleanValue()) {
                packet.yaw = TestPhase.mc.player.rotationYaw;
                packet.pitch = TestPhase.mc.player.rotationPitch;
            }
            if (this.setID.getValue().booleanValue()) {
                this.teleportID = packet.getTeleportId();
            }
        }
    }

    private boolean checkHitBoxes() {
        return !TestPhase.mc.world.getCollisionBoxes((Entity)TestPhase.mc.player, TestPhase.mc.player.getEntityBoundingBox().expand(-0.0625, -0.0625, -0.0625)).isEmpty();
    }

    private boolean resetCounter(int counter) {
        if (++this.flightCounter >= counter) {
            this.flightCounter = 0;
            return true;
        }
        return false;
    }

    private double[] getMotion(double speed) {
        float moveForward = TestPhase.mc.player.movementInput.moveForward;
        float moveStrafe = TestPhase.mc.player.movementInput.moveStrafe;
        float rotationYaw = TestPhase.mc.player.prevRotationYaw + (TestPhase.mc.player.rotationYaw - TestPhase.mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) {
                rotationYaw += (float)(moveForward > 0.0f ? -45 : 45);
            } else if (moveStrafe < 0.0f) {
                rotationYaw += (float)(moveForward > 0.0f ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            } else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        double posX = (double)moveForward * speed * -Math.sin(Math.toRadians(rotationYaw)) + (double)moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
        double posZ = (double)moveForward * speed * Math.cos(Math.toRadians(rotationYaw)) - (double)moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));
        return new double[]{posX, posZ};
    }

    private void sendPackets(double x, double y, double z, boolean teleport) {
        Vec3d vec = new Vec3d(x, y, z);
        Vec3d position = TestPhase.mc.player.getPositionVector().add(vec);
        Vec3d outOfBoundsVec = this.outOfBoundsVec(vec, position);
        this.packetSender((CPacketPlayer)new CPacketPlayer.Position(position.x, position.y, position.z, TestPhase.mc.player.onGround));
        if (this.invalidPacket.getValue().booleanValue()) {
            this.packetSender((CPacketPlayer)new CPacketPlayer.Position(outOfBoundsVec.x, outOfBoundsVec.y, outOfBoundsVec.z, TestPhase.mc.player.onGround));
        }
        if (this.setPos.getValue().booleanValue()) {
            TestPhase.mc.player.setPosition(position.x, position.y, position.z);
        }
        this.teleportPacket(position, teleport);
    }

    private void teleportPacket(Vec3d pos, boolean shouldTeleport) {
        if (shouldTeleport) {
            TestPhase.mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(++this.teleportID));
            this.teleportmap.put(this.teleportID, new IDtime(pos, new Timer()));
        }
    }

    private Vec3d outOfBoundsVec(Vec3d offset, Vec3d position) {
        return position.add(0.0, 1337.0, 0.0);
    }

    private void packetSender(CPacketPlayer packet) {
        this.packets.add(packet);
        TestPhase.mc.player.connection.sendPacket((Packet)packet);
    }

    private void clean() {
        this.teleportmap.clear();
        this.flightCounter = 0;
        if (this.resetID.getValue().booleanValue()) {
            this.teleportID = 0;
        }
        this.packets.clear();
    }

    public static class IDtime {
        private final Vec3d pos;
        private final Timer timer;

        public IDtime(Vec3d pos, Timer timer) {
            this.pos = pos;
            this.timer = timer;
            this.timer.reset();
        }

        public Vec3d getPos() {
            return this.pos;
        }

        public Timer getTimer() {
            return this.timer;
        }
    }
}

