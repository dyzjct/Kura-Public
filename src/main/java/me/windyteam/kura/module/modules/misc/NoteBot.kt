package me.windyteam.kura.module.modules.misc

import me.windyteam.kura.event.events.client.PacketEvents
import me.windyteam.kura.event.events.client.SettingChangeEvent
import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.event.events.render.RenderEvent
import me.windyteam.kura.gui.settingpanel.component.ActionEventListener
import me.windyteam.kura.gui.settingpanel.component.components.Button
import me.windyteam.kura.gui.settingpanel.component.components.ScrollPane
import me.windyteam.kura.gui.settingpanel.layout.GridLayout
import me.windyteam.kura.gui.window.WindowChooser
import me.windyteam.kura.manager.FileManager
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.utils.other.MultiThreading.runAsync
import me.windyteam.kura.utils.animations.BlockEasingRender
import me.windyteam.kura.utils.block.BlockInteractionHelper
import me.windyteam.kura.utils.block.BlockUtil
import me.windyteam.kura.utils.gl.MelonTessellator
import me.windyteam.kura.utils.mc.ChatUtil
import me.windyteam.kura.utils.render.RenderUtils
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.network.play.server.SPacketBlockAction
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Mouse
import java.awt.Color
import java.awt.Point
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.nio.channels.Channels
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.math.abs
import kotlin.math.floor


@Module.Info(name = "NoteBot", category = Category.MISC)
class NoteBot : Module() {
    private var blockRenderSmooth = BlockEasingRender(BlockPos(0, 0, 0), 650f, 150f)
    private var tune = bsetting("Tune", false)
    private var active = bsetting("Active", false)
    private var downloadSongs = bsetting("DownloadSongs", false)
    private var openSelectGui = bsetting("ChooseSong", false)
    private var soundBytes: MutableMap<Sound, Byte> = EnumMap(Sound::class.java)
    private var soundEntries: MutableList<SoundEntry?> = ArrayList()
    private var posList: MutableList<BlockPos> = ArrayList()
    private var file = File(FileManager.NOTEBOT_PATH)
    private var renderPos: MutableList<BlockPos?> = ArrayList()
    private var registers: Array<IRegister>? = null
    private var soundIndex = 0
    private var index = 0
    private var posPitch: MutableMap<BlockPos?, AtomicInteger>? = null
    private var soundPositions: Map<Sound, Array<BlockPos?>>? = null
    private var currentPos: BlockPos? = null
    private var nextPos: BlockPos? = null
    private var endPos: BlockPos? = null
    private var tuneStage = 0
    private var tuneIndex = 0
    private var tuned = false
    private var guiSongChoose: GuiScreen? = null
    override fun onEnable() {
        if (nullCheck()) {
            disable()
            return
        }
        if (guiSongChoose == null) {
            guiSongChoose = SongChooser()
        }
        soundEntries.clear()
        noteBlocks
        soundIndex = 0
        index = 0
        resetTuning()
    }

    @SubscribeEvent
    fun onSettingChange(event: SettingChangeEvent) {
        if (event.stage == 2 && event.setting != null && this == event.setting.contain && event.setting == tune && tune.value) {
            resetTuning()
        }
    }

    @SubscribeEvent
    fun onPacketReceive(event: PacketEvents.Receive) {
        if (tune.value && event.packet is SPacketBlockAction && tuneStage == 0 && soundPositions != null) {
            val packet = event.packet as SPacketBlockAction
            val sound = Sound.values()[packet.data1]
            val pitch = packet.data2
            val positions = soundPositions!![sound]!!
            for (i in 0..24) {
                val position = positions[i]
                if (packet.blockPosition != position) continue
                if (posPitch!![position]!!.toInt() != -1) break
                var pitchDif = i - pitch
                if (pitchDif < 0) {
                    pitchDif += 25
                }
                posPitch!![position]!!.set(pitchDif)
                if (pitchDif == 0) break
                tuned = false
                break
            }
            if (endPos == packet.blockPosition && tuneIndex >= posPitch!!.values.size) {
                tuneStage = 1
            }
        }
    }

