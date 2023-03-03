package me.windyteam.kura.module.modules.combat

import me.windyteam.kura.event.events.entity.MotionUpdateEvent
import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.setting.BooleanSetting
import me.windyteam.kura.setting.IntegerSetting
import me.windyteam.kura.utils.entity.EntityUtil
import me.windyteam.kura.utils.inventory.InventoryUtil
import me.windyteam.kura.utils.other.autoClean
import me.windyteam.kura.utils.other.loading
import me.windyteam.kura.utils.other.pistonCount
import me.windyteam.kura.utils.player.Timer
import me.windyteam.kura.utils.player.getTarget
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module.Info(name = "HoleKickerNew", category = Category.COMBAT)
object HoleKickerRewrite : Module() {
    private val range = settings("Range", 5, 1, 16)
    val delay: IntegerSetting? = settings("Delay", 100, 0, 500)
    val breakCrystal: BooleanSetting? = settings("BreakCrystal", false)
    val packetPlace: BooleanSetting? = settings("PacketPlace", false)
    val autoToggle: BooleanSetting? = settings("AutoToggle", true)
    val autoPush: BooleanSetting? = settings("Push", false)
    val rotate: BooleanSetting? = settings("Rotate", false)
    val xinBypass: BooleanSetting? = settings("XinBypass", false)
    var pistonList = mutableListOf<BlockPos>()
    var checkList = mutableListOf<BlockPos>()
    var breakList = mutableListOf<BlockPos>()
    var timer = Timer()

    var target: EntityPlayer? = null
    private var isSneaking = false
    override fun onEnable() {
        if (fullNullCheck()) return
        if (InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK) == -1 || InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON) == -1 && InventoryUtil.findHotbarBlock(
                Blocks.PISTON
            ) == -1
        ) {
            if (autoToggle!!.value) {
                disable()
            }
            return
        }
        EntityUtil.getRoundedBlockPos(mc.player)
        loading()
    }

    @SubscribeEvent
    fun onTick(event: MotionUpdateEvent) {
        if (fullNullCheck()) return
        if (!mc.player.onGround) return
        runCatching {
            if (InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK) == -1 || InventoryUtil.findHotbarBlock(Blocks.STICKY_PISTON) == -1 && InventoryUtil.findHotbarBlock(
                    Blocks.PISTON
                ) == -1
            ) {
                if (autoToggle!!.value) {
                    disable()
                }
                return
            }
            EntityUtil.getRoundedBlockPos(mc.player)
            target = getTarget(range.value)
            if (target == null) {
                if (autoToggle!!.value) {
                    disable()
                }
                return
            }

            target?.let {
                pistonCount(it)
            }
        }
    }

    override fun onDisable() {
        isSneaking = EntityUtil.stopSneaking(isSneaking)
        autoClean()
    }
}