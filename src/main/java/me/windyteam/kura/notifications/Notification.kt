package me.windyteam.kura.notifications

import me.windyteam.kura.notifications.other.Animation
import me.windyteam.kura.notifications.other.Easing
import me.windyteam.kura.notifications.other.GLutil
import me.windyteam.kura.manager.FontManager
import me.windyteam.kura.utils.mc.ChatUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import java.awt.Color


class Notification(val message: String, val type: NotificationType) {
    val animation: Animation = Animation({ 500f }, false, { Easing.BACK_IN_OUT })
    val progress: Animation = Animation({ 800f }, false, { Easing.LINEAR })

    fun render(y: Float) {
        animation.state = if (!progress.state) {
            true
        } else {
            progress.getAnimationFactor() != 1.0
        }

        if (animation.getAnimationFactor() == 1.0) {
            progress.state = true
        }

        val scaledResolution = ScaledResolution(Minecraft.getMinecraft())

        val width = (ChatUtil.getStringWidth(message) + 12f).coerceAtLeast(75f)
        val x = scaledResolution.scaledWidth - ((width + 10) * animation.getAnimationFactor()).toFloat()

        GLutil.drawRect(x, y, width, 20f, Color(0, 0, 0, 150))
        FontManager.font2!!.drawStringWithShadow(message, x + 6.0, y + 6.0, 0)
        GLutil.drawRect(x, y + 19f, width * progress.getAnimationFactor().toFloat(), 1f, type.colour)
    }
}