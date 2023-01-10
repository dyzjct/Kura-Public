package me.windyteam.kura.module.modules.movement;

import me.windyteam.kura.event.events.client.PacketEvents;
import me.windyteam.kura.event.events.entity.MotionUpdateEvent;
import me.windyteam.kura.event.events.entity.MoveEvent;
import me.windyteam.kura.manager.Mapping;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.setting.BooleanSetting;
import me.windyteam.kura.setting.ModeSetting;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.utils.Timer;
import me.windyteam.kura.utils.entity.EntityUtil;
import me.windyteam.kura.utils.mc.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

@Module.Info(name = "LongJump", category = Category.MOVEMENT)
public class LongJump extends Module {
    public static LongJump INSTANCE = new LongJump();
    public ModeSetting<Mode> mode = msetting("Mode", Mode.TICK);
    public Setting<Float> boost = fsetting("Boost", 4.48f, 1, 20).m(mode, Mode.DIRECT);
    public Setting<Boolean> lagOff = bsetting("LagBackCheck", true);
    public Setting<Boolean> step = bsetting("SetStep", false);
    public Setting<Boolean> autoOff = bsetting("AutoOff", true);
    public BooleanSetting dealDMG = bsetting("DealDamage", true);
    public BooleanSetting updateY = bsetting("UpdateY", true);
    public int stage;
    public double moveSpeed;
    public double lastDist;
    public boolean beganJump = false;
    public boolean fuckDMG = false;
    public int groundTicks;
    public int airTicks;
    Timer timer = new Timer();

