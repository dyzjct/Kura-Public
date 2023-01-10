package me.windyteam.kura.manager;

import me.windyteam.kura.event.events.entity.MotionUpdateEvent;
import me.windyteam.kura.utils.math.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.ConcurrentHashMap;

public class RotationManager extends Event {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ConcurrentHashMap<Float, Float> rotationMap = new ConcurrentHashMap<>();

    private static boolean fullNullCheck() {
        return mc.player == null || mc.world == null;
    }

    public static void addRotations(float yaw, float pitch) {
        if (rotationMap != null) {
            rotationMap.put(yaw, pitch);
        }
    }

    public static void resetRotation() { }

    public void setPlayerRotations(float yaw, float pitch) {
        RotationManager.mc.player.rotationYaw = yaw;
        RotationManager.mc.player.rotationYawHead = yaw;
        RotationManager.mc.player.rotationPitch = pitch;
    }

    public void lookAtVec3d(Vec3d vec3d) {
        float[] angle = MathUtil.calcAngle(RotationManager.mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(vec3d.x, vec3d.y, vec3d.z));
        this.setPlayerRotations(angle[0], angle[1]);
    }

    public void lookAtVec3d(double x, double y, double z) {
        Vec3d vec3d = new Vec3d(x, y, z);
        this.lookAtVec3d(vec3d);
    }

    @SubscribeEvent
    public void onMotion(MotionUpdateEvent.Tick event) {
        if (fullNullCheck()) {
            return;
        }
        if (rotationMap != null) {
            if (!rotationMap.isEmpty()) {
                rotationMap.forEach((y, p) -> {
                    event.setRotation(y, p);
                    rotationMap.remove(y, p);
                });
            }
        }
    }
}