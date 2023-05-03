package me.windyteam.kura.notifications

import net.minecraftforge.common.MinecraftForge
import java.util.concurrent.CopyOnWriteArrayList

class NotificationManager {

    val notifications = CopyOnWriteArrayList<Notification>()

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    fun addNotification(notification: Notification) = notifications.add(notification)

}