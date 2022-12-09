package me.dyzjct.kura.module.modules.render.breakesp

import com.google.common.collect.Maps
import me.dyzjct.kura.event.events.block.BlockBreakEvent
import me.dyzjct.kura.event.events.render.RenderEvent
import me.dyzjct.kura.module.Category
import me.dyzjct.kura.module.Module
import me.dyzjct.kura.setting.BooleanSetting
import me.dyzjct.kura.setting.Setting
import me.dyzjct.kura.utils.block.BlockUtil
import me.dyzjct.kura.utils.block.BreakingUtil
import me.dyzjct.kura.utils.gl.MelonTessellator
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color
import java.text.DecimalFormat
import kotlin.math.abs


@Module.Info(name = "BreakESP", category = Category.RENDER, description = "BreakEsp")
class BreakESP : Module() {
    private val mineMap: MutableMap<Int, BreakESPExtend>? = Maps.newHashMap()
    private var renderAir: BooleanSetting = bsetting("RenderAir", false)
    private var drawID = bsetting("DrawID", false)
    private var drawProgress = bsetting("DrawProgress", false)
    private var colors = csetting("Color", Color(11, 232, 145))
    private var alpha: Setting<Int> = isetting("Alpha", 100, 1, 255)
    private var range: Setting<Int> = isetting("Range", 6, 1, 20)
    private var lineWidth: Setting<Int> = isetting("LineWidth", 2, 1, 3)
    private var renderMode = isetting("RenderMode", 0, 0, 10)
    var minePos: BlockPos? = null
    var packetPos: BlockPos? = null
    private var df = DecimalFormat("0.00")
    companion object { @JvmStatic var INSTANCE:BreakESP? = BreakESP() }


    @SubscribeEvent
    fun onBreak(event: BlockBreakEvent) {
        if (fullNullCheck()) {
            return
        }
        if (event.position != null) {
            if (event.position.getDistance(
                    mc.player.posX.toInt(),
                    mc.player.posY.toInt(),
                    mc.player.posZ.toInt()
                ) <= range.value
            ) {
                if (BlockUtil.canBreak(event.position, renderAir.value) && mineMap != null) {
                    var destroyblockprogress = mineMap[event.breakerId]
                    if (destroyblockprogress == null
                        || destroyblockprogress.getPosition().getX() != event.position.getX()
                        || destroyblockprogress.getPosition().getY() != event.position.getY()
                        || destroyblockprogress.getPosition()
                            .getZ() != event.position.getZ()
                    ) {
                        if (mc.world.getEntityByID(event.breakerId) is EntityPlayer) {
                            destroyblockprogress = BreakESPExtend(
                                event.breakerId,
                                event.position,
                                BreakingUtil.calcBreakTime(
                                    event.breakerId,
                                    event.position
                                ),
                                System.currentTimeMillis(),
                                0f
                            )
                            if (!mineMap.containsKey(event.breakerId)) {
                                mineMap[event.breakerId] = destroyblockprogress
                            } else {
                                mineMap.replace(event.breakerId, destroyblockprogress)
                            }
                        }
                    }
                }
            }
        }
    }



    override fun onWorldRender(event: RenderEvent) {
        val color = Color(colors.value.red, colors.value.green, colors.value.blue)
        //TODO: PacketMine Mode
        mineMap!!.forEach {
            packetPos = it.value.getPosition()
            if (abs(it.value.currentTime - System.currentTimeMillis()) < it.value.calcMineTime) {
                it.value.finalProgress =
                    abs(it.value.calcMineTime - (abs(it.value.currentTime - System.currentTimeMillis()).toFloat()))
                //ChatUtil.sendMessage(it.value.finalProgress.toString())
            }
            if (mc.world.getBlockState(packetPos!!).block == Blocks.AIR && !renderAir.value) {
                return@forEach
            }
            if (packetPos != null && !mc.world.isAirBlock(packetPos!!) && packetPos !== minePos) {
                if (packetPos!!.getDistance(
                        mc.player.posX.toInt(),
                        mc.player.posY.toInt(),
                        mc.player.posZ.toInt()
                    ) <= range.value
                ) {
                    MelonTessellator.boxESP(
                        packetPos,
                        color,
                        alpha.value,
                        lineWidth.value.toFloat(),
                        abs(it.value.finalProgress / it.value.calcMineTime),
                        renderMode.value
                    ) //MathHelper.clamp((progress + 2f) / 100f, 0, 1f));
                    if (drawProgress.value) {
                        GlStateManager.pushMatrix()
                        MelonTessellator.glBillboardDistanceScaled(
                            packetPos!!.getX().toFloat() + 0.5f,
                            packetPos!!.getY().toFloat() + 0.8f,
                            packetPos!!.getZ().toFloat() + 0.5f,
                            mc.player,
                            0.5f
                        )
                        if (it.value.finalProgress != 0f) {
                            GlStateManager.disableDepth()
                            GlStateManager.translate(
                                -mc.fontRenderer.getStringWidth(
                                    df.format(
                                        MathHelper.clamp(
                                            abs(100.0 - ((it.value.finalProgress / it.value.calcMineTime) * 100.0)),
                                            0.0,
                                            100.0
                                        ) / 2.0
                                    ).toDouble().toString()
                                ).toFloat(), 0f, 0f
                            )
                            mc.fontRenderer.drawStringWithShadow(
                                df.format(
                                    //getPercentage(
                                    //destroyBlockProgress.partialBlockDamage.toDouble()
                                    //)
                                    MathHelper.clamp(
                                        abs(100.0 - ((it.value.finalProgress / it.value.calcMineTime) * 100.0)),
                                        0.0,
                                        100.0
                                    )
                                ).toString() + "%",
                                0f,
                                0f,
                                if (MathHelper.clamp(
                                        abs(100.0 - ((it.value.finalProgress / it.value.calcMineTime) * 100.0)),
                                        0.0,
                                        100.0
                                    ) >= 50.0
                                ) Color(0, 255, 0).rgb else Color(255, 0, 0).rgb
                            )
                        }
                        GlStateManager.popMatrix()
                    }
                    if (drawID.value) {
                        if (mc.world.getEntityByID(it.key) is EntityPlayer) {
                            GlStateManager.pushMatrix()
                            MelonTessellator.glBillboardDistanceScaled(
                                packetPos!!.getX().toFloat() + 0.5f,
                                packetPos!!.getY().toFloat() + 0.5f,
                                packetPos!!.getZ().toFloat() + 0.5f,
                                mc.player,
                                0.4f
                            )
                            GlStateManager.disableDepth()
                            GlStateManager.translate(
                                -(mc.fontRenderer.getStringWidth((mc.world.getEntityByID(it.key) as EntityPlayer).name) / 2.0),
                                0.0,
                                0.0
                            )
                            mc.fontRenderer.drawStringWithShadow(
                                ((mc.world.getEntityByID(it.key) as EntityPlayer).name),
                                0f,
                                0f,
                                Color(255, 255, 255).rgb
                            )
                            GlStateManager.popMatrix()
                        }
                    }
                }
            }
        }
    }

    private fun getPercentage(input: Double): Float {
        val df = DecimalFormat("0.00")
        val result = df.format((if (input == 0.0) input + 1f else input + 2.00f) / 10.0f)
        val resultSort = result.toFloat()
        val arrayResult = resultSort.toString().split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val animatedNum = "99." + arrayResult[1]
        return if (resultSort > 0.9) MathHelper.clamp(
            resultSort * 100.0f,
            0f,
            100f
        ) else MathHelper.clamp(resultSort * animatedNum.toFloat(), 0f, 100f)
    }



}