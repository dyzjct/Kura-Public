package me.dyzjct.kura.module.modules.render

import com.google.common.collect.Maps
import me.dyzjct.kura.event.events.block.BlockBreakEvent
import me.dyzjct.kura.event.events.render.RenderEvent
import me.dyzjct.kura.manager.FriendManager
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
    private var renderSelf = bsetting("RenderSelf", false)
    private var drawID = bsetting("DrawID", false)
    private var drawProgress = bsetting("DrawProgress", false)
    private var colors = csetting("Color", Color(11, 232, 145))
    private var friendColor = csetting("FriendColor", Color(157, 14, 192))
    private var alpha: Setting<Int> = isetting("Alpha", 100, 1, 255)
    private var range: Setting<Int> = isetting("Range", 6, 1, 20)
    private var lineWidth: Setting<Int> = isetting("LineWidth", 2, 1, 3)
    private var renderMode = isetting("RenderMode", 0, 0, 10)
    private var minePos: BlockPos? = null
    private var packetPos: BlockPos? = null
    private var df = DecimalFormat("0.00")


    @SubscribeEvent
    fun onBreak(event: BlockBreakEvent) {
        if (fullNullCheck()) {
            return
        }
        if (event.position != null) {
            if (!renderSelf.value) {
                if (event.breakerId == mc.player.entityId) {
                    return
                }
            }
            if (event.position.getDistance(
                    mc.player.posX.toInt(), mc.player.posY.toInt(), mc.player.posZ.toInt()
                ) <= range.value
            ) {
                if (BlockUtil.canBreak(event.position, renderAir.value) && mineMap != null) {
                    var destroyblockprogress = mineMap[event.breakerId]
                    if (destroyblockprogress == null || destroyblockprogress.position.getX() != event.position.getX() || destroyblockprogress.position.getY() != event.position.getY() || destroyblockprogress.position.getZ() != event.position.getZ()) {
                        if (mc.world.getEntityByID(event.breakerId) is EntityPlayer) {
                            destroyblockprogress = BreakESPExtend(
                                event.breakerId, event.position, BreakingUtil.calcBreakTime(
                                    event.breakerId, event.position
                                ), System.currentTimeMillis(), 0f
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
        if (fullNullCheck()) {
            return
        }
        val color = Color(colors.value.red, colors.value.green, colors.value.blue)
        val fcolor = Color(friendColor.value.red, friendColor.value.green, friendColor.value.blue)
        //TODO: PacketMine Mode
        mineMap!!.forEach {
            packetPos = it.value.position
            it.value.finalProgress = MathHelper.clamp(
                it.value.calcMineTime - (MathHelper.clamp(
                    (System.currentTimeMillis() - it.value.currentTime).toDouble(), 0.0, it.value.currentTime.toDouble()
                ).toFloat()), 0.0f, it.value.calcMineTime
            )
            if (mc.world.getBlockState(packetPos!!).block === Blocks.AIR && !renderAir.value) {
                return@forEach
            }
            if (packetPos != null && packetPos !== minePos && mc.world.getEntityByID(it.value.minerID) != null) {
                if (packetPos!!.getDistance(
                        mc.player.posX.toInt(), mc.player.posY.toInt(), mc.player.posZ.toInt()
                    ) <= range.value
                ) {
                    MelonTessellator.boxESP(
                        packetPos!!,
                        if (FriendManager.isFriend(mc.world.getEntityByID(it.value.minerID)!!)) {
                            fcolor
                        } else {
                            color
                        },
                        alpha.value,
                        lineWidth.value.toFloat(),
                        abs(it.value.finalProgress / it.value.calcMineTime),
                        renderMode.value
                    )
                    if (drawProgress.value) {
                        GlStateManager.pushMatrix()
                        MelonTessellator.glBillboardDistanceScaled(
                            packetPos!!.getX().toFloat() + 0.5f,
                            packetPos!!.getY().toFloat() + 0.8f,
                            packetPos!!.getZ().toFloat() + 0.5f,
                            mc.player,
                            1f
                        )
                        if (it.value.finalProgress >= 0f) {
                            GlStateManager.disableDepth()
                            try {
                                GlStateManager.translate(
                                    -mc.fontRenderer.getStringWidth(
                                        df.format(
                                            MathHelper.clamp(
                                                abs(100.0 - ((it.value.finalProgress / it.value.calcMineTime) * 100.0)),
                                                0.0,
                                                100.0
                                            )
                                        ).toDouble().toString()
                                    ).toFloat() / 2.0f, 0f, 0f
                                )
                                mc.fontRenderer.drawStringWithShadow(
                                    df.format(
                                        MathHelper.clamp(
                                            abs(100.0 - ((it.value.finalProgress / it.value.calcMineTime) * 100.0)),
                                            0.0,
                                            100.0
                                        )
                                    ).toString() + "%", 0f, 0f, if (MathHelper.clamp(
                                            abs(100.0 - ((it.value.finalProgress / it.value.calcMineTime) * 100.0)),
                                            0.0,
                                            100.0
                                        ) >= 50.0
                                    ) Color(0, 255, 0).rgb else Color(255, 0, 0).rgb
                                )
                            } catch (_: NumberFormatException) {
                            }
                            GlStateManager.popMatrix()
                        }
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

    class BreakESPExtend(
        var minerID: Int,
        val position: BlockPos,
        var calcMineTime: Float,
        var currentTime: Long,
        var finalProgress: Float
    )
}