    @SubscribeEvent
    fun onUpdateWalkingPlayerEvent(event: MotionUpdateEvent.Tick) {
        if (fullNullCheck()) {
            return
        }
        try {
            if (downloadSongs.value) {
                downloadSongs()
                ChatUtil.NoSpam.sendWarnMessage("Songs downloaded")
                downloadSongs.value = false
            }
            if (openSelectGui.value) {
                mc.displayGuiScreen(guiSongChoose)
                openSelectGui.value = false
            }
            if (tune.value) {
                tunePre(event)
            }
            if (active.value) {
                noteBotPre(event)
            }
            if (tune.value) {
                tunePost()
            }
            if (active.value) {
                noteBotPost()
            }
        } catch (ignored: Exception) {
        }
    }

    private fun tunePre(event: MotionUpdateEvent.Tick) {
        currentPos = null
        if (tuneStage == 1 && getAtomicBlockPos(null) == null) {
            if (tuned) {
                ChatUtil.NoSpam.sendWarnMessage("Done tuning.")
                tune.value = false
            } else {
                tuned = true
                tuneStage = 0
                tuneIndex = 0
            }
        } else {
            if (tuneStage != 0) {
                currentPos = getAtomicBlockPos(nextPos)
                nextPos = currentPos
            } else {
                while (tuneIndex < 250 && currentPos == null) {
                    currentPos = soundPositions!![Sound.values()[floor(
                        tuneIndex.toDouble() / 25.0
                    )
                        .toInt()]]!![tuneIndex % 25]
                    ++tuneIndex
                }
            }
            if (currentPos != null) {
                event.setRotation(
                    BlockInteractionHelper.getLegitRotations(Vec3d(currentPos!!))[0],
                    BlockInteractionHelper.getLegitRotations(
                        Vec3d(currentPos!!)
                    )[1]
                )
            }
        }
    }

    private fun tunePost() {
        if (tuneStage == 0 && currentPos != null) {
            hitNoteBlock(currentPos)
        } else if (currentPos != null) {
            posPitch!![currentPos]!!.decrementAndGet()
            adjustNoteBlock(currentPos)
        }
    }

    private fun noteBotPre(event: MotionUpdateEvent.Tick) {
        posList.clear()
        if (registers == null) {
            return
        }
        while (index < registers!!.size) {
            val register = registers!![index]
            if (register is SimpleRegister) {
                if (++soundIndex >= register.sound) {
                    ++index
                    soundIndex = 0
                }
                if (posList.size > 0) {
                    val blockPos = posList[0]
                    event.setRotation(
                        BlockInteractionHelper.getLegitRotations(Vec3d(blockPos))[0],
                        BlockInteractionHelper.getLegitRotations(
                            Vec3d(blockPos)
                        )[1]
                    )
                }
                return
            }
            if (register !is SoundRegister) continue
            val pos = getRegisterPos(register)
            if (pos != null) {
                posList.add(pos)
            }
            ++index
        }
        index = 0
    }

    private fun noteBotPost() {
        for (pos in ArrayList(posList)) {
            if (pos == null) continue
            hitNoteBlock(pos)
        }
    }

    override fun onWorldRender(event: RenderEvent) {
        val hsBtoRGB = Color.HSBtoRGB(
            floatArrayOf(
                System.currentTimeMillis() % 11520L / 11520.0f
            )[0], 0.5f, 1f
        )
        val r = hsBtoRGB shr 16 and 0xFF
        val g = hsBtoRGB shr 8 and 0xFF
        val b = hsBtoRGB and 0xFF
        if (renderPos.isNotEmpty()) {
            blockRenderSmooth.begin()
            MelonTessellator.drawBBBox(blockRenderSmooth.getFullUpdate(), Color(r, g, b), 150, 3f, true)
            renderPos.clear()
        }
    }

    private fun resetTuning() {
        if (mc.world == null || mc.player == null) {
            disable()
            return
        }
        tuned = true
        soundPositions = setUpSoundMap()
        posPitch = LinkedHashMap()
        soundPositions!!.values.forEach(Consumer { array: Array<BlockPos?> ->
            listOf(*array).forEach(
                Consumer { pos: BlockPos? ->
                    if (pos != null) {
                        endPos = pos
                        (posPitch as LinkedHashMap<BlockPos?, AtomicInteger>)[pos] = AtomicInteger(-1)
                    }
                })
        })
        tuneStage = 0
        tuneIndex = 0
    }

