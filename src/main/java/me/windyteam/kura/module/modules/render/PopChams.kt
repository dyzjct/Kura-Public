package me.windyteam.kura.module.modules.render

import com.mojang.authlib.GameProfile
import me.windyteam.kura.event.events.client.PacketEvents
import me.windyteam.kura.event.events.render.RenderEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.setting.Setting
import me.windyteam.kura.utils.gl.MelonTessellator.prepare
import me.windyteam.kura.utils.gl.MelonTessellator.release
import me.windyteam.kura.utils.render.TotemPopCham
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelPlayer
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.Packet
import net.minecraft.network.play.server.SPacketEntityStatus
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import java.awt.Color

@Module.Info(name = "PopChams", description = "Renders when someone pops", category = Category.RENDER)
class PopChams : Module() {
    var self = bsetting("Self", false)
    @JvmField
    var rF = isetting("RedFill", 255, 0, 255)
    @JvmField
    var gF = isetting("GreenFill", 26, 0, 255)
    @JvmField
    var bF = isetting("BlueFill", 42, 0, 255)
    @JvmField
    var aF = isetting("AlphaFill", 42, 0, 255)
    @JvmField
    var fadestart = isetting("FadeStart", 200, 0, 3000)
    @JvmField
    var fadetime: Setting<Double> = dsetting("FadeTime", 0.5, 0.0, 2.0)
    var onlyOneEsp = bsetting("OnlyOneEsp", true)
    var player: EntityOtherPlayerMP? = null
    var playerModel: ModelPlayer? = null
    var startTime: Long? = null
    var alphaFill = 0.0

    @SubscribeEvent
    fun onPacketReceive(event: PacketEvents.Receive) {
        if (event.getPacket<Packet<*>>() is SPacketEntityStatus && event.getPacket<SPacketEntityStatus>()
                .opCode.toInt() == 35
        ) {
            event.getPacket<SPacketEntityStatus>().getEntity(mc.world as World)
            if (self.value as Boolean || event.getPacket<SPacketEntityStatus>().getEntity(mc.world).getEntityId() != mc.player.getEntityId()) {
                val profile = GameProfile(mc.player.uniqueID, "")
                player = EntityOtherPlayerMP(mc.world, profile)
                player!!.copyLocationAndAnglesFrom(event.getPacket<SPacketEntityStatus>().getEntity(mc.world as World))
                playerModel = ModelPlayer(0.0f, false)
                startTime = System.currentTimeMillis()
                playerModel!!.bipedHead.showModel = false
                playerModel!!.bipedBody.showModel = false
                playerModel!!.bipedLeftArmwear.showModel = false
                playerModel!!.bipedLeftLegwear.showModel = false
                playerModel!!.bipedRightArmwear.showModel = false
                playerModel!!.bipedRightLegwear.showModel = false
                alphaFill = aF.value.toDouble()
            }
        }
    }

    override fun onWorldRender(event: RenderEvent) {
        if (onlyOneEsp.value) {
            if (player == null || mc.world == null || mc.player == null) {
                return
            }
            GL11.glLineWidth(1.0f)
            val fillColorS = Color((rF.value as Int), (bF.value as Int), (gF.value as Int), (aF.value as Int))
            var fillA = fillColorS.alpha
            val time = System.currentTimeMillis() - startTime!! - (fadestart.value as Number).toLong()
            if (System.currentTimeMillis() - startTime!! > (fadestart.value as Number).toLong()) {
                var normal = this.normalize(time.toDouble(), 0.0, (fadetime.value as Number).toDouble())
                normal = MathHelper.clamp(normal, 0.0, 1.0)
                normal = -normal + 1.0
                fillA *= normal.toInt()
            }
            val fillColor = newAlpha(fillColorS, fillA)
            if (player != null && playerModel != null) {
                prepare(7)
                GL11.glPushAttrib(1048575)
                GL11.glEnable(2881)
                GL11.glEnable(2848)
                if (alphaFill > 1.0) {
                    alphaFill -= fadetime.value
                }
                val fillFinal = Color(fillColor.red, fillColor.green, fillColor.blue, alphaFill.toInt())
                glColor(fillFinal)
                GL11.glPolygonMode(1032, 6914)
                TotemPopCham.renderEntity(
                    player as EntityLivingBase?,
                    playerModel as ModelBase?,
                    player!!.limbSwing,
                    player!!.limbSwingAmount,
                    1.0f
                )
                GL11.glPolygonMode(1032, 6913)
                TotemPopCham.renderEntity(
                    player as EntityLivingBase?,
                    playerModel as ModelBase?,
                    player!!.limbSwing,
                    player!!.limbSwingAmount,
                    1.0f
                )
                GL11.glPolygonMode(1032, 6914)
                GL11.glPopAttrib()
                release()
            }
        } else if (!(onlyOneEsp.value as Boolean)) {
            TotemPopCham(player, playerModel, startTime, alphaFill)
        }
    }

    fun normalize(value: Double, min: Double, max: Double): Double {
        return (value - min) / (max - min)
    }

    companion object {
        @JvmField
        var INSTANCE = PopChams()
        fun newAlpha(color: Color, alpha: Int): Color {
            return Color(color.red, color.green, color.blue, alpha)
        }

        fun glColor(color: Color) {
            GL11.glColor4f(
                color.red.toFloat() / 255.0f,
                color.green.toFloat() / 255.0f,
                color.blue.toFloat() / 255.0f,
                color.alpha.toFloat() / 255.0f
            )
        }
    }
}