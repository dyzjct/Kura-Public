package me.windyteam.kura.module.modules.render

import me.windyteam.kura.event.events.render.RenderEvent
import me.windyteam.kura.friend.FriendManager
import me.windyteam.kura.mixin.client.IEntityRenderer
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.Module.Info
import me.windyteam.kura.utils.inventory.InventoryUtil
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GLAllocation
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.boss.EntityDragon
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.monster.EntityGhast
import net.minecraft.entity.monster.EntityGolem
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.monster.EntitySlime
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.passive.EntityBat
import net.minecraft.entity.passive.EntitySquid
import net.minecraft.entity.passive.EntityVillager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.MathHelper
import net.minecraftforge.client.event.RenderGameOverlayEvent
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.GLU
import java.awt.Color
import java.text.DecimalFormat
import java.util.regex.Pattern
import javax.vecmath.Vector3d
import javax.vecmath.Vector4d
import kotlin.math.pow

@Info(name = "ESP2D", category = Category.RENDER)
object ESP2D : Module() {
    private val bbtt = settings("2B2T Mode", true)
    private val outline = settings("Outline", true)
    private val boxMode = settings("Mode", Mode.Corners)
    private val healthBar = settings("Health-bar", true)
    private val hpBarMode = settings("HBar-Mode", Mode2.Dot)
    private val healthNumber = settings("HealthNumber", true)
    private val hpMode = settings("HP-Mode", Mode4.Health)
    private val hoverValue = settings("Details-HoverOnly", false)
    private val tagsValue = settings("Tags", true)
    private val tagsBGValue = settings("Tags-Background", true)
    private val armorBar = settings("ArmorBar", true)
    private val outlineFont = settings("OutlineFont", true)
    private val clearNameValue = settings("Use-Clear-Name", false)
    private val localPlayer = settings("Local-Player", true)
    private val friendColor = settings("FriendColor", true)
    private val mobs = settings("Mobs", false)
    private val animals = settings("Animals", false)
    private val droppedItems = settings("Dropped-Items", false)
    private val colorModeValue = settings("ColorMode", Mode5.Custom)
    private val colorRedValue = settings("Red", 255, 0, 255)
    private val colorGreenValue = settings("Green", 255, 0, 255)
    private val colorBlueValue = settings("Blue", 255, 0, 255)
    private val saturationValue = settings("Saturation", 1.0f, 0.0f, 1.0f)
    private val brightnessValue = settings("Brightness", 1.0f, 0.0f, 1.0f)
    private val mixerSecondsValue = settings("Seconds", 2, 1, 10)
    private val fontScaleValue = settings("Font-Scale", 0.5f, 0.0f, 1.0f)
    private val dFormat = DecimalFormat("0.0")
    private val viewport = GLAllocation.createDirectIntBuffer(16)
    private val modelview = GLAllocation.createDirectFloatBuffer(16)
    private val projection = GLAllocation.createDirectFloatBuffer(16)
    private val vector = GLAllocation.createDirectFloatBuffer(4)
    private val backgroundColor = Color(0, 0, 0, 120).rgb
    private val black = Color.BLACK.rgb


    private fun getColor(entity: Entity?): Color {
        return if (entity is EntityLivingBase && entity is EntityPlayer && friendColor.value && FriendManager.isFriend(
                entity
            )
        ) {
            Color.cyan
        } else when (colorModeValue.value) {
            Mode5.Custom -> {
                Color(colorRedValue.value, colorGreenValue.value, colorBlueValue.value)
            }

            Mode5.AnotherRainbow -> {
                Color(
                    getRainbowOpaque(
                        mixerSecondsValue.value, saturationValue.value, brightnessValue.value, 0
                    )
                )
            }

            Mode5.Slowly -> {
                slowlyRainbow(
                    System.nanoTime(), 0, saturationValue.value, brightnessValue.value
                )
            }

            else -> {
                fade(
                    Color(
                        colorRedValue.value, colorGreenValue.value, colorBlueValue.value
                    ), 0, 100
                )
            }
        }
    }

