package me.windyteam.kura.module.modules.combat;

import me.windyteam.kura.event.events.entity.MotionUpdateEvent;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.module.ModuleManager;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("all")
@Module.Info(name = "Aimbot", category = Category.HIDDEN, visible = false)
public class Aimbot extends Module {
    public static Aimbot INSTANCE = new Aimbot();
    public boolean shouldRotate;
    public float yaw;
    public float pitch;
    public boolean shouldReset;
    public float playerPitch;
    public float lastPlayerPitch;
    public float playerYaw;
    public float renderYaw;
    public float YawOffset;
    float rotationYaw;
    boolean smoothRotatePitch;
    boolean smoothRotated;
    boolean smoothRotateYaw;
    int addedInputYaw;
    float smoothYaw;
    float rotationPitch;
    int addedOriginYaw;
    float smoothPitch;

    public Aimbot() {
        shouldRotate = false;
        shouldReset = false;
    }

    public static double calculateDirectionDifference(double n, double n2) {
        while (n < 0.0) {
            n += 360.0;
        }
        while (n > 360.0) {
            n -= 360.0;
        }
        while (n2 < 0.0) {
            n2 += 360.0;
        }
        while (n2 > 360.0) {
            n2 -= 360.0;
        }
        final double n3 = Math.abs(n2 - n) % 360.0;
        return (n3 > 180.0) ? (360.0 - n3) : n3;
    }

    @SubscribeEvent
    public void oao(RenderPlayerEvent.Post event) {
        if (shouldReset) {
            mc.player.rotationPitch = playerPitch;
            mc.player.prevRotationPitch = lastPlayerPitch;
            mc.player.rotationYaw = playerYaw;
            mc.player.rotationYawHead = renderYaw;
            mc.player.renderYawOffset = YawOffset;
            shouldReset = false;
        }
    }

    @SubscribeEvent
    public void awa(RenderPlayerEvent.Pre event) {
        if (mc.player == null || !((Aimbot) ModuleManager.getModuleByName("Aimbot")).shouldRotate) {
            return;
        } else {
            playerPitch = mc.player.rotationPitch;
            lastPlayerPitch = mc.player.prevRotationPitch;
            playerYaw = mc.player.rotationYaw;
            renderYaw = mc.player.rotationYawHead;
            YawOffset = mc.player.renderYawOffset;
            mc.player.rotationYaw = ((Aimbot) ModuleManager.getModuleByName("Aimbot")).yaw;
            mc.player.renderYawOffset = ((Aimbot) ModuleManager.getModuleByName("Aimbot")).yaw;
            mc.player.rotationYawHead = ((Aimbot) ModuleManager.getModuleByName("Aimbot")).yaw;
            mc.player.rotationPitch = ((Aimbot) ModuleManager.getModuleByName("Aimbot")).pitch;
            mc.player.prevRotationPitch = ((Aimbot) ModuleManager.getModuleByName("Aimbot")).pitch;
            shouldReset = true;
            return;
        }
    }

    public boolean setRotation(float setSmoothRotationYaw, float n, final boolean b) {
        final boolean b2 = false;
        smoothRotatePitch = b2;
        smoothRotateYaw = b2;
        smoothRotated = true;
        if (b) {
            if (!shouldRotate) {
                yaw = mc.player.prevRotationYaw;
                pitch = mc.player.prevRotationPitch;
            }
            if (calculateDirectionDifference(setSmoothRotationYaw + 180.0f, yaw + 180.0f) > 90.0) {
                setSmoothRotationYaw = setSmoothRotationYaw(setSmoothRotationYaw, yaw);
                smoothRotated = false;
            }
            if (Math.abs(n - pitch) > 90.0f) {
                smoothRotatePitch = true;
                smoothRotated = false;
                smoothPitch = n;
                if (n > pitch) {
                    n -= (n - pitch) / 2.0f;
                } else {
                    n += (pitch - n) / 2.0f;
                }
            }
        }
        yaw = setSmoothRotationYaw;
        pitch = n;
        shouldRotate = true;
        return !smoothRotatePitch && !smoothRotateYaw;
    }

    @SubscribeEvent
    public void Post(MotionUpdateEvent.Tick event) {
        if (event.getStage() == 1) {
            if (shouldReset) {
                mc.player.rotationPitch = rotationPitch;
                mc.player.rotationYaw = rotationYaw;
                shouldReset = false;
            }
            if (mc.player == null || !((Aimbot) ModuleManager.getModuleByName("Aimbot")).shouldRotate) {
                return;
            } else {
                mc.player.renderYawOffset = ((Aimbot) ModuleManager.getModuleByName("Aimbot")).yaw;
                mc.player.rotationYawHead = ((Aimbot) ModuleManager.getModuleByName("Aimbot")).yaw;
                return;
            }
        }
    }

    public float setSmoothRotationYaw(float smoothYaw, float n) {
        smoothRotateYaw = true;
        final int n2 = 0;
        addedOriginYaw = n2;
        addedInputYaw = n2;
        while (smoothYaw + 180.0f < 0.0f) {
            smoothYaw += 360.0f;
            ++addedInputYaw;
        }
        while (smoothYaw + 180.0f > 360.0f) {
            smoothYaw -= 360.0f;
            --addedInputYaw;
        }
        while (n + 180.0f < 0.0f) {
            n += 360.0f;
            ++addedOriginYaw;
        }
        while (n + 180.0f > 360.0f) {
            n -= 360.0f;
            --addedOriginYaw;
        }
        smoothYaw += 180.0f;
        n += 180.0f;
        final double n3 = n - smoothYaw;
        if (n3 >= -180.0 && n3 >= 180.0) {
            smoothYaw -= (float) (n3 / 2.0);
        } else {
            smoothYaw += (float) (n3 / 2.0);
        }
        smoothYaw -= 180.0f;
        if (addedInputYaw > 0) {
            for (int i = 0; i < addedInputYaw; ++i) {
                smoothYaw -= 360.0f;
            }
        } else if (addedInputYaw < 0) {
            for (int j = 0; j > addedInputYaw; --j) {
                smoothYaw += 360.0f;
            }
        }
        return smoothYaw;
    }

    public boolean setRotation(final float n, final float n2) {
        setRotation(n, n2, false);
        return false;
    }

    public void setxp(float n) {
        smoothRotatePitch = false;
        smoothRotated = true;
        pitch = n;
        shouldRotate = true;
    }

    @SubscribeEvent
    public void Pre(MotionUpdateEvent.Tick event) {
        if (event.getStage() == 0) {
            if (smoothRotated) {
                if (smoothRotateYaw) {
                    yaw = smoothYaw;
                    smoothRotateYaw = false;
                }
                if (smoothRotatePitch) {
                    pitch = smoothPitch;
                    smoothRotatePitch = false;
                }
            }
            if (shouldRotate) {
                rotationPitch = mc.player.rotationPitch;
                rotationYaw = mc.player.rotationYaw;
                mc.player.rotationPitch = pitch;
                mc.player.rotationYaw = yaw;
                shouldReset = true;
                smoothRotated = true;
            }
        }
    }

    public void resetRotation() {
        if (fullNullCheck()) {
            return;
        }
        shouldRotate = false;
        smoothYaw = mc.player.rotationYaw;
        yaw = rotationYaw;
        smoothPitch = mc.player.rotationPitch;
        pitch = rotationPitch;
        smoothRotated = true;
    }

}
