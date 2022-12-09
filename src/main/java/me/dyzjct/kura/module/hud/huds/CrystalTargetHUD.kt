package me.dyzjct.kura.module.hud.huds

import me.dyzjct.kura.module.HUDModule
import me.dyzjct.kura.module.ModuleManager
import me.dyzjct.kura.module.hud.info.Player.Companion.drawEntityOnScreen
import me.dyzjct.kura.module.modules.sexy.MelonAuraPlus
import me.dyzjct.kura.setting.IntegerSetting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.StringUtils
import java.awt.Color
import kotlin.math.max
import kotlin.math.min

@HUDModule.Info(name = "CrystalTargetHUD", x = 250, y = 150, width = 150, height = 80)
class CrystalTargetHUD : HUDModule() {
    val size:IntegerSetting = isetting("Size", 30,1,100)
    val mc: Minecraft = Minecraft.getMinecraft()
    override fun onRender() {
        if (ModuleManager.getModuleByClass(MelonAuraPlus::class.java).isEnabled) {
            if (MelonAuraPlus.renderEnt != null) {
                val name = StringUtils.stripControlCodes(MelonAuraPlus.renderEnt.name)
                val renderX = x + 35
                val renderY = y + 10
                val healthPercentage: Float = MelonAuraPlus.renderEnt.health / MelonAuraPlus.renderEnt.maxHealth
                val maxX = 30.coerceAtLeast(mc.fontRenderer.getStringWidth(name) + 30).toFloat() + size.value
                Gui.drawRect(
                    renderX,
                    renderY,
                    ((renderX + maxX).toInt()), renderY + 40 + size.value, Color(0f, 0f, 0f, 0.6f).rgb
                )
                Gui.drawRect(
                    renderX,
                    renderY + 50 + size.value,
                    (renderX + maxX * healthPercentage).toInt(),
                    renderY + 75 + size.value,
                    getHealthColor(MelonAuraPlus.renderEnt)
                )
                mc.fontRenderer.drawStringWithShadow(name, (renderX + 25).toFloat(), renderY + 7f, -1)
                drawEntityOnScreen(
                    renderX + 12,
                    renderY + 33,
                    15,
                    MelonAuraPlus.renderEnt.rotationYaw,
                    MelonAuraPlus.renderEnt.rotationPitch,
                    MelonAuraPlus.renderEnt
                )
            }
        }
    }

    private fun getHealthColor(player: EntityLivingBase): Int {
        val f = player.health
        val f2 = player.maxHealth
        val f3 = max(0.0f, min(f, f2) / f2)
        return Color.HSBtoRGB(f3 / 3.0f, 1.0f, 0.75f) or -0x1000000
    }
}