    override fun onDisable() {
        collectedEntities!!.clear()
    }

    override fun onWorldRender(event: RenderEvent) {
        GL11.glPushMatrix()
        collectEntities()
        val partialTicks = event.partialTicks
        val scaledResolution = ScaledResolution(mc)
        val scaleFactor = scaledResolution.scaleFactor
        val scaling = scaleFactor / scaleFactor.toDouble().pow(2.0)
        GL11.glScaled(scaling, scaling, scaling)
        val black = black
        val renderMng = mc.getRenderManager()
        val entityRenderer = mc.entityRenderer
        val outline = outline.value
        val health = healthBar.value
        var i = 0
        val collectedEntitiesSize = collectedEntities!!.size
        while (i < collectedEntitiesSize) {
            val entity: Entity = collectedEntities!![i]
            val color = getColor(entity).rgb
            if (isInViewFrustrum(entity)) {
                val x = interpolate(entity.posX, entity.lastTickPosX, partialTicks.toDouble())
                val y = interpolate(entity.posY, entity.lastTickPosY, partialTicks.toDouble())
                val z = interpolate(entity.posZ, entity.lastTickPosZ, partialTicks.toDouble())
                val width = entity.width / 1.5
                val height = entity.height + if (entity.isSneaking) -0.3 else 0.2
                val aabb = AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width)
                val vectors: List<*> = listOf(
                    Vector3d(aabb.minX, aabb.minY, aabb.minZ),
                    Vector3d(aabb.minX, aabb.maxY, aabb.minZ),
                    Vector3d(aabb.maxX, aabb.minY, aabb.minZ),
                    Vector3d(aabb.maxX, aabb.maxY, aabb.minZ),
                    Vector3d(aabb.minX, aabb.minY, aabb.maxZ),
                    Vector3d(aabb.minX, aabb.maxY, aabb.maxZ),
                    Vector3d(aabb.maxX, aabb.minY, aabb.maxZ),
                    Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ)
                )
                (mc.entityRenderer as IEntityRenderer).invokeSetupCameraTransform(partialTicks, 0)
                var position: Vector4d? = null
                for (o in vectors) {
                    var vector = o as Vector3d?
                    vector = project2D(
                        scaleFactor,
                        vector!!.x - renderMng.viewerPosX,
                        vector.y - renderMng.viewerPosY,
                        vector.z - renderMng.viewerPosZ
                    )
                    if (vector != null && vector.z >= 0.0 && vector.z < 1.0) {
                        if (position == null) {
                            position = Vector4d(vector.x, vector.y, vector.z, 0.0)
                        }
                        position.x = vector.x.coerceAtMost(position.x)
                        position.y = vector.y.coerceAtMost(position.y)
                        position.z = vector.x.coerceAtLeast(position.z)
                        position.w = vector.y.coerceAtLeast(position.w)
                    }
                }
                if (position != null) {
                    entityRenderer.setupOverlayRendering()
                    val posX = position.x
                    val posY = position.y
                    val endPosX = position.z
                    val endPosY = position.w
                    if (outline) {
                        if (boxMode.value == Mode.Box) {
                            newDrawRect(posX - 1.0, posY, posX + 0.5, endPosY + 0.5, black)
                            newDrawRect(posX - 1.0, posY - 0.5, endPosX + 0.5, posY + 0.5 + 0.5, black)
                            newDrawRect(endPosX - 0.5 - 0.5, posY, endPosX + 0.5, endPosY + 0.5, black)
                            newDrawRect(posX - 1.0, endPosY - 0.5 - 0.5, endPosX + 0.5, endPosY + 0.5, black)
                            newDrawRect(posX - 0.5, posY, posX + 0.5 - 0.5, endPosY, color)
                            newDrawRect(posX, endPosY - 0.5, endPosX, endPosY, color)
                            newDrawRect(posX - 0.5, posY, endPosX, posY + 0.5, color)
                            newDrawRect(endPosX - 0.5, posY, endPosX, endPosY, color)
                        } else {
                            newDrawRect(posX + 0.5, posY, posX - 1.0, posY + (endPosY - posY) / 4.0 + 0.5, black)
                            newDrawRect(posX - 1.0, endPosY, posX + 0.5, endPosY - (endPosY - posY) / 4.0 - 0.5, black)
                            newDrawRect(posX - 1.0, posY - 0.5, posX + (endPosX - posX) / 3.0 + 0.5, posY + 1.0, black)
                            newDrawRect(endPosX - (endPosX - posX) / 3.0 - 0.5, posY - 0.5, endPosX, posY + 1.0, black)
                            newDrawRect(endPosX - 1.0, posY, endPosX + 0.5, posY + (endPosY - posY) / 4.0 + 0.5, black)
                            newDrawRect(
                                endPosX - 1.0, endPosY, endPosX + 0.5, endPosY - (endPosY - posY) / 4.0 - 0.5, black
                            )
                            newDrawRect(
                                posX - 1.0, endPosY - 1.0, posX + (endPosX - posX) / 3.0 + 0.5, endPosY + 0.5, black
                            )
                            newDrawRect(
                                endPosX - (endPosX - posX) / 3.0 - 0.5,
                                endPosY - 1.0,
                                endPosX + 0.5,
                                endPosY + 0.5,
                                black
                            )
                            newDrawRect(posX, posY, posX - 0.5, posY + (endPosY - posY) / 4.0, color)
                            newDrawRect(posX, endPosY, posX - 0.5, endPosY - (endPosY - posY) / 4.0, color)
                            newDrawRect(posX - 0.5, posY, posX + (endPosX - posX) / 3.0, posY + 0.5, color)
                            newDrawRect(endPosX - (endPosX - posX) / 3.0, posY, endPosX, posY + 0.5, color)
                            newDrawRect(endPosX - 0.5, posY, endPosX, posY + (endPosY - posY) / 4.0, color)
                            newDrawRect(endPosX - 0.5, endPosY, endPosX, endPosY - (endPosY - posY) / 4.0, color)
                            newDrawRect(posX, endPosY - 0.5, posX + (endPosX - posX) / 3.0, endPosY, color)
                            newDrawRect(endPosX - (endPosX - posX) / 3.0, endPosY - 0.5, endPosX - 0.5, endPosY, color)
                        }
                    }
                    val living = entity is EntityLivingBase
                    if (living) {
                        val entityLivingBase = entity as EntityLivingBase
                        if (health) {
                            var armorValue = entityLivingBase.health
                            var itemDurability = entityLivingBase.maxHealth
                            if (bbtt.value && entityLivingBase is EntityPlayer) {
                                armorValue = entityLivingBase.getHealth() + entityLivingBase.getAbsorptionAmount()
                                itemDurability = entityLivingBase.getMaxHealth() + 16.0f
                            }
                            if (armorValue > itemDurability) {
                                armorValue = itemDurability
                            }
                            val durabilityWidth = (armorValue / itemDurability).toDouble()
                            val textWidth = (endPosY - posY) * durabilityWidth
                            val healthDisplay =
                                dFormat.format((entityLivingBase.health + entityLivingBase.getAbsorptionAmount()).toDouble()) + " ��c\u2764"
                            val healthPercent =
                                (entityLivingBase.health / itemDurability * 100.0f).toInt().toString() + "%"
                            if (healthNumber.value && (!hoverValue.value || entity === mc.player || isHovering(
                                    posX, endPosX, posY, endPosY, scaledResolution
                                ))
                            ) {
                                drawScaledString(
                                    if (hpMode.value == Mode4.Health) healthDisplay else healthPercent,
                                    posX - 4.0 - mc.fontRenderer.getStringWidth(if (hpMode.value == Mode4.Health) healthDisplay else healthPercent) * fontScaleValue.value,
                                    endPosY - textWidth - mc.fontRenderer.FONT_HEIGHT / 2.0f * fontScaleValue.value,
                                    fontScaleValue.value.toDouble()
                                )
                            }
                            newDrawRect(posX - 3.5, posY - 0.5, posX - 1.5, endPosY + 0.5, backgroundColor)
                            if (armorValue > 0.0f) {
                                val healthColor = getHealthColor(armorValue, itemDurability).rgb
                                val deltaY = endPosY - posY
                                if (hpBarMode.value == Mode2.Dot && deltaY >= 60.0) {
                                    var k = 0.0
                                    while (k < 10.0) {
                                        val reratio = MathHelper.clamp(
                                            armorValue - k * (itemDurability / 10.0), 0.0, itemDurability / 10.0
                                        ) / (itemDurability / 10.0)
                                        val hei = (deltaY / 10.0 - 0.5) * reratio
                                        newDrawRect(
                                            posX - 3.0,
                                            endPosY - (deltaY + 0.5) / 10.0 * k,
                                            posX - 2.0,
                                            endPosY - (deltaY + 0.5) / 10.0 * k - hei,
                                            healthColor
                                        )
                                        ++k
                                    }
                                } else {
                                    newDrawRect(posX - 3.0, endPosY, posX - 2.0, endPosY - textWidth, healthColor)
                                }
                            }
                        }
                    }
                    if (living && tagsValue.value) {
                        val entityLivingBase = entity as EntityLivingBase
                        var entName =
                            if (clearNameValue.value) entityLivingBase.name else entityLivingBase.displayName.formattedText
                        if (friendColor.value && FriendManager.isFriend(entityLivingBase.name)) {
                            entName = "��b$entName"
                        }
                        if (tagsBGValue.value) {
                            newDrawRect(
                                posX + (endPosX - posX) / 2.0 - (mc.fontRenderer.getStringWidth(entName) / 2.0f + 2.0f) * fontScaleValue.value,
                                posY - 1.0 - (mc.fontRenderer.FONT_HEIGHT + 2.0f) * fontScaleValue.value,
                                posX + (endPosX - posX) / 2.0 + (mc.fontRenderer.getStringWidth(entName) / 2.0f + 2.0f) * fontScaleValue.value,
                                posY - 1.0 + 2.0f * fontScaleValue.value,
                                -1610612736
                            )
                        }
                        drawScaledCenteredString(
                            entName,
                            posX + (endPosX - posX) / 2.0,
                            posY - 1.0 - mc.fontRenderer.FONT_HEIGHT * fontScaleValue.value,
                            fontScaleValue.value.toDouble()
                        )
                    }
                    if (armorBar.value && entity is EntityPlayer && living) {
                        val entityLivingBase = entity as EntityLivingBase
                        val constHeight = (endPosY - posY) / 4.0
                        for (m in 4 downTo 1) {
                            var armorStack = entityLivingBase.getItemStackFromSlot(EntityEquipmentSlot.HEAD)
                            if (m == 3) {
                                armorStack = entityLivingBase.getItemStackFromSlot(EntityEquipmentSlot.CHEST)
                            }
                            if (m == 2) {
                                armorStack = entityLivingBase.getItemStackFromSlot(EntityEquipmentSlot.LEGS)
                            }
                            if (m == 1) {
                                armorStack = entityLivingBase.getItemStackFromSlot(EntityEquipmentSlot.FEET)
                            }
                            val theHeight = constHeight + 0.25
                            newDrawRect(
                                endPosX + 1.5,
                                endPosY + 0.5 - theHeight * m,
                                endPosX + 3.5,
                                endPosY + 0.5 - theHeight * (m - 1),
                                Color(0, 0, 0, 120).rgb
                            )
                            newDrawRect(
                                endPosX + 2.0,
                                endPosY + 0.5 - theHeight * (m - 1) - 0.25,
                                endPosX + 3.0,
                                endPosY + 0.5 - theHeight * (m - 1) - 0.25 - (constHeight - 0.25) * MathHelper.clamp(
                                    InventoryUtil.getItemDurability(armorStack) / armorStack.maxDamage.toDouble(),
                                    0.0,
                                    1.0
                                ),
                                Color(0, 255, 255).rgb
                            )
                        }
                    }
                }
            }
            ++i
        }
        GL11.glPopMatrix()
        GlStateManager.enableBlend()
        GlStateManager.resetColor()
        entityRenderer.setupOverlayRendering()
    }

    private fun renderItemStack(stack: ItemStack, x: Int) {
        GlStateManager.pushMatrix()
        GlStateManager.depthMask(true)
        GlStateManager.clear(256)
        RenderHelper.enableStandardItemLighting()
        mc.getRenderItem().zLevel = -150.0f
        GlStateManager.disableAlpha()
        GlStateManager.enableDepth()
        GlStateManager.disableCull()
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, -26)
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, -26)
        mc.getRenderItem().zLevel = 0.0f
        RenderHelper.disableStandardItemLighting()
        GlStateManager.enableCull()
        GlStateManager.enableAlpha()
        GlStateManager.scale(0.5f, 0.5f, 0.5f)
        GlStateManager.disableDepth()
        GlStateManager.enableDepth()
        GlStateManager.scale(2.0f, 2.0f, 2.0f)
        GlStateManager.popMatrix()
    }

    private fun isHovering(minX: Double, maxX: Double, minY: Double, maxY: Double, sc: ScaledResolution): Boolean {
        return sc.scaledWidth / 2.0 >= minX && sc.scaledWidth / 2.0 < maxX && sc.scaledHeight / 2.0 >= minY && sc.scaledHeight / 2.0 < maxY
    }

    private fun drawOutlineStringWithoutGL(s: String?, x: Float, y: Float, color: Int, fontRenderer: FontRenderer) {
        fontRenderer.drawString(stripColor(s), (x * 2.0f - 1.0f).toInt(), (y * 2.0f).toInt(), Color.BLACK.rgb)
        fontRenderer.drawString(stripColor(s), (x * 2.0f + 1.0f).toInt(), (y * 2.0f).toInt(), Color.BLACK.rgb)
        fontRenderer.drawString(stripColor(s), (x * 2.0f).toInt(), (y * 2.0f - 1.0f).toInt(), Color.BLACK.rgb)
        fontRenderer.drawString(stripColor(s), (x * 2.0f).toInt(), (y * 2.0f + 1.0f).toInt(), Color.BLACK.rgb)
        fontRenderer.drawString(s, (x * 2.0f).toInt(), (y * 2.0f).toInt(), color)
    }

    private fun drawScaledString(text: String, x: Double, y: Double, scale: Double) {
        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, x)
        GlStateManager.scale(scale, scale, scale)
        if (outlineFont.value) {
            drawOutlineStringWithoutGL(text, 0.0f, 0.0f, -1, mc.fontRenderer)
        } else {
            mc.fontRenderer.drawStringWithShadow(text, 0.0f, 0.0f, -1)
        }
        GlStateManager.popMatrix()
    }

    private fun drawScaledCenteredString(text: String, x: Double, y: Double, scale: Double) {
        drawScaledString(text, x - mc.fontRenderer.getStringWidth(text) / 2.0f * scale, y, scale)
    }

    private fun collectEntities() {
        collectedEntities!!.clear()
        val playerEntities: List<Entity> = mc.world.loadedEntityList
        var i = 0
        val playerEntitiesSize = playerEntities.size
        while (i < playerEntitiesSize) {
            val entity: Entity = playerEntities[i]
            if (isSelected(
                    entity, false
                ) || localPlayer.value && entity is EntityPlayerSP && mc.gameSettings.thirdPersonView != 0 || droppedItems.value && entity is EntityItem
            ) {
                collectedEntities!!.add(entity)
            }
            ++i
        }
    }

    fun isSelected(entity: Entity, canAttackCheck: Boolean): Boolean {
        if (entity !is EntityLivingBase || !entity.isEntityAlive() || entity === mc.player) {
            return false
        }
        return if (entity is EntityPlayer) {
            !canAttackCheck || !FriendManager.isFriend(entity) && !entity.isSpectator && !entity.isPlayerSleeping
        } else isMob(entity) && mobs.value || isAnimal(entity) && animals.value
    }

    fun isAnimal(entity: Entity?): Boolean {
        return entity is EntityAnimal || entity is EntitySquid || entity is EntityGolem || entity is EntityVillager || entity is EntityBat
    }

    fun isMob(entity: Entity?): Boolean {
        return entity is EntityMob || entity is EntitySlime || entity is EntityGhast || entity is EntityDragon
    }

    private fun project2D(scaleFactor: Int, x: Double, y: Double, z: Double): Vector3d? {
        GL11.glGetFloat(2982, modelview)
        GL11.glGetFloat(2983, projection)
        GL11.glGetInteger(2978, viewport)
        return if (GLU.gluProject(
                x.toFloat(), y.toFloat(), z.toFloat(), modelview, projection, viewport, vector
            )
        ) Vector3d(
            (vector[0] / scaleFactor).toDouble(),
            ((Display.getHeight() - vector[1]) / scaleFactor).toDouble(),
            vector[2].toDouble()
        ) else null
    }

    enum class Mode {
        Box, Corners
    }

    enum class Mode2 {
        Dot, Line
    }

    enum class Mode4 {
        Health, Percent
    }

    enum class Mode5 {
        Custom, Slowly, AnotherRainbow
    }

    private var COLOR_PATTERN: Pattern? = null
    private val DISPLAY_LISTS_2D: IntArray
    private var frustrum: Frustum? = null
    private var collectedEntities: MutableList<Entity>? = null

    init {
        COLOR_PATTERN = Pattern.compile("(?i)��[0-9A-FK-OR]")
        DISPLAY_LISTS_2D = IntArray(4)
        for (i in DISPLAY_LISTS_2D.indices) {
            DISPLAY_LISTS_2D[i] = GL11.glGenLists(1)
        }
        GL11.glNewList(DISPLAY_LISTS_2D[0], 4864)
        quickDrawRect(-7.0f, 2.0f, -4.0f, 3.0f)
        quickDrawRect(4.0f, 2.0f, 7.0f, 3.0f)
        quickDrawRect(-7.0f, 0.5f, -6.0f, 3.0f)
        quickDrawRect(6.0f, 0.5f, 7.0f, 3.0f)
        GL11.glEndList()
        GL11.glNewList(DISPLAY_LISTS_2D[1], 4864)
        quickDrawRect(-7.0f, 3.0f, -4.0f, 3.3f)
        quickDrawRect(4.0f, 3.0f, 7.0f, 3.3f)
        quickDrawRect(-7.3f, 0.5f, -7.0f, 3.3f)
        quickDrawRect(7.0f, 0.5f, 7.3f, 3.3f)
        GL11.glEndList()
        GL11.glNewList(DISPLAY_LISTS_2D[2], 4864)
        quickDrawRect(4.0f, -20.0f, 7.0f, -19.0f)
        quickDrawRect(-7.0f, -20.0f, -4.0f, -19.0f)
        quickDrawRect(6.0f, -20.0f, 7.0f, -17.5f)
        quickDrawRect(-7.0f, -20.0f, -6.0f, -17.5f)
        GL11.glEndList()
        GL11.glNewList(DISPLAY_LISTS_2D[3], 4864)
        quickDrawRect(7.0f, -20.0f, 7.3f, -17.5f)
        quickDrawRect(-7.3f, -20.0f, -7.0f, -17.5f)
        quickDrawRect(4.0f, -20.3f, 7.3f, -20.0f)
        quickDrawRect(-7.3f, -20.3f, -4.0f, -20.0f)
        GL11.glEndList()
        frustrum = Frustum()
    }


    fun getHealthColor(health: Float, maxHealth: Float): Color {
        val fractions = floatArrayOf(0.0f, 0.5f, 1.0f)
        val colors = arrayOf(Color(108, 0, 0), Color(255, 51, 0), Color.GREEN)
        val progress = health / maxHealth
        return blendColors(fractions, colors, progress)!!.brighter()
    }

    fun blendColors(fractions: FloatArray, colors: Array<Color>, progress: Float): Color? {
        if (fractions.size == colors.size) {
            val indices = getFractionIndices(fractions, progress)
            val range = floatArrayOf(fractions[indices[0]], fractions[indices[1]])
            val colorRange = arrayOf(colors[indices[0]], colors[indices[1]])
            val max = range[1] - range[0]
            val value = progress - range[0]
            val weight = value / max
            return blend(colorRange[0], colorRange[1], (1.0f - weight).toDouble())
        }
        throw IllegalArgumentException("Fractions and colours must have equal number of elements")
    }

    fun blend(color1: Color, color2: Color, ratio: Double): Color? {
        val r = ratio.toFloat()
        val ir = 1.0f - r
        val rgb1 = color1.getColorComponents(FloatArray(3))
        val rgb2 = color2.getColorComponents(FloatArray(3))
        var red = rgb1[0] * r + rgb2[0] * ir
        var green = rgb1[1] * r + rgb2[1] * ir
        var blue = rgb1[2] * r + rgb2[2] * ir
        if (red < 0.0f) {
            red = 0.0f
        } else if (red > 255.0f) {
            red = 255.0f
        }
        if (green < 0.0f) {
            green = 0.0f
        } else if (green > 255.0f) {
            green = 255.0f
        }
        if (blue < 0.0f) {
            blue = 0.0f
        } else if (blue > 255.0f) {
            blue = 255.0f
        }
        var color3: Color? = null
        runCatching {
            color3 = Color(red, green, blue)
        }
        return color3
    }

    fun getFractionIndices(fractions: FloatArray, progress: Float): IntArray {
        val range = IntArray(2)
        var startPoint: Int
        startPoint = 0
        while (startPoint < fractions.size && fractions[startPoint] <= progress) {
            ++startPoint
        }
        if (startPoint >= fractions.size) {
            startPoint = fractions.size - 1
        }
        range[0] = startPoint - 1
        range[1] = startPoint
        return range
    }

    fun stripColor(input: String?): String {
        return COLOR_PATTERN!!.matcher(input!!).replaceAll("")
    }

    fun fade(color: Color, index: Int, count: Int): Color {
        val hsb = FloatArray(3)
        Color.RGBtoHSB(color.red, color.green, color.blue, hsb)
        var brightness =
            Math.abs((System.currentTimeMillis() % 2000L / 1000.0f + index / count.toFloat() * 2.0f) % 2.0f - 1.0f)
        brightness = 0.5f + 0.5f * brightness
        hsb[2] = brightness % 2.0f
        return Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]))
    }

    fun slowlyRainbow(time: Long, count: Int, qd: Float, sq: Float): Color {
        val color = Color(Color.HSBtoRGB((time + count * -3000000.0f) / 2.0f / 1.0E9f, qd, sq))
        return Color(color.red / 255.0f, color.green / 255.0f, color.blue / 255.0f, color.alpha / 255.0f)
    }

    fun setColor(color: Color) {
        val alpha = (color.rgb shr 24 and 0xFF) / 255.0f
        val red = (color.rgb shr 16 and 0xFF) / 255.0f
        val green = (color.rgb shr 8 and 0xFF) / 255.0f
        val blue = (color.rgb and 0xFF) / 255.0f
        GL11.glColor4f(red, green, blue, alpha)
    }

    fun getRainbowOpaque(seconds: Int, saturation: Float, brightness: Float, index: Int): Int {
        val hue = (System.currentTimeMillis() + index) % (seconds * 1000) / (seconds * 1000).toFloat()
        return Color.HSBtoRGB(hue, saturation, brightness)
    }

    fun interpolate(current: Double, old: Double, scale: Double): Double {
        return old + (current - old) * scale
    }

    fun isInViewFrustrum(entity: Entity): Boolean {
        return isInViewFrustrum(entity.entityBoundingBox) || entity.ignoreFrustumCheck
    }

    private fun isInViewFrustrum(bb: AxisAlignedBB): Boolean {
        val current = mc.getRenderViewEntity()
        frustrum!!.setPosition(current!!.posX, current.posY, current.posZ)
        return frustrum!!.isBoundingBoxInFrustum(bb)
    }

    fun quickDrawRect(x: Float, y: Float, x2: Float, y2: Float) {
        GL11.glBegin(7)
        GL11.glVertex2d(x2.toDouble(), y.toDouble())
        GL11.glVertex2d(x.toDouble(), y.toDouble())
        GL11.glVertex2d(x.toDouble(), y2.toDouble())
        GL11.glVertex2d(x2.toDouble(), y2.toDouble())
        GL11.glEnd()
    }

    fun drawRect(x: Double, y: Double, x2: Double, y2: Double, color: Int) {
        GL11.glEnable(3042)
        GL11.glDisable(3553)
        GL11.glBlendFunc(770, 771)
        GL11.glEnable(2848)
        glColor(color)
        GL11.glBegin(7)
        GL11.glVertex2d(x2, y)
        GL11.glVertex2d(x, y)
        GL11.glVertex2d(x, y2)
        GL11.glVertex2d(x2, y2)
        GL11.glEnd()
        GL11.glEnable(3553)
        GL11.glDisable(3042)
        GL11.glDisable(2848)
    }

    fun newDrawRect(left: Double, top: Double, right: Double, bottom: Double, color: Int) {
        var left = left
        var top = top
        var right = right
        var bottom = bottom
        if (left < right) {
            val i = left
            left = right
            right = i
        }
        if (top < bottom) {
            val j = top
            top = bottom
            bottom = j
        }
        val f3 = (color shr 24 and 0xFF) / 255.0f
        val f4 = (color shr 16 and 0xFF) / 255.0f
        val f5 = (color shr 8 and 0xFF) / 255.0f
        val f6 = (color and 0xFF) / 255.0f
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.buffer
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.color(f4, f5, f6, f3)
        worldrenderer.begin(7, DefaultVertexFormats.POSITION)
        worldrenderer.pos(left, bottom, 0.0).endVertex()
        worldrenderer.pos(right, bottom, 0.0).endVertex()
        worldrenderer.pos(right, top, 0.0).endVertex()
        worldrenderer.pos(left, top, 0.0).endVertex()
        tessellator.draw()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

    fun color(color: Color?) {
        var color = color
        if (color == null) {
            color = Color.white
        }
        color(
            (color!!.red / 255.0f).toDouble(),
            (color.green / 255.0f).toDouble(),
            (color.blue / 255.0f).toDouble(),
            (color.alpha / 255.0f).toDouble()
        )
    }

    fun color(red: Double, green: Double, blue: Double, alpha: Double) {
        GL11.glColor4d(red, green, blue, alpha)
    }

    fun glColor(hex: Int) {
        val alpha = (hex shr 24 and 0xFF) / 255.0f
        val red = (hex shr 16 and 0xFF) / 255.0f
        val green = (hex shr 8 and 0xFF) / 255.0f
        val blue = (hex and 0xFF) / 255.0f
        GlStateManager.color(red, green, blue, alpha)
    }

    fun render(mode: Int, render: Runnable) {
        GL11.glBegin(mode)
        render.run()
        GL11.glEnd()
    }
}