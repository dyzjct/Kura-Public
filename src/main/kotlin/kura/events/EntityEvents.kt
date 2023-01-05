package kura.events

import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.fml.common.eventhandler.Event

sealed class EntityEvents(val entity: EntityLivingBase) : Event() {
    class Death(entity: EntityLivingBase) : EntityEvent(entity)
}