    private fun getAtomicBlockPos(blockPos: BlockPos?): BlockPos? {
        var atomicInteger: AtomicInteger
        var blockPos2: BlockPos? = null
        val iterator2: Iterator<Map.Entry<BlockPos?, AtomicInteger>> = posPitch!!.entries.iterator()
        do {
            if (!iterator2.hasNext()) {
                return null
            }
            val (key, value) = iterator2.next()
            if (key != null) {
                blockPos2 = key
            }
            atomicInteger = value
        } while (blockPos2 == null || blockPos2 == blockPos || atomicInteger.toInt() <= 0)
        return blockPos2
    }

    private val noteBlocks: Unit
        get() {
            fillSoundBytes()
            for (x in -6..5) {
                for (y in -1..4) {
                    for (z in -6..5) {
                        var sound: Sound? = null
                        var soundByte: Byte? = null
                        val pos = mc.player.position.add(x, y, z)
                        val block = mc.world.getBlockState(pos).block
                        if (pos.distanceSqToCenter(
                                mc.player.posX,
                                mc.player.posY + mc.player.getEyeHeight().toDouble(),
                                mc.player.posZ
                            ) >= 27.0 || block !== Blocks.NOTEBLOCK || soundBytes[getSoundFromBlockState(
                                mc.world.getBlockState(
                                    pos.down()
                                )
                            )
                                .also { sound = it }].also { soundByte = it!! }!! > 25
                        ) continue
                        soundEntries.add(SoundEntry(pos, SoundRegister(sound!!, soundByte!!)))
                        soundBytes.replace(sound!!, (soundByte!! + 1).toByte())
                    }
                }
            }
        }

    private fun fillSoundBytes() {
        soundBytes.clear()
        for (sound in Sound.values()) {
            soundBytes[sound] = 0.toByte()
        }
    }

    private fun hitNoteBlock(pos: BlockPos?) {
        renderPos.add(pos)
        blockRenderSmooth.updatePos(pos!!)
        val facing = BlockUtil.getFacing(pos)
        mc.player.connection.sendPacket(
            CPacketPlayerDigging(
                CPacketPlayerDigging.Action.START_DESTROY_BLOCK,
                pos,
                facing
            )
        )
        mc.player.connection.sendPacket(
            CPacketPlayerDigging(
                CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK,
                pos,
                facing
            )
        )
    }

    private fun adjustNoteBlock(pos: BlockPos?) {
        renderPos.add(pos)
        blockRenderSmooth.updatePos(pos!!)
        mc.player.connection.sendPacket(
            CPacketPlayerTryUseItemOnBlock(
                pos,
                BlockUtil.getFacing(pos),
                EnumHand.MAIN_HAND,
                0.0f,
                0.0f,
                0.0f
            )
        )
    }

    private fun downloadSongs() {
        runAsync {
            try {
                val songFile = File(file, "songs.zip")
                if (!songFile.exists()) {
                    songFile.parentFile.mkdirs()
                    try {
                        songFile.createNewFile()
                    } catch (exception: Exception) {
                        // empty catch block
                    }
                }
                val fileChannel = FileOutputStream(songFile).channel
                val readableByteChannel =
                    Channels.newChannel(URL("https://dl.dropboxusercontent.com/s/a7vlw0uw2x0hxdn/songs.zip").openStream())
                fileChannel.transferFrom(readableByteChannel, 0L, Long.MAX_VALUE)
                unzip(songFile, file)
                songFile.deleteOnExit()
            } catch (ioe: Exception) {
                ioe.printStackTrace()
            }
        }
    }

    fun calculateMouseLocation(): Point? {
        val minecraft = Minecraft.getMinecraft()
        var scale = minecraft.gameSettings.guiScale
        if (scale == 0) {
            scale = 1000
        }
        var scaleFactor: Int = 0
        while (scaleFactor < scale && minecraft.displayWidth / (scaleFactor + 1) >= 320 && minecraft.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor
        }
        return Point(Mouse.getX() / scaleFactor, minecraft.displayHeight / scaleFactor - Mouse.getY() / scaleFactor - 1)
    }

