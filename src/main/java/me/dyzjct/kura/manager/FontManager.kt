package me.dyzjct.kura.manager

import me.dyzjct.kura.utils.font.CFont
import me.dyzjct.kura.utils.font.CFontRenderer
import java.awt.Font

object FontManager {
    var fonts: CFontRenderer? = null

    fun onInit() {
        fonts = CFontRenderer(CFont.CustomFont("/assets/fonts/font.ttf", 20f, Font.BOLD), true, false)
    }

    fun drawString(text: String, x: Float, z: Float, color: Int) {
        if (fonts != null) {
            fonts!!.drawString(text, x, z, color)
        }
    }

    fun drawStringShadow(text: String, x: Double, z: Double, color: Int) {
        if (fonts != null) {
            fonts!!.drawStringWithShadow(text, x, z, color)
        }
    }

    fun getWidth(text: String): Int {
        return fonts!!.getStringWidth(text)
    }

    fun getHeight(): Int {
        return fonts!!.height
    }
}