    @Override
    public void onEnable() {
        timer.reset();
        groundTicks = 0;
        stage = 0;
        beganJump = false;
        fuckDMG = true;
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            ChatUtil.NoSpam.sendMessage("Do Not Use LongJump While Sneaking! You Will Got Kicked for Invalid Packet!!!");
            disable();
        }
    }

    @Override
    public void onDisable() {
        setTimer(1.0f);
    }

    @SubscribeEvent
    public void onPacketReceive(final PacketEvents.Receive event) {
        if (lagOff.getValue() && event.getPacket() instanceof SPacketPlayerPosLook) {
            disable();
        }
    }

    @SubscribeEvent
    public void onMove(final MoveEvent event) {
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            ChatUtil.NoSpam.sendMessage("Do Not Use LongJump While Sneaking! You Will Got Kicked for Invalid Packet!!!");
            disable();
            return;
        }
        if (event.getStage() != 0) {
            return;
        }
        if (step.getValue()) {
            mc.player.stepHeight = 0.6f;
        }
        doVirtue(event);
    }

    @SubscribeEvent
    public void onTickEvent(final TickEvent.ClientTickEvent event) {
        if (fullNullCheck() || event.phase != TickEvent.Phase.START) {
            return;
        }
        if (dealDMG.getValue()) {
            if (fuckDMG) {
                damagePlayer(1);
                fuckDMG = false;
            }
        }
        if (mode.getValue() == Mode.TICK) {
            doNormal();
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(final MotionUpdateEvent.Tick event) {
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            ChatUtil.NoSpam.sendMessage("Do Not Use LongJump While Sneaking! You Will Got Kicked for Invalid Packet!!!");
            disable();
            return;
        }
        if (event.getStage() != 0) {
            return;
        }
        if (autoOff.getValue() && beganJump && mc.player.onGround) {
            disable();
            return;
        }
        switch (mode.getValue()) {
            case VIRTUE: {
                if (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) {
                    final double xDist = mc.player.posX - mc.player.prevPosX;
                    final double zDist = mc.player.posZ - mc.player.prevPosZ;
                    lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
                    break;
                }
                break;
            }
            case TICK: {
                return;
            }
            case DIRECT: {
                if (EntityUtil.isInLiquid() || EntityUtil.isOnLiquid()) {
                    break;
                }
                final float direction = mc.player.rotationYaw + ((mc.player.moveForward < 0.0f) ? 180 : 0) + ((mc.player.moveStrafing > 0.0f) ? (-90.0f * ((mc.player.moveForward < 0.0f) ? -0.5f : ((mc.player.moveForward > 0.0f) ? 0.5f : 1.0f))) : 0.0f) - ((mc.player.moveStrafing < 0.0f) ? (-90.0f * ((mc.player.moveForward < 0.0f) ? -0.5f : ((mc.player.moveForward > 0.0f) ? 0.5f : 1.0f))) : 0.0f);
                final float xDir = (float) Math.cos((direction + 90.0f) * 3.141592653589793 / 180.0);
                final float zDir = (float) Math.sin((direction + 90.0f) * 3.141592653589793 / 180.0);
                if (!mc.player.collidedVertically) {
                    ++airTicks;
                    if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(0.0, 2.147483647E9, 0.0, false));
                    }
                    groundTicks = 0;
                    if (updateY.getValue()) {
                        if (!mc.player.collidedVertically) {
                            if (mc.player.motionY == -0.07190068807140403) {
                                final EntityPlayerSP player = mc.player;
                                player.motionY *= 0.3499999940395355;
                            } else if (mc.player.motionY == -0.10306193759436909) {
                                final EntityPlayerSP player2 = mc.player;
                                player2.motionY *= 0.550000011920929;
                            } else if (mc.player.motionY == -0.13395038817442878) {
                                final EntityPlayerSP player3 = mc.player;
                                player3.motionY *= 0.6700000166893005;
                            } else if (mc.player.motionY == -0.16635183030382) {
                                final EntityPlayerSP player4 = mc.player;
                                player4.motionY *= 0.6899999976158142;
                            } else if (mc.player.motionY == -0.19088711097794803) {
                                final EntityPlayerSP player5 = mc.player;
                                player5.motionY *= 0.7099999785423279;
                            } else if (mc.player.motionY == -0.21121925191528862) {
                                final EntityPlayerSP player6 = mc.player;
                                player6.motionY *= 0.20000000298023224;
                            } else if (mc.player.motionY == -0.11979897632390576) {
                                final EntityPlayerSP player7 = mc.player;
                                player7.motionY *= 0.9300000071525574;
                            } else if (mc.player.motionY == -0.18758479151225355) {
                                final EntityPlayerSP player8 = mc.player;
                                player8.motionY *= 0.7200000286102295;
                            } else if (mc.player.motionY == -0.21075983825251726) {
                                final EntityPlayerSP player9 = mc.player;
                                player9.motionY *= 0.7599999904632568;
                            }
                            if (mc.player.motionY < -0.2 && mc.player.motionY > -0.24) {
                                final EntityPlayerSP player10 = mc.player;
                                player10.motionY *= 0.7;
                            }
                            if (mc.player.motionY < -0.25 && mc.player.motionY > -0.32) {
                                final EntityPlayerSP player11 = mc.player;
                                player11.motionY *= 0.8;
                            }
                            if (mc.player.motionY < -0.35 && mc.player.motionY > -0.8) {
                                final EntityPlayerSP player12 = mc.player;
                                player12.motionY *= 0.98;
                            }
                            if (mc.player.motionY < -0.8 && mc.player.motionY > -1.6) {
                                final EntityPlayerSP player13 = mc.player;
                                player13.motionY *= 0.99;
                            }
                        }
                    }
                    setTimer(0.85f);
                    final double[] speedVals = {0.420606, 0.417924, 0.415258, 0.412609, 0.409977, 0.407361, 0.404761, 0.402178, 0.399611, 0.39706, 0.394525, 0.392, 0.3894, 0.38644, 0.383655, 0.381105, 0.37867, 0.37625, 0.37384, 0.37145, 0.369, 0.3666, 0.3642, 0.3618, 0.35945, 0.357, 0.354, 0.351, 0.348, 0.345, 0.342, 0.339, 0.336, 0.333, 0.33, 0.327, 0.324, 0.321, 0.318, 0.315, 0.312, 0.309, 0.307, 0.305, 0.303, 0.3, 0.297, 0.295, 0.293, 0.291, 0.289, 0.287, 0.285, 0.283, 0.281, 0.279, 0.277, 0.275, 0.273, 0.271, 0.269, 0.267, 0.265, 0.263, 0.261, 0.259, 0.257, 0.255, 0.253, 0.251, 0.249, 0.247, 0.245, 0.243, 0.241, 0.239, 0.237};
                    if (mc.gameSettings.keyBindForward.pressed) {
                        try {
                            mc.player.motionX = xDir * speedVals[airTicks - 1] * 3.0;
                            mc.player.motionZ = zDir * speedVals[airTicks - 1] * 3.0;
                            break;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            return;
                        }
                    }
                    mc.player.motionX = 0.0;
                    mc.player.motionZ = 0.0;
                    break;
                }
                setTimer(1.0f);
                airTicks = 0;
                ++groundTicks;
                final EntityPlayerSP player14 = mc.player;
                player14.motionX /= 13.0;
                final EntityPlayerSP player15 = mc.player;
                player15.motionZ /= 13.0;
                if (updateY.getValue()) {
                    if (groundTicks == 1) {
                        updatePosition(mc.player.posX, mc.player.posY, mc.player.posZ);
                        updatePosition(mc.player.posX + 0.0624, mc.player.posY, mc.player.posZ);
                        updatePosition(mc.player.posX, mc.player.posY + 0.419, mc.player.posZ);
                        updatePosition(mc.player.posX + 0.0624, mc.player.posY, mc.player.posZ);
                        updatePosition(mc.player.posX, mc.player.posY + 0.419, mc.player.posZ);
                        break;
                    }
                }
                if (groundTicks > 2) {
                    groundTicks = 0;
                    mc.player.motionX = xDir * 0.3;
                    mc.player.motionZ = zDir * 0.3;
                    if (updateY.getValue()) {
                        mc.player.motionY = 0.42399999499320984;
                    }
                    beganJump = true;
                    break;
                }
                break;
            }
        }
    }

    public void damagePlayer(double d) {
        if (d < 1)
            d = 1;
        if (d > MathHelper.floor(mc.player.getMaxHealth()))
            d = MathHelper.floor(mc.player.getMaxHealth());

        double offset = 0.0625;
        if (mc.player != null && mc.getConnection() != null && mc.player.onGround) {
            for (int i = 0; i <= ((3 + d) / offset); i++) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX,
                        mc.player.posY + offset, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX,
                        mc.player.posY, mc.player.posZ, (i == ((3 + d) / offset))));
            }
        }
    }

    public void doNormal() {
        if (autoOff.getValue() && beganJump && mc.player.onGround) {
            disable();
            return;
        }
        switch (mode.getValue()) {
            case VIRTUE: {
                if (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) {
                    final double xDist = mc.player.posX - mc.player.prevPosX;
                    final double zDist = mc.player.posZ - mc.player.prevPosZ;
                    lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
                    break;
                }
                break;
            }
            case TICK: {
            }
            case DIRECT: {
                if (EntityUtil.isInLiquid() || EntityUtil.isOnLiquid()) {
                    break;
                }
                final float direction = mc.player.rotationYaw + ((mc.player.moveForward < 0.0f) ? 180 : 0) + ((mc.player.moveStrafing > 0.0f) ? (-90.0f * ((mc.player.moveForward < 0.0f) ? -0.5f : ((mc.player.moveForward > 0.0f) ? 0.5f : 1.0f))) : 0.0f) - ((mc.player.moveStrafing < 0.0f) ? (-90.0f * ((mc.player.moveForward < 0.0f) ? -0.5f : ((mc.player.moveForward > 0.0f) ? 0.5f : 1.0f))) : 0.0f);
                final float xDir = (float) Math.cos((direction + 90.0f) * 3.141592653589793 / 180.0);
                final float zDir = (float) Math.sin((direction + 90.0f) * 3.141592653589793 / 180.0);
                if (!mc.player.collidedVertically) {
                    ++airTicks;
                    if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(0.0, 2.147483647E9, 0.0, false));
                    }
                    groundTicks = 0;
                    if (updateY.getValue()) {
                        if (!mc.player.collidedVertically) {
                            if (mc.player.motionY == -0.07190068807140403) {
                                final EntityPlayerSP player = mc.player;
                                player.motionY *= 0.3499999940395355;
                            } else if (mc.player.motionY == -0.10306193759436909) {
                                final EntityPlayerSP player2 = mc.player;
                                player2.motionY *= 0.550000011920929;
                            } else if (mc.player.motionY == -0.13395038817442878) {
                                final EntityPlayerSP player3 = mc.player;
                                player3.motionY *= 0.6700000166893005;
                            } else if (mc.player.motionY == -0.16635183030382) {
                                final EntityPlayerSP player4 = mc.player;
                                player4.motionY *= 0.6899999976158142;
                            } else if (mc.player.motionY == -0.19088711097794803) {
                                final EntityPlayerSP player5 = mc.player;
                                player5.motionY *= 0.7099999785423279;
                            } else if (mc.player.motionY == -0.21121925191528862) {
                                final EntityPlayerSP player6 = mc.player;
                                player6.motionY *= 0.20000000298023224;
                            } else if (mc.player.motionY == -0.11979897632390576) {
                                final EntityPlayerSP player7 = mc.player;
                                player7.motionY *= 0.9300000071525574;
                            } else if (mc.player.motionY == -0.18758479151225355) {
                                final EntityPlayerSP player8 = mc.player;
                                player8.motionY *= 0.7200000286102295;
                            } else if (mc.player.motionY == -0.21075983825251726) {
                                final EntityPlayerSP player9 = mc.player;
                                player9.motionY *= 0.7599999904632568;
                            }
                            if (mc.player.motionY < -0.2 && mc.player.motionY > -0.24) {
                                final EntityPlayerSP player10 = mc.player;
                                player10.motionY *= 0.7;
                            }
                            if (mc.player.motionY < -0.25 && mc.player.motionY > -0.32) {
                                final EntityPlayerSP player11 = mc.player;
                                player11.motionY *= 0.8;
                            }
                            if (mc.player.motionY < -0.35 && mc.player.motionY > -0.8) {
                                final EntityPlayerSP player12 = mc.player;
                                player12.motionY *= 0.98;
                            }
                            if (mc.player.motionY < -0.8 && mc.player.motionY > -1.6) {
                                final EntityPlayerSP player13 = mc.player;
                                player13.motionY *= 0.99;
                            }
                        }
                    }
                    setTimer(0.85f);
                    final double[] speedVals = {0.420606, 0.417924, 0.415258, 0.412609, 0.409977, 0.407361, 0.404761, 0.402178, 0.399611, 0.39706, 0.394525, 0.392, 0.3894, 0.38644, 0.383655, 0.381105, 0.37867, 0.37625, 0.37384, 0.37145, 0.369, 0.3666, 0.3642, 0.3618, 0.35945, 0.357, 0.354, 0.351, 0.348, 0.345, 0.342, 0.339, 0.336, 0.333, 0.33, 0.327, 0.324, 0.321, 0.318, 0.315, 0.312, 0.309, 0.307, 0.305, 0.303, 0.3, 0.297, 0.295, 0.293, 0.291, 0.289, 0.287, 0.285, 0.283, 0.281, 0.279, 0.277, 0.275, 0.273, 0.271, 0.269, 0.267, 0.265, 0.263, 0.261, 0.259, 0.257, 0.255, 0.253, 0.251, 0.249, 0.247, 0.245, 0.243, 0.241, 0.239, 0.237};
                    if (mc.gameSettings.keyBindForward.pressed) {
                        try {
                            mc.player.motionX = xDir * speedVals[airTicks - 1] * 3.0;
                            mc.player.motionZ = zDir * speedVals[airTicks - 1] * 3.0;
                            break;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            return;
                        }
                    }
                    mc.player.motionX = 0.0;
                    mc.player.motionZ = 0.0;
                    break;
                }
                setTimer(1.0f);
                airTicks = 0;
                ++groundTicks;
                final EntityPlayerSP player14 = mc.player;
                player14.motionX /= 13.0;
                final EntityPlayerSP player15 = mc.player;
                player15.motionZ /= 13.0;
                if (updateY.getValue()) {
                    if (groundTicks == 1) {
                        updatePosition(mc.player.posX, mc.player.posY, mc.player.posZ);
                        updatePosition(mc.player.posX + 0.0624, mc.player.posY, mc.player.posZ);
                        updatePosition(mc.player.posX, mc.player.posY + 0.419, mc.player.posZ);
                        updatePosition(mc.player.posX + 0.0624, mc.player.posY, mc.player.posZ);
                        updatePosition(mc.player.posX, mc.player.posY + 0.419, mc.player.posZ);
                        break;
                    }
                }
                if (groundTicks > 2) {
                    groundTicks = 0;
                    mc.player.motionX = xDir * 0.3;
                    mc.player.motionZ = zDir * 0.3;
                    if (updateY.getValue()) {
                        mc.player.motionY = 0.42399999499320984;
                    }
                    beganJump = true;
                    break;
                }
                break;
            }
        }
    }

    public void doVirtue(final MoveEvent event) {
        if (mode.getValue() == Mode.VIRTUE && (mc.player.moveForward != 0.0f || (mc.player.moveStrafing != 0.0f && !EntityUtil.isOnLiquid() && !EntityUtil.isInLiquid()))) {
            if (stage == 0) {
                moveSpeed = boost.getValue() * getBaseMoveSpeed();
            } else if (stage == 1) {
                if (updateY.getValue()) {
                    event.setY(mc.player.motionY = 0.42);
                }
                moveSpeed *= 2.149;
            } else if (stage == 2) {
                final double difference = 0.66 * (lastDist - getBaseMoveSpeed());
                moveSpeed = lastDist - difference;
            } else {
                moveSpeed = lastDist - lastDist / 159.0;
            }
            setMoveSpeed(event, moveSpeed = Math.max(getBaseMoveSpeed(), moveSpeed));
            final List<AxisAlignedBB> collidingList = mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0));
            final List<AxisAlignedBB> collidingList2 = mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -0.4, 0.0));
            if (updateY.getValue()) {
                if (!mc.player.collidedVertically && (collidingList.size() > 0 || collidingList2.size() > 0)) {
                    event.setY(mc.player.motionY = -0.001);
                }
            }
            ++stage;
        } else if (stage > 0) {
            disable();
        }
    }

    public void updatePosition(final double x, final double y, final double z) {
        mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, mc.player.onGround));
    }

    public void setMoveSpeed(final MoveEvent event, final double speed) {
        final MovementInput movementInput = mc.player.movementInput;
        double forward = movementInput.moveForward;
        double strafe = movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            double cos = Math.cos(Math.toRadians(yaw + 90.0f));
            double sin = Math.sin(Math.toRadians(yaw + 90.0f));
            event.setX(forward * speed * cos + strafe * speed * sin);
            event.setZ(forward * speed * sin - strafe * speed * cos);
        }
    }

    public double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (mc.player != null && mc.player.isPotionActive(MobEffects.SPEED)) {
            final int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }

    public void setTimer(final float value) {
        try {
            final Field timer = Minecraft.class.getDeclaredField(Mapping.timer);
            timer.setAccessible(true);
            final Field tickLength = Timer.class.getDeclaredField(Mapping.tickLength);
            tickLength.setAccessible(true);
            tickLength.setFloat(timer.get(mc), 50.0f / value);
        } catch (Exception ignored) {
        }
    }

    public enum Mode {
        VIRTUE,
        DIRECT,
        TICK
    }
}
