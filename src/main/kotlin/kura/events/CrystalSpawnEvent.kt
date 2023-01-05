package kura.events

import me.dyzjct.kura.module.modules.crystalaura.CrystalHelper.CrystalDamage
import net.minecraftforge.fml.common.eventhandler.Event

class CrystalSpawnEvent(
    val entityID: Int,
    val crystalDamage: CrystalDamage
) : Event()