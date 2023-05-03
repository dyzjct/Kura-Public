package me.windyteam.kura.module.modules.combat;

import me.windyteam.kura.event.events.entity.MotionUpdateEvent;
import me.windyteam.kura.event.events.render.RenderEvent;
import me.windyteam.kura.module.Category;
import me.windyteam.kura.module.Module;
import me.windyteam.kura.module.modules.client.Colors;
import me.windyteam.kura.setting.IntegerSetting;
import me.windyteam.kura.setting.Setting;
import me.windyteam.kura.utils.block.BlockUtil;
import me.windyteam.kura.utils.color.ColorUtil;
import me.windyteam.kura.utils.entity.CrystalUtil;
import me.windyteam.kura.utils.entity.TargetUtils;
import me.windyteam.kura.utils.gl.RenderUtils;
import me.windyteam.kura.utils.inventory.InventoryUtil;
import me.windyteam.kura.utils.mc.ChatUtil;
import me.windyteam.kura.utils.block.BlockUtil;
import me.windyteam.kura.utils.entity.CrystalUtil;
import me.windyteam.kura.utils.mc.ChatUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEmptyDrops;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Module.Info(name = "PistonCrystal", category = Category.COMBAT, description = "FKu Burrow god or holecamper XDD")
public class PistonCrystal extends Module {

    public Setting<Double> range = dsetting("Range", 4.9D, 0.0D, 10.0D);
    public Setting<Integer> delay1 = isetting("ChangeDelay", 5, 0, 20);
    public Setting<Integer> delay2 = isetting("PlaceDelay", 2, 0, 100);
    public Setting<Integer> min = isetting("MinDamage", 21, 0, 100);
    public IntegerSetting thread = isetting("Thread" , 1 , 0 , 10);

    int progress = 0;

    List<PA> attackable;

    @Override
    public void onEnable() {
        progress = 0;
        attackable = new ArrayList<>();
    }

    @Override
    public void onUpdate() {
        InventoryUtil.push();

        int pitem = InventoryUtil.pickItem(33, false);
        int cryst = InventoryUtil.pickItem(426, false);
        int powtem1 = InventoryUtil.pickItem(152, false);
        int powtem2 = InventoryUtil.pickItem(76, false);
        if (pitem == -1 || cryst == -1 || (powtem1 == -1 && powtem2 == -1)) {
            ChatUtil.sendMessage("\u00A77[Kura] \u00A74Item Not Found ");
            toggle();
        }
        if (!TargetUtils.findTarget(range.getValue())) return;
        Entity player = TargetUtils.currentTarget;

        int range = (int) this.range.getValue().floatValue();
        if (attackable == null) {
            return;
        }
        if (attackable.isEmpty() || mc.player.ticksExisted % Math.max(1, delay1.getValue() / Math.max(1, thread.getValue())) == 0) {
            attackable = new ArrayList<>();
            for (int dx = -range; dx <= range; dx++) {
                for (int dy = -range; dy <= range; dy++) {
                    for (int dz = -range; dz <= range; dz++) {
                        BlockPos pos = new BlockPos(mc.player).add(dx, dy, dz);
                        if (player.getDistanceSq(pos) > range * range) continue;
                        boolean b = false;
                        for (BlockPos off : pistonoff) {
                            if (mc.world.getBlockState(pos.add(off)).getBlock() instanceof BlockObsidian) {
                                b = true;
                                break;
                            }
                            if (mc.world.getBlockState(pos.add(off)).getBlock() instanceof BlockEmptyDrops) {
                                b = true;
                                break;
                            }
                        }
                        if (!b) continue;
                        double damage = CrystalUtil.getDamage(new Vec3d(pos).add(0.5, 0, 0.5), TargetUtils.currentTarget);
                        if (damage < min.getValue()) continue;
                        PA pa = new PA(pos, damage);
                        if (!pa.canPA()) continue;
                        attackable.add(pa);
                    }
                }

                attackable.sort((a, b) -> {
                    if (a == null && b == null)
                        return 0;

                    return Double.compare(b.damage, a.damage);

                });
            }
        }
        InventoryUtil.pop();
    }

    @SubscribeEvent
    public void onEvent(MotionUpdateEvent.Tick event) {
        InventoryUtil.push();
        if (!TargetUtils.findTarget(range.getValue())) return;

        if (!attackable.isEmpty()) {
            attackable.get(0).updatePA(event);
        }

        for (Entity et : mc.world.loadedEntityList) {
            if (et instanceof EntityEnderCrystal) {
                if (et.getDistance(mc.player) > range.getValue()) continue;
                mc.playerController.attackEntity(mc.player, et);
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }
        }

        InventoryUtil.pop();
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (attackable == null) {
            return;
        }
        if (!attackable.isEmpty()) {
            Color col = ColorUtil.rainbow(Colors.rainbowHue.getValue());
            RenderUtils.drawBlockBox(attackable.get(0).crystal, RenderUtils.alpha(col, 0x20), false);
            RenderUtils.drawBlockBox(attackable.get(0).piston, RenderUtils.alpha(col, 0x20), false);
            if (attackable.get(0).power != null)
                RenderUtils.drawBlockBox(attackable.get(0).power, RenderUtils.alpha(col, 0x20), false);
            RenderUtils.drawBlockBox(attackable.get(0).crystal.offset(attackable.get(0).pistonFacing), RenderUtils.alpha(new Color(0xffffff), 0x20), false);
        }
    }