    private fun getRegisterPos(register: SoundRegister): BlockPos? {
        val soundEntry =
            soundEntries.stream().filter { entry: SoundEntry? -> entry!!.register == register }.findFirst().orElse(null)
                ?: return null
        return soundEntry.pos
    }

    enum class Sound {
        NONE, GOLD, GLASS, BONE, WOOD, CLAY, ICE, SAND, ROCK, WOOL
    }

    interface IRegister
    class SoundEntry(var pos: BlockPos, var register: SoundRegister)
    class SimpleRegister(var sound: Int) : IRegister
    class SoundRegister(var sound: Sound, var soundByte: Byte) : IRegister {

        override fun equals(other: Any?): Boolean {
            if (other is SoundRegister) {
                return other.sound == sound && other.soundByte == soundByte
            }
            return false
        }

        override fun hashCode(): Int {
            var result = sound.hashCode()
            result = 31 * result + soundByte
            return result
        }
    }

    inner class SongChooser : GuiScreen() {
        private var rby = this.height - 70
        private var window =
            WindowChooser("Choose Your Song", 150, 150, 290, 347)

        init {
            reload()
        }

        private fun reload() {
            val pane = ScrollPane(
                GridLayout(1)
            )
            var c = 0
            for (song in Objects.requireNonNull(file.listFiles())) {
                if (song.isDirectory || !song.name.endsWith(".notebot")) continue
                val but = Button(
                    song.name.replace(
                        ".notebot",
                        ""
                    ), 280, 15
                )
                but.onClickListener =
                    ActionEventListener {
                        try {
                            registers = createRegister(song)
                            ChatUtil.NoSpam.sendMessage(ChatUtil.GREEN + "Loaded: " + ChatUtil.BLUE + but.title)
                        } catch (e: Exception) {
                            ChatUtil.NoSpam.sendErrorMessage("An Error occurred with $file")
                        }
                    }
                pane.addComponent(but)
                ++c
            }
            if (c != 0) {
                window.setContentPane(pane)
            } else {
                ChatUtil.NoSpam.sendErrorMessage("Pls Download Song!")
            }
        }

        override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
            val point = calculateMouseLocation()
            window.mouseMoved(point!!.x, point.y)
            window.render()
            RenderUtils.drawRect(30.0, rby.toDouble(), 70.0, 25.0, Color(51, 51, 51))
            RenderUtils.drawRectOutline(30.0, rby.toDouble(), 70.0, 25.0, Color.BLACK)
            RenderUtils.getFontRender().drawCenteredString("Reload", 65.0f, rby.toFloat() - 12.5f, Color.white.rgb)
        }

