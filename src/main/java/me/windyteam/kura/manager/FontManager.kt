package me.windyteam.kura.manager

import me.windyteam.kura.utils.font.CFont
import me.windyteam.kura.utils.font.CFontRenderer
import java.awt.Font

object FontManager {
    var fonts: CFontRenderer? = null
    var font2: CFontRenderer? =null
    var font3: CFontRenderer? =null

    fun onInit() {
        fonts = CFontRenderer(
            CFont.CustomFont(
                "/assets/fonts/font.ttf",
                20f,
                Font.BOLD
            ), true, false
        )
        font2 = CFontRenderer(
            CFont.CustomFont(
                "/assets/fonts/Comfortaa.ttf",
                20f,
                Font.BOLD
            ), true, false
        )
        font3 = CFontRenderer(
            CFont.CustomFont(
                "/assets/fonts/FZLanTYJW_Te.ttf",
                20f,
                Font.BOLD
            ), true, false
        )
    }
}