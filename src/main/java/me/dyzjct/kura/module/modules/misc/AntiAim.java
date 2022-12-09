package me.dyzjct.kura.module.modules.misc;

import me.dyzjct.kura.event.events.entity.MotionUpdateEvent;
import me.dyzjct.kura.manager.RotationManager;
import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.ModeSetting;
import me.dyzjct.kura.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Info(name = "AntiAim", category = Category.MISC)
public class AntiAim extends Module {
    public Setting<YawMode> yawMode = msetting("YawMode", YawMode.Spin);
    public ModeSetting<PitchMode> pitchMode = msetting("PitchMode", PitchMode.Down);
    public Setting<Float> spinSpeed = fsetting("SpinSpeed", 10, 1, 100);
    public Setting<Integer> CustomPitch = isetting("CustomPitch", 90, 0, 360).m(pitchMode, PitchMode.Custom);
    private float rotation;
    private float rotation2;

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        RotationManager.resetRotation();
        rotation = mc.player.rotationYaw;
        rotation2 = mc.player.rotationPitch;
    }

    @Override
    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }
        RotationManager.resetRotation();
        rotation = mc.player.rotationYaw;
        rotation2 = mc.player.rotationPitch;
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(MotionUpdateEvent.Tick event) {
        if (fullNullCheck()) {
            return;
        }

        float[] derpRotations = {rotation + (float) (Math.random() * (double) 360 - (double) 180), (float) (Math.random() * (double) 180 - (double) 90)};
        switch (yawMode.getValue()) {
            case Off: {
                break;
            }
            case Spin: {
                rotation += spinSpeed.getValue();
                if (!(rotation >= 360.0f)) break;
                rotation = 0.0f;
                break;
            }
            case Jitter: {
                rotation = mc.player.ticksExisted % 2 == 0 ? 90 : -90;
                break;
            }
            case Bruh: {
                rotation = derpRotations[0];
                break;
            }
        }
        switch (pitchMode.getValue()) {
            case Off: {
                break;
            }
            case Up: {
                rotation2 = -90.0f;
                break;
            }
            case Down: {
                rotation2 = 90.0f;
                break;
            }
            case Jitter: {
                rotation2 += 30.0f;
                if (rotation2 > 90.0f) {
                    rotation2 = -90.0f;
                    return;
                }
                if (!(rotation2 < -90.0f)) break;
                rotation2 = 90.0f;
                return;
            }
            case Headless: {
                rotation2 = 180.0f;
                break;
            }
            case Bruh: {
                rotation2 = derpRotations[1];
                break;
            }
            case Custom: {
                rotation2 = CustomPitch.getValue();
                break;
            }
        }
        mc.player.rotationYawHead = rotation;
        mc.player.renderYawOffset = rotation;
        event.setYaw(rotation);
        event.setPitch(rotation2);
    }

    private enum YawMode {
        Off,
        Spin,
        Jitter,
        Bruh
    }

    private enum PitchMode {
        Off,
        Up,
        Down,
        Jitter,
        Headless,
        Bruh,
        Custom
    }
}