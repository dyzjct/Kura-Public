package kura.events

import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.fml.common.eventhandler.Event

sealed class EntityEvent(val entity: EntityLivingBase) : Event() {
    class UpdateHealth(entity: EntityLivingBase, val prevHealth: Float, val health: Float) : EntityEvent(entity)
    }