        @Throws(IOException::class)
        override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
            window.mouseMoved(mouseX, mouseY)
            window.mousePressed(mouseButton, mouseX, mouseY)
            if (mouseX >= 30.coerceAtMost(100) && mouseX <= 30.coerceAtLeast(100) && mouseY >= rby.coerceAtMost(rby + 25) && mouseY <= rby.coerceAtLeast(
                    rby + 25
                )
            ) {
                reload()
            }
            super.mouseClicked(mouseX, mouseY, mouseButton)
        }

        override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
            window.mouseMoved(mouseX, mouseY)
            window.mouseReleased(state, mouseX, mouseY)
            super.mouseReleased(mouseX, mouseY, state)
        }

        override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
            window.mouseMoved(mouseX, mouseY)
            super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
        }

        @Throws(IOException::class)
        override fun handleMouseInput() {
            super.handleMouseInput()
            val eventDWheel = Mouse.getEventDWheel()
            window.mouseWheel(eventDWheel)
        }

        @Throws(IOException::class)
        override fun keyTyped(typedChar: Char, keyCode: Int) {
            window.keyPressed(keyCode, typedChar)
            super.keyTyped(typedChar, keyCode)
        }
    }

    companion object {
        var INSTANCE = NoteBot()
        fun setUpSoundMap(): Map<Sound, Array<BlockPos?>> {
            val result = LinkedHashMap<Sound, Array<BlockPos?>>()
            val atomicSounds = HashMap<Any, Any>()
            listOf(*Sound.values()).forEach(
                Consumer { sound: Sound ->
                    val var10002 = arrayOfNulls<BlockPos>(25)
                    result[sound] = var10002
                    atomicSounds[sound] = AtomicInteger()
                })
            for (x in -6..5) {
                for (y in -1..4) {
                    for (z in -6..5) {
                        var sound2: Sound? = null
                        var soundByte = 0
                        val pos = mc.player.position.add(x, y, z)
                        val block = mc.world.getBlockState(pos).block
                        if (distanceSqToCenter(pos) >= 27.040000000000003 || block !== Blocks.NOTEBLOCK || (atomicSounds[getSoundFromBlockState(
                                mc.world.getBlockState(pos.down())
                            ).also { sound2 = it }] as AtomicInteger?)!!.getAndIncrement().also { soundByte = it } >= 25
                        ) continue
                        result[sound2]!![soundByte] = pos
                    }
                }
            }
            return result
        }

        private fun distanceSqToCenter(pos: BlockPos): Double {
            val var1 = abs(mc.player.posX - pos.getX().toDouble() - 0.5)
            val var3 = abs(mc.player.posY + mc.player.getEyeHeight().toDouble() - pos.getY().toDouble() - 0.5)
            val var5 = abs(mc.player.posZ - pos.getZ().toDouble() - 0.5)
            return var1 * var1 + var3 * var3 + var5 * var5
        }

        private fun unzip(file1: File?, fileIn: File) {
            var zipEntry: ZipEntry
            val zipInputStream: ZipInputStream
            val var2 = ByteArray(1024)
            try {
                if (!fileIn.exists()) {
                    fileIn.mkdir()
                }
                zipInputStream = ZipInputStream(FileInputStream(file1!!))
                zipEntry = zipInputStream.nextEntry!!
            } catch (ioe: IOException) {
                ioe.printStackTrace()
                return
            }
            while (true) {
                var outputStream: FileOutputStream
                try {
                    var index: Int
                    val fileName = zipEntry.name
                    val newFile = File(fileIn, fileName)
                    File(newFile.parent).mkdirs()
                    outputStream = FileOutputStream(newFile)
                    while (zipInputStream.read(var2).also { index = it } > 0) {
                        outputStream.write(var2, 0, index)
                    }
                } catch (ioe: IOException) {
                    ioe.printStackTrace()
                    return
                }
                zipEntry = try {
                    outputStream.close()
                    zipInputStream.nextEntry!!
                } catch (ioe: IOException) {
                    ioe.printStackTrace()
                    return
                }
            }
        }

        fun getSoundFromBlockState(state: IBlockState): Sound {
            if (state.block === Blocks.CLAY) {
                return Sound.CLAY
            }
            if (state.block === Blocks.GOLD_BLOCK) {
                return Sound.GOLD
            }
            if (state.block === Blocks.WOOL) {
                return Sound.WOOL
            }
            if (state.block === Blocks.PACKED_ICE) {
                return Sound.ICE
            }
            if (state.block === Blocks.BONE_BLOCK) {
                return Sound.BONE
            }
            if (state.material === Material.ROCK) {
                return Sound.ROCK
            }
            if (state.material === Material.SAND) {
                return Sound.SAND
            }
            if (state.material === Material.GLASS) {
                return Sound.GLASS
            }
            return if (state.material === Material.WOOD) Sound.WOOD else Sound.NONE
        }

        @Throws(IOException::class)
        fun createRegister(file: File?): Array<IRegister> {
            var n2: Int
            val fileInputStream = FileInputStream(file!!)
            val filebyte = ByteArray(fileInputStream.available())
            fileInputStream.read(filebyte)
            val arrayList = ArrayList<IRegister>()
            var bl = true
            for (n in filebyte) {
                n2 = n.toInt()
                if (n2 != 64) continue
                bl = false
                break
            }
            var n = 0
            var n6 = 0
            while (n6 < filebyte.size) {
                val n4 = filebyte[n]
                if (n4 == (if (bl) 5.toByte() else 64)) {
                    val arrby3 = byteArrayOf(filebyte[++n], filebyte[++n])
                    n2 = arrby3[0].toInt() and 0xFF or (arrby3[1].toInt() and 0xFF shl 8)
                    arrayList.add(SimpleRegister(n2))
                } else {
                    arrayList.add(SoundRegister(Sound.values()[n4.toInt()], filebyte[++n]))
                }
                n6 = ++n
            }
            return arrayList.toTypedArray()
        }
    }
}