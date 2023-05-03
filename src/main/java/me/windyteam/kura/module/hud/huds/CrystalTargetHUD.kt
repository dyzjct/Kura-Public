package me.windyteam.kura.module.hud.huds

import me.windyteam.kura.Kura
import me.windyteam.kura.module.HUDModule
import me.windyteam.kura.module.ModuleManager
import me.windyteam.kura.module.hud.info.Player.Companion.drawEntityOnScreen
import me.windyteam.kura.module.modules.crystalaura.AutoCrystal
import me.windyteam.kura.utils.combat.HoleUtil
import me.windyteam.kura.utils.gl.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.StringUtils
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.math.min

@HUDModule.Info(name = "CrystalTargetHUD", x = 250, y = 150, width = 150, height = 80)
object CrystalTargetHUD : HUDModule() {
    private val size = isetting("Size", 30,1,100)
    private val blur = bsetting("Blur",true)
    private val safeColor = csetting("TargetSafeColor",Color(255,0,0))
    private val unSafeColor = csetting("TargetUnSafeColor",Color(0,255,0))
    private val drawMode = msetting("DrawMode",DrawMode.Target)
    private val mc = Minecraft.getMinecraft()
    private var joni: DynamicTexture? = null
    private var sjq: DynamicTexture? = null
    private var hsy: DynamicTexture? = null
    private var hyp: DynamicTexture? = null
    private var rn: DynamicTexture? = null
    private var zyx: DynamicTexture? = null

    override fun onConfigLoad() {
        joni = DynamicTexture(
            ImageIO.read(
                Objects.requireNonNull(
                    Kura::class.java.getResourceAsStream("/assets/kura/targetHud/Joni.jpg")
                )
            )
        )
        sjq = DynamicTexture(
            ImageIO.read(
                Objects.requireNonNull(
                    Kura::class.java.getResourceAsStream("/assets/kura/targetHud/ShiJunQi.jpg")
                )
            )
        )
        hsy = DynamicTexture(
            ImageIO.read(
                Objects.requireNonNull(
                    Kura::class.java.getResourceAsStream("/assets/kura/targetHud/HuShuYu.png")
                )
            )
        )
        hyp = DynamicTexture(
            ImageIO.read(
                Objects.requireNonNull(
                    Kura::class.java.getResourceAsStream("/assets/kura/targetHud/HeYuePing.jpg")
                )
            )
        )
        rn = DynamicTexture(
            ImageIO.read(
                Objects.requireNonNull(
                    Kura::class.java.getResourceAsStream("/assets/kura/targetHud/RuiNan.png")
                )
            )
        )
        zyx = DynamicTexture(
            ImageIO.read(
                Objects.requireNonNull(
                    Kura::class.java.getResourceAsStream("/assets/kura/targetHud/ZhuYiXuan.png")
                )
            )
        )
    }

    override fun onRender() {
        if (ModuleManager.getModuleByClass(AutoCrystal::class.java).isEnabled) {
            if (AutoCrystal.renderEnt != null) {
                val name = StringUtils.stripControlCodes(AutoCrystal.renderEnt!!.name)
                val renderX = x + 35
                val renderY = y + 10
                val healthPercentage: Float = AutoCrystal.renderEnt!!.health / AutoCrystal.renderEnt!!.maxHealth
                val maxX = 30.coerceAtLeast(mc.fontRenderer.getStringWidth(name) + 30).toFloat() + size.value
                if (blur.value) {
                    Gui.drawRect(
                        renderX,
                        renderY,
                        ((renderX + maxX).toInt()), renderY -10 + size.value, Color(0f, 0f, 0f, 0.6f).rgb
                    )
                }
                Gui.drawRect(
                    renderX,
                    renderY - 10 + size.value,
                    (renderX + maxX * healthPercentage).toInt(),
                    renderY + size.value,
                    getHealthColor(AutoCrystal.renderEnt!!)
                )
                val isSafeColor = Color(
                    safeColor.value.red / 255f,
                    safeColor.value.green / 255f,
                    safeColor.value.blue / 255f,
                    1f
                ).rgb
                val isUnSafeColor = Color(
                    unSafeColor.value.red / 255f,
                    unSafeColor.value.green / 255f,
                    unSafeColor.value.blue / 255f,
                    1f
                ).rgb
                mc.fontRenderer.drawStringWithShadow(name, (renderX + 55).toFloat(), renderY + 7f, -1)
                mc.fontRenderer.drawStringWithShadow(if (HoleUtil.isInHole(AutoCrystal.renderEnt) || HoleUtil.isInBurrow(AutoCrystal.renderEnt)) { "is Safety" } else {"is Unsafe"}, (renderX + 55).toFloat(), renderY + 20f, if (HoleUtil.isInHole(AutoCrystal.renderEnt) || HoleUtil.isInBurrow(AutoCrystal.renderEnt)) { isSafeColor } else { isUnSafeColor })
                when (drawMode.value) {
                    DrawMode.Target -> {
                        drawEntityOnScreen(
                            renderX + 12,
                            renderY + 33,
                            15,
                            AutoCrystal.renderEnt!!.rotationYaw,
                            AutoCrystal.renderEnt!!.rotationPitch,
                            AutoCrystal.renderEnt!!
                        )
                    }
                    DrawMode.Joni -> {
                        drawImage(renderX,renderY,joni!!)
                    }
                    DrawMode.ShiJunQi -> {
                        drawImage(renderX,renderY, sjq!!)
                    }
                    DrawMode.HuShuYu -> {
                        drawImage(renderX,renderY,hsy!!)
                    }
                    DrawMode.HeYuePing -> {
                        drawImage(renderX,renderY,hyp!!)
                    }
                    DrawMode.RuiNan -> {
                        drawImage(renderX,renderY,rn!!)
                    }
                    DrawMode.ZhuYiXuan -> {
                        drawImage(renderX,renderY,zyx!!)
                    }
                }
            }
        }
    }

    private fun drawImage(x:Int , y:Int , mark:DynamicTexture) {
        GL11.glPushMatrix()
        RenderUtils.bindTexture(mark.glTextureId)
        RenderUtils.drawTexture(
            x.toDouble(),
            y.toDouble(),
            50.0,
            50.0
        )
        GL11.glPopMatrix()
    }

    private fun getHealthColor(player: EntityLivingBase): Int {
        val f = player.health
        val f2 = player.maxHealth
        val f3 = max(0.0f, min(f, f2) / f2)
        return Color.HSBtoRGB(f3 / 3.0f, 1.0f, 0.75f) or -0x1000000
    }

    enum class DrawMode {
        Target,Joni,ShiJunQi,HuShuYu,HeYuePing,RuiNan,ZhuYiXuan
    }
}