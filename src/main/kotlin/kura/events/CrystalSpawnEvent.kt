package kura.events

import me.windyteam.kura.module.modules.crystalaura.cystalHelper.CrystalDamage
import net.minecraftforge.fml.common.eventhandler.Event

class CrystalSpawnEvent(
    val entityID: Int,
    val crystalDamage: CrystalDamage
) : Event()