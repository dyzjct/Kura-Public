package me.dyzjct.kura.module.modules.movement;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.setting.Setting;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

@Module.Info(name = "GuiMove", description = "null", category = Category.MOVEMENT)
public class GuiMove extends Module {
    private final Setting<Boolean> chat = bsetting("Chat", false);
    private final Setting<Boolean> sneak = bsetting("Sneak", false);
    private final Setting<Integer> yawSpeed = isetting("YawSpeed", 6, 0, 20);
    private final Setting<Integer> pitchSpeed = isetting("PitchSpeed", 6, 0, 20);

    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        if (isEnabled() && mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat) || (mc.currentScreen instanceof GuiChat && chat.getValue()) || mc.player.isElytraFlying()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                for (int i = 0; i < pitchSpeed.getValue(); ++i) {
                    mc.player.rotationPitch--;
                }
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                for (int i = 0; i < pitchSpeed.getValue(); ++i) {
                    mc.player.rotationPitch++;
                }
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                for (int i = 0; i < yawSpeed.getValue(); ++i) {
                    mc.player.rotationYaw++;
                }
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                for (int i = 0; i < yawSpeed.getValue(); ++i) {
                    mc.player.rotationYaw--;
                }
            }
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode())) {
                mc.player.setSprinting(true);
            }
            try {
                if (mc.player.rotationPitch > 90) mc.player.rotationPitch = 90;
                if (mc.player.rotationPitch < -90) mc.player.rotationPitch = -90;
            } catch (Exception ignored) {
            }
        }
    }

    @SubscribeEvent
    public void awa(InputUpdateEvent event) {
        if (isEnabled() && mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat) || (mc.currentScreen instanceof GuiChat && chat.getValue())) {
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
                event.getMovementInput().moveForward = getSpeed();
            }

            if (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
                event.getMovementInput().moveForward = -getSpeed();
            }

            if (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())) {
                event.getMovementInput().moveStrafe = getSpeed();
            }

            if (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())) {
                event.getMovementInput().moveStrafe = -getSpeed();
            }

            if (Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                event.getMovementInput().jump = true;
            }

            if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && sneak.getValue()) {
                event.getMovementInput().sneak = true;
            }
        }
    }

    private float getSpeed() {
        float x = 1;
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && sneak.getValue()) {
            x = 0.30232558139f;
        }
        return x;
    }
}
