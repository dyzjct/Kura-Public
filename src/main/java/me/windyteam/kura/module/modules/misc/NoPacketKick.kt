package me.windyteam.kura.module.modules.misc

import me.windyteam.kura.module.Category
import me.windyteam.kura.module.Module

@Module.Info(name = "NoPacketKick", category = Category.MISC, description = "Prevent large packets from kicking you")
class NoPacketKick : Module() {
    init {
        INSTANCE = this
    }

    companion object {
        var INSTANCE: NoPacketKick? = null
    }
}