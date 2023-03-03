package me.windyteam.kura.module.modules.render

import me.windyteam.kura.event.events.render.RenderEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.IModule
import me.windyteam.kura.module.Module
import me.windyteam.kura.setting.Setting
import me.windyteam.kura.utils.render.RenderUtil
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import java.awt.Color

@Module.Info(name = "BlockHighlight", category = Category.RENDER)
object BlockHighlight : Module() {
    private val range = settings("Range", 8, 1, 12)
    private val accel = settings("Deceleration", 0.8f, 0.0f, 5.0f)
    private var max = isetting("MaxPositions", 15, 1, 30)
    private val box = bsetting("FullBlock", true)
    private val outline = bsetting("Outline", true)
    private val lineWidth = settings("LineWidth", 1.5f, 0.1f, 5.0f).b(outline)
    private var red = isetting("Red", 255, 1, 255)
    private var green = isetting("Green", 255, 1, 255)
    private var blue = isetting("Blue", 255, 1, 255)
    private var alpha = isetting("Alpha", 50, 1, 255)
    private var renderBB: AxisAlignedBB? = null
    private var lastPos: BlockPos? = null
    private var positions: ArrayList<*>? = null
    private var timePassed = 0f
    override fun onInit() {
        positions = ArrayList<BlockPos>()
    }

    override fun onWorldRender(event: RenderEvent) {
        if (fullNullCheck()) return
        runCatching{
            val mc = Minecraft.getMinecraft()
            val ray = mc.objectMouseOver
            var blockPos: BlockPos? = null
            if (mc.world.getBlockState(ray.blockPos).block !== Blocks.AIR) {
                blockPos = ray.blockPos
            }
            if (positions!!.size > max.value) {
                positions!!.removeAt(0)
            }
            if (lastPos == null || IModule.mc.player.getDistance(
                    renderBB!!.minX,
                    renderBB!!.minY,
                    renderBB!!.minZ
                ) > range.value
            ) {
                lastPos = blockPos
                renderBB = blockPos?.let { AxisAlignedBB(it) }
                timePassed = 0.0f
            }
            if (lastPos != blockPos) {
                lastPos = blockPos
                timePassed = 0.0f
            }
            val xDiff = blockPos!!.getX() - renderBB!!.minX
            val yDiff = blockPos.getY() - renderBB!!.minY
            val zDiff = blockPos.getZ() - renderBB!!.minZ
            var multiplier = timePassed / 10000 * accel.value
            if (multiplier > 1.0f) {
                multiplier = 1.0f
            }
            val colors = Color(red.value, green.value, blue.value, alpha.value)
            RenderUtil.drawFadingBox(
                renderBB!!.offset(
                    xDiff * multiplier,
                    yDiff * multiplier,
                    zDiff * multiplier
                ).also { renderBB = it }, colors, colors, lineWidth.value, outline.value, box.value, 1.0f, 1.0f, 1.0f
            )
            if (renderBB == AxisAlignedBB(blockPos)) {
                timePassed = 0.0f
            } else {
                timePassed += 50.0f
            }
        }
    }
}