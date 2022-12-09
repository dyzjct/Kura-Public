package me.dyzjct.kura.module.modules.combat.HoleFiller

import me.dyzjct.kura.event.events.entity.MotionUpdateEvent
import me.dyzjct.kura.event.events.render.RenderEvent
import me.dyzjct.kura.manager.FriendManager
import me.dyzjct.kura.manager.HotbarManager.spoofHotbar
import me.dyzjct.kura.module.Category
import me.dyzjct.kura.module.Module
import me.dyzjct.kura.module.modules.xddd.CrystalHelper
import me.dyzjct.kura.setting.Setting
import me.dyzjct.kura.utils.NTMiku.TimerUtils
import me.dyzjct.kura.utils.block.BlockUtil
import me.dyzjct.kura.utils.combat.HoleUtils.world
import me.dyzjct.kura.utils.entity.CrystalUtil
import me.dyzjct.kura.utils.entity.EntityUtil
import me.dyzjct.kura.utils.entity.HoleUtil
import me.dyzjct.kura.utils.entity.PlayerUtil
import me.dyzjct.kura.utils.gl.MelonTessellator
import me.dyzjct.kura.utils.inventory.InventoryUtil
import me.dyzjct.kura.utils.mc.ChatUtil
import me.dyzjct.kura.utils.vector.distance
import net.minecraft.block.BlockObsidian
import net.minecraft.block.BlockWeb
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.concurrent.CopyOnWriteArrayList
import java.util.stream.Collectors
import kotlin.math.abs

