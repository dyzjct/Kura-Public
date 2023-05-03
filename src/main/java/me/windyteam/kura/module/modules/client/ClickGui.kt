package me.windyteam.kura.module.modules.client

import me.windyteam.kura.Kura
import me.windyteam.kura.gui.clickgui.GUIRender
import me.windyteam.kura.gui.clickgui.guis.ClickGuiScreen
import me.windyteam.kura.manager.FileManager
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.utils.gl.RenderUtils
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraftforge.client.event.RenderGameOverlayEvent
import org.lwjgl.opengl.GL11
import java.util.*
import javax.imageio.ImageIO

@Module.Info(name = "ClickGUI", category = Category.CLIENT, keyCode = 22, visible = false)
class ClickGui : Module() {
    private var image = settings("Image", false)
    private var imageMode = settings("ImageMode", 1, 1, 8)
    private var imageX = settings("x", 606, 0, 1000).b(image)
    private var imageY = settings("y", 218, 0, 1000).b(image)
    private var screen: ClickGuiScreen? = null
    private var lastReportImage = 0
    override fun onInit() {
        INSTANCE = this
        setGUIScreen(ClickGuiScreen())
    }

    override fun onEnable() {
        if (!fullNullCheck() && mc.currentScreen !is ClickGuiScreen) {
            GUIRender.getINSTANCE().initGui()
            mc.displayGuiScreen(screen)
        }

        if (image.value && lastReportImage != imageMode.value){
            when (imageMode.value) {
                1 -> {
                    mark = DynamicTexture(
                        ImageIO.read(
                            Objects.requireNonNull(
                                Kura::class.java.getResourceAsStream("/assets/kura/images/image1.png")
                            )
                        )
                    )
                    lastReportImage = 1
                }

                2 -> {
                    mark = DynamicTexture(
                        ImageIO.read(
                            Objects.requireNonNull(
                                Kura::class.java.getResourceAsStream("/assets/kura/images/image2.png")
                            )
                        )
                    )
                    lastReportImage = 2
                }

                3 -> {
                    mark = DynamicTexture(
                        ImageIO.read(
                            Objects.requireNonNull(
                                Kura::class.java.getResourceAsStream("/assets/kura/images/image3.png")
                            )
                        )
                    )
                    lastReportImage = 3
                }

                4 -> {
                    mark = DynamicTexture(
                        ImageIO.read(
                            Objects.requireNonNull(
                                Kura::class.java.getResourceAsStream("/assets/kura/images/image4.png")
                            )
                        )
                    )
                    lastReportImage = 4
                }

                5 -> {
                    mark = DynamicTexture(
                        ImageIO.read(
                            Objects.requireNonNull(
                                Kura::class.java.getResourceAsStream("/assets/kura/images/image5.png")
                            )
                        )
                    )
                    lastReportImage = 5
                }

                6 -> {
                    mark = DynamicTexture(
                        ImageIO.read(
                            Objects.requireNonNull(
                                Kura::class.java.getResourceAsStream("/assets/kura/images/image6.png")
                            )
                        )
                    )
                    lastReportImage = 6
                }

                7 -> {
                    mark = DynamicTexture(
                        ImageIO.read(
                            Objects.requireNonNull(
                                Kura::class.java.getResourceAsStream("/assets/kura/images/image7.png")
                            )
                        )
                    )
                    lastReportImage = 7
                }

                8 -> {
                    mark = DynamicTexture(
                        ImageIO.read(
                            Objects.requireNonNull(
                                Kura::class.java.getResourceAsStream("/assets/kura/images/image8.png")
                            )
                        )
                    )
                    lastReportImage = 8
                }
            }
        }
    }

    override fun onRender2D(event: RenderGameOverlayEvent.Post) {
        if (fullNullCheck()) return
        if (image.value && isEnabled) {
            drawImageTest()
        }
    }

    private fun drawImageTest() {
        GL11.glPushMatrix()
        GL11.glTranslated(
            (imageX.value + 320).toDouble() - 345.0 / 2.0 - 3.0,
            y.toDouble() + height.toDouble() / 2.0,
            0.0
        )
        GL11.glRotated(0.0, 0.0, 0.0, 1.0)
        RenderUtils.bindTexture(mark!!.glTextureId)
        RenderUtils.drawTexture(
            (imageX.value.toFloat() - 2).toDouble(),
            (imageY.value.toFloat() - 36).toDouble(),
            (320 - 7).toDouble(),
            (345 - 7).toDouble()
        )
        GL11.glPopMatrix()
    }

    override fun onDisable() {
        if (!fullNullCheck() && mc.currentScreen is ClickGuiScreen) {
            mc.displayGuiScreen(null)
        }
        FileManager.saveAll()
    }

    private fun setGUIScreen(screen: ClickGuiScreen) {
        this.screen = screen
    }

    companion object {
        @JvmField
        var INSTANCE: ClickGui? = null
        var mark: DynamicTexture? = null
        val instance: Colors?
            // NULL
            get() = null
    }
}
