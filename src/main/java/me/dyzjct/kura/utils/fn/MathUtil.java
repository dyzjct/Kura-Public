package me.dyzjct.kura.utils.fn;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MathUtil {
  private static final Random random = new Random();
  private static final Minecraft mc = Minecraft.getMinecraft();
  
  public static int getRandom(int min, int max) {
    return min + random.nextInt(max - min + 1);
  }
  
  public static double getRandom(double min, double max) {
    return MathHelper.clamp(min + random.nextDouble() * max, min, max);
  }
  
  public static Vec3d getInterpolatedRenderPos(Entity entity, float ticks) {
    return interpolateEntity(entity, ticks).subtract((Minecraft.getMinecraft().getRenderManager()).viewerPosX, (Minecraft.getMinecraft().getRenderManager()).viewerPosY, (Minecraft.getMinecraft().getRenderManager()).viewerPosZ);
  }
  
  public static Vec3d interpolateEntity(Entity entity, float time) {
    return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time);
  }
  
  public static float getRandom(float min, float max) {
    return MathHelper.clamp(min + random.nextFloat() * max, min, max);
  }
  
  public static int clamp(int num, int min, int max) {
    return (num < min) ? min : Math.min(num, max);
  }
  
  public static float clamp(float num, float min, float max) {
    return (num < min) ? min : Math.min(num, max);
  }
  
  public static double clamp(double num, double min, double max) {
    return (num < min) ? min : Math.min(num, max);
  }
  
  public static float sin(float value) {
    return MathHelper.sin(value);
  }
  
  public static float cos(float value) {
    return MathHelper.cos(value);
  }
  
  public static float wrapDegrees(float value) {
    return MathHelper.wrapDegrees(value);
  }
  
  public static double wrapDegrees(double value) {
    return MathHelper.wrapDegrees(value);
  }
  
  public static Vec3d roundVec(Vec3d vec3d, int places) {
    return new Vec3d(round(vec3d.x, places), round(vec3d.y, places), round(vec3d.z, places));
  }
  
  public static double square(double input) {
    return input * input;
  }
  
  public static double round(double value, int places) {
    if (places < 0)
      throw new IllegalArgumentException(); 
    BigDecimal bd = BigDecimal.valueOf(value);
    bd = bd.setScale(places, RoundingMode.FLOOR);
    return bd.doubleValue();
  }
  
  public static float wrap(float valI) {
    float val = valI % 360.0F;
    if (val >= 180.0F)
      val -= 360.0F; 
    if (val < -180.0F)
      val += 360.0F; 
    return val;
  }
  
  public static Vec3d direction(float yaw) {
    return new Vec3d(Math.cos(degToRad((yaw + 90.0F))), 0.0D, Math.sin(degToRad((yaw + 90.0F))));
  }
  
  public static float round(float value, int places) {
    if (places < 0)
      throw new IllegalArgumentException(); 
    BigDecimal bd = BigDecimal.valueOf(value);
    bd = bd.setScale(places, RoundingMode.FLOOR);
    return bd.floatValue();
  }
  
  public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean descending) {
    LinkedList<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
    if (descending) {
      list.sort((Comparator)Map.Entry.comparingByValue(Comparator.reverseOrder()));
    } else {
      list.sort((Comparator)Map.Entry.comparingByValue());
    } 
    LinkedHashMap<Object, Object> result = new LinkedHashMap<>();
    for (Map.Entry<K, V> entry : list)
      result.put(entry.getKey(), entry.getValue()); 
    return (Map)result;
  }
  
  public static double multiply(double one) {
    return one * one;
  }
  
  public static Vec3d extrapolatePlayerPosition(EntityPlayer player, int ticks) {
    Vec3d lastPos = new Vec3d(player.lastTickPosX, player.lastTickPosY, player.lastTickPosZ);
    Vec3d currentPos = new Vec3d(player.posX, player.posY, player.posZ);
    double distance = multiply(player.motionX) + multiply(player.motionY) + multiply(player.motionZ);
    Vec3d tempVec = calculateLine(lastPos, currentPos, distance * ticks);
    return new Vec3d(tempVec.x, player.posY, tempVec.z);
  }
  
  public static Vec3d calculateLine(Vec3d x1, Vec3d x2, double distance) {
    double length = Math.sqrt(multiply(x2.x - x1.x) + multiply(x2.y - x1.y) + multiply(x2.z - x1.z));
    double unitSlopeX = (x2.x - x1.x) / length;
    double unitSlopeY = (x2.y - x1.y) / length;
    double unitSlopeZ = (x2.z - x1.z) / length;
    double x = x1.x + unitSlopeX * distance;
    double y = x1.y + unitSlopeY * distance;
    double z = x1.z + unitSlopeZ * distance;
    return new Vec3d(x, y, z);
  }
  
  public static String getTimeOfDay() {
    Calendar c = Calendar.getInstance();
    int timeOfDay = c.get(11);
    if (timeOfDay < 12)
      return "Good Morning "; 
    if (timeOfDay < 16)
      return "Good Afternoon "; 
    if (timeOfDay < 21)
      return "Good Evening "; 
    return "Good Night ";
  }
  
  public static double radToDeg(double rad) {
    return rad * 57.295780181884766D;
  }
  
  public static double degToRad(double deg) {
    return deg * 0.01745329238474369D;
  }
  
  public static double getIncremental(double val, double inc) {
    double one = 1.0D / inc;
    return Math.round(val * one) / one;
  }
  
  public static double[] directionSpeed(double speed) {
    float forward = mc.player.movementInput.moveForward;
    float side = mc.player.movementInput.moveStrafe;
    float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
    if (forward != 0.0F) {
      if (side > 0.0F) {
        yaw += ((forward > 0.0F) ? -45 : 45);
      } else if (side < 0.0F) {
        yaw += ((forward > 0.0F) ? 45 : -45);
      } 
      side = 0.0F;
      if (forward > 0.0F) {
        forward = 1.0F;
      } else if (forward < 0.0F) {
        forward = -1.0F;
      } 
    } 
    double sin = Math.sin(Math.toRadians((yaw + 90.0F)));
    double cos = Math.cos(Math.toRadians((yaw + 90.0F)));
    double posX = forward * speed * cos + side * speed * sin;
    double posZ = forward * speed * sin - side * speed * cos;
    return new double[] { posX, posZ };
  }
  
  public static List<Vec3d> getBlockBlocks(Entity entity) {
    ArrayList<Vec3d> vec3ds = new ArrayList<>();
    AxisAlignedBB bb = entity.getEntityBoundingBox();
    double y = entity.posY;
    double minX = round(bb.minX, 0);
    double minZ = round(bb.minZ, 0);
    double maxX = round(bb.maxX, 0);
    double maxZ = round(bb.maxZ, 0);
    if (minX != maxX) {
      vec3ds.add(new Vec3d(minX, y, minZ));
      vec3ds.add(new Vec3d(maxX, y, minZ));
      if (minZ != maxZ) {
        vec3ds.add(new Vec3d(minX, y, maxZ));
        vec3ds.add(new Vec3d(maxX, y, maxZ));
        return vec3ds;
      } 
    } else if (minZ != maxZ) {
      vec3ds.add(new Vec3d(minX, y, minZ));
      vec3ds.add(new Vec3d(minX, y, maxZ));
      return vec3ds;
    } 
    vec3ds.add(entity.getPositionVector());
    return vec3ds;
  }
  
  public static boolean areVec3dsAligned(Vec3d vec3d1, Vec3d vec3d2) {
    return areVec3dsAlignedRetarded(vec3d1, vec3d2);
  }
  
  public static boolean areVec3dsAlignedRetarded(Vec3d vec3d1, Vec3d vec3d2) {
    BlockPos pos1 = new BlockPos(vec3d1);
    BlockPos pos2 = new BlockPos(vec3d2.x, vec3d1.y, vec3d2.z);
    return pos1.equals(pos2);
  }
  
  public static float[] calcAngle(Vec3d from, Vec3d to) {
    double difX = to.x - from.x;
    double difY = (to.y - from.y) * -1.0D;
    double difZ = to.z - from.z;
    double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
    return new float[] { (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0D), (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist))) };
  }
  
  public static float[] calcAngleNoY(Vec3d from, Vec3d to) {
    double difX = to.x - from.x;
    double difZ = to.z - from.z;
    return new float[] { (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0D) };
  }
  
  public static double roundDouble(double number, int scale) {
    BigDecimal bd = new BigDecimal(number);
    bd = bd.setScale(scale, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }
}