@Module.Info(name = "HoleFiller", description = "Auto Hole Filling", category = Category.COMBAT)
class HoleFiller : Module() {
    private var blocks: List<BlockPos>? = CopyOnWriteArrayList()
    private var placeTimer = TimerUtils()
    private var oldSlot = 0
    private var print = "0.0"
    private var posToFill: BlockPos? = null
    companion object { @JvmStatic var INSTANCE: HoleFiller? = HoleFiller() }
    private var holeRangeEnemy = dsetting("EnemyHoleRange", 0.1, 0.1, 2.0)
    private var placeRange = isetting("PlaceRange", 5, 1, 6)
    private var placeDelay = isetting("PlaceDelay", 10,0,1000)
    private var range = isetting("Range", 3, 1, 6)
    private var predictTicks = isetting("PredictTicks", 4, 0, 20)
    private var web = bsetting("Web", false)
    private var rotate = bsetting("Rotate", true)
    private var packet = bsetting("Packet", false)
    private var colors = csetting("Color", Color(11, 232, 145))
    private var lineWidth: Setting<Float> = fsetting("LineWidth", 3f, 1f, 3f)

    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent.FastTick?){
        blocks = findCrystalBlocks()
        oldSlot = mc.player.inventory.currentItem
        val target = getTarget(range.value.toDouble())
        for (pos in blocks!!) {
            val result = BlockUtil.valid(pos)
            if (result != BlockUtil.ValidResult.Ok) {
                continue
            }
            if (target != null && target !== mc.player) {
                val targetDistance = if (predictTicks.value > 0) target.positionVector.add(
                    CrystalHelper.PredictionHandlerNew(
                        target,
                        predictTicks.value
                    )
                ) else target.positionVector
                val dist = horizontalDist(
                    targetDistance, Vec3d(
                        pos.getX().toDouble(), pos.getY().toDouble(), pos.getZ().toDouble()
                    )
                )
                if (FriendManager.isFriend(target.name)) continue
                if (targetDistance.y <= pos.y + 0.5) continue
                if (mc.player.getDistance(
                        targetDistance.x,
                        targetDistance.y,
                        targetDistance.z
                    ) > range.value
                ) continue
                if (mc.player.getDistance(
                        pos.x.toDouble(),
                        pos.y.toDouble(),
                        pos.z.toDouble()
                    ) > placeRange.value
                ) continue
                if (target.entityBoundingBox.intersects(AxisAlignedBB(pos))) {
                    print = "BoundingBox Intersects!"
                    ChatUtil.NoSpam.sendMessage(print)
                    continue
                }
                if (dist >= Double.MAX_VALUE) continue
                if (abs(dist) > holeRangeEnemy.value) continue
                print = dist.toString()
                posToFill = pos
            }
        }
        if (posToFill == null) {
            return
        }
        if (mc.player.getDistance(
                posToFill!!.x.toDouble(),
                posToFill!!.y.toDouble(),
                posToFill!!.z.toDouble()
            ) > placeRange.value
        ) {
            posToFill = null
            return
        }
        if (!world.getBlockState(posToFill!!).block.equals(Blocks.AIR)) {
            posToFill = null
            return
        }
        if (HoleFillerExtend.AntiFuck){
            if (posToFill!=Blocks.WEB&&posToFill!=Blocks.OBSIDIAN){
                placeBlock(posToFill!!, rotate.value, packet.value)
            }
        }
    }


    private fun placeBlock(pos: BlockPos, rotate: Boolean, packet: Boolean) {
        if (web.value) {
            if (InventoryUtil.findHotbarBlock(BlockWeb::class.java) != -1) {
                spoofHotbar(InventoryUtil.findHotbarBlock(BlockWeb::class.java))
            } else if (InventoryUtil.findHotbarBlock(BlockWeb::class.java) == -1 && InventoryUtil.findHotbarBlock(
                    BlockObsidian::class.java
                ) != -1
            ) {
                spoofHotbar(InventoryUtil.findHotbarBlock(BlockObsidian::class.java))
            }
        } else {
            if (InventoryUtil.findHotbarBlock(BlockObsidian::class.java) != -1) {
                spoofHotbar(InventoryUtil.findHotbarBlock(BlockObsidian::class.java))
            } else if (InventoryUtil.findHotbarBlock(BlockObsidian::class.java) == -1 && InventoryUtil.findHotbarBlock(
                    BlockWeb::class.java
                ) != -1
            ) {
                spoofHotbar(InventoryUtil.findHotbarBlock(BlockWeb::class.java))
            }
        }
        if (placeTimer.tickAndReset(placeDelay.value)) {
            BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate, packet)
        }
        spoofHotbar(oldSlot)
    }

    private fun getTarget(range: Double): EntityPlayer? {
        var target: EntityPlayer? = null
        for (player in CopyOnWriteArrayList(mc.world.playerEntities)) {
            if (EntityUtil.isntValid(player, range)) continue
            if (mc.player.getDistance(player) > range) continue
            target = player
            if (player != null) {
                break
            }
        }
        return target
    }

    private fun isHole(posA: BlockPos?): Boolean {
        return HoleUtil.is2HoleB(posA) || HoleUtil.isHole(posA)
    }

    private fun horizontalDist(player: Vec3d, vec3d: Vec3d): Double {
        return distance(player.x, player.z, vec3d.x, vec3d.z)
    }

    private fun findCrystalBlocks(): List<BlockPos> {
        val positions = NonNullList.create<BlockPos>()
        positions.addAll(
            CrystalUtil.getSphere(
                PlayerUtil.getPlayerPos(),
                placeRange.value.toDouble(),
                placeRange.value.toDouble(),
                false,
                true,
                0
            )
                .stream()
                .filter { isHole(it) }
                .collect(Collectors.toList()))
        return positions
    }

    override fun getHudInfo(): String {
        return print
    }

    override fun onWorldRender(event: RenderEvent) {
        if (fullNullCheck()) {
            return
        }
        val color = Color(colors.value.red, colors.value.green, colors.value.blue)
        if (posToFill != null) {
            MelonTessellator.prepare(GL11.GL_QUADS)
            MelonTessellator.drawFullBox(posToFill, lineWidth.value, color.rgb)
            MelonTessellator.release()
        }
    }


    override fun onEnable() {
        if (fullNullCheck()) {
            return
        }
        mc.playerController.updateController()
        placeTimer.reset()
    }
}