package kura.events

import net.minecraftforge.fml.common.eventhandler.Event


sealed class TickEvent : Event() {
    object Pre : TickEvent()
    object Post : TickEvent()
}