package me.windyteam.kura.module.modules.combat

import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module
import me.windyteam.kura.module.ModuleManager
import me.windyteam.kura.setting.Setting
import me.windyteam.kura.utils.entity.PlayerUtil
import net.minecraft.block.BlockAir
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import java.util.*
import java.util.function.Supplier
import java.util.stream.Collectors
import kotlin.math.floor

@Module.Info(name = "AutoBurrow", category = Category.COMBAT)
object AutoBurrow : Module() {
    private var smartRange: Setting<Float> = fsetting("Smart Range", 2.5f, 1.0f, 10.0f)
    private var onlyInHole: Setting<Boolean> = bsetting("Hole Only", true)

    override fun onUpdate() {
        if (!onlyInHole.value || PlayerUtil.isInHole(mc.player)) {
            val entsSorted: java.util.ArrayList<*> = mc.world.loadedEntityList.stream()
                .filter { entity: Entity -> entity is EntityPlayer && entity !== mc.player }
                .sorted(Comparator.comparing { e: Entity? -> mc.player.getDistance(e) }).collect(
                    Collectors.toCollection(
                        Supplier { ArrayList() })
                )
            entsSorted.reverse()
            val burrow = ModuleManager.getModuleByName("Burrow") as Burrow
            val pos = BlockPos(
                floor(mc.player.positionVector.x), floor(mc.player.positionVector.y + 0.2), Math.floor(
                    mc.player.positionVector.z
                )
            )
            val var4: Iterator<*> = entsSorted.iterator()
            while (var4.hasNext()) {
                val ent = var4.next() as Entity
                if (ent !== mc.player && mc.player.getDistance(ent) < smartRange.value as Float && !PlayerUtil.isInHole(
                        ent
                    ) && !burrow.isEnabled && mc.world.getBlockState(pos).block is BlockAir
                ) {
                    burrow.enable()
                    if (onlyInHole.value as Boolean) {
                        return
                    }
                    disable()
                }
            }
        }
    }
}
