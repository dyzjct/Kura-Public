package me.dyzjct.kura.manager

import me.dyzjct.kura.module.Module
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.AxisAlignedBB

@Suppress("NOTHING_TO_INLINE")
object EntityManager : Module() {
    private var entity0 = emptyList<Entity>()
    val entity: List<Entity>
        get() = entity0

    private var livingBase0 = emptyList<EntityLivingBase>()
    val livingBase: List<EntityLivingBase>
        get() = livingBase0

    private var players0 = emptyList<EntityPlayer>()
    val players: List<EntityPlayer>
        get() = players0

    init {
        entity0 = mc.world.loadedEntityList.toList()
        livingBase0 = mc.world.loadedEntityList.filterIsInstance<EntityLivingBase>()
        players0 = mc.world.playerEntities.toList()
    }

    inline fun checkEntityCollision(box: AxisAlignedBB, noinline predicate: (Entity) -> Boolean): Boolean {
        return entity.asSequence()
            .filter { it.isEntityAlive }
            .filter { it.preventEntitySpawning }
            .filter { it.entityBoundingBox.intersects(box) }
            .filter(predicate)
            .none()
    }

    inline fun checkEntityCollision(box: AxisAlignedBB, ignoreEntity: Entity): Boolean {
        return entity.asSequence()
            .filter { it.isEntityAlive }
            .filter { it.preventEntitySpawning }
            .filter { it != ignoreEntity }
            .filter { it.entityBoundingBox.intersects(box) }
            .none()
    }

    inline fun checkEntityCollision(box: AxisAlignedBB): Boolean {
        return entity.asSequence()
            .filter { it.isEntityAlive }
            .filter { it.preventEntitySpawning }
            .filter { it.entityBoundingBox.intersects(box) }
            .none()
    }

    inline fun checkAnyEntity(box: AxisAlignedBB, noinline predicate: (Entity) -> Boolean): Boolean {
        return entity.asSequence()
            .filter { it.isEntityAlive }
            .filter { it.entityBoundingBox.intersects(box) }
            .filter(predicate)
            .none()
    }

    inline fun checkAnyEntity(box: AxisAlignedBB, ignoreEntity: Entity): Boolean {
        return entity.asSequence()
            .filter { it.isEntityAlive }
            .filter { it != ignoreEntity }
            .filter { it.entityBoundingBox.intersects(box) }
            .none()
    }

    inline fun checkAnyEntity(box: AxisAlignedBB): Boolean {
        return entity.asSequence()
            .filter { it.isEntityAlive }
            .filter { it.entityBoundingBox.intersects(box) }
            .none()
    }
}