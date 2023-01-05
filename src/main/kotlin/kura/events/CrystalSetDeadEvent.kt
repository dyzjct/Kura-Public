package kura.events

import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraftforge.fml.common.eventhandler.Event

class CrystalSetDeadEvent(
    val x: Double,
    val y: Double,
    val z: Double,
    val crystals: List<EntityEnderCrystal>
) : Event()