    public static final BlockPos[] pistonoff = new BlockPos[]{
            /*y = -1*/
            new BlockPos(-1, -1, -1),
            new BlockPos(0, -1, -1),
            new BlockPos(1, -1, -1),
            new BlockPos(-1, -1, 0),
            new BlockPos(0, -1, 0),
            new BlockPos(1, -1, 0),
            new BlockPos(-1, -1, 1),
            new BlockPos(0, -1, 1),
            new BlockPos(1, -1, 1),
            /*y = 0*/
            new BlockPos(-1, 0, -1),
            new BlockPos(0, 0, -1),
            new BlockPos(1, 0, -1),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 1),
            new BlockPos(0, 0, 1),
            new BlockPos(1, 0, 1),
            /*y = 1*/
            new BlockPos(-1, 1, -1),
            new BlockPos(0, 1, -1),
            new BlockPos(1, 1, -1),
            new BlockPos(-1, 1, 0),
            new BlockPos(0, 1, 0),
            new BlockPos(1, 1, 0),
            new BlockPos(-1, 1, 1),
            new BlockPos(0, 1, 1),
            new BlockPos(1, 1, 1)
    };

    public class PA {

        public BlockPos pos;
        public BlockPos crystal;
        public BlockPos power;
        public EnumFacing pistonFacing;
        public BlockPos piston;
        public double damage;

        public PA(BlockPos pos, double damage) {
            this.pos = pos;
            this.damage = damage;
            this.stage = 0;
        }

        public boolean canPA() {
            double pist = .5;
            for (EnumFacing f : EnumFacing.values()) {
                BlockPos crypos = pos.offset(f);
                //check
                if (!mc.world.isAirBlock(crypos)) continue;
                if (!mc.world.isAirBlock(crypos.offset(EnumFacing.UP))) continue;
                if (!TargetUtils.canAttack(mc.player.getPositionVector().add(0, mc.player.getEyeHeight(), 0), new Vec3d(crypos).add(.5D, 1.7D, .5D)))
                    continue;
                if (!(mc.world.getBlockState(crypos.offset(EnumFacing.DOWN)).getBlock() instanceof BlockObsidian) && !(mc.world.getBlockState(crypos.offset(EnumFacing.DOWN)).getBlock() instanceof BlockEmptyDrops))
                    continue;
                if (!mc.world.checkNoEntityCollision(Block.FULL_BLOCK_AABB.offset(crypos))) continue;
                if (mc.player.getDistanceSq((double) crypos.getX() + 0.5D, (double) crypos.getY() + 0.5D, (double) crypos.getZ() + 0.5D) >= 64.0D)
                    continue;
                //check2
                this.crystal = crypos;
                this.pistonFacing = rotateHantaigawa(f);
                if (pistonFacing == EnumFacing.DOWN) continue;
                if (!mc.world.isAirBlock(crypos.offset(pistonFacing))) continue;

                for (BlockPos off : pistonoff) {
                    BlockPos pispos = crystal.add(off);
                    if (pispos.equals(crypos)) continue;
                    if (crypos.offset(EnumFacing.UP).equals(pispos)) continue;
                    if (crypos.offset(pistonFacing).equals(pispos)) continue;
                    EnumFacing sfac = EnumFacing.getDirectionFromEntityLiving(pispos, mc.player);
                    if (sfac.getAxis() == EnumFacing.Axis.Y) {
                        if (pistonFacing != sfac) continue;
                    }
                    if (pistonFacing.getAxis() == EnumFacing.Axis.Y) {
                        if (pistonFacing != sfac) continue;
                    }
                    this.power = null;
                    if (mc.world.isBlockPowered(pispos)) {
                        if (BlockUtil.isPlaceable(pispos, 0, true) == null) continue;
                    } else {
                        for (EnumFacing fa : EnumFacing.values()) {
                            BlockPos powpos = pispos.offset(fa);
                            if (pispos.equals(powpos)) continue;
                            if (pispos.offset(pistonFacing).equals(powpos)) continue;
                            if (crypos.equals(powpos)) continue;
                            if (crypos.offset(EnumFacing.UP).equals(powpos)) continue;
                            if (mc.player.getDistanceSq((double) powpos.getX() + 0.5D, (double) powpos.getY() + 0.5D, (double) powpos.getZ() + 0.5D) >= 64.0D)
                                continue;
                            if (BlockUtil.isPlaceable(powpos, 0, true) == null) continue;

                            if (pistonFacing.getDirectionVec().getX() > 0 && powpos.getX() - pist > crypos.getX())
                                continue;
                            if (pistonFacing.getDirectionVec().getY() > 0 && powpos.getY() - pist > crypos.getY())
                                continue;
                            if (pistonFacing.getDirectionVec().getZ() > 0 && powpos.getZ() - pist > crypos.getZ())
                                continue;
                            if (pistonFacing.getDirectionVec().getX() < 0 && powpos.getX() + pist < crypos.getX())
                                continue;
                            if (pistonFacing.getDirectionVec().getY() < 0 && powpos.getY() + pist < crypos.getY())
                                continue;
                            if (pistonFacing.getDirectionVec().getZ() < 0 && powpos.getZ() + pist < crypos.getZ())
                                continue;
                            if (!mc.world.isAirBlock(powpos)) continue;
                            this.power = powpos;
                        }
                        if (power == null) continue;
                    }
                    if (mc.player.getDistanceSq((double) pispos.getX() + 0.5D, (double) pispos.getY() + 0.5D, (double) pispos.getZ() + 0.5D) >= 64.0D)
                        continue;
                    if (!mc.world.checkNoEntityCollision(Block.FULL_BLOCK_AABB.offset(pispos))) continue;
                    if (pistonFacing.getDirectionVec().getX() > 0 && pispos.getX() - pist > crypos.getX()) continue;
                    if (pistonFacing.getDirectionVec().getY() > 0 && pispos.getY() - pist > crypos.getY()) continue;
                    if (pistonFacing.getDirectionVec().getZ() > 0 && pispos.getZ() - pist > crypos.getZ()) continue;
                    if (pistonFacing.getDirectionVec().getX() < 0 && pispos.getX() + pist < crypos.getX()) continue;
                    if (pistonFacing.getDirectionVec().getY() < 0 && pispos.getY() + pist < crypos.getY()) continue;
                    if (pistonFacing.getDirectionVec().getZ() < 0 && pispos.getZ() + pist < crypos.getZ()) continue;
                    if (!mc.world.isAirBlock(pispos)) continue;
                    if (!mc.world.isAirBlock(pispos.offset(pistonFacing))) continue;
                    if (pispos.getY() < crystal.getY() && pistonFacing.getAxis() != EnumFacing.Axis.Y) continue;
                    this.piston = pispos;
                    return true;
                }
            }
            return false;
        }

        public int stage;


        public void updatePA(MotionUpdateEvent.Tick event) {
            int pitem = InventoryUtil.pickItem(33, false);
            int powtem1 = InventoryUtil.pickItem(152, false);
            int powtem2 = InventoryUtil.pickItem(76, false);
            int cryst = InventoryUtil.pickItem(426, false);

            switch (pistonFacing) {

                case SOUTH:
                    event.setYaw(180);
                    event.setPitch(0);
                    break;
                case NORTH:
                    event.setYaw(0);
                    event.setPitch(0);
                    break;
                case EAST:
                    event.setYaw(90);
                    event.setPitch(0);
                    break;
                case WEST:
                    event.setYaw(-90);
                    event.setPitch(0);
                    break;
                case UP:
                case DOWN:
                    event.setPitch(90);
                    break;

            }

            if (stage == delay2.getValue()) {
                InventoryUtil.setSlot(pitem);
                BlockUtil.doPlace(BlockUtil.isPlaceable(piston, 0, false), true);

                if (power != null) {
                    InventoryUtil.setSlot(powtem1);
                    InventoryUtil.setSlot(powtem2);
                    BlockUtil.doPlace(BlockUtil.isPlaceable(power, 0, false), true);
                }

                InventoryUtil.setSlot(pitem);
                BlockUtil.doPlace(BlockUtil.isPlaceable(piston, 0, false), true);

                InventoryUtil.setSlot(cryst);
                CrystalUtil.placeCrystal(crystal);

                if (power != null) {
                    InventoryUtil.setSlot(powtem1);
                    InventoryUtil.setSlot(powtem2);
                    BlockUtil.doPlace(BlockUtil.isPlaceable(power, 0, false), true);
                }
            }

            if (stage == delay2.getValue() + 1) {
                InventoryUtil.setSlot(cryst);
                mc.world.setBlockToAir(piston);
                if (power != null) {
                    mc.world.setBlockToAir(power);
                }
            }
            stage++;
        }
    }

    public EnumFacing rotateHantaigawa(EnumFacing f) {
        switch (f) {
            case WEST:
                return EnumFacing.EAST;

            case EAST:
                return EnumFacing.WEST;

            case SOUTH:
                return EnumFacing.NORTH;

            case NORTH:
                return EnumFacing.SOUTH;

            case UP:
                return EnumFacing.DOWN;

            case DOWN:
                return EnumFacing.UP;

            default:
                throw new IllegalStateException("Unable to get CCW facing of " + this);
        }
    }
}
