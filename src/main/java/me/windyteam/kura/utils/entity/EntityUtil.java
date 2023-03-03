package me.windyteam.kura.utils.entity;

import com.google.gson.JsonParser;
import me.windyteam.kura.Kura;
import me.windyteam.kura.friend.FriendManager;
import me.windyteam.kura.utils.Wrapper;
import me.windyteam.kura.utils.block.BlockUtil;
import me.windyteam.kura.utils.math.MathUtil;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ChunkCache;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class EntityUtil {
    public static final Vec3d[] antiDropOffsetList;
    public static final Vec3d[] platformOffsetList;
    public static final Vec3d[] legOffsetList;
    public static final Vec3d[] OffsetList;
    public static final Vec3d[] antiStepOffsetList;
    public static final Vec3d[] antiScaffoldOffsetList;
    public static final Vec3d[] doubleLegOffsetList;
    final static Minecraft mc = Minecraft.getMinecraft();
    private static BlockPos BlockPos;

    static {
        antiDropOffsetList = new Vec3d[]{new Vec3d(0.0, -2.0, 0.0)};
        platformOffsetList = new Vec3d[]{new Vec3d(0.0, -1.0, 0.0), new Vec3d(0.0, -1.0, -1.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(-1.0, -1.0, 0.0), new Vec3d(1.0, -1.0, 0.0)};
        legOffsetList = new Vec3d[]{new Vec3d(-1.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 1.0)};
        OffsetList = new Vec3d[]{new Vec3d(1.0, 1.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(0.0, 2.0, 0.0)};
        antiStepOffsetList = new Vec3d[]{new Vec3d(-1.0, 2.0, 0.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(0.0, 2.0, -1.0)};
        antiScaffoldOffsetList = new Vec3d[]{new Vec3d(0.0, 3.0, 0.0)};
        doubleLegOffsetList = new Vec3d[]{new Vec3d(-1.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-2.0, 0.0, 0.0), new Vec3d(2.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -2.0), new Vec3d(0.0, 0.0, 2.0)};
    }

    public static boolean isHoldingWeapon(EntityPlayer player) {
        return player.getHeldItemMainhand().getItem() instanceof ItemSword || player.getHeldItemMainhand().getItem() instanceof ItemAxe;
    }
    public static boolean isProjectile(final Entity entity) {
        return entity instanceof EntityShulkerBullet || entity instanceof EntityFireball;
    }

    public static boolean isVehicle(final Entity entity) {
        return entity instanceof EntityBoat || entity instanceof EntityMinecart;
    }

    public static void attackEntity(Entity entity, boolean packet, boolean swingArm) {
        if (packet) {
            EntityUtil.mc.player.connection.sendPacket(new CPacketUseEntity(entity));
        } else {
            EntityUtil.mc.playerController.attackEntity(EntityUtil.mc.player, entity);
        }
        if (swingArm) {
            EntityUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }
    public static float getSpeed() {
        return MathHelper.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
    }

    public static void strafe() {
        strafe(getSpeed());
    }

    public static void strafe(float speed) {
        if (!isMoving()) {
            return;
        }
        double forward = mc.player.moveForward;
        double strafe = mc.player.moveStrafing;
        float yaw = mc.player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            mc.player.motionX = 0.0;
            mc.player.motionZ = 0.0;
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
        }
        double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        mc.player.motionX = (forward * speed * cos + strafe * speed * sin);
        mc.player.motionZ = (forward * speed * sin - strafe * speed * cos);
    }

    public static boolean getSurroundWeakness(Vec3d pos, int feetMine, int render) {
        switch (feetMine) {
            case 1: {
                Block blockb;
                Block blocka;
                BlockPos raytrace = new BlockPos(pos);
                if (!BlockUtil.canBlockBeSeen(raytrace.getX() - 2, raytrace.getY(), raytrace.getZ()) && Math.sqrt(EntityUtil.mc.player.getDistanceSq(raytrace.getX() - 2, raytrace.getY(), raytrace.getZ())) > 3.0) {
                    return false;
                }
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-2, 1, 0)).getBlock();
                if (block != Blocks.AIR && block != Blocks.FIRE || (blocka = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-2, 0, 0)).getBlock()) != Blocks.AIR && blocka != Blocks.FIRE || (blockb = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-2, -1, 0)).getBlock()) != Blocks.OBSIDIAN && blockb != Blocks.BEDROCK || EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-1, 0, 0)).getBlock() == Blocks.BEDROCK)
                    break;
                return true;
            }
            case 2: {
                Block blockb;
                Block blocka;
                BlockPos raytrace = new BlockPos(pos);
                if (!BlockUtil.canBlockBeSeen(raytrace.getX() + 2, raytrace.getY(), raytrace.getZ()) && Math.sqrt(EntityUtil.mc.player.getDistanceSq(raytrace.getX() + 2, raytrace.getY(), raytrace.getZ())) > 3.0) {
                    return false;
                }
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(2, 1, 0)).getBlock();
                if (block != Blocks.AIR && block != Blocks.FIRE || (blocka = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(2, 0, 0)).getBlock()) != Blocks.AIR && blocka != Blocks.FIRE || (blockb = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(2, -1, 0)).getBlock()) != Blocks.OBSIDIAN && blockb != Blocks.BEDROCK || EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(1, 0, 0)).getBlock() == Blocks.BEDROCK)
                    break;
                return true;
            }
            case 3: {
                Block blockb;
                Block blocka;
                BlockPos raytrace = new BlockPos(pos);
                if (!BlockUtil.canBlockBeSeen(raytrace.getX(), raytrace.getY(), raytrace.getZ() - 2) && Math.sqrt(EntityUtil.mc.player.getDistanceSq(raytrace.getX(), raytrace.getY(), raytrace.getZ() - 2)) > 3.0) {
                    return false;
                }
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, -2)).getBlock();
                if (block != Blocks.AIR && block != Blocks.FIRE || (blocka = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, -2)).getBlock()) != Blocks.AIR && blocka != Blocks.FIRE || (blockb = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, -1, -2)).getBlock()) != Blocks.OBSIDIAN && blockb != Blocks.BEDROCK || EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, -1)).getBlock() == Blocks.BEDROCK)
                    break;
                return true;
            }
            case 4: {
                Block blockb;
                Block blocka;
                BlockPos raytrace = new BlockPos(pos);
                if (!BlockUtil.canBlockBeSeen(raytrace.getX(), raytrace.getY(), raytrace.getZ() + 2) && Math.sqrt(EntityUtil.mc.player.getDistanceSq(raytrace.getX(), raytrace.getY(), raytrace.getZ() + 2)) > 3.0) {
                    return false;
                }
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, 2)).getBlock();
                if (block != Blocks.AIR && block != Blocks.FIRE || (blocka = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, 2)).getBlock()) != Blocks.AIR && blocka != Blocks.FIRE || (blockb = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, -1, 2)).getBlock()) != Blocks.OBSIDIAN && blockb != Blocks.BEDROCK || EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, 1)).getBlock() == Blocks.BEDROCK)
                    break;
                return true;
            }
            case 5: {
                BlockPos raytrace = new BlockPos(pos);
                if (!BlockUtil.canBlockBeSeen(raytrace.getX() - 1, raytrace.getY(), raytrace.getZ()) && Math.sqrt(EntityUtil.mc.player.getDistanceSq(raytrace.getX() - 1, raytrace.getY(), raytrace.getZ())) > 3.0) {
                    return false;
                }
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-1, 1, 0)).getBlock();
                if (block != Blocks.AIR && block != Blocks.FIRE || EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-1, 0, 0)).getBlock() == Blocks.BEDROCK)
                    break;
                return true;
            }
            case 6: {
                BlockPos raytrace = new BlockPos(pos);
                if (!BlockUtil.canBlockBeSeen(raytrace.getX() + 1, raytrace.getY(), raytrace.getZ()) && Math.sqrt(EntityUtil.mc.player.getDistanceSq(raytrace.getX() + 1, raytrace.getY(), raytrace.getZ())) > 3.0) {
                    return false;
                }
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(1, 1, 0)).getBlock();
                if (block != Blocks.AIR && block != Blocks.FIRE || EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(1, 0, 0)).getBlock() == Blocks.BEDROCK)
                    break;
                return true;
            }
            case 7: {
                BlockPos raytrace = new BlockPos(pos);
                if (!BlockUtil.canBlockBeSeen(raytrace.getX(), raytrace.getY(), raytrace.getZ() - 1) && Math.sqrt(EntityUtil.mc.player.getDistanceSq(raytrace.getX(), raytrace.getY(), raytrace.getZ() - 1)) > 3.0) {
                    return false;
                }
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, -1)).getBlock();
                if (block != Blocks.AIR && block != Blocks.FIRE || EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, -1)).getBlock() == Blocks.BEDROCK)
                    break;
                return true;
            }
            case 8: {
                BlockPos raytrace = new BlockPos(pos);
                if (!BlockUtil.canBlockBeSeen(raytrace.getX(), raytrace.getY(), raytrace.getZ() + 1) && Math.sqrt(EntityUtil.mc.player.getDistanceSq(raytrace.getX(), raytrace.getY(), raytrace.getZ() + 1)) > 3.0) {
                    return false;
                }
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, 1)).getBlock();
                if (block != Blocks.AIR && block != Blocks.FIRE || EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, 1)).getBlock() == Blocks.BEDROCK)
                    break;
                return true;
            }
        }
        switch (render) {
            case 1: {
                Block blockb;
                Block blocka;
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-2, 1, 0)).getBlock();
                if (block != Blocks.AIR) {
                    if (block != Blocks.FIRE) return false;
                }
                if ((blocka = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-2, 0, 0)).getBlock()) != Blocks.AIR) {
                    if (blocka != Blocks.FIRE) return false;
                }
                if ((blockb = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-2, -1, 0)).getBlock()) != Blocks.OBSIDIAN) {
                    if (blockb != Blocks.BEDROCK) return false;
                }
                return EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-1, 0, 0)).getBlock() != Blocks.BEDROCK;
            }
            case 2: {
                Block blockb;
                Block blocka;
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(2, 1, 0)).getBlock();
                if (block != Blocks.AIR) {
                    if (block != Blocks.FIRE) return false;
                }
                if ((blocka = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(2, 0, 0)).getBlock()) != Blocks.AIR) {
                    if (blocka != Blocks.FIRE) return false;
                }
                if ((blockb = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(2, -1, 0)).getBlock()) != Blocks.OBSIDIAN) {
                    if (blockb != Blocks.BEDROCK) return false;
                }
                return EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(1, 0, 0)).getBlock() != Blocks.BEDROCK;
            }
            case 3: {
                Block blockb;
                Block blocka;
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, -2)).getBlock();
                if (block != Blocks.AIR) {
                    if (block != Blocks.FIRE) return false;
                }
                if ((blocka = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, -2)).getBlock()) != Blocks.AIR) {
                    if (blocka != Blocks.FIRE) return false;
                }
                if ((blockb = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, -1, -2)).getBlock()) != Blocks.OBSIDIAN) {
                    if (blockb != Blocks.BEDROCK) return false;
                }
                return EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, -1)).getBlock() != Blocks.BEDROCK;
            }
            case 4: {
                Block blockb;
                Block blocka;
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, 2)).getBlock();
                if (block != Blocks.AIR) {
                    if (block != Blocks.FIRE) return false;
                }
                if ((blocka = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, 2)).getBlock()) != Blocks.AIR) {
                    if (blocka != Blocks.FIRE) return false;
                }
                if ((blockb = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, -1, 2)).getBlock()) != Blocks.OBSIDIAN) {
                    if (blockb != Blocks.BEDROCK) return false;
                }
                return EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, 1)).getBlock() != Blocks.BEDROCK;
            }
            case 5: {
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-1, 1, 0)).getBlock();
                if (block != Blocks.AIR) {
                    if (block != Blocks.FIRE) return false;
                }
                return EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-1, 0, 0)).getBlock() != Blocks.BEDROCK;
            }
            case 6: {
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(1, 1, 0)).getBlock();
                if (block != Blocks.AIR) {
                    if (block != Blocks.FIRE) return false;
                }
                return EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(1, 0, 0)).getBlock() != Blocks.BEDROCK;
            }
            case 7: {
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, -1)).getBlock();
                if (block != Blocks.AIR) {
                    if (block != Blocks.FIRE) return false;
                }
                return EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, -1)).getBlock() != Blocks.BEDROCK;
            }
            case 8: {
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, 1)).getBlock();
                if (block != Blocks.AIR) {
                    if (block != Blocks.FIRE) return false;
                }
                return EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, 1)).getBlock() != Blocks.BEDROCK;
            }
        }
        return false;
    }

    public static Entity getPredictedPosition(Entity entity, double x) {
        if (x == 0.0) {
            return entity;
        }
        EntityPlayer e = null;
        double motionX = entity.posX - entity.lastTickPosX;
        double motionY = entity.posY - entity.lastTickPosY;
        double motionZ = entity.posZ - entity.lastTickPosZ;
        boolean shouldPredict = false;
        boolean shouldStrafe = false;
        double motion = Math.sqrt(Math.pow(motionX, 2.0) + Math.pow(motionZ, 2.0) + Math.pow(motionY, 2.0));
        if (motion > 0.1) {
            shouldPredict = true;
        }
        if (!shouldPredict) {
            return entity;
        }
        if (motion > 0.31) {
            shouldStrafe = true;
        }
        int i = 0;
        while ((double) i < x) {
            if (e == null) {
                if (isOnGround(0.0, 0.0, 0.0, entity)) {
                    motionY = shouldStrafe ? 0.4 : -0.07840015258789;
                } else {
                    motionY -= 0.08;
                    motionY *= 0.98f;
                }
                e = placeValue(motionX, motionY, motionZ, (EntityPlayer) entity);
            } else {
                if (isOnGround(0.0, 0.0, 0.0, e)) {
                    motionY = shouldStrafe ? 0.4 : -0.07840015258789;
                } else {
                    motionY -= 0.08;
                    motionY *= 0.98f;
                }
                e = placeValue(motionX, motionY, motionZ, e);
            }
            ++i;
        }
        return e;
    }

    public static boolean isOnGround(double height) {
        return !mc.world.getCollisionBoxes(mc.player,
                mc.player.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
    }

    public static boolean isOnGround(double x, double y, double z, Entity entity) {
        double d3 = y;
        List<?> list1 = Minecraft.getMinecraft().world.getCollisionBoxes(entity, entity.getEntityBoundingBox().expand(x, y, z));
        if (y != 0.0) {
            for (Object o : list1) {
                y = ((AxisAlignedBB) o).calculateYOffset(entity.getEntityBoundingBox(), y);
            }
        }
        return d3 != y && d3 < 0.0;
    }

    public static EntityPlayer placeValue(double x, double y, double z, EntityPlayer entity) {
        List<?> list1 = Minecraft.getMinecraft().world.getCollisionBoxes(entity, entity.getEntityBoundingBox().expand(x, y, z));
        if (y != 0.0) {
            int l = list1.size();
            for (Object o : list1) {
                y = ((AxisAlignedBB) o).calculateYOffset(entity.getEntityBoundingBox(), y);
            }
            if (y != 0.0) {
                entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(0.0, y, 0.0));
            }
        }
        if (x != 0.0) {
            int l5 = list1.size();
            for (Object o : list1) {
                x = calculateXOffset(entity.getEntityBoundingBox(), x, (AxisAlignedBB) o);
            }
            if (x != 0.0) {
                entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(x, 0.0, 0.0));
            }
        }
        if (z != 0.0) {
            int i6 = list1.size();
            for (Object o : list1) {
                z = calculateZOffset(entity.getEntityBoundingBox(), z, (AxisAlignedBB) o);
            }
            if (z != 0.0) {
                entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(0.0, 0.0, z));
            }
        }
        return entity;
    }

    public static double calculateXOffset(AxisAlignedBB other, double offsetX, AxisAlignedBB this1) {
        if (other.maxY > this1.minY && other.minY < this1.maxY && other.maxZ > this1.minZ && other.minZ < this1.maxZ) {
            double d0;
            if (offsetX > 0.0 && other.maxX <= this1.minX) {
                double d1 = this1.minX - other.maxX;
                if (d1 < offsetX) {
                    offsetX = d1;
                }
            } else if (offsetX < 0.0 && other.minX >= this1.maxX && (d0 = this1.maxX - other.minX) > offsetX) {
                offsetX = d0;
            }
        }
        return offsetX;
    }

    public static double calculateZOffset(AxisAlignedBB other, double offsetZ, AxisAlignedBB this1) {
        if (other.maxX > this1.minX && other.minX < this1.maxX && other.maxY > this1.minY && other.minY < this1.maxY) {
            double d0;
            if (offsetZ > 0.0 && other.maxZ <= this1.minZ) {
                double d1 = this1.minZ - other.maxZ;
                if (d1 < offsetZ) {
                    offsetZ = d1;
                }
            } else if (offsetZ < 0.0 && other.minZ >= this1.maxZ && (d0 = this1.maxZ - other.minZ) > offsetZ) {
                offsetZ = d0;
            }
        }
        return offsetZ;
    }

    public static EntityPlayer getClosestEnemy(double distance) {
        EntityPlayer closest = null;
        try {
            for (EntityPlayer player : mc.world.playerEntities) {
                if (isntValid(player, distance)) continue;
                if (closest == null) {
                    closest = player;
                    continue;
                }
                if (!(mc.player.getDistanceSq(player) < mc.player.getDistanceSq(closest))) continue;
                closest = player;
            }
        } catch (Exception ignored) {
        }
        return closest;
    }


    public static double GetDistanceOfEntityToBlock(final Entity p_Entity, final BlockPos p_Pos) {
        return GetDistance(p_Entity.posX, p_Entity.posY, p_Entity.posZ, p_Pos.getX(), p_Pos.getY(), p_Pos.getZ());
    }

    public static double GetDistance(final double p_X, final double p_Y, final double p_Z, final double x, final double y, final double z) {
        final double d0 = p_X - x;
        final double d2 = p_Y - y;
        final double d3 = p_Z - z;
        return MathHelper.sqrt(d0 * d0 + d2 * d2 + d3 * d3);
    }

    public static boolean isCrystalAtFeet(EntityEnderCrystal crystal, double range) {
        for (EntityPlayer player : mc.world.playerEntities) {
            if (mc.player.getDistanceSq(player) > range * range || FriendManager.isFriend(player.getName())) continue;
            for (Vec3d vec : doubleLegOffsetList) {
                if (new BlockPos(player.getPositionVector()).add(vec.x, vec.y, vec.z) != crystal.getPosition())
                    continue;
                return true;
            }
        }
        return false;
    }

    public static boolean stopSneaking(final boolean isSneaking) {
        if (isSneaking && mc.player != null) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
        return false;
    }

    public static BlockPos getRoundedBlockPos(final Entity entity) {
        if (entity != null) {
            return new BlockPos(MathUtil.roundVec(entity.getPositionVector(), 0));
        } else {
            return null;
        }
    }

    public static boolean isTrapped(final EntityPlayer player, final boolean antiScaffold, final boolean antiStep, final boolean legs, final boolean platform, final boolean antiDrop) {
        return getUntrappedBlocks(player, antiScaffold, antiStep, legs, platform, antiDrop).size() == 0;
    }

    public static List<Vec3d> getUntrappedBlocks(final EntityPlayer player, final boolean antiScaffold, final boolean antiStep, final boolean legs, final boolean platform, final boolean antiDrop) {
        final ArrayList<Vec3d> vec3ds = new ArrayList<Vec3d>();
        if (!antiStep && getUnsafeBlocks(player, 2, false).size() == 4) {
            vec3ds.addAll(getUnsafeBlocks(player, 2, false));
        }
        for (int i = 0; i < getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop).length; ++i) {
            final Vec3d vector = getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop)[i];
            final BlockPos targetPos = new BlockPos(player.getPositionVector()).add(vector.x, vector.y, vector.z);
            final Block block = mc.world.getBlockState(targetPos).getBlock();
            if (block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow) {
                vec3ds.add(vector);
            }
        }
        return vec3ds;
    }

    public static List<Vec3d> getOffsetList(final int y, final boolean floor) {
        final ArrayList<Vec3d> offsets = new ArrayList<Vec3d>();
        offsets.add(new Vec3d(-1.0, y, 0.0));
        offsets.add(new Vec3d(1.0, y, 0.0));
        offsets.add(new Vec3d(0.0, y, -1.0));
        offsets.add(new Vec3d(0.0, y, 1.0));
        if (floor) {
            offsets.add(new Vec3d(0.0, y - 1, 0.0));
        }
        return offsets;
    }

    public static List<Vec3d> getTrapOffsetsList(final boolean antiScaffold, final boolean antiStep, final boolean legs, final boolean platform, final boolean antiDrop) {
        final ArrayList<Vec3d> offsets = new ArrayList<Vec3d>(getOffsetList(1, false));
        offsets.add(new Vec3d(0.0, 2.0, 0.0));
        if (antiScaffold) {
            offsets.add(new Vec3d(0.0, 3.0, 0.0));
        }
        if (antiStep) {
            offsets.addAll(getOffsetList(2, false));
        }
        if (legs) {
            offsets.addAll(getOffsetList(0, false));
        }
        if (platform) {
            offsets.addAll(getOffsetList(-1, false));
            offsets.add(new Vec3d(0.0, -1.0, 0.0));
        }
        if (antiDrop) {
            offsets.add(new Vec3d(0.0, -2.0, 0.0));
        }
        return offsets;
    }

    public static Vec3d[] getTrapOffsets(final boolean antiScaffold, final boolean antiStep, final boolean legs, final boolean platform, final boolean antiDrop) {
        final List<Vec3d> offsets = getTrapOffsetsList(antiScaffold, antiStep, legs, platform, antiDrop);
        final Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }

    public static boolean isTrappedExtended(final int extension, final EntityPlayer player, final boolean antiScaffold, final boolean antiStep, final boolean legs, final boolean platform, final boolean antiDrop, final boolean raytrace) {
        return getUntrappedBlocksExtended(extension, player, antiScaffold, antiStep, legs, platform, antiDrop, raytrace).size() == 0;
    }

    public static List<Vec3d> getUntrappedBlocksExtended(final int extension, final EntityPlayer player, final boolean antiScaffold, final boolean antiStep, final boolean legs, final boolean platform, final boolean antiDrop, final boolean raytrace) {
        final ArrayList<Vec3d> placeTargets = new ArrayList<Vec3d>();
        if (extension == 1) {
            placeTargets.addAll(targets(player.getPositionVector(), antiScaffold, antiStep, legs, platform, antiDrop, raytrace));
        } else {
            int extend = 1;
            for (final Vec3d vec3d : MathUtil.getBlockBlocks(player)) {
                if (extend > extension) {
                    break;
                }
                placeTargets.addAll(targets(vec3d, antiScaffold, antiStep, legs, platform, antiDrop, raytrace));
                ++extend;
            }
        }
        final ArrayList<Vec3d> removeList = new ArrayList<Vec3d>();
        for (final Vec3d vec3d : placeTargets) {
            final BlockPos pos = new BlockPos(vec3d);
            if (BlockUtil.isPositionPlaceable(pos, raytrace) != -1) {
                continue;
            }
            removeList.add(vec3d);
        }
        for (final Vec3d vec3d : removeList) {
            placeTargets.remove(vec3d);
        }
        return placeTargets;
    }

    public static List<Vec3d> targets(final Vec3d vec3d, final boolean antiScaffold, final boolean antiStep, final boolean legs, final boolean platform, final boolean antiDrop, final boolean raytrace) {
        final ArrayList<Vec3d> placeTargets = new ArrayList<Vec3d>();
        if (antiDrop) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiDropOffsetList));
        }
        if (platform) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, platformOffsetList));
        }
        if (legs) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, legOffsetList));
        }
        Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, OffsetList));
        if (antiStep) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiStepOffsetList));
        } else {
            final List<Vec3d> vec3ds = getUnsafeBlocksFromVec3d(vec3d, 2, false);
            if (vec3ds.size() == 4) {
                for (final Vec3d vector : vec3ds) {
                    final BlockPos position = new BlockPos(vec3d).add(vector.x, vector.y, vector.z);
                    switch (BlockUtil.isPositionPlaceable(position, raytrace)) {
                        case -1:
                        case 1:
                        case 2: {
                            continue;
                        }
                        case 3: {
                            placeTargets.add(vec3d.add(vector));
                            break;
                        }
                    }
                    if (antiScaffold) {
                        Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList));
                    }
                    return placeTargets;
                }
            }
        }
        if (antiScaffold) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList));
        }
        return placeTargets;
    }

    public static Color getColor(final Entity entity, final int red, final int green, final int blue, final int alpha, final boolean colorFriends) {
        Color color = new Color(red / 255.0f, green / 255.0f, blue / 255.0f, alpha / 255.0f);
        if (entity instanceof EntityPlayer) {
            if (colorFriends && FriendManager.isFriend(entity.getName())) {
                color = new Color(0.33333334f, 1.0f, 1.0f, alpha / 255.0f);
            }
        }
        return color;
    }

    public static boolean isPassive(final Entity e) {
        return (!(e instanceof EntityWolf) || !((EntityWolf) e).isAngry()) && (e instanceof EntityAnimal || e instanceof EntityAgeable || e instanceof EntityTameable || e instanceof EntityAmbientCreature || e instanceof EntitySquid || (e instanceof EntityIronGolem && ((EntityIronGolem) e).getRevengeTarget() == null));
    }

    public static boolean isAboveBlock(final Entity entity, final BlockPos blockPos) {
        return entity.posY >= blockPos.getY();
    }

    public static boolean checkForLiquid(final Entity entity, final boolean b) {
        if (entity == null) {
            return false;
        }
        final double posY = entity.posY;
        double n;
        if (b) {
            n = 0.03;
        } else if (entity instanceof EntityPlayer) {
            n = 0.2;
        } else {
            n = 0.5;
        }
        final double n2 = posY - n;
        for (int i = MathHelper.floor(entity.posX); i < MathHelper.ceil(entity.posX); ++i) {
            for (int j = MathHelper.floor(entity.posZ); j < MathHelper.ceil(entity.posZ); ++j) {
                if (mc.world.getBlockState(new BlockPos(i, MathHelper.floor(n2), j)).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkCollide() {
        return !mc.player.isSneaking() && (mc.player.getRidingEntity() == null || mc.player.getRidingEntity().fallDistance < 3.0f) && mc.player.fallDistance < 3.0f;
    }

    public static boolean isAboveLiquid(final Entity entity) {
        if (entity == null) {
            return false;
        }
        final double n = entity.posY + 0.01;
        for (int i = MathHelper.floor(entity.posX); i < MathHelper.ceil(entity.posX); ++i) {
            for (int j = MathHelper.floor(entity.posZ); j < MathHelper.ceil(entity.posZ); ++j) {
                if (mc.world.getBlockState(new BlockPos(i, (int) n, j)).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean nullCheck() {
        return (Wrapper.getPlayer() == null || Wrapper.getWorld() == null);
    }

    public static boolean isInLiquid() {
        if (mc.player.fallDistance >= 3.0f) {
            return false;
        }
        boolean inLiquid = false;
        final AxisAlignedBB bb = (mc.player.getRidingEntity() != null) ? mc.player.getRidingEntity().getEntityBoundingBox() : mc.player.getEntityBoundingBox();
        final int y = (int) bb.minY;
        for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; ++x) {
            for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; ++z) {
                final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (!(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    inLiquid = true;
                }
            }
        }
        return inLiquid;
    }

    public static boolean isOnLiquid(final double offset) {
        if (mc.player.fallDistance >= 3.0f) {
            return false;
        }
        final AxisAlignedBB bb = (mc.player.getRidingEntity() != null) ? mc.player.getRidingEntity().getEntityBoundingBox().contract(0.0, 0.0, 0.0).offset(0.0, -offset, 0.0) : mc.player.getEntityBoundingBox().contract(0.0, 0.0, 0.0).offset(0.0, -offset, 0.0);
        boolean onLiquid = false;
        final int y = (int) bb.minY;
        for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0); ++x) {
            for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0); ++z) {
                final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != Blocks.AIR) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }

    public static boolean isOnLiquid() {
        final double y = mc.player.posY - 0.03;
        for (int x = MathHelper.floor(mc.player.posX); x < MathHelper.ceil(mc.player.posX); ++x) {
            for (int z = MathHelper.floor(mc.player.posZ); z < MathHelper.ceil(mc.player.posZ); ++z) {
                final BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
                if (mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isMoving() {
        return mc.player.moveForward != 0.0 || mc.player.moveStrafing != 0.0;
    }

    public static boolean isChasing() {
        return mc.player.moveForward >= 0.3 || mc.player.moveStrafing >= 0.3;
    }

    public static void attackEntity(Entity entity, boolean packet) {
        if (packet) {
            mc.player.connection.sendPacket(new CPacketUseEntity(entity));
        } else {
            mc.playerController.attackEntity(mc.player, entity);
        }
    }

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time);
    }

    public static boolean isLiving(final Entity e) {
        return e instanceof EntityLivingBase;
    }

    public static boolean isFakeLocalPlayer(final Entity entity) {
        return entity != null && entity.getEntityId() == -100 && Wrapper.getPlayer() != entity;
    }

    public static Vec3d getInterpolatedAmount(final Entity entity, final double x, final double y, final double z) {
        return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
    }

    public static Vec3d getInterpolatedAmount(final Entity entity, final Vec3d vec) {
        return getInterpolatedAmount(entity, vec.x, vec.y, vec.z);
    }

    public static boolean isAlive(Entity entity) {
        return (isLiving(entity) && !entity.isDead && ((EntityLivingBase) entity).getHealth() > 0.0F);
    }

    public static boolean isDead(Entity entity) {
        return !isAlive(entity);
    }

    public static boolean isntValid(Entity entity, double range) {
        return (entity == null || isDead(entity) || (entity instanceof EntityPlayer && FriendManager.isFriend(entity.getName())));
    }

    public static boolean isValid(Entity entity, double range) {
        return !isntValid(entity, range);
    }

    public static boolean isSafe(Entity entity, int height, boolean floor) {
        return (getUnsafeBlocks(entity, height, floor).size() == 0);
    }

    public static boolean isSafe(Entity entity) {
        return isSafe(entity, 0, false);
    }

    public static int GetItemInHotbar(Item Item) {
        for (int I = 0; I < 9; ++I) {
            ItemStack l_Stack = mc.player.inventory.getStackInSlot(I);

            if (l_Stack != ItemStack.EMPTY) {
                if (l_Stack.getItem() == Item) {
                    return I;
                }
            }
        }
        return -1;
    }

    public static int GetItemSlotInHotbar(Block web) {
        for (int l_I = 0; l_I < 9; ++l_I) {
            ItemStack l_Stack = mc.player.inventory.getStackInSlot(l_I);

            if (l_Stack != ItemStack.EMPTY) {
                if (l_Stack.getItem() instanceof ItemBlock) {
                    ItemBlock block = (ItemBlock) l_Stack.getItem();

                    if (block.getBlock().equals(web))
                        return l_I;
                }
            }
        }

        return -1;
    }

    public static boolean IsPlayerInHole(EntityPlayer who) {
        BlockPos blockPos = new BlockPos(Math.floor(who.posX), Math.floor(who.posY), Math.floor(who.posZ));

        IBlockState blockState = mc.world.getBlockState(blockPos);

        if (blockState.getBlock() != Blocks.AIR)
            return false;

        if (mc.world.getBlockState(blockPos.up()).getBlock() != Blocks.AIR)
            return false;

        if (mc.world.getBlockState(blockPos.down()).getBlock() == Blocks.AIR)
            return false;

        final BlockPos[] touchingBlocks = new BlockPos[]
                {blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west()};

        int validHorizontalBlocks = 0;
        for (BlockPos touching : touchingBlocks) {
            final IBlockState touchingState = mc.world.getBlockState(touching);
            if ((touchingState.getBlock() != Blocks.AIR) && touchingState.isFullBlock())
                validHorizontalBlocks++;
        }

        return validHorizontalBlocks >= 4;
    }

    public static BlockPos GetLocalPlayerPosFloored() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public static boolean IsPlayerInHole() {
        BlockPos blockPos = GetLocalPlayerPosFloored();

        IBlockState blockState = mc.world.getBlockState(blockPos);

        if (blockState.getBlock() != Blocks.AIR)
            return false;

        if (mc.world.getBlockState(blockPos.up()).getBlock() != Blocks.AIR)
            return false;

        if (mc.world.getBlockState(blockPos.down()).getBlock() == Blocks.AIR)
            return false;

        final BlockPos[] touchingBlocks = new BlockPos[]
                {blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west()};

        int validHorizontalBlocks = 0;
        for (BlockPos touching : touchingBlocks) {
            final IBlockState touchingState = mc.world.getBlockState(touching);
            if ((touchingState.getBlock() != Blocks.AIR) && touchingState.isFullBlock())
                validHorizontalBlocks++;
        }

        return validHorizontalBlocks >= 4;
    }

    public static boolean isPlayerInHole(Block block) {
        BlockPos blockPos = GetLocalPlayerPosFloored();

        final BlockPos[] touchingBlocks = new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west()};

        int validHorizontalBlocks = 0;
        for (BlockPos touching : touchingBlocks) {
            final IBlockState touchingState = mc.world.getBlockState(touching);
            if (touchingState.getBlock() != Blocks.AIR && touchingState.isFullBlock()) {
                if (block.equals(Blocks.OBSIDIAN)) {
                    if (touchingState.getBlock().equals(Blocks.OBSIDIAN) || touchingState.getBlock().equals(Blocks.BEDROCK)) {
                        validHorizontalBlocks++;
                    }
                } else if (touchingState.getBlock().equals(block)) validHorizontalBlocks++;
            }
        }
        return validHorizontalBlocks >= 4;
    }

    public static List<Vec3d> getUnsafeBlocks(Entity entity, int height, boolean floor) {
        return getUnsafeBlocksFromVec3d(entity.getPositionVector(), height, floor);
    }

    public static Vec3d[] getUnsafeBlockArray(Entity entity, int height, boolean floor) {
        List<Vec3d> list = getUnsafeBlocks(entity, height, floor);
        Vec3d[] array = new Vec3d[list.size()];
        return list.toArray(array);
    }

    public static Vec3d[] getUnsafeBlockArrayFromVec3d(Vec3d pos, int height, boolean floor) {
        List<Vec3d> list = getUnsafeBlocksFromVec3d(pos, height, floor);
        Vec3d[] array = new Vec3d[list.size()];
        return list.toArray(array);
    }

    public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor) {
        List<Vec3d> vec3ds = new ArrayList<>();
        return vec3ds;
    }

    public static Vec3d getInterpolatedAmount(final Entity entity, final double ticks) {
        return getInterpolatedAmount(entity, ticks, ticks, ticks);
    }

    public static boolean isMobAggressive(final Entity entity) {
        if (entity instanceof EntityPigZombie) {
            if (((EntityPigZombie) entity).isArmsRaised() || ((EntityPigZombie) entity).isAngry()) {
                return true;
            }
        } else {
            if (entity instanceof EntityWolf) {
                return ((EntityWolf) entity).isAngry() && !Wrapper.getPlayer().equals(((EntityWolf) entity).getOwner());
            }
            if (entity instanceof EntityEnderman) {
                return ((EntityEnderman) entity).isScreaming();
            }
        }
        return isHostileMob(entity);
    }

    public static boolean isNeutralMob(final Entity entity) {
        return entity instanceof EntityPigZombie || entity instanceof EntityWolf || entity instanceof EntityEnderman;
    }

    public static boolean isFriendlyMob(final Entity entity) {
        return (entity.isCreatureType(EnumCreatureType.CREATURE, false) && !isNeutralMob(entity)) || entity.isCreatureType(EnumCreatureType.AMBIENT, false) || entity instanceof EntityVillager || entity instanceof EntityIronGolem || (isNeutralMob(entity) && !isMobAggressive(entity));
    }

    public static boolean isHostileMob(final Entity entity) {
        return entity.isCreatureType(EnumCreatureType.MONSTER, false) && !isNeutralMob(entity);
    }

    public static Vec3d getInterpolatedPos(final Entity entity, final float ticks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, ticks));
    }

    public static Vec3d getInterpolatedRenderPos(final Entity entity, final float ticks) {
        return getInterpolatedPos(entity, ticks).subtract(Wrapper.getMinecraft().getRenderManager().renderPosX, Wrapper.getMinecraft().getRenderManager().renderPosY, Wrapper.getMinecraft().getRenderManager().renderPosZ);
    }

    public static Vec3d getInterpolatedRenderPos(BlockPos pos) {
        return new Vec3d(pos.x, pos.y, pos.z).subtract(Wrapper.getMinecraft().getRenderManager().renderPosX, Wrapper.getMinecraft().getRenderManager().renderPosY, Wrapper.getMinecraft().getRenderManager().renderPosZ);
    }


    public static boolean isInWater(final Entity entity) {
        if (entity == null) {
            return false;
        }
        final double y = entity.posY + 0.01;
        for (int x = MathHelper.floor(entity.posX); x < MathHelper.ceil(entity.posX); ++x) {
            for (int z = MathHelper.floor(entity.posZ); z < MathHelper.ceil(entity.posZ); ++z) {
                final BlockPos pos = new BlockPos(x, (int) y, z);
                if (Wrapper.getWorld().getBlockState(pos).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isDrivenByPlayer(final Entity entityIn) {
        return Wrapper.getPlayer() != null && entityIn != null && entityIn.equals(Wrapper.getPlayer().getRidingEntity());
    }

    public static boolean isAboveWater(final Entity entity) {
        return isAboveWater(entity, false);
    }

    public static boolean isAboveWater(final Entity entity, final boolean packet) {
        if (entity == null) {
            return false;
        }
        final double y = entity.posY - (packet ? 0.03 : (isPlayer(entity) ? 0.2 : 0.5));
        for (int x = MathHelper.floor(entity.posX); x < MathHelper.ceil(entity.posX); ++x) {
            for (int z = MathHelper.floor(entity.posZ); z < MathHelper.ceil(entity.posZ); ++z) {
                final BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
                if (Wrapper.getWorld().getBlockState(pos).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isArmorLow(EntityPlayer player, int durability) {
        for (ItemStack piece : player.inventory.armorInventory) {
            if (piece == null)
                return true;
            if (getItemDamage(piece) < durability)
                return true;
        }
        return false;
    }

    public static int getItemDamage(ItemStack stack) {
        return stack.getMaxDamage() - stack.getItemDamage();
    }

    public static float getDamageInPercent(ItemStack stack) {
        return getItemDamage(stack) / stack.getMaxDamage() * 100.0F;
    }

    public static boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
            return ((mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || mc.world
                    .getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && mc.world
                    .getBlockState(boost).getBlock() == Blocks.AIR && mc.world
                    .getBlockState(boost2).getBlock() == Blocks.AIR && mc.world
                    .getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.world
                    .getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty());
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean canPlaceCrystal(BlockPos blockPos, boolean specialEntityCheck, boolean oneDot15) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
            ChunkCache mc = null;
            if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN)
                return false;
            if ((mc.world.getBlockState(boost).getBlock() != Blocks.AIR || mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) && !oneDot15)
                return false;
            if (specialEntityCheck) {
                for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                    if (!(entity instanceof net.minecraft.entity.item.EntityEnderCrystal))
                        return false;
                }
                if (!oneDot15)
                    for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                        if (!(entity instanceof net.minecraft.entity.item.EntityEnderCrystal))
                            return false;
                    }
            } else {
                return (mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && (oneDot15 || mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty()));
            }
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    public static List<BlockPos> possiblePlacePositions(float placeRange, boolean specialEntityCheck, boolean oneDot15) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphere(getPlayerPos(mc.player), placeRange, (int) placeRange, false, true, 0).stream().filter(pos -> canPlaceCrystal(pos, specialEntityCheck, oneDot15)).collect(Collectors.toList()));
        return positions;
    }

    public static List<BlockPos> getBlockSphere(float breakRange, Class<?> clazz) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphere(getPlayerPos(mc.player), breakRange, (int) breakRange, false, true, 0).stream().filter(pos -> clazz.isInstance(mc.world.getBlockState(pos).getBlock())).collect(Collectors.toList()));
        return positions;
    }

    public static List<BlockPos> possiblePlacePositions(float placeRange) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphere(getPlayerPos(mc.player), placeRange, (int) placeRange, false, true, 0).stream().filter(EntityUtil::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

    public static BlockPos getPlayerPos(EntityPlayer player) {
        return new BlockPos(Math.floor(player.posX), Math.floor(player.posY), Math.floor(player.posZ));
    }

    public static BlockPos getPlayerPos(double pY) {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY + pY), Math.floor(mc.player.posZ));
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
    }

    public static List<BlockPos> getDisc(BlockPos pos, float r) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                double dist = ((cx - x) * (cx - x) + (cz - z) * (cz - z));
                if (dist < (r * r)) {
                    BlockPos position = new BlockPos(x, cy, z);
                    circleblocks.add(position);
                }
            }
        }
        return circleblocks;
    }

    public static List<BlockPos> getSphere(BlockPos pos, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; ) {
                int y = sphere ? (cy - (int) r) : cy;
                for (; ; z++) {
                    if (y < (sphere ? (cy + r) : (cy + h))) {
                        double dist = ((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0));
                        if (dist < (r * r) && (!hollow || dist >= ((r - 1.0F) * (r - 1.0F)))) {
                            BlockPos l = new BlockPos(x, y + plus_y, z);
                            circleblocks.add(l);
                        }
                        y++;
                        continue;
                    }
                }
            }
        }
        return circleblocks;
    }

    private static Vec3d getEyesPos() {
        return new Vec3d(Wrapper.getPlayer().posX, Wrapper.getPlayer().posY + Wrapper.getPlayer().getEyeHeight(), Wrapper.getPlayer().posZ);
    }

    public static boolean isInHole(final Entity entity) {
        return isBlockValid(new BlockPos(entity.posX, entity.posY, entity.posZ));
    }

    public static boolean isBlockValid(final BlockPos blockPos) {
        return isBedrockHole(blockPos) || isObbyHole(blockPos) || isBothHole(blockPos);
    }

    public static boolean holdingWeapon(final EntityPlayer player) {
        return player.getHeldItemMainhand().getItem() instanceof ItemSword || player.getHeldItemMainhand().getItem() instanceof ItemAxe;
    }

    public static boolean isObbyHole(final BlockPos blockPos) {
        final BlockPos[] array = new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()};
        for (BlockPos pos : array) {
            final IBlockState touchingState = mc.world.getBlockState(pos);
            if (touchingState.getBlock() == Blocks.AIR || touchingState.getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
        }
        return true;
    }

    public static boolean isBedrockHole(final BlockPos blockPos) {
        final BlockPos[] array = new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()};
        for (final BlockPos pos : array) {
            final IBlockState touchingState = mc.world.getBlockState(pos);
            if (touchingState.getBlock() == Blocks.AIR || touchingState.getBlock() != Blocks.BEDROCK) {
                return false;
            }
        }
        return true;
    }

    public static boolean isBothHole(final BlockPos blockPos) {
        final BlockPos[] array = new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()};
        for (final BlockPos pos : array) {
            final IBlockState touchingState = mc.world.getBlockState(pos);
            if (touchingState.getBlock() == Blocks.AIR || (touchingState.getBlock() != Blocks.BEDROCK && touchingState.getBlock() != Blocks.OBSIDIAN)) {
                return false;
            }
        }
        return true;
    }

    public static double[] calculateLookAt(final double n, final double n2, final double n3, final EntityPlayer entityPlayer) {
        final Vec3d eyesPos = getEyesPos();
        final double n4 = n - eyesPos.x;
        final double n5 = n2 - eyesPos.y;
        final double n6 = n3 - eyesPos.z;
        return new double[]{Wrapper.getPlayer().rotationYaw + MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(n6, n4)) - 90.0f - Wrapper.getPlayer().rotationYaw), Wrapper.getPlayer().rotationPitch + MathHelper.wrapDegrees((float) (-Math.toDegrees(Math.atan2(n5, Math.sqrt(n4 * n4 + n6 * n6)))) - Wrapper.getPlayer().rotationPitch)};
    }

    public static double[] lookxp(final EntityPlayer gay) {
        double diry = gay.posY;
        final double len = Math.sqrt(diry);
        diry /= len;
        double pitch = Math.asin(diry);
        pitch = 90.0;
        return new double[]{pitch};
    }

    public static float[] calcAngle(final Vec3d from, final Vec3d to) {
        final double difX = to.x - from.x;
        final double difY = (to.y - from.y) * -1.0;
        final double difZ = to.z - from.z;
        final double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
    }

    public static double[] wdnmd(float yaw, float pitch) {
        mc.player.rotationYaw = yaw;
        mc.player.rotationYawHead = yaw;
        mc.player.rotationPitch = pitch;
        pitch = (float) (pitch * 180.0 / 3.141592653589793);
        yaw = (float) (yaw * 180.0 / 3.141592653589793);
        yaw += 90.0;
        return new double[]{yaw, pitch};
    }

    public static boolean canPlaceCrystal(BlockPos pos, boolean ignoreCrystals, boolean noBoost2, List<Entity> entities) {
        if (mc.world.getBlockState(pos).getBlock() != Blocks.OBSIDIAN && mc.world.getBlockState(pos).getBlock() != Blocks.BEDROCK) {
            return false;
        }

        BlockPos boost = pos.up();

        if (mc.world.getBlockState(boost).getBlock() != Blocks.AIR || !checkEntityList(boost, ignoreCrystals, entities)) {
            return false;
        }

        if (!noBoost2) {
            BlockPos boost2 = boost.up();

            if (mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) {
                return false;
            }

            return checkEntityList(boost2, ignoreCrystals, entities);
        }

        return true;
    }

    public static boolean checkEntityList(BlockPos pos, boolean ignoreCrystals, List<Entity> entities) {
        if (entities == null) {
            return checkEntities(pos, ignoreCrystals);
        }

        for (Entity entity : entities) {
            if (entity != null && !(isDead(entity) || ignoreCrystals && entity instanceof EntityEnderCrystal)) {
                if (entity.getEntityBoundingBox().intersects(new AxisAlignedBB(pos))) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean checkEntities(BlockPos pos, boolean ignoreCrystals) {
        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (isDead(entity) || ignoreCrystals && entity instanceof EntityEnderCrystal) {
                continue;
            }

            return false;
        }

        return true;
    }

    public static boolean isPlayer(final Entity entity) {
        return entity instanceof EntityPlayer;
    }

    public static double getRelativeX(final float yaw) {
        return MathHelper.sin(-yaw * 0.017453292f);
    }

    public static double getRelativeZ(final float yaw) {
        return MathHelper.cos(yaw * 0.017453292f);
    }

    public static String getNameFromUUID(String uuid) {
        try {
            Kura.logger.info("Attempting to get name from UUID: " + uuid);

            String jsonUrl = IOUtils.toString(new URL("https://api.mojang.com/user/profiles/" + uuid.replace("-", "") + "/names"));

            JsonParser parser = new JsonParser();

            return parser.parse(jsonUrl).getAsJsonArray().get(parser.parse(jsonUrl).getAsJsonArray().size() - 1).getAsJsonObject().get("name").toString();
        } catch (IOException ex) {
            Kura.logger.error(ex.getStackTrace());

            Kura.logger.error("Failed to get username from UUID due to an exception. Maybe your internet is being the big gay? Somehow?");
        }
        return null;
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck, float height) {
        return (!shouldCheck || mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX(), (pos.getY() + height), pos.getZ()), false, true, false) == null);
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck) {
        return rayTracePlaceCheck(pos, shouldCheck, 1.0F);
    }

    public static boolean rayTracePlaceCheck(BlockPos pos) {
        return rayTracePlaceCheck(pos, true);
    }

    public static float getHealth(Entity entity) {
        if (isLiving(entity)) {
            EntityLivingBase livingBase = (EntityLivingBase) entity;
            return livingBase.getHealth() + livingBase.getAbsorptionAmount();
        }
        return 0.0F;
    }

    public static float getHealth(Entity entity, boolean absorption) {
        if (isLiving(entity)) {
            EntityLivingBase livingBase = (EntityLivingBase) entity;
            return livingBase.getHealth() + (absorption ? livingBase.getAbsorptionAmount() : 0.0F);
        }
        return 0.0F;
    }

    public static BlockPos[] possiblePlacePositions(Integer value, boolean booleanValue) {
        return null;
    }


    public static Vec3d[] getOffsets(int y, boolean floor, boolean face) {
        List<Vec3d> offsets = getOffsetList(y, floor, face);
        Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }

    public static List<Vec3d> getOffsetList(int y, boolean floor, boolean face) {
        ArrayList<Vec3d> offsets = new ArrayList<Vec3d>();
        if (face) {
            offsets.add(new Vec3d(-1.0, y, 0.0));
            offsets.add(new Vec3d(1.0, y, 0.0));
            offsets.add(new Vec3d(0.0, y, -1.0));
            offsets.add(new Vec3d(0.0, y, 1.0));
        } else {
            offsets.add(new Vec3d(-1.0, y, 0.0));
        }
        if (floor) {
            offsets.add(new Vec3d(0.0, y - 1, 0.0));
        }
        return offsets;
    }

    public static String getPlayerName(EntityPlayer player) {
        return player.getGameProfile() != null ?
                player.getGameProfile().getName() : player.getName();
    }

    public static List<Entity> getEntityList() {
        return Wrapper.getWorld().getLoadedEntityList();
    }

    public static boolean isSafe(Entity entity, int height, boolean floor, boolean face) {
        return getUnsafeBlocks(entity, height, floor, face).size() == 0;
    }

    public static List<Vec3d> getUnsafeBlocks(Entity entity, int height, boolean floor, boolean face) {
        return getUnsafeBlocksFromVec3d(entity.getPositionVector(), height, floor, face);
    }

    public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor, boolean face) {
        ArrayList<Vec3d> vec3ds = new ArrayList<Vec3d>();
        for (Vec3d vector : getOffsets(height, floor, face)) {
            BlockPos targetPos = new BlockPos(pos).add(vector.x, vector.y, vector.z);
            Block block = mc.world.getBlockState(targetPos).getBlock();
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow))
                continue;
            vec3ds.add(vector);
        }
        return vec3ds;
    }

    public static boolean canEntityFeetBeSeen(final Entity entityIn) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posX + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(entityIn.posX, entityIn.posY, entityIn.posZ), false, true, false) == null;
    }

    public static Vec3d[] getVarOffsets(final int x, final int y, final int z) {
        final List<Vec3d> offsets = getVarOffsetList(x, y, z);
        final Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }

    public static List<Vec3d> getVarOffsetList(final int x, final int y, final int z) {
        final ArrayList<Vec3d> offsets = new ArrayList<>();
        offsets.add(new Vec3d(x, y, z));
        return offsets;
    }

    public static void autoCenter() {
        try{
            BlockPos centerPos = mc.player.getPosition();
            double y = centerPos.getY();
            double x = centerPos.getX();
            double z = centerPos.getZ();

            Vec3d plusPlus = new Vec3d(x + 0.5, y, z + 0.5);
            Vec3d plusMinus = new Vec3d(x + 0.5, y, z - 0.5);
            Vec3d minusMinus = new Vec3d(x - 0.5, y, z - 0.5);
            Vec3d minusPlus = new Vec3d(x - 0.5, y, z + 0.5);
            if (getDst(plusPlus) < getDst(plusMinus) && getDst(plusPlus) < getDst(minusMinus) && getDst(plusPlus) < getDst(minusPlus)) {
                x = centerPos.getX() + 0.5;
                z = centerPos.getZ() + 0.5;
                centerPlayer(x, y, z);
            }
            if (getDst(plusMinus) < getDst(plusPlus) && getDst(plusMinus) < getDst(minusMinus) && getDst(plusMinus) < getDst(minusPlus)) {
                x = centerPos.getX() + 0.5;
                z = centerPos.getZ() - 0.5;
                centerPlayer(x, y, z);
            }
            if (getDst(minusMinus) < getDst(plusPlus) && getDst(minusMinus) < getDst(plusMinus) && getDst(minusMinus) < getDst(minusPlus)) {
                x = centerPos.getX() - 0.5;
                z = centerPos.getZ() - 0.5;
                centerPlayer(x, y, z);
            }
            if (getDst(minusPlus) < getDst(plusPlus) && getDst(minusPlus) < getDst(plusMinus) && getDst(minusPlus) < getDst(minusMinus)) {
                x = centerPos.getX() - 0.5;
                z = centerPos.getZ() + 0.5;
                centerPlayer(x, y, z);
            }
        }catch (Exception e){
//            SB
        }

    }

    public static double getDst(Vec3d vec) {
        return mc.player.getPositionVector().distanceTo(vec);
    }

    public static void centerPlayer(BlockPos pos) {
        centerPlayer(pos.getX(), pos.getY(), pos.getZ());
    }

    public static void centerPlayer(double x, double y, double z) {
        mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, true));
        mc.player.setPosition(x, y, z);
    }
}
