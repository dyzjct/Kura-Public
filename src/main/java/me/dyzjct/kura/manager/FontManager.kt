package me.dyzjct.kura.manager

import me.dyzjct.kura.utils.font.CFont
import me.dyzjct.kura.utils.font.CFontRenderer
import java.awt.Font

object FontManager {
    var fonts: CFontRenderer? = null
    var font2: CFontRenderer? =null

    fun onInit() {
        fonts = CFontRenderer(CFont.CustomFont("/assets/fonts/font.ttf", 20f, Font.BOLD), true, false)
        font2 = CFontRenderer(CFont.CustomFont("/assets/fonts/Comfortaa.ttf", 20f, Font.BOLD), true, false)
    }
}