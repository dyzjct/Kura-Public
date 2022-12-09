package me.dyzjct.kura.module.modules.movement;

import me.dyzjct.kura.module.Category;
import me.dyzjct.kura.module.Module;
import me.dyzjct.kura.module.ModuleManager;
import me.dyzjct.kura.module.modules.misc.AntiMine;
import me.dyzjct.kura.setting.ModeSetting;
import me.dyzjct.kura.setting.Setting;
import net.minecraft.network.play.client.CPacketPlayer;

@Module.Info(name = "Step", category = Category.MOVEMENT)
public class Step extends Module {
    private final ModeSetting<?> mode = msetting("Mode", Mode.NCP);
    private final Setting<Boolean> reverse = bsetting("ReverseStep", true);
    private final Setting<Boolean> disableanti = bsetting("DisableAntiMine", false);
    public final Setting<Double> height = dsetting("Height", 2, 0, 5);
    public boolean stepped;

    public static double[] forward(final double n) {
        float moveForward = mc.player.movementInput.moveForward;
        float moveStrafe = mc.player.movementInput.moveStrafe;
        float n2 = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) {
                n2 += ((moveForward > 0.0f) ? -45 : 45);
            } else if (moveStrafe < 0.0f) {
                n2 += ((moveForward > 0.0f) ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            } else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(n2 + 90.0f));
        final double cos = Math.cos(Math.toRadians(n2 + 90.0f));
        return new double[]{moveForward * n * cos + moveStrafe * n * sin, moveForward * n * sin - moveStrafe * n * cos};
    }

    @Override
    public void onUpdate() {
        if (mc.world == null || mc.player == null || mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder() || mc.gameSettings.keyBindJump.isKeyDown()) {
            return;
        }
        if (disableanti.getValue()&&ModuleManager.getModuleByClass(AntiMine.class).isEnabled()){
            ModuleManager.getModuleByClass(AntiMine.class).disable();
        }
        if (mc.player != null && mc.player.onGround && !mc.player.isInWater() && !mc.player.isOnLadder() && this.reverse.getValue()) {
            for (double n = 0.0; n < this.height.getValue() + 0.5; n += 0.01) {
                if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -n, 0.0)).isEmpty()) {
                    mc.player.motionY = -10.0;
                    break;
                }
            }
        }
        if (this.mode.getValue() == Mode.NCP) {
            this.stepped = false;
            final double[] forward = forward(0.1);
            boolean b = false;
            boolean b2 = false;
            boolean b3 = false;
            boolean b4 = false;
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forward[0], 3.1, forward[1])).isEmpty()) {
                mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forward[0], 2.9, forward[1]));
            }
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forward[0], 2.6, forward[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forward[0], 2.4, forward[1])).isEmpty()) {
                b = true;
            }
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forward[0], 2.1, forward[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forward[0], 1.9, forward[1])).isEmpty()) {
                b2 = true;
            }
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forward[0], 1.6, forward[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forward[0], 1.4, forward[1])).isEmpty()) {
                b3 = true;
            }
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forward[0], 1.0, forward[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forward[0], 0.6, forward[1])).isEmpty()) {
                b4 = true;
            }
            if (mc.player.collidedHorizontally && (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) && mc.player.onGround) {
                if (b4 && this.height.getValue() >= 1.0) {
                    this.stepped = true;
                    final double[] array = {0.42, 0.753, 1.0};
                    for (double v : array) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + v, mc.player.posZ, mc.player.onGround));
                    }
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0, mc.player.posZ);
                }
                if (b3 && this.height.getValue() >= 1.5) {
                    this.stepped = true;
                    final double[] array2 = {0.42, 0.75, 1.0, 1.16, 1.23, 1.2, 1.5};
                    for (double v : array2) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + v, mc.player.posZ, mc.player.onGround));
                    }
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 1.5, mc.player.posZ);
                }
                if (b2 && this.height.getValue() >= 2.0) {
                    this.stepped = true;
                    final double[] array3 = {0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43, 2.0};
                    for (double v : array3) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + v, mc.player.posZ, mc.player.onGround));
                    }
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 2.0, mc.player.posZ);
                }
                if (b && this.height.getValue() >= 2.5) {
                    this.stepped = true;
                    final double[] array4 = {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907, 2.5};
                    for (double v : array4) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + v, mc.player.posZ, mc.player.onGround));
                    }
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 2.5, mc.player.posZ);
                }
            }
        } else if(mode.getValue().equals(Mode.VANILLA)) {
            mc.player.stepHeight = (float) (double) this.height.getValue();
        }
    }

    @Override
    public String getHudInfo() {
        return this.mode.getValue().equals(Mode.NCP) ? "NCP" : "Vanilla";
    }

    public void onDisable() {
        mc.player.stepHeight = 0.5f;
    }

    public enum Mode {
        VANILLA,
        NCP
    }
}
