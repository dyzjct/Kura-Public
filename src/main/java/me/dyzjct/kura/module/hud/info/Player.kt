package me.dyzjct.kura.module.hud.info

import me.dyzjct.kura.module.HUDModule
import me.dyzjct.kura.setting.Setting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import kotlin.math.atan

@HUDModule.Info(name = "PlayerView", x = 60, y = 60, width = 15, height = 15)
class Player : HUDModule() {
    private val size: Setting<Int> = isetting("Size", 50, 0, 300)
    private fun boxRender(x: Int, y: Int, size: Int) {
        val var1 = ScaledResolution(mc)
        drawEntityOnScreen(
            x, y, size, x.toFloat() - Mouse.getX()
                .toFloat(), (-var1.scaledHeight).toFloat() + Mouse.getY().toFloat(), mc.player
        )
    }

    override fun onRender() {
        boxRender(x + 17, y + height, size.value)
    }

    companion object {
        @JvmStatic
        fun drawEntityOnScreen(
            p_147046_0_: Int,
            p_147046_1_: Int,
            p_147046_2_: Int,
            p_147046_3_: Float,
            p_147046_4_: Float,
            p_147046_5_: EntityLivingBase
        ) {
            GlStateManager.enableColorMaterial()
            GlStateManager.pushMatrix()
            GlStateManager.shadeModel(GL11.GL_SMOOTH)
            GlStateManager.translate(p_147046_0_.toFloat(), p_147046_1_.toFloat(), 50.0f)
            GlStateManager.scale((-p_147046_2_).toFloat(), p_147046_2_.toFloat(), p_147046_2_.toFloat())
            GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f)
            val yawOffset = p_147046_5_.renderYawOffset
            val yaw = p_147046_5_.rotationYaw
            val pitch = p_147046_5_.rotationPitch
            val prevYawHead = p_147046_5_.prevRotationYawHead
            val yawHead = p_147046_5_.rotationYawHead
            GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f)
            RenderHelper.enableStandardItemLighting()
            GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f)
            GlStateManager.rotate(-atan((p_147046_4_ / 40.0f).toDouble()).toFloat() * 20.0f, 1.0f, 0.0f, 0.0f)
            p_147046_5_.renderYawOffset = atan((p_147046_3_ / 40.0f).toDouble()).toFloat() * 20.0f
            p_147046_5_.rotationYaw = atan((p_147046_3_ / 40.0f).toDouble()).toFloat() * 40.0f
            p_147046_5_.rotationPitch = -atan((p_147046_4_ / 40.0f).toDouble()).toFloat() * 20.0f
            p_147046_5_.rotationYawHead = p_147046_5_.rotationYaw
            p_147046_5_.prevRotationYawHead = p_147046_5_.rotationYaw
            GlStateManager.translate(0.0f, 0.0f, 0.0f)
            val renderManager = Minecraft.getMinecraft().getRenderManager()
            renderManager.setPlayerViewY(180.0f)
            renderManager.isRenderShadow = false
            renderManager.renderEntity(p_147046_5_, 0.0, 0.0, 0.0, 0.0f, 1.0f, false)
            renderManager.isRenderShadow = true
            p_147046_5_.renderYawOffset = yawOffset
            p_147046_5_.rotationYaw = yaw
            p_147046_5_.rotationPitch = pitch
            p_147046_5_.prevRotationYawHead = prevYawHead
            p_147046_5_.rotationYawHead = yawHead
            GlStateManager.popMatrix()
            RenderHelper.disableStandardItemLighting()
            GlStateManager.disableRescaleNormal()
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit)
            GlStateManager.disableTexture2D()
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
        }
    }
}